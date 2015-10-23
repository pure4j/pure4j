package org.pure4j.test.checker.spec.immutable.parent_class_a_library;

import org.pure4j.immutable.AbstractImmutableValue;
import org.pure4j.test.ShouldBePure;

public class SomeBeanLibraryBased extends AbstractImmutableValue<SomeBeanLibraryBased>{

	@ShouldBePure
	public SomeBeanLibraryBased(String name, int age) {
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
	protected void fields(Visitor v, SomeBeanLibraryBased other) {
		v.visit(name, other.name);
		v.visit(age, other.age);
	}

}
