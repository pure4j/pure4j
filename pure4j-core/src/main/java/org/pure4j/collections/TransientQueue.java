package org.pure4j.collections;

import java.util.LinkedList;

public class TransientQueue<T> extends LinkedList<T> implements ITransientQueue<T> {
	
	public TransientQueue() {
		super();
	}
	
	public TransientQueue(ISeq<T> s) {
		for (T t : s) {
			add(t);
		}
	}

	@Override
	public IPersistentCollection<T> persistent() {
		return new PersistentQueue<T>(this.size(), null, new PersistentVector<T>(this));
	}

}
