package net.twagame.serial.io;

import java.io.IOException;

/**
 * An output for the TWASerializer. This interface is implemented by {@link StreamOutput} and {@link MemoryOutput}.
 * 
 * @author hadadzhi
 */
public interface SerializationOutput
{
	/**
	 * Writes a {@code String} to the underlying output stream.
	 */
	public void writeString(String str) throws IOException;

	/**
	 * Writes the least significant byte of a given int as a byte.
	 */
	public void write(int b) throws IOException;

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code> to this output.
	 */
	public void write(byte b[], int off, int len) throws IOException;

	/**
	 * Writes a byte.
	 */
	public void writeByte(byte v) throws IOException;

	/**
	 * Writes a boolean as 1 byte.
	 * <p>
	 * {@code true == 1}, {@code false == 0}
	 */
	public void writeBoolean(boolean v) throws IOException;

	/**
	 * Writes a 2-byte short.
	 */
	public void writeShort(short v) throws IOException;

	/**
	 * Writes a 4-byte int.
	 */
	public void writeInt(int v) throws IOException;

	/**
	 * Writes an 8-byte long.
	 */
	public void writeLong(long v) throws IOException;

	/**
	 * Writes a 4-byte float.
	 */
	public void writeFloat(float v) throws IOException;

	/**
	 * Writes an 8-byte double.
	 */
	public void writeDouble(double v) throws IOException;

	/**
	 * Closes the underlying stream, if such stream exists.
	 */
	public void close() throws IOException;
}
