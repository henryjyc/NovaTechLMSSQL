package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Publisher;

public final class BookDaoImpl implements BookDao {
	public BookDaoImpl(final Connection dbConection) {
		throw new IllegalStateException("Not yet implemented");
	}
	@Override
	public void update(final Book t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public void delete(final Book t) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Book get(final int id) throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public List<Book> getAll() throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public Book create(final String title, final Author author, final Publisher publisher)
			throws SQLException {
		throw new IllegalStateException("Not yet implemented");
	}
}
