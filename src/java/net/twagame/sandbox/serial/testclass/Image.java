package net.twagame.sandbox.serial.testclass;

import java.io.Serializable;

import net.twagame.serial.annotation.TWASerializable;

@SuppressWarnings({ "serial" })
@TWASerializable
public class Image implements Serializable
{
	private String uri = "http://javaone.com/keynote_large.jpg";
	private String title = "Javaone Keynote";
	private int width = 1024;
	private int height = 768;
	private boolean large = true;

	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public boolean isLarge()
	{
		return large;
	}

	public void setLarge(boolean large)
	{
		this.large = large;
	}
}
