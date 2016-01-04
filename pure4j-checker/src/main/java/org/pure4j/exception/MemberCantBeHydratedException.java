package org.pure4j.exception;

import org.pure4j.model.impl.AbstractMemberHandle;

public class MemberCantBeHydratedException extends Pure4JException {

	public MemberCantBeHydratedException(AbstractMemberHandle mh) {
		super("Couldn't hydrate: "+mh);
	}

}
