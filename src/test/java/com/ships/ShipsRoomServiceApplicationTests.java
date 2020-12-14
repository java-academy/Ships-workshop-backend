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
	 public void testHomeMessage() {
		// given
		final String expectedMessage = "Welcome to Ships Backend";
		// when
		final String homeMessage = new ShipsRoomServiceApplication().home();
		// then
		assertEquals(expectedMessage, homeMessage);
	}
}
