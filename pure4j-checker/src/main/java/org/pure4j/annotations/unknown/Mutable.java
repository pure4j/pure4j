package org.pure4j.annotations.unknown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeQualifier;

/**
 * Default type for java elements - mutable.
 * 
 * @author robmoffat
 *
 */
@TypeQualifier
@InvisibleQualifier
@DefaultQualifierInHierarchy
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.TYPE})
@SubtypeOf({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mutable {

}
