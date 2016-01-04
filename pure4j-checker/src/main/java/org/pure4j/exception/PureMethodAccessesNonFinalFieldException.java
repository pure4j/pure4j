package org.pure4j.exception;

import org.pure4j.model.impl.FieldDeclarationHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodAccessesNonFinalFieldException extends Pure4JException {

	public PureMethodAccessesNonFinalFieldException(PureMethod pureMethod, FieldDeclarationHandle fh) {
		super("Pure implementation is expected to be pure but accesses non-final field " +fh+"\n"+pureMethod);
	}

}
