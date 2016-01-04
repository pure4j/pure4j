package org.pure4j.model.impl;

import java.util.List;

import org.pure4j.model.StackArgumentsCallHandle;

public class StackArgumentsMethodCallHandle extends AbstractMemberHandle implements StackArgumentsCallHandle {

	private boolean firstCall;

	public boolean isFirstCall() {
		return firstCall;
	}

	public StackArgumentsMethodCallHandle(String className, String name, String desc, int line, List<Integer> localVariables, boolean firstCall) {
		super(className, name, desc, line);
		this.localVariables = localVariables == null ? this.localVariables : localVariables;
		this.firstCall = firstCall;
	}
	
	public List<Integer> getLocalVariables() {
		return localVariables;
	}

	protected List<Integer> localVariables;
	
	
	
}
