package com.hotel.reservas;

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
                        .title("MS Gestión de Reservas API")
                        .version("1.0.0")
                        .description("Módulo transaccional del sistema hotelero. " +
                                     "Crea reservas calculando el monto total según días de estadía, " +
                                     "consulta historial por cliente y gestiona cancelaciones.")
                        .contact(new Contact()
                                .name("Hotel Management System")
                                .email("dev@hotel.com")));
    }
}
