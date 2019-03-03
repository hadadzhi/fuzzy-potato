package net.twagame.serial.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * This class implements a {@link SerializationInput} in which the data is read from some {@link InputStream}.
 * 
 * @author hadadzhi
 * @see DataInputStream
 */
public class StreamInput implements SerializationInput
{
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final int INITIAL_STRING_BUFFER_SIZE = 16;

	private final Charset charset;
	private final DataInputStream in;

	/**
	 * Used in readString()
	 */
	private int size;
	/**
	 * Used in readString()
	 */
	private byte[] stringBuffer = new byte[INITIAL_STRING_BUFFER_SIZE];

	/**
	 * Creates a new {@link StreamInput} that uses the {@link StreamInput#DEFAULT_CHARSET}.
	 * 
	 * @param istream
	 */
	public StreamInput(InputStream istream)
	{
		this(istream, DEFAULT_CHARSET);
	}

	/**
	 * Creates a new {@link StreamInput} that uses the specified charset.
	 * 
	 * @param istream
	 * @param charsetName
	 *            a {@#code String} containing the name of the charset to use.
	 */
	public StreamInput(InputStream istream, String charsetName)
	{
		this(istream, Charset.forName(charsetName));
	}

	/**
	 * Creates a new {@link StreamInput} that uses the specified charset.
	 * 
	 * @param istream
	 * @param charset
	 *            the {@link Charset} to use.
	 */
	public StreamInput(InputStream istream, Charset charset)
	{
		in = new DataInputStream(istream);
		this.charset = charset;
	}

	/**
	 * Returns the next string from the stream, assuming it was written using {@link StreamOutput#writeString(String)}.
	 * 
	 * @return the next {@code String} from the underlying stream or {@code null} if EOF has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public String readString() throws IOException
	{
		size = readShortUnsigned();

		if (size == -1)
			return null;

		if (stringBuffer.length < size)
			stringBuffer = new byte[size * 2];

		in.readFully(stringBuffer, 0, size);

		return new String(stringBuffer, 0, size, charset);
	}

	/**
	 * Reads the next two bytes from the underlying stream and interprets them as unsigned short. The result is returned as int. This method
	 * returns {@code -1} if EOF has been reached before reading both bytes.
	 * 
	 * @return the next two bytes from the underlying stream, interpreted as an unsigned short.
	 * @throws IOException
	 */
	public int readShortUnsigned() throws IOException
	{
		int ch1 = read();
		int ch2 = read();
		if ((ch1 | ch2) < 0)
			return -1; // EOF
		return (ch1 << 8) + (ch2 << 0);
	}

	@Override
	public int read() throws IOException
	{
		return in.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return in.read(b, off, len);
	}

	@Override
	public byte readByte() throws IOException
	{
		return in.readByte();
	}

	@Override
	public boolean readBoolean() throws IOException
	{
		return in.readBoolean();
	}

	@Override
	public short readShort() throws IOException
	{
		return in.readShort();
	}

	@Override
	public int readInt() throws IOException
	{
		return in.readInt();
	}

	@Override
	public long readLong() throws IOException
	{
		return in.readLong();
	}

	@Override
	public float readFloat() throws IOException
	{
		return in.readFloat();
	}

	@Override
	public double readDouble() throws IOException
	{
		return in.readDouble();
	}

	@Override
	public void close() throws IOException
	{
		in.close();
	}
}
