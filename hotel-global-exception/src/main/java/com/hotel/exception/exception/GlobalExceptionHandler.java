package com.hotel.exception.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
            String errores = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Error de Validación")
                    .message(errores)
                    .path(request.getRequestURI())
                    .build();

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {

        if (ex.getMessage() != null && (ex.getMessage().toLowerCase().contains("no encontrado") || ex.getMessage().toLowerCase().contains("not found"))) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Recurso No Encontrado")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        throw ex;
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflicto de Datos o Parámetro Inválido")
                .message(ex.getMessage() != null ? ex.getMessage() : "No se pudo guardar la información debido a un conflicto con los datos existentes.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


        @ExceptionHandler(java.net.ConnectException.class)
        public ResponseEntity<ErrorResponse> handleConnectionError(java.net.ConnectException ex, HttpServletRequest request) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.ACCEPTED.value())
                    .error("Servicio Temporalmente Sin Conexión")
                    .message("Tu solicitud fue procesada correctamente en el sistema. Sin embargo, los servidores externos no tienen conexión a internet en este momento; tu notificación se encuentra en proceso de guardado y se enviará automáticamente al restablecerse el enlace.")
                    .path(request.getRequestURI())
                    .build();

            return new ResponseEntity<>(error, HttpStatus.ACCEPTED);
        }


        @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.FORBIDDEN.value())
                    .error("Acceso Prohibido")
                    .message("No cuentas con las credenciales o el rol administrativo necesario para ejecutar esta acción.")
                    .path(request.getRequestURI())
                    .build();

            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }


        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error("Error Interno del Servidor")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();

            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

