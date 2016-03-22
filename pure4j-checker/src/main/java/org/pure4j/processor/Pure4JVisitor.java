package org.pure4j.processor;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Modifier;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.pure4j.annotations.immutable.ImmutableValue;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;

public class Pure4JVisitor extends BaseTypeVisitor<Pure4JTypeFactory>{

	public Pure4JVisitor(BaseTypeChecker checker) {
		super(checker);
	}

	public Pure4JVisitor(BaseTypeChecker checker, Pure4JTypeFactory typeFactory) {
		super(checker, typeFactory);
	}
	
	private Class<? extends Annotation> mutabilityAnnotation;

	@Override
	public Void visitClass(ClassTree node, Void p) {
		if (hasImmutableValueAnnotation(node)) {
			mutabilityAnnotation = ImmutableValue.class;
		} else {
			mutabilityAnnotation = null;
		}
		return super.visitClass(node, p);
	}

	@Override
	public Void visitVariable(VariableTree node, Void p) {
		if (mutabilityAnnotation == ImmutableValue.class) {
			// must be final
			if (!node.getModifiers().getFlags().contains(Modifier.FINAL)) {
				checker.report(Result.failure(node.getName()+" should be final"), node);
			}
		}
		
		return super.visitVariable(node, p);
	}

	private boolean hasImmutableValueAnnotation(ClassTree node) {
		for (AnnotationTree annTree : node.getModifiers().getAnnotations()) {
			if (annTree.toString().contains("ImmutableValue")) { 	// OMG WTF
				return true;			
			}
		}

			
		return false;
		
	}
	
}
