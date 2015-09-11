package org.pure4j.checker.basic.immutable.good;

import java.math.BigInteger;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.pure.methods.SomePureStuff;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class SomeValueObject {

	private final String name;
	private final Integer age;
	private final int nameLength;
	private final BigInteger bi = new BigInteger("498");
	private final int r = 5;
	public BigInteger getBi() {
		return bi;
	}

	public int getR() {
		return r;
	}

	public char getC() {
		return c;
	}

	private final char c = '5';
	
	
	public int getNameLength() {
		return nameLength;
	}

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	public SomeValueObject(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
		this.nameLength = SomePureStuff.getLetterCount(name);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "hey";
	}
	
	
}
