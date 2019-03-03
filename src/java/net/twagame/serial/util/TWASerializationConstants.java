package net.twagame.serial.util;

/**
 * This class contains constants and enums used by various classes in the net.twagame.serial package.
 * 
 * @author hadadzhi
 */
public class TWASerializationConstants
{
	/**
	 * Type tags for primitive wrappers.
	 */
	public static final byte TAG_WRAPPER_BYTE = 1;
	public static final byte TAG_WRAPPER_BOOLEAN = 2;
	public static final byte TAG_WRAPPER_SHORT = 3;
	public static final byte TAG_WRAPPER_INT = 4;
	public static final byte TAG_WRAPPER_LONG = 5;
	public static final byte TAG_WRAPPER_FLOAT = 6;
	public static final byte TAG_WRAPPER_DOUBLE = 7;

	/**
	 * Type tags for arrays
	 */
	public static final byte TAG_ARRAY_BYTE = 10;
	public static final byte TAG_ARRAY_BOOLEAN = 11;
	public static final byte TAG_ARRAY_SHORT = 12;
	public static final byte TAG_ARRAY_INT = 13;
	public static final byte TAG_ARRAY_LONG = 14;
	public static final byte TAG_ARRAY_FLOAT = 15;
	public static final byte TAG_ARRAY_DOUBLE = 16;
	public static final byte TAG_ARRAY_OBJECT = 17;

	/**
	 * Stream tags
	 */
	public static final byte TAG_NEWOBJECT = 30;
	public static final byte TAG_REFERENCE = 31;
	public static final byte TAG_NULL = 32;
	public static final byte TAG_STRING = 33;
	public static final byte TAG_COLLECTION = 34;
	public static final byte TAG_MAP = 35;

	/**
	 * Enumeration for class types
	 */
	public static enum ClassType
	{
		ARRAY,
		COLLECTION,
		MAP,
		STRING,
		WRAPPER,
		TWASERIALIZABLE,
		PRIMITIVE;
	}

	/**
	 * Enumeration for subtypes
	 */
	public static enum ClassSubType
	{
		BYTE,
		BOOLEAN,
		SHORT,
		INT,
		LONG,
		FLOAT,
		DOUBLE,
		OBJECT;
	}

	/**
	 * Private constructor to prevent instantiation
	 */
	private TWASerializationConstants()
	{
	}
}
