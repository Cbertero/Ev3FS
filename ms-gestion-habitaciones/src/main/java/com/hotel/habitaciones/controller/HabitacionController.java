package com.hotel.habitaciones.controller;

import com.hotel.habitaciones.dto.HabitacionDto;
import com.hotel.habitaciones.service.HabitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
@Tag(name = "Gestión de Habitaciones", description = "Endpoints de infraestructura y control de inventario")
public class HabitacionController {

    private final HabitacionService habitacionService;

    @Operation(summary = "Listar habitaciones disponibles",
               description = "Retorna todas las habitaciones con estado DISPONIBLE")
    @ApiResponse(responseCode = "200", description = "Lista de habitaciones disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<CollectionModel<EntityModel<HabitacionDto>>> listarDisponibles() {
        List<EntityModel<HabitacionDto>> habitaciones = habitacionService.listarDisponibles()
                .stream()
                .map(dto -> EntityModel.of(dto,
                        linkTo(methodOn(HabitacionController.class).listarDisponibles()).withSelfRel(),
                        linkTo(methodOn(HabitacionController.class)
                                .actualizarEstado(dto.getIdHabitacion(), "OCUPADA")).withRel("ocupar")
                ))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<HabitacionDto>> collection = CollectionModel.of(
                habitaciones,
                linkTo(methodOn(HabitacionController.class).listarDisponibles()).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Actualizar estado de habitación",
               description = "Cambia el estado de una habitación. Estados válidos: DISPONIBLE, OCUPADA, MANTENCION")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Estado inválido"),
        @ApiResponse(responseCode = "404", description = "Habitación no encontrada")
    })
    @PutMapping("/estado/{id}")
    public ResponseEntity<EntityModel<String>> actualizarEstado(
            @Parameter(description = "ID de la habitación", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado: DISPONIBLE, OCUPADA, MANTENCION", required = true)
            @RequestParam String nuevoEstado) {

        String respuesta = habitacionService.actualizarEstadoHabitacion(id, nuevoEstado);

        EntityModel<String> model = EntityModel.of(respuesta,
                linkTo(methodOn(HabitacionController.class).actualizarEstado(id, nuevoEstado)).withSelfRel(),
                linkTo(methodOn(HabitacionController.class).listarDisponibles()).withRel("disponibles")
        );

        return ResponseEntity.ok(model);
    }
}
