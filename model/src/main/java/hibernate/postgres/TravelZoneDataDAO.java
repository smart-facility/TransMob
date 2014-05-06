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
 * TravelZoneData entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see hibernate.postgres.TravelZoneDataEntity
 */

@Transactional("postgres")
public class TravelZoneDataDAO {

    private static final Logger log = LoggerFactory.getLogger(TravelZoneDataDAO.class);

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public void save(TravelZoneDataEntity transientInstance) {
		log.debug("saving TravelZoneDataEntity instance");
		try {
			getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(TravelZoneDataEntity persistentInstance) {
		log.debug("deleting TravelZoneDataEntity instance");
		try {
            getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TravelZoneDataEntity findById(Integer id) {
		log.debug("getting TravelZoneDataEntity instance with id: " + id);
		try {
			return (TravelZoneDataEntity) getCurrentSession().get(TravelZoneDataEntity.class, id);
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<TravelZoneDataEntity> findAll() {
		log.debug("finding all TravelZoneDataEntity instances");
		try {
			String queryString = "from TravelZoneDataEntity order by gid";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public TravelZoneDataEntity merge(TravelZoneDataEntity detachedInstance) {
		log.debug("merging TravelZoneDataEntity instance");
		try {
			TravelZoneDataEntity result = (TravelZoneDataEntity) getCurrentSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}


    private Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}