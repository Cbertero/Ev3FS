package com.hotel.gestion.personal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personal")
public class PersonalEntity {

        @Id
        private String rut;
        private String nombreCompleto;
        private String cargo;
        private String turno;
        private int horasExtras;
        private double sueldoBase;
        private double sueldoTotal;
    }
