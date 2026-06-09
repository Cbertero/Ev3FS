package com.hotel.reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de transferencia de la Reserva")
public class ReservaDto extends RepresentationModel<ReservaDto> {

    @Schema(description = "ID único de la reserva (generado automáticamente)", example = "1")
    private Long idReserva;

    @NotBlank(message = "El RUT del huésped es obligatorio")
    @Schema(description = "RUT del huésped que realiza la reserva", example = "12345678-9")
    private String rutHuesped;

    @NotNull(message = "El ID de habitación es obligatorio")
    @Schema(description = "ID de la habitación a reservar", example = "102")
    private Long idHabitacion;

    @NotNull(message = "La cantidad de días es obligatoria")
    @Min(value = 1, message = "La estadía debe ser de al menos 1 día")
    @Schema(description = "Número de noches de estadía", example = "3")
    private Integer cantidadDias;

    @Schema(description = "Monto total calculado (precioBase x cantidadDias)", example = "225000.0")
    private Double montoTotal;
}
