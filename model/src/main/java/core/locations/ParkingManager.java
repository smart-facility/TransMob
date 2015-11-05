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
package core.locations;

import core.ApplicationContextHolder;
import core.HardcodedData;
import core.model.TextFileHandler;
import core.synthetic.individual.Individual;
import core.synthetic.travel.mode.TravelModes;
import core.synthetic.traveldiary.TravelDiaryColumns;
import hibernate.postgres.ParkingAccessDao;
import hibernate.postgres.ParkingAccessEntity;
import hibernate.postgres.ParkingCapacityDAO;
import hibernate.postgres.ParkingCapacityEntity;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * This class is responsible for managing and allocating parking spots and capacities.
 */
public final class ParkingManager {

    /**
     * Logger.
     */
    private static Logger logger = Logger.getLogger(ParkingManager.class);

    /**
     * The maximum distance between an activity location and a carpark.
     */
    private static final Double MAX_DISTANCE_TO_CARPARK = (double) 500;

    /**
     *  Reference to the parking access DAO.
     */
    private ParkingAccessDao parkingAccessDao;

    /**
     *  Reference to the parking capacity DAO.
     */
    private ParkingCapacityDAO parkingCapacityDao;

    /**
     * Map of parking capacities keyed by parking ID.
     */
    private final Map<Integer, Integer> parkingCapacityMap = new HashMap<>();

    /**
     * Map of travel diaries associated with a particular activity location. This is used to determine parking
     * availability for a given activity location at a given time. It is keyed on activity location id.
     */
    private Map<Integer, List<Individual>> travelDiariesByLocationId = new HashMap<>();

    /**
     * Singleton instance.
     */
    private static ParkingManager instance;

    /**
     * Default constructor.
     */
    private ParkingManager() {
        parkingAccessDao = ApplicationContextHolder.getBean(ParkingAccessDao.class);
        parkingCapacityDao = ApplicationContextHolder.getBean(ParkingCapacityDAO.class);

        setupParkingCapacity();
    }

    /**
     * Singleton accessor for the parking manager.
     * @return Reference to the parking manager.
     */
    public static synchronized ParkingManager getInstance() {
            if (instance == null) {
                instance = new ParkingManager();
            }

            return instance;
    }

    //=================================================================
    /**
     * Setup the parking capacities map.
     */
    private void setupParkingCapacity() {

        List<ParkingCapacityEntity> capacities = parkingCapacityDao.findAll();

        for (ParkingCapacityEntity capacity : capacities) {
            parkingCapacityMap.put(capacity.getParkingId(), capacity.getSpace());
        }
    }


    /**
     * Searching the closest parking location which still has free car space.
     *
     * @param availableActLocID The activity location ID for the activity where we want to park near.
     * @param travelDiary The travel diary for the individual.
     *
     * @return an array which has 2 value: activity location ID and its
     *         available parking location ID
     *
     */
    public int[] searchCarPark(int availableActLocID, int[] travelDiary) {

        List<ParkingAccessEntity> parkingSpotsForLocation = parkingAccessDao.findByFromId(availableActLocID);

        if (parkingSpotsForLocation.isEmpty()) {
            // There are no parking spots for this activity location.
            return new int[] { 0, 0 };
        }
        else {
            int parkingSpace = 0;
            // Sorted set of parking locations sorted by distance from the activity location
            Set<ParkingDistance> parkingByDistanceToActivity = new TreeSet<>();

            Double [] availableActCoords = ActivityLocationManager.getInstance().getHmActivityLocCordinators().get(availableActLocID);

				/* pick up the closest available parking place and get the car space */
            for (ParkingAccessEntity parkingSpot : parkingSpotsForLocation) {
                int availableLocationID = parkingSpot.getToId();

                // break when the parking location is out of study area
                Double [] parkingLocation = ActivityLocationManager.getInstance().getHmActivityLocCordinators().get(availableLocationID);
                if (parkingLocation == null) {
                    return new int[] { 0, 0 };
                }
                else {
                	// only get activity location that is not Home location
                    if (availableLocationID >= ActivityLocationManager.getInstance().getMinActivityLocID()
                            && (availableLocationID <= ActivityLocationManager.getInstance().getMaxActivityLocID())) {
                    	
                        try {
                            double distance = getDistance(
                                    availableActCoords[0],
                                    availableActCoords[1],
                                    parkingLocation[0],
                                    parkingLocation[1]);

                            if (distance <= MAX_DISTANCE_TO_CARPARK) {
                                // We only care about those spots that are within the MAX distance.
                                parkingByDistanceToActivity.add(new ParkingDistance(availableLocationID, distance));
                            }
                        } catch (Exception e) {
                            logger.debug("Failed to determine distance from parking location to the activity location.", e);
                        }
                    }
                }
            }

            // If there isn't any parking within the MAX distance, then return.
            if (parkingByDistanceToActivity.isEmpty()) {
                return new int[] { 0, 0 };
            }

            // calculates the total number of cars already at parking locations
            // associated with the same destination and at the end time of
            // the trip we're considering (denoted by crnTravDiaries[matrixRow])
            int carCount = 0;
            // for each of the individuals whose travel diary has been assigned with
            // locations.

            List<Individual> allocatedIndividuals = travelDiariesByLocationId.get(availableActLocID);

            if (allocatedIndividuals != null) {

                // assigning to a variable so we don't have to re-evaluate the expression inside of the loop.
                int driverModeConstant = TravelModes.CarDriver.getIntValue();

                for (Individual prevInd : allocatedIndividuals) {
                    int[][] tmpDiaries = prevInd.getTravelDiariesWeekdays();

                    if (tmpDiaries == null) {
                        continue;
                    }

                    // for each of the trips in this individual's travel diary
                    for (int itd = 0; itd < tmpDiaries.length; itd++) {
                        // if the mode of this trip is car driver
                        // AND the destination is identical to locationID
                        // AND end time of this trip is before our arrival time
                        // AND (this is the last trip OR start time of next trip that
                        // uses car departs after our arrival time)
                        if (tmpDiaries[itd][TravelDiaryColumns.TravelMode_Col.getIntValue()] == driverModeConstant
                                && tmpDiaries[itd][TravelDiaryColumns.Destination_Col.getIntValue()] == availableActLocID
                                && tmpDiaries[itd][TravelDiaryColumns.EndTime_Col.getIntValue()] <= travelDiary[TravelDiaryColumns.EndTime_Col.getIntValue()]) {
                            if (itd == tmpDiaries.length - 1) {
                                // AND this is the last trip
                                logger.trace("Incrementing car count due to this being the last trip for location id: " + availableActLocID + " count is now: " + carCount);
                                carCount++;
                            } else {
                                // OR start time of next trip that uses car departs
                                // after our arrival time
                                for (int jtd = itd + 1; jtd < tmpDiaries.length; jtd++) {
                                    if (tmpDiaries[jtd][TravelDiaryColumns.TravelMode_Col.getIntValue()] == driverModeConstant
                                            && tmpDiaries[jtd][TravelDiaryColumns.StartTime_Col.getIntValue()] > travelDiary[TravelDiaryColumns.EndTime_Col.getIntValue()]) {
                                        carCount++;
                                        logger.trace("Incrementing car count due to car overlapping our time: " + availableActLocID + " count is now: " + carCount);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                logger.trace("Nobody else is at location " + availableActLocID + " so plenty of parking available.");
            }

            ParkingDistance chosenParking = null;

            // for each of the parking associated with locationID, from closest to
            // farthest
            for (ParkingDistance parkingLocation : parkingByDistanceToActivity) {

                // get the capacity of this parking
                parkingSpace += parkingCapacityMap.get(parkingLocation.getParkingLocationId());

                // if the capacity of this parking is larger than the total number
                // of cars in all parking locations associated with locationID
                if (carCount < parkingSpace) {
                	// picks this car parking
                	chosenParking = parkingLocation; 
                    break;
                }
            }

            // if no car parking found or if the distance from this carpark to
            // locationID is greater than 500 metres.
            if (chosenParking == null) {
                logger.trace("Unable to go to activity location due to lack of parking. Activity Location was: " + availableActLocID);
                return new int[] { 0, 0 };
            } else {
                return new int[] { availableActLocID, chosenParking.getParkingLocationId() };
            }
        }
    }

    //===========================================================================
    /**
     * gets distance between 2 points.
     *
     * @param x1 X co-ordinate of the first location.
     * @param y1 Y co-ordinate of the first location.
     * @param x2 X co-ordinate of the second location.
     * @param y2 Y co-ordinate of the second location.
     * @return the distance between 2 locations with x, y coordinates.
     */
    private static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Get the data of parking access and put into database.
     * @deprecated
     */
    @Deprecated
    public void setParkingAccessToDB() {
        try {
            List sheetData = TextFileHandler.readCSV(HardcodedData.parkingAccessFileLocation);
            List<ParkingAccessEntity> entities = new ArrayList<>();
            ParkingAccessDao accessDao = ApplicationContextHolder
                    .getBean(ParkingAccessDao.class);

            // Clear the table
            accessDao.deleteAll();

            int accessIdDCol = 0;
            int accessFromIdCol = 1;
            int accessToIdCol = 2;
            int accessFromTypeCol = 3;
            int accessToTypeCol = 4;
            
			/* Iterates the data and put it into the database */
            for (int i = 1; i < sheetData.size(); i++) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Processing parking access file at line: " + i);
                }

                List list = (List) sheetData.get(i);

                ParkingAccessEntity entity = new ParkingAccessEntity();
                entity.setAccessId(Integer.parseInt(String.valueOf(list.get(accessIdDCol))));
                entity.setFromId(Integer.parseInt(String.valueOf(list.get(accessFromIdCol))));
                entity.setToId(Integer.parseInt(String.valueOf(list.get(accessToIdCol))));
                entity.setFromType(String.valueOf(list.get(accessFromTypeCol)));
                entity.setToType(String.valueOf(list.get(accessToTypeCol)));

                entities.add(entity);
            }

            accessDao.save(entities);

            logger.debug("Finish putting parking access into database");

        } catch (Exception e) {
            logger.error("Unable to push parking access data into DB", e);
        }

    }

    /**
     * Get the data of parking capacity and put into database.
     * @deprecated
     *
     */
    @Deprecated
    public void setParkingCapacityToDB() {

        try {
            List sheetData = TextFileHandler.readCSV(HardcodedData.parkingCapacityFileLocation);
            List<ParkingCapacityEntity> entities = new ArrayList<>();
            ParkingCapacityDAO capacityDao = ApplicationContextHolder
                    .getBean(ParkingCapacityDAO.class);

            // Clear the table
            capacityDao.deleteAll();

            int parkinIdDCol = 0;
            int xCoordinateCol = 1;
            int yCoordinateCol = 2;
            int spaceCol = 3;
            int noteCol = 4;
            
			/* Iterates the data and put it into the database */
            for (int i = 1; i < sheetData.size(); i++) {
                List list = (List) sheetData.get(i);

                ParkingCapacityEntity entity = new ParkingCapacityEntity();
                entity.setParkingId(Integer.parseInt(String.valueOf(list.get(parkinIdDCol))));
                entity.setxCoordinate(Double.parseDouble(String.valueOf(list.get(xCoordinateCol))));
                entity.setyCoordinate(Double.parseDouble(String.valueOf(list.get(yCoordinateCol))));
                entity.setSpace(Integer.parseInt(String.valueOf(list.get(spaceCol))));
                entity.setNote(String.valueOf(list.get(noteCol)));

                entities.add(entity);
            }

            capacityDao.save(entities);

            logger.debug("Finish putting parking capacity into database");

        } catch (Exception e) {
            logger.error("Failed to load parking capacities into the database.", e);
        }

    }

    /**
     * Clear the map of travel diaries allocated to a location.
     */
    public void resetParkingAllocationsForActivities() {
        travelDiariesByLocationId = new HashMap<>();
    }

    /**
     * Indicates that the individual will require parking for each of the destinations in their travel diary for which
     * they are the driver.
     * @param indiv The individual to allocate parking for.
     */
    public void allocateParkingForIndividual(Individual indiv) {
        int [][] travelDiary = indiv.getTravelDiariesWeekdays();

        if (travelDiary != null) {
            for (int [] trip : travelDiary) {

                if (trip[TravelDiaryColumns.TravelMode_Col.getIntValue()] == TravelModes.CarDriver.getIntValue()) {
                    int destinationId = trip[TravelDiaryColumns.Destination_Col.getIntValue()];
                    List<Individual> allocatedIndividuals = travelDiariesByLocationId.get(destinationId);

                    if (allocatedIndividuals == null) {
                        allocatedIndividuals = new ArrayList<>();
                    }

                    allocatedIndividuals.add(indiv);
                    travelDiariesByLocationId.put(destinationId, allocatedIndividuals);
                }
            }
        }
    }

    /**
     * Internal class representing the distance a parking spot has to the desired activity location.
     * This class is used so that we can easily sort available parking spots by distance to the activity.
     */
    private static final class ParkingDistance implements Comparable<ParkingDistance> {
        /** Parking location identifier. */
        private int parkingLocationId;

        /** Distance from the activity location. */
        private double distanceFromLocation;

        /**
         * Get the distance from the activity location for this parking spot.
         * @return Distance from the activity location.
         */
        private double getDistanceFromLocation() {
            return distanceFromLocation;
        }

        /**
         * Get the parking spot ID.
         * @return parking ID.
         */
        private int getParkingLocationId() {
            return parkingLocationId;
        }

        /**
         * Constructor.
         * @param parkingLocationId Parking spot ID.
         * @param distanceFromLocation Distance from the activity location.
         */
        private ParkingDistance(int parkingLocationId, double distanceFromLocation) {
            this.parkingLocationId = parkingLocationId;
            this.distanceFromLocation = distanceFromLocation;
        }


        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         * <p/>
         * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
         * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
         * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
         * <tt>y.compareTo(x)</tt> throws an exception.)
         * <p/>
         * <p>The implementor must also ensure that the relation is transitive:
         * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
         * <tt>x.compareTo(z)&gt;0</tt>.
         * <p/>
         * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
         * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
         * all <tt>z</tt>.
         * <p/>
         * <p>It is strongly recommended, but <i>not</i> strictly required that
         * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
         * class that implements the <tt>Comparable</tt> interface and violates
         * this condition should clearly indicate this fact.  The recommended
         * language is "Note: this class has a natural ordering that is
         * inconsistent with equals."
         * <p/>
         * <p>In the foregoing description, the notation
         * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
         * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
         * <tt>0</tt>, or <tt>1</tt> according to whether the value of
         * <i>expression</i> is negative, zero or positive.
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compareTo(ParkingDistance o) {
            if (o == null) {
                throw new NullPointerException();
            } else if (distanceFromLocation < o.getDistanceFromLocation()) {
                return -1;
            } else if (distanceFromLocation > o.getDistanceFromLocation()) {
                return 1;
            }

            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
            	return true;
            	}
            if (o == null || getClass() != o.getClass()) {
            	return false;
            }

            ParkingDistance that = (ParkingDistance) o;

            if (Double.compare(that.distanceFromLocation, distanceFromLocation) != 0) {
            	return false;
            }
            
            if (parkingLocationId != that.parkingLocationId) {
            	return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = parkingLocationId;
            temp = Double.doubleToLongBits(distanceFromLocation);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
}
