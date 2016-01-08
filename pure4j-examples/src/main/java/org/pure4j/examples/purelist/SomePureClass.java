package org.pure4j.examples.purelist;

import org.pure4j.annotations.pure.Pure;

public class SomePureClass {

	/**
	 * This method is going pass the purity check, because even though it calling
	 * <pre>notMarkedPure()</pre>, that is in the example.pure file, which is loaded up
	 * before processing.  
	 * 
	 * This is useful for when you need to mark 3rd party components as pure.
	 */
	@Pure
	public static void thisIsPure() {
		notMarkedPure();	
	}
	
	public static void notMarkedPure() {
		// but actually is fine.
	}
}
