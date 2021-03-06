package com.ships;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.testng.Assert.assertEquals;

@SpringBootTest
public class ShipsRoomServiceApplicationTests {

	@Test
	void applicationStartTest() {
		// given
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
		// then
		ShipsRoomServiceApplication.main(new String[] {});
	}

	@Test
	 void testHomeMessage() {
		// given
		final String expectedMessage = "Welcome to Room Service";
		// when
		final String homeMessage = new ShipsController().home();
		// then
		assertEquals(expectedMessage, homeMessage);
	}
}
