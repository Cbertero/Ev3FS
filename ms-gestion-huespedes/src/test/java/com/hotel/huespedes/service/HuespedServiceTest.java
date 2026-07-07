package com.hotel.huespedes.service;

import com.hotel.huespedes.dto.HuespedDto;
import com.hotel.huespedes.entity.HuespedEntity;
import com.hotel.huespedes.exception.HuespedNotFoundException;
import com.hotel.huespedes.repository.HuespedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HuespedService - pruebas unitarias")
class HuespedServiceTest {

    @Mock
    private HuespedRepository huespedRepository;

    @InjectMocks
    private HuespedService huespedService;

    private HuespedEntity huespedExistente;
    private HuespedDto huespedDto;

    @BeforeEach
    void setUp() {
        huespedExistente = new HuespedEntity(
                "12345678-9", "Juan Pérez González", "juan.perez@email.com",
                "+56912345678", "Santiago, Chile");

        huespedDto = new HuespedDto(
                "12345678-9", "Juan Pérez González", "juan.perez@email.com",
                "+56912345678", "Santiago, Chile");
    }

    @Nested
    @DisplayName("buscarPorRut()")
    class BuscarPorRut {

        @Test
        @DisplayName("retorna el DTO cuando el RUT existe")
        void buscarPorRut_existente_retornaDto() {
            when(huespedRepository.findByRut("12345678-9")).thenReturn(Optional.of(huespedExistente));

            HuespedDto resultado = huespedService.buscarPorRut("12345678-9");

            assertThat(resultado.getRut()).isEqualTo("12345678-9");
            assertThat(resultado.getNombreCompleto()).isEqualTo("Juan Pérez González");
            assertThat(resultado.getEmail()).isEqualTo("juan.perez@email.com");
        }

        @Test
        @DisplayName("lanza HuespedNotFoundException cuando el RUT no existe")
        void buscarPorRut_inexistente_lanzaHuespedNotFoundException() {
            when(huespedRepository.findByRut("99999999-9")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> huespedService.buscarPorRut("99999999-9"))
                    .isInstanceOf(HuespedNotFoundException.class)
                    .hasMessageContaining("99999999-9");
        }
    }

    @Nested
    @DisplayName("registrarOActualizarHuesped()")
    class RegistrarOActualizarHuesped {

        @Test
        @DisplayName("retorna mensaje de registro cuando el RUT es nuevo")
        void registrarOActualizarHuesped_rutNuevo_retornaMensajeDeRegistro() {
            when(huespedRepository.findByRut("12345678-9")).thenReturn(Optional.empty());
            when(huespedRepository.save(any(HuespedEntity.class))).thenReturn(huespedExistente);

            String mensaje = huespedService.registrarOActualizarHuesped(huespedDto);

            assertThat(mensaje).contains("registrado exitosamente");
            assertThat(mensaje).contains("12345678-9");
        }

        @Test
        @DisplayName("retorna mensaje de actualización cuando el RUT ya existe")
        void registrarOActualizarHuesped_rutExistente_retornaMensajeDeActualizacion() {
            when(huespedRepository.findByRut("12345678-9")).thenReturn(Optional.of(huespedExistente));
            when(huespedRepository.save(any(HuespedEntity.class))).thenReturn(huespedExistente);

            String mensaje = huespedService.registrarOActualizarHuesped(huespedDto);

            assertThat(mensaje).contains("actualizado exitosamente");
        }

        @Test
        @DisplayName("mapea correctamente el DTO a la entidad antes de guardar")
        void registrarOActualizarHuesped_mapeaCorrectamenteAEntidad() {
            when(huespedRepository.findByRut("12345678-9")).thenReturn(Optional.empty());
            when(huespedRepository.save(any(HuespedEntity.class))).thenReturn(huespedExistente);

            huespedService.registrarOActualizarHuesped(huespedDto);

            ArgumentCaptor<HuespedEntity> captor = ArgumentCaptor.forClass(HuespedEntity.class);
            verify(huespedRepository, times(1)).save(captor.capture());
            HuespedEntity guardado = captor.getValue();
            assertThat(guardado.getRut()).isEqualTo(huespedDto.getRut());
            assertThat(guardado.getNombreCompleto()).isEqualTo(huespedDto.getNombreCompleto());
            assertThat(guardado.getEmail()).isEqualTo(huespedDto.getEmail());
            assertThat(guardado.getTelefono()).isEqualTo(huespedDto.getTelefono());
            assertThat(guardado.getProcedencia()).isEqualTo(huespedDto.getProcedencia());
        }

        @Test
        @DisplayName("siempre consulta primero por RUT antes de guardar")
        void registrarOActualizarHuesped_consultaPorRutAntesDeGuardar() {
            when(huespedRepository.findByRut(any())).thenReturn(Optional.empty());
            when(huespedRepository.save(any(HuespedEntity.class))).thenReturn(huespedExistente);

            huespedService.registrarOActualizarHuesped(huespedDto);

            verify(huespedRepository, never()).findById(any());
            verify(huespedRepository, times(1)).findByRut(huespedDto.getRut());
        }
    }
}
