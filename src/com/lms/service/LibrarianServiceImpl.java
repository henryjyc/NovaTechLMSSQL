package com.lms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.lms.customExceptions.TransactionException;
import com.lms.dao.BookDao;
import com.lms.dao.CopiesDao;
import com.lms.dao.LibraryBranchDao;
import com.lms.model.Book;
import com.lms.model.Branch;

public final class LibrarianServiceImpl implements LibrarianService {
	private final LibraryBranchDao branchDao;
	private final BookDao bookDao;
	private final CopiesDao copiesDao;

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
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void updateBranch(final Branch branch) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void setBranchCopies(final Branch branch, final Book book,
			final int noOfCopies) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Book> getAllBooks() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Map<Branch, Map<Book, Integer>> getAllCopies() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}
}
