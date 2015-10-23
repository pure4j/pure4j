package org.pure4j.test.checker.collections;

import org.junit.Test;
import org.pure4j.test.checker.Helper;
import org.pure4j.test.collections.ArraySeqExample;
import org.pure4j.test.collections.EnumerationSeqExample;
import org.pure4j.test.collections.IteratorSeqExample;
import org.pure4j.test.collections.PersistentArrayMapExample;
import org.pure4j.test.collections.PersistentHashMapExample;
import org.pure4j.test.collections.PersistentHashSetExample;
import org.pure4j.test.collections.PersistentListExample;
import org.pure4j.test.collections.PersistentQueueExample;
import org.pure4j.test.collections.PersistentTreeMapExample;
import org.pure4j.test.collections.PersistentTreeSetExample;
import org.pure4j.test.collections.PersistentVectorExample;
import org.pure4j.test.collections.TransientsTest;

public class TestPurityOfCollectionsTests {

	@Test 
	public void testArraySeqPurity() {
		Helper.check(0, ArraySeqExample.class);
	}
	
	@Test 
	public void testEnumerationSeqPurity() {
		Helper.check(0, EnumerationSeqExample.class);
	}
	
	@Test 
	public void testIteratorSeqPurity() {
		Helper.check(0, IteratorSeqExample.class);
	}
	
	@Test 
	public void testPersistentArrayMapPurity() {
		Helper.check(0, PersistentArrayMapExample.class);
	}
	
	@Test 
	public void testPersistentHashMapPurity() {
		Helper.check(0, PersistentHashMapExample.class);
	}
	
	@Test 
	public void testPersistentHashSetPurity() {
		Helper.check(0, PersistentHashSetExample.class);
	}
	
	@Test 
	public void testPersistentListPurity() {
		Helper.check(0, PersistentListExample.class);
	}
	
	@Test 
	public void testPersistentQueuePurity() {
		Helper.check(0, PersistentQueueExample.class);
	}
	
	@Test 
	public void testPersistentTreeMapPurity() {
		Helper.check(0, PersistentTreeMapExample.class);
	}
	
	@Test 
	public void testPersistentTreeSetPurity() {
		Helper.check(0, PersistentTreeSetExample.class);
	}
	
	@Test 
	public void testPersistentVectorPurity() {
		Helper.check(0, PersistentVectorExample.class);
	}
	
	@Test 
	public void testTransientsPurity() {
		Helper.check(0, TransientsTest.class);
	}

}
