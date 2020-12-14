package com.ja.ships_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tinylog.Logger;

/**
 * Main class responsible for start an application.
 */
@RestController  //TODO: remove it. Separate controller for it
@SpringBootApplication
public class ShipsBackendApplication {
	//TODO: remove password and login from properties file

	@RequestMapping("/")
	String home() {
		return "Welcome to Ships Backend";
	}//TODO: remove it



	public static void main(String[] args) {
		Logger.debug("Application start");
		String ENV_PORT = System.getenv().get("PORT");
		String ENV_DYNO = System.getenv().get("DYNO");
		if(ENV_PORT != null && ENV_DYNO != null) {
			System.getProperties().put("server.port", ENV_PORT);
		}
		SpringApplication.run(ShipsBackendApplication.class, args);
	}
}
