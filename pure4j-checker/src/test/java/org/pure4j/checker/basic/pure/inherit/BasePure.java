package org.pure4j.checker.basic.pure.inherit;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

@Pure
public class BasePure {

	@ShouldBePure
	public int onBasePure() {
		return 4;
	}
}
