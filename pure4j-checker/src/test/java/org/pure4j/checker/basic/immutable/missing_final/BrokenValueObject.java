package org.pure4j.checker.basic.immutable.missing_final;

import java.io.InputStream;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public class BrokenValueObject extends AnotherBrokenObject {

	Integer int2;
	String b;
	final InputStream is;
	
	public BrokenValueObject(Integer in, Integer int2, String b, InputStream is) {
		super(in);
		this.int2 = int2;
		this.b = b;
		this.is = is;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((int2 == null) ? 0 : int2.hashCode());
		result = prime * result + ((is == null) ? 0 : is.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public void increment() {
		this.int2++;
	}

	public Integer getInt2() {
		return int2;
	}

	public String getB() {
		return b;
	}

	public InputStream getIs() {
		return is;
	}
	
	
}
