package hibernate.postgres;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data access object (DAO) providing persistence and search support for
 * TransimsHouseholdLocation entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see hibernate.postgres.TransimsHouseholdLocationEntity
 * @author MyEclipse Persistence Tools
 */
@Transactional("postgres")
public class TransimsHouseholdLocationDAO {
    private static final int BATCH_SIZE = 50;
    /** session factory for the DAO. */
    private SessionFactory sessionFactory; 

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    private static final Logger log = LoggerFactory
			.getLogger(TransimsHouseholdLocationDAO.class);
	// property constants
	public static final String TRAVEL_ZONE_ID = "travelZoneId";

	public void save(TransimsHouseholdLocationEntity transientInstance) {
		log.debug("saving TransimsHouseholdLocation instance");
		try {
			getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

    /**
     * Persist a list of entities.
     * @param entities Entities to persist.
     */
    @Transactional
    public void save( final List<TransimsHouseholdLocationEntity> entities ){
        Session session = getCurrentSession();

        int index  = 0;

        for ( TransimsHouseholdLocationEntity entity : entities) {
            session.save(entity);
            if ( index % BATCH_SIZE == 0 ) { // same as the JDBC batch size
                //flush a batch of inserts and release memory:
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }


    public void delete(TransimsHouseholdLocationEntity persistentInstance) {
		log.debug("deleting TransimsHouseholdLocation instance");
		try {
			getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

    /**
     * Delete all entities in the table.
     */
    public void deleteAll() {
        Query query = getCurrentSession().createQuery("delete from TransimsHouseholdLocationEntity ");
        query.executeUpdate();
    }

	public TransimsHouseholdLocationEntity findById(java.lang.Integer id) {
		log.debug("getting TransimsHouseholdLocation instance with id: " + id);
		try {
            return (TransimsHouseholdLocationEntity) getCurrentSession()
					.get(TransimsHouseholdLocationEntity.class, id);
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	private List<TransimsHouseholdLocationEntity> findByProperty(String propertyName, Object value) {
		log.debug("finding TransimsHouseholdLocation instance with property: "
				+ propertyName + ", value: " + value
				+ "order by dwelling_index");
		try {
			String queryString = "from TransimsHouseholdLocationEntity as model where model." + propertyName + "= ? order by dwellingIndex";
			Query queryObject = getCurrentSession().createQuery(queryString).setCacheable(true);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public void clearTransimsHouseholdLocationBonding() {

		String hqlUpdate = "update TransimsHouseholdLocationEntity SET hholdIndex= -1, yearAvailable=0, bedroomsNumber=0";

		getCurrentSession().createQuery(hqlUpdate).executeUpdate();
	}

	public List<TransimsHouseholdLocationEntity> findByTravelZoneId(Integer travelZoneId) {
		return findByProperty(TRAVEL_ZONE_ID, travelZoneId);
	}

	public List<TransimsHouseholdLocationEntity> findAll() {
		log.debug("finding all TransimsHouseholdLocation instances");
		try {
			String queryString = "from TransimsHouseholdLocationEntity order by dwellingIndex";
			Query queryObject = getCurrentSession().createQuery(queryString).setCacheable(true);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public TransimsHouseholdLocationEntity merge(TransimsHouseholdLocationEntity detachedInstance) {
		log.debug("merging TransimsHouseholdLocation instance");
		try {
            TransimsHouseholdLocationEntity result = (TransimsHouseholdLocationEntity) getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(TransimsHouseholdLocationEntity instance) {
		log.debug("attaching dirty TransimsHouseholdLocation instance");
		try {
            getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

    public Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

}