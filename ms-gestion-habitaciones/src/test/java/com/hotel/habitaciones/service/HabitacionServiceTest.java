package com.hotel.habitaciones.service;

import com.hotel.habitaciones.dto.HabitacionDto;
import com.hotel.habitaciones.entity.HabitacionEntity;
import com.hotel.habitaciones.exception.EstadoInvalidoException;
import com.hotel.habitaciones.exception.HabitacionNotFoundException;
import com.hotel.habitaciones.repository.HabitacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HabitacionService - pruebas unitarias")
class HabitacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private HabitacionService habitacionService;

    private HabitacionEntity habitacionDisponible;

    @BeforeEach
    void setUp() {
        habitacionDisponible = new HabitacionEntity(1L, "DOBLE", 75000.0, "DISPONIBLE");
    }

    @Nested
    @DisplayName("listarDisponibles()")
    class ListarDisponibles {

        @Test
        @DisplayName("retorna la lista de DTOs mapeados cuando existen habitaciones disponibles")
        void listarDisponibles_conHabitacionesDisponibles_retornaListaDeDtos() {
            HabitacionEntity otra = new HabitacionEntity(2L, "SUITE", 120000.0, "DISPONIBLE");
            when(habitacionRepository.findByEstado("DISPONIBLE"))
                    .thenReturn(List.of(habitacionDisponible, otra));

            List<HabitacionDto> resultado = habitacionService.listarDisponibles();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).getIdHabitacion()).isEqualTo(1L);
            assertThat(resultado.get(0).getTipoHabitacion()).isEqualTo("DOBLE");
            assertThat(resultado.get(0).getPrecioBase()).isEqualTo(75000.0);
            assertThat(resultado.get(0).getEstado()).isEqualTo("DISPONIBLE");
            assertThat(resultado.get(1).getIdHabitacion()).isEqualTo(2L);
            verify(habitacionRepository, times(1)).findByEstado("DISPONIBLE");
        }

        @Test
        @DisplayName("retorna lista vacía cuando no hay habitaciones disponibles")
        void listarDisponibles_sinHabitacionesDisponibles_retornaListaVacia() {
            when(habitacionRepository.findByEstado("DISPONIBLE")).thenReturn(List.of());

            List<HabitacionDto> resultado = habitacionService.listarDisponibles();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("retorna el DTO cuando la habitación existe")
        void obtenerPorId_idExistente_retornaDto() {
            when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacionDisponible));

            HabitacionDto dto = habitacionService.obtenerPorId(1L);

            assertThat(dto.getIdHabitacion()).isEqualTo(1L);
            assertThat(dto.getTipoHabitacion()).isEqualTo("DOBLE");
            assertThat(dto.getPrecioBase()).isEqualTo(75000.0);
        }

        @Test
        @DisplayName("lanza HabitacionNotFoundException cuando la habitación no existe")
        void obtenerPorId_idInexistente_lanzaHabitacionNotFoundException() {
            when(habitacionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> habitacionService.obtenerPorId(99L))
                    .isInstanceOf(HabitacionNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("actualizarEstadoHabitacion()")
    class ActualizarEstadoHabitacion {

        @ParameterizedTest(name = "acepta el estado válido \"{0}\"")
        @ValueSource(strings = {"DISPONIBLE", "OCUPADA", "MANTENCION"})
        @DisplayName("actualiza el estado y guarda la entidad cuando el estado es válido")
        void actualizarEstadoHabitacion_estadoValido_actualizaYGuarda(String estadoValido) {
            when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacionDisponible));
            when(habitacionRepository.save(any(HabitacionEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            String mensaje = habitacionService.actualizarEstadoHabitacion(1L, estadoValido);

            ArgumentCaptor<HabitacionEntity> captor = ArgumentCaptor.forClass(HabitacionEntity.class);
            verify(habitacionRepository).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo(estadoValido);
            assertThat(mensaje).contains("1").contains(estadoValido);
        }

        @Test
        @DisplayName("normaliza el estado recibido en minúsculas a mayúsculas")
        void actualizarEstadoHabitacion_estadoEnMinusculas_normalizaAMayusculas() {
            when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacionDisponible));
            when(habitacionRepository.save(any(HabitacionEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            String mensaje = habitacionService.actualizarEstadoHabitacion(1L, "ocupada");

            assertThat(habitacionDisponible.getEstado()).isEqualTo("OCUPADA");
            assertThat(mensaje).contains("OCUPADA");
        }

        @Test
        @DisplayName("lanza EstadoInvalidoException y no consulta el repositorio cuando el estado no es válido")
        void actualizarEstadoHabitacion_estadoInvalido_lanzaEstadoInvalidoException() {
            assertThatThrownBy(() -> habitacionService.actualizarEstadoHabitacion(1L, "LIBRE"))
                    .isInstanceOf(EstadoInvalidoException.class)
                    .hasMessageContaining("LIBRE");

            verify(habitacionRepository, never()).findById(any());
            verify(habitacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza HabitacionNotFoundException cuando el estado es válido pero la habitación no existe")
        void actualizarEstadoHabitacion_idInexistente_lanzaHabitacionNotFoundException() {
            when(habitacionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> habitacionService.actualizarEstadoHabitacion(99L, "OCUPADA"))
                    .isInstanceOf(HabitacionNotFoundException.class)
                    .hasMessageContaining("99");

            verify(habitacionRepository, never()).save(any());
        }
    }
}
