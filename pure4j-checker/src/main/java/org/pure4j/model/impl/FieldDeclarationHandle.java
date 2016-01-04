package org.pure4j.model.impl;

import org.pure4j.model.GenericTypeHandle;
import org.pure4j.model.TypedDeclarationHandle;

public class FieldDeclarationHandle extends AbstractDeclarationHandle implements TypedDeclarationHandle {

	private GenericTypeHandle returnType;
	
	public FieldDeclarationHandle(String className, String name, int line, int modifiers, ClassHandle on, GenericTypeHandle returnType) {
		super(className, name, null, line, modifiers, on);
		this.returnType = returnType;
	}
	
	public GenericTypeHandle getGenericType() {
		return returnType;
	}


	
	
}
