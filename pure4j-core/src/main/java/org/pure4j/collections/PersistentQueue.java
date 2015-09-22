/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package org.pure4j.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreNonImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

//import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * conses onto rear, peeks/pops from front See Okasaki's Batched Queues This
 * differs in that it uses a PersistentVector as the rear, which is in-order, so
 * no reversing or suspensions required for persistent use
 */

public class PersistentQueue<K> implements IPersistentList<K>, Collection<K>, Counted {

	final private static PersistentQueue<Object> EMPTY = new PersistentQueue<Object>(0,null, null);

	final int cnt;
	final ISeq<K> f;
	final PersistentVector<K> r;
	// static final int INITIAL_REAR_SIZE = 4;
	
	@IgnoreNonImmutableTypeCheck
	private int _hasheq = -1;

	PersistentQueue(int cnt, ISeq<K> f, PersistentVector<K> r) {
		this.cnt = cnt;
		this.f = f;
		this.r = r;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Sequential))
			return false;
		ISeq<K> ms = PureCollections.seq(obj);
		for (ISeq<K> s = seq(); s != null; s = s.next(), ms = ms.next()) {
			if (ms == null || !Util.equals(s.first(), ms.first()))
				return false;
		}
		return ms == null;

	}
	
	public int hashCode() {
		if (_hasheq == -1) {
			_hasheq = Murmur3.hashOrdered(this);
		}
		return _hasheq;
	}

	@SuppressWarnings("unchecked")
	public K peek() {
		return (K) PureCollections.first(f);
	}

	public PersistentQueue<K> pop() {
		if (f == null) // hmmm... pop of empty queue -> empty queue?
			return this;
		// throw new IllegalStateException("popping empty queue");
		ISeq<K> f1 = f.next();
		PersistentVector<K> r1 = r;
		if (f1 == null) {
			f1 = PureCollections.seq(r);
			r1 = null;
		}
		return new PersistentQueue<K>(cnt - 1, f1, r1);
	}

	public int count() {
		return cnt;
	}

	@SuppressWarnings("unchecked")
	public ISeq<K> seq() {
		if (f == null)
			return null;
		return new Seq<K>(f, (ISeq<K>) PureCollections.seq(r));
	}

	@SuppressWarnings("unchecked")
	public PersistentQueue<K> cons(K o) {
		Pure4J.immutable(o);
		if (f == null) // empty
			return new PersistentQueue<K>(cnt + 1,  PureCollections.list(o), null);
		else {
			PersistentVector<K> rr = (PersistentVector<K>) ((r != null) ? r : PersistentVector.emptyVector());
			return new PersistentQueue<K>(cnt + 1, f, rr.cons(o));
		}
	}

	@SuppressWarnings("unchecked")
	public PersistentQueue<K> empty() {
		return (PersistentQueue<K>) EMPTY;
	}
	
	@SuppressWarnings("unchecked")
	public static <K> PersistentQueue<K> emptyQueue() {
		return (PersistentQueue<K>) EMPTY;
	}

	static class Seq<K> extends ASeq<K> {
		final ISeq<K> f;
		final ISeq<K> rseq;

		Seq(ISeq<K> f, ISeq<K> rseq) {
			this.f = f;
			this.rseq = rseq;
		}

		public K first() {
			return f.first();
		}

		public ISeq<K> next() {
			ISeq<K> f1 = f.next();
			ISeq<K> r1 = rseq;
			if (f1 == null) {
				if (rseq == null)
					return null;
				f1 = rseq;
				r1 = null;
			}
			return new Seq<K>(f1, r1);
		}

		public int count() {
			return PureCollections.count(f) + PureCollections.count(rseq);
		}
	}

	// java.util.Collection implementation

	public Object[] toArray() {
		return PureCollections.seqToArray(seq());
	}

	public boolean add(Object o) {
		Pure4J.unsupported();
		return false;
	}

	public boolean remove(Object o) {
		Pure4J.unsupported();
		return false;
	}

	public boolean addAll(Collection<? extends K> c) {
		Pure4J.unsupported();
		return false;
	}

	public void clear() {
		Pure4J.unsupported();
	}

	public boolean retainAll(Collection<?> c) {
		Pure4J.unsupported();
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		Pure4J.unsupported();
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		Pure4J.immutable(c);
		for (Object o : c) {
			if (contains(o))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Pure(Enforcement.FORCE)
	public <E> E[] toArray(E[] a) {
		return (E[]) PureCollections.seqToNewArray(seq(), a);
	}

	public int size() {
		return count();
	}

	public boolean isEmpty() {
		return count() == 0;
	}

	public boolean contains(Object o) {
		Pure4J.immutable(o);
		for (ISeq<K> s = seq(); s != null; s = s.next()) {
			if (Util.equals(s.first(), o))
				return true;
		}
		return false;
	}

	public Iterator<K> iterator() {
		return new IPureIterator<K>() {
			private ISeq<K> fseq = f;
			private final Iterator<K> riter = r != null ? r.iterator() : null;

			public boolean hasNext() {
				return ((fseq != null && fseq.seq() != null) || (riter != null && riter
						.hasNext()));
			}

			public K next() {
				if (fseq != null) {
					K ret = fseq.first();
					fseq = fseq.next();
					return ret;
				} else if (riter != null && riter.hasNext())
					return riter.next();
				else
					throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public String toString() {
		return ToStringFunctions.toString(this);
	}
	

}
