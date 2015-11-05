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
package hibernate.configuration;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Flags DAO.
 */
@Transactional("configuration")
public class RunsDAO {

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
    public RunsEntity findOne( final Integer id ){
        return (RunsEntity) getCurrentSession().get( RunsEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< RunsEntity > findAll(){
       return getCurrentSession().createQuery( "from RunsEntity ").list();
    }
    
    /**
     * Retrieve all instances of the entity from run and configuration.
     * @return List of all instances of the entity from run and configuration.
     */
   		
    public List<Object[]> getAllfromRunsAndConfigurationEntity(){
   		Session session = this.sessionFactory.openSession();
   		String sqlArray = "select a.id, b.nameofscenario, a.seed, a.status, a.timeStart, a.timeFinished, a.hostname, a.nameforoutputdb " +
   				" from RunsEntity as a,  ConfigurationEntity as b" +
   				" where a.configurationId=b.id ";
   		return session.createQuery(sqlArray ).list();
    }
    
    /**
     * Retrieve all hostName from run
     * @return List of all hostName from run
     */
    public List<Integer> getListHostnamefromRun() {
   		return getCurrentSession().createQuery( "SELECT hostname FROM RunsEntity ").list();
	}
       
    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final RunsEntity entity ){
        getCurrentSession().persist( entity );
    }
    
    

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final RunsEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final RunsEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final Integer  entityId ){
        final RunsEntity entity = findOne( entityId );
        delete( entity );
    }


    /**
     * Update the finish time for a flags entry whose start time matches the one provided.
     * @param timeStart The start time for the flags entry to update.
     * @param timeFinished The finish time.
     */
    public void updateTimeFinished(Timestamp timeStart, Timestamp timeFinished) {

        Query query = getCurrentSession().createQuery("from RunsEntity where timeStart=?");
        query.setTimestamp(0, timeStart);
        RunsEntity flags = (RunsEntity) query.uniqueResult();

        if (flags != null) {
            flags.setTimeFinished(timeFinished);
            save(flags);
        }
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

}
