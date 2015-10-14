package org.pure4j.collections;

import java.util.Collection;
import java.util.LinkedList;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class TransientList<K> extends LinkedList<K> implements ITransientList<K> {

	@Override
	public IPersistentList<K> persistent() {
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
	
	@Pure(Enforcement.FORCE)
	@SafeVarargs
	public TransientList(K... items) {
		for (K k : items) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}
	
	public TransientList(Seqable<K> seqable) {
		for (K k : seqable.seq()) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}
}