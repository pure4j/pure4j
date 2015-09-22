package org.pure4j.annotations.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pure4j.collections.PersistentVector;

/**
 * Overrides the field & method return-type checks on an @ImmutableValue class to ignore the 
 * checking of the field/method type.  
 * 
 * Since this is disabling some purity testing, it should be avoided if possible.  
 * 
 * <h3>1.  Within <code>@ImmutableValue</code> classes</h3>
 * 
 * <p>This can be used to indicate that a field within the class is managed by the class, and won't be 
 * shared/exposed.  For example, having an array field within an immutable value is not allowed, as the
 * array is mutable.  Preferentially, use a {@link PersistentVector} or similar to contain the array in an immutable 
 * structure.
 * 
 * <h3>2.  Return types for methods in <code>@MutableState</code> classes.</h3>
 * 
 * <p>Sometimes you cannot narrow the return type for a mutable state object far enough, perhaps because the
 * return type is generic.  You can use this annotation on the method to skip the checking of the return type.
 * 
 * @author robmoffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface IgnoreNonImmutableTypeCheck {

}
