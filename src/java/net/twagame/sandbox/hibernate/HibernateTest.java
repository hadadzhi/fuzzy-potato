//package net.twagame.sandbox.hibernate;
//
//import java.util.Collection;
//import java.util.List;
//
//import org.hibernate.Session;
//import org.hibernate.cfg.Configuration;
//import org.hibernate.service.ServiceRegistry;
//import org.hibernate.service.ServiceRegistryBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///*
// * TODO: Read the Hibernate documentation about sessions, transactions, etc and write a DatabaseManager class for our server.
// * 
// * Basic tasks and concepts:
// * Sessions should be short-lived
// * Maximum one session per thread
// * Transactions should be even more short-lived, representing an atomic piece of work
// * Think of error handling
// * Think of concurrency: separate threads must either never try to modify the same persistent object concurrently, or use 
// * some control mechanism
// */
//public class HibernateTest
//{
//	private Session session;
//	private static Logger log;
//
//	public HibernateTest() throws Exception
//	{
//		log = LoggerFactory.getLogger(HibernateTest.class);
//
//		Configuration hibernateConfiguration = new Configuration().configure();
//		ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(hibernateConfiguration.getProperties()).buildServiceRegistry();
//		session = hibernateConfiguration.buildSessionFactory(sr).openSession();
//	}
//
//	public void execute()
//	{
//		long startMillis = 0;
//		if (log.isDebugEnabled())
//			startMillis = System.currentTimeMillis();
//
//		log.info("Creating items and characters");
//		GameCharacter char1 = new GameCharacter("GameCharacter1");
//		GameCharacter char2 = new GameCharacter("GameCharacter2");
//		Item item1 = new Item("Еда");
//		Item item2 = new Item("Еще еда");
//
//		log.info("Adding items to characters");
//		char1.getInventory().put(item1, 1);
//		char1.getInventory().put(item2, 3);
//		char2.getInventory().put(item1, 8);
//
//		log.info("Saving items and characters to the database");
//		save(item1, item2, char1, char2);
//
//		listCharacters();
//
//		String name = char2.getName();
//
//		log.info("Loading all characters with name {} from database", name);
//		Collection<GameCharacter> loadedCharacters = loadCharactersByName(name);
//
//		log.info("Loaded {} characters", loadedCharacters.size());
//
//		log.info("Adding 1 '{}' to the inventory of all loaded characters", item2);
//		for (GameCharacter c : loadedCharacters)
//			c.getInventory().put(item2, 1);
//
//		log.info("Saving changes to the database");
//		save(loadedCharacters);
//
//		listCharacters();
//
//		log.info("Deleting loaded characters from database");
//		delete(loadedCharacters);
//
//		listCharacters();
//
//		session.close();
//
//		if (log.isDebugEnabled())
//			log.debug("Finished in {} milliseconds", System.currentTimeMillis() - startMillis);
//	}
//
//	public void listCharacters()
//	{
//		String hql = "FROM GameCharacter";
//
//		@SuppressWarnings("unchecked")
//		Collection<GameCharacter> Characters = session.createQuery(hql).list();
//
//		log.info("List of characters:");
//		if (Characters.isEmpty())
//			log.info("<Empty>");
//		else
//			for (GameCharacter e : Characters)
//			{
//				log.info("Character: " + e.toString());
//			}
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<GameCharacter> loadCharactersByName(String name)
//	{
//		// TODO: Think of SQL-injection protection
//		String hql = String.format("FROM GameCharacter c WHERE c.name = '%s'", name);
//
//		return session.createQuery(hql).list();
//	}
//
//	public void save(Collection<? extends AbstractPersistentObject> objects)
//	{
//		save(objects.toArray(new AbstractPersistentObject[0]));
//	}
//
//	public void save(AbstractPersistentObject... objects)
//	{
//		session.beginTransaction();
//
//		for (AbstractPersistentObject object : objects)
//			session.saveOrUpdate(object);
//
//		session.getTransaction().commit();
//	}
//
//	public void delete(Collection<? extends AbstractPersistentObject> objects)
//	{
//		delete(objects.toArray(new AbstractPersistentObject[0]));
//	}
//
//	public void delete(AbstractPersistentObject... objects)
//	{
//		session.beginTransaction();
//
//		for (AbstractPersistentObject object : objects)
//			session.delete(object);
//
//		session.getTransaction().commit();
//	}
//
//	public static void main(String[] args)
//	{
//		try
//		{
//			new HibernateTest().execute();
//		}
//		catch (Exception e)
//		{
//			log.error("Error opening Hibernate session:", e);
//		}
//	}
//}
