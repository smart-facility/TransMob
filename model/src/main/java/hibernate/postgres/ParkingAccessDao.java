package hibernate.postgres;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Parking Access DAO.
 */
@Transactional("postgres")
public class ParkingAccessDao {

    private final int BATCH_SIZE = 50;

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
    public ParkingAccessEntity findOne( final int id ){
        return (ParkingAccessEntity) getCurrentSession().get( ParkingAccessEntity.class, id );
    }


    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< ParkingAccessEntity > findAll(){
        return getCurrentSession().createQuery( "from ParkingAccessEntity order by fromId").setCacheable(true).list();
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< ParkingAccessEntity > findByFromId(int fromId){
        Query query = getCurrentSession().createQuery( "from ParkingAccessEntity where fromId=?").setCacheable(true);
        query.setInteger(0, fromId);
        return query.list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final ParkingAccessEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Persist a list of entities.
     * @param entities Entities to persist.
     */
    @Transactional
    public void save( final List<ParkingAccessEntity> entities ){
        Session session = getCurrentSession();

        int index  = 0;

        for ( ParkingAccessEntity entity : entities) {
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
    public void update( final ParkingAccessEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final ParkingAccessEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final ParkingAccessEntity entity = findOne( entityId );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

    /**
     * Delete all entities in the table.
     */
    public void deleteAll() {
        Query query = getCurrentSession().createQuery("delete from ParkingAccessEntity ");
        query.executeUpdate();
    }
}
