package org.pure4j.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureParameters;

@MutableUnshared
public abstract class ATransientList<K> implements List<K>, Seqable<K> {
	
	protected final List<K> wrapped;

	public ATransientList(List<K> wrapped) {
		this.wrapped = wrapped;
	}

	@PureParameters(Enforcement.NOT_PURE)
	public boolean addAll(int index, Collection<? extends K> c) {
		boolean result = false;
		for (K k : c) {
			Pure4J.immutable(k);
			wrapped.add(index++, k);
			result = true;
		}
		
		return result;
	}

	@Override
	public K get(int index) {
		return Pure4J.returnImmutable(wrapped.get(index));
	}

	@Override
	public K set(int index, K element) {
		Pure4J.immutable(element);
		return Pure4J.returnImmutable(wrapped.set(index, element));
	}

	@Override
	public void add(int index, K element) {
		Pure4J.immutable(element);
		wrapped.add(index, element);
	}

	@Override
	public K remove(int index) {
		return Pure4J.returnImmutable(wrapped.remove(index));
	}

	@Override
	public int indexOf(Object o) {
		Pure4J.immutable(o);
		return wrapped.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		Pure4J.immutable(o);
		return wrapped.lastIndexOf(o);
	}

	@Pure(Enforcement.NOT_PURE)
	@Override
	public ListIterator<K> listIterator() {
		return wrapped.listIterator();
	}

	@Pure(Enforcement.NOT_PURE)
	@Override
	public ListIterator<K> listIterator(int index) {
		return wrapped.listIterator(index);
	}

	@Pure(Enforcement.NOT_PURE)
	@Override
	public List<K> subList(int fromIndex, int toIndex) {
		return wrapped.subList(fromIndex, toIndex);
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		Pure4J.immutable(o);
		return wrapped.contains(o);
	}

	@Pure(Enforcement.NOT_PURE)
	@Override
	public Iterator<K> iterator() {
		return wrapped.iterator();
	}

	@Pure(Enforcement.NOT_PURE)
	@Override
	public Object[] toArray() {
		return wrapped.toArray();
	}

	@Pure(Enforcement.NOT_PURE)
	@Override
	public <T> T[] toArray(T[] a) {
		return wrapped.toArray(a);
	}

	@Override
	public boolean add(K e) {
		Pure4J.immutable(e);
		return wrapped.add(e);
	}

	@Override
	public boolean remove(Object o) {
		Pure4J.immutable(o);
		return wrapped.remove(o);
	}

	@PureParameters(Enforcement.NOT_PURE)
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object object : c) {
			if (!wrapped.contains(object)) {
				return false;
			}
		}
		
		return true;
	}

	@PureParameters(Enforcement.NOT_PURE)
	@Override
	public boolean addAll(Collection<? extends K> c) {
		boolean result = false;
		for (K k : c) {
			Pure4J.immutable(k);
			result = wrapped.add(k) || result;
		}
		
		return result;
		
	}

	@PureParameters(Enforcement.NOT_PURE)
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for (Object k : c) {
			result = wrapped.remove(k) || result;
		}
		
		return result;
	}

	@Pure(Enforcement.FORCE)
	@PureParameters(Enforcement.NOT_PURE)
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		for (Iterator<K> iterator = this.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (!c.contains(object)) {
				iterator.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public void clear() {
		wrapped.clear();
	}

	@Override
	public int hashCode() {
		return Hasher.hashOrdered(wrapped);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof List) {
			Collection<?> ma = (Collection<?>) obj;
			if (ma.size() != this.size() || ma.hashCode() != this.hashCode())
				return false;
			for (Iterator<?> i1 = ((List<?>) this).iterator(), i2 = ma.iterator(); i1
					.hasNext();) {
				if (!Util.equals(i1.next(), i2.next()))
					return false;
			}
			return true;
		}
		 
		return false;
	}

	@Override
	public String toString() {
		return ToStringFunctions.toString(wrapped);
	}

	@Override
	public ISeq<K> seq() {
		return new IterableSeq<>(this);
	}

}