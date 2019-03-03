package net.twagame.sandbox.serial.testclass;

import java.io.Serializable;

import net.twagame.serial.annotation.TWASerializable;

@SuppressWarnings({ "serial" })
@TWASerializable
public class SomeClass implements Serializable
{
	private int someint;
	private short someshort;
	private byte somebyte;
	private long somelong;
	private boolean someboolean;
	private float somefloat;
	private double somedouble;
	private String somestring;

	public int getSomeint()
	{
		return someint;
	}

	public void setSomeint(int someint)
	{
		this.someint = someint;
	}

	public short getSomeshort()
	{
		return someshort;
	}

	public void setSomeshort(short someshort)
	{
		this.someshort = someshort;
	}

	public byte getSomebyte()
	{
		return somebyte;
	}

	public void setSomebyte(byte somebyte)
	{
		this.somebyte = somebyte;
	}

	public long getSomelong()
	{
		return somelong;
	}

	public void setSomelong(long somelong)
	{
		this.somelong = somelong;
	}

	public boolean isSomeboolean()
	{
		return someboolean;
	}

	public void setSomeboolean(boolean someboolean)
	{
		this.someboolean = someboolean;
	}

	public float getSomefloat()
	{
		return somefloat;
	}

	public void setSomefloat(float somefloat)
	{
		this.somefloat = somefloat;
	}

	public double getSomedouble()
	{
		return somedouble;
	}

	public void setSomedouble(double somedouble)
	{
		this.somedouble = somedouble;
	}

	public String getSomestring()
	{
		return somestring;
	}

	public void setSomestring(String somestring)
	{
		this.somestring = somestring;
	}
}
