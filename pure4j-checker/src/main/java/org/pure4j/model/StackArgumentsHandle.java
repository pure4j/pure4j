package org.pure4j.model;

import java.util.List;

public interface StackArgumentsHandle {

	public List<Integer> getLocalVariables();
	public Object getName();
	public boolean isFirstCall();
}
