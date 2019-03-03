package net.twagame.serial.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This class implements a {@link SerializationOutput} in which the data is written to some {@link OutputStream}.
 * 
 * @author hadadzhi
 * @see DataOutputStream
 */
public class StreamOutput implements SerializationOutput
{
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private final Charset charset;
	private final DataOutputStream out;

	/**
	 * Creates a new {@link StreamInput} that uses the {@link StreamInput#DEFAULT_CHARSET}.
	 * 
	 * @param istream
	 */
	public StreamOutput(OutputStream ostream)
	{
		this(ostream, DEFAULT_CHARSET);
	}

	/**
	 * Creates a new {@link StreamInput} that uses the specified charset.
	 * 
	 * @param istream
	 * @param charsetName
	 *            a {@#code String} containing the name of the charset to use.
	 */
	public StreamOutput(OutputStream ostream, String charsetName)
	{
		this(ostream, Charset.forName(charsetName));
	}

	/**
	 * Creates a new {@link StreamInput} that uses the specified charset.
	 * 
	 * @param istream
	 * @param charset
	 *            the {@link Charset} to use.
	 */
	public StreamOutput(OutputStream ostream, Charset charset)
	{
		out = new DataOutputStream(ostream);
		this.charset = charset;
	}

	/**
	 * Writes a {@code String} to the underlying output stream, prefixed by length as unsigned short. The string is encoded in the charset
	 * passed to the constructor of this {@link StreamOutput} object. If charset was not specified, the {@link StreamOutput#DEFAULT_CHARSET}
	 * is used.
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

		// The magic starts here
		// Write length as unsigned short
		out.write((byte) ((bytes.length >>> 8) & 0xFF));
		out.write((byte) ((bytes.length >>> 0) & 0xFF));
		// Write string as bytes
		out.write(bytes);
	}

	@Override
	public void write(int b) throws IOException
	{
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		out.write(b, off, len);
	}

	@Override
	public void writeByte(byte v) throws IOException
	{
		out.writeByte(v);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException
	{
		out.writeBoolean(v);
	}

	@Override
	public void writeShort(short v) throws IOException
	{
		out.writeShort(v);
	}

	@Override
	public void writeInt(int v) throws IOException
	{
		out.writeInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException
	{
		out.writeLong(v);
	}

	@Override
	public void writeFloat(float v) throws IOException
	{
		out.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) throws IOException
	{
		out.writeDouble(v);
	}

	@Override
	public void close() throws IOException
	{
		out.flush();
		out.close();
	}
}
