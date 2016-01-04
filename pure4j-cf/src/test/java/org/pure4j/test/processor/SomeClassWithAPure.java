package org.pure4j.test.processor;

import org.pure4j.annotations.pure.Pure;

public class SomeClassWithAPure {

	@Pure
	public static final int somePureMethod() {
		return 5;
	}
}
