package org.pure4j.test.checker.spec.mutable_unshared.library.supplier;

import org.pure4j.annotations.mutable.MutableUnshared;

@MutableUnshared
public class PureLibrary {

	public int doSomething(String a) {
		return a.hashCode();
	}
}
