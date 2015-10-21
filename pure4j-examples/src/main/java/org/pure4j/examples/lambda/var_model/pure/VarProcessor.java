package org.pure4j.examples.lambda.var_model.pure;

import java.util.Currency;

import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.ISeq;

public interface VarProcessor {

	Amount getVar(IPersistentMap<Ticker, PnLStream> historic, ISeq<Sensitivity> sensitivities, IPersistentMap<Currency, Float> fxRates);
}
