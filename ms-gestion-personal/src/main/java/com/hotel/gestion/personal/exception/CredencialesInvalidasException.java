package com.hotel.gestion.personal.exception;

/**
 * Se lanza cuando el RUT no existe o la contraseña no coincide con el hash
 * almacenado. Se maneja con su propio @ExceptionHandler en este servicio
 * (ver PersonalExceptionHandler) en vez de en la librería hotel-global-exception,
 * porque "credenciales inválidas" es una regla de negocio propia del login
 * de personal y no algo que el resto de microservicios necesite reutilizar.
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
