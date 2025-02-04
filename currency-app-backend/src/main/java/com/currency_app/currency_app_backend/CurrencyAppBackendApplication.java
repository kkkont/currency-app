package com.currency_app.currency_app_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyAppBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyAppBackendApplication.class, args);
	}

}
