package org.pure4j.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.FieldHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.ThisHandle;

/**
 * The responsibility of this class is to keep track of all the methods we want to check 
 * for purity.  The {@link PureMethod} implementations track each individual call and self-check the
 * purity.  This is done on a per method-declaration level.
 * 
 * @author robmoffat
 *
 */
public class PureChecklistHandler {
	
	public static final boolean IGNORE_EXCEPTION_CONSTRUCTION = true;
	public static final boolean IGNORE_EQUALS_PARAMETER_PURITY = true;

	class PureMethod {
		
		public MemberHandle declaration;
		public Enforcement e;
		private Boolean pureInterface = null;
		private Boolean pureImplementation = null;
		private Set<Class<?>> usedIn = new LinkedHashSet<Class<?>>();
	
		private PureMethod(MemberHandle declaration, Enforcement e) {
			super();
			this.declaration = declaration;
			this.e = e;
			setupEnforcements(e);
		}

		protected void setupEnforcements(Enforcement e) {
			if (e==Enforcement.FORCE) {
				 pureImplementation = true;
				 pureInterface = true;
			} else if (e==Enforcement.NOT_PURE) {
				pureImplementation = false;
				pureInterface = false;
			}
		}
		
		private PureMethod(String description, Enforcement e) throws ClassNotFoundException {
			int firstDot = description.indexOf(".");
			int firstBracket = description.indexOf("(");
			String classPart = description.substring(0, firstDot);
			String methodPart = description.substring(firstDot+1, firstBracket);
			String descPart = description.substring(firstBracket);
			this.declaration = (methodPart.equals("<init>")) ? 
				new ConstructorHandle(classPart, descPart) : 
				new MethodHandle(classPart, methodPart, descPart);
			
			this.e = e;
			setupEnforcements(e);
		}
	
		@Override
		public String toString() {
			return "PureMethod [declaration="
					+ declaration + ", e=" + e + ", usedIn=" + usedIn +"]";
		}
		
		public boolean checkImplementationPurity(Callback cb, ProjectModel pm) {
			if (pureImplementation == null) {
				pureImplementation = true;
				
				if (!pm.withinModel(declaration.getClassName())) {
					// we can't confirm that the method is pure, unless it has been forced already.
					
					cb.registerError(this+" is expected to be a pure method, but is not in scope and isn't marked as such.", null);
					pureImplementation = false;
				}
				
				for (MemberHandle mh: pm.getCalls(declaration)) {
					if ((mh instanceof MethodHandle) || (mh instanceof ConstructorHandle)) {
						if (!isMarkedPure(mh, cb)) {
							cb.registerError(this+" is expected to be a pure method, but calls impure method "+mh, null);
							pureImplementation = false;
						}
					} else if (mh instanceof FieldHandle) {
						FieldHandle fieldHandle = (FieldHandle)mh;
						Field f = fieldHandle.hydrate(cl);
						if (!onCurrentObject(fieldHandle, f)) {
							if ((!Modifier.isFinal(f.getModifiers()))) {
								cb.registerError(this+" is expected to be pure but accesses non-final field which isn't private/protected "+fieldHandle, null);
								pureImplementation = false;
							}
							
							if (!immutables.typeIsMarkedImmutable(f.getGenericType(), cb)) {
								cb.registerError(this+" is expected to be pure but accesses a non-immutable value which isn't private/protected "+fieldHandle, null);
								pureImplementation = false;
							}
						}
					} else if (mh instanceof ThisHandle) {
						cb.registerError(this+" is expected to be pure but accesses 'this' "+mh, null);
						pureImplementation = false;
					}
				}
			}
			return pureImplementation;
		}
	
		private boolean onCurrentObject(FieldHandle fh, Field f) {
			if (fh.getDeclaringClass().equals(declaration.getDeclaringClass())) {
				// ok, it's the same class, but is it the same instance?  This is 
				// a cheap way to work it out, but not the best.
				// TODO: prevent package access?
				boolean pub = Modifier.isPublic(f.getModifiers());
				boolean stat = Modifier.isStatic(f.getModifiers());
				
				boolean out =  !pub && !stat;
				return out;
			}
			
			return false;
		}
	
		public boolean checkInterfacePurity(Callback cb) {
			if (pureInterface == null) {
				pureInterface = true;
				
				if (IGNORE_EQUALS_PARAMETER_PURITY) {
					if (("equals".equals(declaration.getName())) && 
							("(Ljava/lang/Object;)Z".equals(declaration.getDesc()))) {
						return true;
					}
				}
	
				// check the signature of the pure method
				for (Type t : declaration.getGenericTypes(cl)) {
					if (!immutables.typeIsMarkedImmutable(t, cb)) {
						cb.registerError("Pure method "+declaration+" can't take non-immutable argument "+t, null);
						pureInterface = false;
					}
				}
			} 
			return pureInterface;
			
		}	
	}
	
	private ClassLoader cl;
	private ImmutableClassHandler immutables;
	
	public PureChecklistHandler(ClassLoader cl, ImmutableClassHandler immutables) {
		super();
		this.cl = cl;
		this.immutables = immutables;
		loadPureLists();
	}

	private Map<MemberHandle, PureMethod> pureChecklist = new HashMap<MemberHandle, PureMethod>();

	
	public void loadPureLists() {
		try {
			load("/java-lang.pure");
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load the pure lists: ",e);
		}
	}

	private void load(String fileName) throws IOException, ClassNotFoundException {
		InputStream is = PureChecklistHandler.class.getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			PureMethod pureMethod = new PureMethod(line.trim(), Enforcement.FORCE);
			pureChecklist.put(pureMethod.declaration, pureMethod);
			line = br.readLine();
		}
	}
	
	public boolean isMarkedPure(MemberHandle mh, Callback cb) {
		if (pureChecklist.containsKey(mh)) {
			PureMethod pm = pureChecklist.get(mh);
			return pm.e != Enforcement.NOT_PURE;
		}
		
		if (IGNORE_EQUALS_PARAMETER_PURITY) {
			if (("equals".equals(mh.getName())) && ("(Ljava/lang/Object;)Z".equals(mh.getDesc()))) {
				return true;
			}
		}
		
		Pure p = mh.getAnnotation(cl, Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		if (isMarkedPure(mh.getDeclaringClass(cl), cb)) {
			return true;
		}
		
		if (IGNORE_EXCEPTION_CONSTRUCTION) {
			if (Throwable.class.isAssignableFrom(mh.getDeclaringClass(cl))) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isMarkedPure(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}
		
		// immutables also count as pure
		if (immutables.classIsMarkedImmutable(in, cb)) {
			return true;
		}
		
		Pure p = in.getAnnotation(Pure.class);
		if ((p != null) && (p.value() != Enforcement.NOT_PURE)) {
			return true;
		}
		
		for (Class<?> intf : in.getInterfaces()) {
			if (isMarkedPure(intf, cb)) {
				return true;
			}
		}
		
		if (isMarkedPure(in.getSuperclass(), cb)) {
			return true;
		}
		
		return false;
	}

	public void addMethod(MemberHandle declaration, Enforcement e, Class<?> usedIn) {
		PureMethod pm;
		if (pureChecklist.containsKey(declaration)) {;
			pm = pureChecklist.get(declaration);
		} else {
			pm = new PureMethod(declaration, e);
			pureChecklist.put(declaration, pm);
		}
			
		pm.usedIn.add(usedIn);
			
		if (pm.e != e) {
			if (pm.e != Enforcement.FORCE) {
				throw new RuntimeException("Was expecting different enforcement for "+pm+": "+e);
			}
		}
	}
	
	public static boolean methodIsVisible(Method m, Class<?> pureClass) {
		if (Modifier.isPrivate(m.getModifiers())) {
			return false;
		}
		
		if (Modifier.isProtected(m.getModifiers())) {
			return false;
		}
 		
		
		Class<?> onClass = m.getDeclaringClass();
		while (pureClass != onClass) {
			Method over;
			try {
				over = pureClass.getDeclaredMethod(m.getName(), m.getParameterTypes());
				if (over != null) {
					return false;
				}
			} catch (Exception e) {
			}
			
			pureClass = pureClass.getSuperclass();
		}
		
		return true;
	}
	
	public void doPureMethodChecks(Callback cb, ProjectModel pm) {
		for (PureMethod pureCandidate : pureChecklist.values()) {
			cb.send("Checking pure method: "+pureCandidate);
			pureCandidate.checkInterfacePurity(cb); 
			pureCandidate.checkImplementationPurity(cb, pm);
		}
	}
	
	public void outputPureMethodList(Callback cb, ProjectModel pm) {
		for (PureMethod pureMethod : pureChecklist.values()) {
			if ((pureMethod.pureImplementation) && (pureMethod.pureInterface)) {
				if (pm.getAllClasses().contains(pureMethod.declaration.getDeclaringClass())) {
					cb.registerPure(pureMethod.declaration.toString());
				}
			}
		}
	}
}
