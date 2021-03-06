/* This file is part of TransMob.

   TransMob is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   TransMob is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser Public License for more details.

   You should have received a copy of the GNU Lesser Public License
   along with TransMob.  If not, see <http://www.gnu.org/licenses/>.

*/
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
 * TravelZonesRandwickGSEntity entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see hibernate.postgis.TravelZonesRandwickGSEntity
 * @author MyEclipse Persistence Tools
 */
@Transactional("postgis")
public class TravelZonesRandwickGSDAO {
	private static final Logger log = LoggerFactory.getLogger(TravelZonesRandwickGSDAO.class);
    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	// property constants
	public static final String TZ06 = "tz06";


	public void save(TravelZonesRandwickGSEntity transientInstance) {
		log.debug("saving TravelZonesRandwickGSEntity instance");
		try {
            getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(TravelZonesRandwickGSEntity persistentInstance) {
		log.debug("deleting TravelZonesRandwickGSEntity instance");
		try {
            getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TravelZonesRandwickGSEntity findById(java.lang.Integer id) {
		log.debug("getting TravelZonesRandwickGSEntity instance with id: " + id);
		try {
			return (TravelZonesRandwickGSEntity) getCurrentSession().get(TravelZonesRandwickGSEntity.class, id);
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

    public List<TravelZonesRandwickGSEntity> findByTz06(Integer tz06) {
		log.debug("finding TravelZonesRandwickGSEntity instance with tz06: " + tz06);
		try {
			String queryString = "from TravelZonesRandwickGSEntity as model where model.tz06= ?";
			Query queryObject = getCurrentSession().createQuery(queryString).setCacheable(true);
			queryObject.setParameter(0, tz06);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}


	public List<TravelZonesRandwickGSEntity> findAll() {
		log.debug("finding all TravelZonesRandwickGSEntity instances");
		try {
			String queryString = "from TravelZonesRandwickGSEntity order by gid";
			Query queryObject = getCurrentSession().createQuery(queryString).setCacheable(true);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public TravelZonesRandwickGSEntity merge(TravelZonesRandwickGSEntity detachedInstance) {
		log.debug("merging TravelZonesRandwickGSEntity instance");
		try {
			TravelZonesRandwickGSEntity result = (TravelZonesRandwickGSEntity) getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(TravelZonesRandwickGSEntity instance) {
		log.debug("attaching dirty TravelZonesRandwickGSEntity instance");
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