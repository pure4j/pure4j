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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreNonImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public abstract class APersistentMap<K, V> implements IPersistentMap<K, V>,
		Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable, MapEquivalence {
	
	private static final long serialVersionUID = 1L;
	
	@IgnoreNonImmutableTypeCheck
	int _hasheq = -1;

	 /**
     * Returns a string representation of this map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this map
     */
    public String toString() {
       return ToStringFunctions.toString(this);
    }


	public IPersistentMap<K, V> cons(Map.Entry<K, V> o) {
		Pure4J.immutable(o);
		return assoc(o.getKey(), o.getValue());
	}

	public boolean equals(Object obj) {
		return mapEquals(this, obj);
	}

	static public <K2, V2> boolean mapEquals(IPersistentMap<K2,V2> m1, Object obj) {
		if (m1 == obj)
			return true;
		if (!(obj instanceof Map))
			return false;
		
		Map<?,?> m = (Map<?,?>) obj;

		if (m.size() != m1.count())
			return false;
		
		for (ISeq<Entry<K2,V2>> s = m1.seq(); s != null; s = s.next()) {
			Entry<K2,V2> e = s.first();
			boolean found = m.containsKey(e.getKey());

			if (!found || !Util.equals(e.getValue(), m.get(e.getKey())))
				return false;
		}

		return true;
	}

	public int hashCode() {
		if (_hasheq == -1) {
			_hasheq = Murmur3.hashUnordered(this);
		}
		return _hasheq;
	}

	static public int mapHasheq(IPersistentMap<?,?> m) {
		return Murmur3.hashUnordered(m);
	}

	static public class KeySeq<K, V> extends ASeq<K> {
		private static final long serialVersionUID = 5550732202968416322L;
		
		final ISeq<Entry<K, V>> seq;

		@IgnoreNonImmutableTypeCheck
		final Iterable<Entry<K, V>> iterable;

		@Pure
		static public <K2, V2> KeySeq<K2, V2> create(ISeq<Entry<K2, V2>> seq) {
			if (seq == null)
				return null;
			return new KeySeq<K2, V2>(seq, null);
		}

		@Pure
		static public <K2, V2> KeySeq<K2, V2> createFromMap(IPersistentMap<K2, V2> map) {
			if (map == null)
				return null;
			ISeq<Entry<K2, V2>> seq = map.seq();
			if (seq == null)
				return null;
			return new KeySeq<K2, V2>(seq, map);
		}

		@Pure(Enforcement.FORCE)
		private KeySeq(ISeq<Entry<K, V>> seq, Iterable<Entry<K,V>> iterable) {
			this.seq = seq;
			this.iterable = iterable;
		}

		public K first() {
			return seq.first().getKey();
		}

		public ISeq<K> next() {
			return create(seq.next());
		}

		@SuppressWarnings("unchecked")
		public IPureIterator<K> iterator() {
			if (iterable == null)
				return super.iterator();

			if (iterable instanceof IMapIterable)
				return (IPureIterator<K>) ((IMapIterable<?,?>) iterable).keyIterator();

			final Iterator<Entry<K, V>> mapIter = iterable.iterator();
			
			return new IPureIterator<K>() {
				public boolean hasNext() {
					return mapIter.hasNext();
				}

				public K next() {
					return mapIter.next().getKey();
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	static public class ValSeq<K, V> extends ASeq<V> {
		
		final ISeq<Entry<K, V>> seq;
		
		@IgnoreNonImmutableTypeCheck
		final Iterable<Entry<K,V>> iterable;

		@Pure
		static public <K, V> ValSeq<K, V> create(ISeq<Entry<K, V>> seq) {
			if (seq == null)
				return null;
			return new ValSeq<K, V>(seq, null);
		}

		@Pure
		static public <K, V> ValSeq<K, V> createFromMap(IPersistentMap<K, V> map) {
			if (map == null)
				return null;
			ISeq<Entry<K,V>> seq = map.seq();
			if (seq == null)
				return null;
			return new ValSeq<K, V>(seq, map);
		}
		
		@Pure(Enforcement.FORCE)
		private ValSeq(ISeq<Entry<K, V>> seq, Iterable<Entry<K, V>> iterable) {
			this.seq = seq;
			this.iterable = iterable;
		}

		public V first() {
			return seq.first().getValue();
		}

		public ISeq<V> next() {
			return create(seq.next());
		}
		
		@SuppressWarnings("unchecked")
		public IPureIterator<V> iterator() {
			if (iterable == null)
				return super.iterator();

			if (iterable instanceof IMapIterable)
				return (IPureIterator<V>) ((IMapIterable<?,?>) iterable).valIterator();

			final Iterator<Entry<K,V>> mapIter = iterable.iterator();
			return new IPureIterator<V>() {
				public boolean hasNext() {
					return mapIter.hasNext();
				}

				public V next() {
					return mapIter.next().getValue();
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	// java.util.Map implementation

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean containsValue(Object value) {
		Pure4J.immutable(value);
		return values().contains(value);
	}
	
	public Set<Entry<K, V>> entrySet() {
		return new AImmutableSet<Entry<K, V>>() {
			
			public Iterator<Entry<K, V>> iterator() {
				return APersistentMap.this.iterator();
			}

			public int size() {
				return count();
			}

			public int hashCode() {
				return APersistentMap.this.hashCode();
			}

			public boolean contains(Object o) {
				if (o instanceof Entry) {
					Entry<?,?> e = (Entry<?,?>) o;
					Entry<?,?> found = entryAt(e.getKey());
					if (found != null
							&& Util.equals(found.getValue(), e.getValue()))
						return true;
				}
				return false;
			}
		};
	}

	public V get(Object key) {
		Pure4J.immutable(key);
		return valAt(key);
	}

	public boolean isEmpty() {
		return count() == 0;
	}

	public Set<K> keySet() {
		return new AImmutableSet<K>() {

			public Iterator<K> iterator() {
				final Iterator<Entry<K, V>> mi = APersistentMap.this.iterator();

				return new IPureIterator<K>() {

					public boolean hasNext() {
						return mi.hasNext();
					}

					public K next() {
						Entry<K, V> e = mi.next();
						return e.getKey();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			public int size() {
				return count();
			}

			public boolean contains(Object o) {
				return APersistentMap.this.containsKey(o);
			}
		};
	}

	public V put(K key, V value) {
		Pure4J.immutable(key, value);
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		Pure4J.immutable(t);
		throw new UnsupportedOperationException();
	}

	public V remove(Object key) {
		Pure4J.immutable(key);
		throw new UnsupportedOperationException();
	}

	public int size() {
		return count();
	}

	public Collection<V> values() {
		return new AImmutableCollection<V>() {

			public Iterator<V> iterator() {
				final Iterator<Map.Entry<K, V>> mi = APersistentMap.this.iterator();

				return new IPureIterator<V>() {

					public boolean hasNext() {
						return mi.hasNext();
					}

					public V next() {
						Entry<K, V> e = mi.next();
						return e.getValue();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			public int size() {
				return count();
			}
		};
	}

}
