package net.twagame.serial.io;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * This class implements a {@link SerializationOutput} in which the data is written into a byte array. The buffer automatically grows as
 * data is written to it. The data can be retrieved using <code>getByteArray()</code>. This class provides methods to efficiently write data
 * directly into the underlying byte array. Closing a <code>MemoryOutput</code> has no effect. The methods in this class can be called after
 * the stream has been closed without generating an IOException. This class is not thread-safe.
 * 
 * @author hadadzhi
 */
public class MemoryOutput implements SerializationOutput
{
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final int DEFAULT_CAPACITY = 512;

	private final Charset charset;

	private byte[] buffer;
	private int written = 0;

	/**
	 * Ensures that the buffer has the required amount of unused bytes remaining, expanding the buffer if needed. If the required capacity
	 * exceeds {@link Integer#MAX_VALUE}, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param required
	 */
	private void require(int required)
	{
		if (required < 0)
			throw new IllegalArgumentException("Can not require < 0 bytes");

		if (((long) buffer.length + required) > Integer.MAX_VALUE)
			throw new IllegalArgumentException("The MemoryOutput has exceeded the maximum byte array capacity.");

		if ((buffer.length - written - required) < 0)
		{
			long newCapacity = ((long) buffer.length + required) * 2;
			byte[] newBuffer = new byte[(newCapacity > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) newCapacity];
			System.arraycopy(buffer, 0, newBuffer, 0, written);
			buffer = newBuffer;
		}
	}

	/**
	 * Creates a new memory output stream. The buffer capacity is initially 512 bytes, though its size increases if necessary.
	 */
	public MemoryOutput()
	{
		this(DEFAULT_CAPACITY, DEFAULT_CHARSET);
	}

	/**
	 * Creates a new memory output stream, with a buffer capacity of the specified size, in bytes.
	 * 
	 * @param size
	 *            the initial size.
	 * @exception IllegalArgumentException
	 *                if size is negative.
	 */
	public MemoryOutput(int initialCapacity)
	{
		this(initialCapacity, DEFAULT_CHARSET);
	}

	/**
	 * Creates a new memory output stream that uses the specified charset to write strings. The buffer capacity is initially 512 bytes,
	 * though its size increases if necessary.
	 * 
	 * @param charset
	 */
	public MemoryOutput(Charset charset)
	{
		this(DEFAULT_CAPACITY, charset);
	}

	/**
	 * Creates a new memory output stream that uses the specified charset to write strings. The buffer capacity is initially 512 bytes,
	 * though its size increases if necessary.
	 * 
	 * @param charset
	 */
	public MemoryOutput(String charsetName)
	{
		this(DEFAULT_CAPACITY, Charset.forName(charsetName));
	}

	/**
	 * Creates a new memory output stream, with a buffer capacity of the specified size, in bytes, that uses the specified charset to write
	 * strings.
	 * 
	 * @param charset
	 */
	public MemoryOutput(int initialCapacity, Charset charset)
	{
		if (initialCapacity < 0)
		{
			throw new IllegalArgumentException("Negative initial capacity: " + initialCapacity);
		}
		buffer = new byte[initialCapacity];
		this.charset = charset;
	}

	/**
	 * Writes the specified byte to this byte array output stream.
	 * 
	 * @param b
	 *            the byte to be written.
	 */
	@Override
	public void write(int b)
	{
		require(1);
		buffer[written++] = (byte) b;
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code> to this byte array output stream.
	 * 
	 * @param b
	 *            the data.
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write.
	 */
	@Override
	public void write(byte b[], int off, int len)
	{
		if ((off < 0) || (off > b.length) || (len < 0) || (((off + len) - b.length) > 0))
		{
			throw new IndexOutOfBoundsException();
		}

		require(len);
		System.arraycopy(b, off, buffer, written, len);
		written += len;
	}

	/**
	 * Writes the least significant byte of a give int as a byte.
	 * 
	 * @param v
	 */
	@Override
	public void writeByte(byte v)
	{
		write(v);
	}

	/**
	 * Writes a boolean as a single byte.
	 * <p>
	 * {@code true == 1}, {@code false == 0}
	 * 
	 * @param v
	 */
	@Override
	public void writeBoolean(boolean v)
	{
		write(v ? 1 : 0);
	}

	@Override
	public void writeShort(short v)
	{
		require(2);
		buffer[written++] = (byte) (v >>> 8);
		buffer[written++] = (byte) (v);
	}

	/**
	 * Writes a 4-byte int.
	 * 
	 * @param v
	 */
	@Override
	public void writeInt(int v)
	{
		require(4);
		buffer[written++] = (byte) (v >>> 24);
		buffer[written++] = (byte) (v >>> 16);
		buffer[written++] = (byte) (v >>> 8);
		buffer[written++] = (byte) (v);
	}

	/**
	 * Writes an 8-byte long.
	 * 
	 * @param v
	 */
	@Override
	public void writeLong(long v)
	{
		require(8);
		buffer[written++] = (byte) (v >>> 56);
		buffer[written++] = (byte) (v >>> 48);
		buffer[written++] = (byte) (v >>> 40);
		buffer[written++] = (byte) (v >>> 32);
		buffer[written++] = (byte) (v >>> 24);
		buffer[written++] = (byte) (v >>> 16);
		buffer[written++] = (byte) (v >>> 8);
		buffer[written++] = (byte) (v);
	}

	/**
	 * Writes a 4-byte float.
	 * 
	 * @param v
	 */
	@Override
	public void writeFloat(float v)
	{
		writeInt(Float.floatToIntBits(v));
	}

	/**
	 * Writes an 8-byte double.
	 * 
	 * @param v
	 */
	@Override
	public void writeDouble(double v)
	{
		writeLong(Double.doubleToLongBits(v));
	}

	/**
	 * Writes a {@code String}, prefixed by its length as unsigned short, using the specified encoding.
	 * 
	 * @param str
	 *            a string to be written.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws NullPointerException
	 *             if the argument is {@code null}.
	 */
	@Override
	public void writeString(String str) throws IOException
	{
		if (str == null)
			throw new NullPointerException();

		byte[] bytes = str.getBytes(charset);

		if (bytes.length > 65535)
			throw new IOException("The encoded string is too long: " + bytes.length + " bytes.");

		// Write length as unsigned short
		require(2);
		buffer[written++] = (byte) ((bytes.length >>> 8) & 0xFF);
		buffer[written++] = (byte) (bytes.length & 0xFF);
		// Write string as bytes
		write(bytes, 0, bytes.length);
	}

	/**
	 * Creates a newly allocated byte array. Its size is the current number of bytes written to this output and the valid contents of the
	 * buffer have been copied into it.
	 * 
	 * @return the current contents of this output stream, as a byte array.
	 * @see java.io.ByteArrayOutputStream#size()
	 */
	public byte[] getBytes()
	{
		byte[] bytes = new byte[written];
		System.arraycopy(buffer, 0, bytes, 0, written);
		return bytes;
	}

	/**
	 * Returns the current size of valid data in the buffer.
	 * 
	 * @return the value of the <code>written</code> field, which is the number of valid (used for writing) bytes in the underlying array.
	 * @see java.io.ByteArrayOutputStream#count
	 */
	public int size()
	{
		return written;
	}

	/**
	 * Resets the <code>written</code> field of this {@link MemoryOutput} to <code>0</code>, so that all currently accumulated output is
	 * discarded. The output can be used again, reusing the already allocated buffer space.
	 * 
	 * @see java.io.ByteArrayInputStream#count
	 */
	public void reset()
	{
		written = 0;
	}

	/**
	 * Closing a <tt>MemoryOutput</tt> has no effect. The methods of this class can be called after it has been closed without generating
	 * <tt>IOException</tt>.
	 */
	@Override
	public void close() throws IOException
	{
	}
}
