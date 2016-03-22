package org.pure4j.test.immutable;

import org.pure4j.immutable.AbstractImmutableValue;

public class Bob extends AbstractImmutableValue<Bob> {

	public Bob(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	
	final private String name;
	final private int age;

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}
	
	@Override
	public void fields(Visitor v, Bob other) {
		v.visit(name, other.name);
		v.visit(age, other.age);
	}
	
}
