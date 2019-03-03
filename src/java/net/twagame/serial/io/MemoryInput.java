package net.twagame.serial.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * This class implements a {@link SerializationInput} in which the data is read from a byte array. This class provides methods to
 * efficiently read data directly from the underlying byte array. Closing a <code>MemoryInput</code> has no effect. The methods in this
 * class can be called after it has been closed without generating an IOException. This class is not thread-safe.
 * 
 * @author hadadzhi
 */
public final class MemoryInput implements SerializationInput
{
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private byte[] buffer;
	private int read = 0;

	// Used in readString()
	private int size;
	private Charset charset = DEFAULT_CHARSET;

	/**
	 * Creates a MemoryInput that reads from the specified byte array.
	 * 
	 * @param byteArray
	 */
	public MemoryInput(byte[] byteArray)
	{
		buffer = byteArray;
	}

	/**
	 * Creates a MemoryInput that reads from the specified byte array and uses the specified charset to read strings.
	 * 
	 * @param byteArray
	 * @param charset
	 */
	public MemoryInput(byte[] byteArray, Charset charset)
	{
		this(byteArray);
		this.charset = charset;
	}

	/**
	 * Sets a new byte array to read from and resets the read bytes counter to <code>0</code>. <b>The new byte array should not be modified
	 * externally after it has been passed to this method!</b>
	 * 
	 * @param newByteArray
	 */
	public void reset(byte[] newByteArray)
	{
		read = 0;
		buffer = newByteArray;
	}

	/**
	 * Requires the buffer to have {@code required} more remaining bytes. If the buffer has less than {@code required} bytes remaining,
	 * EOFException is thrown.
	 * 
	 * @param required
	 * @throws EOFException
	 */
	private void require(int required) throws EOFException
	{
		if (required < 0)
			throw new IllegalArgumentException();

		if ((buffer.length - read) < required)
			throw new EOFException();
	}

	/**
	 * Reads the next byte of data from this input stream. The value byte is returned as an <code>int</code> in the range <code>0</code> to
	 * <code>255</code>. If no byte is available because the end of the stream has been reached, the value <code>-1</code> is returned.
	 * <p>
	 * This <code>read</code> method cannot block.
	 * 
	 * @return the next byte of data, or <code>-1</code> if the end of the stream has been reached.
	 */
	@Override
	public int read()
	{
		//buffer[read++] is implicitly cast to int and "& 0xFF" gets rid of the sign bit.
		return (read < buffer.length) ? (buffer[read++] & 0xFF) : -1;
	}

	/**
	 * Reads up to <code>len</code> bytes of data into an array of bytes from this input stream. If <code>pos</code> equals
	 * <code>count</code>, then <code>-1</code> is returned to indicate end of file. Otherwise, the number <code>k</code> of bytes read is
	 * equal to the smaller of <code>len</code> and <code>count-pos</code>. If <code>k</code> is positive, then bytes <code>buf[pos]</code>
	 * through <code>buf[pos+k-1]</code> are copied into <code>b[off]</code> through <code>b[off+k-1]</code> in the manner performed by
	 * <code>System.arraycopy</code>. The value <code>k</code> is added into <code>pos</code> and <code>k</code> is returned.
	 * <p>
	 * This <code>read</code> method cannot block.
	 * 
	 * @param b
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in the destination array <code>b</code>
	 * @param len
	 *            the maximum number of bytes read.
	 * @return the total number of bytes read into the buffer, or <code>-1</code> if there is no more data because the end of the stream has
	 *         been reached.
	 * @exception NullPointerException
	 *                If <code>b</code> is <code>null</code>.
	 * @exception IndexOutOfBoundsException
	 *                If <code>off</code> is negative, <code>len</code> is negative, or <code>len</code> is greater than
	 *                <code>b.length - off</code>
	 */
	@Override
	public int read(byte b[], int off, int len)
	{
		if (b == null)
		{
			throw new NullPointerException();
		}
		else if ((off < 0) || (len < 0) || (len > (b.length - off)))
		{
			throw new IndexOutOfBoundsException();
		}

		if (read >= buffer.length)
		{
			return -1;
		}

		int avail = buffer.length - read;

		if (len > avail)
		{
			len = avail;
		}
		if (len <= 0)
		{
			return 0;
		}

		System.arraycopy(buffer, read, b, off, len);
		read += len;

		return len;
	}

	/**
	 * Closing a <tt>MemoryInput</tt> has no effect. The methods in this class can be called after it has been closed without generating an
	 * <tt>IOException</tt>.
	 */
	@Override
	public void close() throws IOException
	{
	}

	/**
	 * Reads a string from the input, assuming it was written using {@link MamoryOutput#writeString(String)}.
	 * 
	 * @return the read {@code String} or {@code null} if EOF has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public String readString() throws IOException
	{
		size = readShortUnsigned();

		if (size == -1)
			return null;

		require(size);
		read += size;

		return new String(buffer, read - size, size, charset);
	}

	/**
	 * Reads two bytes as unsigned short. The result is returned as int. This method returns {@code -1} if EOF has been reached before
	 * reading both bytes.
	 * 
	 * @return the next two bytes from the underlying stream, interpreted as an unsigned short.
	 */
	private int readShortUnsigned()
	{
		int ch1 = read();
		int ch2 = read();
		if ((ch1 | ch2) < 0)
			return -1; // EOF
		return (ch1 << 8) + (ch2 << 0);
	}

	@Override
	public byte readByte() throws IOException
	{
		require(1);
		return buffer[read++];
	}

	@Override
	public boolean readBoolean() throws IOException
	{
		require(1);
		return buffer[read++] != 0;
	}

	@Override
	public short readShort() throws IOException
	{
		require(2);
		//The "& 0xFF" is to get rid of the sign bit because buffer[read++] is implicitly cast to int.
		return (short) (((buffer[read++] & 0xFF) << 8) | (buffer[read++] & 0xFF));
	}

	@Override
	public int readInt() throws IOException
	{
		require(4);
		return ((buffer[read++] & 0xFF) << 24)
			| ((buffer[read++] & 0xFF) << 16)
			| ((buffer[read++] & 0xFF) << 8)
			| (buffer[read++] & 0xFF);
	}

	@Override
	public long readLong() throws IOException
	{
		require(8);
		return (((long) buffer[read++]) << 56)
			| (((long) buffer[read++] & 0xFF) << 48)
			| (((long) buffer[read++] & 0xFF) << 40)
			| (((long) buffer[read++] & 0xFF) << 32)
			| (((long) buffer[read++] & 0xFF) << 24)
			| ((buffer[read++] & 0xFF) << 16)
			| ((buffer[read++] & 0xFF) << 8)
			| ((buffer[read++] & 0xFF));
	}

	@Override
	public float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}
}
