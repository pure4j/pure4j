package org.pure4j.model;

import java.util.List;

public class StackArgumentsMethodHandle extends MethodHandle implements StackArgumentsHandle {

	private boolean firstCall;

	public boolean isFirstCall() {
		return firstCall;
	}

	public StackArgumentsMethodHandle(String className, String name, String desc, int line, List<Integer> localVariables, boolean firstCall) {
		super(className, name, desc, line);
		this.localVariables = localVariables == null ? this.localVariables : localVariables;
		this.firstCall = firstCall;
	}
	
	public List<Integer> getLocalVariables() {
		return localVariables;
	}

	protected List<Integer> localVariables;
	
	
	
}
