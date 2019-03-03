package net.twagame.serial.util;

import java.lang.reflect.Field;

import net.twagame.serial.TWADeserializer;
import net.twagame.serial.TWASerializer;
import net.twagame.serial.util.unsafe.UnsafeField;

/**
 * Holds information about a {@link Field} of a {@link Class}, obtained by reflection and used by {@link TWASerializer} and
 * {@link TWADeserializer}. This class is intended to be immutable though does not implement paranoid protection for performance reasons.
 * Instances of this class are created by the {@link ClassDescriptorsFactory} automatically.
 * 
 * @author hadadzhi
 */
public class FieldDescriptor implements Comparable<FieldDescriptor>
{
	private final String name;
	private final UnsafeField reflectedUnsafeField;

	FieldDescriptor(Field reflectedField)
	{
		reflectedField.setAccessible(true);
		this.reflectedUnsafeField = new UnsafeField(reflectedField);
		this.name = reflectedField.getName();
	}

	public UnsafeField getUnsafeField()
	{
		return reflectedUnsafeField;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Compares field names lexicographically, if they are equal, compares declaring class names lexicographically. <b>Note: this class has
	 * a natural ordering that is inconsistent with equals.</b>
	 */
	@Override
	public int compareTo(FieldDescriptor other)
	{
		if (this == other)
		{
			return 0;
		}

		int compareNames = this.getName().compareTo(other.getName());

		if (compareNames != 0)
		{
			return compareNames;
		}
		else
		{
			return this.getUnsafeField().
						getField().
						getDeclaringClass().
						getName().
						compareTo(other.getUnsafeField().
										getField().
										getDeclaringClass().
										getName());
		}
	}
}
