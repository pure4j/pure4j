package org.pure4j.checkerframework.mutability;

import org.pure4j.annotations.mutability.ImmutableValue;

public class SimpleImmutableValueTest {

	public boolean testArgumentIsImmutableValue(@ImmutableValue Object someValue) {
		return true;
	}
	
	public void testImmutableValueString() {
		testArgumentIsImmutableValue("hello");
	}
	
	public void testImmutableValueInteger() {
		testArgumentIsImmutableValue(15);
	}

	public void testImmutableValueArgument() {
		@ImmutableValue String someString = "jelly";
		testArgumentIsImmutableValue(someString);
	}

}
