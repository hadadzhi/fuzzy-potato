package net.twagame.sandbox.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Item extends AbstractPersistentObject
{
	@Column
	private String name;

	@Deprecated
	protected Item()
	{
	}

	public Item(String name)
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

	@Override
	public String toString()
	{
		return name;
	}
}
