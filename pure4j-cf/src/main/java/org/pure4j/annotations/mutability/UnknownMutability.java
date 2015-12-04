package org.pure4j.annotations.mutability;

import java.lang.annotation.Target;

import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeQualifier;

@TypeQualifier
@InvisibleQualifier
@DefaultQualifierInHierarchy
@SubtypeOf({})
@Target({})		// means it can't be applied in code
public @interface UnknownMutability {

}
