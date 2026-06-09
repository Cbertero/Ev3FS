package com.hotel.reservas.exception;

public class ReservaNotFoundException extends RuntimeException {
    public ReservaNotFoundException(Long id) {
        super("No existe ninguna reserva con el ID: " + id);
    }
}
