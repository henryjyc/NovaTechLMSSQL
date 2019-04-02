package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.lms.model.Borrower;

public final class BorrowerDaoImpl implements BorrowerDao {
	public BorrowerDaoImpl(final Connection dbConnection) {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void update(final Borrower t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void delete(final Borrower t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Borrower get(final int id) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public List<Borrower> getAll() throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Borrower create(final String borrowerName, final String borrowerAddress,
			final String borrowerPhone) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}
}
