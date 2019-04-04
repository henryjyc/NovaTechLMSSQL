package com.lms.customExceptions;

/**
 * An exception class to report the failure of an operation of a type other than
 * deletion, insertion, and update from the service layer to the application
 * layer.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@SuppressWarnings("serial")
public class UnknownSQLException extends TransactionException {
	/**
	 * To throw an instance of this exception class, the caller must supply the
	 * exception message and the underlying cause exception.
	 *
	 * @param errorMessage the exception message
	 */
	public UnknownSQLException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
}
