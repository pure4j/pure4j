package org.pure4j.exception;

import org.pure4j.model.impl.FieldDeclarationHandle;

public class FieldNotFinalException extends Pure4JException {

	public FieldNotFinalException(FieldDeclarationHandle f) {
		super("Field "+f+" not declared final, but is immutable");
	}

}
