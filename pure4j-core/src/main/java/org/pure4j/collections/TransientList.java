package org.pure4j.collections;

import java.util.Collection;
import java.util.LinkedList;

final public class TransientList<K> extends LinkedList<K> implements ITransientCollection<K> {

	@Override
	public IPersistentCollection<K> persistent() {
		return PersistentList.create(this);
	}

	public TransientList() {
		super();
	}

	public TransientList(Collection<? extends K> c) {
		super(c);
	}		
	
}