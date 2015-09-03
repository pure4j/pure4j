package org.pure4j.checker.library;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.Pure4JException;
import org.pure4j.processor.Callback;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.PurityChecker;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.core.io.DefaultResourceLoader;

public class JavaPurity {
	
	@Test
	public void createPurityList() throws IOException {
		FileCallback fc = new FileCallback(new File("java-lang.pure"));
		ClassFileModelBuilder cfmb = new ClassFileModelBuilder();
		ClassLoader cl = this.getClass().getClassLoader();
		DefaultResourceLoader drl = new DefaultResourceLoader(cl);
		
		cfmb.visit(drl.getResource("classpath:/java/lang/StringBuilder.class"));
		cfmb.visit(drl.getResource("classpath:/java/lang/AbstractStringBuilder.class"));
		cfmb.visit(drl.getResource("classpath:/java/lang/Math.class"));
		cfmb.visit(drl.getResource("classpath:));
		ProjectModel pm = cfmb.getModel();
		
		PurityChecker checker = new PurityChecker(cl);
		checker.addMethodsFromClassToPureList(StringBuilder.class, fc, pm, true);

		
//		// set all classes to pure
//
//		for (String className : pm.getAllClasses()) {
//			System.out.println("Register pure: "+className);
//			Class<?> c = ClassHandle.hydrateClass(className, cl);
//			lib.registerPure(c);
//		} 
		
		checker.checkModel(pm, fc);
		fc.close();
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
