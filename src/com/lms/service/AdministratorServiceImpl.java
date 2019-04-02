package com.lms.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.TransactionException;
import com.lms.customExceptions.UnknownSQLException;
import com.lms.customExceptions.UpdateException;
import com.lms.dao.AuthorDao;
import com.lms.dao.BookDao;
import com.lms.dao.BookLoansDao;
import com.lms.dao.BorrowerDao;
import com.lms.dao.LibraryBranchDao;
import com.lms.dao.PublisherDao;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.model.Publisher;

/**
 * An implementation of the service class for administrative UIs.
 *
 * <p>TODO: Split interface into "cataloger" and "executive" services.
 *
 * @author Jonathan Lovelace
 *
 */
public final class AdministratorServiceImpl implements AdministratorService {
	private final LibraryBranchDao branchDao;
	private final BookDao bookDao;
	private final AuthorDao authorDao;
	private final PublisherDao publisherDao;
	private final BorrowerDao borrowerDao;
	private final BookLoansDao loansDao;
	/**
	 * Logger for handling errors in the DAO layer.
	 */
	private static final Logger LOGGER = Logger.getLogger(AdministratorService.class.getName());

	/**
	 * To construct this service class, the caller must supply instances of each DAO
	 * it uses.
	 *
	 * @param branchDao    the branch DAO
	 * @param bookDao      the book DAO
	 * @param authorDao    the author DAO
	 * @param publisherDao the publisher DAO
	 * @param loansDao     the loans DAO
	 */
	public AdministratorServiceImpl(final LibraryBranchDao branchDao,
			final BookDao bookDao, final AuthorDao authorDao,
			final PublisherDao publisherDao, final BookLoansDao loansDao,
			final BorrowerDao borrowerDao) {
		this.branchDao = branchDao;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
		this.publisherDao = publisherDao;
		this.loansDao = loansDao;
		this.borrowerDao = borrowerDao;
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
	public Book createBook(final String title, final Author author,
			final Publisher publisher) throws TransactionException {
		try {
			return bookDao.create(title, author, publisher);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a book", except);
			// TODO: abort current transaction
			throw new InsertException("Creating a book failed"); // TODO: Add constructor taking cause to all custom exceptions
		}
	}

	@Override
	public void updateBook(final Book book) throws TransactionException {
		try {
			bookDao.update(book);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a book", except);
			// TODO: abort current transaction
			throw new UpdateException("Updating book record failed");
		}
	}

	@Override
	public void deleteBook(final Book book) throws TransactionException {
		try {
			bookDao.delete(book);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a book record", except);
			// TODO: abort current transaction
			throw new DeleteException("Removing book record failed");
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
	public Author createAuthor(final String name) throws TransactionException {
		try {
			return authorDao.create(name);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating an author", except);
			// TODO: abort current transaction
			throw new InsertException("Creating an author failed");
		}

	}

	@Override
	public void updateAuthor(final Author author) throws TransactionException {
		try {
			authorDao.update(author);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating an author", except);
			// TODO: abort current transaction
			throw new UpdateException("Updating author record failed");
		}
	}

	@Override
	public void deleteAuthor(final Author author) throws TransactionException {
		try {
			authorDao.delete(author);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing an author record", except);
			// TODO: abort current transaction
			throw new DeleteException("Removing author record failed");
		}
	}

	@Override
	public List<Author> getAllAuthors() throws TransactionException {
		try {
			return authorDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting authors", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting author records failed", except);
		}
	}

	@Override
	public Publisher createPublisher(final String name) throws TransactionException {
		// TODO: Just delegate to this.createPublisher(name, "", "");
		try {
			return publisherDao.create(name, "", "");
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a publisher", except);
			// TODO: abort current transaction
			throw new InsertException("Creating a publisher failed");
		}
	}

	@Override
	public Publisher createPublisher(final String name, final String address,
			final String phone) throws TransactionException {
		try {
			return publisherDao.create(name, address, phone);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a publisher", except);
			// TODO: abort current transaction
			throw new InsertException("Creating a publisher failed");
		}
	}

	@Override
	public void updatePublisher(final Publisher publisher) throws TransactionException {
		try {
			publisherDao.update(publisher);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a publisher", except);
			// TODO: abort current transaction
			throw new UpdateException("Updating publisher record failed");
		}
	}

	@Override
	public void deletePublisher(final Publisher publisher) throws TransactionException {
		try {
			publisherDao.delete(publisher);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a publisher record", except);
			// TODO: abort current transaction
			throw new DeleteException("Removing publisher record failed");
		}
	}

	@Override
	public List<Publisher> getAllPublishers() throws TransactionException {
		try {
			return publisherDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting publishers", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting publisher records failed", except);
		}
	}

	@Override
	public Branch createBranch(final String name, final String address) throws TransactionException {
		try {
			return branchDao.create(name, address);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a branch", except);
			// TODO: abort current transaction
			throw new InsertException("Creating a branch failed");
		}
	}

	@Override
	public void deleteBranch(final Branch branch) throws TransactionException {
		try {
			branchDao.delete(branch);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a branch record", except);
			// TODO: abort current transaction
			throw new DeleteException("Removing branch record failed");
		}
	}

	@Override
	public void updateBranch(final Branch branch) throws TransactionException {
		try {
			branchDao.update(branch);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a branch", except);
			// TODO: abort current transaction
			throw new UpdateException("Updating branch record failed");
		}
	}

	@Override
	public Borrower createBorrower(final String name, final String address,
			final String phone) throws TransactionException {
		try {
			return borrowerDao.create(name, address, phone);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a borrower", except);
			// TODO: abort current transaction
			throw new InsertException("Creating a borrower failed");
		}
	}

	@Override
	public void updateBorrower(final Borrower borrower) throws TransactionException {
		try {
			borrowerDao.update(borrower);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a borrower", except);
			// TODO: abort current transaction
			throw new UpdateException("Updating borrower record failed");
		}
	}

	@Override
	public void deleteBorrower(final Borrower borrower) throws TransactionException {
		try {
			borrowerDao.delete(borrower);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a borrower record", except);
			// TODO: abort current transaction
			throw new DeleteException("Removing borrower record failed");
		}
	}

	@Override
	public List<Borrower> getAllBorrowers() throws TransactionException {
		try {
			return borrowerDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting borrowers", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting borrower records failed", except);
		}
	}

	@Override
	public boolean overrideDueDateForLoan(final Book book, final Borrower borrower,
			final Branch branch, final LocalDate dueDate) throws TransactionException {
		final Optional<Loan> loan;
		try {
			loan = Optional.ofNullable(loansDao.get(book, borrower, branch));
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting loan record", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting loan record failed", except);
		}
		if (loan.isPresent()) {
			loan.get().setDueDate(dueDate);
			try {
				loansDao.update(loan.get());
			} catch (final SQLException except) {
				LOGGER.log(Level.SEVERE, "SQL error while updating a loan", except);
				// TODO: abort current transaction
				throw new UpdateException("Updating loan record failed");
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Loan> getAllLoans() throws TransactionException {
		try {
			return loansDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting loans", except);
			// TODO: abort current transaction
			throw new UnknownSQLException("Getting loan records failed", except);
		}
	}
}
