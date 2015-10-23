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

import java.util.Map.Entry;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public class MapEntry<K, V> extends AMapEntry<K, V> {
	
	final K _key;
	final V _val;

	public MapEntry(K key, V val) {
		Pure4J.immutable(key, val);
		this._key = key;
		this._val = val;
	}

	public K key() {
		return _key;
	}

	public V val() {
		return _val;
	}

	public K getKey() {
		return key();
	}

	public V getValue() {
		return val();
	}

	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException("Persistent Collections don't support changing map entries");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Entry<?,?>) {
			Entry<?,?> other = (Entry<?,?>) obj;
			if (_key == null) {
				if (other.getKey() != null)
					return false;
			} else if (!_key.equals(other.getKey()))
				return false;
			
			
			if (_val == null) {
				if (other.getValue() != null)
					return false;
			} else if (!_val.equals(other.getValue()))
				return false;
			
			return true;
		}
		return false;
	}
	
	

}
