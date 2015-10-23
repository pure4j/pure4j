package org.pure4j.test.checker.spec.mutable_unshared.static_call;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.PureMethodAccessesNonFinalFieldException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;


public class TestCheckStatic {
	
	@MutableUnshared
	public static class TestClass {
		
		private static int x_hidden;
		
		public static int x_public;
		
		@Pure
		@ShouldBePure
		public static int callOK() {
			return 5;
		}
		
		@Pure
		@CausesError(PureMethodAccessesNonFinalFieldException.class)
		public static int callSharedStateBad() {
			return x_hidden;
		}
		
		@Pure
		@CausesError(PureMethodAccessesNonFinalFieldException.class)
		public static int callNotOk() {
			return x_public;
		}
		
		@CausesError(PureMethodAccessesNonFinalFieldException.class)
		public int memberNotOk() {
			return x_public;
		}
		
		@CausesError(PureMethodAccessesNonFinalFieldException.class)
		public int memberSharedStateNotOk() {
			return x_hidden;
		}
		
		@ShouldBePure
		public int shouldBeOK() {
			return callOK();
		}
	}
}
