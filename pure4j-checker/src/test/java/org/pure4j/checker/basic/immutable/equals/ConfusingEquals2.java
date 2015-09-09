package org.pure4j.checker.basic.immutable.equals;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public final class ConfusingEquals2 {
	
	public int hashCode() {
		return 0;
	}
	
	public String toString() {
		return "blah";
	}

}
