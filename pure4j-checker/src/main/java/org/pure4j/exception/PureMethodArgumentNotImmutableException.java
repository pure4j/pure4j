package org.pure4j.exception;

import java.lang.reflect.Type;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodArgumentNotImmutableException extends Pure4JException {

	public PureMethodArgumentNotImmutableException(PureMethod pureMethod, Type t) {
		super("method:      "+pureMethod+" can't take non-immutable argument "+t);
	}

}
