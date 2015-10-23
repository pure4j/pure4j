package org.pure4j.test.checker.spec.immutable.broken_extend;

import java.math.BigInteger;

import org.pure4j.exception.ClassExpectingPureMethod;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.spec.pure.static_methods.StaticPureMethods;

public final class SomeValueObjectBrokenExtend extends AbstractVO {

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
	public SomeValueObjectBrokenExtend(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
		this.nameLength = StaticPureMethods.getLetterCount(name);
	}

	@Override
	@CausesError({ClassExpectingPureMethod.class, PureMethodCallsImpureException.class})
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	@ShouldBePure
	public String toString() {
		return name;
	}
	
	
}
