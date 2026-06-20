package com.hotel.ms_recepcionista.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO Maestro que agrupa toda la información para realizar un Check-in")
public class CheckInRequestDto {

        @Valid
        @NotNull(message = "El personal es obligatorio")
        private PersonalDto personal;

        @Valid
        @NotNull(message = "Los datos del huésped son obligatorios")
        private HuespedDto huesped;

        @Valid
        @NotNull(message = "Los detalles de la reserva son obligatorios")
        private ReservaDto reserva;

        @Valid
        @NotNull(message = "La habitación es obligatoria")
        private HabitacionDto habitacion;
}
