package org.pure4j.annotations.pure;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.pure4j.annotations.immutable.ImmutableValue;

/**
 * Declares that methods (or single method) of the class are Pure.   
 * 
 * <p>See the <a href="https://en.wikipedia.org/wiki/Pure_function">Wikipedia definition</a>, from which the following
 * is derived.  Essentially:
 * 
 * <ul>
 * <li>The function always evaluates the same result value given the same argument value(s). 
 * <li>Evaluation of the result does not cause any semantically observable side effect or output. 
 * Such as output to I/O devices.</li>
 * </ul>
 * 
 * <h3><code>Method</code> Purity.</h3>
 * <p>
 * This is indicated by applying the <code>Pure</code> annotation to a single method.  A pure method will:
 * <ol>
 * <li>Only call other methods (or constructors) marked <code>Pure</code>.
 * <li>Only take immutable (see {@link ImmutableValue}) parameters.
 * <li>Only construct objects marked as immutable, or pure (see Class Purity, below).
 * <li>Only accesses fields where they are immutable and final.
 * </ol></p>
 * 
 * 
 * <h3><code>Class</code> Purity.</h3>
 * <p>
 * This is indicated by applying the <code>Pure</code> annotation to the class.  In addition to the 4 allowances above, a
 * pure class can:
 * <ol>
 * <li>Access fields on the current object (<code>this</code>).
 * </ol> 
 * <p>A pure class is when where the behaviour exhibits the following characteristics:
 * <ul>
 * <li>It's behaviour is deterministic with regard to a particular set of interactions
 * <li>There are no side-effects outside of the class' interface.  
 * </ul>
 * 
 * <p>This is exactly like a pure method, then, except that you are describing not a single method call, but a series of 
 * interactions with the class.  Pure classes are therefore <b>not</b> guaranteed to be thread-safe.  They may be implemented in this way, but it is
 * not a requirement. 
 *  
 * <p>If you define a class as @Pure, it's purity will be checked on a per-method (and per-constructor) basis.  The checker will report errors for methods that
 * don't meet the requirements.  Adding @Pure to individual constructors/methods will override the class-level behaviour.
 * 
 * <p>Adding @Pure to a single interface or superclass will cause all concrete classes to be regarded as pure.
 * 
 * <h3>Caveats</h3>
 * <p>
 * Use of reflection within your code can break these guarantees.  The compile-time checker will make no attempt
 * to ensure semantics where reflection is involved. (In fact, this is undecideable).  
 * 
 * @see {@link ImmutableValue}
 * @see {@link Enforcement}
 * 
 * @author robmoffat
 *
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pure {
	
	Enforcement value() default Enforcement.CHECKED;
	
	Mutability params() default Mutability.ALLOW_IMMUTABLE_ONLY;

}
