package com.lms.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.lms.customExceptions.TransactionException;
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
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Book createBook(final String title, final Author author,
			final Publisher publisher) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void updateBook(final Book book) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void deleteBook(final Book book) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Book> getAllBooks() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Author createAuthor(final String name) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void updateAuthor(final Author author) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void deleteAuthor(final Author author) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Author> getAllAuthors() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Publisher createPublisher(final String name) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Publisher createPublisher(final String name, final String address,
			final String phone) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void updatePublisher(final Publisher publisher) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void deletePublisher(final Publisher publisher) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Publisher> getAllPublishers() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Branch createBranch(final String name, final String address) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void deleteBranch(final Branch branch) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void updateBranch(final Branch branch) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Borrower createBorrower(final String name, final String address,
			final String phone) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void updateBorrower(final Borrower borrower) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void deleteBorrower(final Borrower borrower) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Borrower> getAllBorrowers() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public boolean overrideDueDateForLoan(final Book book, final Borrower borrower,
			final Branch branch, final LocalDate dueDate) throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<Loan> getAllLoans() throws TransactionException {
		throw new IllegalStateException("Not implemented");
	}
}
