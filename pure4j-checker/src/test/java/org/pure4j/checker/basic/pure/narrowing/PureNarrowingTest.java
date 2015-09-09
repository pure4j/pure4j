package org.pure4j.checker.basic.pure.narrowing;

import org.pure4j.annotations.pure.Pure;

@Pure
public class PureNarrowingTest {

	public String doSomethingInterfaceNotPure(Object in) {
		return "blah";
	}
	
	public String doSomethingInterfacePure(String in) {
		return doSomethingInterfaceNotPure(in);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return null;
	}
	
	
}
