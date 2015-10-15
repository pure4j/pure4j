/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich May 20, 2006 */

package org.pure4j.collections;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

/**
 * Persistent Red Black Tree 
 * Note that instances of this class are constant
 * values i.e. add/remove etc return new values
 *
 * See Okasaki, Kahrs, Larsen et al
 */

public class PersistentTreeMap<K, V> extends APersistentMap<K, V> implements Reversible<Entry<K, V>>, Sorted<K, Entry<K, V>> {
	
	static final DefaultComparator<Object> DEFAULT_COMPARATOR = new DefaultComparator<Object>();
	
	@ImmutableValue
	private static final class DefaultComparator<K> implements Comparator<K>, Serializable {
	    public int compare(Object o1, Object o2){
			return Util.compare(o1, o2);
		}

	    private Object readResolve() throws ObjectStreamException {
	        // ensures that we aren't hanging onto a new default comparator for every
	        // sorted set, etc., we deserialize
	        return DEFAULT_COMPARATOR;
	    }
	}

	@IgnoreImmutableTypeCheck
	public final Comparator<? super K> comp;
	@IgnoreImmutableTypeCheck
	public final Node tree;
	
	@IgnoreImmutableTypeCheck
	public final int _count;

	final static private PersistentTreeMap<Object, Object> EMPTY = new PersistentTreeMap<Object, Object>();

	@Pure(Enforcement.FORCE)
	public PersistentTreeMap(Comparator<? super K> comp, Map<K, V> other) {
		this(createTemporary(comp, other.entrySet()));
	}

	public PersistentTreeMap() {
		this((Comparator<? super K>) DEFAULT_COMPARATOR);
	}
	
	public PersistentTreeMap(IPersistentMap<K, V> in) {
		this(DEFAULT_COMPARATOR, in.seq());
	}
	
	public PersistentTreeMap(Comparator<? super K> comp, IPersistentMap<K, V> in) {
		this(comp, in.seq());
	}
	
	private PersistentTreeMap(PersistentTreeMap<K, V> temp) {
		this._count = temp._count;
		this.tree = temp.tree;
		this.comp = temp.comp;
	}

	public PersistentTreeMap(Comparator<? super K> comp) {
		Pure4J.immutable(comp);
		this.comp = comp;
		tree = null;
		_count = 0;
	}

	private PersistentTreeMap(Comparator<? super K> comp, Node tree, int _count) {
		this.comp = comp;
		this.tree = tree;
		this._count = _count;
	}

	public PersistentTreeMap(Seqable<Entry<K, V>> items) {
		this(DEFAULT_COMPARATOR, items);
	}

	public PersistentTreeMap(Map<K, V> map) {
		this(DEFAULT_COMPARATOR, map);
	}
	
	public PersistentTreeMap(Comparator<? super K> comp, Seqable<Entry<K, V>> seq) {
		this(createTemporary(comp, seq));
	}
	
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public PersistentTreeMap(Comparator<? super K> comp, K... items) {
		this((PersistentTreeMap<K, V>) createTemporary(comp, items));
	}
	
	@SafeVarargs
	public PersistentTreeMap(K... items) {
		this(DEFAULT_COMPARATOR, items);
	}
 
	private static <K, V> PersistentTreeMap<K, V> createTemporary(Comparator<? super K> comp, Iterable<Entry<K, V>> in) {
		PersistentTreeMap<K,V> ret = new PersistentTreeMap<K, V>(comp);
		for (Entry<K, V> entry : in) {
			Pure4J.immutable(entry.getKey(), entry.getValue());
			ret = ret.assoc(entry.getKey(), entry.getValue());
		}
		return ret;
	}
	
	private static <K> PersistentTreeMap<K, K> createTemporary(Comparator<? super K> comp, K[] items) {
		PersistentTreeMap<K,K> ret = new PersistentTreeMap<K, K>(comp);
		for (int i = 0; i < items.length; i=i+2) {
			Pure4J.immutable(items[i], items[i+1]);
			ret = ret.assoc(items[i], items[i+1]);
		}
		return ret;
	}
	
	public boolean containsKey(Object key) {
		Pure4J.immutable(key);
		return entryAt(key) != null;
	}

	public PersistentTreeMap<K, V> assocEx(K key, V val) {
		Pure4J.immutable(key, val);
		Box found = new Box(null);
		Node t = add(tree, key, val, found);
		if (t == null) // null == already contains key
		{
			throw Util.runtimeException("Key already present");
		}
		return new PersistentTreeMap<K, V>(comp, t.blacken(), _count + 1);
	}

	@Pure(Enforcement.FORCE)	// due to use of Box
	public PersistentTreeMap<K, V> assoc(K key, V val) {
		Pure4J.immutable(key, val);
		Box found = new Box(null);
		Node t = add(tree, key, val, found);
		if (t == null) // null == already contains key
		{
			Node foundNode = (Node) found.val;
			if (foundNode.val() == val) // note only get same collection on
										// identity of val, not equals()
				return this;
			return new PersistentTreeMap<K, V>(comp, replace(tree, key, val), _count);
		}
		return new PersistentTreeMap<K, V>(comp, t.blacken(), _count + 1);
	}

	@Pure(Enforcement.FORCE)
	public PersistentTreeMap<K, V> without(Object key) {
		Pure4J.immutable(key);
		Box found = new Box(null);
		Node t = remove(tree, key, found);
		if (t == null) {
			if (found.val == null)// null == doesn't contain key
				return this;
			// empty
			return new PersistentTreeMap<K, V>(comp);
		}
		return new PersistentTreeMap<K, V>(comp, t.blacken(), _count - 1);
	}

	public ISeq<Entry<K, V>> seq() {
		if (_count > 0)
			return Seq.create(tree, true, _count);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <K, V> PersistentTreeMap<K, V> emptyMap() {
		return (PersistentTreeMap<K, V>) EMPTY;
	}

	public ISeq<Entry<K, V>> rseq() {
		if (_count > 0)
			return Seq.create(tree, false, _count);
		return null;
	}

	public Comparator<? super K> comparator() {
		return comp;
	}

	public K entryKey(Map.Entry<K, V> entry) {
		Pure4J.immutable(entry);
		return entry.getKey();
	}

	public ISeq<Entry<K, V>> seq(boolean ascending) {
		if (_count > 0)
			return Seq.create(tree, ascending, _count);
		return null;
	}

	public ISeq<Entry<K, V>> seqFrom(K key, boolean ascending) {
		Pure4J.immutable(key);
		if (_count > 0) {
			ISeq<Node> stack = null;
			Node t = tree;
			while (t != null) {
				int c = doCompare(key, t.key);
				if (c == 0) {
					stack = PureCollections.cons(t, stack);
					return new Seq<K, V>(stack, ascending);
				} else if (ascending) {
					if (c < 0) {
						stack = PureCollections.cons(t, stack);
						t = t.left();
					} else
						t = t.right();
				} else {
					if (c > 0) {
						stack = PureCollections.cons(t, stack);
						t = t.right();
					} else
						t = t.left();
				}
			}
			if (stack != null)
				return new Seq<K, V>(stack, ascending);
		}
		return null;
	}

	public NodeIterator<K, V> iterator() {
		return new NodeIterator<K, V>(tree, true);
	}
	
	public NodeIterator<K, V> reverseIterator() {
		return new NodeIterator<K, V>(tree, false);
	}

	public IPureIterator<K> keyIterator() {
		return keys(iterator());
	}

	public IPureIterator<V> valIterator() {
		return vals(iterator());
	}

	private IPureIterator<K> keys(NodeIterator<K, V> it) {
		return new KeyIterator<K, V>(it);
	}

	private IPureIterator<V> vals(NodeIterator<K, V> it) {
		return new ValIterator<K, V>(it);
	}

	int depth(Node t) {
		if (t == null)
			return 0;
		return 1 + Math.max(depth(t.left()), depth(t.right()));
	}

	@SuppressWarnings("unchecked")
	public V get(Object key, Object notFound) {
		Pure4J.immutable(key, notFound);
		Node n = (Node) entryAt(key);
		return (V) ((n != null) ? n.val() : notFound);
	}

	public V get(Object key) {
		Pure4J.immutable(key);
		return get(key, null);
	}

	public int capacity() {
		return _count;
	}

	public int size() {
		return _count;
	}

	@SuppressWarnings("unchecked")
	public IMapEntry<K, V> entryAt(Object key) {
		Pure4J.immutable(key);
		Node t = tree;
		while (t != null) {
			int c = doCompare(key, t.key);
			if (c == 0)
				return (IMapEntry<K, V>) t;
			else if (c < 0)
				t = t.left();
			else
				t = t.right();
		}
		return (IMapEntry<K, V>) t;
	}

	@SuppressWarnings("unchecked")
	public int doCompare(Object k1, Object k2) {
		Pure4J.immutable(k1, k2);
		return comp.compare((K) k1, (K) k2);
	}

	@Pure(Enforcement.FORCE)
	private Node add(Node t, K key, V val, Box found) {
		if (t == null) {
			if (val == null)
				return new Red(key);
			return new RedVal(key, val);
		}
		int c = doCompare(key, t.key);
		if (c == 0) {
			found.val = t;
			return null;
		}
		Node ins = c < 0 ? add(t.left(), key, val, found) : add(t.right(), key,
				val, found);
		if (ins == null) // found below
			return null;
		if (c < 0)
			return t.addLeft(ins);
		return t.addRight(ins);
	}
	
	@Pure(Enforcement.FORCE)	// use of box
	private Node remove(Node t, Object key, Box found) {
		if (t == null)
			return null; // not found indicator
		int c = doCompare(key, t.key);
		if (c == 0) {
			found.val = t;
			return append(t.left(), t.right());
		}
		Node del = c < 0 ? remove(t.left(), key, found) : remove(t.right(),
				key, found);
		if (del == null && found.val == null) // not found below
			return null;
		if (c < 0) {
			if (t.left() instanceof Black)
				return balanceLeftDel(t.key, t.val(), del, t.right());
			else
				return red(t.key, t.val(), del, t.right());
		}
		if (t.right() instanceof Black)
			return balanceRightDel(t.key, t.val(), t.left(), del);
		return red(t.key, t.val(), t.left(), del);
		// return t.removeLeft(del);
		// return t.removeRight(del);
	}

	static Node append(Node left, Node right) {
		if (left == null)
			return right;
		else if (right == null)
			return left;
		else if (left instanceof Red) {
			if (right instanceof Red) {
				Node app = append(left.right(), right.left());
				if (app instanceof Red)
					return red(
							app.key,
							app.val(),
							red(left.key, left.val(), left.left(), app.left()),
							red(right.key, right.val(), app.right(),
									right.right()));
				else
					return red(left.key, left.val(), left.left(),
							red(right.key, right.val(), app, right.right()));
			} else
				return red(left.key, left.val(), left.left(),
						append(left.right(), right));
		} else if (right instanceof Red)
			return red(right.key, right.val(), append(left, right.left()),
					right.right());
		else // black/black
		{
			Node app = append(left.right(), right.left());
			if (app instanceof Red)
				return red(
						app.key,
						app.val(),
						black(left.key, left.val(), left.left(), app.left()),
						black(right.key, right.val(), app.right(),
								right.right()));
			else
				return balanceLeftDel(left.key, left.val(), left.left(),
						black(right.key, right.val(), app, right.right()));
		}
	}

	static Node balanceLeftDel(Object key, Object val, Node del, Node right) {
		if (del instanceof Red)
			return red(key, val, del.blacken(), right);
		else if (right instanceof Black)
			return rightBalance(key, val, del, right.redden());
		else if (right instanceof Red && right.left() instanceof Black)
			return red(
					right.left().key,
					right.left().val(),
					black(key, val, del, right.left().left()),
					rightBalance(right.key, right.val(), right.left().right(),
							right.right().redden()));
		else
			throw new UnsupportedOperationException("Invariant violation");
	}

	static Node balanceRightDel(Object key, Object val, Node left, Node del) {
		if (del instanceof Red)
			return red(key, val, left, del.blacken());
		else if (left instanceof Black)
			return leftBalance(key, val, left.redden(), del);
		else if (left instanceof Red && left.right() instanceof Black)
			return red(
					left.right().key,
					left.right().val(),
					leftBalance(left.key, left.val(), left.left().redden(),
							left.right().left()),
					black(key, val, left.right().right(), del));
		else
			throw new UnsupportedOperationException("Invariant violation");
	}

	static Node leftBalance(Object key, Object val, Node ins, Node right) {
		if (ins instanceof Red && ins.left() instanceof Red)
			return red(ins.key, ins.val(), ins.left().blacken(),
					black(key, val, ins.right(), right));
		else if (ins instanceof Red && ins.right() instanceof Red)
			return red(ins.right().key, ins.right().val(),
					black(ins.key, ins.val(), ins.left(), ins.right().left()),
					black(key, val, ins.right().right(), right));
		else
			return black(key, val, ins, right);
	}

	static Node rightBalance(Object key, Object val, Node left, Node ins) {
		if (ins instanceof Red && ins.right() instanceof Red)
			return red(ins.key, ins.val(), black(key, val, left, ins.left()),
					ins.right().blacken());
		else if (ins instanceof Red && ins.left() instanceof Red)
			return red(ins.left().key, ins.left().val(),
					black(key, val, left, ins.left().left()),
					black(ins.key, ins.val(), ins.left().right(), ins.right()));
		else
			return black(key, val, left, ins);
	}

	private Node replace(Node t, K key, V val) {
		int c = doCompare(key, t.key);
		return t.replace(t.key, c == 0 ? val : t.val(),
				c < 0 ? replace(t.left(), key, val) : t.left(),
				c > 0 ? replace(t.right(), key, val) : t.right());
	}

	static Red red(Object key, Object val, Node left, Node right) {
		if (left == null && right == null) {
			if (val == null)
				return new Red(key);
			return new RedVal(key, val);
		}
		if (val == null)
			return new RedBranch(key, left, right);
		return new RedBranchVal(key, val, left, right);
	}

	static Black black(Object key, Object val, Node left, Node right) {
		if (left == null && right == null) {
			if (val == null)
				return new Black(key);
			return new BlackVal(key, val);
		}
		if (val == null)
			return new BlackBranch(key, left, right);
		return new BlackBranchVal(key, val, left, right);
	}

	
	@ImmutableValue
	static abstract class Node extends AMapEntry<Object, Object> {
		final protected Object key;

		Node(Object key) {
			this.key = key;
		}

		public Object key() {
			return key;
		}

		public Object val() {
			return null;
		}

		public Object getKey() {
			return key();
		}

		public Object getValue() {
			return val();
		}

		Node left() {
			return null;
		}

		Node right() {
			return null;
		}

		abstract Node addLeft(Node ins);

		abstract Node addRight(Node ins);

		abstract Node removeLeft(Node del);

		abstract Node removeRight(Node del);

		abstract Node blacken();

		abstract Node redden();

		Node balanceLeft(Node parent) {
			return black(parent.key, parent.val(), this, parent.right());
		}

		Node balanceRight(Node parent) {
			return black(parent.key, parent.val(), parent.left(), this);
		}

		abstract Node replace(Object key, Object val, Node left, Node right);
		
		@Override
		public Object setValue(Object value) {
			throw new UnsupportedOperationException();
		}
	}

	static class Black extends Node {
		public Black(Object key) {
			super(key);
		}

		Node addLeft(Node ins) {
			return ins.balanceLeft(this);
		}

		Node addRight(Node ins) {
			return ins.balanceRight(this);
		}

		Node removeLeft(Node del) {
			return balanceLeftDel(key, val(), del, right());
		}

		Node removeRight(Node del) {
			return balanceRightDel(key, val(), left(), del);
		}

		Node blacken() {
			return this;
		}

		Node redden() {
			return new Red(key);
		}

		Node replace(Object key, Object val, Node left, Node right) {
			return black(key, val, left, right);
		}
	}

	static class BlackVal extends Black {
		final Object val;

		public BlackVal(Object key, Object val) {
			super(key);
			this.val = val;
		}

		public Object val() {
			return val;
		}

		Node redden() {
			return new RedVal(key, val);
		}

	}

	static class BlackBranch extends Black {
		final Node left;

		final Node right;

		public BlackBranch(Object key, Node left, Node right) {
			super(key);
			this.left = left;
			this.right = right;
		}

		public Node left() {
			return left;
		}

		public Node right() {
			return right;
		}

		Node redden() {
			return new RedBranch(key, left, right);
		}

	}

	static class BlackBranchVal extends BlackBranch {
		final Object val;

		public BlackBranchVal(Object key, Object val, Node left, Node right) {
			super(key, left, right);
			this.val = val;
		}

		public Object val() {
			return val;
		}

		Node redden() {
			return new RedBranchVal(key, val, left, right);
		}

	}

	static class Red extends Node {
		public Red(Object key) {
			super(key);
		}

		Node addLeft(Node ins) {
			return red(key, val(), ins, right());
		}

		Node addRight(Node ins) {
			return red(key, val(), left(), ins);
		}

		Node removeLeft(Node del) {
			return red(key, val(), del, right());
		}

		Node removeRight(Node del) {
			return red(key, val(), left(), del);
		}

		Node blacken() {
			return new Black(key);
		}

		Node redden() {
			throw new UnsupportedOperationException("Invariant violation");
		}

		Node replace(Object key, Object val, Node left, Node right) {
			return red(key, val, left, right);
		}

	}

	static class RedVal extends Red {
		final Object val;

		public RedVal(Object key, Object val) {
			super(key);
			this.val = val;
		}

		public Object val() {
			return val;
		}

		Node blacken() {
			return new BlackVal(key, val);
		}

	}

	static class RedBranch extends Red {
		final Node left;

		final Node right;

		public RedBranch(Object key, Node left, Node right) {
			super(key);
			this.left = left;
			this.right = right;
		}

		public Node left() {
			return left;
		}

		public Node right() {
			return right;
		}

		Node balanceLeft(Node parent) {
			if (left instanceof Red)
				return red(key, val(), left.blacken(),
						black(parent.key, parent.val(), right, parent.right()));
			else if (right instanceof Red)
				return red(
						right.key,
						right.val(),
						black(key, val(), left, right.left()),
						black(parent.key, parent.val(), right.right(),
								parent.right()));
			else
				return super.balanceLeft(parent);

		}

		Node balanceRight(Node parent) {
			if (right instanceof Red)
				return red(key, val(),
						black(parent.key, parent.val(), parent.left(), left),
						right.blacken());
			else if (left instanceof Red)
				return red(
						left.key,
						left.val(),
						black(parent.key, parent.val(), parent.left(),
								left.left()),
						black(key, val(), left.right(), right));
			else
				return super.balanceRight(parent);
		}

		Node blacken() {
			return new BlackBranch(key, left, right);
		}

	}

	static class RedBranchVal extends RedBranch {
		final Object val;

		public RedBranchVal(Object key, Object val, Node left, Node right) {
			super(key, left, right);
			this.val = val;
		}

		public Object val() {
			return val;
		}

		Node blacken() {
			return new BlackBranchVal(key, val, left, right);
		}
	}

	@ImmutableValue
	static private class Seq<K, V> extends ASeq<Entry<K, V>> {
		final ISeq<Node> stack;
		final boolean asc;
		final int cnt;

		private Seq(ISeq<Node> stack, boolean asc) {
			this.stack = stack;
			this.asc = asc;
			this.cnt = -1;
		}

		public Seq(ISeq<Node> stack, boolean asc, int cnt) {
			this.stack = stack;
			this.asc = asc;
			this.cnt = cnt;
		}

		@Pure
		static <K, V> Seq<K, V> create(Node t, boolean asc, int cnt) {
			return new Seq<K, V>(push(t, null, asc), asc, cnt);
		}

		@Pure
		static ISeq<Node> push(Node t, ISeq<Node> stack, boolean asc) {
			while (t != null) {
				stack = PureCollections.cons(t, stack);
				t = asc ? t.left() : t.right();
			}
			return stack;
		}

		@SuppressWarnings("unchecked")
		public Entry<K, V> first() {
			return (java.util.Map.Entry<K, V>) stack.first();
		}

		public ISeq<Entry<K, V>> next() {
			Node t = (Node) stack.first();
			ISeq<Node> nextstack = push(asc ? t.right() : t.left(), stack.next(), asc);
			if (nextstack != null) {
				return new Seq<K, V>(nextstack, asc, cnt - 1);
			}
			return null;
		}

		public int size() {
			if (cnt < 0)
				return super.size();
			return cnt;
		}
	}

	public static class NodeIterator<K, V> implements IPureIterator<Entry<K, V>> {
		private Stack<Node> stack = new Stack<Node>();
		private boolean asc;

		NodeIterator(Node t, boolean asc) {
			this.asc = asc;
			push(t);
		}

		void push(Node t) {
			while (t != null) {
				stack.push(t);
				t = asc ? t.left() : t.right();
			}
		}

		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@SuppressWarnings("unchecked")
		public Entry<K, V> next() {
			Node t = (Node) stack.pop();
			push(asc ? t.right() : t.left());
			return (java.util.Map.Entry<K, V>) Pure4J.returnImmutable(t);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	static class KeyIterator<K, V> implements IPureIterator<K> {
		NodeIterator<K, V> it;

		KeyIterator(NodeIterator<K, V> it) {
			this.it = it;
		}

		public void doSomething(Object o) {

		}
		
		public boolean hasNext() {
			return it.hasNext();
		}

		public K next() {
			return it.next().getKey();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	static class ValIterator<K, V> implements IPureIterator<V> {
		NodeIterator<K, V> it;

		ValIterator(NodeIterator<K, V> it) {
			this.it = it;
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public V next() {
			return it.next().getValue();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public ITransientMap<K, V> asTransient() {
		return new TransientTreeMap<K, V>(this);
	}
}
