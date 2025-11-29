package com.floginfe_be.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI()
				.info(new Info()
						.title("Flogin API")
						.description("API documentation for Flogin System")
						.version("1.0.0"));
	}
}
