package com.hotel.gestion.personal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDto {
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;
    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;
    private String cargo;
    private String turno;
    @Min(value = 1, message = "El sueldo debe ser mayor a 0")
    private Double sueldoBase;
}



