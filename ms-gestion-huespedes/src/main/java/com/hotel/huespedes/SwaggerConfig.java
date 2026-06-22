package com.hotel.huespedes;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Gestión de Huéspedes API")
                        .version("1.0.0")
                        .description("Microservicio para la gestión de clientes (huéspedes) del hotel. " +
                                     "Permite registrar, actualizar y consultar huéspedes por RUT.")
                        .contact(new Contact()
                                .name("Hotel Management System")
                                .email("dev@hotel.com")));
    }
}
