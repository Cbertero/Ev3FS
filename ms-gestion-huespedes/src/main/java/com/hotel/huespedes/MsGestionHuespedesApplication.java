package com.hotel.huespedes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hotel.huespedes", "com.hotel.exception"})
public class MsGestionHuespedesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsGestionHuespedesApplication.class, args);
    }
}
