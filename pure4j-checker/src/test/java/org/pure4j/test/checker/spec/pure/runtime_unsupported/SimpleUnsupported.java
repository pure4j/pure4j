package org.pure4j.test.checker.spec.pure.runtime_unsupported;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.ShouldBePure;

public class SimpleUnsupported {

	@Pure
	@ShouldBePure
	public void doSomething(Object in1, Object in2) {
		Pure4J.unsupported();
	}

	
}
