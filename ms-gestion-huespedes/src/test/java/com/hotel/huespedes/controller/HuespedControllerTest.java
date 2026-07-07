package com.hotel.huespedes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.huespedes.dto.HuespedDto;
import com.hotel.huespedes.exception.HuespedNotFoundException;
import com.hotel.huespedes.service.HuespedService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web de HuespedController.
 */
@WebMvcTest(HuespedController.class)
@DisplayName("HuespedController - pruebas web (MockMvc)")
class HuespedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HuespedService huespedService;

    private HuespedDto huespedValido() {
        return new HuespedDto("12345678-9", "Juan Pérez González",
                "juan.perez@email.com", "+56912345678", "Santiago, Chile");
    }

    @Test
    @DisplayName("GET /api/huespedes/buscar/{rut} retorna 200 con los datos del huésped")
    void buscarPorRut_existente_retorna200ConDatos() throws Exception {
        when(huespedService.buscarPorRut("12345678-9")).thenReturn(huespedValido());

        mockMvc.perform(get("/api/huespedes/buscar/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Pérez González"));
    }

    @Test
    @DisplayName("GET /api/huespedes/buscar/{rut} retorna 404 cuando el huésped no existe")
    void buscarPorRut_inexistente_retorna404() throws Exception {
        when(huespedService.buscarPorRut("00000000-0"))
                .thenThrow(new HuespedNotFoundException("00000000-0"));

        mockMvc.perform(get("/api/huespedes/buscar/00000000-0"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/huespedes/guardar con datos válidos retorna 200 con el mensaje del servicio")
    void guardarHuesped_datosValidos_retorna200ConMensaje() throws Exception {
        when(huespedService.registrarOActualizarHuesped(any(HuespedDto.class)))
                .thenReturn("Huésped con RUT 12345678-9 registrado exitosamente.");

        mockMvc.perform(post("/api/huespedes/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(huespedValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Huésped con RUT 12345678-9 registrado exitosamente."));
    }

    @Test
    @DisplayName("POST /api/huespedes/guardar con datos inválidos retorna 400 por validación " +
            "(el handler de MethodArgumentNotValidException no depende del bug de coincidencia de texto)")
    void guardarHuesped_datosInvalidos_retorna400() throws Exception {
        HuespedDto invalido = new HuespedDto("", "", "no-es-un-email", "", "");

        mockMvc.perform(post("/api/huespedes/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
