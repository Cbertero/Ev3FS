package com.hotel.ms_notificaciones.controller;
import com.hotel.ms_notificaciones.dto.NotificacionRequest;
import com.hotel.ms_notificaciones.service.Servicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API para gestionar el envío de notificaciones")
public class Controller {

        private final Servicio servicio;

        @Operation(summary = "Enviar notificación", description = "Recibe los datos y los procesa")
        @PostMapping("/enviar")
        public ResponseEntity<String> recibirNotificacion(@Valid @RequestBody NotificacionRequest request) {

            servicio.procesarNotificacion(request);

            return ResponseEntity.ok("Notificación recibida y en proceso");
        }
    }

