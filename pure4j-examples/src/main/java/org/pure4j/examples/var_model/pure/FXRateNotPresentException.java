package org.pure4j.examples.var_model.pure;

import java.util.Currency;

public class FXRateNotPresentException extends RuntimeException {

	public FXRateNotPresentException(Currency ccy) {
		super("For ccy: "+ccy);
	}

}
