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
