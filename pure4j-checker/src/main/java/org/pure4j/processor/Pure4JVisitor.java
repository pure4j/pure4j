package org.pure4j.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.tree.JCTree.JCIdent;

public class Pure4JVisitor extends BaseTypeVisitor<ImmutabilityTypeFactory>{

	public Pure4JVisitor(BaseTypeChecker checker) {
		super(checker);
		this.ptypeFactory = new PurityTypeFactory(checker);
	}
	
	protected PurityTypeFactory ptypeFactory;
	
	@Override
	protected ImmutabilityTypeFactory createTypeFactory() {
		return new ImmutabilityTypeFactory(checker);
	}
	
	
	

	@Override
	public void setRoot(CompilationUnitTree root) {
		super.setRoot(root);
		ptypeFactory.setRoot(root);
	}




	@Override
	public Void visitClass(ClassTree node, Void p) {
		if (hasImmutableValueAnnotation(node)) {
			for (Tree t : node.getMembers()) {
				
				if (t.getKind() == Kind.VARIABLE) {
					checkVariableImmutability((VariableTree) t);
					checkVariableFinal((VariableTree) t);
				} else if (t.getKind() == Kind.METHOD) {
					checkMethodOnImmutableClass((MethodTree) t);
					
				}
			}
		}
		
		return super.visitClass(node, p);
	}

	private void checkVariableFinal(VariableTree t) {
		ModifiersTree mt = t.getModifiers();
		if (!mt.getFlags().contains(Modifier.FINAL)) {
			checker.report(Result.failure("pure4j.expected_final", mt), t);
		}
	}

	private void checkMethodOnImmutableClass(MethodTree node) {
		ModifiersTree mt = node.getModifiers();
		if (mt.getFlags().contains(Modifier.STATIC)) {
			// only test non-static methods.
			return;
		} 
		
		checkMethodArguments(node);
		checkMethodCallsPureMethods(node);
	}
	
	
	
	private void checkMethodCallsPureMethods(MethodTree node) {
		if (purityAnnotationOneOf(node, ptypeFactory.PURE, ptypeFactory.PURE_NOT_THREADSAFE)) {
			
			node.getBody().accept(new TreeScanner<Void, Void>() {

				@Override
				public Void visitClass(ClassTree arg0, Void arg1) {
					// TODO Auto-generated method stub
					return super.visitClass(arg0, arg1);
				}

				@Override
				public Void visitMethodInvocation(MethodInvocationTree arg0, Void arg1) {
					AnnotatedTypeMirror atm = ptypeFactory.getAnnotatedType(arg0);
					if (purityAnnotationOneOf(atm, ptypeFactory.IMPURE, ptypeFactory.UNSPECIFIED_PURITY)) {
						checker.report(Result.failure("pure4j.method_calls_impure", atm), arg0);
					}
					return null;
				}

				@Override
				public Void visitNewClass(NewClassTree arg0, Void arg1) {
					// TODO Auto-generated method stub
					return super.visitNewClass(arg0, arg1);
				}

				
			
				
				
			}, null);
			
		}
	}

	@Override
	public Void visitMethod(MethodTree node, Void p) {
		if (purityAnnotationOneOf(node, ptypeFactory.PURE, ptypeFactory.PURE_NOT_THREADSAFE)) {
			checkMethodArguments(node);
		}
		return null;
	}

	private boolean purityAnnotationOneOf(Tree node, AnnotationMirror... options) {
		AnnotatedTypeMirror atm = ptypeFactory.getAnnotatedType(node);
		return purityAnnotationOneOf(atm, options);
	}
	
	private boolean purityAnnotationOneOf(AnnotatedTypeMirror atm, AnnotationMirror... options) {
		AnnotationMirror am = atm.getAnnotationInHierarchy(ptypeFactory.PURE);
		
		if (am.equals(ptypeFactory.UNSPECIFIED_PURITY)) {
			
			System.out.println("Trying to establish purity of : "+atm);
			
			// TODO: works, but we need code in here to work out what the purity should be,
			
		}
		
		
		for (AnnotationMirror annotationMirror : options) {
			if (am.equals(annotationMirror)) {
				return true;
			}
		}
		
		return false;
	}

	private void checkMethodArguments(MethodTree mt) {
		if (purityAnnotationOneOf(mt, ptypeFactory.PURE)) {
			for (VariableTree vt : mt.getParameters()) {
				checkVariableImmutability(vt);
			}
		}
	}

	private void checkVariableImmutability(VariableTree t) {
		AnnotatedTypeMirror atm = atypeFactory.fromMember(t);
		TypeMirror underlying = atm.getUnderlyingType();
		if (underlying.getKind() == TypeKind.ARRAY) {
			
			if (!hasIgnoreImmutableTypeCheckAnnotation(t)) {
				// arrays are not immutable, so throw an error
				checker.report(Result.failure("pure4j.expected_immutable", atm, underlying), t);
			}
		}
	}

	private boolean hasImmutableValueAnnotation(ClassTree node) {
		AnnotatedDeclaredType adt = atypeFactory.getAnnotatedType(node);
		return adt.hasAnnotation(atypeFactory.IMMUTABLE);
	}
	
	private boolean hasIgnoreImmutableTypeCheckAnnotation(VariableTree vt) {
		for (AnnotationTree at : vt.getModifiers().getAnnotations()) {
			Tree tree = at.getAnnotationType();
			if (tree instanceof JCIdent) {
				Type t = ((JCIdent) tree).type;
				if (t instanceof ClassType) {
					ClassType ct = (ClassType) t;
					if (ct.equals(atypeFactory.IGNORE_IMMUTABLE_TYPE_CHECK.getAnnotationType())) {
						return true;
					}
				}
			}
 		}
		return false;
	}
	
}
