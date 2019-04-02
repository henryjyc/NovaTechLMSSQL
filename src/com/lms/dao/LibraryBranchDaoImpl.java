package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.lms.model.Branch;

public final class LibraryBranchDaoImpl implements LibraryBranchDao {
	public LibraryBranchDaoImpl(final Connection dbConnection) {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void update(final Branch t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void delete(final Branch t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Branch get(final int id) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public List<Branch> getAll() throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Branch create(final String branchName, final String branchAddress)
			throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}
}
