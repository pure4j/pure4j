package org.pure4j.checker.equals;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

@ImmutableValue
public final class ConfusingEquals2 {
	
	public int hashCode() {
		return 0;
	}
	
	public String toString() {
		return "blah";
	}

}
