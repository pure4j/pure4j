package org.pure4j.checker.basic.immutable.broken_extend;

import java.math.BigInteger;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.pure.methods.SomePureStuff;
import org.pure4j.checker.basic.support.ShouldBePure;

public final class SomeValueObject extends AbstractVO {

	private final String name;
	private final Integer age;
	private final int nameLength;
	private final BigInteger bi = new BigInteger("498");
	
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

	private final int r = 5;
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
	public SomeValueObject(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
		this.nameLength = SomePureStuff.getLetterCount(name);
	}

	@Override
	@ShouldBePure
	public int hashCode() {
		return 0;
	}

	@Override
	@ShouldBePure
	public String toString() {
		return name;
	}
	
	
}
