package org.pure4j.test.checker.lambda.filter;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.PersistentList;
import org.pure4j.collections.ISeq;
import org.pure4j.lambda.PureCollectors;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.support.AbstractChecker;

public class PureFilter extends AbstractChecker {

	@ShouldBePure
	@Pure
	public static ISeq<String> consumeBlah(PersistentList<String> in) {
		ISeq<String> done = in.stream().filter((a) -> a.startsWith("e")).collect(PureCollectors.toSeq());
		return done;
	}
	
	
	@Test
	public void usingAPure() {
		PersistentList<String> someSeq = new PersistentList<String>("john", "eats", "chips");
		Assert.assertEquals(new PersistentList<String>("eats") , consumeBlah(someSeq));
	}
	
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
