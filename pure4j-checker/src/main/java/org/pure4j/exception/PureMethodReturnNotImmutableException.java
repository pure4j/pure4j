package org.pure4j.exception;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodReturnNotImmutableException extends Pure4JException {

	public PureMethodReturnNotImmutableException(PureMethod pureMethod, int line) {
		super("method can't return non-immutable type on line: "+line+"\n"+pureMethod);
	}

}
