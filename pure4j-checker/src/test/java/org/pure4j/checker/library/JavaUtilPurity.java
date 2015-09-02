package org.pure4j.checker.library;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.Pure4JException;
import org.pure4j.processor.Callback;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.PurityChecker;
import org.springframework.core.io.DefaultResourceLoader;

public class JavaUtilPurity {
	
	public static final String JAR_PATH="/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/jre/lib/rt.jar";
	
	public static final String JP_2 = "jar:file:/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/jre/lib/rt.jar!";
	
	@Test
	public void createPurityList() throws IOException {
//		FileCallback fc = new FileCallback(new File("java-lang.pure"));
//		ClassFileModelBuilder cfmb = new ClassFileModelBuilder();
//		ClassLoader cl = this.getClass().getClassLoader();
//		DefaultResourceLoader drl = new DefaultResourceLoader(cl);
//		
//		cfmb.visit(drl.getResource("classpath:/java/lang/StringBuilder.class"));
//		cfmb.visit(drl.getResource("classpath:/java/lang/AbstractStringBuilder.class"));
//		ProjectModel pm = cfmb.getModel();
//		
//		PurityChecker checker = new PurityChecker(cl);
//		checker.addMethodsFromClassToPureList(StringBuilder.class, fc, true);
//
//		
////		// set all classes to pure
//		ClassLibrary lib = checker.getLibrary();
//		for (String className : pm.getAllClasses()) {
//			System.out.println("Register pure: "+className);
//			Class<?> c = ClassHandle.hydrateClass(className, cl);
//			lib.registerPure(c);
//		} 
//		
//		checker.checkModel(pm, fc);
//		fc.close();
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

		@Override
		public void registerPure(String signature) {
			try {
				stream.write(signature);
				stream.write("\n");
			} catch (IOException e) {
				throw new Pure4JException("IO", e);
			}
		}

		@Override
		public void close() throws IOException {
			stream.close();
		}
	};
}
