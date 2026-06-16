package com.hotel.gestion.usuarios.servicio;

import com.hotel.gestion.usuarios.dto.TokenResponseDto;
import com.hotel.gestion.usuarios.dto.UsuarioLoginDto;
import com.hotel.gestion.usuarios.entity.UsuarioEntity;
import com.hotel.gestion.usuarios.repository.Repositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class Servicio {


    private final Repositorio repositorio;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenResponseDto verificarCredenciales(UsuarioLoginDto dto) {

        UsuarioEntity usuario = repositorio.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Recurso No Encontrado: El usuario ingresado no existe."));


        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta. Intente nuevamente.");
        }


        String tokenSimulado = "JWT-" + UUID.randomUUID().toString().substring(0, 8);

        return TokenResponseDto.builder()
                .token(tokenSimulado)
                .rol(usuario.getRolId())
                .autenticado(true)
                .build();
    }
}
