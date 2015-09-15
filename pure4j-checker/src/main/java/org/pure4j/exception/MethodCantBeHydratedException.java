package org.pure4j.exception;

import org.pure4j.model.MethodHandle;

public class MethodCantBeHydratedException extends Pure4JException {

	public MethodCantBeHydratedException(MethodHandle mh) {
		super("Couldn't hydrate method: "+mh);
	}

}
