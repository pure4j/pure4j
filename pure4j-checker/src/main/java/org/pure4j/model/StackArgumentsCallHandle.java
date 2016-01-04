package org.pure4j.model;

import java.util.List;

public interface StackArgumentsCallHandle extends CallHandle {

	public List<Integer> getLocalVariables();
	public boolean isFirstCall();
}
