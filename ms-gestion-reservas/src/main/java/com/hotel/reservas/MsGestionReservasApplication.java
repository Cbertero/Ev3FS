package com.hotel.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hotel.reservas", "com.hotel.exception"})
public class MsGestionReservasApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsGestionReservasApplication.class, args);
    }
}
