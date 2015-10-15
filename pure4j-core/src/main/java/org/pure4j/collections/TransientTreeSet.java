package org.pure4j.collections;

import java.util.Comparator;
import java.util.TreeSet;

import org.pure4j.Pure4J;

public class TransientTreeSet<T> extends TreeSet<T> implements ITransientSet<T> {
	
	public TransientTreeSet() {
		super();
	}

	public TransientTreeSet(Comparator<? super T> comparator) {
		super(Pure4J.immutable(comparator));
	}
	
	public TransientTreeSet(Comparator<? super T> comparator, ISeq<T> items) {
		this(Pure4J.immutable(comparator));
		for (T t : items) {
			add(t);
		}
	}

	@Override
	public IPersistentSet<T> persistent() {
		return new PersistentTreeSet<T>(this);
	}

}