package com.hotel.habitaciones.exception;

public class HabitacionNotFoundException extends RuntimeException {
    public HabitacionNotFoundException(Long id) {
        super("No existe ninguna habitación con el ID: " + id);
    }
}
