# 🧠 Spring AI PDF RAG App

A backend application built with **Spring Boot** and **Spring AI** that enables users to upload PDF documents and interact with them using **Retrieval-Augmented Generation (RAG)**. It leverages **Ollama (Gemma 3B)** to generate context-aware answers from embedded PDF content.

## 🚀 Features

- Upload and parse PDF documents
- Extract text and chunk content intelligently
- Store and retrieve embeddings using `pgvector` (PostgreSQL)
- Semantic search to retrieve relevant chunks
- Contextual response generation with Ollama via Spring AI
