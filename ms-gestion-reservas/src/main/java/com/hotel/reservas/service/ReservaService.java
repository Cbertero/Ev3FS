package com.hotel.reservas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hotel.reservas.dto.ReservaDto;
import com.hotel.reservas.entity.ReservaEntity;
import com.hotel.reservas.exception.DatosReservaInvalidosException;
import com.hotel.reservas.exception.ReservaCanceladaException;
import com.hotel.reservas.exception.ReservaNotFoundException;
import com.hotel.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final double PRECIO_BASE_DEFECTO = 50000.0;
    private final ReservaRepository reservaRepository;
    private final RestTemplate restTemplate;

    // URL base del MS de Habitaciones, inyectada desde application.yaml
    // (propiedad hotel.ms.habitaciones.url) y sobreescribible en Docker
    // vía variable de entorno HOTEL_MS_HABITACIONES_URL.
    @Value("${hotel.ms.habitaciones.url:http://ms-gestion-habitaciones:8084}")
    private String habitacionesUrl;

    public String crearReserva(ReservaDto dto) {
        if (dto.getCantidadDias() == null || dto.getCantidadDias() < 1) {
            throw new DatosReservaInvalidosException(
                    "cantidadDias es obligatorio y debe ser mayor a cero.");
        }
        if (dto.getRutHuesped() == null || dto.getRutHuesped().isBlank()) {
            throw new DatosReservaInvalidosException("El RUT del huésped no puede estar vacío.");
        }
        if (dto.getIdHabitacion() == null) {
            throw new DatosReservaInvalidosException("El ID de habitación es obligatorio.");
        }

        // Llamada al MS de Habitaciones (Integrado respetando tu flujo)
        double precioBase = obtenerPrecioHabitacion(dto.getIdHabitacion());
        double montoTotal = precioBase * dto.getCantidadDias();

        ReservaEntity entity = new ReservaEntity(
                null,
                dto.getRutHuesped(),
                dto.getIdHabitacion(),
                dto.getCantidadDias(),
                montoTotal,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "ACTIVA"
        );

        ReservaEntity guardada = reservaRepository.save(entity);
        return String.format("Reserva #%d creada exitosamente. Monto total: $%.0f CLP",
                guardada.getIdReserva(), montoTotal);
    }

    public List<ReservaDto> obtenerHistorialPorCliente(String rut) {
        return reservaRepository.findByRutHuesped(rut)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public String cancelarReserva(Long idReserva) {
        ReservaEntity entity = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if ("CANCELADA".equals(entity.getEstado())) {
            throw new ReservaCanceladaException(idReserva);
        }

        entity.setEstado("CANCELADA");
        reservaRepository.save(entity);
        return "Reserva #" + idReserva + " cancelada exitosamente.";
    }

    // Consulta el precio real de la habitación al MS de Habitaciones.
    // Antes llamaba a "http://localhost:8080/..." (hardcodeado), lo que
    // dentro de un contenedor Docker apunta al propio contenedor de reservas
    // y nunca llegaba al gateway ni al servicio de habitaciones.
    // Ahora usa la URL inyectada por configuración y parsea el campo
    // "precioBase" real del HabitacionDto en vez de descartar la respuesta.
    private double obtenerPrecioHabitacion(Long idHabitacion) {
        try {
            String url = habitacionesUrl + "/api/habitaciones/" + idHabitacion;
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("precioBase")) {
                return response.get("precioBase").asDouble(PRECIO_BASE_DEFECTO);
            }
            return PRECIO_BASE_DEFECTO;
        } catch (Exception e) {
            return PRECIO_BASE_DEFECTO; // Si el servicio de habitaciones no responde, usa el fallback
        }
    }

    private ReservaDto toDto(ReservaEntity e) {
        return new ReservaDto(
                e.getIdReserva(),
                e.getRutHuesped(),
                e.getIdHabitacion(),
                e.getCantidadDias(),
                e.getMontoTotal()
        );
    }
}
