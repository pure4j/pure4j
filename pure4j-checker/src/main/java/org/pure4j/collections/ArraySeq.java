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
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureInterface;

public class ArraySeq<K> extends ASeq<K> {
	
	@IgnoreImmutableTypeCheck
	private final K[] array;
	final int i;

	// ISeq _rest;
	
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
			if (component == byte.class) {
				return new ByteSeq((byte[]) o);
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
				return new BooleanSeq((boolean[]) o);
			} 
			if (component == float.class) {
				return create((float[]) o);
			} 
			
			return new ArraySeq<Object>((Object[]) o);
		} else {
			throw new IllegalArgumentException("Was expecting array type but got "+class1.getName());
		}
	}
	
	@Pure(Enforcement.FORCE)
	public static IntSeq create(int... array) {
		return new IntSeq(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static FloatSeq create(float... array) {
		return new FloatSeq(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static LongSeq create(long... array) {
		return new LongSeq(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static ByteSeq create(byte... array) {
		return new ByteSeq(array, 0, true);
	}
	
	
	@Pure(Enforcement.FORCE)
	public static ShortSeq create(short... array) {
		return new ShortSeq(array, 0, true);
	}
	
	@PureInterface(Enforcement.NOT_PURE)
	public static CharSeq create(char... array) {
		return new CharSeq(array, 0, true);
	}
	
	@Pure(Enforcement.FORCE)
	public static DoubleSeq create(double... array) {
		return new DoubleSeq(array, 0, true);
	}

	@Pure(Enforcement.FORCE)
	@SafeVarargs
	public ArraySeq(K... array) {
		this(array, 0, true);
	}
	
	private ArraySeq(K[] array, int i, boolean copy) {
		Pure4J.immutableArray(array);
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

	public int size() {
		if (array != null)
			return array.length - i;
		return 0;
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

	static public class IntSeq extends ASeq<Integer>  {
		@IgnoreImmutableTypeCheck
		public final int[] array;
		final int i;

		@Pure(Enforcement.FORCE)
		public IntSeq(int... array) {
			this(array, 0, true);
		}
		
		@Pure(Enforcement.FORCE) // due to array copy
		private IntSeq(int[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Integer first() {
			return array[i];
		}

		public ISeq<Integer> next() {
			if (i + 1 < array.length)
				return new IntSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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

	static public class FloatSeq extends ASeq<Float>   {
		
		@IgnoreImmutableTypeCheck
		public final float[] array;
		final int i;

		@Pure(Enforcement.FORCE)
		public FloatSeq(float... array) {
			this(array, 0, true);
		}
		
		@Pure(Enforcement.FORCE) // due to array copy
		FloatSeq(float[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Float first() {
			return array[i];
		}

		public ISeq<Float> next() {
			if (i + 1 < array.length)
				return new FloatSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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

	static public class DoubleSeq extends ASeq<Double> {
		@IgnoreImmutableTypeCheck
		public final double[] array;
		final int i;

		@Pure(Enforcement.FORCE)
		public DoubleSeq(double... array) {
			this(array, 0, true);
		}
		
		@Pure(Enforcement.FORCE) // due to array copy
		DoubleSeq(double[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Double first() {
			return array[i];
		}

		public ISeq<Double> next() {
			if (i + 1 < array.length)
				return new DoubleSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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

	static public class LongSeq extends ASeq<Long> {
		@IgnoreImmutableTypeCheck
		public final long[] array;
		final int i;

		@Pure(Enforcement.FORCE)
		public LongSeq(long... array) {
			this(array, 0, true);
		}


		@Pure(Enforcement.FORCE) // due to array copy
		private LongSeq(long[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Long first() {
			return array[i];
		}

		public ISeq<Long> next() {
			if (i + 1 < array.length)
				return new LongSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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

	static public class ByteSeq extends ASeq<Byte>  {
		@IgnoreImmutableTypeCheck
		public final byte[] array;
		final int i;
		
		@Pure(Enforcement.FORCE)
		public ByteSeq(byte... array) {
			this(array, 0, true);
		}

		@Pure(Enforcement.FORCE) // due to array copy
		private ByteSeq(byte[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Byte first() {
			return array[i];
		}

		public ISeq<Byte> next() {
			if (i + 1 < array.length)
				return new ByteSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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

	static public class CharSeq extends ASeq<Character> {
		@IgnoreImmutableTypeCheck
		public final char[] array;
		final int i;

		@Pure(Enforcement.FORCE)
		public CharSeq(char... array) {
			this(array, 0, true);
		}
		
		@Pure(Enforcement.FORCE) // due to array copy
		private CharSeq(char[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Character first() {
			return array[i];
		}

		public ISeq<Character> next() {
			if (i + 1 < array.length)
				return new CharSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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

	static public class ShortSeq extends ASeq<Short> {
		@IgnoreImmutableTypeCheck
		public final short[] array;
		final int i;

		
		@Pure(Enforcement.FORCE)
		public ShortSeq(short... array) {
			this(array, 0, true);
		}

		@Pure(Enforcement.FORCE) // due to array copy
		private ShortSeq(short[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Short first() {
			return array[i];
		}

		public ISeq<Short> next() {
			if (i + 1 < array.length)
				return new ShortSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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

	static public class BooleanSeq extends ASeq<Boolean> {
		@IgnoreImmutableTypeCheck
		public final boolean[] array;
		final int i;

		@Pure(Enforcement.FORCE)
		public BooleanSeq(boolean... array) {
			this(array, 0, true);
		}
		
		@Pure(Enforcement.FORCE) // due to array copy
		BooleanSeq(boolean[] array, int i, boolean copy) {
			this.array = copy ? Arrays.copyOf(array, array.length) : array;
			this.i = i;
		}

		public Boolean first() {
			return array[i];
		}

		public ISeq<Boolean> next() {
			if (i + 1 < array.length)
				return new BooleanSeq(array, i + 1, false);
			return null;
		}

		public int size() {
			return array.length - i;
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
