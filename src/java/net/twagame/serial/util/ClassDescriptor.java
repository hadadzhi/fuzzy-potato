package net.twagame.serial.util;

import java.util.Collection;
import java.util.Map;

import net.twagame.serial.TWADeserializer;
import net.twagame.serial.TWASerializer;
import net.twagame.serial.annotation.TWASerializable;
import net.twagame.serial.exception.TWASerializationException;
import net.twagame.serial.util.TWASerializationConstants.ClassSubType;
import net.twagame.serial.util.TWASerializationConstants.ClassType;
import net.twagame.serial.util.unsafe.UnsafeStealer;
import sun.misc.Unsafe;

/**
 * Holds information about a {@link Class}, obtained by reflection and used by {@link TWASerializer} and {@link TWADeserializer}. This class
 * is intended to be immutable though does not implement paranoid protection for performance reasons. Instances of this class are created by
 * the {@link ClassDescriptorsFactory} automatically.
 * 
 * @author hadadzhi
 */
public class ClassDescriptor
{
	private static final Unsafe unsafe = UnsafeStealer.getUnsafe();

	private final String name;
	private final Class<?> reflectedClass;
	private final Class<?> componentType;
	private final ClassType type;
	private final ClassSubType subType;
	private final FieldDescriptor[] fieldDescriptors;
	private final boolean forceSafeInstantiation;

	ClassDescriptor(Class<?> reflectedClass, boolean forceSafeInstantiation, FieldDescriptor[] fieldDescriptors)
	{
		this.reflectedClass = reflectedClass;
		this.componentType = reflectedClass.getComponentType();
		this.name = reflectedClass.getName();
		this.fieldDescriptors = fieldDescriptors;
		this.subType = calculateSubType(reflectedClass);
		this.forceSafeInstantiation = forceSafeInstantiation;

		if (reflectedClass.isPrimitive())
		{
			this.type = ClassType.PRIMITIVE;
		}
		else if (reflectedClass.isArray())
		{
			this.type = ClassType.ARRAY;
		}
		else if (Collection.class.isAssignableFrom(reflectedClass))
		{
			this.type = ClassType.COLLECTION;
		}
		else if (Map.class.isAssignableFrom(reflectedClass))
		{
			this.type = ClassType.MAP;
		}
		else if (reflectedClass.equals(String.class))
		{
			this.type = ClassType.STRING;
		}
		else if (reflectedClass.equals(Boolean.class)
				|| reflectedClass.equals(Byte.class)
				|| reflectedClass.equals(Integer.class)
				|| reflectedClass.equals(Short.class)
				|| reflectedClass.equals(Double.class)
				|| reflectedClass.equals(Float.class)
				|| reflectedClass.equals(Long.class))
		{
			this.type = ClassType.WRAPPER;
		}
		else if (reflectedClass.isAnnotationPresent(TWASerializable.class))
		{
			this.type = ClassType.TWASERIALIZABLE;
		}
		else
		{
			throw new AssertionError("This should's have happened");
		}
	}

	private ClassSubType calculateSubType(Class<?> c)
	{
		Class<?> type = c.isArray() ? c.getComponentType() : c;
		if (type.equals(int.class) || type.equals(Integer.class))
		{
			return ClassSubType.INT;
		}
		else if (type.equals(short.class) || type.equals(Short.class))
		{
			return ClassSubType.SHORT;
		}
		else if (type.equals(long.class) || type.equals(Long.class))
		{
			return ClassSubType.LONG;
		}
		else if (type.equals(float.class) || type.equals(Float.class))
		{
			return ClassSubType.FLOAT;
		}
		else if (type.equals(double.class) || type.equals(Double.class))
		{
			return ClassSubType.DOUBLE;
		}
		else if (type.equals(byte.class) || type.equals(Byte.class))
		{
			return ClassSubType.BYTE;
		}
		else if (type.equals(boolean.class) || type.equals(Boolean.class))
		{
			return ClassSubType.BOOLEAN;
		}
		else
		{
			return ClassSubType.OBJECT;
		}
	}

	public Object instantiate()
	{
		try
		{
			if (forceSafeInstantiation)
			{
				return reflectedClass.newInstance();
			}
			else
			{
				return unsafe.allocateInstance(reflectedClass);
			}
		}
		catch (Exception e)
		{
			throw new TWASerializationException("Could not instantiate class " + this.reflectedClass.getName(), e);
		}
	}

	public String getName()
	{
		return name;
	}

	public FieldDescriptor[] getFieldDescriptors()
	{
		return fieldDescriptors;
	}

	public ClassType getType()
	{
		return type;
	}

	public Class<?> getComponentType()
	{
		return componentType;
	}

	public ClassSubType getSubType()
	{
		return subType;
	}

	public Class<?> getReflectedClass()
	{
		return reflectedClass;
	}
}
