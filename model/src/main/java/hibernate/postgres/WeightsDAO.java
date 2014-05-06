package hibernate.postgres;

import core.synthetic.survey.DemographicInfomation;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for weights.
 */
@Transactional("postgres")
public class WeightsDAO {
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
    public WeightsEntity findOne( final int id ){
        return (WeightsEntity) getCurrentSession().get( WeightsEntity.class, id );
    }

    /**
     * Find the Weights entity for the given demographic.
     * @param demographicInfomation  The demographic to retrieve the weights for.
     * @return Weights entity matching the given demographics.
     */
    public WeightsEntity findByDemographic(final DemographicInfomation demographicInfomation) {
        int gender = demographicInfomation.getGender().getIntValue();
        int age = demographicInfomation.getAge().getIntValue();
        int income = demographicInfomation.getSalary().getIntValue();

        Query query = getCurrentSession().createQuery("from WeightsEntity where age = ? and gender = ? and income = ?").setCacheable(true);
        query.setInteger(0, age);
        query.setInteger(1, gender);
        query.setInteger(2, income);
        return (WeightsEntity) query.uniqueResult();

    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< WeightsEntity > findAll(){
        return getCurrentSession().createQuery( "from WeightsEntity ").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final WeightsEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Update an entity.
     * @param entity Entity to update.
     */
    public void update( final WeightsEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final WeightsEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final WeightsEntity entity = findOne( entityId );
        delete( entity );
    }

    private Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
