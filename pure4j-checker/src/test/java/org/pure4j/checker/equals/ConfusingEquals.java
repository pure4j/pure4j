package org.pure4j.checker.equals;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public final class ConfusingEquals {


	/**
	 * Note that this doesn't override the Object.equals
	 * 
	 * @param o
	 * @return
	 */
	public boolean equals(String o) {
		return true;
	}
	
	public int hashCode() {
		return 0;
	}
	

	@Override
	public String toString() {
		return "ConfusingEquals []";
	}
	
	
}
