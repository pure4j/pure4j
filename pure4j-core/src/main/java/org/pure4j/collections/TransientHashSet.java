package org.pure4j.collections;

import java.util.Collection;
import java.util.HashSet;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class TransientHashSet<T> extends ATransientCollection<T> implements ITransientSet<T> {

	protected HashSet<T> wrapped;
	
	public TransientHashSet() {
		this(new HashSet<T>());
	}
	
	private TransientHashSet(HashSet<T> w) {
		this.wrapped = w;
	}

	@Pure(Enforcement.FORCE)
	public TransientHashSet(Collection<? extends T> c) {
		this();
		for (T t : c) {
			Pure4J.immutable(t);
			wrapped.add(t);
		}
	}
	
	public TransientHashSet(IPersistentCollection<T> c) {
		this(new HashSet<T>(c));
	}
	
	public TransientHashSet(ISeq<T> c) {
		this();
		for (T t : c) {
			wrapped.add(t);
		}
	}
	
	@SafeVarargs
	@Pure(Enforcement.FORCE)
	public TransientHashSet(T... items) {
		this();
		for (T t : items) {
			Pure4J.immutable(t);
			wrapped.add(t);
		}
	}

	public TransientHashSet(int initialCapacity, float loadFactor) {
		this(new HashSet<T>(initialCapacity, loadFactor));
	}

	public TransientHashSet(int initialCapacity) {
		this(new HashSet<T>(initialCapacity));
	}

	@Override
	public IPersistentSet<T> persistent() {
		return new PersistentHashSet<T>(this);
	}

	@Override
	public ISeq<T> seq() {
		return IterableSeq.create(wrapped.iterator());
	}

	@Override
	protected Collection<T> getWrapped() {
		return wrapped;
	}


	public String toString() {
		return ToStringFunctions.toString(this);
	}

	public boolean equals(Object obj) {
		return APersistentSet.setEquals(this, obj);
	}
	

	public int hashCode() {
		return Hasher.hashUnordered(this);
	}
}
