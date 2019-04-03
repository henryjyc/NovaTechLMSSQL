package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Publisher;

public final class BookDaoImpl implements BookDao {
	private final PreparedStatement updateStatement;
	private final PreparedStatement deleteStatement;
	private final PreparedStatement findStatement;
	private final PreparedStatement getAllStatement;
	private final PreparedStatement findAuthorStatement;
	private final PreparedStatement findPublisherStatement;
	private final PreparedStatement createAuthorStatement;
	private final PreparedStatement createPublisherStatement;
	private final PreparedStatement createBookStatement;

	public BookDaoImpl(final Connection dbConnection) throws SQLException {
		updateStatement = dbConnection.prepareStatement(
				"UPDATE `tbl_book` SET `title` = ?, `authId` = ?, `pubId` = ? WHERE `bookId` = ?");
		deleteStatement = dbConnection
				.prepareStatement("DELETE FROM `tbl_book` WHERE `bookID` = ?");
		findStatement = dbConnection.prepareStatement(
				"SELECT * FROM `tbl_book` INNER JOIN `tbl_author` ON `tbl_book`.`authId` = `tbl_author`.`authorId` INNER JOIN `tbl_publisher` ON `tbl_book`.`pubId` = `tbl_publisher`.`publisherId` WHERE `tbl_book`.`bookId` = ?");
		getAllStatement = dbConnection.prepareStatement(
				"SELECT * FROM `tbl_book` INNER JOIN `tbl_author` ON `tbl_book`.`authId` = `tbl_author`.`authorId` INNER JOIN `tbl_publisher` ON `tbl_book`.`pubId` = `tbl_publisher`.`publisherId`");
		findAuthorStatement = dbConnection
				.prepareStatement("SELECT * FROM `tbl_author` WHERE `authorId` = ?");
		findPublisherStatement = dbConnection.prepareStatement(
				"SELECT * FROM `tbl_publisher` WHERE `publisherId` = ?");
		createAuthorStatement = dbConnection
				.prepareStatement("INSERT INTO `tbl_author` (`authorName`) VALUES(?)");
		createPublisherStatement = dbConnection.prepareStatement(
				"INSERT INTO `tbl_publisher` (`publisherName`, `publisherAddress`, `publisherPhone`) VALUES(?, ?, ?)");
		createBookStatement = dbConnection.prepareStatement(
				"INSERT INTO `tbl_book` (`title`, `authId`, `pubId`) VALUES(?, ?, ?)");
	}

	@Override
	public void update(final Book book) throws SQLException {
		final Author author = book.getAuthor();
		final Publisher publisher = book.getPublisher();
		synchronized (updateStatement) {
			updateStatement.setString(1, book.getTitle());
			if (author == null) {
				updateStatement.setNull(2, Types.INTEGER);
			} else {
				updateStatement.setInt(2, author.getId());
			}
			if (publisher == null) {
				updateStatement.setNull(3, Types.INTEGER);
			} else {
				updateStatement.setInt(3, publisher.getId());
			}
			updateStatement.setInt(4, book.getId());
			updateStatement.executeUpdate();
		}
	}

	@Override
	public void delete(final Book book) throws SQLException {
		synchronized (deleteStatement) {
			deleteStatement.setInt(1, book.getId());
			deleteStatement.executeUpdate();
		}
	}

	@Override
	public Book get(final int id) throws SQLException {
		synchronized (findStatement) {
			findStatement.setInt(1, id);
			try (ResultSet result = findStatement.executeQuery()) {
				Book retval = null;
				while (result.next()) {
					if (retval != null) {
						throw new IllegalStateException("Multiple results for key");
					} else {
						Author author;
						final int authorId = result.getInt("authorId");
						if (result.wasNull()) {
							author = null;
						} else {
							author = new Author(authorId, result.getString("authorName"));
						}
						Publisher publisher;
						final int publisherId = result.getInt("publisherId");
						if (result.wasNull()) {
							publisher = null;
						} else {
							publisher = new Publisher(publisherId,
									result.getString("publisherName"),
									result.getString("publisherAddress"),
									result.getString("publisherPhone"));
						}
						retval = new Book(id, result.getString("title"), author,
								publisher);
					}
				}
				return retval;
			}
		}
	}

	@Override
	public List<Book> getAll() throws SQLException {
		final List<Book> retval = new ArrayList<>();
		synchronized (getAllStatement) {
			try (final ResultSet result = getAllStatement.executeQuery()) {
				while (result.next()) {
					Author author;
					final int authorId = result.getInt("authorId");
					if (result.wasNull()) {
						author = null;
					} else {
						author = new Author(authorId, result.getString("authorName"));
					}
					Publisher publisher;
					final int publisherId = result.getInt("publisherId");
					if (result.wasNull()) {
						publisher = null;
					} else {
						publisher = new Publisher(publisherId,
								result.getString("publisherName"),
								result.getString("publisherAddress"),
								result.getString("publisherPhone"));
					}
					retval.add(new Book(result.getInt("bookId"),
							result.getString("title"), author, publisher));
				}
			}
			return retval;
		}
	}

	@Override
	public Book create(final String title, final Author author, final Publisher publisher)
			throws SQLException {
		synchronized (createBookStatement) {
			createBookStatement.setString(1, title);
			if (author == null) {
				createBookStatement.setNull(2, Types.VARCHAR);
			} else {
				createBookStatement.setInt(2, author.getId());
			}
			if (publisher == null) {
				createBookStatement.setNull(3, Types.VARCHAR);
			} else {
				createBookStatement.setInt(3, publisher.getId());
			}
			createBookStatement.executeUpdate();
			try (final ResultSet result = createBookStatement.getGeneratedKeys()) {
				result.next();
				return new Book(result.getInt("authorId"), title, author, publisher);
			}
		}
	}
}
