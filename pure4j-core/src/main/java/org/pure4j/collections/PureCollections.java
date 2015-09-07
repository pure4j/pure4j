package org.pure4j.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class PureCollections {

	// supports java Collection.toArray(T[])
	static public Object[] seqToPassedArray(ISeq seq, Object[] passed) {
		Object[] dest = passed;
		int len = count(seq);
		if (len > dest.length) {
			dest = (Object[]) Array.newInstance(passed.getClass()
					.getComponentType(), len);
		}
		for (int i = 0; seq != null; ++i, seq = seq.next())
			dest[i] = seq.first();
		if (len < passed.length) {
			dest[len] = null;
		}
		return dest;
	}

	public static int count(Object o) {
		if (o instanceof Counted)
			return ((Counted) o).count();
		return countFrom(Util.ret1(o, o = null));
	}

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

	static public ISeq seq(Object coll) {
		if (coll instanceof ASeq)
			return (ASeq) coll;
		else if (coll instanceof LazySeq)
			return ((LazySeq) coll).seq();
		else
			return seqFrom(coll);
	}

	static ISeq seqFrom(Object coll) {
		if (coll instanceof Seqable)
			return ((Seqable) coll).seq();
		else if (coll == null)
			return null;
		else if (coll instanceof Iterable)
			return chunkIteratorSeq(((Iterable) coll).iterator());
		else if (coll.getClass().isArray())
			return ArraySeq.createFromObject(coll);
		else if (coll instanceof CharSequence)
			return StringSeq.create((CharSequence) coll);
		else if (coll instanceof Map)
			return seq(((Map) coll).entrySet());
		else {
			Class c = coll.getClass();
			Class sc = c.getSuperclass();
			throw new IllegalArgumentException(
					"Don't know how to create ISeq from: " + c.getName());
		}
	}

	static public ISeq keys(Object coll) {
		if (coll instanceof IPersistentMap)
			return APersistentMap.KeySeq.createFromMap((IPersistentMap) coll);
		else
			return APersistentMap.KeySeq.create(seq(coll));
	}

	static public ISeq vals(Object coll) {
		if (coll instanceof IPersistentMap)
			return APersistentMap.ValSeq.createFromMap((IPersistentMap) coll);
		else
			return APersistentMap.ValSeq.create(seq(coll));
	}

	static public Object[] seqToArray(ISeq seq) {
		int len = length(seq);
		Object[] ret = new Object[len];
		for (int i = 0; seq != null; ++i, seq = seq.next())
			ret[i] = seq.first();
		return ret;
	}

	static public int length(ISeq list) {
		int i = 0;
		for (ISeq c = list; c != null; c = c.next()) {
			i++;
		}
		return i;
	}

	static public Object first(Object x) {
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

	static public ISeq next(Object x) {
		if (x instanceof ISeq)
			return ((ISeq) x).next();
		ISeq seq = seq(x);
		if (seq == null)
			return null;
		return seq.next();
	}

	static public Object nth(Object coll, int n) {
		if (coll instanceof Indexed)
			return ((Indexed) coll).nth(n);
		return nthFrom(Util.ret1(coll, coll = null), n);
	}
	
	@SuppressWarnings("unchecked")
	static public <K> IPersistentVector<K> subvec(IPersistentVector<K> v, int start, int end){
		if(end < start || start < 0 || end > v.count())
			throw new IndexOutOfBoundsException();
		if(start == end)
			return PersistentVector.EMPTY;
		return new APersistentVector.SubVector<K>(v, start, end);
	}
}
