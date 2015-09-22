package org.pure4j.exception;

import java.lang.reflect.Type;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodParameterNotImmutableException extends Pure4JException {

	public PureMethodParameterNotImmutableException(PureMethod pureMethod, Type t) {
		super("method  can't take non-immutable argument "+t+"\n"+pureMethod);
	}

}
