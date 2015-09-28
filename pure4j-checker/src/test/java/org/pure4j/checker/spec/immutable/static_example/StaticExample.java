package org.pure4j.checker.spec.immutable.static_example;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.support.ShouldBePure;

@ImmutableValue
public class StaticExample {

	@ShouldBePure
	public StaticExample() {
		
	}
	
	
	@ShouldBePure
	@Override
	public int hashCode() {
		return 1;
	}


	@ShouldBePure
	@Override
	public String toString() {
		return null;
	}

	/**
	 * Not going to be tested.
	 */
	public static Object someCrazyThing(Object o) {
		return o;
	}
}
