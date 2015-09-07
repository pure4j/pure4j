/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich May 24, 2009 */

package org.pure4j.collections;

import java.io.Serializable;

public final class ArrayChunk<K> implements IChunk<K>, Serializable {

	final K[] array;
	final int off;
	final int end;

	public ArrayChunk(K[] array) {
		this(array, 0, array.length);
	}

	public ArrayChunk(K[] array, int off) {
		this(array, off, array.length);
	}

	public ArrayChunk(K[] array, int off, int end) {
		this.array = array;
		this.off = off;
		this.end = end;
	}

	public K nth(int i) {
		return array[off + i];
	}

	public K nth(int i, K notFound) {
		if (i >= 0 && i < count())
			return nth(i);
		return notFound;
	}

	public int count() {
		return end - off;
	}

	public IChunk<K> dropFirst() {
		if (off == end)
			throw new IllegalStateException("dropFirst of empty chunk");
		return new ArrayChunk<K>(array, off + 1, end);
	}
}
