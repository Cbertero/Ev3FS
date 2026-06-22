package com.hotel.log.auditoria.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "logs_auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLog;

    private String usuarioResponsable;
    private String microservicioOrigen;
    private String accion;
    private String timestamp;
}
