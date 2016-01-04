package org.pure4j.model.impl;

import org.pure4j.model.DeclarationHandle;

public class ClassInitHandle extends AbstractDeclarationHandle implements DeclarationHandle {

	public ClassInitHandle(String className, String desc, int line, int modifiers, ClassHandle on) {
		super(className, "<clinit>", desc, line, modifiers, on);
	}

}
