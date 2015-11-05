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
package hibernate.postgres;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for HTS Data.
 */
@Transactional("postgres")
public class HtsDataDAO {

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
    public HtsDataEntity findOne( final HtsDataEntity.HtsDataEntityPK id ){
        return (HtsDataEntity) getCurrentSession().get( HtsDataEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< HtsDataEntity > findAll(){
        return getCurrentSession().createQuery( "from HtsDataEntity order by key.householdId" ).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final HtsDataEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final HtsDataEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final HtsDataEntity entity ){
        getCurrentSession().delete( entity );
    }


    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final HtsDataEntity.HtsDataEntityPK entityId ){
        final HtsDataEntity entity = findOne( entityId );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
