package com.grandcinema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Autorise le front, hébergé sur une autre origine, à appeler l'API depuis le navigateur
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Value("${cors.origins}")
	private String[] origins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**").allowedOrigins(origins);
	}

}
