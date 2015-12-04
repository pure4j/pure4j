package org.pure4j.checkerframework;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;

import com.sun.source.tree.MethodInvocationTree;

public class Pure4JVisitor extends BaseTypeVisitor<Pure4JTypeFactory>{

	public Pure4JVisitor(BaseTypeChecker checker) {
		super(checker);
		System.out.println("ksjhds");
	}

	@Override
	protected void checkMethodInvocability(AnnotatedExecutableType method, MethodInvocationTree node) {
		System.out.println("The method: "+method);
	}
	
	

}
