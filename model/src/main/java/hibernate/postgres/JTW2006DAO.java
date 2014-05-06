package hibernate.postgres;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for 6JTW2006_OriginOutside_DestinationInsideStudyArea.
 */
@Transactional("postgres")
public class JTW2006DAO {
	private final int BATCH_SIZE = 50;

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param ID Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public JTW2006Entity findOne( final int ID ){
        return (JTW2006Entity) getCurrentSession().get( JTW2006Entity.class, ID );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List<JTW2006Entity> findAll(){
        return getCurrentSession().createQuery("from JTW2006Entity order by ID").list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final JTW2006Entity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Persist a list of entities.
     * @param entities Entities to persist.
     */
    @Transactional
    public void save( final List<JTW2006Entity> entities ){
        Session session = getCurrentSession();

        int index  = 0;

        for (JTW2006Entity entity : entities) {
            session.save(entity);
            if ( index % BATCH_SIZE == 0 ) { // same as the JDBC batch size
                //flush a batch of inserts and release memory:
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final JTW2006Entity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final JTW2006Entity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final JTW2006Entity entity = findOne( entityId );
        delete( entity );
    }

    /**
     * Delete all entities.
     */
    public void deleteAll() {
        Query query = getCurrentSession().createQuery("delete from JTW2006Entity ");
        query.executeUpdate();
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}

