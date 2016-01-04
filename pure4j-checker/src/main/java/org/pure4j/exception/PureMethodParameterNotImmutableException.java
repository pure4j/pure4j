package org.pure4j.exception;

import org.pure4j.model.GenericTypeHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodParameterNotImmutableException extends Pure4JException {

	public PureMethodParameterNotImmutableException(PureMethod pureMethod, GenericTypeHandle t) {
		super("method  can't take non-immutable argument "+t+"\n"+pureMethod);
	}

}
