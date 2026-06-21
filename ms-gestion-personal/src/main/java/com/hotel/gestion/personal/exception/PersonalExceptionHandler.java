package com.hotel.gestion.personal.exception;

import com.hotel.exception.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Handler propio de este microservicio para errores de login.
 * Se mantiene aquí (en vez de en hotel-global-exception) porque devuelve 401,
 * un código que ningún handler de la librería compartida cubre todavía, y
 * que solo tiene sentido en el contexto de autenticación de personal.
 */
@ControllerAdvice
public class PersonalExceptionHandler {

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredencialesInvalidas(
            CredencialesInvalidasException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Credenciales Inválidas")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
