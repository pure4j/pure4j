/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Mar 3, 2008 */

package org.pure4j.collections;



public abstract class ATransientSet<K> implements ITransientSet<K> {

	volatile ITransientMap<K,K> impl;

	ATransientSet(ITransientMap<K, K> impl) {
		this.impl = impl;
	}
	
	public int count() {
		return impl.count();
	}

	public ITransientSet<K> conj(K val) {
		ITransientMap<K, K> m = impl.assoc(val, val);
		if (m != impl) this.impl = m;
		return this;
	}

	public boolean contains(Object key) {
		return null != impl.valAt(key, null);
	}

	public ITransientSet<K> disjoin(Object key)  {
		ITransientMap<K, K> m = impl.without(key);
		if (m != impl) this.impl = m;
		return this;
	}

	public K get(Object key) {
		return impl.valAt(key);
	}
	
}
