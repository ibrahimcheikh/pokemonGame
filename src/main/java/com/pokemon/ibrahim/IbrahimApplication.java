package com.pokemon.ibrahim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class IbrahimApplication {
	@Bean
	public WebClient webClient() {
		/**
		 * Before I was using restTemplate but
		 * because its deprecated.............
		 * I had an error when I used webClient
		 * I exceeded the buffer..............
		 * so here im just adding more space to
		 * the webClient buffer to 1 MB.......
		 */
		ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
				.codecs(config -> config.defaultCodecs().maxInMemorySize(1024 * 1024))
				.build();
				return WebClient.builder().exchangeStrategies(exchangeStrategies).build();
	}


	public static void main(String[] args) {
		SpringApplication.run(IbrahimApplication.class, args);
	}
}
