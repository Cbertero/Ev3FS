package com.hotel.reservas.exception;

public class ReservaNotFoundException extends RuntimeException {
    public ReservaNotFoundException(Long id) {
        super("Reserva no encontrada con el ID: " + id);
    }
}
