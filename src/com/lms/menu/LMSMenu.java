package com.lms.menu;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.service.AdministratorServiceImpl;
import com.lms.service.BorrowerServiceImpl;
import com.lms.service.LibrarianServiceImpl;

/**
 * A text-based main-menu UI for the library management system.
 *
 * @author Jonathan Lovelace
 */
public final class LMSMenu {
	/**
	 * The helper for interacting with the user.
	 */
	private final MenuHelper mh;
	/**
	 * Logger for logging unexpected errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(LMSMenu.class.getName());
	/**
	 * The clock to get "the current time" from.
	 */
	private final Clock clock;

	/**
	 * To initialize the menu, the caller must provide I/O streams and the database
	 * connection. Note that the caller is responsible for ensuring that all of
	 * these resources are properly closed after the menu goes out of scope.
	 *
	 * @param inStream     how to get input from the user
	 * @param outStream    how to send output to the user
	 * @param dbConnection how to connect to the database
	 */
	public LMSMenu(final Reader inStream, final Writer outStream, final Clock clock) {
		mh = new MenuHelper(new Scanner(inStream), new PrintWriter(outStream));
		this.clock = clock;
	}

	/**
	 * Show the topmost-level menu, solicit user input, and call the appropriate
	 * sub-menu.
	 */
	public void chooseRole() {
		mh.println("Welcome to the GCIT Library Management System!");
		mh.println();
		try {
			while (true) {
				mh.println("Please choose your role:");
				mh.println("1) Patron");
				mh.println("2) Librarian");
				mh.println("3) Administrator");
				switch (mh.getString("Role:")) {
				case "0":
					return;
				case "1":
					new BorrowerMenu(new BorrowerServiceImpl(), mh, clock).menu();
					continue;
				case "2":
					new LibrarianMenu(new LibrarianServiceImpl(), mh).menu();
					continue;
				case "3":
					new AdministratorMenu(new AdministratorServiceImpl(), mh).menu();
					continue;
				default:
					mh.println("Please select role, or type 0 to quit");
					break;
				}
			}
		} catch (final SQLException except) {
			LOGGER.log(Level.SEVERE, "SQL error initializing DB connection", except);
			mh.println(
					"An unrecoverable error occurred while trying to connect to the database.");
		} catch (final IOException except) {
			LOGGER.log(Level.SEVERE,
					"I/O error reading DB configuration properties file", except);
			mh.println(
					"An unrecoverable error occurred while reading database configuration parameters.");
		}
	}
}
