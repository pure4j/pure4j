package org.pure4j.model.impl;

import org.pure4j.model.ArgumentedDeclarationHandle;
import org.pure4j.model.GenericTypeHandle;

public class ConstructorDeclarationHandle extends AbstractDeclarationHandle implements ArgumentedDeclarationHandle {
	
	public ConstructorDeclarationHandle(String className, String desc, int line, int modifiers, ClassHandle on, GenericTypeHandle[] args) {
		super(className, "<init>", desc, line, modifiers, on);
		this.args = args;
	}
	
	GenericTypeHandle[] args;
	
	@Override
	public GenericTypeHandle[] getGenericTypes() {
		return args;
	}
	
}
