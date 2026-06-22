package com.hotel.huespedes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de transferencia del Huésped")
public class HuespedDto extends RepresentationModel<HuespedDto> {

    @NotBlank(message = "El RUT es obligatorio")
    @Schema(description = "RUT del huésped (ej: 12345678-9)", example = "12345678-9")
    private String rut;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Schema(description = "Nombre completo del huésped", example = "Juan Pérez González")
    private String nombreCompleto;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Schema(description = "Correo electrónico del huésped", example = "juan.perez@email.com")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;

    @NotBlank(message = "La procedencia es obligatoria")
    @Schema(description = "Ciudad o país de procedencia del huésped", example = "Santiago, Chile")
    private String procedencia;
}
