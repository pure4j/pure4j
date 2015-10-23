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

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Enumeration;

public class EnumerationSeq<K> extends ASeq<K> {

	final Enumeration<K> iter;
	final State state;

	static class State {
		volatile Object val;
		volatile Object _rest;
	}

	private static <K> EnumerationSeq<K> create(Enumeration<K> iter) {
		if (iter.hasMoreElements())
			return new EnumerationSeq<K>(iter);
		return null;
	}

	public EnumerationSeq(Enumeration<K> iter) {
		this(iter,new State());
		this.state.val = state;
		this.state._rest = state;
	}

	EnumerationSeq(Enumeration<K> iter, State state) {
		this.iter = iter;
		this.state = state;
	}

	@SuppressWarnings("unchecked")
	public K first() {
		if (state.val == state)
			synchronized (state) {
				if (state.val == state)
					state.val = iter.nextElement();
			}
		return (K) state.val;
	}

	@SuppressWarnings("unchecked")
	public ISeq<K> next() {
		if (state._rest == state)
			synchronized (state) {
				if (state._rest == state) {
					first();
					state._rest = create(iter);
				}
			}
		return (ISeq<K>) state._rest;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		throw new NotSerializableException(getClass().getName());
	}

}
