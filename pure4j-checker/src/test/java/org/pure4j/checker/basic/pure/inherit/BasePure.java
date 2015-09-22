package org.pure4j.checker.basic.pure.inherit;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.basic.support.ShouldBePure;

@MutableUnshared
public class BasePure {

	@ShouldBePure
	public int onBasePure() {
		return 4;
	}
}
