package hibernate.postgis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data access object (DAO) providing persistence and search support for
 * TravelZonesFacilitiesEntity entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see hibernate.postgis.TravelZonesFacilitiesEntity
 * @author MyEclipse Persistence Tools
 */

@Transactional("postgis")
public class TravelZonesFacilitiesDAO {
	private static final Logger log = LoggerFactory.getLogger(TravelZonesFacilitiesDAO.class);

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	public void save(TravelZonesFacilitiesEntity transientInstance) {
		log.debug("saving TravelZonesFacilitiesEntity instance");
		try {
			getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(TravelZonesFacilitiesEntity persistentInstance) {
		log.debug("deleting TravelZonesFacilitiesEntity instance");
		try {
			getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TravelZonesFacilitiesEntity findById(java.lang.Integer id) {
		log.debug("getting TravelZonesFacilitiesEntity instance with id: " + id);
		try {
            return (TravelZonesFacilitiesEntity) getCurrentSession().get(TravelZonesFacilitiesEntity.class, id);
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<TravelZonesFacilitiesEntity> findAll() {
		log.debug("finding all TravelZonesFacilitiesEntity instances");
		try {
			String queryString = "from TravelZonesFacilitiesEntity order by tz2006";
			Query queryObject = getCurrentSession().createQuery(queryString).setCacheable(true);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public TravelZonesFacilitiesEntity merge(TravelZonesFacilitiesEntity detachedInstance) {
		log.debug("merging TravelZonesFacilitiesEntity instance");
		try {
            TravelZonesFacilitiesEntity result = (TravelZonesFacilitiesEntity) getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(TravelZonesFacilitiesEntity instance) {
		log.debug("attaching dirty TravelZonesFacilitiesEntity instance");
		try {
            getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

    /**
     * Update a list of entities.
     * @param entities Entities to update.
     */
    @Transactional
    public void updateAll( final List<TravelZonesFacilitiesEntity> entities ){
        for (TravelZonesFacilitiesEntity entity : entities) {
            getCurrentSession().merge( entity );
        }
    }

	public Map<Integer, TravelZonesFacilitiesEntity> getByMap() {
		Map<Integer, TravelZonesFacilitiesEntity> travelMap = new HashMap<Integer, TravelZonesFacilitiesEntity>();

		for (TravelZonesFacilitiesEntity item : (ArrayList<TravelZonesFacilitiesEntity>) findAll()) {
			travelMap.put(item.getTz2006(), item);
		}

		return travelMap;
	}

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

    public void startTransaction() {
        getCurrentSession().beginTransaction();
    }

    public void endTransaction() {
        getCurrentSession().getTransaction().commit();
    }

}