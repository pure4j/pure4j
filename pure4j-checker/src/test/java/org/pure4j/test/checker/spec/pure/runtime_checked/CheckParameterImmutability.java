package org.pure4j.test.checker.spec.pure.runtime_checked;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.exception.ImpureCodeCallingPureCodeWithoutInterfacePurity;
import org.pure4j.exception.IncorrectPure4JImmutableCallException;
import org.pure4j.exception.MissingImmutableParameterCheckException;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.immutable.ClassNotImmutableException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

import com.sun.org.apache.bcel.internal.generic.IMUL;

public class CheckParameterImmutability {

	private String s = "blah";
	
	@MutableUnshared
	static class InnerClass {

		@SuppressWarnings("unused")
		private Object o;
		
		@CausesError(FieldTypeNotImmutableException.class)
		@ShouldBePure
		public InnerClass(int i, Object o) {
			super();
			Pure4J.immutable(o);
			this.o = o;
		}
		
	}
	
	class InnerClass2 extends InnerClass {

		@ShouldBePure
		public InnerClass2(Object o, Object o2) {
			super(5, o);
			Pure4J.immutable(o2);
		}
		
		@ShouldBePure
		public void callParentThing() {
			testParam1Bad(null, null);
		}
		
	}
	
	/**
	 * Not going to be pure as we do something before checking
	 * @param in1
	 * @param in2
	 */
	@Pure
	@CausesError({IncorrectPure4JImmutableCallException.class, PureMethodParameterNotImmutableException.class})
	public String testParam1Bad(Object in1, Object in2) {
		Object in3 = in2;
		Pure4J.immutable(in1, in2);
		return in3.toString();
	}
	
	/**
	 * Should be fine.
	 */
	@Pure
	@ShouldBePure
	public String testParam2Good(Object in1, Object in2) {
		Pure4J.immutable(in1, in2);
		return in1.toString() + in2.toString();
	}
	
	/**
	 * Should be fine.
	 */
	@Pure
	@ShouldBePure
	public String testParam3Good(Object in1, int in2) {
		Pure4J.immutable(in1);
		return in1.toString();
	}
	
	/**
	 * Not testing all the parameters
	 */
	@Pure
	@CausesError({MissingImmutableParameterCheckException.class, PureMethodParameterNotImmutableException.class})
	public String testParam4Bad(Object in1, Object in2) {
		Pure4J.immutable(in1);
		return in1.toString() + in2.toString();
	}
	
	/**
	 *Calling with constants, gives a warning at compile time
	 */
	@Pure
	@CausesError({IncorrectPure4JImmutableCallException.class, PureMethodParameterNotImmutableException.class})
	public String testParam5Bad(Object in1) {
		Pure4J.immutable(in1, 6);
		return in1.toString();
	}
	
	/**
	 *Calling with constants, gives a warning at compile time
	 */
	@Pure
	@ShouldBePure
	public String testParam6Good(Object in1, Object in2,Object in3, Object in4, Object in5, Object in6) {
		Pure4J.immutable(in1,in2, in3, in4);
		Pure4J.immutable(in5, in6);
		return in1.toString();
	}
	
	@Pure
	@CausesError({PureMethodParameterNotImmutableException.class, IncorrectPure4JImmutableCallException.class})  // called with non-arguments
 	public String testParam7Good(Object in1) {
		Pure4J.immutable(in1, s);
		return in1.toString();
	}
	
	@Test
	@ShouldBePure
	@Pure
	public void callTheGoodOnes() {
		testParam2Good("string", 66);
		testParam3Good('c', 3455);
		testParam5Bad("hello");
		testParam7Good(345987d);
	}
	
	@CausesError(ImpureCodeCallingPureCodeWithoutInterfacePurity.class)
	@Test(expected=ClassNotImmutableException.class) 
	public void testBad1() {
		testParam1Bad(new Object(), "dhh");
	}
}
