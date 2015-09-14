package org.pure4j.exception;

import java.lang.reflect.Field;

public class FieldNotFinalException extends Pure4JException {

	public FieldNotFinalException(Field f) {
		super("Field "+f+" not declared final, but has @ImmutableValue annotation");
	}

}
