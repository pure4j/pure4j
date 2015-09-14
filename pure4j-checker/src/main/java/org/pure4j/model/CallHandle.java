package org.pure4j.model;

public abstract class CallHandle extends MemberHandle {

	public CallHandle(String className, String name, String desc, int line) {
		this.className = className;
		this.name = name;
		this.desc = desc;
		this.lineNumber = line;
	}
	
	public CallHandle() {
	}
	
	protected int lineNumber;

	public int getLineNumber() {
		return lineNumber;
	}
	
	public abstract CallHandle swapClass(Class<?> class1);
}
