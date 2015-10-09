package org.pure4j.collections;

import java.util.ArrayList;
import java.util.Collection;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureParameters;

final public class TransientVector<K> extends ArrayList<K> implements ITransientVector<K> {
	
	public TransientVector() {
		super();
	}
	
	@PureParameters(Enforcement.NOT_PURE)
	public TransientVector(Collection<? extends K> c) {
		super(c);
	}

	public TransientVector(int initialCapacity) {
		super(initialCapacity);
	}

	public TransientVector(ISeq<K> list) {
		super(list.count());
		for (K k : list) {
			add(k);
		}
	}

	@Pure(Enforcement.FORCE)  // this is a short-cut to simply iterating over the whole contents, which would be pure.	
	@Override
	public IPersistentVector<K> persistent() {
		return new PersistentVector<K>(this);
	}
	
}