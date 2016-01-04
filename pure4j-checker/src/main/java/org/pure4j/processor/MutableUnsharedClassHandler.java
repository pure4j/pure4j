package org.pure4j.processor;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.ClassNotFinalException;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.impl.AnnotationHandle;
import org.pure4j.model.impl.ClassHandle;
import org.pure4j.model.impl.FieldDeclarationHandle;

/**
 * Keeps track of the classes that have been registered as immutable
 * @author robmoffat
 *
 */
public class MutableUnsharedClassHandler extends AbstractClassAnnotationCache implements ClassAnnotationCache {

	public static final boolean CHECK_FOR_FINAL_CLASSES = false;
	public static final boolean CHECK_CLASSES_OUTSIDE_PROJECT = false;

	
	private ClassAnnotationCache immutableHandler;
	
	public MutableUnsharedClassHandler(ClassAnnotationCache ivch) {
		this.immutableHandler = ivch;
	}
	
	@Override
	public void doClassChecks(ClassHandle mutableSharedClass, Callback cb, ProjectModel pm) {
		
		if (CHECK_FOR_FINAL_CLASSES) {
			if (mutableSharedClass.isFinal()) {
				cb.registerError(new ClassNotFinalException(mutableSharedClass));
			}
		}
		
		while (!mutableSharedClass.isObject()) {
			if (!CHECK_CLASSES_OUTSIDE_PROJECT) {
				String classDescriptor = mutableSharedClass.getDescriptor();
				if (!pm.withinModel(classDescriptor)) {
					return;
				}
			}
			
			for (FieldDeclarationHandle f : mutableSharedClass.getDeclaredFields()) {
				if (!f.isStatic()) {
					boolean pub = f.isPublic();
					boolean priv = f.isPrivate();
					boolean prot = f.isProtected();
					boolean synthetic = f.isSynthetic();
					boolean accessible = (pub || ((!priv) && (!prot))) && (!synthetic);
					if (accessible) {
						if (!immutableHandler.typeIsMarked(f.getGenericType(), cb, pm)) {
							cb.registerError(new FieldTypeNotImmutableException(f, mutableSharedClass));
						}
					}
				}
			}
			
			mutableSharedClass = pm.getClassHandle(mutableSharedClass.getSuperclass());
		}
		
	}
	
	@Override
	public boolean classIsMarked(ClassHandle in, Callback cb, ProjectModel pm) {
		if (!classMap.containsKey(in.getClassName())) {
			AnnotationHandle ann = AbstractClassAnnotationCache.classHierarchyAnnotation(in, MutableUnshared.class, pm);
			boolean mu = (ann != null);
	
			if (mu) {
				cb.send("@MutableUnshared "+in.getClassName());
			} 
			classMap.put(in.getClassName(), mu);
			return mu;
		} else {
			return classMap.get(in.getClassName());
		}
	}
}
