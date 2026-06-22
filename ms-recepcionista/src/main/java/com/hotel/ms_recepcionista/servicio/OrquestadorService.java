package com.hotel.ms_recepcionista.servicio;

import com.hotel.ms_recepcionista.dto.CheckInRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OrquestadorService {

    private final RestTemplate restTemplate;

    @Value("${gateway.url}")
    private String gatewayUrl;

    public String procesarCheckIn(CheckInRequestDto dto) {

        restTemplate.postForObject(gatewayUrl + "/api/huespedes/guardar", dto.getHuesped(), Object.class);
        restTemplate.postForObject(gatewayUrl + "/api/reservas/crear", dto.getReserva(), Object.class);
        restTemplate.postForObject(gatewayUrl + "/api/habitaciones/estado/" + dto.getHabitacion(), null, Object.class);

        var log = java.util.Map.of(
                "usuarioResponsable", dto.getPersonal(),
                "microservicioOrigen", "ms-recepcionista",
                "accion", "CHECK-IN EXITOSO - Reserva: " + dto.getReserva(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );

        restTemplate.postForObject(gatewayUrl + "/api/auditoria/registrar-evento", log, Object.class);

        return "Check-in exitoso realizado por " + dto.getPersonal();
    }
}