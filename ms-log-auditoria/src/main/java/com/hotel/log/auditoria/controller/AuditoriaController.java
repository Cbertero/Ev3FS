package com.hotel.log.auditoria.controller;

import com.hotel.log.auditoria.dto.AuditoriaDto;
import com.hotel.log.auditoria.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {
    private final AuditoriaService service;

    @PostMapping("/registrar-evento")
    public ResponseEntity<String> registrarEvento(@RequestBody AuditoriaDto dto) {
        service.registrarEventoCritico(dto);
        return ResponseEntity.ok("Evento registrado en bitácora");
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AuditoriaDto>> listarAuditoria() {
        return ResponseEntity.ok(service.obtenerBitacoraCompleta());
    }
}