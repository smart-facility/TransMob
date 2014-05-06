package hibernate.postgres;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;


/**
 * Flags DAO.
 */
@Transactional("configuration")
public class NewTendbyBerdrDAO {

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
    public NewTendbyBerdrEntity findOne( final Integer id ){
        return (NewTendbyBerdrEntity) getCurrentSession().get( NewTendbyBerdrEntity.class, id );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< NewTendbyBerdrEntity > findAll(){
       return getCurrentSession().createQuery( "from NewTendbyBerdrEntity ").list();
    }
   		


       
    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final NewTendbyBerdrEntity entity ){
        getCurrentSession().persist( entity );
    }
    
    

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final NewTendbyBerdrEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final NewTendbyBerdrEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final Integer  entityId ){
        final NewTendbyBerdrEntity entity = findOne( entityId );
        delete( entity );
    }



    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

}
