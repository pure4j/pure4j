package org.pure4j.examples.var_model.pure;

import java.util.Collections;
import java.util.Currency;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.collections.IPersistentList;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.IPersistentVector;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PureCollections;
import org.pure4j.collections.PureCollectors;

@ImmutableValue
public class VarProcessorImpl implements VarProcessor {
	
	private final float confidenceLevel;
	private final Currency homeCurrency;

	public VarProcessorImpl(float confidenceLevel, Currency homeCurrency) {
		this.confidenceLevel = confidenceLevel;
		this.homeCurrency = homeCurrency;
	}
	
	/**
	 * It should complain about this method signature, but it wont.  Because, we allow
	 * 
	 */
	public Amount getVar(IPersistentMap<Ticker, PnLStream> historic, ISeq<Sensitivity> sensitivities, IPersistentMap<Currency, Float> fxRates) {
		// combine the sensitivities
		PnLStream combined = null;
		for (Sensitivity s : sensitivities) {
			Ticker t = s.getTicker();
			PnLStream theStream = historic.get(t);
			float scale = s.getAmount();
			if (theStream.getCcy() != homeCurrency) {
				Float rate = fxRates.get(theStream.getCcy());
				if (rate == null) {
					throw new FXRateNotPresentException(theStream.getCcy());
				}
				scale = scale * rate;
			}
			
			PnLStream scaledStream = theStream.scale(scale);
			
			
			
			combined = (combined == null) ? scaledStream : combined.add(scaledStream);
		}
		
		// collect the results + sort (probably highly inefficient)
		IPersistentList<Float> results = combined.getPnls().values().stream().collect(PureCollectors.toPersistentList());
		IPersistentVector<Float> sorted = PureCollections.sort(results.seq());
		
		// work out confidence level
		float members = sorted.size();
		float index = members * confidenceLevel;
		
		return new Amount(homeCurrency, sorted.get((int) index));
	}

	@Override
	public int hashCode() {
		return 0;	// we don't care.  The fact we have to implement this is a bug.
	}

	@Override
	public String toString() {
		return "VarProcessorImpl";
	}
	
	
}
