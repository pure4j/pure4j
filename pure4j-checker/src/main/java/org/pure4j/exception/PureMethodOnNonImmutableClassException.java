package org.pure4j.exception;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodOnNonImmutableClassException extends Pure4JException {

	public PureMethodOnNonImmutableClassException(PureMethod pureMethod, Class<?> c) {
		super("Non-static pure method on class which is not immutable: "+c+".\n"+
				"- Try making the method 'static' \n"+pureMethod);
	}

}
