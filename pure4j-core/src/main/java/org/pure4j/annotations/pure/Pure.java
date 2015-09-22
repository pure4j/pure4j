package org.pure4j.annotations.pure;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.pure4j.annotations.immutable.ImmutableValue;

/**
 * Declares that a method is "Pure".   
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
 * </ol></p>
 * 
 * <h3>Mutability</h3>
 * 
 * <p>By default, adding @Pure will check that the method only accepts and returns @ImmutableValue parameters, 
 * in concordance with the contract for @MutableUnshared.  However, by setting mutability to <code>ANYTHING</code>
 * you are using the purity contract for @ImmutableValue objects.  
 * 
 * <h3>Overriding Purity</h3>
 * 
 * <p>This is the most common use-case for this annotation.  Having specified @ImmutableValue or @MutableUnshared 
 * at the class level, you may want to override the purity of individual instance methods within the class.
 * <ul><li><b>CHECKED: </b> The default, purity is checked at compile-time.
 * <li><b>FORCE: </b>Purity checking is disabled, and the method is registered as pure.
 * <li><b>NOT_PURE: </b>Purity checking is disasbled, the method is marked as not pure.
 * </ul>
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
