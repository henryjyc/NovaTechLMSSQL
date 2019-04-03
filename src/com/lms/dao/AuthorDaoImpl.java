package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Author;

public final class AuthorDaoImpl implements AuthorDao {
	private final PreparedStatement updateStatement;
	private final PreparedStatement deleteStatement;
	private final PreparedStatement findStatement;
	private final PreparedStatement getAllStatement;
	private final PreparedStatement createStatement;

	/**
	 * To create an instance of this DAO, the caller must supply the database
	 * connection.
	 *
	 * @param dbConnection the database connection.
	 */
	public AuthorDaoImpl(final Connection dbConnection) throws SQLException {
		updateStatement = dbConnection.prepareStatement(
				"UPDATE `tbl_author` SET `authorName` = ? WHERE `authorId` = ?");
		deleteStatement = dbConnection
				.prepareStatement("DELETE FROM `tbl_author` WHERE `authorId` = ?");
		findStatement = dbConnection
				.prepareStatement("SELECT * FROM `tbl_author` WHERE `authorId` = ?");
		getAllStatement = dbConnection.prepareStatement("SELECT * FROM `tbl_author`");
		createStatement = dbConnection.prepareStatement(
				"INSERT INTO `tbl_author` (`authorName`) VALUES (?)");
	}

	@Override
	public void update(final Author author) throws SQLException {
		synchronized (updateStatement) {
			updateStatement.setString(1, author.getName());
			updateStatement.setInt(2, author.getId());
			updateStatement.executeUpdate();
		}
	}

	@Override
	public void delete(final Author author) throws SQLException {
		synchronized (deleteStatement) {
			deleteStatement.setInt(1, author.getId());
			deleteStatement.executeUpdate();
		}
	}

	@Override
	public Author get(final int id) throws SQLException {
		synchronized (findStatement) {
			findStatement.setInt(1, id);
			try (ResultSet result = findStatement.executeQuery()) {
				Author retval = null;
				while (result.next()) {
					if (retval != null) {
						throw new IllegalStateException("Multiple results for key");
					} else {
						retval = new Author(id, result.getString("authorName"));
					}
				}
				return retval;
			}
		}
	}

	@Override
	public List<Author> getAll() throws SQLException {
		final List<Author> retval = new ArrayList<>();
		synchronized (getAllStatement) {
			try (final ResultSet result = getAllStatement.executeQuery()) {
				while (result.next()) {
					retval.add(new Author(result.getInt("authorId"),
							result.getString("authorName")));
				}
			}
		}
		return retval;
	}

	@Override
	public Author create(final String authorName) throws SQLException {
		synchronized (createStatement) {
			createStatement.setString(1, authorName);
			createStatement.executeUpdate();
			try (final ResultSet result = createStatement.getGeneratedKeys()) {
				result.next();
				return new Author(result.getInt("authorId"), authorName);
			}
		}
	}
}
