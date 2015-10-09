package org.pure4j.collections;

import java.util.ArrayList;
import java.util.Collection;

final public class TransientVector<K> extends ArrayList<K> implements ITransientVector<K> {
	
	public TransientVector() {
		super();
	}
	
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

	@Override
	public IPersistentVector<K> persistent() {
		return PersistentVector.create(this);
	}

	
}