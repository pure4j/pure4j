package org.pure4j.model.impl;

import org.pure4j.model.ArgumentedDeclarationHandle;
import org.pure4j.model.GenericTypeHandle;
import org.pure4j.model.TypedDeclarationHandle;

public class MethodDeclarationHandle extends AbstractDeclarationHandle implements ArgumentedDeclarationHandle, TypedDeclarationHandle {

	protected GenericTypeHandle returnType;
	protected GenericTypeHandle args[];
	
	public MethodDeclarationHandle(String className, String name, String desc, int line, int modifiers, ClassHandle on, GenericTypeHandle returnType, GenericTypeHandle[] args) {
	    super(className, name, desc, line, modifiers, on);
	    this.args = args;
	    this.returnType = returnType;
	}

	public boolean overrides(MethodDeclarationHandle another) {
		return (this.name.equals(another.getName())) && (this.getDesc().equals(another.getDesc()));
	}

	
	@Override
	public GenericTypeHandle[] getGenericTypes() {
		return args;
	}
	
	public GenericTypeHandle getGenericType() {
		return returnType;
	}

}
