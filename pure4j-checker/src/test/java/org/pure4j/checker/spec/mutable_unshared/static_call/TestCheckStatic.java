package org.pure4j.checker.spec.mutable_unshared.static_call;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.CausesError;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.exception.PureMethodAccessesSharedFieldException;


public class TestCheckStatic {
	
	@MutableUnshared
	public static class TestClass {
		
		private static int x_hidden = 5;
		
		public static int x_public = 6;
		
		@Pure
		@ShouldBePure
		public static int callOK() {
			return 5;
		}
		
		@Pure
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public static int callSharedStateBad() {
			return x_hidden;
		}
		
		@Pure
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public static int callNotOk() {
			return x_public;
		}
		
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public int memberNotOk() {
			return x_public;
		}
		
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public int memberSharedStateNotOk() {
			return x_hidden;
		}
		
		@ShouldBePure
		public int shouldBeOK() {
			return callOK();
		}
	}
}
