package org.pure4j.exception;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class IncorrectPure4JImmutableCallException extends Pure4JException {

	public IncorrectPure4JImmutableCallException(PureMethod pureMethod) {
		super("Pure interface:      "+pureMethod+" has call to Pure4J.immutable, but it must be the first call in the method and only passed method parameters");
	}

}
