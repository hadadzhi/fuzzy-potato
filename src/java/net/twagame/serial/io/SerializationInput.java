package net.twagame.serial.io;

import java.io.IOException;

/**
 * An input for the TWADeserializer. This interface is implemented by {@link StreamInput} and {@link MemoryInput}.
 * 
 * @author hadadzhi
 */
public interface SerializationInput
{
	/**
	 * Reads a {@code String}.
	 */
	public String readString() throws IOException;

	/**
	 * Reads one byte. The value byte is returned as an <code>int</code> in the range <code>0</code> to <code>255</code>. If no byte is
	 * available because the end of the stream has been reached, the value <code>-1</code> is returned.
	 */
	public int read() throws IOException;

	/**
	 * Reads up to <code>len</code> bytes of data into an array of bytes. Returns the number of bytes actually read.
	 * 
	 * @param b
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in the destination array <code>b</code>
	 * @param len
	 *            the maximum number of bytes read.
	 */
	public int read(byte b[], int off, int len) throws IOException;

	/**
	 * Reads a byte.
	 */
	public byte readByte() throws IOException;

	/**
	 * Reads a boolean.
	 */
	public boolean readBoolean() throws IOException;

	/**
	 * Reads a short.
	 */
	public short readShort() throws IOException;

	/**
	 * Reads an int.
	 */
	public int readInt() throws IOException;

	/**
	 * Reads a long.
	 */
	public long readLong() throws IOException;

	/**
	 * reads a float.
	 */
	public float readFloat() throws IOException;

	/**
	 * Reads a double.
	 */
	public double readDouble() throws IOException;

	/**
	 * Closes the underlying stream, if such stream exists.
	 */
	public void close() throws IOException;
}
