package org.pure4j.checker.basic.corners.return_type;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

@Pure
public interface Class1 {

	public Class1 process(Object in);
	
	public Object p2(Object in);
}
