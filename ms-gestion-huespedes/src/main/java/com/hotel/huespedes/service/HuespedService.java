package com.hotel.huespedes.service;

import com.hotel.huespedes.dto.HuespedDto;
import com.hotel.huespedes.entity.HuespedEntity;
import com.hotel.huespedes.exception.HuespedNotFoundException;
import com.hotel.huespedes.repository.HuespedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HuespedService {

    private final HuespedRepository huespedRepository;

    public HuespedDto buscarPorRut(String rut) {
        HuespedEntity entity = huespedRepository.findByRut(rut)
                .orElseThrow(() -> new HuespedNotFoundException(rut));
        return toDto(entity);
    }


    public String registrarOActualizarHuesped(HuespedDto dto) {
        boolean existe = huespedRepository.findByRut(dto.getRut()).isPresent();
        HuespedEntity entity = toEntity(dto);
        huespedRepository.save(entity);
        return existe
                ? "Huésped con RUT " + dto.getRut() + " actualizado exitosamente."
                : "Huésped con RUT " + dto.getRut() + " registrado exitosamente.";
    }



    private HuespedDto toDto(HuespedEntity e) {
        return new HuespedDto(
                e.getRut(),
                e.getNombreCompleto(),
                e.getEmail(),
                e.getTelefono(),
                e.getProcedencia()
        );
    }

    private HuespedEntity toEntity(HuespedDto dto) {
        return new HuespedEntity(
                dto.getRut(),
                dto.getNombreCompleto(),
                dto.getEmail(),
                dto.getTelefono(),
                dto.getProcedencia()
        );
    }
}
