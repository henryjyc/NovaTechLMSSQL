package com.lms.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.dao.AuthorDao;
import com.lms.dao.AuthorDaoImpl;
import com.lms.dao.BookDao;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.BookLoansDao;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDao;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.DBConnectionFactory;
import com.lms.dao.LibraryBranchDao;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.dao.PublisherDao;
import com.lms.dao.PublisherDaoImpl;
import com.lms.exceptions.DeleteException;
import com.lms.exceptions.InsertException;
import com.lms.exceptions.TransactionException;
import com.lms.exceptions.UnknownSQLException;
import com.lms.exceptions.UpdateException;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.model.Publisher;
import com.lms.util.ThrowingRunnable;

/**
 * An implementation of the service class for administrative UIs.
 *
 * <p>TODO: Split interface into "cataloger" and "executive" services.
 *
 * @author Jonathan Lovelace
 *
 */
public final class AdministratorServiceImpl implements AdministratorService {
	/**
	 * Extracted constant message to log when a rollback fails.
	 */
	private static final String ROLLBACK_FAILED = "Further error while rolling back transaction";
	/**
	 * DAO to access the library-branch table.
	 */
	private final LibraryBranchDao branchDao;
	/**
	 * DAO to access the book table.
	 */
	private final BookDao bookDao;
	/**
	 * DAO to access the author table.
	 */
	private final AuthorDao authorDao;
	/**
	 * DAO to access the publisher table.
	 */
	private final PublisherDao publisherDao;
	/**
	 * DAO to access the borrower table.
	 */
	private final BorrowerDao borrowerDao;
	/**
	 * DAO to access the loans table.
	 */
	private final BookLoansDao loansDao;
	/**
	 * Logger for handling errors in the DAO layer.
	 */
	private static final Logger LOGGER = Logger.getLogger(AdministratorService.class.getName());
	/**
	 * Method to use to commit a transaction, if the DAO backend supports transactions.
	 */
	private final ThrowingRunnable<SQLException> commitHandle;
	/**
	 * Method to use to roll back a transaction, if the DAO backend supports transactions.
	 */
	private final ThrowingRunnable<SQLException> rollbackHandle;

	/**
	 * To construct this service class using this constructor, the caller must
	 * supply instances of each DAO it uses and method references to commit and roll
	 * back transactions.
	 *
	 * @param branchDao    the branch DAO
	 * @param bookDao      the book DAO
	 * @param authorDao    the author DAO
	 * @param publisherDao the publisher DAO
	 * @param loansDao     the loans DAO
	 * @param commit       the method handle to commit a transaction, if the backend
	 *                     supports that
	 * @param rollback     the method handle to roll back a transaction, if the
	 *                     backend supports that
	 */
	public AdministratorServiceImpl(final LibraryBranchDao branchDao,
			final BookDao bookDao, final AuthorDao authorDao,
			final PublisherDao publisherDao, final BookLoansDao loansDao,
			final BorrowerDao borrowerDao,
			final ThrowingRunnable<SQLException> commit,
			final ThrowingRunnable<SQLException> rollback) {
		this.branchDao = branchDao;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
		this.publisherDao = publisherDao;
		this.loansDao = loansDao;
		this.borrowerDao = borrowerDao;
		commitHandle = commit;
		rollbackHandle = rollback;
	}

	/**
	 * To construct this service class using this constructor, the caller must
	 * merely supply a connection to the database.
	 * @param db the connection to the database
	 * @throws SQLException on error setting up DAOs.
	 */
	public AdministratorServiceImpl(final Connection db) throws SQLException {
		this(new LibraryBranchDaoImpl(db), new BookDaoImpl(db),
				new AuthorDaoImpl(db), new PublisherDaoImpl(db),
				new BookLoansDaoImpl(db), new BorrowerDaoImpl(db), db::commit,
				db::rollback);
	}

	/**
	 * Constructor that uses the default DB connection factory to supply the
	 * database connection and uses the default DAO implementations.
	 * @throws IOException on I/O error reading DB configuration
	 * @throws SQLException on error setting up the database or DAOs
	 */
	public AdministratorServiceImpl() throws SQLException, IOException {
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
	public Book createBook(final String title, final Author author,
			final Publisher publisher) throws TransactionException {
		try {
			return bookDao.create(title, author, publisher);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a book", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new InsertException("Creating a book failed", except);
		}
	}

	@Override
	public void updateBook(final Book book) throws TransactionException {
		try {
			bookDao.update(book);
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
	public void deleteBook(final Book book) throws TransactionException {
		try {
			bookDao.delete(book);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a book record", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new DeleteException("Removing book record failed", except);
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
	public Author createAuthor(final String name) throws TransactionException {
		try {
			return authorDao.create(name);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating an author", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new InsertException("Creating an author failed", except);
		}

	}

	@Override
	public void updateAuthor(final Author author) throws TransactionException {
		try {
			authorDao.update(author);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating an author", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new UpdateException("Updating author record failed", except);
		}
	}

	@Override
	public void deleteAuthor(final Author author) throws TransactionException {
		try {
			authorDao.delete(author);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing an author record", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new DeleteException("Removing author record failed", except);
		}
	}

	@Override
	public List<Author> getAllAuthors() throws TransactionException {
		try {
			return authorDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting authors", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Getting author records failed", except);
		}
	}

	@Override
	public Publisher createPublisher(final String name) throws TransactionException {
		return createPublisher(name, "", "");
	}

	@Override
	public Publisher createPublisher(final String name, final String address,
			final String phone) throws TransactionException {
		try {
			return publisherDao.create(name, address, phone);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a publisher", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new InsertException("Creating a publisher failed", except);
		}
	}

	@Override
	public void updatePublisher(final Publisher publisher) throws TransactionException {
		try {
			publisherDao.update(publisher);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a publisher", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new UpdateException("Updating publisher record failed", except);
		}
	}

	@Override
	public void deletePublisher(final Publisher publisher) throws TransactionException {
		try {
			publisherDao.delete(publisher);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a publisher record", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new DeleteException("Removing publisher record failed", except);
		}
	}

	@Override
	public List<Publisher> getAllPublishers() throws TransactionException {
		try {
			return publisherDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting publishers", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Getting publisher records failed", except);
		}
	}

	@Override
	public Branch createBranch(final String name, final String address) throws TransactionException {
		try {
			return branchDao.create(name, address);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a branch", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new InsertException("Creating a branch failed", except);
		}
	}

	@Override
	public void deleteBranch(final Branch branch) throws TransactionException {
		try {
			branchDao.delete(branch);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a branch record", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new DeleteException("Removing branch record failed", except);
		}
	}

	@Override
	public void updateBranch(final Branch branch) throws TransactionException {
		try {
			branchDao.update(branch);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a branch", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new UpdateException("Updating branch record failed", except);
		}
	}

	@Override
	public Borrower createBorrower(final String name, final String address,
			final String phone) throws TransactionException {
		try {
			return borrowerDao.create(name, address, phone);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a borrower", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);// TODO: add as suppressed exception to next-thrown
			}
			throw new InsertException("Creating a borrower failed", except);
		}
	}

	@Override
	public void updateBorrower(final Borrower borrower) throws TransactionException {
		try {
			borrowerDao.update(borrower);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while updating a borrower", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new UpdateException("Updating borrower record failed", except);
		}
	}

	@Override
	public void deleteBorrower(final Borrower borrower) throws TransactionException {
		try {
			borrowerDao.delete(borrower);
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while removing a borrower record", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
			}
			throw new DeleteException("Removing borrower record failed", except);
		}
	}

	@Override
	public List<Borrower> getAllBorrowers() throws TransactionException {
		try {
			return borrowerDao.getAll();
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting borrowers", except);
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
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
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Getting loan record failed", except);
		}
		if (loan.isPresent()) {
			loan.get().setDueDate(dueDate);
			try {
				loansDao.update(loan.get());
			} catch (final SQLException except) {
				LOGGER.log(Level.SEVERE, "SQL error while updating a loan", except);
				try {
					rollbackHandle.run();
				} catch (final SQLException inner) {
					LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner); // TODO: add as suppressed exception to next-thrown
				}
				throw new UpdateException("Updating loan record failed", except);
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
			try {
				rollbackHandle.run();
			} catch (final SQLException inner) {
				LOGGER.log(Level.SEVERE, ROLLBACK_FAILED, inner);
			}
			throw new UnknownSQLException("Getting loan records failed", except);
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
