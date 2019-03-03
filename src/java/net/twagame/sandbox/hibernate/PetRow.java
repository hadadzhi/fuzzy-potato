package net.twagame.sandbox.hibernate;

public class PetRow
{
	private String name;
	private int level;
	private long ownerCount;
	private String ownerName;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	@Override
	public String toString()
	{
		return name + " level: " + level + " owners: " + ownerCount + " ownerName: " + ownerName;
	}

	public long getOwnerCount()
	{
		return ownerCount;
	}

	public void setOwnerCount(long ownerCount)
	{
		this.ownerCount = ownerCount;
	}

	public String getOwnerName()
	{
		return ownerName;
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}
}
