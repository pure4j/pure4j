package org.pure4j.examples.lambda.var_model.pure;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.collections.IPersistentList;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.IPersistentVector;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PureCollections;
import org.pure4j.lambda.Java8API;
import org.pure4j.lambda.PureCollectors;

@ImmutableValue
@Java8API
public class VarProcessorImpl implements VarProcessor {
	
	private final float confidenceLevel;

	public VarProcessorImpl(float confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}
	
	public float getVar(IPersistentMap<String, PnLStream> historic, ISeq<Sensitivity> sensitivities) {
		// combine the sensitivities
		PnLStream combined = null;
		for (Sensitivity s : sensitivities) {
			String t = s.getTicker();
			PnLStream theStream = historic.get(t);
			float scale = s.getAmount();			
			PnLStream scaledStream = theStream.scale(scale);
			combined = (combined == null) ? scaledStream : combined.add(scaledStream);
		}
		
		// collect the results + sort (probably highly inefficient)
		Stream<Float> stream = combined.getPnls().values().stream();
		Collector<Float, List<Float>, IPersistentList<Float>> collector = PureCollectors.toPersistentList();
		IPersistentList<Float> results = stream.collect(collector);
		IPersistentVector<Float> sorted = PureCollections.sort(results.seq());
		
		// work out confidence level
		float members = sorted.size();
		float index = members - members * confidenceLevel -1;
		
		return sorted.get((int) Math.floor(index));
	}

	@Override
	public int hashCode() {
		Pure4J.unsupported();
		return 0;
	}

	@Override
	public String toString() {
		return "VarProcessorImpl";
	}
	
	
}
