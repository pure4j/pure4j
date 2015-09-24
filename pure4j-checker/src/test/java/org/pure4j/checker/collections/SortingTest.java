package org.pure4j.checker.collections;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.PureCollections;
import org.pure4j.collections.PureCollectors;

public class SortingTest {

	@Test
	public void makeSureWeCanSortStuff() {
		ArraySeq<Float> unsorted = new ArraySeq<>(11f, 9f, 5f);
		Assert.assertEquals(new ArraySeq<>(5f, 9f, 11f), PureCollections.sort(unsorted));;
		
		
	}
	
	@Test(expected=UnsupportedOperationException.class) 
	public void doASort() {
		new ArraySeq<>(5f, 3f, 2f).sort(null);
	}
}
