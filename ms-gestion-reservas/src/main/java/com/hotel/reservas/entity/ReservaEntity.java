package com.hotel.reservas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "rut_huesped", nullable = false)
    private String rutHuesped;

    @Column(name = "id_habitacion", nullable = false)
    private Long idHabitacion;

    @Column(name = "cantidad_dias", nullable = false)
    private Integer cantidadDias;

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;

    @Column(name = "fecha_creacion", nullable = false)
    private String fechaCreacion;

    @Column(name = "estado", nullable = false)
    private String estado; // ACTIVA, CANCELADA
}
