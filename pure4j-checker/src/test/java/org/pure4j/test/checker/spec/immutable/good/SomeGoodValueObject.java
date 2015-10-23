package org.pure4j.test.checker.spec.immutable.good;

import java.math.BigInteger;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.spec.pure.static_methods.StaticPureMethods;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class SomeGoodValueObject {

	private final String name;
	private final Integer age;
	private final int nameLength;
	private final BigInteger bi = new BigInteger("498");
	private final int r = 5;
	
	@ShouldBePure
	public BigInteger getBi() {
		return bi;
	}

	@ShouldBePure
	public int getR() {
		return r;
	}

	@ShouldBePure
	public char getC() {
		return c;
	}

	private final char c = '5';
	
	
	@ShouldBePure
	public int getNameLength() {
		return nameLength;
	}

	@ShouldBePure
	public String getName() {
		return name;
	}

	@ShouldBePure
	public Integer getAge() {
		return age;
	}

	@ShouldBePure
	public SomeGoodValueObject(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
		this.nameLength = StaticPureMethods.getLetterCount(name);
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return 0;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return "hey";
	}
	
	
}
