package net.twagame.sandbox.serial.testclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.twagame.serial.annotation.TWASerializable;

@SuppressWarnings({ "serial" })
@TWASerializable
public class TestClass extends SomeClass implements Serializable
{
	private Media media;
	private List<Image> images = new ArrayList<>();
	private List<Image> images2 = new ArrayList<>();
	private Collection<double[]> collectionOfDoubleArrays = new ArrayList<>();
	private double[][] doubledouble;

	public Media getMedia()
	{
		return media;
	}

	public void setMedia(Media media)
	{
		this.media = media;
	}

	public List<Image> getImages()
	{
		return images;
	}

	public void setImages(List<Image> images)
	{
		this.images = images;
	}

	public List<Image> getImages2()
	{
		return images2;
	}

	public void setImages2(List<Image> images2)
	{
		this.images2 = images2;
	}

	public Collection<double[]> getCollectionOfDoubleArrays()
	{
		return collectionOfDoubleArrays;
	}

	public void setCollectionOfDoubleArrays(Collection<double[]> collectionOfDoubleArrays)
	{
		this.collectionOfDoubleArrays = collectionOfDoubleArrays;
	}

	public double[][] getDoubledouble()
	{
		return doubledouble;
	}

	public void setDoubledouble(double[][] doubledouble)
	{
		this.doubledouble = doubledouble;
	}
}
