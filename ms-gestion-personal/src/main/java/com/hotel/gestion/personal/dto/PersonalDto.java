package com.hotel.gestion.personal.dto;

import jakarta.validation.constraints.*;
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

        // Contraseña en texto plano recibida solo al registrar; Servicio.registrar()
        // la hashea con BCrypt antes de guardarla y nunca se devuelve en las respuestas.
        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
        private String password;

        private Integer horasExtras;


        private Double sueldoBase;
        private Double sueldoTotal;
    }




