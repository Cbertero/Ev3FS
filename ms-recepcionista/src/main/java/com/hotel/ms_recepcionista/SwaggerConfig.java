package com.hotel.ms_recepcionista;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Microservicio: Recepcionista")
                        .version("1.0.0")
                        .description("Este microservicio actúa como el rol de Recepcionista del sistema hotelero. " +
                                "Es el encargado principal de gestionar el registro de entrada, salida y " +
                                "la coordinación inicial de los procesos de hospitalidad del hotel.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo Hotelero")));
    }
}