package org.pure4j.exception;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class MissingImmutableParameterCheckException extends Pure4JException {

	public MissingImmutableParameterCheckException(PureMethod pureMethod, int paramNo) {
		super("Pure interface has call to Pure4J.immutable, but this doesn't include parameter "+paramNo+"\n"+pureMethod);
	}

}
