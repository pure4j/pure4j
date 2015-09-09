package org.pure4j.immutable;

/**
 * Thrown at runtime by {@link LibraryHelp} if a library is passed a non-immutable implementation.
 * 
 * @author robmoffat
 *
 */
public class ClassNotImmutableException extends RuntimeException {

	public ClassNotImmutableException(String message) {
		super(message);
	}

}
