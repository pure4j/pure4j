package org.pure4j.checker.basic.corners.mutable_enun;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;

/**
 * A really bad java edge-case where you can create a mutable enum.  
 * Perhaps we should make the assumption that all enums should be immutable, and go from there?
 * After all, this is atrocious coding style anyway.
 * 
 * @author robmoffat
 *
 */
public class MutableEnum extends AbstractChecker {

	@ImmutableValue
	static enum Blah { A(new int[] { 1, 2}), B(new int[] {6, 7}); 
		
		Blah(int[] in) {
			this.someState = in;
		}
		
		public int[] someState;
	}
	
	@Pure
	public void consumeBlah(Blah b) {
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 5);
	}
}
