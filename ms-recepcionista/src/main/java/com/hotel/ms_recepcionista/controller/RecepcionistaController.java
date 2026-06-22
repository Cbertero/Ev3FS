package com.hotel.ms_recepcionista.controller;


import com.hotel.ms_recepcionista.dto.CheckInRequestDto;
import com.hotel.ms_recepcionista.servicio.OrquestadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recepcion")
@RequiredArgsConstructor
public class RecepcionistaController {

    private final OrquestadorService orquestadorService;

    @PostMapping("/check-in")
    public ResponseEntity<String> realizarCheckIn(@Valid @RequestBody CheckInRequestDto request) {

        String respuesta = orquestadorService.procesarCheckIn(request);
        return ResponseEntity.ok(respuesta);
    }
}