package org.pure4j.exception;

import org.pure4j.model.CallHandle;
import org.pure4j.model.DeclarationHandle;

public class ImpureCodeCallingPureCodeWithoutInterfacePurity extends Pure4JException {

	public ImpureCodeCallingPureCodeWithoutInterfacePurity(DeclarationHandle from, CallHandle to) {
		super("Method "+from+" expecting to call an interface-pure method: \n"+to);
	}

}
