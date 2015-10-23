package org.pure4j.collections;

import java.util.LinkedList;

import org.pure4j.Pure4J;

public class TransientQueue<T> extends ATransientList<T> implements ITransientQueue<T> {
	
	public TransientQueue() {
		super(new LinkedList<T>());
	}
	
	public TransientQueue(ISeq<T> s) {
		this();
		for (T t : s) {
			add(t);
		}
	}

	@Override
	public IPersistentCollection<T> persistent() {
		return new PersistentQueue<T>(this.size(), this.seq(), null);
	}

	@Override
	public boolean offer(T e) {
		Pure4J.immutable(e);
		return wrapped.add(e);
	}

	@Override
	public T remove() {
		return Pure4J.returnImmutable(((LinkedList<T>)wrapped).remove());
	}

	@Override
	public T poll() {
		return Pure4J.returnImmutable(((LinkedList<T>)wrapped).poll());
	}

	@Override
	public T element() {
		return Pure4J.returnImmutable(((LinkedList<T>)wrapped).element());
	}

	@Override
	public T peek() {
		return Pure4J.returnImmutable(((LinkedList<T>)wrapped).peek());
	}

}
