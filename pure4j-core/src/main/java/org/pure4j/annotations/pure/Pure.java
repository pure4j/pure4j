package org.pure4j.annotations.pure;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.mutable.MutableUnshared;

/**
 * See: <a href="http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/Pure.html">Online Spec</a>
 * @author robmoffat
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pure {
	
	/**
	 * Sets the level of implementation purity.
	 * @return by default, enforcement is {@link Enforcement}.CHECKED
	 */
	Enforcement value() default Enforcement.CHECKED;
	
}
