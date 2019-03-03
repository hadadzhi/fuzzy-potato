package net.twagame.sandbox.hibernate;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NamedQuery;

@Entity
@NamedQuery(name = "petsForCharacterId", query = "select c.pets from GameCharacter c where c.id = :charId")
public class GameCharacter extends AbstractPersistentObject
{
	private String name;

	@ManyToMany(cascade = { CascadeType.ALL })
	private Collection<Pet> pets = new HashSet<Pet>();
	
	@ManyToOne
	private Pet pet;

//	@ElementCollection
//	@CollectionTable(joinColumns = { @JoinColumn(name = "character_id") })
//	@MapKeyJoinColumn(name = "item_id")
//	@Column(name = "quantity")
//	private Map<Item, Integer> inventory = new HashMap<Item, Integer>();

	@Deprecated
	protected GameCharacter()
	{
	}

	public GameCharacter(String name)
	{
		this.setName(name);
	}

//	@Override
//	public String toString()
//	{
//		return "Id: " + Long.toString(this.getId()) + ", Name: " + this.getName() + ", Inventory: " + this.inventory;
//	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Collection<Pet> getPets()
	{
		return pets;
	}

	public void setPets(Collection<Pet> pets)
	{
		this.pets = pets;
	}

	public Pet getPet()
	{
		return pet;
	}

	public void setPet(Pet pet)
	{
		this.pet = pet;
	}

//	public Map<Item, Integer> getInventory()
//	{
//		return inventory;
//	}
//
//	public void setInventory(Map<Item, Integer> inventory)
//	{
//		this.inventory = inventory;
//	}
}
