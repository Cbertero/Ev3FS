package com.hotel.reservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.reservas.dto.ReservaDto;
import com.hotel.reservas.exception.ReservaCanceladaException;
import com.hotel.reservas.exception.ReservaNotFoundException;
import com.hotel.reservas.service.ReservaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web de ReservaController.
 */
@WebMvcTest(ReservaController.class)
@DisplayName("ReservaController - pruebas web (MockMvc)")
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservaService reservaService;

    @Test
    @DisplayName("POST /api/reservas/crear con datos válidos retorna 201 con el mensaje del servicio")
    void crearReserva_datosValidos_retorna201() throws Exception {
        ReservaDto dto = new ReservaDto(null, "11111111-1", 5L, 3, null);
        when(reservaService.crearReserva(any(ReservaDto.class)))
                .thenReturn("Reserva #1 creada exitosamente. Monto total: $150000 CLP");

        mockMvc.perform(post("/api/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Reserva #1 creada exitosamente. Monto total: $150000 CLP"));
    }

    @Test
    @DisplayName("POST /api/reservas/crear sin cantidadDias retorna 400 por validación de Bean Validation")
    void crearReserva_sinCantidadDias_retorna400() throws Exception {
        ReservaDto dto = new ReservaDto(null, "11111111-1", 5L, null, null);

        mockMvc.perform(post("/api/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/reservas/historial/{rut} retorna 200 con el listado de reservas")
    void obtenerHistorial_conDatos_retorna200ConLista() throws Exception {
        ReservaDto dto = new ReservaDto(1L, "11111111-1", 5L, 3, 150000.0);
        when(reservaService.obtenerHistorialPorCliente("11111111-1")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/reservas/historial/11111111-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..idReserva", hasItem(1)));
    }

    @Test
    @DisplayName("GET /api/reservas/historial/{rut} retorna 200 con lista vacía cuando no hay reservas")
    void obtenerHistorial_sinDatos_retorna200ConListaVacia() throws Exception {
        when(reservaService.obtenerHistorialPorCliente("22222222-2")).thenReturn(List.of());

        mockMvc.perform(get("/api/reservas/historial/22222222-2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/reservas/cancelar/{id} retorna 200 con el mensaje de confirmación")
    void cancelarReserva_exitosa_retorna200() throws Exception {
        when(reservaService.cancelarReserva(1L)).thenReturn("Reserva #1 cancelada exitosamente.");

        mockMvc.perform(post("/api/reservas/cancelar/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/reservas/cancelar/{id} retorna 409 cuando la reserva ya estaba cancelada " +
            "(ReservaCanceladaException extiende IllegalArgumentException, manejada correctamente)")
    void cancelarReserva_yaCancelada_retorna409() throws Exception {
        when(reservaService.cancelarReserva(anyLong())).thenThrow(new ReservaCanceladaException(2L));

        mockMvc.perform(post("/api/reservas/cancelar/2"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/reservas/cancelar/{id} retorna 404 cuando la reserva no existe")
    void cancelarReserva_inexistente_retorna404() throws Exception {
        when(reservaService.cancelarReserva(anyLong())).thenThrow(new ReservaNotFoundException(99L));

        mockMvc.perform(post("/api/reservas/cancelar/99"))
                .andExpect(status().isNotFound());
    }
}
