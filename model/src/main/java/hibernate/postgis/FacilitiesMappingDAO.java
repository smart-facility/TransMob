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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Facilities Mapping DAO.
 */
@Transactional("postgis")
public class FacilitiesMappingDAO {

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param id Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public FacilitiesMappingEntity findOne( final int id ){
        return (FacilitiesMappingEntity) getCurrentSession().get( FacilitiesMappingEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< FacilitiesMappingEntity > findAll(){
        return getCurrentSession().createQuery( "from FacilitiesMappingEntity order by id").list();
    }


    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final FacilitiesMappingEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final FacilitiesMappingEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final FacilitiesMappingEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final FacilitiesMappingEntity entity = findOne( entityId );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
