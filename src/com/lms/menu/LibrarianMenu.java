package com.lms.menu;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import com.lms.exceptions.TransactionException;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Branch;
import com.lms.service.LibrarianService;

/**
 * The text-based menu UI for librarians.
 *
 * @author Jonathan Lovelace
 */
public final class LibrarianMenu {
	/**
	 * The service class we use to handle database interfacing for us.
	 */
	private final LibrarianService service;
	/**
	 * The helper we use for interacting with the user.
	 */
	private final MenuHelper mh;
	/**
	 * To initialize the menu, the caller must provide the service instance and the
	 * menu helper.
	 *
	 * @param service    the service object for interacting with the database
	 * @param menuHelper the helper for interacting with the user
	 */
	public LibrarianMenu(final LibrarianService service, final MenuHelper menuHelper) {
		this.service = service;
		mh = menuHelper;
	}

	/**
	 * Ask the user for new name and address for the given branch.
	 *
	 * @param branch the branch to update
	 */
	private void updateLibraryDetails(final Branch branch) {
		mh.printf("Updating details of branch \"%s\" (ID #%d). Existing details:%n",
				branch.getName(), branch.getId());
		mh.print("Name:\t");
		mh.println(branch.getName());
		mh.print("Address:\t");
		mh.println(branch.getAddress());
		mh.println("Enter new details (blank to keep existing data):");
		final String name = mh.getString("Name:\t");
		if (!name.isEmpty()) {
			branch.setName(name);
		}
		final String address = mh.getString("Address:\t");
		if (!address.isEmpty()) {
			branch.setAddress(address);
		}
		try {
			service.updateBranch(branch);
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while writing your changes to the database.");
		}
	}

	/**
	 * Allow the user to change the number of copies of a book held by a branch.
	 *
	 * @param branch the branch in question
	 */
	private void changeHeldCopies(final Branch branch) {
		final Optional<Optional<Book>> chosenBook;
		try {
			chosenBook = mh.chooseFromList(service.getAllBooks(),
					"Choose book to change number of copies:", "Chosen book:",
					"Return to previous menu", "No books found", "No such book",
					book -> String.format("%s by %s", book.getTitle(),
							Optional.ofNullable(book.getAuthor()).map(Author::getName)
									.orElse("an unknown author")));
		} catch (final TransactionException except) {
			mh.println(
					"An error occurred while getting the list of books from the database.");
			return;
		}
		Book book;
		if (chosenBook.isPresent() && chosenBook.get().isPresent()) {
			book = chosenBook.get().get();
		} else {
			return;
		}
		mh.print("Existing number of copies: ");
		Map<Book, Integer> branchHoldings;
		try {
			branchHoldings = service.getAllCopies().get(branch);
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting that branch's holdings.");
			return;
		}
		if (branchHoldings == null) {
			mh.println("0");
		} else if (branchHoldings.containsKey(book)) {
			mh.println(Integer.toString(branchHoldings.get(book)));
		} else {
			mh.println("0");
		}
		final OptionalInt copies = mh.getNumber("New number of copies:",
				"That was not a number.");
		if (copies.isPresent()) {
			try {
				service.setBranchCopies(branch, book, copies.getAsInt());
				service.commit();
			} catch (final TransactionException except) {
				mh.println(
						"An error occurred while writing your changes to the database.");
			}
		}
	}

	/**
	 * Ask the user what operation to take on the given branch, then dispatch to the
	 * appropriate method.
	 */
	private void manageBranch(final Branch branch) {
		while (true) {
			mh.println("Would you like to");
			mh.println("0) Return to previous menu");
			mh.println("1) Update library details");
			mh.println("2) Change number of copies held of a book");
			switch (mh.getString("Chosen action:")) {
			case "0":
				return;
			case "1":
				updateLibraryDetails(branch);
				continue;
			case "2":
				changeHeldCopies(branch);
				continue;
			default:
				mh.println("Unknown action");
				break;
			}
		}
	}

	/**
	 * Ask the user which branch to manage, then for actions to take within that
	 * branch.
	 */
	public void menu() {
		while (true) {
			Optional<Optional<Branch>> chosenBranch;
			try {
				chosenBranch = mh.chooseFromList(service.getAllBranches(),
						"Choose branch to manage:", "Chosen branch:",
						"Quit to previous menu", "No branches found", "No such branch",
						Branch::getName);
			} catch (final TransactionException except) {
				mh.println(
						"An error occurred in getting the list of branches from the database.");
				return;
			}
			if (chosenBranch.isPresent() && chosenBranch.get().isPresent()) {
				manageBranch(chosenBranch.get().get());
			} else {
				break;
			}
		}
	}
}
