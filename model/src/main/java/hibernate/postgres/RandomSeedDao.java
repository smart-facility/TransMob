package hibernate.postgres;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for the random seed entity.
 */
@Transactional("postgres")
public class RandomSeedDao {

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
    public RandomSeedEntity findOne( final int id ){
        return (RandomSeedEntity) getCurrentSession().get( RandomSeedEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< DutyRateEntity > findAll(){
        return getCurrentSession().createQuery( "from RandomSeedEntity " ).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final RandomSeedEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final RandomSeedEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final RandomSeedEntity entity ){
        getCurrentSession().delete( entity );
    }


    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final RandomSeedEntity entity = findOne( entityId );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }}
