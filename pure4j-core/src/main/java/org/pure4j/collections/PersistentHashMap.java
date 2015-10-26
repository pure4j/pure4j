/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package org.pure4j.collections;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

/*
 A persistent rendition of Phil Bagwell's Hash Array Mapped Trie

 Uses path copying for persistence
 HashCollision leaves vs. extended hashing
 Node polymorphism vs. conditionals
 No sub-tree pools or root-resizing
 Any errors are my own
 */

public class PersistentHashMap<K, V> extends APersistentMap<K, V> implements IMapIterable<K, V> {

	private static final long serialVersionUID = -5413707354958055094L;
	final int count;
	
	@IgnoreImmutableTypeCheck
	final INode root;
	final boolean hasNull;
	
	@IgnoreImmutableTypeCheck
	final V nullValue;

	final private static PersistentHashMap<Object, Object> EMPTY = new PersistentHashMap<Object, Object>(0,
			null, false, null);
	
	@IgnoreImmutableTypeCheck
	final private static Object NOT_FOUND = new Object();
	
	public PersistentHashMap() {
		this(0, null, false, null);
	}

	@Pure
	private static <K, V> TemporaryHashMap<K, V> createTemporary(Iterable<Entry<K, V>> other) {
		TemporaryHashMap<K, V> ret = new TemporaryHashMap<K,V>();
		for (Entry<K,V> o : other) {
			Pure4J.immutable(o.getKey(), o.getValue());
			ret = (TemporaryHashMap<K, V>) ret.assoc(o.getKey(), o.getValue());
		}
		return ret;
	}
	
	@Pure
	private static <K, V> TemporaryHashMap<K, V> createTemporary(ISeq<Map.Entry<K, V>> other) {
		TemporaryHashMap<K, V> ret = new TemporaryHashMap<K,V>();
		for (Entry<K,V> o : other) {
			ret = (TemporaryHashMap<K, V>) ret.assoc(o.getKey(), o.getValue());
		}
		return ret;
	}
	
	private PersistentHashMap(TemporaryHashMap<K, V> in) {
		this(in.count, in.root, in.hasNull, in.nullValue);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Pure(Enforcement.FORCE)
	@SafeVarargs
	public PersistentHashMap(K... pairs) {
		this((TemporaryHashMap) createTemporary(pairs));
	}

	
	@Pure
	@SuppressWarnings("unchecked")
	private static <K, V> TemporaryHashMap<K, V> createTemporary(K[] pairs) {
		Pure4J.immutableArray(pairs);
		TemporaryHashMap<K, V> ret = new TemporaryHashMap<K, V>();
		if (pairs.length % 2 != 0) {
			throw new IllegalArgumentException("Argument must supply key/value pairs");
		}
		for (int i = 0; i < pairs.length; i=i+2) {
			ret.put((K) pairs[i], (V) pairs[i+1]);	
		}
		return ret;
	}
	
	@Pure
	@SuppressWarnings("unchecked")
	public static <K, V> PersistentHashMap<K, V> emptyMap() {
		return (PersistentHashMap<K, V>) EMPTY;
	}

	private PersistentHashMap(int count, INode root, boolean hasNull, V nullValue) {
		this.count = count;
		this.root = root;
		this.hasNull = hasNull;
		this.nullValue = nullValue;
	}

	public PersistentHashMap(ISeq<Map.Entry<K, V>> items) {
		this(createTemporary(items));
	}
	
	public PersistentHashMap(IPersistentMap<K, V> in) {
		this(in.seq());
	}
	
	@Pure(Enforcement.FORCE)
	public PersistentHashMap(Map<K, V> in) {
		this(createTemporary(in.entrySet()));
	}

	@Pure
	static int hash(Object k) {
		Pure4J.immutable(k);
		return Pure4J.hashCode(k);		// should be a less expensive way to do this
	}

	public boolean containsKey(Object key) {
		Pure4J.immutable(key);
		if (key == null)
			return hasNull;
		return (root != null) ? root.find(0, hash(key), key, NOT_FOUND) != NOT_FOUND
				: false;
	}

	@SuppressWarnings("unchecked")
	public IMapEntry<K, V> entryAt(Object key) {
		Pure4J.immutable(key);
		if (key == null)
			return hasNull ? new MapEntry<K, V>(null, nullValue) : null;
		return (root != null) ? root.find(0, hash(key), key) : null;
	}

	@Pure(Enforcement.FORCE)
	/*
	 * Not pure, specifically because of Box.
	 * It would be fairly easy to re-write Box so that it has getters and setters, and 
	 * assures it's purity.  However, it's part of the implementation, so I won't do this now.
	 */
	public PersistentHashMap<K, V> assoc(K key, V val) {
		Pure4J.immutable(key, val);
		if (key == null) {
			if (hasNull && val == nullValue)
				return this;
			return new PersistentHashMap<K, V>(hasNull ? count : count + 1,
					root, true, val);
		}
		Box addedLeaf = new Box(null);
		INode newroot = (root == null ? BitmapIndexedNode.EMPTY : root).assoc(
				0, hash(key), key, val, addedLeaf);
		if (newroot == root)
			return this;
		return new PersistentHashMap<K, V>(addedLeaf.val == null ? count
				: count + 1, newroot, hasNull, nullValue);
	}

	@SuppressWarnings("unchecked")
	public V get(Object key, V notFound) {
		Pure4J.immutable(key, notFound);
		if (key == null)
			return hasNull ? nullValue : notFound;
		return (V) (root != null ? root.find(0, hash(key), key, notFound) : notFound);
	}

	public V get(Object key) {
		Pure4J.immutable(key);
		return get(key, null);
	}

	public PersistentHashMap<K,V> assocEx(K key, V val) {
		Pure4J.immutable(key, val);
		if (containsKey(key))
			throw Util.runtimeException("Key already present");
		return assoc(key, val);
	}

	public PersistentHashMap<K, V> without(Object key) {
		Pure4J.immutable(key);
		if (key == null)
			return hasNull ? new PersistentHashMap<K, V>(count - 1, root,
					false, null) : this;
		if (root == null)
			return this;
		INode newroot = root.without(0, hash(key), key);
		if (newroot == root)
			return this;
		return new PersistentHashMap<K, V>(count - 1, newroot, hasNull,
				nullValue);
	}

	@IgnoreImmutableTypeCheck
	static final IPureIterator<Object> EMPTY_ITER = new IPureIterator<Object>() {
		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
	
	@SuppressWarnings("unchecked")
	public static <X> IPureIterator<X> emptyIter() {
		return (IPureIterator<X>) EMPTY_ITER;
	}

	@SuppressWarnings("unchecked")
	public Iterator<Entry<K,V>> iterator() {
		final Iterator<?> rootIter = (root == null) ? EMPTY_ITER : root
				.iterator();
		if (hasNull) {
			return new IPureIterator<Entry<K, V>>() {
				private boolean seen = false;

				public boolean hasNext() {
					if (!seen)
						return true;
					else
						return rootIter.hasNext();
				}

				public Entry<K, V> next() {
					if (!seen) {
						seen = true;
						return (Entry<K, V>)  nullValue;
					} else
						return (Entry<K, V>) rootIter.next();
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else
			return (Iterator<Entry<K, V>>) rootIter;
	}

	public IPureIterator<K> keyIterator() {
		return KeySeq.create(seq()).iterator();
	}
	
	public IPureIterator<V> valIterator() {
		return ValSeq.create(seq()).iterator();
	}

	public int size() {
		return count;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ISeq<Entry<K, V>> seq() {
		ISeq<Entry<K, V>> s = root != null ? root.nodeSeq() : null;
		MapEntry<K, V> first = new MapEntry<K, V>(null, nullValue);
		return hasNull ? new Cons(first, s) : s;
	}

	public PersistentHashMap<K, V> empty() {
		return emptyMap();
	}

	static int mask(int hash, int shift) {
		// return ((hash << shift) >>> 27);// & 0x01f;
		return (hash >>> shift) & 0x01f;
	}

	public TransientHashMap<K, V> asTransient() {
		return new TransientHashMap<K, V>(this);
	}

	
	private static final class TemporaryHashMap<K, V> {
		final AtomicReference<Thread> edit;
		volatile INode root;
		volatile int count;
		volatile boolean hasNull;
		volatile V nullValue;
		final Box leafFlag = new Box(null);

		public final TemporaryHashMap<K,V> assoc(K key, V val) {
			ensureEditable();
			return doAssoc(key, val);
		}

		public V put(K key, V value) {
			doAssoc(key, value);
			return value;
		}

		TemporaryHashMap(AtomicReference<Thread> edit, INode root, int count,
				boolean hasNull, V nullValue) {
			this.edit = edit;
			this.root = root;
			this.count = count;
			this.hasNull = hasNull;
			this.nullValue = nullValue;
		}
		
		TemporaryHashMap() {
			this(new AtomicReference<Thread>(Thread.currentThread()), null, 0, false, null);
		}

		TemporaryHashMap<K, V> doAssoc(K key, V val) {
			if (key == null) {
				if (this.nullValue != val)
					this.nullValue = val;
				if (!hasNull) {
					this.count++;
					this.hasNull = true;
				}
				return this;
			}
			// Box leafFlag = new Box(null);
			leafFlag.val = null;
			INode n = (root == null ? BitmapIndexedNode.EMPTY : root).assoc(
					edit, 0, hash(key), key, val, leafFlag);
			if (n != this.root)
				this.root = n;
			if (leafFlag.val != null)
				this.count++;
			return this;
		}

		void ensureEditable() {
			if (edit.get() == null)
				throw new IllegalAccessError(
						"Transient used after persistent! call");
		}
	}

	@MutableUnshared
	@SuppressWarnings({"rawtypes"})
	static interface INode extends Serializable {
		INode assoc(int shift, int hash, Object key, Object val, Box addedLeaf);

		INode without(int shift, int hash, Object key);

		IMapEntry find(int shift, int hash, Object key);

		Object find(int shift, int hash, Object key, Object notFound);

		ISeq nodeSeq();

		INode assoc(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Object val, Box addedLeaf);

		INode without(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Box removedLeaf);

		// returns the result of (f [k v]) for each iterated element
		Iterator iterator();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	final static class ArrayNode implements INode {
		int count;
		final INode[] array;
		final AtomicReference<Thread> edit;

		ArrayNode(AtomicReference<Thread> edit, int count, INode[] array) {
			this.array = array;
			this.edit = edit;
			this.count = count;
		}

		public INode assoc(int shift, int hash, Object key, Object val,
				Box addedLeaf) {
			int idx = mask(hash, shift);
			INode node = array[idx];
			if (node == null)
				return new ArrayNode(null, count + 1, cloneAndSet(array, idx,
						BitmapIndexedNode.EMPTY.assoc(shift + 5, hash, key,
								val, addedLeaf)));
			INode n = node.assoc(shift + 5, hash, key, val, addedLeaf);
			if (n == node)
				return this;
			return new ArrayNode(null, count, cloneAndSet(array, idx, n));
		}

		public INode without(int shift, int hash, Object key) {
			int idx = mask(hash, shift);
			INode node = array[idx];
			if (node == null)
				return this;
			INode n = node.without(shift + 5, hash, key);
			if (n == node)
				return this;
			if (n == null) {
				if (count <= 8) // shrink
					return pack(null, idx);
				return new ArrayNode(null, count - 1,
						cloneAndSet(array, idx, n));
			} else
				return new ArrayNode(null, count, cloneAndSet(array, idx, n));
		}

		public IMapEntry find(int shift, int hash, Object key) {
			int idx = mask(hash, shift);
			INode node = array[idx];
			if (node == null)
				return null;
			return node.find(shift + 5, hash, key);
		}

		public Object find(int shift, int hash, Object key, Object notFound) {
			int idx = mask(hash, shift);
			INode node = array[idx];
			if (node == null)
				return notFound;
			return node.find(shift + 5, hash, key, notFound);
		}

		public ISeq nodeSeq() {
			return Seq.create(array);
		}

		public Iterator<Object> iterator() {
			return new Iter(array);
		}


		private ArrayNode ensureEditable(AtomicReference<Thread> edit) {
			if (this.edit == edit)
				return this;
			return new ArrayNode(edit, count, this.array.clone());
		}

		private ArrayNode editAndSet(AtomicReference<Thread> edit, int i,
				INode n) {
			ArrayNode editable = ensureEditable(edit);
			editable.array[i] = n;
			return editable;
		}

		private INode pack(AtomicReference<Thread> edit, int idx) {
			Object[] newArray = new Object[2 * (count - 1)];
			int j = 1;
			int bitmap = 0;
			for (int i = 0; i < idx; i++)
				if (array[i] != null) {
					newArray[j] = array[i];
					bitmap |= 1 << i;
					j += 2;
				}
			for (int i = idx + 1; i < array.length; i++)
				if (array[i] != null) {
					newArray[j] = array[i];
					bitmap |= 1 << i;
					j += 2;
				}
			return new BitmapIndexedNode(edit, bitmap, newArray);
		}

		public INode assoc(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Object val, Box addedLeaf) {
			int idx = mask(hash, shift);
			INode node = array[idx];
			if (node == null) {
				ArrayNode editable = editAndSet(edit, idx,
						BitmapIndexedNode.EMPTY.assoc(edit, shift + 5, hash,
								key, val, addedLeaf));
				editable.count++;
				return editable;
			}
			INode n = node.assoc(edit, shift + 5, hash, key, val, addedLeaf);
			if (n == node)
				return this;
			return editAndSet(edit, idx, n);
		}

		public INode without(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Box removedLeaf) {
			int idx = mask(hash, shift);
			INode node = array[idx];
			if (node == null)
				return this;
			INode n = node.without(edit, shift + 5, hash, key, removedLeaf);
			if (n == node)
				return this;
			if (n == null) {
				if (count <= 8) // shrink
					return pack(edit, idx);
				ArrayNode editable = editAndSet(edit, idx, n);
				editable.count--;
				return editable;
			}
			return editAndSet(edit, idx, n);
		}

		static class Seq extends ASeq {
			final INode[] nodes;
			final int i;
			final ISeq s;

			static ISeq create(INode[] nodes) {
				return create(nodes, 0, null);
			}

			private static ISeq create(INode[] nodes,
					int i, ISeq s) {
				if (s != null)
					return new Seq(nodes, i, s);
				for (int j = i; j < nodes.length; j++)
					if (nodes[j] != null) {
						ISeq ns = nodes[j].nodeSeq();
						if (ns != null)
							return new Seq(nodes, j + 1, ns);
					}
				return null;
			}

			private Seq(INode[] nodes, int i, ISeq s) {
				this.nodes = nodes;
				this.i = i;
				this.s = s;
			}

			public Object first() {
				return s.first();
			}

			public ISeq next() {
				return create(nodes, i, s.next());
			}

		}

		static class Iter implements Iterator {
			private final INode[] array;
			private int i = 0;
			private Iterator nestedIter;

			private Iter(INode[] array) {
				this.array = array;
			}

			public boolean hasNext() {
				while (true) {
					if (nestedIter != null)
						if (nestedIter.hasNext())
							return true;
						else
							nestedIter = null;

					if (i < array.length) {
						INode node = array[i++];
						if (node != null)
							nestedIter = node.iterator();
					} else
						return false;
				}
			}

			public Object next() {
				if (hasNext())
					return nestedIter.next();
				else
					throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	final static class BitmapIndexedNode implements INode {
		static final BitmapIndexedNode EMPTY = new BitmapIndexedNode(null, 0,
				new Object[0]);

		int bitmap;
		Object[] array;
		final AtomicReference<Thread> edit;

		final int index(int bit) {
			return Integer.bitCount(bitmap & (bit - 1));
		}

		BitmapIndexedNode(AtomicReference<Thread> edit, int bitmap,
				Object[] array) {
			this.bitmap = bitmap;
			this.array = array;
			this.edit = edit;
		}

		public INode assoc(int shift, int hash, Object key, Object val,
				Box addedLeaf) {
			int bit = bitpos(hash, shift);
			int idx = index(bit);
			if ((bitmap & bit) != 0) {
				Object keyOrNull = array[2 * idx];
				Object valOrNode = array[2 * idx + 1];
				if (keyOrNull == null) {
					INode n = ((INode) valOrNode).assoc(shift + 5, hash, key,
							val, addedLeaf);
					if (n == valOrNode)
						return this;
					return new BitmapIndexedNode(null, bitmap, cloneAndSet(
							array, 2 * idx + 1, n));
				}
				if (Util.equals(key, keyOrNull)) {
					if (val == valOrNode)
						return this;
					return new BitmapIndexedNode(null, bitmap, cloneAndSet(
							array, 2 * idx + 1, val));
				}
				addedLeaf.val = addedLeaf;
				return new BitmapIndexedNode(null, bitmap, cloneAndSet(
						array,
						2 * idx,
						null,
						2 * idx + 1,
						createNode(shift + 5, keyOrNull, valOrNode, hash, key,
								val)));
			} else {
				int n = Integer.bitCount(bitmap);
				if (n >= 16) {
					INode[] nodes = new INode[32];
					int jdx = mask(hash, shift);
					nodes[jdx] = EMPTY.assoc(shift + 5, hash, key, val,
							addedLeaf);
					int j = 0;
					for (int i = 0; i < 32; i++)
						if (((bitmap >>> i) & 1) != 0) {
							if (array[j] == null)
								nodes[i] = (INode) array[j + 1];
							else
								nodes[i] = EMPTY.assoc(shift + 5,
										hash(array[j]), array[j], array[j + 1],
										addedLeaf);
							j += 2;
						}
					return new ArrayNode(null, n + 1, nodes);
				} else {
					Object[] newArray = new Object[2 * (n + 1)];
					System.arraycopy(array, 0, newArray, 0, 2 * idx);
					newArray[2 * idx] = key;
					addedLeaf.val = addedLeaf;
					newArray[2 * idx + 1] = val;
					System.arraycopy(array, 2 * idx, newArray, 2 * (idx + 1),
							2 * (n - idx));
					return new BitmapIndexedNode(null, bitmap | bit, newArray);
				}
			}
		}

		public INode without(int shift, int hash, Object key) {
			int bit = bitpos(hash, shift);
			if ((bitmap & bit) == 0)
				return this;
			int idx = index(bit);
			Object keyOrNull = array[2 * idx];
			Object valOrNode = array[2 * idx + 1];
			if (keyOrNull == null) {
				INode n = ((INode) valOrNode).without(shift + 5, hash, key);
				if (n == valOrNode)
					return this;
				if (n != null)
					return new BitmapIndexedNode(null, bitmap, cloneAndSet(
							array, 2 * idx + 1, n));
				if (bitmap == bit)
					return null;
				return new BitmapIndexedNode(null, bitmap ^ bit, removePair(
						array, idx));
			}
			if (Util.equals(key, keyOrNull))
				// TODO: collapse
				return new BitmapIndexedNode(null, bitmap ^ bit, removePair(
						array, idx));
			return this;
		}

		public IMapEntry find(int shift, int hash, Object key) {
			int bit = bitpos(hash, shift);
			if ((bitmap & bit) == 0)
				return null;
			int idx = index(bit);
			Object keyOrNull = array[2 * idx];
			Object valOrNode = array[2 * idx + 1];
			if (keyOrNull == null)
				return ((INode) valOrNode).find(shift + 5, hash, key);
			if (Util.equals(key, keyOrNull))
				return new MapEntry(keyOrNull, valOrNode);
			return null;
		}

		public Object find(int shift, int hash, Object key, Object notFound) {
			int bit = bitpos(hash, shift);
			if ((bitmap & bit) == 0)
				return notFound;
			int idx = index(bit);
			Object keyOrNull = array[2 * idx];
			Object valOrNode = array[2 * idx + 1];
			if (keyOrNull == null)
				return ((INode) valOrNode).find(shift + 5, hash, key, notFound);
			if (Util.equals(key, keyOrNull))
				return valOrNode;
			return notFound;
		}

		public ISeq<Object> nodeSeq() {
			return NodeSeq.create(array);
		}

		public Iterator<Object> iterator() {
			return new NodeIter(array);
		}

		private BitmapIndexedNode ensureEditable(AtomicReference<Thread> edit) {
			if (this.edit == edit)
				return this;
			int n = Integer.bitCount(bitmap);
			Object[] newArray = new Object[n >= 0 ? 2 * (n + 1) : 4]; // make
																		// room
																		// for
																		// next
																		// assoc
			System.arraycopy(array, 0, newArray, 0, 2 * n);
			return new BitmapIndexedNode(edit, bitmap, newArray);
		}

		private BitmapIndexedNode editAndSet(AtomicReference<Thread> edit,
				int i, Object a) {
			BitmapIndexedNode editable = ensureEditable(edit);
			editable.array[i] = a;
			return editable;
		}

		private BitmapIndexedNode editAndSet(AtomicReference<Thread> edit,
				int i, Object a, int j, Object b) {
			BitmapIndexedNode editable = ensureEditable(edit);
			editable.array[i] = a;
			editable.array[j] = b;
			return editable;
		}

		private BitmapIndexedNode editAndRemovePair(
				AtomicReference<Thread> edit, int bit, int i) {
			if (bitmap == bit)
				return null;
			BitmapIndexedNode editable = ensureEditable(edit);
			editable.bitmap ^= bit;
			System.arraycopy(editable.array, 2 * (i + 1), editable.array,
					2 * i, editable.array.length - 2 * (i + 1));
			editable.array[editable.array.length - 2] = null;
			editable.array[editable.array.length - 1] = null;
			return editable;
		}

		public INode assoc(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Object val, Box addedLeaf) {
			int bit = bitpos(hash, shift);
			int idx = index(bit);
			if ((bitmap & bit) != 0) {
				Object keyOrNull = array[2 * idx];
				Object valOrNode = array[2 * idx + 1];
				if (keyOrNull == null) {
					INode n = ((INode) valOrNode).assoc(edit, shift + 5, hash,
							key, val, addedLeaf);
					if (n == valOrNode)
						return this;
					return editAndSet(edit, 2 * idx + 1, n);
				}
				if (Util.equals(key, keyOrNull)) {
					if (val == valOrNode)
						return this;
					return editAndSet(edit, 2 * idx + 1, val);
				}
				addedLeaf.val = addedLeaf;
				return editAndSet(
						edit,
						2 * idx,
						null,
						2 * idx + 1,
						createNode(edit, shift + 5, keyOrNull, valOrNode, hash,
								key, val));
			} else {
				int n = Integer.bitCount(bitmap);
				if (n * 2 < array.length) {
					addedLeaf.val = addedLeaf;
					BitmapIndexedNode editable = ensureEditable(edit);
					System.arraycopy(editable.array, 2 * idx, editable.array,
							2 * (idx + 1), 2 * (n - idx));
					editable.array[2 * idx] = key;
					editable.array[2 * idx + 1] = val;
					editable.bitmap |= bit;
					return editable;
				}
				if (n >= 16) {
					INode[] nodes = new INode[32];
					int jdx = mask(hash, shift);
					nodes[jdx] = EMPTY.assoc(edit, shift + 5, hash, key, val,
							addedLeaf);
					int j = 0;
					for (int i = 0; i < 32; i++)
						if (((bitmap >>> i) & 1) != 0) {
							if (array[j] == null)
								nodes[i] = (INode) array[j + 1];
							else
								nodes[i] = EMPTY.assoc(edit, shift + 5,
										hash(array[j]), array[j], array[j + 1],
										addedLeaf);
							j += 2;
						}
					return new ArrayNode(edit, n + 1, nodes);
				} else {
					Object[] newArray = new Object[2 * (n + 4)];
					System.arraycopy(array, 0, newArray, 0, 2 * idx);
					newArray[2 * idx] = key;
					addedLeaf.val = addedLeaf;
					newArray[2 * idx + 1] = val;
					System.arraycopy(array, 2 * idx, newArray, 2 * (idx + 1),
							2 * (n - idx));
					BitmapIndexedNode editable = ensureEditable(edit);
					editable.array = newArray;
					editable.bitmap |= bit;
					return editable;
				}
			}
		}

		public INode without(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Box removedLeaf) {
			int bit = bitpos(hash, shift);
			if ((bitmap & bit) == 0)
				return this;
			int idx = index(bit);
			Object keyOrNull = array[2 * idx];
			Object valOrNode = array[2 * idx + 1];
			if (keyOrNull == null) {
				INode n = ((INode) valOrNode).without(edit, shift + 5, hash,
						key, removedLeaf);
				if (n == valOrNode)
					return this;
				if (n != null)
					return editAndSet(edit, 2 * idx + 1, n);
				if (bitmap == bit)
					return null;
				return editAndRemovePair(edit, bit, idx);
			}
			if (Util.equals(key, keyOrNull)) {
				removedLeaf.val = removedLeaf;
				// TODO: collapse
				return editAndRemovePair(edit, bit, idx);
			}
			return this;
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	final static class HashCollisionNode implements INode {

		final int hash;
		int count;
		Object[] array;
		final AtomicReference<Thread> edit;

		HashCollisionNode(AtomicReference<Thread> edit, int hash, int count,
				Object... array) {
			this.edit = edit;
			this.hash = hash;
			this.count = count;
			this.array = array;
		}

		public INode assoc(int shift, int hash, Object key, Object val,
				Box addedLeaf) {
			if (hash == this.hash) {
				int idx = findIndex(key);
				if (idx != -1) {
					if (array[idx + 1] == val)
						return this;
					return new HashCollisionNode(null, hash, count,
							cloneAndSet(array, idx + 1, val));
				}
				Object[] newArray = new Object[2 * (count + 1)];
				System.arraycopy(array, 0, newArray, 0, 2 * count);
				newArray[2 * count] = key;
				newArray[2 * count + 1] = val;
				addedLeaf.val = addedLeaf;
				return new HashCollisionNode(edit, hash, count + 1, newArray);
			}
			// nest it in a bitmap node
			return new BitmapIndexedNode(null, bitpos(this.hash, shift),
					new Object[] { null, this }).assoc(shift, hash, key, val,
					addedLeaf);
		}

		public INode without(int shift, int hash, Object key) {
			int idx = findIndex(key);
			if (idx == -1)
				return this;
			if (count == 1)
				return null;
			return new HashCollisionNode(null, hash, count - 1, removePair(
					array, idx / 2));
		}

		public IMapEntry find(int shift, int hash, Object key) {
			int idx = findIndex(key);
			if (idx < 0)
				return null;
			if (Util.equals(key, array[idx]))
				return new MapEntry(array[idx], array[idx + 1]);
			return null;
		}

		public Object find(int shift, int hash, Object key, Object notFound) {
			int idx = findIndex(key);
			if (idx < 0)
				return notFound;
			if (Util.equals(key, array[idx]))
				return array[idx + 1];
			return notFound;
		}

		public ISeq nodeSeq() {
			return NodeSeq.create(array);
		}

		public Iterator iterator() {
			return new NodeIter(array);
		}

		public int findIndex(Object key) {
			for (int i = 0; i < 2 * count; i += 2) {
				if (Util.equals(key, array[i]))
					return i;
			}
			return -1;
		}

		private HashCollisionNode ensureEditable(AtomicReference<Thread> edit) {
			if (this.edit == edit)
				return this;
			Object[] newArray = new Object[2 * (count + 1)]; // make room for
																// next assoc
			System.arraycopy(array, 0, newArray, 0, 2 * count);
			return new HashCollisionNode(edit, hash, count, newArray);
		}

		private HashCollisionNode ensureEditable(AtomicReference<Thread> edit,
				int count, Object[] array) {
			if (this.edit == edit) {
				this.array = array;
				this.count = count;
				return this;
			}
			return new HashCollisionNode(edit, hash, count, array);
		}

		private HashCollisionNode editAndSet(AtomicReference<Thread> edit,
				int i, Object a) {
			HashCollisionNode editable = ensureEditable(edit);
			editable.array[i] = a;
			return editable;
		}

		private HashCollisionNode editAndSet(AtomicReference<Thread> edit,
				int i, Object a, int j, Object b) {
			HashCollisionNode editable = ensureEditable(edit);
			editable.array[i] = a;
			editable.array[j] = b;
			return editable;
		}

		public INode assoc(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Object val, Box addedLeaf) {
			if (hash == this.hash) {
				int idx = findIndex(key);
				if (idx != -1) {
					if (array[idx + 1] == val)
						return this;
					return editAndSet(edit, idx + 1, val);
				}
				if (array.length > 2 * count) {
					addedLeaf.val = addedLeaf;
					HashCollisionNode editable = editAndSet(edit, 2 * count,
							key, 2 * count + 1, val);
					editable.count++;
					return editable;
				}
				Object[] newArray = new Object[array.length + 2];
				System.arraycopy(array, 0, newArray, 0, array.length);
				newArray[array.length] = key;
				newArray[array.length + 1] = val;
				addedLeaf.val = addedLeaf;
				return ensureEditable(edit, count + 1, newArray);
			}
			// nest it in a bitmap node
			return new BitmapIndexedNode(edit, bitpos(this.hash, shift),
					new Object[] { null, this, null, null }).assoc(edit, shift,
					hash, key, val, addedLeaf);
		}

		public INode without(AtomicReference<Thread> edit, int shift, int hash,
				Object key, Box removedLeaf) {
			int idx = findIndex(key);
			if (idx == -1)
				return this;
			removedLeaf.val = removedLeaf;
			if (count == 1)
				return null;
			HashCollisionNode editable = ensureEditable(edit);
			editable.array[idx] = editable.array[2 * count - 2];
			editable.array[idx + 1] = editable.array[2 * count - 1];
			editable.array[2 * count - 2] = editable.array[2 * count - 1] = null;
			editable.count--;
			return editable;
		}
	}

	private static INode[] cloneAndSet(INode[] array, int i, INode a) {
		INode[] clone = array.clone();
		clone[i] = a;
		return clone;
	}

	private static Object[] cloneAndSet(Object[] array, int i, Object a) {
		Object[] clone = array.clone();
		clone[i] = a;
		return clone;
	}

	private static Object[] cloneAndSet(Object[] array, int i, Object a, int j,
			Object b) {
		Object[] clone = array.clone();
		clone[i] = a;
		clone[j] = b;
		return clone;
	}

	private static Object[] removePair(Object[] array, int i) {
		Object[] newArray = new Object[array.length - 2];
		System.arraycopy(array, 0, newArray, 0, 2 * i);
		System.arraycopy(array, 2 * (i + 1), newArray, 2 * i, newArray.length
				- 2 * i);
		return newArray;
	}

	private static INode createNode(int shift, Object key1, Object val1,
			int key2hash, Object key2, Object val2) {
		int key1hash = hash(key1);
		if (key1hash == key2hash)
			return new HashCollisionNode(null, key1hash, 2, new Object[] {
					key1, val1, key2, val2 });
		Box addedLeaf = new Box(null);
		AtomicReference<Thread> edit = new AtomicReference<Thread>();
		return BitmapIndexedNode.EMPTY.assoc(edit, shift, key1hash, key1, val1,
				addedLeaf).assoc(edit, shift, key2hash, key2, val2, addedLeaf);
	}

	private static INode createNode(AtomicReference<Thread> edit, int shift,
			Object key1, Object val1, int key2hash, Object key2, Object val2) {
		int key1hash = hash(key1);
		if (key1hash == key2hash)
			return new HashCollisionNode(null, key1hash, 2, new Object[] {
					key1, val1, key2, val2 });
		Box addedLeaf = new Box(null);
		return BitmapIndexedNode.EMPTY.assoc(edit, shift, key1hash, key1, val1,
				addedLeaf).assoc(edit, shift, key2hash, key2, val2, addedLeaf);
	}

	private static int bitpos(int hash, int shift) {
		return 1 << mask(hash, shift);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static final class NodeIter implements Iterator<Object> {
		private static final Object NULL = new Object();
		final Object[] array;
		private int i = 0;
		private Object nextEntry = NULL;
		private Iterator nextIter;

		NodeIter(Object[] array) {
			this.array = array;
		}

		private boolean advance() {
			while (i < array.length) {
				Object key = array[i];
				Object nodeOrVal = array[i + 1];
				i += 2;
				if (key != null) {
					nextEntry = new MapEntry(key, nodeOrVal);
					return true;
				} else if (nodeOrVal != null) {
					Iterator iter = ((INode) nodeOrVal).iterator();
					if (iter != null && iter.hasNext()) {
						nextIter = iter;
						return true;
					}
				}
			}
			return false;
		}

		public boolean hasNext() {
			if (nextEntry != NULL || nextIter != null)
				return true;
			return advance();
		}

		public Object next() {
			Object ret = nextEntry;
			if (ret != NULL) {
				nextEntry = NULL;
				return ret;
			} else if (nextIter != null) {
				ret = nextIter.next();
				if (!nextIter.hasNext())
					nextIter = null;
				return ret;
			} else if (advance())
				return next();
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	static final class NodeSeq extends ASeq {
		final Object[] array;
		final int i;
		final ISeq s;

		NodeSeq(Object[] array, int i) {
			this(array, i, null);
		}

		static ISeq create(Object[] array) {
			return create(array, 0, null);
		}

		private static ISeq create(Object[] array, int i, ISeq s) {
			if (s != null)
				return new NodeSeq(array, i, s);
			for (int j = i; j < array.length; j += 2) {
				if (array[j] != null)
					return new NodeSeq(array, j, null);
				INode node = (INode) array[j + 1];
				if (node != null) {
					ISeq nodeSeq = node.nodeSeq();
					if (nodeSeq != null)
						return new NodeSeq(array, j + 2, nodeSeq);
				}
			}
			return null;
		}

		NodeSeq(Object[] array, int i, ISeq s) {
			this.array = array;
			this.i = i;
			this.s = s;
		}

		public Object first() {
			if (s != null)
				return s.first();
			return new MapEntry(array[i], array[i + 1]);
		}

		public ISeq next() {
			if (s != null)
				return create(array, i, s.next());
			return create(array, i + 2, null);
		}
	}

}
