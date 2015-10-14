package org.pure4j.collections;

import java.util.Collection;
import java.util.HashSet;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureParameters;

public class TransientHashSet<T> extends HashSet<T> implements ITransientSet<T> {

	public TransientHashSet() {
		super();
	}

	@PureParameters(Enforcement.NOT_PURE)
	public TransientHashSet(Collection<? extends T> c) {
		super(c);
	}

	public TransientHashSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public TransientHashSet(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public IPersistentSet<T> persistent() {
		return new PersistentHashSet<T>(this);
	}

	@Override
	public boolean contains(Object o) {
		Pure4J.immutable(o);
		return super.contains(o);
	}

	@Override
	public boolean add(T e) {
		Pure4J.immutable(e);
		return super.add(e);
	}

	@Override
	public boolean remove(Object o) {
		Pure4J.immutable(o);
		return super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Pure4J.immutable(c);
		return super.removeAll(c);
	}

	@Pure(value=Enforcement.FORCE)
	@Override
	public <V> V[] toArray(V[] a) {
		return super.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Pure4J.immutable(c);
		return super.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		Pure4J.immutable(c);
		return super.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Pure4J.immutable(c);
		return super.retainAll(c);
	}
	
	

}
