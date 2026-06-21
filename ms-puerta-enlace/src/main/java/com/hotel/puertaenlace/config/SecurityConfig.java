package com.hotel.puertaenlace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad reactiva para el Gateway.
 *
 * Spring Cloud Gateway usa WebFlux, no Servlet, por lo que la configuración
 * de seguridad debe declararse con ServerHttpSecurity (reactiva) y no con
 * HttpSecurity (la que se usa en los microservicios MVC tradicionales).
 *
 * Aquí se deshabilita la configuración de login por defecto de Spring Security
 * (que exige usuario/contraseña autogenerados para todas las rutas) y se deja
 * pasar todo el tráfico, ya que la validación real del token JWT la realiza
 * JwtAuthenticationFilter como GlobalFilter del propio Gateway.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .build();
    }
}
