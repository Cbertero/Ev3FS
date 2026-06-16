package com.hotel.gestion.personal.servicio;

import com.hotel.gestion.personal.dto.PersonalDto;
import com.hotel.gestion.personal.entity.PersonalEntity;
import com.hotel.gestion.personal.repositorio.Repositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Servicio {

        private final Repositorio repositorio;


        public List<PersonalDto> obtenerTodoElPersonal() {
            return repositorio.findAll().stream()
                    .map(entity -> PersonalDto.builder()
                            .rut(entity.getRut())
                            .nombreCompleto(entity.getNombreCompleto())
                            .cargo(entity.getCargo())
                            .turno(entity.getTurno())
                            .sueldoBase(entity.getSueldoBase())
                            .build())
                    .collect(Collectors.toList());
        }


        public String guardarTrabajador(PersonalDto dto) {

            if (repositorio.existsById(dto.getRut())) {
                throw new IllegalArgumentException("Conflicto de Datos: El trabajador con RUT " + dto.getRut() + " ya está registrado.");
            }

            PersonalEntity entity = PersonalEntity.builder()
                    .rut(dto.getRut())
                    .nombreCompleto(dto.getNombreCompleto())
                    .cargo(dto.getCargo())
                    .turno(dto.getTurno())
                    .sueldoBase(dto.getSueldoBase())
                    .build();

            repositorio.save(entity);
            return "Trabajador registrado exitosamente con el RUT: " + dto.getRut();
        }


        public String darDeBajaTrabajador(String rut) {
            PersonalEntity trabajador = repositorio.findByRut(rut)
                    .orElseThrow(() -> new RuntimeException("Recurso No Encontrado: No existe ningún trabajador con el RUT ingresado."));

            repositorio.delete(trabajador);
            return "Trabajador con RUT " + rut + " ha sido eliminado correctamente del sistema.";
        }
    }
