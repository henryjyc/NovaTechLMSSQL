package com.lms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.TransactionException;
import com.lms.customExceptions.UnknownSQLException;
import com.lms.customExceptions.UpdateException;
import com.lms.dao.BookDao;
import com.lms.dao.CopiesDao;
import com.lms.dao.LibraryBranchDao;
import com.lms.model.Book;
import com.lms.model.Branch;

/**
 * The "service" class to help UIs for librarians.
 *
 * @author Jonathan Lovelace
 */
public final class LibrarianServiceImpl implements LibrarianService {
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
	 * To construct an instance of this service class, the caller must supply
	 * instances of each DAO it uses.
	 *
	 * @param branchDao the library-branch DAO
	 * @param bookDao   the book DAO
	 * @param copiesDao the book-copies DAO
	 */
	public LibrarianServiceImpl(final LibraryBranchDao branchDao, final BookDao bookDao,
			final CopiesDao copiesDao) {
		this.branchDao = branchDao;
		this.bookDao = bookDao;
		this.copiesDao = copiesDao;
	}

	@Override
	public List<Branch> getAllBranches() throws TransactionException {
		try {
			return branchDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE,  "SQL error while getting all branches", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting all branches failed", except);
		}
	}

	@Override
	public void updateBranch(final Branch branch) throws TransactionException {
		try {
			branchDao.update(branch);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a book", except);
			// TODO: abort current transaction
			throw new UpdateException("Updating book record failed");
		}
	}

	@Override
	public void setBranchCopies(final Branch branch, final Book book,
			final int noOfCopies) throws TransactionException {
		try {
			copiesDao.setCopies(branch, book, noOfCopies);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while setting copy records", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Setting copy records failed", except);
		}
	}

	@Override
	public List<Book> getAllBooks() throws TransactionException {
		try {
			return bookDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting books", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting book records failed", except);
		}
	}

	@Override
	public Map<Branch, Map<Book, Integer>> getAllCopies() throws TransactionException {
		try {
			return copiesDao.getAllCopies();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting copy records", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting copy records failed", except);
		}
	}
}
