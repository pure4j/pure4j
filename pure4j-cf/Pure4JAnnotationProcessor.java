package org.pure4j.processor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import org.checkerframework.javacutil.AbstractTypeProcessor;
import org.pure4j.exception.Pure4JException;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

@SupportedAnnotationTypes({"org.pure4j.annotations.*"})
public class Pure4JAnnotationProcessor extends AbstractTypeProcessor {

	private Trees trees;
	private Messager messager;
	private Filer filer;
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.trees = Trees.instance(processingEnv);
		this.messager = processingEnv.getMessager();
		this.filer = processingEnv.getFiler();
	}

//	@Override
//	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//		try {
//			if (!roundEnv.errorRaised() && !roundEnv.processingOver()) {
//				processRound(annotations, roundEnv);
//			}
//		} catch (IOException e) {
//			throw new RuntimeException("Couldn't process annotations: ", e);
//		}
//		return true;
//	}

	PurityChecker pc;
	
	private void processRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws IOException {
		System.out.println("processing: "+annotations);
		pc = new PurityChecker(this.getClass().getClassLoader());
		FileObject fo = filer.getResource(CLASS_OUTPUT, pkg, relativeName);
		
		ClassFileModelBuilder cfmb = new ClassFileModelBuilder();
		
		for (Element e : roundEnv.getRootElements()) {
			TreePath tp = trees.getPath(e);
			CompilationUnitTree cut = tp.getCompilationUnit();
			JavaFileObject obj = cut.getSourceFile();
			cfmb.visit(obj.openInputStream());
		}
		
		PurityChecker checker = new PurityChecker(null);
		checker.checkModel(cfmb.getModel(), new Callback() {
			
			@Override
			public void send(String s) {
				messager.printMessage(Kind.NOTE, s, null);
			}
			
			@Override
			public void registerPure(String signature, Boolean interfacePure, Boolean implementationPure) {
			}
			
			@Override
			public void registerError(Pure4JException optional) {
				messager.printMessage(Kind.ERROR, optional.getMessage(), null); /* need to fill out 'on' */
			}
		});
	}

	@Override
	public void typeProcess(TypeElement element, TreePath tree) {
		element.
		System.out.println("here with the source ");
	}

}
