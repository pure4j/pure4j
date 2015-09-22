package org.pure4j.exception;

import org.pure4j.model.FieldHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodAccessesSharedFieldException extends Pure4JException {

	public PureMethodAccessesSharedFieldException(PureMethod pureMethod, FieldHandle fh) {
		super("Pure implementation is expected to be pure but accesses non-final field which isn't private/protected "+fh+"\n"+pureMethod);
	}

}
