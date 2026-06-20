package com.hotel.ms_recepcionista.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de transferencia de la Habitación")
public class HabitacionDto {

    @Schema(description = "ID único de la habitación", example = "101")
    private Long idHabitacion;

    @NotBlank(message = "El tipo de habitación es obligatorio")
    @Schema(description = "Tipo de habitación (SIMPLE, DOBLE, SUITE)", example = "DOBLE")
    private String tipoHabitacion;

    @NotNull(message = "El precio base es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    @Schema(description = "Precio base por noche en pesos CLP", example = "75000.0")
    private Double precioBase;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado de la habitación: DISPONIBLE, OCUPADA, MANTENCION", example = "DISPONIBLE")
    private String estado;
}
