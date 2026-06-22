package com.hotel.ms_recepcionista.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**"
                                ).permitAll()
                                .anyRequest().permitAll()
                        // Nota: este servicio confía en el JWT validado por el Gateway
                        // (ms-puerta-enlace). Si en el futuro se requiere validar el
                        // JWT también aquí, cambiar la última línea a .authenticated()
                        // y agregar el filtro JWT correspondiente.
                );
        return http.build();
    }
}