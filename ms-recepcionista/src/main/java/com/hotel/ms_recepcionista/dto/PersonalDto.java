package com.hotel.ms_recepcionista.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDto {

    @NotBlank(message = "El RUT es obligatorio.")
    @Pattern(regexp = "^[0-9]{1,2}\\.?[0-9]{3}\\.?[0-9]{3}-[0-9kK]{1}$", message = "Formato de RUT inválido.")
    private String rut;

    @NotBlank(message = "El nombre completo es obligatorio.")
    @Size(min = 3, max = 80)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras.")
    private String nombreCompleto;

    @NotBlank(message = "El cargo es obligatorio.")
    @Pattern(regexp = "^[a-zA-Z ]+$")
    private String cargo;

    @NotBlank(message = "El turno es obligatorio.")
    @Pattern(regexp = "^[a-zA-Z ]+$")
    private String turno;

    private Integer horasExtras;

    private Double sueldoBase;
    private Double sueldoTotal;
}