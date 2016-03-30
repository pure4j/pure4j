package org.pure4j.annotations.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pure4j.collections.PersistentVector;

/**
 * Overrides the field &amp; method return-type checks on an @ImmutableValue class to ignore the 
 * checking of the field/method type.  
 * 
 * Since this is disabling some purity testing, it should be avoided if possible.  
 * 
 * <h3>1.  Within <code>@ImmutableValue</code> classes</h3>
 * 
 * <p>This can be used to indicate that a field within the class is managed by the class, and won't be 
 * shared/exposed.  For example, having an array field within an immutable value is not allowed, as the
 * array is mutable.  Preferentially, use a {@link PersistentVector} or similar to contain the array in an immutable 
 * structure.  Also prevents errors due to non-final fields.
 * 
 * @author robmoffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface IgnoreImmutableTypeCheck {

}
