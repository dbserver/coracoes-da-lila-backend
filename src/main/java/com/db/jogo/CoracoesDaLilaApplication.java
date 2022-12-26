package com.db.jogo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CoracoesDaLilaApplication {

	@Value("${frontend.url}")
    public String frontendServer;
	
	public static void main(String[] args) {
		SpringApplication.run(CoracoesDaLilaApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedOrigins(frontendServer)
				.allowedMethods("*")
				.allowedHeaders("*");
			}
		};
	}
}
