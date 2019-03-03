package net.twagame.sandbox.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriteriaTest
{
	private Session session;
	private static Logger log;

	public CriteriaTest() throws Exception
	{
		log = LoggerFactory.getLogger(CriteriaTest.class);

		Configuration hibernateConfiguration = new Configuration().configure();
		ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(hibernateConfiguration.getProperties()).buildServiceRegistry();
		session = hibernateConfiguration.buildSessionFactory(sr).openSession();
	}

	public static void main(String[] args)
	{
		try
		{
			new CriteriaTest().execute();
		}
		catch (Exception e)
		{
			log.error("Exception running test: ", e);
		}
	}

	private void execute() throws Exception
	{
//		Criteria cr = session.createCriteria(GameCharacter.class, "char").createAlias("char.pet", "pet", JoinType.RIGHT_OUTER_JOIN);

//		Criteria cr = session.createCriteria(Pet.class, "pet");

		//cr.add(Restrictions.like("char.name", "В%"));
//		cr.add(Restrictions.gt("pet.level", 1));

//		cr.add(Restrictions.sqlRestriction("{alias}.level > 1"));

//		cr.add(Subqueries.gt(Long.valueOf(2), DetachedCriteria.forClass(GameCharacter.class, "ichar")
//			.setProjection(Projections.rowCount())
//			.add(Restrictions.eqProperty("ichar.pet.id", "char.pet.id"))));

//		cr.setProjection(Projections.countDistinct("pet.id"));
//
//		List<?> result = cr.list();
//		System.out.println(result);

//		cr.setProjection(Projections.projectionList()
//			.add(Projections.groupProperty("pet.level"), "level")
//			.add(Projections.groupProperty("pet.name"), "name")
//			.add(Projections.property("char.name"), "ownerName")
//			.add(Projections.count("char.id"), "ownerCount"));

//		cr.setProjection(Projections.projectionList()
//			.add(Projections.property("pet.name"), "name")
//			.add(Projections.property("pet.level"), "level")
//			.add(Projections.sqlProjection("(select count(*) from GameCharacter where pet_id = {alias}.id) as ownerCount",
//				new String[] { "ownerCount" }, new Type[] { IntegerType.INSTANCE }), "ownerCount")
//			.add(
//				Projections.sqlProjection(
//					"concat('some shit ownerCount = ', (select count(*) from GameCharacter where pet_id = {alias}.id)) as ownerName",
//					new String[] { "ownerName" }, new Type[] { StringType.INSTANCE }), "ownerName"));
//
//		cr.setResultTransformer(Transformers.aliasToBean(PetRow.class));

//		Criteria rootCr = session.createCriteria(GameCharacter.class, "c");
//		Criteria cr = rootCr.createCriteria("c.pet", "p", JoinType.LEFT_OUTER_JOIN);
//
//		cr.add(Restrictions.like("c.name", "Ва%"));
//		cr.add(Restrictions.gt("p.level", 3));
//
//		cr.setProjection(Projections.projectionList()
//			.add(Projections.sqlProjection("concat({alias}.name, ' ', {alias}.level) as petNameLevel",
//				new String[] { "petNameLevel" },
//				new Type[] { StringType.INSTANCE }))
//			.add(Projections.sqlProjection(CriteriaQueryTranslator.ROOT_SQL_ALIAS + ".name as charName",
//				new String[] { "charName" },
//				new Type[] { StringType.INSTANCE })));
//

		Criteria cr = session.createCriteria(GameCharacter.class, "char");
		cr.createAlias("char.pet", "pet");
		cr.setProjection(Projections.property("char.name"));

//		DetachedCriteria charPetsIds = DetachedCriteria.forClass(GameCharacter.class, "c")
//			.createAlias("c.pets", "p")
//			.setProjection(Projections.property("p.id"))
//			.add(Restrictions.eqProperty("c.id", "char.id"));
//
//		cr.createAlias("char.pets", "pet");
//		cr.add(Subqueries.propertyIn("pet.id", charPetsIds));

//		Query q = session.getNamedQuery("petsForCharacterId").setParameter("charId", Long.valueOf(2));
//
		List<?> result = cr.list();
		System.out.println(result);
	}
}
