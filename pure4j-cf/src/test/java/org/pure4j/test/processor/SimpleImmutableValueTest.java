package org.pure4j.test.processor;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public class SimpleImmutableValueTest {
	
	private final String someValue;

	public SimpleImmutableValueTest(String someValue) {
		super();
		this.someValue = someValue;
	}

	public String getSomeValue() {
		return someValue;
	}

	public boolean testArgumentIsImmutableValue(Object in) {
		Pure4J.immutable(in);
		return true;
	}
	
	public boolean testImmutableValueString() {
		return testArgumentIsImmutableValue("hello");
	}
	
	public int testImmutableValueInteger() {
		return SomeClassWithAPure.somePureMethod();
	}

	public void testImmutableValueArgument() {
		String someString = "jelly";
		testArgumentIsImmutableValue(someString);
	}

}
