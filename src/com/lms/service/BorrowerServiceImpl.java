package com.lms.service;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.TransactionException;
import com.lms.customExceptions.UnknownSQLException;
import com.lms.dao.BookLoansDao;
import com.lms.dao.BorrowerDao;
import com.lms.dao.CopiesDao;
import com.lms.dao.LibraryBranchDao;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

/**
 * The "service" class to help UIs for borrowers.
 *
 * @author Jonathan Lovelace
 */
public final class BorrowerServiceImpl implements BorrowerService {
	/**
	 * The DAO for the "branches" table.
	 */
	private final LibraryBranchDao branchDao;
	/**
	 * The DAO for the "loans" table.
	 */
	private final BookLoansDao loanDao;
	/**
	 * The DAO for the "copies" table.
	 */
	private final CopiesDao copiesDao;
	/**
	 * The DAO for the "borrowers" table.
	 */
	private final BorrowerDao borrowerDao;
	/**
	 * The clock to get "the current time" from.
	 */
	private final Clock clock;
	/**
	 * Logger for handling errors in the DAO layer.
	 */
	private static final Logger LOGGER = Logger.getLogger(BorrowerService.class.getName());

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
		try {
			return branchDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE,  "SQL error while getting all branches", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting all branches failed", except);
		}
	}

	@Override
	public Loan borrowBook(final Borrower borrower, final Book book, final Branch branch,
			final LocalDateTime dateOut, final LocalDate dueDate) throws TransactionException {
		try {
			return loanDao.create(book, borrower, branch, dateOut, dueDate);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a loan record", except);
			// TODO: abort current transaction
			throw new InsertException("Creating a loan failed");
		}
	}

	@Override
	public Map<Book, Integer> getAllBranchCopies(final Branch branch) throws TransactionException {
		try {
			return copiesDao.getAllBranchCopies(branch);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting branch copies", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting branch copy records failed", except);
		}
	}

	@Override
	public Boolean returnBook(final Borrower borrower, final Book book,
			final Branch branch, final LocalDate dueDate) throws TransactionException {
		final Optional<Loan> loan;
		try {
			loan = Optional.ofNullable(loanDao.get(book, borrower, branch));
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting loan details", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting loan details failed", except);
		}
		if (loan.isPresent()) {
			if (loan.get().getDueDate().isAfter(LocalDate.now(clock))) {
				return false;
			} else {
				try {
					loanDao.delete(loan.get());
				} catch (final SQLException except) {
					LOGGER.log(Level.SEVERE, "SQL error while removing a loan record", except);
					// TODO: abort current transaction
					throw new DeleteException("Removing loan record failed");
				}
				return true;
			}
		} else {
			return null;
		}
	}

	@Override
	public List<Branch> getAllBranchesWithLoan(final Borrower borrower) throws TransactionException {
		return getAllBorrowedBooks(borrower).parallelStream().map(Loan::getBranch)
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> getAllBorrowedBooks(final Borrower borrower) throws TransactionException {
		try {
			return loanDao.getAll().parallelStream()
					.filter(loan -> borrower.equals(loan.getBorrower()))
					.collect(Collectors.toList());
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting loan records", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting loan records failed", except);
		}
	}

	@Override
	public Borrower getBorrower(final int cardNo) throws TransactionException {
		try {
			return borrowerDao.get(cardNo);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting borrower details", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting borrower record failed", except);
		}
	}
}
