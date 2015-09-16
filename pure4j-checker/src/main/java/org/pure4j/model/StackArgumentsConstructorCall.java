package org.pure4j.model;

import java.util.List;

public class StackArgumentsConstructorCall extends ConstructorHandle implements StackArgumentsCall {

	private boolean firstCall;

	public boolean isFirstCall() {
		return firstCall;
	}

	public StackArgumentsConstructorCall(String className, String desc, int line, List<Integer> localVariables, boolean firstCall) {
		super(className, desc, line);
		this.localVariables = localVariables == null ? this.localVariables : localVariables;
		this.firstCall = firstCall;
	}
	
	public List<Integer> getLocalVariables() {
		return localVariables;
	}

	protected List<Integer> localVariables;
	
}
