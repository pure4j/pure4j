package org.pure4j.checker.corner_cases.return_type;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public interface Class1 {

	public Class1 process(Object in);
	
	public Object p2(Object in);
}
