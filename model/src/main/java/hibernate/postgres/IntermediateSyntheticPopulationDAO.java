package hibernate.postgres;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data access object (DAO) providing persistence and search support for
 * SyntheticPopulation entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see hibernate.postgres.IntermediateSyntheticPopulationEntity
 * @author MyEclipse Persistence Tools
 */

@Transactional("postgres")
public class IntermediateSyntheticPopulationDAO {
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
    public IntermediateSyntheticPopulationEntity findOne( final int id ){
        return (IntermediateSyntheticPopulationEntity) getCurrentSession().get( IntermediateSyntheticPopulationEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< IntermediateSyntheticPopulationEntity > findAll(){
        return getCurrentSession().createQuery( "from IntermediateSyntheticPopulationEntity " ).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final IntermediateSyntheticPopulationEntity entity ){
        getCurrentSession().persist(entity);
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final IntermediateSyntheticPopulationEntity entity ){
        getCurrentSession().merge(entity);
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final IntermediateSyntheticPopulationEntity entity ){
        getCurrentSession().delete(entity);
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final IntermediateSyntheticPopulationEntity entity = findOne( entityId );
        delete( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}