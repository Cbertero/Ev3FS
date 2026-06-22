package com.hotel.habitaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hotel.habitaciones", "com.hotel.exception"})
public class MsGestionHabitacionesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsGestionHabitacionesApplication.class, args);
    }
}
