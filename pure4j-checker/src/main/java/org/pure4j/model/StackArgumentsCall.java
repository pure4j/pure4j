package org.pure4j.model;

import java.util.List;

public interface StackArgumentsCall {

	public List<Integer> getLocalVariables();
	public Object getName();
	public boolean isFirstCall();
}
