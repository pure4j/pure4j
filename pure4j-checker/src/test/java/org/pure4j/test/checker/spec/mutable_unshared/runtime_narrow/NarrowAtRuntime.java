package org.pure4j.test.checker.spec.mutable_unshared.runtime_narrow;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.test.ShouldBePure;

@MutableUnshared
public class NarrowAtRuntime {


	@ShouldBePure
	public Object in(Object x) {
		Pure4J.immutable(x);
		try {
			if (x.equals(5)) {
				return Pure4J.returnImmutable(null);		// this sucks
			} else {
				return Pure4J.returnImmutable(66);			// so does this.
			}
		} catch (Throwable t) {
			if (x.equals(10)) {
				return Pure4J.returnImmutable(new Object());
			} else {
				throw new IllegalAccessError();
			}
		}
		
	}
}
