package org.pure4j.exception;

import org.pure4j.model.MemberHandle;

public class MemberCantBeHydratedException extends Pure4JException {

	public MemberCantBeHydratedException(MemberHandle mh) {
		super("Couldn't hydrate: "+mh);
	}

}
