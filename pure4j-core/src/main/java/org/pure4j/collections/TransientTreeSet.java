package org.pure4j.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class TransientTreeSet<T> extends ATransientCollection<T> implements ITransientSet<T> {
	
	protected Set<T> wrapped;
	
	public TransientTreeSet() {
		this(new TreeSet<T>());
	}
	
	public TransientTreeSet(Comparator<? super T> comp) {
		this(new TreeSet<T>(Pure4J.immutable(comp)));
	}
	
	private TransientTreeSet(TreeSet<T> wrapped) {
		this.wrapped = wrapped;
	}

	@Pure(Enforcement.FORCE)
	public TransientTreeSet(Collection<? extends T> c) {
		this();
		for (T t : c) {
			Pure4J.immutable(t);
			wrapped.add(t);
		}
	}
	
	@Pure(Enforcement.FORCE)
	public TransientTreeSet(Comparator<? super T> comp, Collection<? extends T> c) {
		this(comp);
		for (T t : c) {
			Pure4J.immutable(t);
			wrapped.add(t);
		}
	}
	
	public TransientTreeSet(IPersistentCollection<T> c) {
		this(new TreeSet<T>(c));
	}
	
	public TransientTreeSet(Comparator<? super T> comp, IPersistentCollection<T> c) {
		this(Pure4J.immutable(comp));
		for (T t : c) {
			wrapped.add(t);
		}
	}
	
	public TransientTreeSet(ISeq<T> c) {
		this();
		for (T t : c) {
			wrapped.add(t);
		}
	}
	
	public TransientTreeSet(Comparator<? super T> comparator, ISeq<T> items) {
		this(Pure4J.immutable(comparator));
		for (T t : items) {
			add(t);
		}
	}
	
	@SafeVarargs
	@Pure(Enforcement.FORCE)
	public TransientTreeSet(T... items) {
		this();
		for (T t : items) {
			Pure4J.immutable(t);
			wrapped.add(t);
		}
	}
	
	@SafeVarargs
	@Pure(Enforcement.FORCE)
	public TransientTreeSet(Comparator<? super T> comp, T... items) {
		this(comp);
		for (T t : items) {
			Pure4J.immutable(t);
			wrapped.add(t);
		}
	}

	

	@Override
	public IPersistentSet<T> persistent() {
		return new PersistentTreeSet<T>(this);
	}
	
	@Override
	public ISeq<T> seq() {
		return new IterableSeq<T>(wrapped);
	}

	@Override
	protected Collection<T> getWrapped() {
		return wrapped;
	}


}