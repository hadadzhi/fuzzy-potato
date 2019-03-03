package net.twagame.serial.util;

import java.util.HashMap;
import java.util.Map;

import net.twagame.serial.TWADeserializer;
import net.twagame.serial.TWASerializer;

/**
 * This class provides access to {@link ClassDescriptor}s, creating and caching them as needed. Used by {@link TWASerializer} and
 * {@link TWADeserializer}. The cache is thread-local. Threads should use {@link ClassDescriptorsCache#getInstance()} to access the cache.
 * 
 * @author hadadzhi
 */
public class ClassDescriptorsCache
{
	private static final ThreadLocal<ClassDescriptorsCache> LOCAL_INSTANCE = new ThreadLocal<ClassDescriptorsCache>() {
		@Override
		protected ClassDescriptorsCache initialValue()
		{
			return new ClassDescriptorsCache();
		}
	};

	/**
	 * Returns an instance of this class for the current thread.
	 * 
	 * @return
	 */
	public static ClassDescriptorsCache getInstance()
	{
		return LOCAL_INSTANCE.get();
	}

	private final Map<String, ClassDescriptor> descForName = new HashMap<>();
	private final Map<Class<?>, ClassDescriptor> descForClass = new HashMap<>();

	public ClassDescriptor forName(String name) throws ClassNotFoundException
	{
		ClassDescriptor desc = descForName.get(name);

		if (desc != null)
		{
			return desc;
		}
		else if ((desc = descForClass.get(Class.forName(name))) != null)
		{
			descForName.put(name, desc);
			return desc;
		}
		else
		{
			desc = ClassDescriptorsFactory.ofClassNamed(name);

			descForName.put(name, desc);
			descForClass.put(desc.getReflectedClass(), desc);

			return desc;
		}
	}

	public ClassDescriptor forClass(Class<?> clazz)
	{
		ClassDescriptor desc = descForClass.get(clazz);

		if (desc != null)
		{
			return desc;
		}
		else if ((desc = descForName.get(clazz.getName())) != null)
		{
			descForClass.put(desc.getReflectedClass(), desc);
			return desc;
		}
		else
		{
			desc = ClassDescriptorsFactory.ofClass(clazz);

			descForClass.put(clazz, desc);
			descForName.put(desc.getName(), desc);

			return desc;
		}
	}

	/**
	 * No need to instantiate this class, threads should use {@link ClassDescriptorsCache#getInstance()} to access the cache.
	 */
	private ClassDescriptorsCache()
	{
	}
}
