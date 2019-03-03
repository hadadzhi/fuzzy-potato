package net.twagame.serial.util.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import sun.misc.Unsafe;

/**
 * A wrapper for the {@link Field} class that uses {@link Unsafe} to get/set field values.
 * 
 * @author hadadzhi
 */
public final class UnsafeField
{
	private static final Unsafe unsafe = UnsafeStealer.getUnsafe();

	private final Field field;
	private final long offset;

	private final Object staticBase;
	private final boolean isStatic;

	public UnsafeField(Field field)
	{
		this.field = field;
		if (Modifier.isStatic(field.getModifiers()))
		{
			this.offset = unsafe.staticFieldOffset(field);
			this.staticBase = unsafe.staticFieldBase(field);
			this.isStatic = true;
		}
		else
		{
			this.offset = unsafe.objectFieldOffset(field);
			this.staticBase = null;
			this.isStatic = false;
		}
	}

	public Object get(Object o)
	{
		if (isStatic)
		{
			return unsafe.getObject(staticBase, offset);
		}
		else
		{
			return unsafe.getObject(o, offset);
		}
	}

	public void set(Object o, Object value)
	{
		if (isStatic)
		{
			unsafe.putObject(staticBase, offset, value);
		}
		else
		{
			unsafe.putObject(o, offset, value);
		}
	}

	public int getInt(Object o)
	{
		if (isStatic)
		{
			return unsafe.getInt(staticBase, offset);
		}
		else
		{
			return unsafe.getInt(o, offset);
		}
	}

	public void setInt(Object o, int value)
	{
		if (isStatic)
		{
			unsafe.putInt(staticBase, offset, value);
		}
		else
		{
			unsafe.putInt(o, offset, value);
		}
	}

	public byte getByte(Object o)
	{
		if (isStatic)
		{
			return unsafe.getByte(staticBase, offset);
		}
		else
		{
			return unsafe.getByte(o, offset);
		}
	}

	public void setByte(Object o, byte value)
	{
		if (isStatic)
		{
			unsafe.putByte(staticBase, offset, value);
		}
		else
		{
			unsafe.putByte(o, offset, value);
		}
	}

	public short getShort(Object o)
	{
		if (isStatic)
		{
			return unsafe.getShort(staticBase, offset);
		}
		else
		{
			return unsafe.getShort(o, offset);
		}
	}

	public void setShort(Object o, short value)
	{
		if (isStatic)
		{
			unsafe.putShort(staticBase, offset, value);
		}
		else
		{
			unsafe.putShort(o, offset, value);
		}
	}

	public long getLong(Object o)
	{
		if (isStatic)
		{
			return unsafe.getLong(staticBase, offset);
		}
		else
		{
			return unsafe.getLong(o, offset);
		}
	}

	public void setLong(Object o, long value)
	{
		if (isStatic)
		{
			unsafe.putLong(staticBase, offset, value);
		}
		else
		{
			unsafe.putLong(o, offset, value);
		}
	}

	public float getFloat(Object o)
	{
		if (isStatic)
		{
			return unsafe.getFloat(staticBase, offset);
		}
		else
		{
			return unsafe.getFloat(o, offset);
		}
	}

	public void setFloat(Object o, float value)
	{
		if (isStatic)
		{
			unsafe.putFloat(staticBase, offset, value);
		}
		else
		{
			unsafe.putFloat(o, offset, value);
		}
	}

	public double getDouble(Object o)
	{
		if (isStatic)
		{
			return unsafe.getDouble(staticBase, offset);
		}
		else
		{
			return unsafe.getDouble(o, offset);
		}
	}

	public void setDouble(Object o, double value)
	{
		if (isStatic)
		{
			unsafe.putDouble(staticBase, offset, value);
		}
		else
		{
			unsafe.putDouble(o, offset, value);
		}
	}

	public boolean getBoolean(Object o)
	{
		if (isStatic)
		{
			return unsafe.getBoolean(staticBase, offset);
		}
		else
		{
			return unsafe.getBoolean(o, offset);
		}
	}

	public void setBoolean(Object o, boolean value)
	{
		if (isStatic)
		{
			unsafe.putBoolean(staticBase, offset, value);
		}
		else
		{
			unsafe.putBoolean(o, offset, value);
		}
	}

	/**
	 * Returns the wrapped {@link Field}.
	 * 
	 * @return
	 */
	public Field getField()
	{
		return field;
	}
}
