package org.pure4j.checker.basic.immutable.parent_class_a_library;

import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.immutable.AbstractImmutableValue;

public class SomeBean extends AbstractImmutableValue<SomeBean>{

	@ShouldBePure
	public SomeBean(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	
	private final String name;
	private final int age;
	
	@ShouldBePure
	public String getName() {
		return name;
	}

	@ShouldBePure
	public int getAge() {
		return age;
	}

	@Override
	@ShouldBePure
	public void fields(Visitor v, SomeBean other) {
		v.visit(name, other.name);
		v.visit(age, other.age);
	}

}
