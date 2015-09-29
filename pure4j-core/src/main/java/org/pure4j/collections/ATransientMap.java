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

import java.util.Map;


abstract class ATransientMap<K, V> implements ITransientMap<K, V> {

	abstract void ensureEditable();
	abstract ITransientMap<K, V> doAssoc(K key, V val);
	abstract ITransientMap<K, V> doWithout(Object key);
	abstract V doValAt(Object key, V notFound);
	abstract int doCount();
	abstract IPersistentMap<K, V> doPersistent();

	public ITransientMap<K, V> conj(Entry<K, V> o) {
		ensureEditable();
		return assoc(o.getKey(), o.getValue());
	}

	public final V valAt(Object key) {
		return valAt(key, null);
	}

	public final ITransientMap<K,V> assoc(K key, V val) {
		ensureEditable();
		return doAssoc(key, val);
	}

	public final ITransientMap<K, V> without(Object key) {
		ensureEditable();
		return doWithout(key);
	}

	public final IPersistentMap<K, V> persistent() {
		ensureEditable();
		return doPersistent();
	}

	public final V valAt(Object key, V notFound) {
		ensureEditable();
		return doValAt(key, notFound);
	}

	public final int count() {
		ensureEditable();
		return doCount();
	}
	@Override
	public int size() {
		return count();
	}
	@Override
	public boolean isEmpty() {
		return count() == 0;
	}
	
	@Override
	public V get(Object key) {
		return doValAt(key, null);
	}
	@Override
	public V put(K key, V value) {
		doAssoc(key, value);
		return value;
	}
	@Override
	public V remove(Object key) {
		V value = doValAt(key, null);
		doWithout(key);
		return value;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> elem : m.entrySet()) {
			doAssoc(elem.getKey(), elem.getValue());
		}
	}

	
}
