package com.ja.ships_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tinylog.Logger;

/**
 * Main class responsible for start an application.
 */
@RestController
@SpringBootApplication
public class ShipsBackendApplication {

	@RequestMapping("/")
	String home() {
		return "Welcome to Ships Backend";
	}

	public static void main(String[] args) {
		Logger.debug("Application start");
		SpringApplication.run(ShipsBackendApplication.class, args);
	}
}
