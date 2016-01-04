package org.pure4j.model.impl;


import org.pure4j.model.GenericTypeHandle;
import org.springframework.asm.Type;

public class AsmGenericTypeHandle implements GenericTypeHandle {

	private Type asmType;
	
	public AsmGenericTypeHandle(String desc) {
		this.asmType = Type.getType(desc);
	}

	@Override
	public boolean isAssignableFrom(GenericTypeHandle handle) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isArray() {
		return false;
	}
	
	public boolean isEnum() {
		return false;
	}
}
