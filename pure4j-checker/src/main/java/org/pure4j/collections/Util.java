/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Apr 19, 2008 */

package org.pure4j.collections;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureNotThreadsafe;

public class Util {

	@Pure
	@SuppressWarnings("unchecked")
	@PureNotThreadsafe(Enforcement.NOT_PURE)
	static public int compare(Object k1, Object k2) {
		if (k1 == k2)
			return 0;
		if (k1 != null) {
			if (k2 == null)
				return 1;
			return ((Comparable<Object>) k1).compareTo(k2);
		}
		return -1;
	}

	@Pure(Enforcement.FORCE)
	static public int hash(Object o) {
		Pure4J.immutable(o);
		if (o == null)
			return 0;
		return o.hashCode();
	}


	@Pure
	@PureNotThreadsafe(Enforcement.NOT_PURE)
	static public RuntimeException runtimeException(String s) {
		return new RuntimeException(s);
	}
	
	@Pure
	@PureNotThreadsafe(Enforcement.NOT_PURE)
	static public boolean equals(Object k1, Object k2){
		if(k1 == k2)
			return true;
		return k1 != null && k1.equals(k2);
	}
}
