package org.pure4j.checker.basic.library.case1.supplier;

import org.pure4j.annotations.pure.Pure;

@Pure
public class PureLibrary {

	public int doSomething(String a) {
		return a.hashCode();
	}
}
