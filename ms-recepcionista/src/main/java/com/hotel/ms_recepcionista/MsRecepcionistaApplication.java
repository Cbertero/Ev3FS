package com.hotel.ms_recepcionista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MsRecepcionistaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRecepcionistaApplication.class, args);
	}

	// Puedes poner esto en tu clase MsRecepcionistaApplication.java
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
