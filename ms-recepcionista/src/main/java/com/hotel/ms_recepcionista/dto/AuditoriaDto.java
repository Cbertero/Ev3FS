package com.hotel.ms_recepcionista.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDto {
    private String usuarioResponsable;
    private String microservicioOrigen;
    private String accion;
    private String timestamp;
}