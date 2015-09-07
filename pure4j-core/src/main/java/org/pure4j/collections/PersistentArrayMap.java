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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;



/**
 * Simple implementation of persistent map on an array
 * <p/>
 * Note that instances of this class are constant values i.e. add/remove etc
 * return new values
 * <p/>
 * Copies array on every change, so only appropriate for _very_small_ maps
 * <p/>
 * null keys and values are ok, but you won't be able to distinguish a null
 * value via valAt - use contains/entryAt
 */

public class PersistentArrayMap<K, V> extends APersistentMap<K, V> implements IMapIterable<K, V> {

	final Object[] array;
	static final int HASHTABLE_THRESHOLD = 16;

	private static final PersistentArrayMap<Object, Object> EMPTY = new PersistentArrayMap<Object, Object>();

	@SuppressWarnings("unchecked")
	static public <K, V> IPersistentMap<K, V> create(Map<K, V> other) {
		ITransientMap<K, V> ret = (ITransientMap<K, V>) EMPTY.asTransient();
		for (Entry<K, V> e : other.entrySet()) {
			ret = ret.assoc(e.getKey(), e.getValue());
		}
		return ret.persistent();
	}

	protected PersistentArrayMap() {
		this.array = new Object[] {};
	}

	IPersistentMap<K, V> createHT(Object[] init) {
		return PersistentHashMap.create(init);
	}

	@SuppressWarnings("unchecked")
	static public <K> PersistentArrayMap<K, K> createWithCheck(K... init) {
		for (int i = 0; i < init.length; i += 2) {
			for (int j = i + 2; j < init.length; j += 2) {
				if (equalKey(init[i], init[j]))
					throw new IllegalArgumentException("Duplicate key: "
							+ init[i]);
			}
		}
		return new PersistentArrayMap<K, K>(init, true);
	}

	@SuppressWarnings("unchecked")
	static public <K> PersistentArrayMap<K, K> createAsIfByAssoc(K[] init) {
		// If this looks like it is doing busy-work, it is because it
		// is achieving these goals: O(n^2) run time like
		// createWithCheck(), never modify init arg, and only
		// allocate memory if there are duplicate keys.
		int n = 0;
		for (int i = 0; i < init.length; i += 2) {
			boolean duplicateKey = false;
			for (int j = 0; j < i; j += 2) {
				if (equalKey(init[i], init[j])) {
					duplicateKey = true;
					break;
				}
			}
			if (!duplicateKey)
				n += 2;
		}
		if (n < init.length) {
			// Create a new shorter array with unique keys, and
			// the last value associated with each key. To behave
			// like assoc, the first occurrence of each key must
			// be used, since its metadata may be different than
			// later equal keys.
			Object[] nodups = new Object[n];
			int m = 0;
			for (int i = 0; i < init.length; i += 2) {
				boolean duplicateKey = false;
				for (int j = 0; j < m; j += 2) {
					if (equalKey(init[i], nodups[j])) {
						duplicateKey = true;
						break;
					}
				}
				if (!duplicateKey) {
					int j;
					for (j = init.length - 2; j >= i; j -= 2) {
						if (equalKey(init[i], init[j])) {
							break;
						}
					}
					nodups[m] = init[i];
					nodups[m + 1] = init[j + 1];
					m += 2;
				}
			}
			if (m != n)
				throw new IllegalArgumentException("Internal error: m=" + m);
			init = (K[]) nodups;
		}
		return new PersistentArrayMap<K, K>(init, true);
	}

	/**
	 * Creates a private copy of init.
	 *
	 * @param init
	 *            {key1,val1,key2,val2,...}
	 */
	public PersistentArrayMap(Object[] init) {
		this(init, true);
	}
	
	private PersistentArrayMap(Object[] init, boolean makeCopy) {
		if (makeCopy) {
			init = Arrays.copyOf(init, init.length);
		}
		this.array = init;
	}

	public int count() {
		return array.length / 2;
	}

	public boolean containsKey(Object key) {
		return indexOf(key) >= 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IMapEntry<K, V> entryAt(Object key) {
		int i = indexOf(key);
		if (i >= 0)
			return new MapEntry(array[i], array[i + 1]);
		return null;
	}

	public IPersistentMap<K, V> assocEx(K key, V val) {
		int i = indexOf(key);
		Object[] newArray;
		if (i >= 0) {
			throw Util.runtimeException("Key already present");
		} else // didn't have key, grow
		{
			if (array.length > HASHTABLE_THRESHOLD)
				return createHT(array).assocEx(key, val);
			newArray = new Object[array.length + 2];
			if (array.length > 0)
				System.arraycopy(array, 0, newArray, 2, array.length);
			newArray[0] = key;
			newArray[1] = val;
		}
		return new PersistentArrayMap<K,V>(newArray, false);
	}

	public IPersistentMap<K, V> assoc(K key, V val) {
		int i = indexOf(key);
		Object[] newArray;
		if (i >= 0) // already have key, same-sized replacement
		{
			if (array[i + 1] == val) // no change, no op
				return this;
			newArray = array.clone();
			newArray[i + 1] = val;
		} else // didn't have key, grow
		{
			if (array.length > HASHTABLE_THRESHOLD)
				return ((IPersistentMap<K, V>) createHT(array)).assoc(key, val);
			newArray = new Object[array.length + 2];
			if (array.length > 0)
				System.arraycopy(array, 0, newArray, 0, array.length);
			newArray[newArray.length - 2] = key;
			newArray[newArray.length - 1] = val;
		}
		return new PersistentArrayMap<K,V>(newArray, false);
	}

	public IPersistentMap<K, V> without(Object key) {
		int i = indexOf(key);
		if (i >= 0) // have key, will remove
		{
			int newlen = array.length - 2;
			if (newlen == 0)
				return empty();
			Object[] newArray = new Object[newlen];
			for (int s = 0, d = 0; s < array.length; s += 2) {
				if (!equalKey(array[s], key)) // skip removal key
				{
					newArray[d] = array[s];
					newArray[d + 1] = array[s + 1];
					d += 2;
				}
			}
			return new PersistentArrayMap<K, V>(newArray, false);
		}
		// don't have key, no op
		return this;
	}

	@SuppressWarnings("unchecked")
	public IPersistentMap<K, V> empty() {
		return (IPersistentMap<K, V>) EMPTY;
	}
	
	@SuppressWarnings("unchecked")
	public static <K, V> PersistentArrayMap<K, V> emptyMap() {
		return (PersistentArrayMap<K, V>) EMPTY;
	}

	@SuppressWarnings("unchecked")
	final public V valAt(Object key, V notFound) {
		int i = indexOf(key);
		if (i >= 0)
			return (V) array[i + 1];
		return notFound;
	}

	public V valAt(Object key) {
		return valAt(key, null);
	}

	public int capacity() {
		return count();
	}

	private int indexOfObject(Object key) {
		for (int i = 0; i < array.length; i += 2) {
			if (Util.equals(key, array[i]))
				return i;
		}
		return -1;
	}

	private int indexOf(Object key) {
		return indexOfObject(key);
	}

	static boolean equalKey(Object k1, Object k2) {
		return Util.equals(k1, k2);
	}

	public Iterator<Entry<K,V>> iterator() {
		return new Iter<K, V>(array);
	}

	public Iterator<K> keyIterator() {
		return KeySeq.create(seq()).iterator();
	}

	public Iterator<V> valIterator() {
		return ValSeq.create(seq()).iterator();
	}

	public ISeq<Entry<K, V>> seq() {
		if (array.length > 0)
			return new Seq<K, V>(array, 0);
		return null;
	}

	static class Seq<K,V> extends ASeq<Entry<K,V>> implements Counted {
		final Object[] array;
		final int i;

		Seq(Object[] array, int i) {
			this.array = array;
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		public Entry<K,V> first() {
			return new MapEntry<K,V>((K)array[i], (V)array[i + 1]);
		}

		public ISeq<Entry<K, V>> next() {
			if (i + 2 < array.length)
				return new Seq<K, V>(array, i + 2);
			return null;
		}

		public int count() {
			return (array.length - i) / 2;
		}
	}

	static class Iter<K, V> implements Iterator<Entry<K, V>> {
		Object[] array;
		int i;

		// for iterator
		Iter(Object[] array) {
			this(array, -2);
		}

		// for entryAt
		Iter(Object[] array, int i) {
			this.array = array;
			this.i = i;
		}

		public boolean hasNext() {
			return i < array.length - 2;
		}

		@SuppressWarnings("unchecked")
		public Entry<K, V> next() {
			i += 2;
			return new MapEntry<K,V>((K) array[i], (V) array[i + 1]);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public TransientArrayMap<K, V> asTransient() {
		return new TransientArrayMap<K, V>(array);
	}

	static final class TransientArrayMap<K, V> extends ATransientMap<K, V> {
		volatile int len;
		final Object[] array;
		volatile Thread owner;

		public TransientArrayMap(Object[] array) {
			this.owner = Thread.currentThread();
			this.array = new Object[Math.max(HASHTABLE_THRESHOLD, array.length)];
			System.arraycopy(array, 0, this.array, 0, array.length);
			this.len = array.length;
		}

		private int indexOf(Object key) {
			for (int i = 0; i < len; i += 2) {
				if (equalKey(array[i], key))
					return i;
			}
			return -1;
		}

		@SuppressWarnings("unchecked")
		ITransientMap<K, V> doAssoc(K key, V val) {
			int i = indexOf(key);
			if (i >= 0) // already have key,
			{
				if (array[i + 1] != val) // no change, no op
					array[i + 1] = val;
			} else // didn't have key, grow
			{
				if (len >= array.length)
					return ((PersistentHashMap<K, V>) PersistentHashMap.create(array)).asTransient()
							.assoc(key, val);
				array[len++] = key;
				array[len++] = val;
			}
			return this;
		}

		ITransientMap<K, V> doWithout(Object key) {
			int i = indexOf(key);
			if (i >= 0) // have key, will remove
			{
				if (len >= 2) {
					array[i] = array[len - 2];
					array[i + 1] = array[len - 1];
				}
				len -= 2;
			}
			return this;
		}

		@SuppressWarnings("unchecked")
		V doValAt(Object key, V notFound) {
			int i = indexOf(key);
			if (i >= 0)
				return (V) array[i + 1];
			return notFound;
		}

		int doCount() {
			return len / 2;
		}

		IPersistentMap<K, V> doPersistent() {
			ensureEditable();
			owner = null;
			Object[] a = new Object[len];
			System.arraycopy(array, 0, a, 0, len);
			return new PersistentArrayMap<K, V>(a);
		}

		void ensureEditable() {
			if (owner == null)
				throw new IllegalAccessError(
						"Transient used after persistent! call");
		}
	}
}
