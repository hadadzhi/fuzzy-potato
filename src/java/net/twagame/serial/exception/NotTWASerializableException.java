package net.twagame.serial.exception;

import net.twagame.serial.annotation.TWASerializable;

/**
 * Thrown when trying to serialize an object of unsupported type, or with missing {@link TWASerializable} annotation.
 * 
 * @author hadadzhi
 */
@SuppressWarnings("serial")
public class NotTWASerializableException extends RuntimeException
{
	public NotTWASerializableException(String message)
	{
		super(message);
	}
}
