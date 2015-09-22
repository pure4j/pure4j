package org.pure4j.checker.basic.mutable_unshared.setting_fields;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodAccessesSharedFieldException;

@MutableUnshared
public class FieldSetting {

	public int x = 0;
	private int y = 0;
	
	public int getY() {
		return y;
	}

	@CausesError(PureMethodAccessesSharedFieldException.class)
	public FieldSetting() {
	}
	
	@CausesError(PureMethodAccessesSharedFieldException.class)
	public void increment() {
		y = x+1;
	}
	
	@Pure
	@ShouldBePure
	public static FieldSetting smuggleSomeMutableState() {
		return new FieldSetting();
	}
}
