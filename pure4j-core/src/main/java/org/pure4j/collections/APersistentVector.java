/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Dec 18, 2007 */

package org.pure4j.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public abstract class APersistentVector<K> implements
		IPersistentVector<K>, Iterable<K>, List<K>, RandomAccess, Comparable<IPersistentVector<K>>,
		Serializable {

	private static final long serialVersionUID = 3143509526367951707L;
	int _hasheq = -1;

	public String toString() {
		return ToStringFunctions.toString(this);
	}

	public ISeq<K> seq() {
		if (count() > 0)
			return new Seq<K>(this, 0);
		return null;
	}

	public ISeq<K> rseq() {
		if (count() > 0)
			return new RSeq<K>(this, count() - 1);
		return null;
	}

	static <E> boolean doEquals(IPersistentVector<E> v, Object obj) {
		if (obj instanceof IPersistentVector) {
			IPersistentVector<?> ov = (IPersistentVector<?>) obj;
			if (ov.count() != v.count())
				return false;
			for (int i = 0; i < v.count(); i++) {
				if (!Util.equals(v.nth(i), ov.nth(i)))
					return false;
			}
			return true;
		} else if (obj instanceof List) {
			Collection<?> ma = (Collection<?>) obj;
			if (ma.size() != v.count() || ma.hashCode() != v.hashCode())
				return false;
			for (Iterator<?> i1 = ((List<?>) v).iterator(), i2 = ma.iterator(); i1
					.hasNext();) {
				if (!Util.equals(i1.next(), i2.next()))
					return false;
			}
			return true;
		} else {
			if (!(obj instanceof Sequential))
				return false;
			ISeq<?> ms = PureCollections.seq(obj);
			for (int i = 0; i < v.count(); i++, ms = ms.next()) {
				if (ms == null || !Util.equals(v.nth(i), ms.first()))
					return false;
			}
			if (ms != null)
				return false;
		}

		return true;

	}
	
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		return doEquals(this, obj);
	}

	public int hashCode() {
		if (_hasheq == -1) {
			int n;
			int hash = 1;

			for (n = 0; n < count(); ++n) {
				hash = 31 * hash + Util.hash(nth(n));
			}

			_hasheq = Murmur3.mixCollHash(hash, n);
		}
		return _hasheq;
	}

	public K get(int index) {
		return nth(index);
	}

	public K nth(int i, K notFound) {
		if (i >= 0 && i < count())
			return nth(i);
		return notFound;
	}

	public K remove(int i) {
		throw new UnsupportedOperationException();
	}

	public int indexOf(Object o) {
		for (int i = 0; i < count(); i++)
			if (Util.equals(nth(i), o))
				return i;
		return -1;
	}

	public int lastIndexOf(Object o) {
		for (int i = count() - 1; i >= 0; i--)
			if (Util.equals(nth(i), o))
				return i;
		return -1;
	}

	public ListIterator<K> listIterator() {
		return listIterator(0);
	}

	public ListIterator<K> listIterator(final int index) {
		return new ListIterator<K>() {
			int nexti = index;

			public boolean hasNext() {
				return nexti < count();
			}

			public K next() {
				return nth(nexti++);
			}

			public boolean hasPrevious() {
				return nexti > 0;
			}

			public K previous() {
				return nth(--nexti);
			}

			public int nextIndex() {
				return nexti;
			}

			public int previousIndex() {
				return nexti - 1;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public void set(Object o) {
				throw new UnsupportedOperationException();
			}

			public void add(Object o) {
				throw new UnsupportedOperationException();
			}
		};
	}

	Iterator<K> rangedIterator(final int start, final int end) {
		return new Iterator<K>() {
			int i = start;

			public boolean hasNext() {
				return i < end;
			}

			public K next() {
				return nth(i++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public List<K> subList(int fromIndex, int toIndex) {
		return new APersistentVector.SubVector<K>(this, fromIndex, toIndex);
	}

	public K set(int i, K o) {
		throw new UnsupportedOperationException();
	}

	public void add(int i, K o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int i, Collection<? extends K> c) {
		throw new UnsupportedOperationException();
	}

	public Object invoke(Object arg1) {
		if (Util.isInteger(arg1))
			return nth(((Number) arg1).intValue());
		throw new IllegalArgumentException("Key must be integer");
	}

	public Iterator<K> iterator() {
		// todo - something more efficient
		return new Iterator<K>() {
			int i = 0;

			public boolean hasNext() {
				return i < count();
			}

			public K next() {
				return nth(i++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public K peek() {
		if (count() > 0)
			return nth(count() - 1);
		return null;
	}

	public boolean containsKey(Object key) {
		if (!(Util.isInteger(key)))
			return false;
		int i = ((Number) key).intValue();
		return i >= 0 && i < count();
	}

	public Object valAt(Object key, Object notFound) {
		if (Util.isInteger(key)) {
			int i = ((Number) key).intValue();
			if (i >= 0 && i < count())
				return nth(i);
		}
		return notFound;
	}

	public Object valAt(Object key) {
		return valAt(key, null);
	}

	// java.util.Collection implementation

	public Object[] toArray() {
		Object[] ret = new Object[count()];
		for (int i = 0; i < count(); i++)
			ret[i] = nth(i);
		return ret;
	}

	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends K> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) PureCollections.seqToNewArray(seq(), a);
	}

	public int size() {
		return count();
	}

	public boolean isEmpty() {
		return count() == 0;
	}

	public boolean contains(Object o) {
		for (ISeq<K> s = seq(); s != null; s = s.next()) {
			if (Util.equals(s.first(), o))
				return true;
		}
		return false;
	}

	public int length() {
		return count();
	}

	@Override
	public int compareTo(IPersistentVector<K> v) {
		if (count() < v.count())
			return -1;
		else if (count() > v.count())
			return 1;
		for (int i = 0; i < count(); i++) {
			int c = Util.compare(nth(i), v.nth(i));
			if (c != 0)
				return c;
		}
		return 0;
	}

	static class Seq<K> extends ASeq<K> implements IndexedSeq<K> {
		// todo - something more efficient
		final IPersistentVector<K> v;
		final int i;

		public Seq(IPersistentVector<K> v, int i) {
			this.v = v;
			this.i = i;
		}

		public K first() {
			return v.nth(i);
		}

		public ISeq<K> next() {
			if (i + 1 < v.count())
				return new APersistentVector.Seq<K>(v, i + 1);
			return null;
		}

		public int index() {
			return i;
		}

		public int count() {
			return v.count() - i;
		}

	}

	public static class RSeq<K> extends ASeq<K> implements IndexedSeq<K>, Counted {
		final IPersistentVector<K> v;
		final int i;

		public RSeq(IPersistentVector<K> vector, int i) {
			this.v = vector;
			this.i = i;
		}

		public K first() {
			return v.nth(i);
		}

		public ISeq<K> next() {
			if (i > 0)
				return new APersistentVector.RSeq<K>(v, i - 1);
			return null;
		}

		public int index() {
			return i;
		}

		public int count() {
			return i + 1;
		}
	}

	public static class SubVector<K> extends APersistentVector<K> {
		
		public final IPersistentVector<K> v;
		public final int start;
		public final int end;
		
		public SubVector(IPersistentVector<K> v, int start,
				int end) {
			if (v instanceof APersistentVector.SubVector) {
				APersistentVector.SubVector<K> sv = (APersistentVector.SubVector<K>) v;
				start += sv.start;
				end += sv.start;
				v = sv.v;
			}
			this.v = v;
			this.start = start;
			this.end = end;
		}

		public Iterator<K> iterator() {
			if (v instanceof APersistentVector) {
				return ((APersistentVector<K>) v).rangedIterator(start, end);
			}
			return super.iterator();
		}

		public K nth(int i) {
			if ((start + i >= end) || (i < 0))
				throw new IndexOutOfBoundsException();
			return v.nth(start + i);
		}

		public IPersistentVector<K> assocN(int i, K val) {
			if (start + i > end)
				throw new IndexOutOfBoundsException();
			else if (start + i == end)
				return cons(val);
			return new SubVector<K>(v.assocN(start + i, val), start, end);
		}

		public int count() {
			return end - start;
		}

		public IPersistentVector<K> cons(K o) {
			return new SubVector<K>(v.assocN(end, o), start, end + 1);
		}

		@SuppressWarnings("unchecked")
		public IPersistentCollection<K> empty() {
			return (IPersistentCollection<K>) PersistentVector.EMPTY;
		}

		@SuppressWarnings("unchecked")
		public IPersistentStack<K> pop() {
			if (end - 1 == start) {
				return (IPersistentStack<K>) PersistentVector.EMPTY;
			}
			return new SubVector<K>(v, start, end - 1);
		}
	}
}
