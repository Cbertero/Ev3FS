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
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/personal-docs/**",
                                "/huespedes-docs/**",
                                "/habitaciones-docs/**",
                                "/reservas-docs/**",
                                "/auditoria-docs/**",
                                "/notificaciones-docs/**",
                                "/recepcionista-docs/**"
                        ).permitAll()
                        // El resto de las rutas reales (/api/**) siguen protegidas
                        // por el JwtAuthenticationFilter ya configurado por ruta
                        // en application.yml; aquí no se exige autenticación
                        // adicional a nivel de WebFlux Security para no duplicar
                        // la validación.
                        .anyExchange().permitAll()
                );
        return http.build();
    }
}