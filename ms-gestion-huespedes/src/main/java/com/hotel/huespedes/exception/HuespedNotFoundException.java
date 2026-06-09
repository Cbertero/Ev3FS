package com.hotel.huespedes.exception;

public class HuespedNotFoundException extends RuntimeException {
    public HuespedNotFoundException(String rut) {
        super("No se encontró ningún huésped con el RUT: " + rut);
    }
}
