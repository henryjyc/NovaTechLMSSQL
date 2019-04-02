package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.lms.model.Book;
import com.lms.model.Branch;

public final class CopiesDaoImpl implements CopiesDao {
	public CopiesDaoImpl(final Connection dbConnection) {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public int getCopies(final Branch branch, final Book book) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void setCopies(final Branch branch, final Book book, final int noOfCopies)
			throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Map<Book, Integer> getAllBranchCopies(final Branch branch)
			throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Map<Branch, Integer> getAllBookCopies(final Book book) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Map<Branch, Map<Book, Integer>> getAllCopies() throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}
}
