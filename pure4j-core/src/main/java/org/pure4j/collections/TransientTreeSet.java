package org.pure4j.collections;

import java.util.Comparator;
import java.util.TreeSet;

public class TransientTreeSet<T> extends TreeSet<T> implements ITransientSet<T> {
	
	public TransientTreeSet() {
		super();
	}

	public TransientTreeSet(Comparator<? super T> comparator) {
		super(comparator);
	}
	
	public TransientTreeSet(Comparator<? super T> comparator, ISeq<T> items) {
		super(comparator);
		for (T t : items) {
			add(t);
		}
	}

	@Override
	public IPersistentSet<T> persistent() {
		IPersistentSet<T> out =  (IPersistentSet<T>) PersistentTreeSet.create(comparator());
		for (T t : this) {
			out = out.cons(t);
		}
		
		return out;
	}

}