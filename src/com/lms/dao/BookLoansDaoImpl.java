package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.model.Publisher;

/**
 * A DAO to provide an interface between the "loans" table in the database and
 * the other layers of the code.
 *
 * @author Jonathan Lovelace
 */
public final class BookLoansDaoImpl implements BookLoansDao {
	/**
	 * The SQL query to update existing loan rows.
	 */
	private final PreparedStatement updateStatement;
	/**
	 * The SQL query to delete loan rows.
	 */
	private final PreparedStatement deleteStatement;
	/**
	 * The SQL query to find a loan by its book, branch, and borrower.
	 */
	private final PreparedStatement findStatement;
	/**
	 * The SQL query to get all loans from the database.
	 */
	private final PreparedStatement getAllStatement;
	/**
	 * The SQL query to insert a new loan into the database.
	 */
	private final PreparedStatement createStatement;

	/**
	 * To construct an instance of a DAO, the caller must provide a connection to
	 * the database.
	 *
	 * @param dbConnection the database connection
	 * @throws SQLException on any unexpected database condition while setting up
	 *                      prepared statements
	 */
	public BookLoansDaoImpl(final Connection dbConnection) throws SQLException {
		createStatement = dbConnection.prepareStatement(
				"INSERT INTO `tbl_book_loans` (`bookId`, `branchId`, `cardNo`, `dateOut`, `dueDate`) VALUES (?, ?, ?, ?, ?)");
		updateStatement = dbConnection.prepareStatement(
				"UPDATE `tbl_book_loans` SET `dateOut` = ?, `dueDate` = ? WHERE `bookId` = ? AND `branchId` = ? AND `cardNo` = ?");
		deleteStatement = dbConnection.prepareStatement(
				"DELETE FROM `tbl_book_loans` WHERE `bookId` = ? AND `branchId` = ? AND `cardNo` = ?");
		findStatement = dbConnection.prepareStatement(
				"SELECT * FROM `tbl_book_loans` WhERE `bookId` = ? AND `branchId` = ? AND `cardNo` = ?");
		getAllStatement = dbConnection.prepareStatement(
				"SELECT * FROM `tbl_book_loans` INNER JOIN `tbl_book` ON `tbl_book`.`bookId` = `tbl_book_loans`.`bookId` LEFT JOIN `tbl_author` ON `tbl_book`.`authId` = `tbl_author`.`authorId` LEFT JOIN `tbl_publisher` ON `tbl_book`.`pubId` = `tbl_publisher`.`publisherId` INNER JOIN `tbl_library_branch` ON `tbl_book_loans`.`branchId` = `tbl_library_branch`.`branchId` INNER JOIN `tbl_borrower` ON `tbl_borrower`.`cardNo` = `tbl_book_loans`.`cardNo`");
	}

	@Override
	public Loan create(final Book book, final Borrower borrower, final Branch branch,
			final LocalDateTime dateOut, final LocalDate dueDate) throws SQLException {
		synchronized (createStatement) {
			createStatement.setInt(1, book.getId());
			createStatement.setInt(2, branch.getId());
			createStatement.setInt(3, borrower.getCardNo());
			createStatement.setDate(4, java.sql.Date.valueOf(dateOut.toLocalDate()));
			createStatement.setDate(5, java.sql.Date.valueOf(dueDate));
			createStatement.executeUpdate();
		}
		return new Loan(book, borrower, branch, dateOut, dueDate);
	}

	@Override
	public void update(final Loan loan) throws SQLException {
		synchronized (updateStatement) {
			updateStatement.setDate(1,
					java.sql.Date.valueOf(loan.getDateOut().toLocalDate()));
			updateStatement.setDate(2, java.sql.Date.valueOf(loan.getDueDate()));
			updateStatement.setInt(3, loan.getBook().getId());
			updateStatement.setInt(4, loan.getBranch().getId());
			updateStatement.setInt(5, loan.getBorrower().getCardNo());
			updateStatement.executeUpdate();
		}
	}

	@Override
	public void delete(final Loan loan) throws SQLException {
		synchronized (deleteStatement) {
			deleteStatement.setInt(1, loan.getBook().getId());
			deleteStatement.setInt(2, loan.getBranch().getId());
			deleteStatement.setInt(3, loan.getBorrower().getCardNo());
			deleteStatement.executeUpdate();
		}
	}

	@Override
	public Loan get(final Book book, final Borrower borrower, final Branch branch)
			throws SQLException {
		synchronized (findStatement) {
			findStatement.setInt(1, book.getId());
			findStatement.setInt(2, branch.getId());
			findStatement.setInt(3, borrower.getCardNo());
			try (ResultSet result = findStatement.executeQuery()) {
				Loan retval = null;
				while (result.next()) {
					if (retval == null) {
						retval = new Loan(book, borrower, branch,
								result.getDate("dateOut").toLocalDate().atStartOfDay(),
								result.getDate("dueDate").toLocalDate());
					} else {
						throw new IllegalStateException("Multiple results for key");
					}
				}
				return retval;
			}
		}
	}

	@Override
	public List<Loan> getAll() throws SQLException {
		final List<Loan> retval = new ArrayList<>();
		synchronized (getAllStatement) {
			try (ResultSet result = getAllStatement.executeQuery()) {
				while (result.next()) {
					final int authorId = result.getInt("authorId");
					final Author author;
					if (result.wasNull()) {
						author = null;
					} else {
						author = new Author(authorId, result.getString("authorName"));
					}
					final int publisherId = result.getInt("publisherId");
					final Publisher publisher;
					if (result.wasNull()) {
						publisher = null;
					} else {
						publisher = new Publisher(publisherId,
								result.getString("publisherName"),
								Optional.ofNullable(
										result.getString("publisherAddress"))
										.orElse(""),
								Optional.ofNullable(
										result.getString("publisherPhone"))
										.orElse(""));
					}
					final Book book = new Book(result.getInt("bookId"),
							result.getString("title"), author, publisher);
					final Borrower borrower = new Borrower(result.getInt("cardNo"),
							result.getString("name"), result.getString("address"),
							result.getString("phone"));
					final Branch branch = new Branch(result.getInt("branchId"),
							Optional.ofNullable(result.getString("branchName"))
									.orElse(""),
							Optional.ofNullable(result.getString("branchAddress"))
									.orElse(""));
					retval.add(new Loan(book, borrower, branch,
							result.getDate("dateOut").toLocalDate().atStartOfDay(),
							result.getDate("dueDate").toLocalDate()));
				}
			}
			return retval;
		}
	}
}
