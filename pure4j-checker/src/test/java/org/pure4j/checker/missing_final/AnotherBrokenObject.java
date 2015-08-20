package org.pure4j.checker.missing_final;

public class AnotherBrokenObject {

	protected Integer in;

	public Integer getIn() {
		return in;
	}

	public void setIn(Integer in) {
		this.in = in;
	}

	public AnotherBrokenObject(Integer in) {
		super();
		this.in = in;
	}
	
}
