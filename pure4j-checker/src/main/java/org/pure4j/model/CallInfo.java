package org.pure4j.model;

import java.util.ArrayList;
import java.util.List;

public class CallInfo {

	private int opcodes;
	List<Object> methodsBeforeReturns = new ArrayList<Object>();

	public List<Object> getMethodsBeforeReturns() {
		return methodsBeforeReturns;
	}

	public int getOpcodes() {
		return opcodes;
	}

	public void setOpcodes(int opcodes) {
		this.opcodes = opcodes;
	}
	
	public void addMethodBeforeReturn(Object mh) {
		methodsBeforeReturns.add(mh);
	}
}
