package com.hotel.reservas.exception;

public class DatosReservaInvalidosException extends RuntimeException {
    public DatosReservaInvalidosException(String mensaje) {
        super(mensaje);
    }
}
