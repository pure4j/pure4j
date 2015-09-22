/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jun 19, 2007 */

package org.pure4j.collections;

import java.util.NoSuchElementException;

import org.pure4j.annotations.mutable.MutableUnshared;

@MutableUnshared
public class SeqIterator<K> implements IPureIterator<K> {
	
	ISeq<K> next;

	public SeqIterator(ISeq<K> o) {
		next = o;
	}

	public boolean hasNext() {
		return next != null;
	}

	public K next() throws NoSuchElementException {
		K out = next.first();
		next = next.next();
		return out;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
