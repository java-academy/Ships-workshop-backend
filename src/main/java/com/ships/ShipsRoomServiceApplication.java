package com.ships;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tinylog.Logger;

/**
 * Main class responsible for start an application.
 */
@SpringBootApplication
public class ShipsRoomServiceApplication {
	public static void main(String[] args) {
		Logger.debug("Application start");
		configureHeroku();
		SpringApplication.run(ShipsRoomServiceApplication.class, args);
	}

	private static void configureHeroku() {
		String ENV_PORT = System.getenv().get("PORT");
		String ENV_DYNO = System.getenv().get("DYNO");
		if(ENV_PORT != null && ENV_DYNO != null) {
			System.getProperties().put("server.port", ENV_PORT);
		}
	}
}
