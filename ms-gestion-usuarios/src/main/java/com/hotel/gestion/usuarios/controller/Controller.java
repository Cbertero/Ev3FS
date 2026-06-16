package com.hotel.gestion.usuarios.controller;

import com.hotel.gestion.usuarios.dto.TokenResponseDto;
import com.hotel.gestion.usuarios.dto.UsuarioLoginDto;
import com.hotel.gestion.usuarios.servicio.Servicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class Controller {


    private final Servicio servicio;


    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody UsuarioLoginDto dto) {
        TokenResponseDto respuesta = servicio.verificarCredenciales(dto);
        return ResponseEntity.ok(respuesta);
    }
}

