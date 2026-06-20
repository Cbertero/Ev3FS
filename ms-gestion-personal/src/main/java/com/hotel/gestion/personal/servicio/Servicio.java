package com.hotel.gestion.personal.servicio;

import com.hotel.gestion.personal.dto.PersonalDto;
import com.hotel.gestion.personal.dto.TokenResponseDto;
import com.hotel.gestion.personal.dto.UsuarioLoginDto;
import com.hotel.gestion.personal.entity.PersonalEntity;
import com.hotel.gestion.personal.repositorio.Repositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Servicio {

    private final Repositorio repositorio;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public TokenResponseDto registrar(PersonalDto dto) {
        if (repositorio.existsById(dto.getRut())) {
            throw new IllegalArgumentException("El trabajador ya existe.");
        }

        // Lógica de sueldos según cargo
        double sueldoBase = 500000.0;
        String cargo = dto.getCargo().toUpperCase();
        if (cargo.equals("RECEPCIONISTA")) sueldoBase = 650000.0;
        else if (cargo.equals("ADMINISTRADOR")) sueldoBase = 1200000.0;

        double sueldoTotal = sueldoBase + (dto.getHorasExtras() * 6500.0);

        // Guardar en BD
        PersonalEntity entidad = PersonalEntity.builder()
                .rut(dto.getRut())
                .nombreCompleto(dto.getNombreCompleto())
                .cargo(cargo)
                .turno(dto.getTurno())
                .horasExtras(dto.getHorasExtras())
                .sueldoBase(sueldoBase)
                .sueldoTotal(sueldoTotal)
                .build();

        repositorio.save(entidad);


        return loguear(new UsuarioLoginDto(dto.getRut(), "clave_temporal"));
    }

    public TokenResponseDto loguear(UsuarioLoginDto loginDto) {


        return TokenResponseDto.builder()
                .token(UUID.randomUUID().toString())
                .rol("RECEPCIONISTA")
                .autenticado(true)
                .build();
    }


    public List<PersonalDto> listar(String rolUsuario) {
        List<PersonalEntity> entidades = repositorio.findAll();
        List<PersonalDto> dtos = new ArrayList<>();
        boolean esAutorizado = rolUsuario.equalsIgnoreCase("RECEPCIONISTA") || rolUsuario.equalsIgnoreCase("ADMINISTRADOR");

        for (PersonalEntity e : entidades) {
            dtos.add(PersonalDto.builder()
                    .rut(e.getRut())
                    .nombreCompleto(e.getNombreCompleto())
                    .cargo(e.getCargo())
                    .turno(e.getTurno())
                    .horasExtras(e.getHorasExtras())
                    .sueldoBase(esAutorizado ? e.getSueldoBase() : null)
                    .sueldoTotal(esAutorizado ? e.getSueldoTotal() : null)
                    .build());
        }
        return dtos;
    }


    public String eliminar(String rut) {
        repositorio.deleteById(rut);
        return "Trabajador eliminado.";
    }
}