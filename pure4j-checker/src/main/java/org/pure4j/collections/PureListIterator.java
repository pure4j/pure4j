package org.pure4j.collections;

import java.util.ListIterator;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.PureInterface;

/**
 * Should only be constructed with immutable contents in order to
 * ensure the {@link MutableUnshared} contract is met.
 * 
 * @author robmoffat
 *
 * @param <E>
 */
public final class PureListIterator<E> implements IPureListIterator<E>{
	
	private ListIterator<E> wraps;
	
	@PureInterface(Enforcement.NOT_PURE)
	public PureListIterator(ListIterator<E> wraps) {
		this.wraps = wraps;
	}

	@Override
	public boolean hasNext() {
		return wraps.hasNext();
	}

	@Override
	public E next() {
		return Pure4J.returnImmutable(wraps.next());
	}

	@Override
	public boolean hasPrevious() {
		return wraps.hasPrevious();
	}

	@Override
	public E previous() {
		return Pure4J.returnImmutable(wraps.previous());
	}

	@Override
	public int nextIndex() {
		return wraps.nextIndex();
	}

	@Override
	public int previousIndex() {
		return wraps.previousIndex();
	}

	@Override
	public void remove() {
		Pure4J.unsupported();
	}

	@Override
	public void set(E e) {
		Pure4J.unsupported();
	}

	@Override
	public void add(E e) {
		Pure4J.unsupported();
	}

}
