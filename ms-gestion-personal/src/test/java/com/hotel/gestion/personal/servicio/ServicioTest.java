package com.hotel.gestion.personal.servicio;

import com.hotel.gestion.personal.dto.PersonalDto;
import com.hotel.gestion.personal.dto.TokenResponseDto;
import com.hotel.gestion.personal.entity.PersonalEntity;
import com.hotel.gestion.personal.repositorio.Repositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la clase Servicio (ms-gestion-personal).
 *
 * Se utiliza Mockito para aislar la lógica de negocio del acceso a datos:
 * el Repositorio se simula (mock) en todos los casos, por lo que estas
 * pruebas no requieren base de datos ni contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio - Gestión de Personal")
class ServicioTest {

    @Mock
    private Repositorio repositorio;

    @InjectMocks
    private Servicio servicio;

    private PersonalDto personalDtoBase;

    @BeforeEach
    void setUp() {
        personalDtoBase = PersonalDto.builder()
                .rut("12345678-9")
                .nombreCompleto("Juan Pérez González")
                .cargo("recepcionista")
                .turno("MAÑANA")
                .horasExtras(0)
                .build();
    }

    // ==========================================================
    // registrar()
    // ==========================================================
    @Nested
    @DisplayName("registrar()")
    class Registrar {

        @Test
        @DisplayName("Lanza excepción si el RUT ya existe")
        void registrar_rutDuplicado_lanzaExcepcion() {
            when(repositorio.existsById(personalDtoBase.getRut())).thenReturn(true);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> servicio.registrar(personalDtoBase)
            );

            assertEquals("El trabajador ya existe.", ex.getMessage());
            verify(repositorio, never()).save(any());
        }

        @Test
        @DisplayName("Asigna sueldo base 650000 para cargo RECEPCIONISTA")
        void registrar_cargoRecepcionista_asignaSueldoBaseCorrecto() {
            personalDtoBase.setCargo("recepcionista");
            personalDtoBase.setHorasExtras(0);
            when(repositorio.existsById(any())).thenReturn(false);

            servicio.registrar(personalDtoBase);

            PersonalEntity guardado = capturarEntidadGuardada();
            assertEquals(650000.0, guardado.getSueldoBase());
        }

        @Test
        @DisplayName("Asigna sueldo base 1200000 para cargo ADMINISTRADOR")
        void registrar_cargoAdministrador_asignaSueldoBaseCorrecto() {
            personalDtoBase.setCargo("administrador");
            personalDtoBase.setHorasExtras(0);
            when(repositorio.existsById(any())).thenReturn(false);

            servicio.registrar(personalDtoBase);

            PersonalEntity guardado = capturarEntidadGuardada();
            assertEquals(1200000.0, guardado.getSueldoBase());
        }

        @Test
        @DisplayName("Asigna sueldo base 500000 (default) para cualquier otro cargo")
        void registrar_cargoNoReconocido_asignaSueldoBaseDefault() {
            personalDtoBase.setCargo("camarero");
            personalDtoBase.setHorasExtras(0);
            when(repositorio.existsById(any())).thenReturn(false);

            servicio.registrar(personalDtoBase);

            PersonalEntity guardado = capturarEntidadGuardada();
            assertEquals(500000.0, guardado.getSueldoBase());
        }

        @Test
        @DisplayName("Calcula sueldo total sumando horas extras a 6500 cada una")
        void registrar_conHorasExtras_calculaSueldoTotalCorrecto() {
            personalDtoBase.setCargo("camarero");   // sueldo base 500000
            personalDtoBase.setHorasExtras(10);     // 10 * 6500 = 65000
            when(repositorio.existsById(any())).thenReturn(false);

            servicio.registrar(personalDtoBase);

            PersonalEntity guardado = capturarEntidadGuardada();
            assertEquals(565000.0, guardado.getSueldoTotal());
        }

        @Test
        @DisplayName("Normaliza el cargo a mayúsculas antes de guardar")
        void registrar_cargoMinusculas_seGuardaEnMayusculas() {
            personalDtoBase.setCargo("recepcionista");
            when(repositorio.existsById(any())).thenReturn(false);

            servicio.registrar(personalDtoBase);

            PersonalEntity guardado = capturarEntidadGuardada();
            assertEquals("RECEPCIONISTA", guardado.getCargo());
        }

        @Test
        @DisplayName("Retorna un TokenResponseDto autenticado tras registrar correctamente")
        void registrar_exitoso_retornaTokenAutenticado() {
            when(repositorio.existsById(any())).thenReturn(false);

            TokenResponseDto resultado = servicio.registrar(personalDtoBase);

            assertNotNull(resultado);
            assertNotNull(resultado.getToken());
            assertEquals(true, resultado.isAutenticado());
        }

        private PersonalEntity capturarEntidadGuardada() {
            ArgumentCaptor<PersonalEntity> captor = ArgumentCaptor.forClass(PersonalEntity.class);
            verify(repositorio, times(1)).save(captor.capture());
            return captor.getValue();
        }
    }

    // ==========================================================
    // listar()
    // ==========================================================
    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("Usuario con rol RECEPCIONISTA ve los sueldos")
        void listar_rolRecepcionista_muestraSueldos() {
            PersonalEntity entidad = PersonalEntity.builder()
                    .rut("12345678-9")
                    .nombreCompleto("Juan Pérez")
                    .cargo("RECEPCIONISTA")
                    .turno("MAÑANA")
                    .horasExtras(0)
                    .sueldoBase(650000.0)
                    .sueldoTotal(650000.0)
                    .build();
            when(repositorio.findAll()).thenReturn(List.of(entidad));

            List<PersonalDto> resultado = servicio.listar("RECEPCIONISTA");

            assertEquals(1, resultado.size());
            assertNotNull(resultado.get(0).getSueldoBase());
            assertNotNull(resultado.get(0).getSueldoTotal());
        }

        @Test
        @DisplayName("Usuario con rol ADMINISTRADOR ve los sueldos")
        void listar_rolAdministrador_muestraSueldos() {
            PersonalEntity entidad = PersonalEntity.builder()
                    .rut("12345678-9")
                    .nombreCompleto("Juan Pérez")
                    .cargo("ADMINISTRADOR")
                    .turno("MAÑANA")
                    .horasExtras(0)
                    .sueldoBase(1200000.0)
                    .sueldoTotal(1200000.0)
                    .build();
            when(repositorio.findAll()).thenReturn(List.of(entidad));

            List<PersonalDto> resultado = servicio.listar("administrador"); // case-insensitive

            assertNotNull(resultado.get(0).getSueldoBase());
            assertNotNull(resultado.get(0).getSueldoTotal());
        }

        @Test
        @DisplayName("Usuario con rol no autorizado NO ve los sueldos (quedan en null)")
        void listar_rolNoAutorizado_ocultaSueldos() {
            PersonalEntity entidad = PersonalEntity.builder()
                    .rut("12345678-9")
                    .nombreCompleto("Juan Pérez")
                    .cargo("CAMARERO")
                    .turno("MAÑANA")
                    .horasExtras(0)
                    .sueldoBase(500000.0)
                    .sueldoTotal(500000.0)
                    .build();
            when(repositorio.findAll()).thenReturn(List.of(entidad));

            List<PersonalDto> resultado = servicio.listar("CAMARERO");

            assertNull(resultado.get(0).getSueldoBase());
            assertNull(resultado.get(0).getSueldoTotal());
        }

        @Test
        @DisplayName("Lista vacía cuando no hay personal registrado")
        void listar_sinRegistros_retornaListaVacia() {
            when(repositorio.findAll()).thenReturn(List.of());

            List<PersonalDto> resultado = servicio.listar("ADMINISTRADOR");

            assertTrue(resultado.isEmpty());
        }
    }

    // ==========================================================
    // eliminar()
    // ==========================================================
    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Elimina por RUT y retorna mensaje de confirmación")
        void eliminar_rutValido_retornaMensajeConfirmacion() {
            String rut = "12345678-9";

            String resultado = servicio.eliminar(rut);

            verify(repositorio, times(1)).deleteById(rut);
            assertEquals("Trabajador eliminado.", resultado);
        }
    }
}