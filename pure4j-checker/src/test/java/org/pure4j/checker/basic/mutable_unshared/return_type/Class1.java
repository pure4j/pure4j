package org.pure4j.checker.basic.mutable_unshared.return_type;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

@MutableUnshared
public interface Class1 {

	public Class1 process(Object in);
	
	public Object p2(Object in);
}
