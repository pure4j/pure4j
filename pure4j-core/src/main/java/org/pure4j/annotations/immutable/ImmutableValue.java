package org.pure4j.annotations.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pure4j.annotations.pure.Pure;

/**
 * Declares that instances of this class are immutable and are value objects.
 * 
 * That is, In object-oriented and functional programming:
 * 
 * <ul>
 * <li><b>An immutable object</b> is an object whose state cannot be modified
 * after it is created.
 * <li>A <b>Value object</b> is one where the identity of the object is defined
 * by the state (i.e. fields) of the object itself.
 * </ul>
 * 
 * <h3>Immutability Contract</h3>
 * 
 * <p>
 * This class declares the immutability contract, which is then checked by the
 * JPure compile-time checker.
 * 
 * <ul>
 * <li>The fields on the class are <code>final</code>, and therefore unalterable
 * once set by the constructor.
 * <li>The fields must only contain other immutable value objects/primitives (see below).
 * <li><strike>Concrete classes inheriting or declaring this annotation must be
 * <code>final</code></strike>.
 * <li>Any generic type parameters supplied to an <code>ImmutableValue</code> annotated
 * class must also be immutable value types.
 * </ul>
 * 
 * <p>
 * Deviations from these rules will be reported and cause failures in your
 * build.
 * 
 * <h3>Value Contract</h3>
 * 
 * <p>
 * <ul>
 * <li>
 * Any instance (i.e. non-static) methods on the class must be pure. (see {@link Pure} for what
 * this entails).
 * </ul>
 * 
 * 
 * <p>
 * In normal operation this will therefore mean that
 * <code>hashCode</code> and <code>toString</code> will need to be implemented,
 * as by default they use built-in methods on the <code>Object</code> class
 * which rely on the object's identity, which are determined by object creation (are therefore impure).
 * 
 * <p>If these methods are not implemented in with pure functions, this will be
 * reported and cause failures in your build.
 * <h3>What Qualifies As An Immutable Value?</h3>
 * <ul>
 * <p><li><code>String</code>s.
 * <li>Java primitives and their wrapper classes.
 * <li>Instances of other classes labelled <code>@ImmutableValue</code>. 
 * <li>BigDecimal, BigInteger
 * <li>Anything extending <code>Throwable</code>.  e.g. checked and unchecked exceptions.  Clients are warned that 
 * <code>toString</code> and <code>hashCode</code> are <b>not</b> pure functions and you should not 
 * rely on them as such.  Exceptions are included for convenience only. 
 * </ul>
 * 
 * @author robmoffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ImmutableValue {

}
