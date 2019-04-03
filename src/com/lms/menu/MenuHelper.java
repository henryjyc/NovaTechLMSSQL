package com.lms.menu;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.function.Function;

/**
 * A class providing helper methods used by menu classes. Some methods duplicate
 * the most commonly used methods in the {@link PrintWriter} interface, while
 * others ease the task of prompting the user and getting input.
 *
 * @author Jonathan Lovelace
 *
 */
public final class MenuHelper {
	/**
	 * The means of getting input from the user.
	 */
	private final Scanner stdin;
	/**
	 * The means of sending output to the user.
	 */
	private final PrintWriter stdout;
	/**
	 * To construct this object, the caller must provide the input and output
	 * streams it should use. The caller also retains responsibility for ensuring
	 * that they are closed after this instance goes out of scope.
	 */
	public MenuHelper(final Scanner inStream, final PrintWriter outStream) {
		stdin = inStream;
		stdout = outStream;
	}
	public void print(final String string) {
		stdout.print(string);
	}
	public void println(final String string) {
		stdout.println(string);
	}
	public void println() {
		stdout.println();
	}
	public void printf(final String format, final Object... args) {
		stdout.printf(format, args);
	}

	/**
	 * Print the given prompt string, plus a space if it doesn't end in whitespace,
	 * and return what the user then inputs (trimmed).
	 *
	 * @param prompt text to prompt the user with
	 * @return the user's input
	 */
	public String getString(final String prompt) {
		print(prompt);
		if (!prompt.isEmpty()
				&& !Character.isWhitespace(prompt.charAt(prompt.length() - 1))) {
			stdout.print(' ');
		}
		stdout.flush();
		return stdin.nextLine().trim();
	}

	/**
	 * Prompt with the given prompt string; if the user's input is numeric, parse
	 * and return it, and otherwise return nothing.
	 *
	 * @param prompt text to prompt the user with
	 * @return the user's input, if numeric.
	 */
	public OptionalInt getNumber(final String prompt) {
		return getNumber(prompt, "That's not a number.");
	}
	/**
	 * Prompt with the given prompt string; if the user's input is numeric, parse
	 * and return it, and otherwise return nothing.
	 *
	 * @param prompt text to prompt the user with
	 * @param nonNumeric what to print if the input is not numeric.
	 * @return the user's input, if numeric.
	 */
	public OptionalInt getNumber(final String prompt, final String nonNumeric) {
		final String input = getString(prompt);
		try {
			return OptionalInt.of(Integer.parseInt(input));
		} catch (final NumberFormatException except) {
			stdout.println(nonNumeric);
			return OptionalInt.empty();
		}
	}

	/**
	 * Ask the user to choose an item from the list, with a special "item 0".
	 *
	 * @param <T>       the type of items i the list
	 * @param list      the list for the user to choose from
	 * @param header    what to print above the list
	 * @param prompt    how to prompt the user after printing the list
	 * @param first     what to print as the first item
	 * @param none      what to print if there are no items in the list
	 * @param invalid   what to print if a number is input that is negative or too
	 *                  large
	 * @param stringify a function to get what to print from each item in the list
	 * @return the empty optional if the list is empty or input is out of range, an
	 *         optional containing the empty optional if the first item is chosen,
	 *         and an optional containing an optional containing the user's choice
	 *         otherwise.
	 */
	public <T> Optional<Optional<T>> chooseFromList(final List<T> list,
			final String header, final String prompt, final String first,
			final String none, final String invalid,
			final Function<T, String> stringify) {
		if (list.isEmpty()) {
			println(none);
			return Optional.empty();
		}
		println(header);
		print("0) ");
		println(first);
		for (int i = 0; i < list.size(); i++) {
			printf("%d) %s%n", i + 1, stringify.apply(list.get(i)));
		}
		final OptionalInt choice = getNumber(prompt, "That's not a number.");
		if (choice.isPresent()) {
			final int index = choice.getAsInt();
			if (index == 0) {
				return Optional.of(Optional.empty());
			} else if (index <= list.size()) {
				return Optional.of(Optional.of(list.get(index - 1)));
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Ask the user to choose an item from the list, with a special "item 0" and
	 * using toString to convert items to Strings to print.
	 *
	 * @param <T>       the type of items i the list
	 * @param list      the list for the user to choose from
	 * @param header    what to print above the list
	 * @param prompt    how to prompt the user after printing the list
	 * @param first     what to print as the first item
	 * @param none      what to print if there are no items in the list
	 * @param invalid   what to print if a number is input that is negative or too
	 *                  large
	 * @return the empty optional if the list is empty or input is out of range, an
	 *         optional containing the empty optional if the first item is chosen,
	 *         and an optional containing an optional containing the user's choice
	 *         otherwise.
	 */
	public <T> Optional<Optional<T>> chooseFromList(final List<T> list,
			final String header, final String prompt, final String first,
			final String none, final String invalid) {
		return chooseFromList(list, header, prompt, first, none, invalid,
				Objects::toString);
	}
	/**
	 * Ask the user a yes-or-no question, and loop until the user answers it properly.
	 * @param prompt what to prompt the user with
	 * @return true if "yes", false if "no".
	 */
	public boolean inputBoolean(final String prompt) {
		while (true) {
			switch (getString(prompt)) {
			case "y": case "yes":
				return true;
			case "n": case "no":
				return false;
			default:
				print("Please enter 'y', 'yes', 'n', or 'no'. ");
				break;
			}
		}
	}
}
