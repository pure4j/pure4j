package org.pure4j.collections;

import java.util.Collection;
import java.util.Iterator;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureInterface;

public abstract class ATransientCollection<K> implements ITransientCollection<K> {

	public ATransientCollection() {
		super();
	}
	
	protected abstract Collection<K> getWrapped();

	@Override
	public int size() {
		return getWrapped().size();
	}

	@Override
	public boolean isEmpty() {
		return getWrapped().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		Pure4J.immutable(o);
		return getWrapped().contains(o);
	}

	@Pure(Enforcement.NOT_PURE)
	@Override
	public Iterator<K> iterator() {
		return getWrapped().iterator();
	}

	@Pure(Enforcement.FORCE)
	@Override
	public Object[] toArray() {
		return getWrapped().toArray();
	}

	@Pure(Enforcement.FORCE)
	@Override
	public <T> T[] toArray(T[] a) {
		return getWrapped().toArray(a);
	}

	@Override
	public boolean add(K e) {
		Pure4J.immutable(e);
		return getWrapped().add(e);
	}

	@Override
	public boolean remove(Object o) {
		Pure4J.immutable(o);
		return getWrapped().remove(o);
	}

	@PureInterface(Enforcement.NOT_PURE)
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object object : c) {
			if (!getWrapped().contains(object)) {
				return false;
			}
		}
		
		return true;
	}

	@PureInterface(Enforcement.NOT_PURE)
	@Override
	public boolean addAll(Collection<? extends K> c) {
		boolean result = false;
		for (K k : c) {
			Pure4J.immutable(k);
			result = getWrapped().add(k) || result;
		}
		
		return result;
		
	}

	@PureInterface(Enforcement.NOT_PURE)
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for (Object k : c) {
			result = getWrapped().remove(k) || result;
		}
		
		return result;
	}

	@Pure(Enforcement.FORCE)
	@PureInterface(Enforcement.NOT_PURE)
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
		getWrapped().clear();
	}
	
}