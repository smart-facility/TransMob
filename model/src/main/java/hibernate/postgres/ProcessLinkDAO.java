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

import com.google.common.base.Preconditions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for ProcessLink entity.
 */
@Transactional("postgres")
public class ProcessLinkDAO {
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
    public ProcessLinkEntity findOne( final int id ){
        return (ProcessLinkEntity) getCurrentSession().get( ProcessLinkEntity.class, id );
    }


    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< ProcessLinkEntity > findAll(){
        return getCurrentSession().createQuery( "from ProcessLinkEntity order by id" ).list();
    }

    /**
     * Retrieve all the toIds of the entities.
     * @return List of all toIds.
     */
    public List<Integer> findAllToIds() {
        List<String> allToIDsAsString = getCurrentSession().createQuery("select toId from ProcessLinkEntity order by id").list();
        List<Integer> allToIds = new ArrayList<Integer>();

        for(String toIdString : allToIDsAsString) {
            allToIds.add(Integer.valueOf(toIdString));
        }

        return allToIds;
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final ProcessLinkEntity entity ){
        Preconditions.checkNotNull(entity);
        getCurrentSession().persist( entity );
    }

    /**
     * Persist a list of entity.
     * @param entities Entities to persist.
     */
    @Transactional
    public void saveAll( final List<ProcessLinkEntity> entities ){
        for (ProcessLinkEntity processLink : entities) {
            getCurrentSession().persist( processLink );
        }
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final ProcessLinkEntity entity ){
        Preconditions.checkNotNull( entity );
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final ProcessLinkEntity entity ){
        Preconditions.checkNotNull( entity );
        getCurrentSession().delete( entity );
    }

    public void deleteAll() {
        getCurrentSession().createQuery("delete from ProcessLinkEntity ").executeUpdate();
    }


    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final ProcessLinkEntity entity = findOne( entityId );
        Preconditions.checkState( entity != null );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

}
