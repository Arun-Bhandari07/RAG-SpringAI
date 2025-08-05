package com.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import smile.clustering.CentroidClustering;
import smile.clustering.KMeans;
import smile.math.MathEx;

@Service
public class SummarizationService {

	private static final Logger logger = LoggerFactory.getLogger(SummarizationService.class);

	private final EmbeddingModel embeddingModel;
	private final ChatClient chatClient;
	private final JdbcTemplate jdbcTemplate;

	public record ClusterChunk(int clusterId, Document doc) {
	}

	public SummarizationService(EmbeddingModel embeddingModel, ChatClient chatClient, JdbcTemplate jdbcTemplate) {
		this.embeddingModel = embeddingModel;
		this.chatClient = chatClient;
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * This method utilizes K-MeansClustering algorithm for summarizing document
	 * Initially, we get vector points after embedding and perform clustering on the vector points
	 * Divided into n (3 here) clusters, get the chunk which is nearest to the centroid of cluster
	 * From each cluster, extract the chunk and summarize each chunk
	 * Finally, combine each summary to get final summary
	 * @param chunkedDocuments
	 */
	public void summarize(List<Document> chunkedDocuments) {
		
		
		if (chunkedDocuments.isEmpty()) {
			logger.warn("No chunks available to summarize");
			return;
		}

		// Embed Chunks
		List<float[]> embeddings = embedDocuments(chunkedDocuments);

		// Perform Clustering
		CentroidClustering<double[], double[]> clustering = clusterEmbeddings(embeddings);

		// Find Representative Chunks per Cluster
		List<ClusterChunk> representativeChunks = findRepresentativeChunksWithClusterId(clustering, embeddings,
				chunkedDocuments);

		// Summarize Representative Chunks (Parallel)
		List<String> summaries = summarizeChunksInParallelWithClusterId(representativeChunks);

		// Combine into Final Summary
		String finalSummary = String.join("\n\n", summaries);
		
		//Remove blank lines
		String cleanedFinalSummary = finalSummary.replaceAll("(?m)^[ \t]*\r?\n", "").trim();
		logger.info("\n= FINAL DOCUMENT SUMMARY ==\n{}\n=============", cleanedFinalSummary);


		// Store final summary in database;
		saveDocumentSummary(finalSummary,(String)chunkedDocuments.get(0).getMetadata().get("source"));
	}

	private List<float[]> embedDocuments(List<Document> documents) {
		BatchingStrategy strategy = new TokenCountBatchingStrategy();
		EmbeddingOptions embeddingOptions = new OllamaOptions();
		return embeddingModel.embed(documents, embeddingOptions, strategy);
	}

	private CentroidClustering<double[], double[]> clusterEmbeddings(List<float[]> vectorValues) {
		double[][] vectors = vectorValues.stream().map(vec -> {
			double[] doubleArray = new double[vec.length];
			for (int i = 0; i < vec.length; i++) {
				doubleArray[i] = vec[i];
			}
			return doubleArray;
		}).toArray(double[][]::new);

		int k = Math.min(3, vectors.length);
		CentroidClustering<double[], double[]> clustering = KMeans.fit(vectors,
				new smile.clustering.Clustering.Options(k, 100));

		logger.info("Cluster Labels: {}",
				Arrays.toString(IntStream.range(0, vectors.length).map(clustering::group).toArray()));
		logger.debug("Centroids: {}",
				Arrays.deepToString(IntStream.range(0, clustering.k()).mapToObj(clustering::center).toArray()));

		return clustering;
	}

	private List<ClusterChunk> findRepresentativeChunksWithClusterId(CentroidClustering<double[], double[]> clustering,
			List<float[]> vectorValues, List<Document> chunkedDocuments) {
		double[][] vectors = vectorValues.stream().map(vec -> {
			double[] doubleArray = new double[vec.length];
			for (int i = 0; i < vec.length; i++) {
				doubleArray[i] = vec[i];
			}
			return doubleArray;
		}).toArray(double[][]::new);

		List<ClusterChunk> representatives = new ArrayList<>();

		for (int clusterId = 0; clusterId < clustering.k(); clusterId++) {
			double[] centroid = clustering.center(clusterId);
			double closestDist = Double.MAX_VALUE;
			int bestIndex = -1;

			for (int i = 0; i < vectors.length; i++) {
				if (clustering.group(i) == clusterId) {
					double dist = MathEx.distance(vectors[i], centroid);
					if (dist < closestDist) {
						closestDist = dist;
						bestIndex = i;
					}
				}
			}

			if (bestIndex != -1) {

				representatives.add(new ClusterChunk(clusterId, chunkedDocuments.get(bestIndex)));
			}
		}

		return representatives;
	}

	private List<String> summarizeChunksInParallelWithClusterId(List<ClusterChunk> clusterChunks) {
        List<CompletableFuture<String>> futures = clusterChunks.stream()
                .map(cc -> CompletableFuture.supplyAsync(() -> {
                    String cleanedInput = cleanContent(cc.doc().toString());

                    if (cleanedInput.trim().length() < 100) {
                        logger.info("Skipping trivial chunk for cluster {}: {}", cc.clusterId(), cleanedInput);
                        return "";
                    }

                
                    String summary = summarize(cleanedInput);
                    logger.info("Cluster {} summary:\n{}", cc.clusterId(), summary);
                    return summary;
                }))
                .toList();

        
        return futures.stream()
                .map(CompletableFuture::join)
                .filter(s -> !s.isEmpty())
                .toList();
    }


	public String summarize(String input) {
		String prompt = """
				You are a document summarization assistant.
				Given the following EXTRACTED TEXT from a document, summarize the key points.
				Ignore formatting like bullet points, markdown syntax (** * # etc.), and headings.
				Summarize ONLY what's given without expecting more content.

				EXTRACTED TEXT:
				---
				%s
				---

				Provide a clean summary of the content above.
				""".formatted(input);

		return chatClient.prompt(prompt).call().content();
	}

	public String cleanContent(String content) {
		return content.replaceAll("\\*+", "") 
				.replaceAll("[-]+", "") 
				.replaceAll("#+", "") 
				.replaceAll("`+", "") 
				.replaceAll("\\d+\\s*", "") 
				.replaceAll("(\\n\\s*){2,}", "\n") 
				.replaceAll("---", "") 
				.trim();
	}
	
	public void saveDocumentSummary(String summary, String filename) {
	    String sql = "INSERT INTO document_summary (id, summary, filename) VALUES (?, ?, ?)";
	    UUID id = UUID.randomUUID();

	    jdbcTemplate.update(sql, id, summary, filename);
	    logger.info("✅ Summary inserted into DB with ID: {}", id);
	}


}
