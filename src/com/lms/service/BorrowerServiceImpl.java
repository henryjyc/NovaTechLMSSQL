package com.lms.service;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.lms.customExceptions.TransactionException;
import com.lms.dao.BookLoansDao;
import com.lms.dao.BorrowerDao;
import com.lms.dao.CopiesDao;
import com.lms.dao.LibraryBranchDao;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

public final class BorrowerServiceImpl implements BorrowerService {
	private final LibraryBranchDao branchDao;
	private final BookLoansDao loanDao;
	private final CopiesDao copiesDao;
	private final BorrowerDao borrowerDao;
	private final Clock clock;

	/**
	 * To construct this service class, the caller must supply instances of each DAO
	 * it uses and a clock to get "the current date".
	 *
	 * @param branchDao   the library-branch DAO.
	 * @param loanDao     the loan DAO
	 * @param copiesDao   the copies DAO
	 * @param borrowerDao the borrower DAO
	 */
	public BorrowerServiceImpl(final LibraryBranchDao branchDao,
			final BookLoansDao loanDao, final CopiesDao copiesDao,
			final BorrowerDao borrowerDao, final Clock clock) {
		this.branchDao = branchDao;
		this.loanDao = loanDao;
		this.copiesDao = copiesDao;
		this.borrowerDao = borrowerDao;
		this.clock = clock;
	}

	@Override
	public List<Branch> getAllBranches() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Loan borrowBook(final Borrower borrower, final Book book, final Branch branch,
			final LocalDateTime dateOut, final LocalDate dueDate) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Map<Book, Integer> getAllBranchCopies(final Branch branch) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Boolean returnBook(final Borrower borrower, final Book book,
			final Branch branch, final LocalDate dueDate) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Branch> getAllBranchesWithLoan(final Borrower borrower) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Loan> getAllBorrowedBooks(final Borrower borrower) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Borrower getBorrower(final int cardNo) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}
}
