package hibernate.postgres;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for the ActivityHotspotsStreetBlockEntity entity.  Set the qualifier for the transaction manager that applies to
 * this DAO.
 */
@Transactional("postgres")
public class ActivityHotspotsStreetBlockDAO {

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
    public ActivityHotspotsStreetBlockEntity findOne( final int id ){
        return (ActivityHotspotsStreetBlockEntity) getCurrentSession().get( ActivityHotspotsStreetBlockEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< ActivityHotspotsStreetBlockEntity > findAll(){
        return getCurrentSession().createQuery( "from ActivityHotspotsStreetBlockEntity order by activityId").list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final ActivityHotspotsStreetBlockEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final ActivityHotspotsStreetBlockEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final ActivityHotspotsStreetBlockEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final ActivityHotspotsStreetBlockEntity entity = findOne( entityId );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

}
