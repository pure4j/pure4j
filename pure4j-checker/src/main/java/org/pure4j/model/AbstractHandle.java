package org.pure4j.model;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;


public abstract class AbstractHandle<X> implements Handle<X> {

	public static Set<Class<?>> hydrateClasses(Set<String> classNames, ClassLoader cl) {
		Set<Class<?>> out = new LinkedHashSet<Class<?>>(classNames.size());
		for (String n : classNames) {
			out.add(hydrateClass(n, cl));
		}

		return out;
	}

	public static Set<Package> hydratePackages(Set<PackageHandle> packageNames, ClassLoader cl) {
		Set<Package> out = new LinkedHashSet<Package>(packageNames.size());
		for (PackageHandle n : packageNames) {
			out.add(n.hydrate(cl));
		}

		return out;
	}

	public static Set<AccessibleObject> hydrateMembers(Set<MemberHandle> members, ClassLoader cl) {
		Set<AccessibleObject> out = new LinkedHashSet<AccessibleObject>(members.size());
		for (MemberHandle n : members) {
			out.add(n.hydrate(cl));
		}

		return out;
	}

	public static Class<?> hydrateClass(String className, ClassLoader cl) {
		try {
			String name = className.replace("/", ".");
			Class<?> c = cl.loadClass(name);

			return c;
		} catch (ClassNotFoundException e) {
			throw new Pure4JException("Could not load class: ", e);
		}
	}

	public static Package hydratePackage(String name, String classInPackage, ClassLoader cl) {
		name = name.replace("/", ".");
		Package out = Package.getPackage(name);
		if (out == null) {
			try {
				Class<?> c = hydrateClass(classInPackage, cl);
				out = c.getPackage();

				if (out == null) {
					throw new Pure4JException("Hydrated " + classInPackage + " but package " + name
							+ " still not available");
				}

			} catch (Exception e) {
				throw new Pure4JException("Couldn't instantiate package object for: " + name, e);
			}

		}
		return out;
	}

	public static Class<?>[] hydrateParams(Type[] types, ClassLoader cl) {
		if ((types == null) || (types.length == 0)) {
			return new Class<?>[0];
		}

		Class<?>[] out = new Class<?>[types.length];
		for (int i = 0; i < types.length; i++) {
			Class<?> el = null;
			if (types[i].getSort() == Type.ARRAY) {
				int dimensions = types[i].getDimensions();
				el = loadElementType(types[i].getElementType(), cl);
				el = Array.newInstance(el, dimensions).getClass();
			} else {
				el = loadElementType(types[i], cl);
			}
			out[i] = el;
		}

		return out;
	}

	private static Class<?> loadElementType(Type type, ClassLoader cl) {
		if (type == Type.BOOLEAN_TYPE) {
			return boolean.class;
		} else if (type == Type.BYTE_TYPE) {
			return byte.class;
		} else if (type == Type.CHAR_TYPE) {
			return char.class;
		} else if (type == Type.DOUBLE_TYPE) {
			return double.class;
		} else if (type == Type.FLOAT_TYPE) {
			return float.class;
		} else if (type == Type.INT_TYPE) {
			return int.class;
		} else if (type == Type.LONG_TYPE) {
			return long.class;
		} else if (type == Type.SHORT_TYPE) {
			return short.class;
		} else if (type == Type.VOID_TYPE) {
			return void.class;
		} else {
			return hydrateClass(type.getClassName(), cl);
		}
	}

	public static Method hydrateMethod(MethodHandle method, ClassLoader cl) {
		Type[] args = Type.getArgumentTypes(method.getDesc());
		Class<?> c = hydrateClass(method.getClassName(), cl);
		Class<?>[] params = hydrateParams(args, cl);
		Method m = getDeclaredMethod(method.getName(), c, params, cl);

		if (m == null) {
			throw new Pure4JException("Could not find method: " + method.getName());
		}
		return m;

	}

	public static Method getDeclaredMethod(String name, Class<?> c, Class<?>[] params, ClassLoader cl) {
		Method m = null;
		try {
			m = c.getDeclaredMethod(name, params);
		} catch (NoSuchMethodException e) {
		}

		if ((m == null) && (!c.isInterface())) {
			m = getDeclaredMethod(name, c.getSuperclass(), params, cl);
		}

		if (m == null) {
			for (Class<?> intf : c.getInterfaces()) {
				if (m == null) {
					m = getDeclaredMethod(name, intf, params, cl);
				}
			}
		}

		return m;

	}

	public static Constructor<?> hydrateConstructor(ConstructorHandle con, ClassLoader cl) {
		try {
			Type[] args = Type.getArgumentTypes(con.getDesc());
			Class<?> c = hydrateClass(con.getClassName(), cl);
			Class<?>[] params = hydrateParams(args, cl);
			Constructor<?> co = c.getDeclaredConstructor(params);
			return co;
		} catch (NoSuchMethodException e) {
			throw new Pure4JException("Could not find constructor: ", e);
		}
	}

	public static Field hydrateField(FieldHandle field, ClassLoader cl) {
		Class<?> c = hydrateClass(field.getClassName(), cl);
		Field f = hydrateFieldOn(c, field.getName());
		if (f==null) {	
			throw new Pure4JException("Could not find field: "+field);
		}
		return f;
	}
	
	private static Field hydrateFieldOn(Class<?> c, String name) {
		if (c == null) {
			return null;
		}
		try {
			Field f = c.getDeclaredField(name);
			return f;
		} catch (NoSuchFieldException e) {
		}
		return hydrateFieldOn(c.getSuperclass(), name);
	}

	public static String convertClassName(Class<?> c) {
		return c.getName().replace(".", "/");
	}

	public static String convertClassName(String name) {
		return name.replace(".", "/");
	}

	public static String convertPackageName(Package p) {
		return p.getName().replace(".", "/");
	}
}
