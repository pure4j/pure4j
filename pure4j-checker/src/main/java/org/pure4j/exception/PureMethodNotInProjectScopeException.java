package org.pure4j.exception;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodNotInProjectScopeException extends Pure4JException {

	public PureMethodNotInProjectScopeException(PureMethod pm) {
		super("Pure implementation is expected to be a pure method, but is not in scope and isn't marked as such.\n"+pm);
	}

}
