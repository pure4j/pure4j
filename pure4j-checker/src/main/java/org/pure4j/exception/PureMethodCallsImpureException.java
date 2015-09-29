package org.pure4j.exception;

import org.pure4j.model.CallHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodCallsImpureException extends Pure4JException {

	public PureMethodCallsImpureException(PureMethod pureMethod, CallHandle ch) {
		super("Pure Method makes impure call to "+ch+"\n"+pureMethod);
	}

}
