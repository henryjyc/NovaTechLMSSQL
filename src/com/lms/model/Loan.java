package com.lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * An object representing the loan of a book. Unlike every other model class,
 * this has no ID field; instead, its identity consists in the intersection of
 * the book, branch, and borrower.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public class Loan {
	/**
	 * The book that was borrowed.
	 */
	private final Book book;
	/**
	 * The borrower who checked out the book.
	 */
	private final Borrower borrower;
	/**
	 * The branch from which the book was checked out.
	 */
	private final Branch branch;
	/**
	 * When the book was checked out.
	 */
	private LocalDateTime dateOut;
	/**
	 * When the book is due.
	 */
	private LocalDate dueDate;

	/**
	 * To construct a Loan object, the caller must supply the book, borrower, and
	 * branch that identify the loan in question and the dates the book was checked
	 * out and is due.
	 *
	 * @param book     the book that was checked out
	 * @param borrower the borrower who checked it out
	 * @param branch   the branch from which it was borrowed
	 * @param dateOut  when it was checked out
	 * @param dueDate  when it is due
	 */
	public Loan(final Book book, final Borrower borrower, final Branch branch,
			final LocalDateTime dateOut, final LocalDate dueDate) {
		this.book = book;
		this.borrower = borrower;
		this.branch = branch;
		this.dateOut = dateOut;
		this.dueDate = dueDate;
	}

	/**
	 * Get the book that is involved in this loan.
	 * @return the book that was checked out
	 */
	public Book getBook() {
		return book;
	}

	/**
	 * Get the borrower involved in this loan.
	 * @return the borrower who checked the book out.
	 */
	public Borrower getBorrower() {
		return borrower;
	}

	/**
	 * Get the branch involved in this loan.
	 * @return the branch from which the book was borrowed.
	 */
	public Branch getBranch() {
		return branch;
	}

	/**
	 * Get when the book was checked out.
	 * @return the date (and time) the book was checked out.
	 */
	public LocalDateTime getDateOut() {
		return dateOut;
	}

	/**
	 * Change when the book was checked out.
	 * @param dateOut the new checked-out date, which must not be null.
	 */
	public void setDateOut(final LocalDateTime dateOut) {
		this.dateOut = dateOut;
	}

	/**
	 * Get the book's due date.
	 * @return the date by which the book must be returned.
	 */
	public LocalDate getDueDate() {
		return dueDate;
	}

	/**
	 * Change the book's due date.
	 * @param dueDate the new due date, which must not be null.
	 */
	public void setDueDate(final LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * We use a combination of the hash codes of the book, borrower, and branch for
	 * this object's hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(book, borrower, branch);
	}

	/**
	 * An object to this one is equal iff it is a Loan involving an equal book,
	 * borrower, and branch and its date out and due date are equal.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Loan) {
			return Objects.equals(book, ((Loan) obj).getBook())
					&& Objects.equals(borrower, ((Loan) obj).getBorrower())
					&& Objects.equals(branch, ((Loan) obj).getBranch())
					&& Objects.equals(dateOut, ((Loan) obj).getDateOut())
					&& Objects.equals(dueDate, ((Loan) obj).getDueDate());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("Loan: %s by %s borrowed from %s by %s on %s, due %s",
				book.getTitle(),
				Optional.ofNullable(book.getAuthor()).map(Author::getName)
						.orElse("an unknown author"),
				branch.getName(), borrower.getName(),
				Objects.toString(dateOut, "an unknown date"),
				Objects.toString(dueDate, "never"));
	}
}
