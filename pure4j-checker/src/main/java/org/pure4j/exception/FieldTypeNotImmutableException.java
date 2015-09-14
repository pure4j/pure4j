package org.pure4j.exception;

import java.lang.reflect.Field;

public class FieldTypeNotImmutableException extends Pure4JException {

	public FieldTypeNotImmutableException(Field f) {
		super(	"Field "+f.getName()+" should have an immutable type on class "+
				f.getGenericType()+".  Consider adding @ImmutableValue to "+f.getGenericType());
	}

}
