package org.pure4j.immutable;

import org.pure4j.Pure4J;

/**
 * Thrown at runtime by {@link Pure4J} if a library is passed a non-immutable implementation.
 * 
 * @author robmoffat
 *
 */
public class ClassNotImmutableException extends RuntimeException {

	public ClassNotImmutableException(String message) {
		super(message);
	}

}
