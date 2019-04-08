package com.lms.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.dao.BookDao;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.CopiesDao;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.DBConnectionFactory;
import com.lms.dao.LibraryBranchDao;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.exceptions.TransactionException;
import com.lms.exceptions.UnknownSQLException;
import com.lms.exceptions.UpdateException;
import com.lms.model.Book;
import com.lms.model.Branch;
import com.lms.util.ThrowingRunnable;

/**
 * The "service" class to help UIs for librarians.
 *
 * @author Jonathan Lovelace
 */
public final class LibrarianServiceImpl implements LibrarianService {
	/**
	 * Extracted constant message to log when a rollback fails.
	 */
	private static final String ROLLBACK_FAILED = "Further error while rolling back transaction";
	/**
	 * The DAO for the "branches" table.
	 */
	private final LibraryBranchDao branchDao;
	/**
	 * The DAO for the "books" table.
	 */
	private final BookDao bookDao;
	/**
	 * The DAO for the "copies" table.
	 */
	private final CopiesDao copiesDao;
	/**
	 * Logger for handling errors in the DAO layer.
	 */
	private static final Logger LOGGER = Logger.getLogger(LibrarianService.class.getName());
	/**
	 * Method to use to commit a transaction, if the DAO backend supports transactions.
	 */
	private final ThrowingRunnable<SQLException> commitHandle;
	/**
	 * Method to use to roll back a transaction, if the DAO backend supports transactions.
	 */
	private final ThrowingRunnable<SQLException> rollbackHandle;

	/**
	 * To construct an instance of this service class, the caller must supply
	 * instances of each DAO it uses and method references to commit and roll back
	 * transactions.
	 *
	 * @param branchDao the library-branch DAO
	 * @param bookDao   the book DAO
	 * @param copiesDao the book-copies DAO
	 * @param commit       the method handle to commit a transaction, if the backend
	 *                     supports that
	 * @param rollback     the method handle to roll back a transaction, if the
	 *                     backend supports that
	 */
	public LibrarianServiceImpl(final LibraryBranchDao branchDao, final BookDao bookDao,
			final CopiesDao copiesDao, final ThrowingRunnable<SQLException> commit,
			final ThrowingRunnable<SQLException> rollback) {
		this.branchDao = branchDao;
		this.bookDao = bookDao;
		this.copiesDao = copiesDao;
		commitHandle = commit;
		rollbackHandle = rollback;
	}

	/**
	 * To construct this service class using this constructor, the caller must
	 * merely supply a connection to the database.
	 * @param db the connection to the database
	 * @throws SQLException on error setting up DAOs.
	 */
	public LibrarianServiceImpl(final Connection db) throws SQLException {
		this(new LibraryBranchDaoImpl(db), new BookDaoImpl(db),
				new CopiesDaoImpl(db), db::commit, db::rollback);
	}
	/**
	 * Constructor that uses the default DB connection factory to supply the
	 * database connection and uses the default DAO implementations.
	 * @throws IOException on I/O error reading DB configuration
	 * @throws SQLException on error setting up the database or DAOs
	 */
	public LibrarianServiceImpl() throws IOException, SQLException {
		this(DBConnectionFactory.getDatabaseConnection());
	}
	@Override
	public List<Branch> getAllBranches() throws TransactionException {
		try {
			return branchDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE,  "SQL error while getting all branches", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Getting all branches failed", except);
		}
	}

	@Override
	public void updateBranch(final Branch branch) throws TransactionException {
		try {
			branchDao.update(branch);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a book", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new UpdateException("Updating book record failed", except);
		}
	}

	@Override
	public void setBranchCopies(final Branch branch, final Book book,
			final int noOfCopies) throws TransactionException {
		try {
			copiesDao.setCopies(branch, book, noOfCopies);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while setting copy records", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Setting copy records failed", except);
		}
	}

	@Override
	public List<Book> getAllBooks() throws TransactionException {
		try {
			return bookDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting books", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Getting book records failed", except);
		}
	}

	@Override
	public Map<Branch, Map<Book, Integer>> getAllCopies() throws TransactionException {
		try {
			return copiesDao.getAllCopies();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting copy records", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Getting copy records failed", except);
		}
	}
	@Override
	public void commit() throws TransactionException {
		try {
			commitHandle.run();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "Error of some kind while committing transaction", except);
			throw new UnknownSQLException("Committing the transaction failed", except);
		}
	}
}
