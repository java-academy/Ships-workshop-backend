package com.ja.ships_backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.testng.Assert.assertEquals;

@SpringBootTest
public class ShipsBackendApplicationTests {

	@Test
	void applicationStartTest() {
		// given
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
		// then
		ShipsBackendApplication.main(new String[] {});
	}

	@org.testng.annotations.Test
	 public void testHomeMessage() {
		// given
		final String expectedMessage = "Welcome to Ships Backend";
		// when
		final String homeMessage = new ShipsBackendApplication().home();
		// then
		assertEquals(expectedMessage, homeMessage);
	}
}
