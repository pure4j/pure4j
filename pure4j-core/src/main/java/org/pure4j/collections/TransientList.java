package org.pure4j.collections;

import java.util.Collection;
import java.util.LinkedList;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

final public class TransientList<K> extends LinkedList<K> implements ITransientCollection<K> {

	@Override
	public IPersistentCollection<K> persistent() {
		return new PersistentList<K>(this);
	}

	public TransientList() {
		super();
	}

	@Pure(Enforcement.FORCE)
	public TransientList(Collection<? extends K> c) {
		super();
		for (K k : c) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}		
	
}