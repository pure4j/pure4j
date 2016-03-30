package org.pure4j.collections;

import java.util.Collection;
import java.util.LinkedList;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

@MutableUnshared
public class TransientList<K> extends ATransientList<K> implements ITransientList<K> {

	@Override
	public IPersistentList<K> persistent() {
		return new PersistentList<K>(this);
	}

	public TransientList() {
		super(new LinkedList<K>());
	}

	@Pure(Enforcement.FORCE)
	public TransientList(Collection<? extends K> c) {
		this();
		for (K k : c) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}		
	
	@Pure(Enforcement.FORCE)
	@SafeVarargs
	public TransientList(K... items) {
		this();
		for (K k : items) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}
	
	public TransientList(ISeq<K> seqable) {
		this();
		for (K k : seqable.seq()) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}
	
	
}