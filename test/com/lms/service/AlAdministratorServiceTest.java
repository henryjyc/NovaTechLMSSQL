package com.lms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lms.dao.AuthorDao;
import com.lms.dao.AuthorDaoImpl;
import com.lms.dao.InMemoryDBFactory;
import com.lms.dao.PublisherDao;
import com.lms.dao.PublisherDaoImpl;
import com.lms.exceptions.TransactionException;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Publisher;

/**
 * Tests of administrator service.
 *
 * @author Al Amine Ahmed Moussa
 * @author Jonathan Lovelace (integration and polishing)
 */
public class AlAdministratorServiceTest {
	/**
	 * The service under test.
	 */
	private AdministratorService administratorService;
	/**
	 * Author DAO used in tests.
	 */
	private AuthorDao authorDao;
	/**
	 * Publisher DAO used in tests.
	 */
	private PublisherDao publisherDao;

	/**
	 * The connection to the database.
	 */
	private Connection conn;

	/**
	 * Set up the DB connection and the DAO before each test.
	 *
	 * @throws SQLException on database errors
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void setUp() throws SQLException, IOException {
		conn = InMemoryDBFactory.getConnection("library");
		administratorService = new AdministratorServiceImpl(conn);
		authorDao = new AuthorDaoImpl(conn);
		publisherDao = new PublisherDaoImpl(conn);
	}

	/**
	 * Tear down the database after each test.
	 *
	 * @throws SQLException on database error while closing the connection
	 */
	@AfterEach
	public void tearDown() throws SQLException {
		conn.close();
	}

	/**
	 * Test that creating a book works.
	 *
	 * @throws SQLException         on database error
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void createBookTest() throws SQLException, TransactionException {
		final String str = "Admin Book Test";

		final Author author = authorDao.get(1);
		final Publisher publisher = publisherDao.get(1);

		final Book b = administratorService.createBook(str, author, publisher);

		assertEquals(str, b.getTitle(), "created book has expected title");
	}

	/**
	 * Test that creating an author works.
	 *
	 * @throws SQLException         on database error
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void createAuthorTest() throws SQLException, TransactionException {
		final String str = "Author Admin Test";
		final Author author = administratorService.createAuthor(str);
		assertEquals(str, author.getName(), "created author has expected name");
	}

	/**
	 * Test that creating a publisher works.
	 *
	 * @throws SQLException         on database error
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void createPublisherTest() throws SQLException, TransactionException {
		final String str1 = "Publisher2";
		final String str2 = "AddressTest2";
		final String str3 = "PhoneTest2";

		final Publisher publisher = administratorService.createPublisher(str1, str2,
				str3);
		assertEquals(str1, publisher.getName(),
				"created publisher has expected name");
		assertEquals(str2, publisher.getAddress(),
				"created publisher has expected address");
		assertEquals(str3, publisher.getPhone(),
				"created publisher has expected phone");
	}

	/**
	 * Test that creating a branch works.
	 *
	 * @throws SQLException         on database error
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void createBranchTest() throws SQLException, TransactionException {
		final String str1 = "Branch1";
		final String str2 = "AddressTest1";

		final Branch branch = administratorService.createBranch(str1, str2);
		assertEquals(str1, branch.getName(), "created branch has expected name");
		assertEquals(str2, branch.getAddress(),
				"created branch has expected address");
	}

	/**
	 * Test that creating a borrower works.
	 *
	 * @throws SQLException         on database error
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void createBorrowerTest() throws SQLException, TransactionException {
		final String str1 = "Borrower1";
		final String str2 = "AddressTest21";
		final String str3 = "PhoneTest1";

		final Borrower borrower = administratorService.createBorrower(str1, str2,
				str3);
		assertEquals(str1, borrower.getName(), "created borrower has expected name");
		assertEquals(str2, borrower.getAddress(),
				"created borrower has expected address");
		assertEquals(str3, borrower.getPhone(),
				"created borrower has expected phone");
	}
}
