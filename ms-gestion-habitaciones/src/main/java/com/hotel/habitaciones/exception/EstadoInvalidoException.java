package com.hotel.habitaciones.exception;

public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String estado) {
        super("Estado inválido: '" + estado + "'. Los valores permitidos son: DISPONIBLE, OCUPADA, MANTENCION");
    }
}
