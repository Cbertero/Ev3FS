package com.hotel.ms_notificaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class NotificacionRequest {

        @Email(message = "El email no es válido")
        @NotBlank(message = "El destinatario es obligatorio")
        private String destinatario;

        @NotBlank(message = "El asunto es obligatorio")
        private String asunto;

        @NotBlank(message = "El cuerpo del mensaje es obligatorio")
        private String cuerpo;
}

