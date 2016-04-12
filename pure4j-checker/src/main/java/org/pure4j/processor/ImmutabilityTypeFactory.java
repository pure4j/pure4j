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
import org.checkerframework.framework.qual.TypeQualifiers;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.type.typeannotator.ListTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.TypeAnnotator;
import org.checkerframework.javacutil.AnnotationUtils;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.immutable.PolyImmutableValue;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.unknown.Mutable;

public class ImmutabilityTypeFactory extends BaseAnnotatedTypeFactory {

	public AnnotationMirror IMMUTABLE, MUTABLE, IGNORE_IMMUTABLE_TYPE_CHECK;

	
	public static final Set<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS = new HashSet<>(Arrays.asList(
			Mutable.class, ImmutableValue.class, MutableUnshared.class, PolyImmutableValue.class));

	
	@Override
	protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
		return SUPPORTED_ANNOTATIONS;
	}

	public ImmutabilityTypeFactory(BaseTypeChecker checker) {
		super(checker);
		IMMUTABLE = AnnotationUtils.fromClass(elements, ImmutableValue.class);
		MUTABLE = AnnotationUtils.fromClass(elements, Mutable.class);
		IGNORE_IMMUTABLE_TYPE_CHECK = AnnotationUtils.fromClass(elements, IgnoreImmutableTypeCheck.class);
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
					
					// ensure the executable code has the same annotation as it's return type.
					// e.g. in a constructor, the constructor has the same type as the thing it constructs.
					ensureHasSameImmutabilityAsDeclaredClass(mirror, t);
				}
				
				
				
				return super.visitExecutable(t, p);
			}

			@Override
			public Void visitDeclared(AnnotatedDeclaredType type, Void p) {
				// ensures that instances of a type have the same annotation as the declaration.  
				// i.e. if the class is declared immutable, all the instance variables are immutable too.
				AnnotatedTypeMirror mirror = getTypeDeclarationMirror(type);
				ensureHasSameImmutabilityAsDeclaredClass(mirror, type);
				return super.visitDeclared(type, p);
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

	
	public void ensureHasSameImmutabilityAsDeclaredClass(AnnotatedTypeMirror declared, AnnotatedTypeMirror target) {
		if ((target.hasAnnotation(IMMUTABLE)) || (target.hasAnnotation(MUTABLE))) {
			return;		// since it's already annotated, we don't need to make this inference.
		}
		
		if (declared.hasAnnotation(IMMUTABLE)) {
			System.out.println("Applying immutable to "+target);
			target.addAnnotation(IMMUTABLE);
		} else if (declared.hasAnnotation(MUTABLE)) {
			System.out.println("Applying mutable to "+target);
			target.addAnnotation(MUTABLE);
		}
		
	}
	
}
