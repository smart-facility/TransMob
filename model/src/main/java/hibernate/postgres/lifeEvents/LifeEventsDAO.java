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
package hibernate.postgres.lifeEvents;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for the life events tables.
 */
@Transactional("postgres")
public class LifeEventsDAO {

    private final int BATCH_SIZE = 50;

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public BirthProbabilityEntity findBirthProbability( final int age ){
        return (BirthProbabilityEntity) getCurrentSession().get( BirthProbabilityEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< BirthProbabilityEntity > findAllBirthProbabilities(){
        return getCurrentSession().createQuery( "from BirthProbabilityEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final BirthProbabilityEntity entity ){
        getCurrentSession().persist( entity );
    }


    /**
     * Persist a list of entities.
     * @param entities Entities to persist.
     */
    @Transactional
    public void saveBirths( final List<BirthProbabilityEntity> entities ){
        Session session = getCurrentSession();

        int index  = 0;

        for ( BirthProbabilityEntity entity : entities) {
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
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public DivorceProbabilityMaleEntity findMaleDivorceProbability( final int age ){
        return (DivorceProbabilityMaleEntity) getCurrentSession().get( DivorceProbabilityMaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< DivorceProbabilityMaleEntity > findAllMaleDivorceProbabilities(){
        return getCurrentSession().createQuery( "from DivorceProbabilityMaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final DivorceProbabilityMaleEntity entity ){
        getCurrentSession().persist( entity );
    }


    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public DivorceProbabilityFemaleEntity findFemaleDivorceProbability( final int age ){
        return (DivorceProbabilityFemaleEntity) getCurrentSession().get( DivorceProbabilityFemaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< DivorceProbabilityFemaleEntity > findAllFemaleDivorceProbabilities(){
        return getCurrentSession().createQuery( "from DivorceProbabilityFemaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final DivorceProbabilityFemaleEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public DeathProbabilityMaleEntity findMaleDeathProbability( final int age ){
        return (DeathProbabilityMaleEntity) getCurrentSession().get( DeathProbabilityMaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< DeathProbabilityMaleEntity > findAllMaleDeathProbabilities(){
        return getCurrentSession().createQuery( "from DeathProbabilityMaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final DeathProbabilityMaleEntity entity ){
        getCurrentSession().persist( entity );
    }


    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public DeathProbabilityFemaleEntity findFemaleDeathProbability( final int age ){
        return (DeathProbabilityFemaleEntity) getCurrentSession().get( DeathProbabilityFemaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< DeathProbabilityFemaleEntity > findAllFemaleDeathProbabilities(){
        return getCurrentSession().createQuery( "from DeathProbabilityFemaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public JobGainProbabilityMaleEntity findMaleJobGainProbability( final int age ){
        return (JobGainProbabilityMaleEntity) getCurrentSession().get( JobGainProbabilityMaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< JobGainProbabilityMaleEntity > findAllMaleJobGainProbabilities(){
        return getCurrentSession().createQuery( "from JobGainProbabilityMaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final JobGainProbabilityMaleEntity entity ){
        getCurrentSession().persist( entity );
    }


    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final JobGainProbabilityFemaleEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public JobGainProbabilityFemaleEntity findFemaleJobGainProbability( final int age ){
        return (JobGainProbabilityFemaleEntity) getCurrentSession().get( JobGainProbabilityFemaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< JobGainProbabilityFemaleEntity > findAllFemaleJobGainProbabilities(){
        return getCurrentSession().createQuery( "from JobGainProbabilityFemaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public JobLossProbabilityMaleEntity findMaleJobLossProbability( final int age ){
        return (JobLossProbabilityMaleEntity) getCurrentSession().get( JobLossProbabilityMaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< JobLossProbabilityMaleEntity > findAllMaleJobLossProbabilities(){
        return getCurrentSession().createQuery( "from JobLossProbabilityMaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final JobLossProbabilityMaleEntity entity ){
        getCurrentSession().persist( entity );
    }


    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public JobLossProbabilityFemaleEntity findFemaleJobLossProbability( final int age ){
        return (JobLossProbabilityFemaleEntity) getCurrentSession().get( JobLossProbabilityFemaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< JobLossProbabilityFemaleEntity > findAllFemaleJobLossProbabilities(){
        return getCurrentSession().createQuery( "from JobLossProbabilityFemaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final JobLossProbabilityFemaleEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public MarriageProbabilityMaleEntity findMaleMarriageProbability( final int age ){
        return (MarriageProbabilityMaleEntity) getCurrentSession().get( MarriageProbabilityMaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< MarriageProbabilityMaleEntity > findAllMaleMarriageProbabilities(){
        return getCurrentSession().createQuery( "from MarriageProbabilityMaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final MarriageProbabilityMaleEntity entity ){
        getCurrentSession().persist( entity );
    }


    /**
     * Retrieve an instance of the entity by the key.
     * @param age Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public MarriageProbabilityFemaleEntity findFemaleMarriageProbability( final int age ){
        return (MarriageProbabilityFemaleEntity) getCurrentSession().get( MarriageProbabilityFemaleEntity.class, age );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< MarriageProbabilityFemaleEntity > findAllFemaleMarriageProbabilities(){
        return getCurrentSession().createQuery( "from MarriageProbabilityFemaleEntity order by age").setCacheable(true).list();
    }

    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final MarriageProbabilityFemaleEntity entity ){
        getCurrentSession().persist( entity );
    }



    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final DeathProbabilityFemaleEntity entity ){
        getCurrentSession().persist( entity );
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
