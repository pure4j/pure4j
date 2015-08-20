package org.pure4j.model;

public class Pure4JException extends RuntimeException {

	private static final long serialVersionUID = -2909650979311615529L;

	public Pure4JException() {
		super();
	}

	public Pure4JException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public Pure4JException(String message, Throwable cause) {
		super(message, cause);
	}

	public Pure4JException(String message) {
		super(message);
	}

	public Pure4JException(Throwable cause) {
		super(cause);
	}

	
}
