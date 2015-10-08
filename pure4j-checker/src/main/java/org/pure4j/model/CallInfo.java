package org.pure4j.model;

import java.util.ArrayList;
import java.util.List;

public class CallInfo {

	private int opcodes;
	List<Object> methodsBeforeReturns = new ArrayList<Object>();
	private boolean usesThis = false;

	public boolean usesThis() {
		return usesThis;
	}

	public void setUsesThis(boolean usesThis) {
		this.usesThis = usesThis;
	}

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
	
	public final static CallInfo NO_CALL = new CallInfo();
}
