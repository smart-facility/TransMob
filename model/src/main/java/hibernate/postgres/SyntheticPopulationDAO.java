package hibernate.postgres;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data access object (DAO) providing persistence and search support for
 * SyntheticPopulation entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see hibernate.postgres.SyntheticPopulationEntity
 * @author MyEclipse Persistence Tools
 */
@Transactional("postgres")
public class SyntheticPopulationDAO {

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	private static final Logger log = LoggerFactory.getLogger(SyntheticPopulationDAO.class);

	// property constants
	public static final String HOUSEHOLD_ID = "householdId";

	public void save(SyntheticPopulationEntity transientInstance) {

		log.debug("saving SyntheticPopulation instance");
		try {
            getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(SyntheticPopulationEntity persistentInstance) {
		log.debug("deleting SyntheticPopulation instance");
		try {
            getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public SyntheticPopulationEntity findById(java.lang.Integer id) {
		log.debug("getting SyntheticPopulation instance with id: " + id);
		try {
			return (SyntheticPopulationEntity) getCurrentSession().get(SyntheticPopulationEntity.class, id);
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	private List<SyntheticPopulationEntity> findByProperty(String propertyName, Object value) {
		log.debug("finding SyntheticPopulation instance with property: " + propertyName + ", value: " + value);
		try {
			String queryString = "from SyntheticPopulationEntity as model where model." + propertyName + "= ? order by id";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<SyntheticPopulationEntity> findByHouseholdId(Integer householdId) {
		return findByProperty(HOUSEHOLD_ID, householdId);
	}

	public List<SyntheticPopulationEntity> findAll() {
		log.debug("finding all SyntheticPopulation instances");
		try {
			return getCurrentSession().createQuery("from SyntheticPopulationEntity order by id").list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<SyntheticPopulationEntity> findFromFirst(int number) {
		log.debug("finding all SyntheticPopulation instances");
		try {
			Query queryObject = getCurrentSession().createQuery("from SyntheticPopulationEntity order by id ");
			queryObject.setFirstResult(0);
			queryObject.setMaxResults(number);

			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    /**
     * Returns household data from synthetic population. The query returns an array which includes the householdId (index 0),
     * the sum of the household income (index 1) and the number of bedrooms (index 2).
     * @return List of arrays containing aggregated household data.
     */
    public List<int []> findHouseholdData() {
    	List<int[]> result=new ArrayList<int[]>();
        String sql = "SELECT household_id,sum(income) as income, count(*)-sum(case when age < 10 then 1 else 0 end)-sum(case when household_relationship = 'Married' or household_relationship = 'Defacto' then 1 else 0 end)+ceil(cast(sum(case when age < 10 then 1 else 0 end) as real)/3)+ceil(cast(sum(case when household_relationship = 'Married' or household_relationship = 'Defacto' then 1 else 0 end) as real)/2) as parents_number FROM synthetic_population group by household_id order by household_id";
        List<Object[]> tempObjects=getCurrentSession().createSQLQuery(sql).list();
        for (Object[] objects : tempObjects) {
        	int[] tempIntArray=new int[3];
            tempIntArray[0] = (int) objects[0];
            tempIntArray[1] = ((BigInteger) objects[1]).intValue();
            tempIntArray[2] = ((Double) objects[2]).intValue();

			result.add(tempIntArray);
		}
        
        return result;
    }

	public SyntheticPopulationEntity merge(SyntheticPopulationEntity detachedInstance) {
		log.debug("merging SyntheticPopulation instance");
		try {
            SyntheticPopulationEntity result = (SyntheticPopulationEntity) getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}
	
	public void deleteAll() {
		log.debug("deleting all SyntheticPopulation instances");
		try {
			Query q = getCurrentSession().createQuery("delete from SyntheticPopulationEntity");
			q.executeUpdate();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

    private final Session getCurrentSession(){
        return this.sessionFactory.getCurrentSession();
    }
}