package org.pure4j.exception;

import org.pure4j.model.impl.ClassHandle;
import org.pure4j.model.impl.FieldDeclarationHandle;

public class FieldTypeNotImmutableException extends Pure4JException {

	public FieldTypeNotImmutableException(FieldDeclarationHandle f, ClassHandle immutableClass) {
		super(	"Field "+f.getName()+" should have an immutable type on class "+
				f+".  Consider adding @ImmutableValue to "+f.getGenericType());
	}

}
