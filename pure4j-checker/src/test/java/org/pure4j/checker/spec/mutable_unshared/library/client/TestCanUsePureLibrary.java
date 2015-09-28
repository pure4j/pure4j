package org.pure4j.checker.spec.mutable_unshared.library.client;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.spec.mutable_unshared.library.supplier.PureLibrary;
import org.pure4j.checker.support.ShouldBePure;

public class TestCanUsePureLibrary {

	@Pure
	@ShouldBePure
	public int someFunction(String in) {
		return new PureLibrary().doSomething(in);
	}
}
