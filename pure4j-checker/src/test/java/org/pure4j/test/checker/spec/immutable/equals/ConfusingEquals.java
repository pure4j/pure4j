package org.pure4j.test.checker.spec.immutable.equals;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.ShouldBePure;

@ImmutableValue
public final class ConfusingEquals {


	/**
	 * Note that this doesn't override the Object.equals.
	 * Doesn't matter - equals is pure.
	 * 
	 * @param o
	 * @return
	 */
	@ShouldBePure
	public boolean equals(String o) {
		return true;
	}
	
	@ShouldBePure
	public int hashCode() {
		return 0 + 6;
	}
	

	@Override
	@ShouldBePure
	public String toString() {
		return "ConfusingEquals []" ;
	}
	
	
}
