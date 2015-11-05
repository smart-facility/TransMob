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
package hibernate.postgres;

import core.FacilityType;
import core.locations.ActivityLocationManager;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for Transims Activity Locations.
 */
@Transactional("postgres")
public class TransimsActivityLocationDAO {

    private final int BATCH_SIZE = 50;

    /** session factory for the DAO. */
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieve an instance of the entity by the key.
     * @param facilityId Key of the entity.
     * @return Entity instance or null if the instance cannot be found.
     */
    public TransimsActivityLocationEntity findOne( final int facilityId ){
        return (TransimsActivityLocationEntity) getCurrentSession().get( TransimsActivityLocationEntity.class, facilityId );
    }

    /**
     * Retrieve all instances of the entity.
     * @return List of all instances of the entity.
     */
    public List< TransimsActivityLocationEntity > findAll(){
        return getCurrentSession().createQuery("from TransimsActivityLocationEntity order by facilityId").list();
    }


    public int findMinActivityLocationId() {
        Criteria criteria = getCurrentSession().createCriteria(TransimsActivityLocationEntity.class)
                .setProjection(Projections.min("activityLocation"));
        List resultList = criteria.list();
        return (Integer) resultList.get(0);

    }

    public int findMaxActivityLocationId() {
        Criteria criteria = getCurrentSession().createCriteria(TransimsActivityLocationEntity.class)
                .setProjection(Projections.max("activityLocation"));
        List resultList = criteria.list();
        return (Integer) resultList.get(0);

    }
    /**
     * Persist an entity.
     * @param entity Entity to persist.
     */
    public void save( final TransimsActivityLocationEntity entity ){
        getCurrentSession().persist( entity );
    }

    /**
     * Persist a list of entities.
     * @param entities Entities to persist.
     */
    @Transactional
    public void save( final List<TransimsActivityLocationEntity> entities ){
        Session session = getCurrentSession();

        int index  = 0;

        for (TransimsActivityLocationEntity entity : entities) {
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
    public void update( final TransimsActivityLocationEntity entity ){
        getCurrentSession().merge( entity );
    }

    /**
     * Delete an entity.
     * @param entity Entity to delete.
     */
    public void delete( final TransimsActivityLocationEntity entity ){
        getCurrentSession().delete( entity );
    }

    /**
     * Delete an entity by ID.
     * @param entityId The ID of the entity to delete.
     */
    public void deleteById( final int entityId ){
        final TransimsActivityLocationEntity entity = findOne( entityId );
        delete( entity );
    }

    /**
     * Delete all entities.
     */
    public void deleteAll() {
        Query query = getCurrentSession().createQuery("delete from TransimsActivityLocationEntity ");
        query.executeUpdate();
    }
    /**
     * Retrieve the data of activity locations in database and put it into a
     * String matrix for further usage
     *
     * @return a 2d String matrix of all activity locations with 6 columns as
     *         [facility_id][activity_ID][type][hospot_id][note_bus][note_train]
     *         [travel_zone] [note_entry]
     *
     */
    public int[][] getActivityLocation() {
        int row = 0;
        List<TransimsActivityLocationEntity> activityLocations = findAll();

        int[][] activityLocation = new int[activityLocations.size()][8];

        for (TransimsActivityLocationEntity transActivitylocation : activityLocations) {
            int colActLoc = 0;

            String locationType = transActivitylocation.getType();

            /* fill in matrix activityLocation */
            int activityLocID = transActivitylocation.getActivityLocation();
            int locationTypeID = FacilityType.getFacilityType(locationType).getValue();

            activityLocation[row][colActLoc++] = transActivitylocation.getFacilityId(); /* facility_id */
            activityLocation[row][colActLoc++] = activityLocID; /* activity_location */
            activityLocation[row][colActLoc++] = locationTypeID; /* type */
            activityLocation[row][colActLoc++] = transActivitylocation.getHotspotId(); /* hotspot_id */
            activityLocation[row][colActLoc++] = transActivitylocation.getNoteBus(); /* note_bus */
            activityLocation[row][colActLoc++] = transActivitylocation.getNoteTrain(); /* note_train */
            activityLocation[row][colActLoc++] = transActivitylocation.getTravelZone(); /* travel_zone */
            activityLocation[row][colActLoc++] = transActivitylocation.getNoteEntry(); /* note_entry */
            

            /* input into Hash Map */
            Double[] xy_coordinators = { transActivitylocation.getxCoord(), transActivitylocation.getyCoord() };

            ActivityLocationManager.getInstance().getHmActivityLocCordinators()
                    .put(activityLocID, xy_coordinators);

            row++;
        }

        return activityLocation;
    }

    private final Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
