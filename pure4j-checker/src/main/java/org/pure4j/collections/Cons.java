/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Mar 25, 2006 11:01:29 AM */

package org.pure4j.collections;

import java.io.Serializable;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;

final public class Cons<K> extends ASeq<K> implements Serializable {

	private static final long serialVersionUID = -5751064091837549225L;

	@IgnoreImmutableTypeCheck
	private final K _first;
	private final ISeq<K> _more;

	public Cons(K first, ISeq<K> _more) {
		Pure4J.immutable(first);
		this._first = first;
		this._more = _more;
	}

	public K first() {
		return _first;
	}

	public ISeq<K> next() {
		return more();
	}

	public ISeq<K> more() {
		if (_more == null)
			return PersistentList.emptySeq();
		return _more;
	}

	public int size() {
		return 1 + _more.size();
	}
}
