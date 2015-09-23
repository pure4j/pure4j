package org.pure4j.examples.var_model.pure;

import org.pure4j.immutable.AbstractImmutableValue;

public final class Sensitivity extends AbstractImmutableValue<Sensitivity> {

	final Ticker ticker;

	final float amount;
	
	public Sensitivity(Ticker ticker, float amount) {
		super();
		this.ticker = ticker;
		this.amount = amount;
	}
	
	public Ticker getTicker() {
		return ticker;
	}

	public float getAmount() {
		return amount;
	}

	@Override
	public void fields(Visitor v, Sensitivity s) {
		v.visit(ticker, s.ticker);
		v.visit(amount, s.amount);
	}


}
