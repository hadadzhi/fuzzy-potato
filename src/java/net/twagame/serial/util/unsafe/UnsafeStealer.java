package net.twagame.serial.util.unsafe;

import java.lang.reflect.Field;

import net.twagame.serial.exception.TWASerializationException;
import sun.misc.Unsafe;

/**
 * Grand Theft Unsafe. See {@link UnsafeStealer#getUnsafe()}
 * 
 * @author hadadzhi
 */
public enum UnsafeStealer
{
	INSTANCE;

	private final Unsafe theUnsafe;

	private UnsafeStealer()
	{
		try
		{
			Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			theUnsafe = (Unsafe) unsafeField.get(null);
		}
		catch (Exception e)
		{
			throw new TWASerializationException("UnsafeStealer could not obtain the Unsafe.", e);
		}
	}

	/**
	 * Like {@link Unsafe#getUnsafe()}, but bypassing the security check. Returns the stolen Unsafe instance. Use with extreme caution.
	 * Welcome to the dark side.
	 * 
	 * @return an instance of the {@link sun.misc.Unsafe} class.
	 */
	public static Unsafe getUnsafe()
	{
		return INSTANCE.theUnsafe;
	}
}
