package com.hotel.log.auditoria.service;

import com.hotel.log.auditoria.dto.AuditoriaDto;
import com.hotel.log.auditoria.entity.AuditoriaEntity;
import com.hotel.log.auditoria.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditoriaService {
    private final AuditoriaRepository repository;

    public void registrarEventoCritico(AuditoriaDto dto) {
        AuditoriaEntity entity = AuditoriaEntity.builder()
                .usuarioResponsable(dto.getUsuarioResponsable())
                .microservicioOrigen(dto.getMicroservicioOrigen())
                .accion(dto.getAccion())
                .timestamp(dto.getTimestamp())
                .build();
        repository.save(entity);
    }

    public List<AuditoriaDto> obtenerBitacoraCompleta() {
        return repository.findAll().stream().map(e -> AuditoriaDto.builder()
                .usuarioResponsable(e.getUsuarioResponsable())
                .microservicioOrigen(e.getMicroservicioOrigen())
                .accion(e.getAccion())
                .timestamp(e.getTimestamp())
                .build()).collect(Collectors.toList());
    }
}