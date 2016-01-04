package org.pure4j.processor;

import java.util.HashMap;
import java.util.Map;

import org.pure4j.model.GenericTypeHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.impl.AnnotationHandle;
import org.pure4j.model.impl.ClassHandle;

public abstract class AbstractClassAnnotationCache implements ClassAnnotationCache {

	protected Map<String, Boolean> classMap = new HashMap<String, Boolean>();

	public AbstractClassAnnotationCache() {
		super();
	}

	@Override
	public boolean classIsMarked(ClassHandle in, Callback cb, ProjectModel pm) {
		return classMap.containsKey(in.getDeclaringClass());
	}



	@Override
	public boolean typeIsMarked(GenericTypeHandle t, Callback cb, ProjectModel pm) {
		if (t.isArray()) {
			return false;
		}
		
		if (t.isEnum()) {
			return true;
		}
		
		if (t instanceof ClassHandle) {
			return classIsMarked((ClassHandle) t, cb, pm);
		}
			
			throw new RuntimeException("Type not supported: "+t);
			
/*			} else if (t instanceof Enum<?>){
				return true;
			} else if (t instanceof ParameterizedType) {
				// we need to make sure the raw class is immutable
				Type raw = ((ParameterizedType) t).getRawType();
				if (!typeIsMarked(raw, cb, pm)) {
					return false;
				}
				
	//			for (Type t2 : ((ParameterizedType) t).getActualTypeArguments()) {
	//				if (!typeIsMarkedImmutable(t2, cb)) {
	//					return false;
	//				}
	//			}
				
				return true;
			} else if (t instanceof TypeVariable) {
				TypeVariable<?> tv = (TypeVariable<?>) t;
				for (Type b : tv.getBounds()) {
					if (!typeIsMarked(b, cb, pm)) {
						return false;
					}
				}
				return true;
			} else if (t instanceof GenericArrayType) {
				return false;
			} else {
				throw new RuntimeException("Type not supported: "+t);
			} */
		}

	
	public void addClass(ClassHandle cl) {
		if (Boolean.FALSE == classMap.get(cl.getClassName())) {
			throw new IllegalStateException("Class is already excluded from cache "+this.getClass().getName()+cl);
		}
		
		classMap.put(cl.getClassName(), true);
	}
	
	public static AnnotationHandle classHierarchyAnnotation(ClassHandle clazz, Class<?> ac, ProjectModel pm) {
		if ((clazz == null) || (clazz.isObject())) {
			return null;
		}
		
		@SuppressWarnings({ "rawtypes" })
		AnnotationHandle ann = clazz.getAnnotation((Class) ac);
		if (ann != null) {
			return ann;
		} else {
			for (String interf : clazz.getInterfaces()) {
				ann = classHierarchyAnnotation(pm.getClassHandle(interf), ac, pm);
				if (ann != null) {
					return ann;
				}
			}
			return classHierarchyAnnotation(pm.getClassHandle(clazz.getSuperclass()), ac, pm);
		}
	}
	
	
}