package org.pure4j.processor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.type.typeannotator.ListTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.TypeAnnotator;
import org.checkerframework.javacutil.AnnotationUtils;
import org.pure4j.annotations.pure.ForcePure;
import org.pure4j.annotations.pure.Impure;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureNotThreadsafe;
import org.pure4j.annotations.pure.UnspecifiedPurity;

public class PurityTypeFactory extends BaseAnnotatedTypeFactory {

	public AnnotationMirror UNSPECIFIED_PURITY, IMPURE, PURE_NOT_THREADSAFE, PURE, FORCE_PURE;
	
	
	public static final Set<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS = new HashSet<>(Arrays.asList(
			UnspecifiedPurity.class, Impure.class, PureNotThreadsafe.class, Pure.class, ForcePure.class));

	
	@Override
	protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
		return SUPPORTED_ANNOTATIONS;
	}
	
	protected ImmutabilityTypeFactory immutabilityTypeFactory;
	
	public PurityTypeFactory(BaseTypeChecker checker) {
		super(checker);
		UNSPECIFIED_PURITY = AnnotationUtils.fromClass(elements, UnspecifiedPurity.class);
		IMPURE = AnnotationUtils.fromClass(elements, Impure.class);
		PURE_NOT_THREADSAFE = AnnotationUtils.fromClass(elements, PureNotThreadsafe.class);
		PURE = AnnotationUtils.fromClass(elements, Pure.class);
		FORCE_PURE = AnnotationUtils.fromClass(elements, ForcePure.class);
		postInit();
	}
	
	

	@Override
	protected TypeAnnotator createTypeAnnotator() {
		return new ListTypeAnnotator(new TypeAnnotator(this) {

			@Override
			public Void visitExecutable(AnnotatedExecutableType t, Void p) {
				if (t.getElement().getKind() == ElementKind.CONSTRUCTOR) {
					
					AnnotatedTypeMirror returnType = t.getReturnType();
					AnnotatedTypeMirror mirror = getTypeDeclarationMirror(returnType);
					
					// executable methods are @Pure by default if they are on an immutable class.
					// e.g. in a constructor, the constructor has the same type as the thing it constructs.
					addPureIfDefinedOnImmutableClass(mirror, t);
				}
				
				
				
				return super.visitExecutable(t, p);
			}
			
		}, super.createTypeAnnotator());
	}

	/**
	 * When we have an instance of a type in the program, return the actual type declaration back instead.
	 */
	private AnnotatedTypeMirror getTypeDeclarationMirror(AnnotatedTypeMirror type) {
		if (!type.isDeclaration()) {
			TypeMirror underlyingType = type.getUnderlyingType();
			if (underlyingType instanceof DeclaredType) {
				Element theOriginalDeclaration = ((DeclaredType) underlyingType).asElement();
				AnnotatedTypeMirror mirror = fromElement(theOriginalDeclaration);
				return mirror;
			}
		}
		
		return type;
	}

	
	public void addPureIfDefinedOnImmutableClass(AnnotatedTypeMirror declared, AnnotatedTypeMirror target) {
		if ((target.hasAnnotation(PURE)) || 
			(target.hasAnnotation(PURE_NOT_THREADSAFE)) ||
			(target.hasAnnotation(FORCE_PURE)) || 
			(target.hasAnnotation(IMPURE))) {
			return;		// since it's already annotated, we don't need to make this inference.
		}
		
		
	}
	
}
