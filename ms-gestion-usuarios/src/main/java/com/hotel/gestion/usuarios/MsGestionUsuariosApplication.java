package com.hotel.gestion.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.hotel.gestion.usuarios", "com.hotel.exception"})
public class MsGestionUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGestionUsuariosApplication.class, args);
	}

}