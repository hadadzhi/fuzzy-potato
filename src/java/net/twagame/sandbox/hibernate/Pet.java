package net.twagame.sandbox.hibernate;

import javax.persistence.Entity;

@Entity
public class Pet extends AbstractPersistentObject
{
	private String name;
	private int level;

	@Deprecated
	public Pet()
	{
	}

	public Pet(String name)
	{
		this.name = name;
	}

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
}
