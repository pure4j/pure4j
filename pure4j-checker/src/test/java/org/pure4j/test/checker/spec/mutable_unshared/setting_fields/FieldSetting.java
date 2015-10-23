package org.pure4j.test.checker.spec.mutable_unshared.setting_fields;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.spec.immutable.broken_extend.SomeValueObjectBrokenExtend;

@MutableUnshared
public class FieldSetting {

	public int x = 0;
	private int y = 0;
	
	@CausesError(FieldTypeNotImmutableException.class)
	public Object a;
	
	public SomeValueObjectBrokenExtend c;
	
	@ShouldBePure
	public int getY() {
		return y;
	}

	@ShouldBePure
	public FieldSetting() {
	}
	
	@ShouldBePure
	public void increment() {
		y = x+1;
	}
	
	class SubFieldSetting extends FieldSetting {
		
		@ShouldBePure // implementation is pure, despite signature issue causing the below
		@CausesError({FieldTypeNotImmutableException.class, FieldTypeNotImmutableException.class})	// happens as we try to smuggle the state from the parent.
		public SubFieldSetting() {
		}
		
		@ShouldBePure
		public int smuggledValue() {
			return y;
		}
	}
	
	@Pure
	@ShouldBePure // error occurs elsewhere.
	public FieldSetting smuggleSomeMutableState() {
		return new SubFieldSetting();
	}
}
