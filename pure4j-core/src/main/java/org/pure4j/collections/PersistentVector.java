/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jul 5, 2007 */

package org.pure4j.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

/**
 * Purity notes: I've heavily used FORCE on this class and it's abstract just because I 
 * don't want to change the implementation and I trust the clojure guys.  
 *
 * @param <K> Element Type
 */ 
public class PersistentVector<K> extends APersistentVector<K> {

	@MutableUnshared
	public static class Node implements Serializable {
		
		transient private final AtomicReference<Thread> edit;
		@IgnoreImmutableTypeCheck
		private final Object[] array;

		private Node(AtomicReference<Thread> edit, Object[] array) {
			this.edit = edit;
			this.array = array;
		}

		private Node(AtomicReference<Thread> edit) {
			this.edit = edit;
			this.array = new Object[32];
		}
	}
	final static AtomicReference<Thread> NOEDIT = new AtomicReference<Thread>(null);

	public final static Node EMPTY_NODE = new Node(NOEDIT, new Object[32]);

	final int cnt;
	public final int shift;
	
	@IgnoreImmutableTypeCheck
	public final Node root;
	
	@IgnoreImmutableTypeCheck
	public final K[] tail;

	public final static PersistentVector<Object> EMPTY = new PersistentVector<Object>(0, 5,
			EMPTY_NODE, new Object[] {});

	@SuppressWarnings("unchecked")
	static public <K> IPersistentVector<K> create(ISeq<K> items) {
		K[] arr = (K[]) new Object[32];
		int i = 0;
		for (; items != null && i < 32; items = items.next())
			arr[i++] = items.first();

		if (items != null) { // >32, construct with array directly
			PersistentVector<K> start = new PersistentVector<K>(32, 5, EMPTY_NODE,
					arr);
			TransientVector<K> ret = start.asTransient();
			for (; items != null; items = items.next())
				ret.add(items.first());
			return ret.persistent();
		} else if (i == 32) { // exactly 32, skip copy
			return new PersistentVector<K>(32, 5, EMPTY_NODE, arr);
		} else { // <32, copy to minimum array and construct
			K[] arr2 = (K[]) new Object[i];
			System.arraycopy(arr, 0, arr2, 0, i);
			return new PersistentVector<K>(i, 5, EMPTY_NODE, arr2);
		}
	}

	@SuppressWarnings("unchecked")
	static public <K> IPersistentVector<K> create(List<K> list) {
		int size = list.size();
		if (size <= 32)
			return new PersistentVector<K>(size, 5, PersistentVector.EMPTY_NODE,
					(K[]) list.toArray());

		TransientVector<K> ret = (TransientVector<K>) EMPTY.asTransient();
		for (int i = 0; i < size; i++)
			ret.add(list.get(i));
		return ret.persistent();
	}

	@SuppressWarnings("unchecked")
	static public <K> IPersistentVector<K> create(Iterable<K> items) {
		// optimize common case
		if (items instanceof ArrayList)
			return create((ArrayList<K>) items);

		Iterator<K> iter = items.iterator();
		TransientVector<K> ret = (TransientVector<K>) EMPTY.asTransient();
		while (iter.hasNext())
			ret.add(iter.next());
		return ret.persistent();
	}

	@SuppressWarnings("unchecked")
	static public <K> IPersistentVector<K> create(K... items) {
		TransientVector<K> ret = (TransientVector<K>) EMPTY.asTransient();
		for (K item : items)
			ret.add(item);
		return ret.persistent();
	}
	
	public PersistentVector() {
		this(0, 5, EMPTY_NODE, (K[]) EMPTY.tail);
	}

	private PersistentVector(int cnt, int shift, Node root, K[] tail) {
		this.cnt = cnt;
		this.shift = shift;
		this.root = root;
		this.tail = tail;
	}

	@Pure(Enforcement.NOT_PURE)
	public TransientVector<K> asTransient() {
		return new TransientVector<K>(this);
	}

	final int tailoff() {
		if (cnt < 32)
			return 0;
		return ((cnt - 1) >>> 5) << 5;
	}

	@Pure(Enforcement.FORCE)
	@SuppressWarnings("unchecked")
	public K[] arrayFor(int i) {
		if (i >= 0 && i < cnt) {
			if (i >= tailoff())
				return tail;
			Node node = root;
			for (int level = shift; level > 0; level -= 5)
				node = (Node) node.array[(i >>> level) & 0x01f];
			return (K[]) node.array;
		}
		throw new IndexOutOfBoundsException();
	}

	public K nth(int i) {
		K[] node = arrayFor(i);
		return node[i & 0x01f];
	}

	public K nth(int i, K notFound) {
		Pure4J.immutable(notFound);
		if (i >= 0 && i < cnt)
			return nth(i);
		return notFound;
	}

	@Pure(Enforcement.FORCE)
	@SuppressWarnings("unchecked")
	public PersistentVector<K> assocN(int i, K val) {
		Pure4J.immutable(val);
		if (i >= 0 && i < cnt) {
			if (i >= tailoff()) {
				K[] newTail = (K[]) new Object[tail.length];
				System.arraycopy(tail, 0, newTail, 0, tail.length);
				newTail[i & 0x01f] = val;

				return new PersistentVector<K>(cnt, shift, root, newTail);
			}

			return new PersistentVector<K>(cnt, shift, doAssoc(shift,
					root, i, val), tail);
		}
		if (i == cnt)
			return cons(val);
		throw new IndexOutOfBoundsException();
	}

	@Pure(Enforcement.FORCE)
	private static Node doAssoc(int level, Node node, int i, Object val) {
		Node ret = new Node(node.edit, node.array.clone());
		if (level == 0) {
			ret.array[i & 0x01f] = val;
		} else {
			int subidx = (i >>> level) & 0x01f;
			ret.array[subidx] = doAssoc(level - 5, (Node) node.array[subidx],
					i, val);
		}
		return ret;
	}

	public int count() {
		return cnt;
	}

	@Pure(Enforcement.FORCE)
	@SuppressWarnings("unchecked")
	public PersistentVector<K> cons(K val) {
		Pure4J.immutable(val);
		// room in tail?
		// if(tail.length < 32)
		if (cnt - tailoff() < 32) {
			K[] newTail = (K[]) new Object[tail.length + 1];
			System.arraycopy(tail, 0, newTail, 0, tail.length);
			newTail[tail.length] = val;
			return new PersistentVector<K>(cnt + 1, shift, root, newTail);
		}
		// full tail, push into tree
		Node newroot;
		Node tailnode = new Node(root.edit, tail);
		int newshift = shift;
		// overflow root?
		if ((cnt >>> 5) > (1 << shift)) {
			newroot = new Node(root.edit);
			newroot.array[0] = root;
			newroot.array[1] = newPath(root.edit, shift, tailnode);
			newshift += 5;
		} else
			newroot = pushTail(shift, root, tailnode);
		return new PersistentVector<K>(cnt + 1, newshift, newroot,
				(K[]) new Object[] { val });
	}

	@Pure(Enforcement.FORCE)
	private Node pushTail(int level, Node parent, Node tailnode) {
		// if parent is leaf, insert node,
		// else does it map to an existing child? -> nodeToInsert = pushNode one
		// more level
		// else alloc new path
		// return nodeToInsert placed in copy of parent
		int subidx = ((cnt - 1) >>> level) & 0x01f;
		Node ret = new Node(parent.edit, parent.array.clone());
		Node nodeToInsert;
		if (level == 5) {
			nodeToInsert = tailnode;
		} else {
			Node child = (Node) parent.array[subidx];
			nodeToInsert = (child != null) ? pushTail(level - 5, child,
					tailnode) : newPath(root.edit, level - 5, tailnode);
		}
		ret.array[subidx] = nodeToInsert;
		return ret;
	}

	private static Node newPath(AtomicReference<Thread> edit, int level,
			Node node) {
		if (level == 0)
			return node;
		Node ret = new Node(edit);
		ret.array[0] = newPath(edit, level - 5, node);
		return ret;
	}

	public IChunkedSeq<K> chunkedSeq() {
		if (count() == 0)
			return null;
		return new ChunkedSeq<K>(this, 0, 0);
	}

	public ISeq<K> seq() {
		return chunkedSeq();
	}

	Iterator<K> rangedIterator(final int start, final int end) {
		return new IPureIterator<K>() {
			int i = start;
			int base = i - (i % 32);
			K[] array = (start < count()) ? arrayFor(i) : null;

			public boolean hasNext() {
				return i < end;
			}

			public K next() {
				if (i - base == 32) {
					array = arrayFor(i);
					base += 32;
				}
				return array[i++ & 0x01f];
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public Iterator<K> iterator() {
		return rangedIterator(0, count());
	}

	static public final class ChunkedSeq<K> extends ASeq<K> implements IChunkedSeq<K>,
			Counted {

		public final PersistentVector<K> vec;
		@IgnoreImmutableTypeCheck
		final K[] node;
		final int i;
		public final int offset;

		public ChunkedSeq(PersistentVector<K> vec, int i, int offset) {
			Pure4J.immutable(vec);
			this.vec = vec;
			this.i = i;
			this.offset = offset;
			this.node = vec.arrayFor(i);
		}

		private ChunkedSeq(PersistentVector<K> vec, K[] node, int i, int offset) {
			this.vec = vec;
			this.node = node;
			this.i = i;
			this.offset = offset;
		}

		@Pure(Enforcement.FORCE)
		public IChunk<K> chunkedFirst() {
			return new ArrayChunk<K>(node, offset);
		}

		public ISeq<K> chunkedNext() {
			if (i + node.length < vec.cnt)
				return new ChunkedSeq<K>(vec, i + node.length, 0);
			return null;
		}

		public ISeq<K> chunkedMore() {
			ISeq<K> s = chunkedNext();
			if (s == null)
				return PersistentList.emptySeq();
			return s;
		}

		public K first() {
			return node[offset];
		}

		public ISeq<K> next() {
			if (offset + 1 < node.length)
				return new ChunkedSeq<K>(vec, node, i, offset + 1);
			return chunkedNext();
		}

		public int count() {
			return vec.cnt - (i + offset);
		}
	}

	@SuppressWarnings("unchecked")
	public IPersistentCollection<K> empty() {
		return (IPersistentCollection<K>) EMPTY;
	}
	
	@SuppressWarnings("unchecked")
	@Pure
	public static <K> PersistentVector<K> emptyVector() {
		return (PersistentVector<K>) EMPTY;
	}

	@Pure(Enforcement.FORCE)
	@SuppressWarnings("unchecked")
	public PersistentVector<K> pop() {
		if (cnt == 0)
			throw new IllegalStateException("Can't pop empty vector");
		if (cnt == 1)
			return (PersistentVector<K>) EMPTY;
		// if(tail.length > 1)
		if (cnt - tailoff() > 1) {
			K[] newTail = (K[]) new Object[tail.length - 1];
			System.arraycopy(tail, 0, newTail, 0, newTail.length);
			return new PersistentVector<K>(cnt - 1, shift, root, newTail);
		}
		K[] newtail = arrayFor(cnt - 2);

		Node newroot = popTail(shift, root);
		int newshift = shift;
		if (newroot == null) {
			newroot = EMPTY_NODE;
		}
		if (shift > 5 && newroot.array[1] == null) {
			newroot = (Node) newroot.array[0];
			newshift -= 5;
		}
		return new PersistentVector<K>(cnt - 1, newshift, newroot, newtail);
	}

	@Pure(Enforcement.FORCE)
	private Node popTail(int level, Node node) {
		int subidx = ((cnt - 2) >>> level) & 0x01f;
		if (level > 5) {
			Node newchild = popTail(level - 5, (Node) node.array[subidx]);
			if (newchild == null && subidx == 0)
				return null;
			else {
				Node ret = new Node(root.edit, node.array.clone());
				ret.array[subidx] = newchild;
				return ret;
			}
		} else if (subidx == 0)
			return null;
		else {
			Node ret = new Node(root.edit, node.array.clone());
			ret.array[subidx] = null;
			return ret;
		}
	}

}
