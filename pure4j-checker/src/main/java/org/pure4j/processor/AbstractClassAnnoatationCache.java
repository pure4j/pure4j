package org.pure4j.processor;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractClassAnnoatationCache implements ClassAnnotationCache {

	protected Map<String, Boolean> classMap = new HashMap<String, Boolean>();

	public AbstractClassAnnoatationCache() {
		super();
	}

	

	@Override
	public boolean typeIsMarked(Type t, Callback cb) {
			if (t instanceof Class) {
				if (((Class<?>) t).isArray()) {
					return false;
				} else if (((Class<?>)t).isEnum()) {
					return true;
				} else {
					return classIsMarked((Class<?>) t, cb);
				}
			} else if (t instanceof Enum<?>){
				return true;
			} else if (t instanceof ParameterizedType) {
				// we need to make sure the raw class is immutable
				Type raw = ((ParameterizedType) t).getRawType();
				if (!typeIsMarked(raw, cb)) {
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
					if (!typeIsMarked(b, cb)) {
						return false;
					}
				}
				return true;
			} else if (t instanceof GenericArrayType) {
				return false;
			} else {
				throw new RuntimeException("Type not supported: "+t);
			}
		}

	
	public void addClass(Class<?> cl) {
		if (Boolean.FALSE == classMap.get(cl.getName())) {
			throw new IllegalStateException("Class is already excluded from cache "+this.getClass().getName()+cl);
		}
		
		classMap.put(cl.getName(), true);
	}
	
	
}