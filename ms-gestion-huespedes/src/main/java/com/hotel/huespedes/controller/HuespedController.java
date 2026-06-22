package com.hotel.huespedes.controller;

import com.hotel.huespedes.dto.HuespedDto;
import com.hotel.huespedes.service.HuespedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/huespedes")
@RequiredArgsConstructor
@Tag(name = "Gestión de Huéspedes", description = "Endpoints para el counter de recepción")
public class HuespedController {

    private final HuespedService huespedService;

    @Operation(summary = "Buscar huésped por RUT",
               description = "Retorna los datos completos de un huésped registrado en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Huésped encontrado"),
        @ApiResponse(responseCode = "404", description = "Huésped no encontrado")
    })
    @GetMapping("/buscar/{rut}")
    public ResponseEntity<EntityModel<HuespedDto>> buscarPorRut(
            @Parameter(description = "RUT del huésped (ej: 12345678-9)", required = true)
            @PathVariable String rut) {

        HuespedDto dto = huespedService.buscarPorRut(rut);

        EntityModel<HuespedDto> model = EntityModel.of(dto,
                linkTo(methodOn(HuespedController.class).buscarPorRut(rut)).withSelfRel(),
                linkTo(methodOn(HuespedController.class).guardarHuesped(dto)).withRel("actualizar")
        );

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Registrar o actualizar un huésped",
               description = "Si el RUT ya existe lo actualiza, si no, lo registra como nuevo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Huésped guardado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/guardar")

    public ResponseEntity<EntityModel<Map<String, String>>> guardarHuesped(
            @Valid @RequestBody HuespedDto dto) {

        String mensajeServicio = huespedService.registrarOActualizarHuesped(dto);

        Map<String, String> cuerpoRespuesta = new HashMap<>();
        cuerpoRespuesta.put("mensaje", mensajeServicio);


        EntityModel<Map<String, String>> model = EntityModel.of(cuerpoRespuesta,
                linkTo(methodOn(HuespedController.class).buscarPorRut(dto.getRut())).withRel("ver-huesped"),
                linkTo(methodOn(HuespedController.class).guardarHuesped(dto)).withSelfRel()
        );

        return ResponseEntity.ok(model);
    }
}
