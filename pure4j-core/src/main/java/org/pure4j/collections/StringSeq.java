/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Dec 6, 2007 */

package org.pure4j.collections;

public class StringSeq extends ASeq<Character> {
	public final CharSequence s;
	public final int i;

	static public StringSeq create(CharSequence s) {
		if (s.length() == 0)
			return null;
		return new StringSeq(s, 0);
	}

	StringSeq(CharSequence s, int i) {
		this.s = s;
		this.i = i;
	}

	public Character first() {
		return Character.valueOf(s.charAt(i));
	}

	public ISeq<Character> next() {
		if (i + 1 < s.length())
			return new StringSeq(s, i + 1);
		return null;
	}

	public int size() {
		return s.length() - i;
	}
}
