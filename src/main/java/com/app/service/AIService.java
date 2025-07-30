package com.app.service;

import reactor.core.publisher.Flux;

public interface AIService {

	public Flux<String> ask();
}
