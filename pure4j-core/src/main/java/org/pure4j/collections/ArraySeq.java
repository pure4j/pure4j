/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jun 19, 2006 */

package org.pure4j.collections;

import java.util.Arrays;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.immutable.ImmutableValue;

public class ArraySeq<K> extends ASeq<K> implements IndexedSeq<K> {
	
	@ImmutableValue(Enforcement.FORCE)
	private final K[] array;
	final int i;

	// ISeq _rest;

	static public <K> ArraySeq<K> create() {
		return null;
	}

	@Pure(Enforcement.FORCE)
	static public <K> ArraySeq<K> create(K... array) {
		if (array == null || array.length == 0)
			return null;
				
		return new ArraySeq<K>(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ASeq<?> createFromArray(Object o) {
		Class<? extends Object> class1 = o.getClass();
		if (class1.isArray()) {
			Class<?> component = class1.getComponentType();
		
			if (component == int.class) {
				return create((int[]) o);
			} 
			if (component == double.class) {
				return create((double[]) o);
			} 
			if (component == long.class) {
				return create((long[]) o);
			} 
			if (component == short.class) {
				return create((short[]) o);
			} 
			if (component == char.class) {
				return create((char[]) o);
			} 
			if (component == boolean.class) {
				return create((boolean[]) o);
			} 
			if (component == float.class) {
				return create((float[]) o);
			} 
			
			return create((Object[]) o);
		} else {
			throw new IllegalArgumentException("Was expecting array type but got "+class1.getName());
		}
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_int create(int... array) {
		return new ArraySeq_int(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_float create(float... array) {
		return new ArraySeq_float(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_long create(long... array) {
		return new ArraySeq_long(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_byte create(byte... array) {
		return new ArraySeq_byte(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_short create(short... array) {
		return new ArraySeq_short(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_char create(char... array) {
		return new ArraySeq_char(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_boolean create(boolean... array) {
		return new ArraySeq_boolean(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ArraySeq_double create(double... array) {
		return new ArraySeq_double(array, 0, true);
	}

	private ArraySeq(K[] array, int i, boolean copy) {
		this.i = i;
		this.array = copy ? Arrays.copyOf(array, array.length) : array;
	}

	public K first() {
		if (array != null)
			return array[i];
		return null;
	}

	public ISeq<K> next() {
		if (array != null && i + 1 < array.length)
			return new ArraySeq<K>(array, i + 1, false);
		return null;
	}

	public int count() {
		if (array != null)
			return array.length - i;
		return 0;
	}

	public int index() {
		return i;
	}

	public int indexOf(Object o) {
		Pure4J.immutable(o);
		if (array != null)
			for (int j = i; j < array.length; j++)
				if (Util.equals(o, array[j]))
					return j - i;
		return -1;
	}

	public int lastIndexOf(Object o) {
		Pure4J.immutable(o);
		if (array != null) {
			if (o == null) {
				for (int j = array.length - 1; j >= i; j--)
					if (array[j] == null)
						return j - i;
			} else {
				for (int j = array.length - 1; j >= i; j--)
					if (o.equals(array[j]))
						return j - i;
			}
		}
		return -1;
	}

	// ////////////////////////////////// specialized primitive versions
	// ///////////////////////////////

	static public class ArraySeq_int extends ASeq<Integer> implements IndexedSeq<Integer>  {
		@ImmutableValue(Enforcement.FORCE)
		public final int[] array;
		final int i;

		ArraySeq_int(int[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Integer first() {
			return array[i];
		}

		public ISeq<Integer> next() {
			if (i + 1 < array.length)
				return new ArraySeq_int(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}

		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				int k = ((Number) o).intValue();
				for (int j = i; j < array.length; j++)
					if (k == array[j])
						return j - i;
			}

			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				int k = ((Number) o).intValue();
				for (int j = array.length - 1; j >= i; j--)
					if (k == array[j])
						return j - i;
			}

			return -1;
		}
	}

	static public class ArraySeq_float extends ASeq<Float> implements IndexedSeq<Float>  {
		
		@ImmutableValue(Enforcement.FORCE)
		public final float[] array;
		final int i;

		ArraySeq_float(float[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Float first() {
			return array[i];
		}

		public ISeq<Float> next() {
			if (i + 1 < array.length)
				return new ArraySeq_float(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}

		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				float f = ((Number) o).floatValue();
				for (int j = i; j < array.length; j++)
					if (f == array[j])
						return j - i;
			}
			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				float f = ((Number) o).floatValue();
				for (int j = array.length - 1; j >= i; j--)
					if (f == array[j])
						return j - i;
			}
			return -1;
		}
	}

	static public class ArraySeq_double extends ASeq<Double> implements IndexedSeq<Double> {
		@ImmutableValue(Enforcement.FORCE)
		public final double[] array;
		final int i;

		ArraySeq_double(double[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Double first() {
			return array[i];
		}

		public ISeq<Double> next() {
			if (i + 1 < array.length)
				return new ArraySeq_double(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}

		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				double d = ((Number) o).doubleValue();
				for (int j = i; j < array.length; j++)
					if (d == array[j])
						return j - i;
			}

			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				double d = ((Number) o).doubleValue();
				for (int j = array.length - 1; j >= i; j--)
					if (d == array[j])
						return j - i;
			}

			return -1;
		}
	}

	static public class ArraySeq_long extends ASeq<Long> implements IndexedSeq<Long> {
		@ImmutableValue(Enforcement.FORCE)
		public final long[] array;
		final int i;

		ArraySeq_long(long[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Long first() {
			return array[i];
		}

		public ISeq<Long> next() {
			if (i + 1 < array.length)
				return new ArraySeq_long(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}

		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				long l = ((Number) o).longValue();
				for (int j = i; j < array.length; j++)
					if (l == array[j])
						return j - i;
			}

			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Number) {
				long l = ((Number) o).longValue();
				for (int j = array.length - 1; j >= i; j--)
					if (l == array[j])
						return j - i;
			}

			return -1;
		}
	}

	static public class ArraySeq_byte extends ASeq<Byte> implements IndexedSeq<Byte>  {
		@ImmutableValue(Enforcement.FORCE)
		public final byte[] array;
		final int i;

		ArraySeq_byte(byte[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Byte first() {
			return array[i];
		}

		public ISeq<Byte> next() {
			if (i + 1 < array.length)
				return new ArraySeq_byte(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}

		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Byte) {
				byte b = ((Byte) o).byteValue();
				for (int j = i; j < array.length; j++)
					if (b == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
			for (int j = i; j < array.length; j++)
				if (o.equals(array[j]))
					return j - i;
			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Byte) {
				byte b = ((Byte) o).byteValue();
				for (int j = array.length - 1; j >= i; j--)
					if (b == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
			for (int j = array.length - 1; j >= i; j--)
				if (o.equals(array[j]))
					return j - i;
			return -1;
		}
	}

	static public class ArraySeq_char extends ASeq<Character> implements IndexedSeq<Character> {
		@ImmutableValue(Enforcement.FORCE)
		public final char[] array;
		final int i;

		ArraySeq_char(char[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Character first() {
			return array[i];
		}

		public ISeq<Character> next() {
			if (i + 1 < array.length)
				return new ArraySeq_char(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}

		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Character) {
				char c = ((Character) o).charValue();
				for (int j = i; j < array.length; j++)
					if (c == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
//			for (int j = i; j < array.length; j++)
//				if (Pure4J.equals(a, b)array[j]==c)
//					return j - i;
			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Character) {
				char c = ((Character) o).charValue();
				for (int j = array.length - 1; j >= i; j--)
					if (c == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
			for (int j = array.length - 1; j >= i; j--)
				if (o.equals(array[j]))
					return j - i;
			return -1;
		}
	}

	static public class ArraySeq_short extends ASeq<Short> implements IndexedSeq<Short> {
		@ImmutableValue(Enforcement.FORCE)
		public final short[] array;
		final int i;

		ArraySeq_short(short[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Short first() {
			return array[i];
		}

		public ISeq<Short> next() {
			if (i + 1 < array.length)
				return new ArraySeq_short(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}

		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Short) {
				short s = ((Short) o).shortValue();
				for (int j = i; j < array.length; j++)
					if (s == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
			for (int j = i; j < array.length; j++)
				if (o.equals(array[j]))
					return j - i;
			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Short) {
				short s = ((Short) o).shortValue();
				for (int j = array.length - 1; j >= i; j--)
					if (s == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
			for (int j = array.length - 1; j >= i; j--)
				if (o.equals(array[j]))
					return j - i;
			return -1;
		}
	}

	static public class ArraySeq_boolean extends ASeq<Boolean> implements IndexedSeq<Boolean> {
		@ImmutableValue(Enforcement.FORCE)
		public final boolean[] array;
		final int i;

		ArraySeq_boolean(boolean[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Boolean first() {
			return array[i];
		}

		public ISeq<Boolean> next() {
			if (i + 1 < array.length)
				return new ArraySeq_boolean(array, i + 1, false);
			return null;
		}

		public int count() {
			return array.length - i;
		}

		public int index() {
			return i;
		}
		
		public int indexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Boolean) {
				boolean b = ((Boolean) o).booleanValue();
				for (int j = i; j < array.length; j++)
					if (b == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
			for (int j = i; j < array.length; j++)
				if (o.equals(array[j]))
					return j - i;
			return -1;
		}

		public int lastIndexOf(Object o) {
			Pure4J.immutable(o);
			if (o instanceof Boolean) {
				boolean b = ((Boolean) o).booleanValue();
				for (int j = array.length - 1; j >= i; j--)
					if (b == array[j])
						return j - i;
			}
			if (o == null) {
				return -1;
			}
			for (int j = array.length - 1; j >= i; j--)
				if (o.equals(array[j]))
					return j - i;
			return -1;
		}
	}

}
