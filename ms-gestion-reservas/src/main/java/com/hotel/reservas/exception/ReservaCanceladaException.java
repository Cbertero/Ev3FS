package com.hotel.reservas.exception;

public class ReservaCanceladaException extends IllegalArgumentException {
    public ReservaCanceladaException(Long id) {
        super("La reserva con ID " + id + " ya se encuentra cancelada.");
    }
}
