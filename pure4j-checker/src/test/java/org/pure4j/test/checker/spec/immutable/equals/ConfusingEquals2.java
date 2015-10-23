package org.pure4j.test.checker.spec.immutable.equals;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.ShouldBePure;

@ImmutableValue
public final class ConfusingEquals2 { 
	
	@ShouldBePure
	public int hashCode() {
		return 0;
	}
	
	@ShouldBePure
	public String toString() {
		return "blah";
	}

}
