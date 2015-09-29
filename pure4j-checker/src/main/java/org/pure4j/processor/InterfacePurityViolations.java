package org.pure4j.processor;

import java.util.HashSet;
import java.util.Set;

import org.pure4j.model.ProjectModel;

/**
 * This looks for occasions where a PureMethod 
 * @author robmoffat
 *
 */
public class InterfacePurityViolations {
	
	
	public Set<String> getPurityViolations(PureChecklistHandler pch, ProjectModel pm, Callback cb, ClassLoader cl) {
		Set<String> impureClasses = new HashSet<String>(pm.getAllClasses().size() * 2);
//		for (PureMethod pure : pch.getMethodList()) {
//			if (pure.checkImplementationPurity(cb, pm) == false) {
//				if (pure.declaration instanceof MethodHandle) {
//					Method m = ((MethodHandle) pure.declaration).hydrate(cl);
//					Class<?> c = m.getDeclaringClass();
//					Class<?> intf = overridesPureMethod(m, c, pch, cb, pm);
//					if (intf != null) {
//						cb.registerError("Method "+m+" is impure but implements pure interface: "+intf,null);
//						impureClasses.add(pure.declaration.getDeclaringClass());
//					}
//
//				}
//			}
//		}
		
		return impureClasses;
	}

//	private Class<?> overridesPureMethod(Method m, Class<?> c, PureChecklistHandler pch, Callback cb, ProjectModel pm) throws SecurityException {
//		if ((m.getDeclaringClass() != c) && (c != null)) {
//			// get same method from c, then.
//			Class<?>[] args = m.getParameterTypes();
//			String name = m.getName();
//			
//			Method parent = null;
//			try {
//				parent = c.getDeclaredMethod(name, args);
//			} catch (NoSuchMethodException e) {
//			}
//			
//			if (parent != null) {
//				MethodHandle mh = new MethodHandle(parent);
//				PureMethod pure = pch.getElementFor(mh);
//				
//				if (!pure.checkImplementationPurity(cb, pm)) {
//					return c;
//				}
//				
//			}
//
//			Class<?> fromSuper = overridesPureMethod(m, c.getSuperclass(), pch, cb, pm);
//			if (fromSuper != null) {
//				return fromSuper;
//			}
//			
//			for (Class<?> class1 : c.getInterfaces()) {
//				Class<?> fromInt = overridesPureMethod(m, class1, pch, cb, pm);
//				if (fromInt != null) {
//					return fromInt;
//				}
//			}
//		}
//		
//		return null;
//	}
	
	
}
