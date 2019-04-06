package com.lms.util;

/**
 * A no-arg method that may throw an exception. This is intended to be used as
 * the type for method references.
 *
 * @author Jonathan Lovelace
 */
@FunctionalInterface
public interface ThrowingRunnable {
	void run() throws Exception;
}
