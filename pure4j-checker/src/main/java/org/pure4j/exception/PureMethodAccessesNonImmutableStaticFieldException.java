package org.pure4j.exception;

import org.pure4j.model.FieldHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PureMethodAccessesNonImmutableStaticFieldException extends Pure4JException {

	public PureMethodAccessesNonImmutableStaticFieldException(PureMethod pureMethod, FieldHandle fh) {
		super("Pure implementation is expected to be pure but accesses a non-immutable static"+fh+"\n"+pureMethod);
	}

}
