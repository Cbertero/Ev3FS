package com.hotel.gestion.personal.controller;

import com.hotel.gestion.personal.dto.PersonalDto;
import com.hotel.gestion.personal.servicio.Servicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
public class Controller {

        private final Servicio servicio;

        @GetMapping("/listar")
        public ResponseEntity<List<PersonalDto>> listar() {
            return ResponseEntity.ok(servicio.obtenerTodoElPersonal());
        }

        @PostMapping("/registrar")
        public ResponseEntity<String> registrar(@Valid @RequestBody PersonalDto dto) {
            String respuesta = servicio.guardarTrabajador(dto);
            return ResponseEntity.ok(respuesta);
        }

        @DeleteMapping("/eliminar/{rut}")
        public ResponseEntity<String> eliminar(@PathVariable String rut) {
            String respuesta = servicio.darDeBajaTrabajador(rut);
            return ResponseEntity.ok(respuesta);
        }
    }

