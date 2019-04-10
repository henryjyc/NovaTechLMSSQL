package com.lms.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lms.dao.InMemoryDBFactory;
import com.lms.exceptions.CriticalSQLException;
import com.lms.exceptions.TransactionException;
import com.lms.model.Publisher;
import com.lms.service.AdministratorService;
import com.lms.service.AdministratorServiceImpl;

/**
 * Tests of some basic menu functionality.
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
public class SalemMainMenuTest {
	/**
	 * The connection to the database.
	 */
	private Connection db;

	/**
	 * Administrator service involved in tests.
	 */
	private AdministratorService adminService;
	/**
	 * Sample publisher name for tests.
	 */
	private static final String SAMPLE_PUBLISHER_NAME = "Tester 1 Test";
	/**
	 * Sample publisher address for tests.
	 */
	private static final String SAMPLE_PUBLISHER_ADDRESS = "410 Terry Ave. North, Seattle, WA, 98109-5210";
	/**
	 * Sample publisher phone for tests.
	 */
	private static final String SAMPLE_PUBLISHER_PHONE = "(206) 266-1000";

	/**
	 * Set up the DB connection and the DAO before running each test.
	 *
	 * @throws SQLException on database errors
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void initAll() throws IOException, SQLException, CriticalSQLException {
		db = InMemoryDBFactory.getConnection("library");
		adminService = new AdministratorServiceImpl(db);
	}

	/**
	 * Tear down the DB connection after running each test.
	 *
	 * @throws SQLException on database error
	 */
	@AfterEach
	public void cleanUp() throws SQLException {
		db.close();
	}

	/**
	 * Test that the menu interface for adding a publisher works."
	 * @throws IOException on I/O error
	 * @throws TransactionException on database error
	 */
	@Test
	public void testingMenu() throws IOException, TransactionException {
		final Publisher lastPublisher = adminService.createPublisher(SAMPLE_PUBLISHER_NAME);
		final int newIdIncrement = lastPublisher.getId() + 1;
		adminService.deletePublisher(lastPublisher);

		final StringReader userInput = new StringReader("1\n2\n" + SAMPLE_PUBLISHER_NAME
				+ "\n" + SAMPLE_PUBLISHER_ADDRESS + "\n" + SAMPLE_PUBLISHER_PHONE + "\n0\n0\n");
		try (PrintWriter out = new PrintWriter(new StringWriter())) {
			final AdministratorMenu subMenu = new AdministratorMenu(adminService,
					new MenuHelper(new Scanner(userInput), out));
			subMenu.menu();
		}

		final List<Publisher> newPublishers = adminService.getAllPublishers()
				.parallelStream().filter(p -> p.getId() == newIdIncrement)
				.collect(Collectors.toList());
		final Publisher newRecord = newPublishers.get(0);

		assertEquals(newIdIncrement, newRecord.getId(),
				"new publisher has expected ID");
		assertEquals(SAMPLE_PUBLISHER_NAME, newRecord.getName(),
				"new publisher has expected name");
		assertEquals(SAMPLE_PUBLISHER_ADDRESS, newRecord.getAddress(),
				"new publisher has expected address");
		assertEquals(SAMPLE_PUBLISHER_PHONE, newRecord.getPhone(),
				"new publisher has expected phone");

		adminService.deletePublisher(newRecord);
	}
}
