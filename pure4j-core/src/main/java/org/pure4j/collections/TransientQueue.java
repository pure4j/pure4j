package org.pure4j.collections;

import java.util.LinkedList;

public class TransientQueue<T> extends LinkedList<T> implements ITransientQueue<T> {

	@Override
	public IPersistentCollection<T> persistent() {
		return new PersistentQueue<T>(this.size(), null, (PersistentVector<T>) PersistentVector.create(this));
	}

}
