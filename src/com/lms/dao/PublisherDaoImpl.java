package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.lms.model.Publisher;

public final class PublisherDaoImpl implements PublisherDao {
	public PublisherDaoImpl(final Connection dbConnection) {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void update(final Publisher t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void delete(final Publisher t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Publisher get(final int id) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public List<Publisher> getAll() throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Publisher create(final String publisherName, final String publisherAddress,
			final String publisherPhone) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}
}
