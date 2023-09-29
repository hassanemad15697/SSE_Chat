package com.test.pushnotification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("SSE Demo")
                        .description("REST API for SSE Demo")
                        .version("0.0.1-SNAPSHOT"));
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
