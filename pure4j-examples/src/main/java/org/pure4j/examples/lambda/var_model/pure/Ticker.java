package org.pure4j.examples.lambda.var_model.pure;

import org.pure4j.immutable.AbstractImmutableValue;

public final class Ticker extends AbstractImmutableValue<Ticker> {

	private final String name;

	public Ticker(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	protected void fields(Visitor v, Ticker t) {
		v.visit(name, t.name);
	}
	
}
