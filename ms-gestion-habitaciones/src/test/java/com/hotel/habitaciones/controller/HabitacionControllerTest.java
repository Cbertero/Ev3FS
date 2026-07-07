package com.hotel.habitaciones.controller;

import com.hotel.habitaciones.dto.HabitacionDto;
import com.hotel.habitaciones.exception.EstadoInvalidoException;
import com.hotel.habitaciones.exception.HabitacionNotFoundException;
import com.hotel.habitaciones.service.HabitacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web de HabitacionController.
 * Se usa @WebMvcTest para levantar solo el contexto MVC (controller + GlobalExceptionHandler
 * heredado del módulo hotel-global-exception vía scanBasePackages), mockeando el servicio.
 */
@WebMvcTest(HabitacionController.class)
@DisplayName("HabitacionController - pruebas web (MockMvc)")
class HabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HabitacionService habitacionService;

    @Test
    @DisplayName("GET /api/habitaciones/disponibles retorna 200 con la lista de habitaciones")
    void listarDisponibles_retorna200ConLista() throws Exception {
        HabitacionDto dto = new HabitacionDto(1L, "DOBLE", 75000.0, "DISPONIBLE");
        when(habitacionService.listarDisponibles()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/habitaciones/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..idHabitacion", hasItem(1)))
                .andExpect(jsonPath("$..tipoHabitacion", hasItem("DOBLE")));
    }

    @Test
    @DisplayName("GET /api/habitaciones/disponibles retorna 200 con lista vacía cuando no hay disponibles")
    void listarDisponibles_sinDatos_retorna200ConListaVacia() throws Exception {
        when(habitacionService.listarDisponibles()).thenReturn(List.of());

        mockMvc.perform(get("/api/habitaciones/disponibles"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/habitaciones/{id} retorna 200 con los datos de la habitación")
    void obtenerPorId_existente_retorna200ConDatos() throws Exception {
        HabitacionDto dto = new HabitacionDto(1L, "SUITE", 120000.0, "DISPONIBLE");
        when(habitacionService.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/habitaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHabitacion").value(1))
                .andExpect(jsonPath("$.tipoHabitacion").value("SUITE"))
                .andExpect(jsonPath("$.precioBase").value(120000.0));
    }

    @Test
    @DisplayName("GET /api/habitaciones/{id} retorna 404 cuando la habitación no existe")
    void obtenerPorId_noExistente_retorna404() throws Exception {
        when(habitacionService.obtenerPorId(99L)).thenThrow(new HabitacionNotFoundException(99L));

        mockMvc.perform(get("/api/habitaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/habitaciones/estado/{id} retorna 200 con el mensaje de confirmación")
    void actualizarEstado_valido_retorna200ConMensaje() throws Exception {
        when(habitacionService.actualizarEstadoHabitacion(eq(1L), eq("OCUPADA")))
                .thenReturn("Estado de habitación 1 actualizado a: OCUPADA");

        mockMvc.perform(put("/api/habitaciones/estado/1").param("nuevoEstado", "OCUPADA"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado de habitación 1 actualizado a: OCUPADA"));
    }

    @Test
    @DisplayName("PUT /api/habitaciones/estado/{id} propaga error cuando el estado es inválido " +
            "(comportamiento actual: 500, ya que EstadoInvalidoException no es un NotFoundException; " +
            "no fue parte del fix aplicado. Si se quiere 400 aquí, se debería agregar un @ExceptionHandler " +
            "específico para EstadoInvalidoException)")
    void actualizarEstado_invalido_respondeConError() throws Exception {
        when(habitacionService.actualizarEstadoHabitacion(anyLong(), anyString()))
                .thenThrow(new EstadoInvalidoException("LIBRE"));

        mockMvc.perform(put("/api/habitaciones/estado/1").param("nuevoEstado", "LIBRE"))
                .andExpect(status().is5xxServerError());
    }
}
