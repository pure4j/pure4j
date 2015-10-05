/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jul 17, 2009 */

package org.pure4j.collections;

import java.util.Collection;

import org.pure4j.annotations.mutable.MutableUnshared;

/**
 * ITransientCollection hierarchy defines classes which, in general are extensions of regular Java collections
 * classes but have a pure constructor (taking a Seq or an IPersistent something), and have the persistent() 
 * method below.
 * 
 * This means that you can use the existing Java collections classes as @MutableUnshareds.
 * 
 * @author robmoffat
 *
 * @param <K> Element type
 */
@MutableUnshared
public interface ITransientCollection<K> extends Collection<K> {

	IPersistentCollection<K> persistent();
	
	
	
}
