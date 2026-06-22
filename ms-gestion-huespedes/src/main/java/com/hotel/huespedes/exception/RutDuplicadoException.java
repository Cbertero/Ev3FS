package com.hotel.huespedes.exception;

public class RutDuplicadoException extends RuntimeException {
    public RutDuplicadoException(String rut) {
        super("Ya existe un huésped registrado con el RUT: " + rut);
    }
}
