package org.pure4j.model.impl;

import org.pure4j.model.CallHandle;


public class FieldCallHandle extends AbstractMemberHandle implements CallHandle {

	public FieldCallHandle(String className, String name, String desc, int line) {
		super(className, name, desc, line);
	}
	
}
