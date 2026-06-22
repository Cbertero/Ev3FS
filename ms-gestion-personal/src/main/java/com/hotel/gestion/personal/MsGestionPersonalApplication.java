package com.hotel.gestion.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hotel.gestion.personal", "com.hotel.exception"})
public class MsGestionPersonalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGestionPersonalApplication.class, args);
	}

}
