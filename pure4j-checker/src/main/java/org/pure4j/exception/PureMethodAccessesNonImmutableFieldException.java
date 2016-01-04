package org.pure4j.exception;

import org.pure4j.model.impl.FieldDeclarationHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodAccessesNonImmutableFieldException extends Pure4JException {

	public PureMethodAccessesNonImmutableFieldException(PureMethod pureMethod, FieldDeclarationHandle fh) {
		super("Pure implementation is expected to be pure but accesses a non-immutable "+fh+"\n"+pureMethod);
	}

}
