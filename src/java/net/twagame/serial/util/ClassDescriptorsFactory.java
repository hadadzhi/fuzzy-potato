package net.twagame.serial.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.twagame.serial.annotation.TWASerializable;
import net.twagame.serial.exception.NotTWASerializableException;

/**
 * Creates {@link ClassDescriptor}s. This class is used by {@link ClassDescriptorsCache} automatically to create and cache
 * {@link ClassDescriptor}s.
 * 
 * @author hadadzhi
 */
public class ClassDescriptorsFactory
{
	private static boolean forceSafeInstantiation;

	static ClassDescriptor ofClassNamed(String name) throws ClassNotFoundException
	{
		return ofClass(Class.forName(name));
	}

	static ClassDescriptor ofClass(Class<?> clazz)
	{
		List<FieldDescriptor> fieldDescList = null;

		//If the class is TWASerializable, collect field descriptors
		if (clazz.isAnnotationPresent(TWASerializable.class))
		{
			forceSafeInstantiation = clazz.getAnnotation(TWASerializable.class).forceSafeInstantiation();

			fieldDescList = getSerializableFieldDescriptorsList(clazz);

			Collections.sort(fieldDescList);

			return new ClassDescriptor(clazz, forceSafeInstantiation, fieldDescList.toArray(new FieldDescriptor[0]));
		}
		else
		{
			return new ClassDescriptor(clazz, false, null);
		}
	}

	private static List<FieldDescriptor> getSerializableFieldDescriptorsList(Class<?> clazz)
	{
		List<FieldDescriptor> list = new ArrayList<>();
		int mods;

		FieldDescriptor fdesc;
		for (Field f : clazz.getDeclaredFields())
		{
			mods = f.getModifiers();

			//Ignore transient and static fields
			if (!Modifier.isTransient(mods) && !Modifier.isStatic(mods))
			{
				fdesc = new FieldDescriptor(f);
				list.add(fdesc);

				//If this class has Collections or Maps, force safe instantiation, regardless of the annotation attribute.
				if (Collection.class.isAssignableFrom(f.getType()) || Map.class.isAssignableFrom(f.getType()))
				{
					forceSafeInstantiation = true;
				}
			}
		}

		// If this class has a TWASerializable superclass, collect its superclass' field descriptors too
		Class<?> superClass = clazz.getSuperclass();
		if (superClass.isAnnotationPresent(TWASerializable.class))
		{
			list.addAll(getSerializableFieldDescriptorsList(superClass));
		}
		else if (!superClass.equals(Object.class))
		{
			throw new NotTWASerializableException("The " + clazz.getName() + "'s superclass " + superClass.getName() + "is not TWASerializable");
		}

		return list;
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ClassDescriptorsFactory()
	{
	}
}
