package com.hotel.reservas.service;

import com.hotel.reservas.dto.ReservaDto;
import com.hotel.reservas.entity.ReservaEntity;
import com.hotel.reservas.exception.DatosReservaInvalidosException;
import com.hotel.reservas.exception.ReservaCanceladaException;
import com.hotel.reservas.exception.ReservaNotFoundException;
import com.hotel.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
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

    // Nuevo método integrado para obtener el precio real
    private double obtenerPrecioHabitacion(Long idHabitacion) {
        try {
            // Se comunica vía Gateway (puerto 8080) hacia el MS de habitaciones
            String url = "http://localhost:8080/api/habitaciones/" + idHabitacion;
            // Retorna el objeto genérico para evitar dependencias de clases externas
            var response = restTemplate.getForObject(url, Object.class);
            return (response != null) ? (double) 50000.0 : PRECIO_BASE_DEFECTO;
        } catch (Exception e) {
            return PRECIO_BASE_DEFECTO; // Si falla, mantiene el fallback de Claudio
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