package com.lms.menu;

import java.util.List;
import java.util.Optional;

import com.lms.customExceptions.TransactionException;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Publisher;
import com.lms.service.AdministratorService;

/**
 * The text-based menu UI for administrators maintaining cataloging data.
 *
 * @author Jonathan Lovelace
 */
public final class CatalogerMenu {
	/**
	 * The service class we use to handle database interfacing for us.
	 */
	private final AdministratorService service;
	/**
	 * The helper we use for interacting with the user.
	 */
	private final MenuHelper mh;
	/**
	 * To initialize the menu, the caller must provide I/O streams, the database
	 * connection, and the service class. Note that the caller is responsible for
	 * ensuring that all of these resources are properly closed after the menu goes
	 * out of scope.
	 *
	 * @param inStream     how to get input from the user
	 * @param outStream    how to send output to the user
	 * @param dbConnection how to connect to the database
	 * @param service      the service class to do the actions the user calls for
	 * @param menuHelper   the helper class to interact with the user
	 */
	public CatalogerMenu(final AdministratorService service,
			final MenuHelper menuHelper) {
		this.service = service;
		mh = menuHelper;
	}

	private Author addAuthor() throws TransactionException {
		final String name = mh.getString("Name of new author:");
		final Optional<Author> existing = service.getAllAuthors().parallelStream()
				.filter(author -> name.equals(author.getName())).findAny();
		if (existing.isPresent()) {
			mh.printf("There is already an author named %s. ", name);
			if (!mh.inputBoolean("Add anyway? ")) {
				return existing.get();
			}
		}
		return service.createAuthor(name);
	}

	private Publisher addPublisher() throws TransactionException {
		final String name = mh.getString("Name of new publisher:");
		final Optional<Publisher> existing = service.getAllPublishers().parallelStream()
				.filter(publisher -> name.equals(publisher.getName())).findAny();
		if (existing.isPresent()) {
			mh.printf("There is already a publisher named %s. ", name);
			if (!mh.inputBoolean("Add anyway? ")) {
				return existing.get();
			}
		}
		final String address = mh.getString("Address of new publisher:");
		final String phone = mh.getString("Phone # of new publisher:");
		return service.createPublisher(name, address, phone);
	}

	private Optional<Author> chooseOrEnterAuthor(final String prompt) {
		final List<Author> authors;
		try {
			authors = service.getAllAuthors();
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all authors from the database");
			return Optional.empty();
		}
		final Optional<Optional<Author>> chosenAuthor = mh.chooseFromList(authors, prompt,
				"(Any other number to cancel.)\n" + prompt, "Add New", "No authors known",
				"No such author", Author::getName);
		if (chosenAuthor.isPresent()) {
			if (chosenAuthor.get().isPresent()) {
				return chosenAuthor.get();
			} else { // "Add new" chosen
				try {
					return Optional.of(addAuthor());
				} catch (final TransactionException except) {
					mh.println("An error occurred while trying to add the author.");
					return Optional.empty();
				}
			}
		} else if (authors.isEmpty()) {
			try {
				return Optional.of(addAuthor());
			} catch (final TransactionException except) {
				mh.println("An error occurred while trying to add the author.");
				return Optional.empty();
			}
		} else { // input out of range, i.e. cancel
			return Optional.empty();
		}
	}

	private Optional<Publisher> chooseOrEnterPublisher(final String prompt) {
		final List<Publisher> publishers;
		try {
			publishers = service.getAllPublishers();
		} catch (final TransactionException except) {
			mh.println(
					"An error occurred while getting all publishers from the database.");
			return Optional.empty();
		}
		final Optional<Optional<Publisher>> chosenPublisher = mh.chooseFromList(
				publishers, prompt, "(Any other number to cancel.)\n" + prompt, "Add New",
				"No publishers known", "No such publisher", Publisher::getName);
		if (chosenPublisher.isPresent()) {
			if (chosenPublisher.get().isPresent()) {
				return chosenPublisher.get();
			} else {
				try {
					return Optional.of(addPublisher());
				} catch (final TransactionException except) {
					mh.println("An error occurred while trying to add the publisher.");
					return Optional.empty();
				}
			}
		} else if (publishers.isEmpty()) {
			try {
				return Optional.of(addPublisher());
			} catch (final TransactionException except) {
				mh.println("An error occurred while trying to add the publisher.");
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	private void addBook() {
		final Optional<Author> author = chooseOrEnterAuthor("Author of new book:");
		if (!author.isPresent()) {
			return;
		}
		final Optional<Publisher> publisher = chooseOrEnterPublisher(
				"Publisher of new book:");
		if (!publisher.isPresent()) {
			return;
		}
		final String title = mh.getString("Title of new book:");
		try {
			service.createBook(title, author.get(), publisher.get());
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while trying to add the book");
		}
	}

	private void updateAuthor() {
		final Optional<Optional<Author>> chosenAuthor;
		try {
			chosenAuthor = mh.chooseFromList(service.getAllAuthors(), "Author to update:",
					"Author to update:", "Quit to previous menu", "No authors known",
					"No such author", Author::getName);
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all authors from the database.");
			return;
		}
		Author author;
		if (chosenAuthor.isPresent()) {
			if (chosenAuthor.get().isPresent()) {
				author = chosenAuthor.get().get();
			} else { // "Quit" chosen
				return;
			}
		} else { // no authors or input out of range
			return;
		}
		final String name = mh.getString("Author's new name (blank to leave unchanged):");
		if (!name.isEmpty()) {
			author.setName(name);
		}
		try {
			service.updateAuthor(author);
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while writing your changes to the database.");
		}
	}

	private void updatePublisher() {
		final Optional<Optional<Publisher>> chosenPublisher;
		try {
			chosenPublisher = mh.chooseFromList(service.getAllPublishers(),
					"Publisher to update:", "Publisher to update:",
					"Quit to previous menu", "No publishers known", "No such publisher",
					Publisher::getName);
		} catch (final TransactionException except) {
			mh.println(
					"An error occurred while getting all publishers from the database.");
			return;
		}
		final Publisher publisher;
		if (chosenPublisher.isPresent()) {
			if (chosenPublisher.get().isPresent()) {
				publisher = chosenPublisher.get().get();
			} else { // "Quit"
				return;
			}
		} else { // empty list or input out of range
			return;
		}
		mh.println("Enter publisher's new information (blank to leave field unchanged):");
		final String name = mh.getString("Name:");
		if (!name.isEmpty()) {
			publisher.setName(name);
		}
		final String address = mh.getString("Address:");
		if (!address.isEmpty()) {
			publisher.setAddress(address);
		}
		final String phone = mh.getString("Phone:");
		if (!phone.isEmpty()) {
			publisher.setPhone(phone);
		}
		try {
			service.updatePublisher(publisher);
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while writing your changes to the database.");
		}
	}

	private void updateBook() {
		final Optional<Optional<Book>> chosenBook;
		try {
			chosenBook = mh.chooseFromList(service.getAllBooks(), "Book to update:",
					"Book to update:", "Quit to previous menu", "No books known",
					"No such book",
					book -> String.format("%s by %s", book.getTitle(),
							Optional.ofNullable(book.getAuthor()).map(Author::getName)
									.orElse("an unknown author")));
		} catch (final TransactionException except) {
			mh.println("An error occurred while retrieving all books from the database.");
			return;
		}
		final Book book;
		if (chosenBook.isPresent() && chosenBook.get().isPresent()) {
			book = chosenBook.get().get();
		} else {
			return;
		}
		if (mh.inputBoolean("Change author of book?")) {
			final Optional<Author> author = chooseOrEnterAuthor("New author of book:");
			if (author.isPresent()) {
				book.setAuthor(author.get());
			}
		}
		mh.printf("Currently published by %s. ", Optional.ofNullable(book.getPublisher())
				.map(Publisher::getName).orElse("an unknown publisher"));
		if (mh.inputBoolean("Change publisher of book?")) {
			final Optional<Publisher> publisher = chooseOrEnterPublisher(
					"New publisher of book:");
			if (publisher.isPresent()) {
				book.setPublisher(publisher.get());
			}
		}
		final String title = mh.getString("New title (blank to leave unchanged):");
		if (!title.isEmpty()) {
			book.setTitle(title);
		}
		try {
			service.updateBook(book);
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while writing your changes to the database.");
		}
	}

	private void deleteBook() {
		final Optional<Optional<Book>> chosenBook;
		try {
			chosenBook = mh.chooseFromList(service.getAllBooks(),
					"Choose book to remove:", "Book to remove:", "Quit to previous menu",
					"No books found", "No such book",
					book -> String.format("%s by %s", book.getTitle(),
							Optional.ofNullable(book.getAuthor()).map(Author::getName)
									.orElse("an unknown author")));
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all books from the database.");
			return;
		}
		if (chosenBook.isPresent() && chosenBook.get().isPresent()) {
			try {
				service.deleteBook(chosenBook.get().get());
				service.commit();
			} catch (final TransactionException except) {
				mh.println("An error occurred while removing the book.");
			}
		}
	}

	private void deleteAuthor() {
		final Optional<Optional<Author>> chosenAuthor;
		try {
			chosenAuthor = mh.chooseFromList(service.getAllAuthors(),
					"Choose author to remove with all his/her books:",
					"Author to remove:", "Quit to previous menu", "No authors found",
					"No such author", Author::getName);
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all authors from the database");
			return;
		}
		if (chosenAuthor.isPresent() && chosenAuthor.get().isPresent()) {
			try {
				service.deleteAuthor(chosenAuthor.get().get());
				service.commit();
			} catch (final TransactionException except) {
				mh.println("An error occurred while removing the author.");
			}
		}
	}

	private void deletePublisher() {
		final Optional<Optional<Publisher>> chosenPublisher;
		try {
			chosenPublisher = mh.chooseFromList(service.getAllPublishers(),
					"Choose author to remove with all their books:",
					"Publisher to remove:", "Quit to previous menu",
					"No publishers found", "No such publisher", Publisher::getName);
		} catch (final TransactionException except) {
			mh.println(
					"An error occurred while getting all publishers from the database.");
			return;
		}
		if (chosenPublisher.isPresent() && chosenPublisher.get().isPresent()) {
			try {
				service.deletePublisher(chosenPublisher.get().get());
				service.commit();
			} catch (final TransactionException except) {
				mh.println("An error occurred while removing the publisher");
			}
		}
	}

	/**
	 * Present a list of options to the user and dispatch handling the user's choice
	 * to the appropriate method until the user selects "quit".
	 */
	public void menu() {
		while (true) {
			mh.println("Would you like to:");
			mh.println("0) Quit to the previous menu");
			mh.println("1) Add a new author");
			mh.println("2) Add a new publisher");
			mh.println("3) Add a new book");
			mh.println("4) Update an author's name");
			mh.println("5) Update details of a publisher");
			mh.println("6) Update details of a book");
			mh.println("7) Delete a book");
			mh.println("8) Delete an author");
			mh.println("9) Delete a publisher");
			switch (mh.getString("Chosen action:")) {
			case "0":
				return;
			case "1":
				try {
					addAuthor();
					service.commit();
				} catch (final TransactionException except) {
					mh.println("An error occurred while trying to add the author.");
					continue;
				}
				break;
			case "2":
				try {
					addPublisher();
					service.commit();
				} catch (final TransactionException except) {
					mh.println("An error occurred while trying to add the publisher.");
					continue;
				}
				break;
			case "3":
				addBook();
				break;
			case "4":
				updateAuthor();
				break;
			case "5":
				updatePublisher();
				break;
			case "6":
				updateBook();
				break;
			case "7":
				deleteBook();
				break;
			case "8":
				deleteAuthor();
				break;
			case "9":
				deletePublisher();
				break;
			default:
				mh.println("Unknown action.");
				break;
			}
		}
	}
}
