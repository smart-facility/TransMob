package hibernate.postgis;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data access object (DAO) providing persistence and search support for
 * Streets entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see StreetsGMARandwickAndSurroundingEntity
 * @author MyEclipse Persistence Tools
 */
@Transactional("postgis")
public class StreetsGMARandwickAndSurroundingDAO {
	private static final Logger log = LoggerFactory
			.getLogger(StreetsGMARandwickAndSurroundingDAO.class);

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


	public void save(StreetsGMARandwickAndSurroundingEntity transientInstance) {
		log.debug("saving StreetsGMARandwickAndSurroundingEntity instance");
		try {
			getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(StreetsGMARandwickAndSurroundingEntity persistentInstance) {
		log.debug("deleting StreetsGMARandwickAndSurroundingEntity instance");
		try {
            getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public StreetsGMARandwickAndSurroundingEntity findById(java.lang.Integer id) {
		log.debug("getting StreetsGMARandwickAndSurroundingEntity instance with id: "
				+ id);
		try {
			return (StreetsGMARandwickAndSurroundingEntity) getCurrentSession()
					.get("hibernate.postgis.StreetsGMARandwickAndSurroundingEntity",
							id);
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<StreetsGMARandwickAndSurroundingEntity> findAll() {
		log.debug("finding all StreetsGMARandwickAndSurroundingEntity instances");
		try {
			String queryString = "from StreetsGMARandwickAndSurroundingEntity order by gid";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public StreetsGMARandwickAndSurroundingEntity merge(
			StreetsGMARandwickAndSurroundingEntity detachedInstance) {
		log.debug("merging StreetsGMARandwickAndSurroundingEntity instance");
		try {
			StreetsGMARandwickAndSurroundingEntity result = (StreetsGMARandwickAndSurroundingEntity) getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(StreetsGMARandwickAndSurroundingEntity instance) {
		log.debug("attaching dirty StreetsGMARandwickAndSurroundingEntity instance");
		try {
            getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}