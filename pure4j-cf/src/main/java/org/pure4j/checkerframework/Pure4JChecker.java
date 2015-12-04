package org.pure4j.checkerframework;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.TypeQualifiers;
import org.pure4j.annotations.mutability.ImmutableValue;
import org.pure4j.annotations.mutability.Mutable;
import org.pure4j.annotations.mutability.MutableUnshared;
import org.pure4j.annotations.mutability.UnknownMutability;

@TypeQualifiers({ImmutableValue.class, Mutable.class, MutableUnshared.class, UnknownMutability.class})
public class Pure4JChecker extends BaseTypeChecker {

	public Pure4JChecker() {
		super();
		System.out.println("ksjhds");
	}

}
