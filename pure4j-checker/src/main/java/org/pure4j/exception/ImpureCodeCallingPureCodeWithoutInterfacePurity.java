package org.pure4j.exception;

import org.pure4j.model.MemberHandle;

public class ImpureCodeCallingPureCodeWithoutInterfacePurity extends Pure4JException {

	public ImpureCodeCallingPureCodeWithoutInterfacePurity(MemberHandle from, MemberHandle to) {
		super("Method "+from+" expecting to call a pure method: \n"+to);
	}

}
