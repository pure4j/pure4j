package org.pure4j.checker.library;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.PersistentArrayMap;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.PersistentHashSet;
import org.pure4j.collections.PersistentList;
import org.pure4j.collections.PersistentQueue;
import org.pure4j.collections.PersistentTreeMap;
import org.pure4j.collections.PersistentTreeSet;
import org.pure4j.collections.PersistentVector;
import org.pure4j.collections.PureCollections;
import org.pure4j.exception.Pure4JException;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.PurityChecker;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;


public class JavaStandardLibraryPurity {
	
	@Test
	public void createJavaStandardPurityList() throws IOException, ClassNotFoundException {
		checkPurityOfClasses("java-builtins.pure", new ClassListProvider() {

			@Override
			public List<Class<?>> topLevelClasses() {
				List<Class<?>> out = new ArrayList<Class<?>>();
				out.addAll(javaLangClasses());
				out.addAll(javaIOClasses());
				out.addAll(javaUtilClasses());
				return out;
			}
		}, "java.", true, false);
	}
	
	@Test
	public void createPure4JCollectionsPurityList() throws IOException, ClassNotFoundException {
		checkPurityOfClasses("pure4j-collections.pure", new ClassListProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Class<?>> topLevelClasses() {
				return Arrays.asList((Class<?>) 
						PureCollections.class,
						ArraySeq.class,
						PersistentHashMap.class, 
						PersistentHashSet.class,
						PersistentList.class,
						PersistentQueue.class,
						PersistentArrayMap.class,
						PersistentTreeMap.class, 
						PersistentTreeSet.class, 
						PersistentVector.class
						);
			}
		}, "org.pure4j", false, true);
	}
	
	interface ClassListProvider {
		
		public List<Class<?>> topLevelClasses();
		
	}

	protected void checkPurityOfClasses(String outputName, ClassListProvider clp, String packageStem, boolean assumePurity, boolean expectNoErrors) throws IOException {
		FileCallback fc = new FileCallback(new File(outputName));
		ClassFileModelBuilder cfmb = new ClassFileModelBuilder();
		ClassLoader cl = this.getClass().getClassLoader();
		DefaultResourceLoader drl = new DefaultResourceLoader(cl);
		Set<Resource> resources = new HashSet<Resource>();
		
		for (Class<?> c : clp.topLevelClasses()) {
			visitAllOf(c, drl, cfmb, packageStem, new HashSet<Class<?>>(), resources);
		}
		
		for (Resource resource : resources) {
			cfmb.visit(resource);
		}
		
		ProjectModel pm = cfmb.getModel();
		PurityChecker checker = new PurityChecker(cl);
		if (assumePurity) {
			for (String classInModel : pm.getAllClasses()) {
				ClassHandle ch = new ClassHandle(classInModel);
				checker.addMethodsFromClassToPureList(ch.hydrate(cl), fc, pm, true);	
			}
		}
		checker.checkModel(pm, fc);
		fc.close();
		
		if (expectNoErrors) {
			Assert.assertEquals(0, fc.errors.size());
		}
	}

	@SuppressWarnings("unchecked")
	protected List<Class<?>> javaUtilClasses() {
		return Arrays.asList(ArrayList.class,
		ListIterator.class,
		Arrays.class,
		LinkedList.class,
		HashMap.class,
		HashSet.class,
		TreeMap.class,
		TreeSet.class,
		Deque.class,
		EnumMap.class,
		EnumSet.class,
		Hashtable.class,
		Vector.class,
		Iterator.class,
		StringTokenizer.class,
		Stack.class,
		Collections.class);
	}

	@SuppressWarnings("unchecked")
	protected List<Class<?>> javaIOClasses() {
	
		return Arrays.asList(ArrayList.class,
		BufferedInputStream.class,
		BufferedOutputStream.class,
		CharArrayReader.class,
		CharArrayWriter.class,
		StringReader.class,
		StringWriter.class,
		PrintStream.class,
		PrintWriter.class);
	}

	@SuppressWarnings("unchecked")
	protected List<Class<?>>  javaLangClasses() {
		return Arrays.asList(
		Number.class,
		StringBuilder.class,
		StringBuffer.class,
		Math.class,
		StrictMath.class,
		Integer.class, 
		Double.class, 
		Float.class,
		Character.class,
		Boolean.class,
		Long.class,
		String.class, 
		Class.class, 
		Enum.class);
	}
	
	private void visitAllOf(Class<?> c, DefaultResourceLoader drl, ClassFileModelBuilder cfmb, String packageStem, Set<Class<?>> done, Set<Resource> resources) throws IOException {
		if ((c != Object.class) && (c != null) && (!done.contains(c))) {
			done.add(c);
			System.out.println("visiting: "+c);
			if (c.getName().startsWith(packageStem)) {
				resources.add(drl.getResource("classpath:/"+c.getName().replace(".", "/")+".class"));
				for (Class<?> intf : c.getInterfaces()) {
					visitAllOf(intf, drl, cfmb, packageStem, done,  resources);
				}
				
				for (Class<?> cl : c.getClasses()) {				
					visitAllOf(cl, drl, cfmb, packageStem, done, resources);
				}
				
				
				visitAllOf(c.getSuperclass(), drl, cfmb, packageStem, done, resources);
			}
		}
	}

	static class FileCallback implements Callback, Closeable {
		
		private BufferedWriter stream;
		
		private List<String> errors = new ArrayList<String>();
		
		public FileCallback(File out) throws IOException {
			stream = new BufferedWriter(new FileWriter(out));
		}
		
		@Override
		public void send(String s) {
			//System.out.println(s);
		}
		
		@Override
		public void registerError(Pure4JException t) {
			errors.add(t.getMessage());
		}

		List<String> pureSignatures = new ArrayList<String>();
		
		@Override
		public void registerPure(String signature) {
			pureSignatures.add(signature);
		}

		@Override
		public void close() throws IOException {
			Collections.sort(errors);
			for (String string : errors) {
				System.err.println(string);
			}
			
			Collections.sort(pureSignatures);
			for (String s : pureSignatures) {
				stream.write("FORCE ");
				stream.write(s);
				stream.write("\n");
			}
			
			stream.close();
		}
	};
}
