package com.lms.menu;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalInt;

import com.lms.customExceptions.TransactionException;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.service.BorrowerService;

/**
 * The text-based menu UI for borrowers.
 *
 * @author Jonathan Lovelace
 */
public final class BorrowerMenu {
	/**
	 * The service class we use to handle database interfacing for us.
	 */
	private final BorrowerService service;
	/**
	 * The helper we use for interacting with the user.
	 */
	private final MenuHelper mh;
	/**
	 * The clock to get "the current time" from.
	 */
	private final Clock clock;

	/**
	 * To initialize the menu, the caller must provide the service class and the
	 * menu I/O helper.
	 *
	 * @param service    the service class to interact with the database
	 * @param menuHelper the helper to interact with the user
	 */
	public BorrowerMenu(final BorrowerService service, final MenuHelper menuHelper,
			final Clock clock) {
		this.service = service;
		mh = menuHelper;
		this.clock = clock;
	}

	/**
	 * Ask the user to choose a branch to check out from and a book to check out,
	 * and create the loan.
	 */
	private void checkOutMenu(final Borrower borrower) {
		mh.println();
		final Optional<Optional<Branch>> chosenBranch;
		try {
			chosenBranch = mh.chooseFromList(service.getAllBranches(),
					"Choose branch to check out from:", "Chosen branch:",
					"Cancel check-out operation", "There are no library branches",
					"No such branch", Branch::getName);
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all branches from the database.");
			return;
		}
		final Branch branch;
		if (chosenBranch.isPresent()) {
			if (chosenBranch.get().isPresent()) {
				branch = chosenBranch.get().get();
			} else { // "Cancel" chosen
				return;
			}
		} else { // no branches or input out of range
			return;
		}
		mh.println();
		// TODO: Filter out books where all copies are currently out
		final Optional<Optional<Book>> chosenBook;
		try {
			chosenBook = mh.chooseFromList(
					new ArrayList<>(service.getAllBranchCopies(branch).keySet()),
					"Choose book to check out:", "Chosen book:",
					"Cancel check-out operation", "No books at that branch",
					"No such book",
					book -> String.format("%s by %s", book.getTitle(),
							Optional.ofNullable(book.getAuthor()).map(Author::getName)
									.orElse("an unknown author")));
		} catch (final TransactionException except) {
			mh.println(
					"An error occurred while getting book copies in that branch from the database.");
			return;
		}
		final Book book;
		if (chosenBook.isPresent()) {
			if (chosenBook.get().isPresent()) {
				book = chosenBook.get().get();
			} else { // "Cancel" chosen
				return;
			}
		} else { // no books at that branch or input out of range
			return;
		}
		try {
			service.borrowBook(borrower, book, branch, LocalDateTime.now(clock),
					LocalDate.now(clock).plusWeeks(1));
			service.commit();
		} catch (final TransactionException except) {
			// TODO: Handle less severely if row already exists for that key
			mh.println("An error occurred while creating the loan record.");
		}
	}

	/**
	 * Ask the user to choose a book he or she has checked out and return it.
	 */
	private void returnBookMenu(final Borrower borrower) {
		final Optional<Optional<Loan>> chosenLoan;
		try {
			chosenLoan = mh.chooseFromList(service.getAllBorrowedBooks(borrower),
					"Your outstanding loans:", "Chosen book:", "Cancel return operation",
					"You have no outstanding loans", "No such book",
					loan -> String.format("%s by %s from %s", loan.getBook().getTitle(),
							Optional.ofNullable(loan.getBook().getAuthor())
									.map(Author::getName).orElse("an unknown author"),
							loan.getBranch().getName()));
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting your outstanding loans.");
			return;
		}
		final Loan loan;
		if (chosenLoan.isPresent()) {
			if (chosenLoan.get().isPresent()) {
				loan = chosenLoan.get().get();
			} else { // "Cancel" chosen
				return;
			}
		} else { // no loans or input out of range
			return;
		}
		try {
			service.returnBook(borrower, loan.getBook(), loan.getBranch(),
					loan.getDueDate());
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while processing your return.");
			mh.println("Your return has not been recorded.");
		}
	}

	/**
	 * Ask the borrower for his or her card number, then present the menu and handle
	 * the user's choices.
	 */
	public void menu() {
		final OptionalInt cardNo = mh.getNumber("Please enter card number:",
				"Card number must be a number.");
		final Borrower borrower;
		if (cardNo.isPresent()) {
			try {
				borrower = service.getBorrower(cardNo.getAsInt());
			} catch (final TransactionException except) {
				mh.println(
						"An error occurred while finding records for that card number.");
				return;
			}
		} else {
			// already told the user it must be numeric
			return;
		}
		if (borrower == null) {
			mh.println("There is no valid library card with that number");
			return;
		}
		while (true) {
			mh.println();
			mh.println("Would you like to");
			mh.println("0) Quit to previous menu");
			mh.println("1) Check out a book");
			mh.println("2) Return a book");
			switch (mh.getString("Chosen action:")) {
			case "0":
				return;
			case "1":
				checkOutMenu(borrower);
				break;
			case "2":
				returnBookMenu(borrower);
				break;
			default:
				mh.println("Unknown action.");
				break;
			}
		}
	}
}
