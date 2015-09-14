package org.pure4j.exception;

public class Pure4JException extends RuntimeException {

	private static final long serialVersionUID = -2909650979311615529L;

	public Pure4JException(String message) {
		super(message);
	}
	
	public Pure4JException(String message, Throwable t) {
		super(message, t);
	}
}
