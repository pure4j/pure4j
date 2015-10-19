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

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;



/**
 * Simple implementation of persistent map on an array.
 * 
 * Note that instances of this class are constant values i.e. add/remove etc
 * return new values
 * 
 * Copies array on every change, so only appropriate for _very_small_ maps
 * 
 * null keys and values are ok, but you won't be able to distinguish a null
 * value via valAt - use contains/entryAt
 * 
 * @param <K> Key
 * @param <V> value
 */

public class PersistentArrayMap<K, V> extends APersistentMap<K, V> implements IMapIterable<K, V> {

	@IgnoreImmutableTypeCheck
	private final Object[] array;
	static final int HASHTABLE_THRESHOLD = 16;

	private static final PersistentArrayMap<Object, Object> EMPTY = new PersistentArrayMap<Object, Object>();

	@Pure(Enforcement.FORCE)
	public PersistentArrayMap(Map<? extends K, ? extends V> other) {
		this.array = new Object[other.size() * 2];
		int pos = 0;
		for (Entry<? extends K, ? extends V> entry : other.entrySet()) {
			Pure4J.immutable(entry.getKey(), entry.getValue());
			this.array[pos++] = entry.getKey();
			this.array[pos++] = entry.getValue();
		}
	}
	
	public PersistentArrayMap(IPersistentMap<K, V> other) {
		this((Map<K, V>) other);
	}
	
	public PersistentArrayMap(ISeq<Entry<K, V>> seq) {
		this.array = new Object[seq.size() * 2];
		int pos = 0;
		for (Entry<K, V> entry : seq) {
			Pure4J.immutable(entry.getKey(), entry.getValue());
			this.array[pos++] = entry.getKey();
			this.array[pos++] = entry.getValue();
		}
	}
	
	

	public PersistentArrayMap() {
		this.array = new Object[] {};
	}
	
	

	@SuppressWarnings("unchecked")
	private IPersistentMap<K, V> createHT(Object[] init) {
		return (IPersistentMap<K, V>) new PersistentHashMap<K,V>((K[]) init);
	}

	/**
	 * Creates a private copy of init.
	 *
	 * @param init
	 *            {key1,val1,key2,val2,...}
	 */
	@SafeVarargs
	@Pure(Enforcement.FORCE)
	public PersistentArrayMap(K... init) {
		this(init, true);
	}
	
	@Pure(Enforcement.FORCE) // makes copy and is private.
	private PersistentArrayMap(Object[] init, boolean makeCopy) {
		Pure4J.immutableArray(init);
		if (makeCopy) {
			init = Arrays.copyOf(init, init.length);
		}
		this.array = init;
	}

	public int size() {
		return array.length / 2;
	}

	public boolean containsKey(Object key) {
		Pure4J.immutable(key);
		return indexOf(key) >= 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IMapEntry<K, V> entryAt(Object key) {
		Pure4J.immutable(key);
		int i = indexOf(key);
		if (i >= 0)
			return new MapEntry(array[i], array[i + 1]);
		return null;
	}

	@Pure(Enforcement.FORCE)	// performs array copy on own array
	public IPersistentMap<K, V> assocEx(K key, V val) {
		Pure4J.immutable(key, val);
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

	@Pure(Enforcement.FORCE)	// performs array copy on own array
	public IPersistentMap<K, V> assoc(K key, V val) {
		Pure4J.immutable(key, val);
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

	@Pure(Enforcement.FORCE)	// private array manipulation
	public IPersistentMap<K, V> without(Object key) {
		Pure4J.immutable(key);
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
	@Pure
	public static <K, V> PersistentArrayMap<K, V> emptyMap() {
		return (PersistentArrayMap<K, V>) EMPTY;
	}

	@SuppressWarnings("unchecked")
	final public V get(Object key, V notFound) {
		Pure4J.immutable(key, notFound);
		int i = indexOf(key);
		if (i >= 0)
			return (V) array[i + 1];
		return notFound;
	}

	public V get(Object key) {
		Pure4J.immutable(key);
		return get(key, null);
	}

	public int capacity() {
		return size();
	}

	@Pure(Enforcement.FORCE)
	private int indexOfObject(Object key) {
		for (int i = 0; i < array.length; i += 2) {
			if (Util.equals(key, array[i]))
				return i;
		}
		return -1;
	}

	@Pure(Enforcement.FORCE)	// since it's private, and doesn't modify
	private int indexOf(Object key) {
		return indexOfObject(key);
	}

	@Pure
	static boolean equalKey(Object k1, Object k2) {
		return Util.equals(k1, k2);
	}

	public Iterator<Entry<K,V>> iterator() {
		return new Iter<K, V>(array);
	}

	public IPureIterator<K> keyIterator() {
		return KeySeq.create(seq()).iterator();
	}

	public IPureIterator<V> valIterator() {
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

		public int size() {
			return (array.length - i) / 2;
		}
	}

	static class Iter<K, V> implements IPureIterator<Entry<K, V>> {
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

	public ITransientMap<K, V> asTransient() {
		return new TransientHashMap<K, V>(this); 
	}
}
