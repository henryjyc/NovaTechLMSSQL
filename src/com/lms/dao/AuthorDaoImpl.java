package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.lms.model.Author;

public final class AuthorDaoImpl implements AuthorDao {
	public AuthorDaoImpl(final Connection dbConnection) {
		throw new IllegalStateException("Not yet implemented");
	}
	@Override
	public void update(final Author t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void delete(final Author t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Author get(final int id) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public List<Author> getAll() throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Author create(final String authorName) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}
}
