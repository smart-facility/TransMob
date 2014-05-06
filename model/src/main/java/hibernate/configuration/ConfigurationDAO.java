package hibernate.configuration;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Configuration DAO.
 */
@Transactional("configuration")
public class ConfigurationDAO {

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
    public ConfigurationEntity findOne( final int id ){
        return (ConfigurationEntity) getCurrentSession().get( ConfigurationEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< ConfigurationEntity > findAll(){
        return getCurrentSession().createQuery( "from ConfigurationEntity ").list();
    }
    
    
    public List<String> getAllscenarioname(){
   		return getCurrentSession().createQuery( "select nameofscenario from ConfigurationEntity ").list();   		
    }
    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final ConfigurationEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final ConfigurationEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final ConfigurationEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final ConfigurationEntity entity = findOne( entityId );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

}
