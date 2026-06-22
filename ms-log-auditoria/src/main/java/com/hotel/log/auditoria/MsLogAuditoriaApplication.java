package com.hotel.log.auditoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hotel.log.auditoria", "com.hotel.exception"})
public class MsLogAuditoriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsLogAuditoriaApplication.class, args);
	}

}
