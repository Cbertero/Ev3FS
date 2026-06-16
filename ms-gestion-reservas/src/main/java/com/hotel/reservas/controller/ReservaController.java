package com.hotel.reservas.controller;

import com.hotel.reservas.dto.ReservaDto;
import com.hotel.reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Gestión de Reservas", description = "Módulo transaccional: crea, consulta y cancela reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(summary = "Crear nueva reserva",
               description = "Calcula el monto total (precioBase x cantidadDias) y registra la reserva. " +
                             "NOTA: cantidadDias es obligatorio; si llega null se retorna 400 de inmediato.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (cantidadDias null, RUT vacío, etc.)")
    })
    @PostMapping("/crear")
    public ResponseEntity<EntityModel<Map<String, String>>> crearReserva(@Valid @RequestBody ReservaDto dto) {
       String respuestaServicio = reservaService.crearReserva(dto);
       Map<String, String> cuerpoRespuesta = new HashMap<>();
       cuerpoRespuesta.put("mensaje", respuestaServicio);
       EntityModel<Map<String, String>> model = EntityModel.of(cuerpoRespuesta,
                linkTo(methodOn(ReservaController.class).crearReserva(dto)).withSelfRel(),
                linkTo(methodOn(ReservaController.class).obtenerHistorial(dto.getRutHuesped())).withRel("historial-cliente")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @Operation(summary = "Obtener historial de reservas por cliente",
               description = "Retorna todas las reservas (activas y canceladas) de un huésped por su RUT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial retornado"),
        @ApiResponse(responseCode = "200", description = "Lista vacía si el cliente no tiene reservas")
    })
    @GetMapping("/historial/{rut}")
    public ResponseEntity<CollectionModel<EntityModel<ReservaDto>>> obtenerHistorial(
            @Parameter(description = "RUT del huésped", required = true)
            @PathVariable String rut) {

        List<EntityModel<ReservaDto>> reservas = reservaService.obtenerHistorialPorCliente(rut)
                .stream()
                .map(dto -> EntityModel.of(dto,
                        linkTo(methodOn(ReservaController.class)
                                .cancelarReserva(dto.getIdReserva())).withRel("cancelar")
                ))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ReservaDto>> collection = CollectionModel.of(
                reservas,
                linkTo(methodOn(ReservaController.class).obtenerHistorial(rut)).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Cancelar una reserva",
            description = "Cambia el estado de la reserva a CANCELADA. No se puede cancelar una ya cancelada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "409", description = "La reserva ya estaba cancelada")
    })
    @PostMapping("/cancelar/{id}")
    public ResponseEntity<String> cancelarReserva(
            @Parameter(description = "ID de la reserva a cancelar", required = true)
            @PathVariable Long id) {

        String respuesta = reservaService.cancelarReserva(id);
        return ResponseEntity.ok(respuesta);
    }
}
