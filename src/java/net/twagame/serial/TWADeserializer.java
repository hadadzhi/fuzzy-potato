package net.twagame.serial;

import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_BOOLEAN;
import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_BYTE;
import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_DOUBLE;
import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_FLOAT;
import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_INT;
import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_LONG;
import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_OBJECT;
import static net.twagame.serial.util.TWASerializationConstants.TAG_ARRAY_SHORT;
import static net.twagame.serial.util.TWASerializationConstants.TAG_COLLECTION;
import static net.twagame.serial.util.TWASerializationConstants.TAG_MAP;
import static net.twagame.serial.util.TWASerializationConstants.TAG_NEWOBJECT;
import static net.twagame.serial.util.TWASerializationConstants.TAG_NULL;
import static net.twagame.serial.util.TWASerializationConstants.TAG_REFERENCE;
import static net.twagame.serial.util.TWASerializationConstants.TAG_STRING;
import static net.twagame.serial.util.TWASerializationConstants.TAG_WRAPPER_BOOLEAN;
import static net.twagame.serial.util.TWASerializationConstants.TAG_WRAPPER_BYTE;
import static net.twagame.serial.util.TWASerializationConstants.TAG_WRAPPER_DOUBLE;
import static net.twagame.serial.util.TWASerializationConstants.TAG_WRAPPER_FLOAT;
import static net.twagame.serial.util.TWASerializationConstants.TAG_WRAPPER_INT;
import static net.twagame.serial.util.TWASerializationConstants.TAG_WRAPPER_LONG;
import static net.twagame.serial.util.TWASerializationConstants.TAG_WRAPPER_SHORT;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.twagame.serial.exception.TWASerializationException;
import net.twagame.serial.io.SerializationInput;
import net.twagame.serial.util.ClassDescriptor;
import net.twagame.serial.util.ClassDescriptorsCache;
import net.twagame.serial.util.FieldDescriptor;
import net.twagame.serial.util.TWASerializationConstants;
import net.twagame.serial.util.unsafe.UnsafeField;

/**
 * The deserializer. This class is not thread safe.
 * 
 * @author hadadzhi
 */
public class TWADeserializer
{
	private final HandleTable handles = new HandleTable();
	private final SerializationInput in;

	private ClassDescriptor currentClassDesc;
	private FieldDescriptor currentFieldDesc;

	private Object currentNewObject;

	/**
	 * Creates a new TWADeserializer that uses the specified {@link SerializationInput}.
	 * 
	 * @param in
	 */
	public TWADeserializer(SerializationInput in)
	{
		this.in = in;
	}

	/**
	 * Reads the next object from the {@link SerializationInput}.
	 * 
	 * @return the next object from the next object from the {@link SerializationInput}. May be {@code null}.
	 * @throws TWASerializationException
	 *             if an exception occurs during deserialization. The original exception can be retrieved with {@link Exception#getCause()}.
	 */
	public Object readObject()
	{
		try
		{
			int tag = in.read();
			switch (tag)
			{
				case TAG_REFERENCE:
				{
					return handles.get(in.readInt());
				}
				case TAG_NULL:
				{
					return null;
				}
				case TAG_NEWOBJECT:
				{
					return readNewObject();
				}
				case TAG_ARRAY_BOOLEAN:
				case TAG_ARRAY_BYTE:
				case TAG_ARRAY_DOUBLE:
				case TAG_ARRAY_FLOAT:
				case TAG_ARRAY_INT:
				case TAG_ARRAY_LONG:
				case TAG_ARRAY_SHORT:
				case TAG_ARRAY_OBJECT:
				{
					return readArray(tag);
				}
				case TAG_WRAPPER_BOOLEAN:
				case TAG_WRAPPER_BYTE:
				case TAG_WRAPPER_DOUBLE:
				case TAG_WRAPPER_FLOAT:
				case TAG_WRAPPER_INT:
				case TAG_WRAPPER_LONG:
				case TAG_WRAPPER_SHORT:
				{
					return readWrapper(tag);
				}
				case TAG_STRING:
				{
					return readString();
				}
				case TAG_COLLECTION:
				{
					return readCollection();
				}
				case TAG_MAP:
				{
					return readMap();
				}
				case -1:
				{
					throw new EOFException("TWASerialization: unexpected end of input");
				}
				default:
				{
					throw new AssertionError("Unknown tag");
				}
			}
		}
		catch (IOException | ClassNotFoundException e)
		{
			throw new TWASerializationException(e);
		}
	}

	/**
	 * Closes the underlying {@link SerializationInput}.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		in.close();
	}

	private Object readString() throws IOException
	{
		int handle = in.readInt();
		String str = in.readString();
		handles.put(handle, str);
		return str;
	}

	private Object readNewObject() throws IOException, ClassNotFoundException
	{
		int handle = in.readInt();

		currentClassDesc = ClassDescriptorsCache.getInstance().forName(in.readString());

		Object o = currentClassDesc.instantiate();

		readFields(o, currentClassDesc.getFieldDescriptors());

		handles.put(handle, o);

		return o;
	}

	private void readFields(Object o, FieldDescriptor[] fieldDescriptors) throws IOException, ClassNotFoundException
	{
		UnsafeField f;
		loop: for (FieldDescriptor fdesc : fieldDescriptors)
		{
			currentClassDesc = ClassDescriptorsCache.getInstance().forClass(fdesc.getUnsafeField().getField().getType());
			currentFieldDesc = fdesc;
			f = fdesc.getUnsafeField();
			switch (currentClassDesc.getType())
			{
				case PRIMITIVE:
				{
					switch (currentClassDesc.getSubType())
					{
						case BYTE:
						{
							f.setByte(o, in.readByte());
							continue loop;
						}
						case BOOLEAN:
						{
							f.setBoolean(o, in.readBoolean());
							continue loop;
						}
						case SHORT:
						{
							f.setShort(o, in.readShort());
							continue loop;
						}
						case INT:
						{
							f.setInt(o, in.readInt());
							continue loop;
						}
						case LONG:
						{
							f.setLong(o, in.readLong());
							continue loop;
						}
						case FLOAT:
						{
							f.setFloat(o, in.readFloat());
							continue loop;
						}
						case DOUBLE:
						{
							f.setDouble(o, in.readDouble());
							continue loop;
						}
						default:
						{
							throw new AssertionError();
						}
					}
				}
				default:
				{
					currentNewObject = o;
					f.set(o, readObject());
					continue loop;
				}
			}
		}
	}

	private Object readArray(int tag) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException, ClassNotFoundException
	{
		int handle = in.readInt();
		int size = in.readInt();

		switch (tag)
		{
			case TAG_ARRAY_BYTE:
			{
				byte[] bytes = new byte[size];

				in.read(bytes, 0, size);

				handles.put(handle, bytes);

				return bytes;
			}
			case TAG_ARRAY_BOOLEAN:
			{
				boolean[] booleans = new boolean[size];

				for (int i = 0; i < size; i++)
					booleans[i] = in.readBoolean();

				handles.put(handle, booleans);

				return booleans;
			}
			case TAG_ARRAY_SHORT:
			{
				short[] shorts = new short[size];

				for (int i = 0; i < size; i++)
					shorts[i] = in.readShort();

				handles.put(handle, shorts);

				return shorts;
			}
			case TAG_ARRAY_INT:
			{
				int[] ints = new int[size];

				for (int i = 0; i < size; i++)
					ints[i] = in.readInt();

				handles.put(handle, ints);

				return ints;
			}
			case TAG_ARRAY_LONG:
			{
				long[] longs = new long[size];

				for (int i = 0; i < size; i++)
					longs[i] = in.readLong();

				handles.put(handle, longs);

				return longs;
			}
			case TAG_ARRAY_FLOAT:
			{
				float[] floats = new float[size];

				for (int i = 0; i < size; i++)
					floats[i] = in.readFloat();

				handles.put(handle, floats);

				return floats;
			}
			case TAG_ARRAY_DOUBLE:
			{
				double[] doubles = new double[size];

				for (int i = 0; i < size; i++)
					doubles[i] = in.readDouble();

				handles.put(handle, doubles);

				return doubles;
			}
			case TAG_ARRAY_OBJECT:
			{
				Object array = Array.newInstance(currentClassDesc.getComponentType(), size);

				// This might help with the the multi-dimensional arrays.
				currentClassDesc = ClassDescriptorsCache.getInstance().forClass(currentClassDesc.getComponentType());

				for (int i = 0; i < size; i++)
					Array.set(array, i, readObject());

				handles.put(handle, array);

				return array;
			}
			default:
			{
				throw new AssertionError();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection readCollection() throws IOException, ClassNotFoundException
	{
		int handle = in.readInt();
		int size = in.readInt();

		Collection c = null;

		if (currentFieldDesc != null)
			c = (Collection) currentFieldDesc.getUnsafeField().get(currentNewObject);

		if (c == null)
			c = new ArrayList();

		c.clear();

		for (int i = 0; i < size; i++)
		{
			c.add(readObject());
		}

		handles.put(handle, c);

		return c;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map readMap() throws IOException, ClassNotFoundException
	{
		int handle = in.readInt();
		int size = in.readInt();

		Map m = null;

		if (currentFieldDesc != null) // Get the map from the current field, if there is one
			m = (Map) currentFieldDesc.getUnsafeField().get(currentNewObject);

		if (m == null) // If there is no current field, or it is not initialized with a map implementation, use default
			m = new HashMap();

		m.clear();

		Object key, value;
		for (int i = 0; i < size; i++)
		{
			key = readObject();
			value = readObject();
			m.put(key, value);
		}

		handles.put(handle, m);

		return m;
	}

	private Object readWrapper(int tag) throws IOException
	{
		switch (tag)
		{
			case TAG_WRAPPER_BYTE:
			{
				return Byte.valueOf(in.readByte());
			}
			case TAG_WRAPPER_BOOLEAN:
			{
				return Boolean.valueOf(in.readBoolean());
			}
			case TAG_WRAPPER_SHORT:
			{
				return Short.valueOf(in.readShort());
			}
			case TAG_WRAPPER_INT:
			{
				return Integer.valueOf(in.readInt());
			}
			case TAG_WRAPPER_LONG:
			{
				return Long.valueOf(in.readLong());
			}
			case TAG_WRAPPER_FLOAT:
			{
				return Float.valueOf(in.readFloat());
			}
			case TAG_WRAPPER_DOUBLE:
			{
				return Double.valueOf(in.readDouble());
			}
			default:
			{
				throw new AssertionError();
			}
		}
	}

	/**
	 * A lightweight replacement for the HashMap.
	 * 
	 * @author hadadzhi
	 */
	private static class HandleTable
	{
		private static final int INITIAL_SIZE = 16;

		private Object[] values = new Object[INITIAL_SIZE];

		/**
		 * Creates a Handle-to-Object mapping for the specified handle and object.
		 * 
		 * @param handle
		 * @param object
		 */
		public void put(int handle, Object object)
		{
			assert (object != null);
			assert (handle >= 0);

			if (handle > (values.length - 1))
			{
				Object[] newArray = new Object[handle * 2];
				System.arraycopy(values, 0, newArray, 0, values.length);
				values = newArray;
			}

			values[handle] = object;
		}

		/**
		 * Returns an object for a given handle.
		 * <p>
		 * This method should never return {@code null} assuming that the {@link TWASerializer} works properly and does not write references
		 * to the objects that have not been written yet, and the {@link TWADeserializer} works properly and requests objects only for
		 * handles read after the {@link TWASerializationConstants#TAG_REFERENCE TAG_REFERENCE}.
		 * 
		 * @param handle
		 * @return
		 */
		public Object get(int handle)
		{
			assert (handle < (values.length - 1));
			assert (values[handle] != null);

			return values[handle];
		}
	}
}
