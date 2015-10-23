package org.pure4j.test.checker.lambda.filter;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.IPersistentList;
import org.pure4j.collections.PersistentList;
import org.pure4j.lambda.PureCollectors;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class PureFilter extends AbstractTest {

	@ShouldBePure
	@Pure
	public static IPersistentList<String> consumeBlah(PersistentList<String> in) {
		IPersistentList<String> done = in.stream().filter((a) -> a.startsWith("e")).collect(PureCollectors.toPersistentList());
		return done;
	}
	
	
	@Test
	public void usingAPure() {
		PersistentList<String> someSeq = new PersistentList<String>("john", "eats", "chips");
		Assert.assertEquals(new PersistentList<String>("eats") , consumeBlah(someSeq));
	}
	
}
