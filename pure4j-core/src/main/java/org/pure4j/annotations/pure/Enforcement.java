package org.pure4j.annotations.pure;

import org.pure4j.annotations.immutable.ImmutableValue;


/**
 * Enforcement is a directive to the purity checker to indicate how to test for purity.
 * 
 * <ul>
 * <li><code>CHECKED</code>:  This is the default, and tells the checker to ensure the code is 
 * pure by enforcing the constraints described in {@link Pure}.
 * <li><code>FORCE</code>: This tells the checker to mark the method or constructor as pure, and skip the
 * testing of it.  Possibly useful for logging or other harmless, but side-effecting methods.  <b>Use with caution</b>.
 * <li><code>NOT_PURE</code>: Tells the checker that the code isn't pure.  This means the checker will skip testing of 
 * the method.  This is useful if you have declared <code>@Pure</code> at the class level, but some methods are not pure,
 * but in any case you want to generally be using the class as a pure class.  This is not acceptable as a class-level annotation.
 * </ul>
 * 
 * @see org.pure4j.annotations.immutable.ImmutableValue
 * @see org.pure4j.annotations.pure.Pure
 * 
 * @author robmoffat
 *
 */
public enum Enforcement {
	
	CHECKED, FORCE, NOT_PURE
}
