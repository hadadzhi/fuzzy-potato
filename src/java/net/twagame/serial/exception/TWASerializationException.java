package net.twagame.serial.exception;

/**
 * Indicates a critical error during serialization/deserialization.
 * 
 * @author hadadzhi
 */
@SuppressWarnings("serial")
public class TWASerializationException extends RuntimeException
{
	public TWASerializationException()
	{
		super();
	}

	public TWASerializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TWASerializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TWASerializationException(String message)
	{
		super(message);
	}

	public TWASerializationException(Throwable cause)
	{
		super(cause);
	}
}
