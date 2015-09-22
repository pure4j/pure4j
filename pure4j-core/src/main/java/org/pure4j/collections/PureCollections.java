package org.pure4j.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.regex.Matcher;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Mutability;
import org.pure4j.annotations.pure.Pure;

public class PureCollections {

	/**
	 * Note that this doesn't use the passed array as that would violate purity.
	 */
	@SuppressWarnings("rawtypes")
	@Pure(Enforcement.FORCE)
	static public Object[] seqToNewArray(ISeq seq, Object[] passed) {
		int len = count(seq);
		Object[] dest = (Object[]) Array.newInstance(passed.getClass().getComponentType(), len);
		for (int i = 0; seq != null; ++i, seq = seq.next()) {
			dest[i] = seq.first();
		}
		return dest;
	}

	public static int count(Object o) {
		if (o instanceof Counted)
			return ((Counted) o).count();
		return countFrom(Util.ret1(o, o = null));
	}

	@SuppressWarnings("rawtypes")
	static int countFrom(Object o) {
		if (o == null)
			return 0;
		else if (o instanceof IPersistentCollection) {
			ISeq s = seq(o);
			o = null;
			int i = 0;
			for (; s != null; s = s.next()) {
				if (s instanceof Counted)
					return i + s.count();
				i++;
			}
			return i;
		} else if (o instanceof CharSequence)
			return ((CharSequence) o).length();
		else if (o instanceof Collection)
			return ((Collection) o).size();
		else if (o instanceof Map)
			return ((Map) o).size();
		else if (o instanceof Map.Entry)
			return 2;
		else if (o.getClass().isArray())
			return Array.getLength(o);

		throw new UnsupportedOperationException(
				"count not supported on this type: "
						+ o.getClass().getSimpleName());
	}

	@SuppressWarnings("unchecked")
	@Pure
	static public <K> ISeq<K> seq(Object coll) {
		Pure4J.immutable(coll);
		if (coll instanceof ASeq)
			return (ASeq<K>) coll;
		else
			return seqFrom(coll);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Pure
	static <K> ISeq<K> seqFrom(Object coll) {
		Pure4J.immutable(coll);
		if (coll instanceof Seqable)
			return ((Seqable) coll).seq();
		else if (coll == null)
			return null;
		else if (coll instanceof Iterable)
			return new IterableSeq(((Iterable) coll));
		else if (coll.getClass().isArray())
			return (ISeq<K>) ArraySeq.createFromArray(coll);
		else if (coll instanceof Map)
			return seq(((Map) coll).entrySet());
		else {
			Class c = coll.getClass();
			throw new IllegalArgumentException(
					"Don't know how to create ISeq from: " + c.getName());
		}
	}

	@Pure(params=Mutability.ANYTHING)
	@SuppressWarnings("rawtypes")
	static public Object[] seqToArray(ISeq seq) {
		int len = length(seq);
		Object[] ret = new Object[len];
		for (int i = 0; seq != null; ++i, seq = seq.next())
			ret[i] = seq.first();
		return ret;
	}

	@Pure
	@SuppressWarnings("rawtypes")
	static public int length(ISeq list) {
		int i = 0;
		for (ISeq c = list; c != null; c = c.next()) {
			i++;
		}
		return i;
	}

	@Pure(params=Mutability.ANYTHING)
	@SuppressWarnings("rawtypes")
	static public Object first(Object x) {
		Pure4J.immutable(x);
		if (x instanceof ISeq)
			return ((ISeq) x).first();
		ISeq seq = seq(x);
		if (seq == null)
			return null;
		return seq.first();
	}

	static public Object second(Object x) {
		return first(next(x));
	}

	static public Object third(Object x) {
		return first(next(next(x)));
	}

	static public Object fourth(Object x) {
		return first(next(next(next(x))));
	}

	@SuppressWarnings("rawtypes")
	static public ISeq next(Object x) {
		if (x instanceof ISeq)
			return ((ISeq) x).next();
		ISeq seq = seq(x);
		if (seq == null)
			return null;
		return seq.next();
	}

	@Pure(Enforcement.FORCE)
	@SuppressWarnings("rawtypes")
	static public Object nth(Object coll, int n) {
		if (coll instanceof Indexed)
			return ((Indexed) coll).nth(n);

		return nthFrom(Util.ret1(coll, coll = null), n);
	}

	@SuppressWarnings("rawtypes")
	static Object nthFrom(Object coll, int n) {
		if (coll == null)
			return null;
		else if (coll instanceof CharSequence)
			return Character.valueOf(((CharSequence) coll).charAt(n));
		else if (coll.getClass().isArray())
			return prepRet(coll.getClass().getComponentType(),
					Array.get(coll, n));
		else if (coll instanceof RandomAccess)
			return ((List) coll).get(n);
		else if (coll instanceof Matcher)
			return ((Matcher) coll).group(n);

		else if (coll instanceof Map.Entry) {
			Map.Entry e = (Map.Entry) coll;
			if (n == 0)
				return e.getKey();
			else if (n == 1)
				return e.getValue();
			throw new IndexOutOfBoundsException();
		}

		else if (coll instanceof Sequential) {
			ISeq seq = PureCollections.seq(coll);
			coll = null;
			for (int i = 0; i <= n && seq != null; ++i, seq = seq.next()) {
				if (i == n)
					return seq.first();
			}
			throw new IndexOutOfBoundsException();
		} else
			throw new UnsupportedOperationException(
					"nth not supported on this type: "
							+ coll.getClass().getSimpleName());
	}

	@SuppressWarnings("rawtypes")
	public static Object prepRet(Class c, Object x) {
		if (!(c.isPrimitive() || c == Boolean.class))
			return x;
		if (x instanceof Boolean)
			return ((Boolean) x) ? Boolean.TRUE : Boolean.FALSE;
		// else if(x instanceof Integer)
		// {
		// return ((Integer)x).longValue();
		// }
		// else if(x instanceof Float)
		// return Double.valueOf(((Float) x).doubleValue());
		return x;
	}

	@SuppressWarnings("rawtypes")
	static Object nthFrom(Object coll, int n, Object notFound) {
		if (coll == null)
			return notFound;
		else if (n < 0)
			return notFound;

		else if (coll instanceof CharSequence) {
			CharSequence s = (CharSequence) coll;
			if (n < s.length())
				return Character.valueOf(s.charAt(n));
			return notFound;
		} else if (coll.getClass().isArray()) {
			if (n < Array.getLength(coll))
				return prepRet(coll.getClass().getComponentType(),
						Array.get(coll, n));
			return notFound;
		} else if (coll instanceof RandomAccess) {
			List list = (List) coll;
			if (n < list.size())
				return list.get(n);
			return notFound;
		} else if (coll instanceof Matcher) {
			Matcher m = (Matcher) coll;
			if (n < m.groupCount())
				return m.group(n);
			return notFound;
		} else if (coll instanceof Sequential) {
			ISeq seq = PureCollections.seq(coll);
			coll = null;
			for (int i = 0; i <= n && seq != null; ++i, seq = seq.next()) {
				if (i == n)
					return seq.first();
			}
			return notFound;
		} else
			throw new UnsupportedOperationException(
					"nth not supported on this type: "
							+ coll.getClass().getSimpleName());
	}

	static public <K> IPersistentVector<K> subvec(IPersistentVector<K> v,
			int start, int end) {
		if (end < start || start < 0 || end > v.count())
			throw new IndexOutOfBoundsException();
		if (start == end)
			return PersistentVector.emptyVector();
		return new APersistentVector.SubVector<K>(v, start, end);
	}

	@Pure
	@SuppressWarnings("unchecked")
	static public <K> ISeq<K> cons(K x, Object coll) {
		Pure4J.immutable(x, coll);
		if (coll == null)
			return new PersistentList<K>(x, null, 1);
		else if (coll instanceof ISeq)
			return new Cons<K>(x, (ISeq<K>) coll);
		else
			return new Cons<K>(x, (ISeq<K>) seq(coll));
	}

	@Pure
	static public <K> ISeq<K> list(K arg1) {
		Pure4J.immutable(arg1);
		return new PersistentList<K>(arg1);
	}
}
