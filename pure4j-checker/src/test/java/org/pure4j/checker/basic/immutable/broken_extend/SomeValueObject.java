package org.pure4j.checker.basic.immutable.broken_extend;

import java.math.BigInteger;

import org.pure4j.Pure4J;
import org.pure4j.checker.basic.pure.methods.SomePureStuff;

public final class SomeValueObject extends AbstractVO {

	private final String name;
	private final Integer age;
	private final int nameLength;
	private final BigInteger bi = new BigInteger("498");
	public BigInteger getBi() {
		return bi;
	}

	public int getR() {
		return r;
	}

	public char getC() {
		return c;
	}

	private final int r = 5;
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
		return name;
	}
	
	
}
