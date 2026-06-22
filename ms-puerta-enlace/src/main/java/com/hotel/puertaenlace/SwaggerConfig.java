package com.hotel.puertaenlace;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway - Hotel CL CB")
                        .version("1.0.0")
                        .description("Puerta de enlace principal para todos los microservicios del hotel."));
    }
}
