package org.pure4j.collections;

import java.util.ArrayList;
import java.util.Collection;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

/**
 * Wrapper around ArrayList that prevents the sharing of any state with other
 * java objects.
 * 
 * @author robmoffat
 *
 * @param <K>
 */
final public class TransientVector<K> extends ATransientList<K> implements ITransientVector<K> {
		
	public TransientVector() {
		super(new ArrayList<K>());
	}
	
	@Pure(Enforcement.FORCE)
	public TransientVector(Collection<? extends K> c) {
		this(c.size());
		for (K k : c) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}
	
	@SafeVarargs
	@Pure(Enforcement.FORCE)
	public TransientVector(K... in) {
		this(in.length);
		for (K k : in) {
			Pure4J.immutable(k);
			this.add(k);
		}
	}

	public TransientVector(int initialCapacity) {
		super(new ArrayList<K>(initialCapacity));
	}

	public TransientVector(ISeq<K> list) {
		this(list.seq().size());
		ISeq<K> ss = list.seq();
		for (K k : ss) {
			add(k); 
		}
	}

	@Pure(Enforcement.FORCE)  // this is a short-cut to simply iterating over the whole contents, which would be pure.	
	@Override
	public IPersistentVector<K> persistent() {
		return new PersistentVector<K>((Collection<K>) this);
	}
	
}