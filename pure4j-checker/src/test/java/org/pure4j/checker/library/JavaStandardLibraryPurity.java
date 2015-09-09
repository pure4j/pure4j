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
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.junit.Test;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.PersistentHashSet;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.PurityChecker;
import org.springframework.core.io.DefaultResourceLoader;


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
		});
	}
	
	@Test
	public void createPure4JCollectionsPurityList() throws IOException, ClassNotFoundException {
		checkPurityOfClasses("pure4j-collections.pure", new ClassListProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Class<?>> topLevelClasses() {
				return Arrays.asList((Class<?>) PersistentHashMap.class, PersistentHashSet.class);
			}
		});
	}
	
	interface ClassListProvider {
		
		public List<Class<?>> topLevelClasses();
		
	}

	protected void checkPurityOfClasses(String outputName, ClassListProvider clp) throws IOException {
		FileCallback fc = new FileCallback(new File(outputName));
		ClassFileModelBuilder cfmb = new ClassFileModelBuilder();
		ClassLoader cl = this.getClass().getClassLoader();
		DefaultResourceLoader drl = new DefaultResourceLoader(cl);
		
		for (Class<?> c : clp.topLevelClasses()) {
			visitAllOf(c, drl, cfmb);
		}
		
		ProjectModel pm = cfmb.getModel();
		PurityChecker checker = new PurityChecker(cl);
		for (String classInModel : pm.getAllClasses()) {
			ClassHandle ch = new ClassHandle(classInModel);
			checker.addMethodsFromClassToPureList(ch.hydrate(cl), fc, pm, true);	
		}
		checker.checkModel(pm, fc);
		fc.close();
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
		return Arrays.asList(ArrayList.class,

		StringBuilder.class,
		StringBuffer.class,
		Math.class,
		StrictMath.class,
		Comparable.class);
	}
	
	private void visitAllOf(Class<?> c, DefaultResourceLoader drl, ClassFileModelBuilder cfmb) throws IOException {
		if ((c != Object.class) && (c != null)) {
			cfmb.visit(drl.getResource("classpath:/"+c.getName().replace(".", "/")+".class"));
			for (Class<?> intf : c.getInterfaces()) {
				visitAllOf(intf, drl, cfmb);
			}
			
			for (Class<?> cl : c.getClasses()) {				
				visitAllOf(cl, drl, cfmb);
			}
			
			
			visitAllOf(c.getSuperclass(), drl, cfmb);
		}
	}

	static class FileCallback implements Callback, Closeable {
		
		private BufferedWriter stream;
		
		public FileCallback(File out) throws IOException {
			stream = new BufferedWriter(new FileWriter(out));
		}
		
		@Override
		public void send(String s) {
			System.out.println(s);
		}
		
		@Override
		public void registerError(String s, Throwable optional) {
			System.err.println(s);
		}

		List<String> pureSignatures = new ArrayList<String>();
		
		@Override
		public void registerPure(String signature) {
			pureSignatures.add(signature);
		}

		@Override
		public void close() throws IOException {
			Collections.sort(pureSignatures);
			for (String s : pureSignatures) {
				stream.write(s);
				stream.write("\n");
			}
			
			stream.close();
		}
	};
}
