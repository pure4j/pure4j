package org.pure4j.checker.basic.immutable_calls_super;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.basic.AbstractChecker;

/**
 * This test is where a class calls it's super.  But, the super is not declared pure, and
 * so the class gets a warning.  
 * 
 * @author robmoffat
 *
 */
public class TestCheckPureNotInheritedBackwards extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 3);
	}
}
