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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.twagame.serial.exception.NotTWASerializableException;
import net.twagame.serial.exception.TWASerializationException;
import net.twagame.serial.io.SerializationOutput;
import net.twagame.serial.util.ClassDescriptor;
import net.twagame.serial.util.ClassDescriptorsCache;
import net.twagame.serial.util.FieldDescriptor;
import net.twagame.serial.util.TWASerializationConstants;
import net.twagame.serial.util.unsafe.UnsafeField;

/**
 * The serializer. This class is not thread safe.
 * 
 * @author hadadzhi
 */
public class TWASerializer
{
	private final HandleTable handles = new HandleTable();
	private final SerializationOutput out;

	private ClassDescriptor currentClassDesc;

	private boolean collectionOrMapAllowed = true;

	/**
	 * Creates a new TWASerializer that uses the specified {@link SerializationOutput}.
	 * 
	 * @param out
	 */
	public TWASerializer(SerializationOutput out)
	{
		this.out = out;
	}

	/**
	 * Closes the underlying {@link SerializationOutput}.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		resetHandles();
		out.close();
	}

	/**
	 * Reinitializes the handle system.
	 */
	public void resetHandles()
	{
		handles.reset();
	}

	/**
	 * Serializes an object and writes it to the {@link SerializationOutput}.
	 * 
	 * @param o
	 *            an object to serialize
	 * @throws TWASerializationException
	 *             if an exception occurs during serialization. The original exception can be extracted using {@link Exception#getCause()}.
	 *             This exception indicates that the current {@link SerializationOutput} is broken and should not be used any further.
	 */
	@SuppressWarnings("rawtypes")
	public void writeObject(Object o)
	{
		try
		{
			if (o == null)
			{
				writeNull();
				return;
			}

			Integer handle = handles.get(o);
			if (handle != null)
			{
				writeHandle(handle);
				return;
			}

			currentClassDesc = ClassDescriptorsCache.getInstance().forClass(o.getClass());

			switch (currentClassDesc.getType())
			{
				case WRAPPER:
				{
					switch (currentClassDesc.getSubType())
					{
						case BYTE:
						{
							out.write(TAG_WRAPPER_BYTE);
							out.writeByte(((Byte) o).byteValue());
							return;
						}
						case BOOLEAN:
						{
							out.write(TAG_WRAPPER_BOOLEAN);
							out.writeBoolean(((Boolean) o).booleanValue());
							return;
						}
						case SHORT:
						{
							out.write(TAG_WRAPPER_SHORT);
							out.writeShort(((Short) o).shortValue());
							return;
						}
						case INT:
						{
							out.write(TAG_WRAPPER_INT);
							out.writeInt(((Integer) o).intValue());
							return;
						}
						case LONG:
						{
							out.write(TAG_WRAPPER_LONG);
							out.writeLong(((Long) o).longValue());
							return;
						}
						case FLOAT:
						{
							out.write(TAG_WRAPPER_FLOAT);
							out.writeFloat(((Float) o).floatValue());
							return;
						}
						case DOUBLE:
						{
							out.write(TAG_WRAPPER_DOUBLE);
							out.writeDouble(((Double) o).doubleValue());
							return;
						}
						default:
						{
							throw new AssertionError();
						}
					}
				}
				case ARRAY:
				{
					writeArray(o);
					return;
				}
				case COLLECTION:
				{
					writeCollection((Collection) o);
					return;
				}
				case MAP:
				{
					writeMap((Map) o);
					return;
				}
				case STRING:
				{
					writeString((String) o);
					return;
				}
				case TWASERIALIZABLE:
				{
					writeNewObject(o);
					return;
				}
				default:
				{
					throw new NotTWASerializableException("The object is not an Array, Collection, Map, String or TWASerializable");
				}
			}
		}
		catch (IOException e)
		{
			throw new TWASerializationException(e);
		}
	}

	private void writeString(String str) throws IOException
	{
		assert (str != null);

		int handle = handles.assign(str);

		out.write(TAG_STRING);
		out.writeInt(handle);

		out.writeString(str);
	}

	private void writeNewObject(Object o) throws IOException
	{
		boolean oldCollectionAllowedFlag = collectionOrMapAllowed;
		collectionOrMapAllowed = true;

		int handle = handles.assign(o);

		out.write(TAG_NEWOBJECT);
		out.writeInt(handle);

		out.writeString(currentClassDesc.getName());

		writeFields(o, currentClassDesc.getFieldDescriptors());

		collectionOrMapAllowed = oldCollectionAllowedFlag;
	}

	private void writeFields(Object o, FieldDescriptor[] fieldDescriptors) throws IOException
	{
		UnsafeField f;
		loop: for (FieldDescriptor fdesc : fieldDescriptors)
		{
			currentClassDesc = ClassDescriptorsCache.getInstance().forClass(fdesc.getUnsafeField().getField().getType());
			f = fdesc.getUnsafeField();
			switch (currentClassDesc.getType())
			{
				case PRIMITIVE:
				{
					switch (currentClassDesc.getSubType())
					{
						case BYTE:
						{
							out.writeByte(f.getByte(o));
							continue loop;
						}
						case BOOLEAN:
						{
							out.writeBoolean(f.getBoolean(o));
							continue loop;
						}
						case SHORT:
						{
							out.writeShort(f.getShort(o));
							continue loop;
						}
						case INT:
						{
							out.writeInt(f.getInt(o));
							continue loop;
						}
						case LONG:
						{
							out.writeLong(f.getLong(o));
							continue loop;
						}
						case FLOAT:
						{
							out.writeFloat(f.getFloat(o));
							continue loop;
						}
						case DOUBLE:
						{
							out.writeDouble(f.getDouble(o));
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
					writeObject(f.get(o));
					continue loop;
				}
			}
		}
	}

	private void writeArray(Object a) throws IOException
	{
		assert (a != null);

		collectionOrMapAllowed = false; // Collections/maps within arrays are not supported.

		int size = Array.getLength(a);
		int handle = handles.assign(a);

		switch (currentClassDesc.getSubType())
		{
			case BYTE:
			{
				out.write(TAG_ARRAY_BYTE);
				out.writeInt(handle);
				out.writeInt(size);

				out.write((byte[]) a, 0, size);

				break;
			}
			case BOOLEAN:
			{
				out.write(TAG_ARRAY_BOOLEAN);
				out.writeInt(handle);
				out.writeInt(size);

				for (boolean element : (boolean[]) a)
				{
					out.writeBoolean(element);
				}

				break;
			}
			case SHORT:
			{
				out.write(TAG_ARRAY_SHORT);
				out.writeInt(handle);
				out.writeInt(size);

				for (short element : (short[]) a)
				{
					out.writeShort(element);
				}

				break;
			}
			case INT:
			{
				out.write(TAG_ARRAY_INT);
				out.writeInt(handle);
				out.writeInt(size);

				for (int element : (int[]) a)
				{
					out.writeInt(element);
				}

				break;
			}
			case LONG:
			{
				out.write(TAG_ARRAY_LONG);
				out.writeInt(handle);
				out.writeInt(size);

				for (long element : (long[]) a)
				{
					out.writeLong(element);
				}

				break;
			}
			case FLOAT:
			{
				out.write(TAG_ARRAY_FLOAT);
				out.writeInt(handle);
				out.writeInt(size);

				for (float element : (float[]) a)
				{
					out.writeFloat(element);
				}

				break;
			}
			case DOUBLE:
			{
				out.write(TAG_ARRAY_DOUBLE);
				out.writeInt(handle);
				out.writeInt(size);

				for (double element : (double[]) a)
				{
					out.writeDouble(element);
				}

				break;
			}
			case OBJECT:
			{
				out.write(TAG_ARRAY_OBJECT);
				out.writeInt(handle);
				out.writeInt(size);

				for (Object element : (Object[]) a)
				{
					writeObject(element);
				}

				break;
			}
		} // switch

		collectionOrMapAllowed = true;
	}

	@SuppressWarnings("rawtypes")
	private void writeCollection(Collection c) throws IOException
	{
		assert (c != null);

		if (!collectionOrMapAllowed)
		{
			throw new TWASerializationException("TWASerialization: collections/maps within collections/maps/arrays are not supported.");
		}

		collectionOrMapAllowed = false;

		int handle = handles.assign(c);

		out.write(TAG_COLLECTION);
		out.writeInt(handle);
		out.writeInt(c.size());

		for (Object o : c)
		{
			writeObject(o);
		}

		collectionOrMapAllowed = true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeMap(Map m) throws IOException
	{
		assert (m != null);

		if (!collectionOrMapAllowed)
		{
			throw new TWASerializationException("TWASerialization: collections/maps within collections/maps/arrays are not supported.");
		}

		collectionOrMapAllowed = false;

		int handle = handles.assign(m);

		out.write(TAG_MAP);
		out.writeInt(handle);
		out.writeInt(m.keySet().size());

		for (Map.Entry entry : (Set<Map.Entry>) m.entrySet())
		{
			writeObject(entry.getKey());
			writeObject(entry.getValue());
		}

		collectionOrMapAllowed = true;
	}

	private void writeHandle(Integer handle) throws IOException
	{
		out.write(TAG_REFERENCE);
		out.writeInt(handle.intValue());
	}

	/**
	 * Writes {@link TWASerializationConstants#TAG_NULL}.
	 * 
	 * @throws IOException
	 */
	private void writeNull() throws IOException
	{
		out.write(TAG_NULL);
	}

	/**
	 * Handles handles.
	 * 
	 * @author hadadzhi
	 */
	private static class HandleTable
	{
		/**
		 * Handles are assigned incrementally starting from {@code 0}.
		 */
		private int handleCounter = 0;

		/**
		 * Holds Object-to-Handle mappings.
		 */
		private final Map<Object, Integer> objToHandle = new HashMap<>();

		/**
		 * Assigns a handle to the given object. Returns the assigned handle.
		 * 
		 * @param o
		 * @return
		 */
		public int assign(Object o)
		{
			assert (o != null);

			objToHandle.put(o, handleCounter);

			return handleCounter++;
		}

		/**
		 * Returns the handle for the given object as an {@link Integer} or {@code null} if no handle is assigned to this object.
		 * 
		 * @param o
		 * @return
		 */
		public Integer get(Object o)
		{
			assert (o != null);

			return objToHandle.get(o);
		}

		/**
		 * Resets the HandleTable to its initial state.
		 */
		public void reset()
		{
			objToHandle.clear();
			handleCounter = 0;
		}
	}
}
