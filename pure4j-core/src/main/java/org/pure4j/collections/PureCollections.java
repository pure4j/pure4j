package org.pure4j.collections;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.regex.Matcher;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class PureCollections {

	/**
	 * Note that this doesn't use the passed array as that would violate purity.
	 * @param seq A sequence
	 * @param passed the array to populate. Not used
	 * @return an array containing the elements of seq
	 */
	@SuppressWarnings("rawtypes")
	@Pure(Enforcement.FORCE)
	static public Object[] seqToNewArray(ISeq seq, Object[] passed, boolean reverse) {
		int len = seq == null ? 0 : seq.size();
		Object[] dest = (Object[]) Array.newInstance(passed.getClass().getComponentType(), len);
		for (int i = 0; seq != null; ++i, seq = seq.next()) {
			if (reverse) {
				dest[len - i - 1] = seq.first(); 
			} else {
				dest[i] = seq.first();
			}
		}
		return dest;
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
		else {
			Class c = coll.getClass();
			throw new IllegalArgumentException(
					"Don't know how to create ISeq from: " + c.getName());
		}
	}

	@Pure
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

	@SuppressWarnings("rawtypes")
	static public ISeq next(Object x) {
		if (x instanceof ISeq)
			return ((ISeq) x).next();
		ISeq seq = seq(x);
		if (seq == null)
			return null;
		return seq.next();
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
	
	/**
	 * Provides a pure implementation for sorting
	 * @param list elements to sort
	 * @param <T> element type
	 * @return a persistent vector in default sorted order
	 */	
	@Pure
	public static <T extends Comparable<? super T>> IPersistentVector<T> sort(IPersistentVector<T> list) {
		ITransientVector<T> tv = list.asTransient();
		Collections.sort(tv);
		return tv.persistent();
	}
	
	@Pure
	public static <T extends Comparable<? super T>> IPersistentVector<T> sort(ISeq<T> list) {
		TransientVector<T> tl = new TransientVector<T>(list);
		Collections.sort(tl);
		return tl.persistent();
	}

	
}
