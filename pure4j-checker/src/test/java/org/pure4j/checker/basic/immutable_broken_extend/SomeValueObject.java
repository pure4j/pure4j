package org.pure4j.checker.basic.immutable_broken_extend;

import java.math.BigInteger;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.pure_methods.SomePureStuff;
import org.pure4j.immutable.EqualsHelp;

public final class SomeValueObject extends AbstractVO {

	private final String name;
	private final Integer age;
	private final int nameLength;
	private final BigInteger bi = new BigInteger("498");
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
	public boolean equals(Object obj) {
		return EqualsHelp.equals(this, obj);
	}

	@Override
	public String toString() {
		return name;
	}
	
	
}