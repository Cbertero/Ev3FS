package com.hotel.gestion.personal.controller;

import com.hotel.gestion.personal.dto.PersonalDto;
import com.hotel.gestion.personal.dto.UsuarioLoginDto;
import com.hotel.gestion.personal.dto.TokenResponseDto;
import com.hotel.gestion.personal.servicio.Servicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
public class Controller {

    private final Servicio servicio;

    @PostMapping("/registrar")
    public ResponseEntity<TokenResponseDto> registrar(@Valid @RequestBody PersonalDto dto) {
        return ResponseEntity.ok(servicio.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody UsuarioLoginDto loginDto) {
        return ResponseEntity.ok(servicio.loguear(loginDto));
    }


    @GetMapping("/listar")
    public ResponseEntity<List<PersonalDto>> listar() {
        String rolUsuario = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .iterator().next()
                .getAuthority()
                .replace("ROLE_", "");

        return ResponseEntity.ok(servicio.listar(rolUsuario));
    }


    @DeleteMapping("/eliminar/{rut}")
    public ResponseEntity<String> eliminar(@PathVariable String rut) {
        return ResponseEntity.ok(servicio.eliminar(rut));
    }
}