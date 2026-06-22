package com.hotel.ms_notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication (scanBasePackages = {"com.hotel.ms_notificaciones", "com.hotel.exception"})
public class MsNotificacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsNotificacionesApplication.class, args);
	}
}
