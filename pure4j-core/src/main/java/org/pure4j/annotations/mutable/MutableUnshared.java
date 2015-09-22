package org.pure4j.annotations.mutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;

/**
 * Declares that instances of this class contain mutable state, which, in order 
 * to preserve the no-side-effects clause of the purity contract, will not be shared with 
 * other classes during pure method calls.
 * 
 * <p>The typical use case for objects marked with this annotation is likely to be collectors, iterators, 
 * enumerations and some types of inner class, where the scope of use is limited to within a single pure 
 * method.
 * 
 * <h3>Contract</h3>
 * 
 * <ul>
 * <li>Public / Package accessible fields of the class must be immutable (see: {@link ImmutableValue} for what qualifies).
 * <li>Public / Package accessible methods must only receive immutable parameters or return immutable values
 * as results ). 
 * <li>Where the interface restricts the arguments of the method, the <code>Pure4J.immutable()</code> function must
 * be used to test the parameters for immutability at the start of the method.
 * </ul>
 * 
 * @see {@link Pure4J} immutable method.
 * @author robmoffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MutableUnshared {

}
