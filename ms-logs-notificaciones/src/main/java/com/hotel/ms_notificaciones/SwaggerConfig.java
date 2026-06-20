package com.hotel.ms_notificaciones;

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
                        .title("API de Notificaciones")
                        .version("1.0.0")
                        .description("Microservicio encargado del envío de correos electronicos, gestion de estados y reintentos automaticos."));
    }
}