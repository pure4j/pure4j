package org.pure4j.checker.library_case.client;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.library_case.supplier.PureLibrary;

public class PureClient {

	@Pure
	public int someFunction(String in) {
		return new PureLibrary().doSomething(in);
	}
}
