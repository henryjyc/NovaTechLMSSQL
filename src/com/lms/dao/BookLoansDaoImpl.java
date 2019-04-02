package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

public final class BookLoansDaoImpl implements BookLoansDao {
	public BookLoansDaoImpl(final Connection dbConnection) {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Loan create(final Book book, final Borrower borrower, final Branch branch,
			final LocalDateTime dateOut, final LocalDate dueDate) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void update(final Loan loan) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void delete(final Loan loan) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Loan get(final Book book, final Borrower borrower, final Branch branch)
			throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public List<Loan> getAll() throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}
}
