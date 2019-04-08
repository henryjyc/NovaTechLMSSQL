package com.lms.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
/**
 * Test of library-branch DAO.
 * @author Jonathan Lovelace
 *
 */
class LibraryBranchDaoTest {
	/**
	 * The DAO being tested.
	 */
	private LibraryBranchDao testee;
	/**
	 * The connection to the database.
	 */
	private Connection db;

	/**
	 * Set up the DB connection and the DAO before each test.
	 *
	 * @throws SQLException on database errors
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void setUp() throws SQLException, IOException {
		db = InMemoryDBFactory.getConnection("library");
		testee = new LibraryBranchDaoImpl(db);
	}

	/**
	 * Tear down the database after each test.
	 *
	 * @throws SQLException on database error while closing the connection
	 */
	@AfterEach
	public void tearDown() throws SQLException {
		db.close();
	}

	@Test
	public final void testCreate() throws SQLException {
		try (PreparedStatement ps = db.prepareStatement(
				"SELECT * from `tbl_library_branch` ORDER BY `branchId` DESC")) {
			try (ResultSet rs = ps.executeQuery()) {
				assertFalse(rs.next(), "No branches before create()");
			}
			testee.create("first branch", "");
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				assertEquals("first branch", rs.getString("branchName"),
						"name was inserted");
				assertEquals(1, rs.getInt("branchId"), "row has expected ID");
				assertNull(rs.getString("branchAddress"),
						"empty string translated to null");
			}
			testee.create("second branch", "address");
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				assertEquals("second branch", rs.getString("branchName"),
						"different name was inserted");
				assertEquals(2, rs.getInt("branchId"), "row has expected ID");
				assertEquals("address", rs.getString("branchAddress"),
						"address was inserted");
			}
		}
	}

	@Test
	public final void testUpdate() throws SQLException {
		try (PreparedStatement ps = db.prepareStatement(
				"SELECT * from `tbl_library_branch` ORDER BY `branchId` DESC")) {
			final Branch branch = testee.create("branch name", "branch address");
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				assertEquals("branch name", rs.getString("branchName"),
						"name was inserted");
				assertEquals(1, rs.getInt("branchId"), "row has expected ID");
				assertEquals("branch address", rs.getString("branchAddress"),
						"address was inserted");
			}
			branch.setName("changed name");
			testee.update(branch);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				assertEquals("changed name", rs.getString("branchName"),
						"name was changed");
				assertEquals(1, rs.getInt("branchId"), "still the same row");
				assertEquals("branch address", rs.getString("branchAddress"),
						"address was not changed");
			}
			branch.setAddress("changed address");
			testee.update(branch);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				assertEquals(1, rs.getInt("branchId"), "still the same row");
				assertEquals("changed address", rs.getString("branchAddress"),
						"address was changed");
			}
		}
	}

	@Test
	public final void testDelete() throws SQLException {
		testee.create("to keep", "");
		final Branch toRemove = testee.create("to delete", "");
		assertEquals(2, testee.getAll().size(), "Two rows before deletion");
		testee.delete(toRemove);
		assertEquals(1, testee.getAll().size(), "One row after deletion");
	}

	@Test
	public final void testDeleteCopiesCascade() throws SQLException {
		final Branch toKeep = testee.create("to keep", "");
		final Branch toRemove = testee.create("to remove", "");
		final BookDao bookDao = new BookDaoImpl(db);
		final CopiesDao copiesDao = new CopiesDaoImpl(db);
		final Book book = bookDao.create("title", null, null);
		copiesDao.setCopies(toKeep, book, 2);
		copiesDao.setCopies(toRemove, book, 3);
		assertEquals(5,
				copiesDao.getAllBookCopies(book).values().stream()
						.mapToInt(Integer::intValue).sum(),
				"Expected number of copies before branch removal");
		testee.delete(toRemove);
		assertEquals(2,
				copiesDao.getAllBookCopies(book).values().stream()
						.mapToInt(Integer::intValue).sum(),
				"Expected number of copies after branch removal");
	}

	@Test
	public final void testDeleteLoansCascade() throws SQLException {
		final Branch toKeep = testee.create("to keep", "");
		final Branch toRemove = testee.create("to remove", "");
		final BookDao bookDao = new BookDaoImpl(db);
		final BorrowerDao borrowerDao = new BorrowerDaoImpl(db);
		final BookLoansDao loansDao = new BookLoansDaoImpl(db);
		final Borrower borrower = borrowerDao.create("borrower", "", "");
		final Book book = bookDao.create("title", null, null);
		loansDao.create(book, borrower, toKeep, null, null);
		loansDao.create(book, borrower, toRemove, null, null);
		assertEquals(2, loansDao.getAll().size(), "Two loans before branch removal");
		testee.delete(toRemove);
		assertEquals(1, loansDao.getAll().size(), "One loan after branch removal");
	}

	@Test
	public final void testGet() throws SQLException {
		try (PreparedStatement ps = db.prepareStatement(
				"INSERT INTO `tbl_library_branch` (`branchName`, `branchAddress`) VALUES (?, ?)")) {
			ps.setString(1, "name one");
			ps.setString(2, "address one");
			ps.executeUpdate();
			ps.setString(1, "name two");
			ps.setString(2, "address two");
			ps.executeUpdate();
			ps.setString(1, "name three");
			ps.setNull(2, Types.VARCHAR);
			ps.executeUpdate();
		}
		assertEquals(new Branch(2, "name two", "address two"), testee.get(2),
				"Expected branch returned by get()");
		assertEquals(new Branch(3, "name three", ""), testee.get(3),
				"Expected branch returned by get()");
		assertEquals(new Branch(1, "name one", "address one"), testee.get(1),
				"Expected branch returned by get()");
		assertNull(testee.get(6), "get() returns null when no such row");
	}

	@Test
	public final void testGetAll() throws SQLException {
		assertEquals(Collections.emptyList(), testee.getAll(),
				"getAll() returns empty list when no rows");
		try (PreparedStatement ps = db.prepareStatement(
				"INSERT INTO `tbl_library_branch` (`branchName`, `branchAddress`) VALUES (?, ?)")) {
			ps.setString(1, "name one");
			ps.setString(2, "address one");
			ps.executeUpdate();
			ps.setString(1, "name two");
			ps.setString(2, "address two");
			ps.executeUpdate();
			ps.setString(1, "name three");
			ps.setNull(2, Types.VARCHAR);
			ps.executeUpdate();
		}
		assertEquals(
				new HashSet<>(Arrays.asList(new Branch(2, "name two", "address two"),
						new Branch(3, "name three", ""),
						new Branch(1, "name one", "address one"))),
				new HashSet<>(testee.getAll()),
				"Expected values returned by getAll()");
	}

}
