package com.lms.exceptions;

/**
 * An exception class to report the failure of an 'update' operation from the
 * service layer to the application layer.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@SuppressWarnings("serial")
public class UpdateException extends TransactionException {
	/**
	 * To throw an instance of this exception class, the caller must supply the
	 * exception message.
	 *
	 * @param errorMessage the exception message
	 */
	public UpdateException(final String errorMessage) {
		super(errorMessage);
	}
}
