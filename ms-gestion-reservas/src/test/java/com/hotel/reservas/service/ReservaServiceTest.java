package com.hotel.reservas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.reservas.dto.ReservaDto;
import com.hotel.reservas.entity.ReservaEntity;
import com.hotel.reservas.exception.DatosReservaInvalidosException;
import com.hotel.reservas.exception.ReservaCanceladaException;
import com.hotel.reservas.exception.ReservaNotFoundException;
import com.hotel.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService - pruebas unitarias")
class ReservaServiceTest {

    private static final String HABITACIONES_URL = "http://test-habitaciones:8084";
    private static final double PRECIO_BASE_DEFECTO = 50000.0;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReservaService reservaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // @Value no se resuelve en un test unitario puro (sin contexto Spring),
        // así que se inyecta manualmente el valor por defecto de la URL del MS de habitaciones.
        ReflectionTestUtils.setField(reservaService, "habitacionesUrl", HABITACIONES_URL);
    }

    private JsonNode habitacionJsonConPrecio(double precioBase) {
        return objectMapper.createObjectNode().put("precioBase", precioBase);
    }

    @Nested
    @DisplayName("crearReserva()")
    class CrearReserva {

        @Test
        @DisplayName("consulta el precio real de la habitación y calcula el monto total correctamente")
        void crearReserva_datosValidos_calculaMontoConPrecioDelServicioDeHabitaciones() {
            ReservaDto dto = new ReservaDto(null, "11111111-1", 5L, 3, null);
            when(restTemplate.getForObject(eq(HABITACIONES_URL + "/api/habitaciones/5"), eq(JsonNode.class)))
                    .thenReturn(habitacionJsonConPrecio(50000.0));
            when(reservaRepository.save(any(ReservaEntity.class))).thenAnswer(invocation -> {
                ReservaEntity e = invocation.getArgument(0);
                e.setIdReserva(10L);
                return e;
            });

            String mensaje = reservaService.crearReserva(dto);

            ArgumentCaptor<ReservaEntity> captor = ArgumentCaptor.forClass(ReservaEntity.class);
            verify(reservaRepository).save(captor.capture());
            ReservaEntity guardada = captor.getValue();
            assertThat(guardada.getMontoTotal()).isEqualTo(150000.0);
            assertThat(guardada.getRutHuesped()).isEqualTo("11111111-1");
            assertThat(guardada.getIdHabitacion()).isEqualTo(5L);
            assertThat(guardada.getEstado()).isEqualTo("ACTIVA");
            assertThat(mensaje).contains("Reserva #10").contains("150000");
        }

        @Test
        @DisplayName("usa el precio por defecto cuando el MS de habitaciones no responde")
        void crearReserva_servicioHabitacionesCaido_usaPrecioPorDefecto() {
            ReservaDto dto = new ReservaDto(null, "11111111-1", 5L, 2, null);
            when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                    .thenThrow(new RestClientException("conexión rechazada"));
            when(reservaRepository.save(any(ReservaEntity.class))).thenAnswer(invocation -> {
                ReservaEntity e = invocation.getArgument(0);
                e.setIdReserva(11L);
                return e;
            });

            reservaService.crearReserva(dto);

            ArgumentCaptor<ReservaEntity> captor = ArgumentCaptor.forClass(ReservaEntity.class);
            verify(reservaRepository).save(captor.capture());
            assertThat(captor.getValue().getMontoTotal()).isEqualTo(PRECIO_BASE_DEFECTO * 2);
        }

        @Test
        @DisplayName("usa el precio por defecto cuando la respuesta no trae el campo precioBase")
        void crearReserva_respuestaSinPrecioBase_usaPrecioPorDefecto() {
            ReservaDto dto = new ReservaDto(null, "11111111-1", 5L, 1, null);
            when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                    .thenReturn(objectMapper.createObjectNode());
            when(reservaRepository.save(any(ReservaEntity.class))).thenAnswer(invocation -> {
                ReservaEntity e = invocation.getArgument(0);
                e.setIdReserva(12L);
                return e;
            });

            reservaService.crearReserva(dto);

            ArgumentCaptor<ReservaEntity> captor = ArgumentCaptor.forClass(ReservaEntity.class);
            verify(reservaRepository).save(captor.capture());
            assertThat(captor.getValue().getMontoTotal()).isEqualTo(PRECIO_BASE_DEFECTO);
        }

        @Test
        @DisplayName("lanza DatosReservaInvalidosException cuando cantidadDias es nulo, sin llamar servicios externos")
        void crearReserva_cantidadDiasNula_lanzaExcepcionSinLlamarDependencias() {
            ReservaDto dto = new ReservaDto(null, "11111111-1", 5L, null, null);

            assertThatThrownBy(() -> reservaService.crearReserva(dto))
                    .isInstanceOf(DatosReservaInvalidosException.class);

            verifyNoInteractions(restTemplate);
            verifyNoInteractions(reservaRepository);
        }

        @Test
        @DisplayName("lanza DatosReservaInvalidosException cuando cantidadDias es menor a uno")
        void crearReserva_cantidadDiasMenorAUno_lanzaExcepcion() {
            ReservaDto dto = new ReservaDto(null, "11111111-1", 5L, 0, null);

            assertThatThrownBy(() -> reservaService.crearReserva(dto))
                    .isInstanceOf(DatosReservaInvalidosException.class);
        }

        @Test
        @DisplayName("lanza DatosReservaInvalidosException cuando el RUT del huésped está vacío")
        void crearReserva_rutHuespedVacio_lanzaExcepcion() {
            ReservaDto dto = new ReservaDto(null, "   ", 5L, 2, null);

            assertThatThrownBy(() -> reservaService.crearReserva(dto))
                    .isInstanceOf(DatosReservaInvalidosException.class);
        }

        @Test
        @DisplayName("lanza DatosReservaInvalidosException cuando el ID de habitación es nulo")
        void crearReserva_idHabitacionNulo_lanzaExcepcion() {
            ReservaDto dto = new ReservaDto(null, "11111111-1", null, 2, null);

            assertThatThrownBy(() -> reservaService.crearReserva(dto))
                    .isInstanceOf(DatosReservaInvalidosException.class);
        }
    }

    @Nested
    @DisplayName("obtenerHistorialPorCliente()")
    class ObtenerHistorialPorCliente {

        @Test
        @DisplayName("retorna la lista de reservas mapeadas del cliente")
        void obtenerHistorialPorCliente_conReservas_retornaListaDeDtos() {
            ReservaEntity reserva = new ReservaEntity(1L, "11111111-1", 5L, 3, 150000.0,
                    "2026-01-01T10:00:00", "ACTIVA");
            when(reservaRepository.findByRutHuesped("11111111-1")).thenReturn(List.of(reserva));

            List<ReservaDto> historial = reservaService.obtenerHistorialPorCliente("11111111-1");

            assertThat(historial).hasSize(1);
            assertThat(historial.get(0).getIdReserva()).isEqualTo(1L);
            assertThat(historial.get(0).getMontoTotal()).isEqualTo(150000.0);
        }

        @Test
        @DisplayName("retorna lista vacía cuando el cliente no tiene reservas")
        void obtenerHistorialPorCliente_sinReservas_retornaListaVacia() {
            when(reservaRepository.findByRutHuesped("22222222-2")).thenReturn(List.of());

            List<ReservaDto> historial = reservaService.obtenerHistorialPorCliente("22222222-2");

            assertThat(historial).isEmpty();
        }
    }

    @Nested
    @DisplayName("cancelarReserva()")
    class CancelarReserva {

        @Test
        @DisplayName("cambia el estado a CANCELADA y guarda cuando la reserva está activa")
        void cancelarReserva_activa_cambiaEstadoYGuarda() {
            ReservaEntity activa = new ReservaEntity(1L, "11111111-1", 5L, 3, 150000.0,
                    "2026-01-01T10:00:00", "ACTIVA");
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(activa));
            when(reservaRepository.save(any(ReservaEntity.class))).thenAnswer(i -> i.getArgument(0));

            String mensaje = reservaService.cancelarReserva(1L);

            assertThat(activa.getEstado()).isEqualTo("CANCELADA");
            assertThat(mensaje).contains("Reserva #1").contains("cancelada exitosamente");
            verify(reservaRepository, times(1)).save(activa);
        }

        @Test
        @DisplayName("lanza ReservaCanceladaException cuando la reserva ya estaba cancelada")
        void cancelarReserva_yaCancelada_lanzaExcepcion() {
            ReservaEntity cancelada = new ReservaEntity(2L, "11111111-1", 5L, 3, 150000.0,
                    "2026-01-01T10:00:00", "CANCELADA");
            when(reservaRepository.findById(2L)).thenReturn(Optional.of(cancelada));

            assertThatThrownBy(() -> reservaService.cancelarReserva(2L))
                    .isInstanceOf(ReservaCanceladaException.class)
                    .hasMessageContaining("2");

            verify(reservaRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza ReservaNotFoundException cuando la reserva no existe")
        void cancelarReserva_inexistente_lanzaExcepcion() {
            when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservaService.cancelarReserva(99L))
                    .isInstanceOf(ReservaNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }
}
