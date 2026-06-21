package com.hotel.habitaciones.service;

import com.hotel.habitaciones.dto.HabitacionDto;
import com.hotel.habitaciones.entity.HabitacionEntity;
import com.hotel.habitaciones.exception.EstadoInvalidoException;
import com.hotel.habitaciones.exception.HabitacionNotFoundException;
import com.hotel.habitaciones.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("DISPONIBLE", "OCUPADA", "MANTENCION");

    private final HabitacionRepository habitacionRepository;


    public List<HabitacionDto> listarDisponibles() {
        return habitacionRepository.findByEstado("DISPONIBLE")
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public HabitacionDto obtenerPorId(Long id) {
        HabitacionEntity entity = habitacionRepository.findById(id)
                .orElseThrow(() -> new HabitacionNotFoundException(id));
        return toDto(entity);
    }


    public String actualizarEstadoHabitacion(Long id, String nuevoEstado) {
        String estadoUpper = nuevoEstado.toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estadoUpper)) {
            throw new EstadoInvalidoException(nuevoEstado);
        }
        HabitacionEntity entity = habitacionRepository.findById(id)
                .orElseThrow(() -> new HabitacionNotFoundException(id));

        entity.setEstado(estadoUpper);
        habitacionRepository.save(entity);
        return "Estado de habitación " + id + " actualizado a: " + estadoUpper;
    }



    private HabitacionDto toDto(HabitacionEntity e) {
        return new HabitacionDto(
                e.getIdHabitacion(),
                e.getTipo(),
                e.getPrecio(),
                e.getEstado()
        );
    }
}
