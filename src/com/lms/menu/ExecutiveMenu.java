package com.lms.menu;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.TransactionException;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.service.AdministratorService;

/**
 * The text-based menu UI for administrators managing branch and borrower
 * accounts.
 *
 * @author Jonathan Lovelace
 */
public final class ExecutiveMenu {
	/**
	 * The service class we use to handle database interfacing for us.
	 */
	private final AdministratorService service;
	/**
	 * The helper we use for interacting with the user.
	 */
	private final MenuHelper mh;
	/**
	 * A logger to record unexpected errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(ExecutiveMenu.class.getName());

	/**
	 * To initialize the menu, the caller must provide the service class and the
	 * menu I/O helper.
	 *
	 * @param service    the service class to do the actions the user calls for
	 * @param menuHelper the helper to interact with the user
	 */
	public ExecutiveMenu(final AdministratorService service,
			final MenuHelper menuHelper) {
		this.service = service;
		mh = menuHelper;
	}

	private void addBranch() {
		// TODO: Check if branch with that name or at that address already exists
		final String name = mh.getString("Name of new branch:");
		final String address = mh.getString("Address of new branch:");
		try {
			service.createBranch(name, address);
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while creating the new branch record.");
		}
	}

	private void updateBranch() {
		final Optional<Optional<Branch>> chosenBranch;
		try {
			chosenBranch = mh.chooseFromList(service.getAllBranches(),
					"Choose branch to update:", "Chosen branch:", "Quit to previous menu",
					"There are no library branches", "No such branch", Branch::getName);
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all branches from the database.");
			return;
		}
		final Branch branch;
		if (chosenBranch.isPresent() && chosenBranch.get().isPresent()) {
			branch = chosenBranch.get().get();
		} else {
			return;
		}
		mh.println("Details of chosen branch:");
		mh.printf("Name:\t%s%n", branch.getName());
		mh.printf("Address:\t%s%n", branch.getAddress());
		mh.println("New details (blank to keep existing data):");
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
			mh.println(
					"An error occurred while trying to write your changes to the database.");
		}
	}

	private void deleteBranch() {
		final Optional<Optional<Branch>> chosenBranch;
		try {
			chosenBranch = mh.chooseFromList(service.getAllBranches(),
					"Choose branch to remove, with all copies in and loans from it:",
					"Chosen branch:", "Quit to previous menu",
					"There are no library branches", "No such branch", Branch::getName);
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all branches from the database.");
			return;
		}
		if (chosenBranch.isPresent() && chosenBranch.get().isPresent()) {
			try {
				service.deleteBranch(chosenBranch.get().get());
				service.commit();
			} catch (final TransactionException except) {
				mh.println("An error occurred while removing the branch record.");
			}
		} else {
			return;
		}
	}

	private void addBorrower() {
		try {
			service.createBorrower(mh.getString("Name of new borrower:\t"),
					mh.getString("Address of new borrower:\t"),
					mh.getString("Phone # of new borrower:\t"));
			service.commit();
		} catch (final TransactionException except) {
			mh.println("An error occurred while creating the new borrower record.");
		}
	}

	private void updateBorrower() {
		final Optional<Optional<Borrower>> chosenBorrower;
		try {
			chosenBorrower = mh.chooseFromList(service.getAllBorrowers(),
					"Choose borrower to update:", "Chosen borrower:",
					"Quit to previous menu", "There are no borrowers", "No such borrower",
					Borrower::getName);
		} catch (final TransactionException except) {
			mh.println(
					"An error occurred while getting all borrowers from the database.");
			return;
		}
		Borrower borrower;
		if (chosenBorrower.isPresent() && chosenBorrower.get().isPresent()) {
			borrower = chosenBorrower.get().get();
		} else {
			return;
		}
		mh.println("Current details of borrower:");
		mh.printf("Name:\t%s%n", borrower.getName());
		mh.printf("Address:\t%s%n", borrower.getAddress());
		mh.printf("Phone:\t%s%n", borrower.getPhone());
		mh.println("New details (blank to keep existing data):");
		final String name = mh.getString("Name:\t");
		if (!name.isEmpty()) {
			borrower.setName(name);
		}
		final String address = mh.getString("Address:\t");
		if (!address.isEmpty()) {
			borrower.setAddress(address);
		}
		final String phone = mh.getString("Phone:\t");
		if (!phone.isEmpty()) {
			borrower.setPhone(phone);
		}
		try {
			service.updateBorrower(borrower);
			service.commit();
		} catch (final TransactionException except) {
			mh.println(
					"An error occurred while trying to write your changes to the database.");
		}
	}

	private void deleteBorrower() {
		final Optional<Optional<Borrower>> chosenBorrower;
		try {
			chosenBorrower = mh.chooseFromList(service.getAllBorrowers(),
					"Choose borrower to remove:", "Chosen borrower:",
					"Quit to previous menu", "There are no borrowers", "No such borrower",
					Borrower::getName);
		} catch (final TransactionException except) {
			mh.println("An error occurred while getting all branches from the database.");
			return;
		}
		if (chosenBorrower.isPresent() && chosenBorrower.get().isPresent()) {
			try {
				service.deleteBorrower(chosenBorrower.get().get());
				service.commit();
			} catch (final TransactionException except) {
				mh.println("An error occurred while removing the borrower record.");
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
			mh.println("1) Add a new library branch");
			mh.println("2) Update details of a branch");
			mh.println("3) Delete a library branch");
			mh.println("4) Add a new borrower");
			mh.println("5) Update details of a borrower");
			mh.println("6) Remove a borrower");
			switch (mh.getString("Chosen action:")) {
			case "0":
				return;
			case "1":
				addBranch();
				break;
			case "2":
				updateBranch();
				break;
			case "3":
				deleteBranch();
				break;
			case "4":
				addBorrower();
				break;
			case "5":
				updateBorrower();
				break;
			case "6":
				deleteBorrower();
				break;
			default:
				mh.println("Unknown action");
				break;
			}
		}
	}

}
