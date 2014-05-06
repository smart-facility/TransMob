package core.locations;

import core.ApplicationContextHolder;
import core.ArrayHandler;
import core.HardcodedData;
import core.ModelMain;
import core.model.TextFileHandler;
import core.synthetic.HouseholdPool;
import core.synthetic.IndividualPool;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.travel.mode.TravelModes;
import core.synthetic.traveldiary.ActivityLocationColumns;
import core.synthetic.traveldiary.TravelDiaryColumns;
import hibernate.postgres.TransimsActivityLocationDAO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  This class is responsible for managing and allocating activity Locations.
 */
public final class ActivityLocationManager {

    /** Logger. */
    private static final Logger logger = Logger.getLogger(ActivityLocationManager.class);

    /** Tracing logger. */
    private static final Logger traceLogger = Logger.getLogger("SetLocation");

    /** Singleton reference. */
    private static ActivityLocationManager instance;

    /** The minimum activity location ID in the database. */
    private final int minActivityLocID;

    /** The maximum activity location ID in the database. */
    private final int maxActivityLocID;

    /** hmActLocIDTravelZone : <ActivityLocationID,TravelZoneCode>. */
    private Map<Integer, Integer> hmActLocIDTravelZone = new HashMap<>();

    /** hmTravelZoneActLocID : <TravelZoneCode,[ActivityLocationID]>. */
    private  Map<Integer, int[]> hmTravelZoneActLocID = new HashMap<>();

    /** hmActivityLocCordinators : <activity_location_ID,[x_coordinate, y_coordinate]>. */
    private final Map<Integer, Double[]> hmActivityLocCordinators = new HashMap<>();

    /** Activity Locations DAO. */
    private TransimsActivityLocationDAO transimsActivityLocationDAO;

    /**
     * array of activity locations. a 2d String matrix of all activity locations with 6 columns as
     *         [facility_id][activity_ID][type][hospot_id][note_bus][note_train]
     *         [travel_zone] [note_entry]
     */
    private int[][] activityLocation;

    /**
     * Return the singleton instance.
     * @return Singleton instance of the ActivityLocationManager.
     */
    public static synchronized ActivityLocationManager getInstance() {
        if (instance == null) {
            instance = new ActivityLocationManager();
        }

        return instance;
    }

    /**
     * Private constructor.
     */
    private ActivityLocationManager() {
        transimsActivityLocationDAO = ApplicationContextHolder.getBean(TransimsActivityLocationDAO.class);
        minActivityLocID = transimsActivityLocationDAO.findMinActivityLocationId();
        maxActivityLocID = transimsActivityLocationDAO.findMaxActivityLocationId();
    }

    /**
     * Getter for the activity locations array.
     * @return a 2d String matrix of all activity locations with 6 columns as
     *         [facility_id][activity_ID][type][hospot_id][note_bus][note_train]
     *         [travel_zone] [note_entry]
     */
    public int[][] getActivityLocation() {
        return activityLocation;
    }

    /**
     * Retrieve the minimum activity location ID from the database.
     * @return Minimum activity location ID
     */
    public int getMinActivityLocID() {
        return minActivityLocID;
    }

    /**
     * Retrieve the maximum activity location ID from the database.
     * @return Maximum activity location ID
     */
    public int getMaxActivityLocID() {
        return maxActivityLocID;
    }

    /**
     * Retrieve the activity location co-ordinates.
     * @return Map of <activity_location_ID,[x_coordinate, y_coordinate]>
    */
    public Map<Integer, Double[]> getHmActivityLocCordinators() {
        return this.hmActivityLocCordinators;
    }

    /**
     * Create the 2 HashMap of Activity Location ID and Travel Zone Code based on
     * the contents of the Activity Location Tz file.
     *
     * @author vlcao
     */
    public void createMapActLocIDTz06() {
        this.activityLocation = transimsActivityLocationDAO.getActivityLocation();


        Map<Integer, ArrayList<Integer>> hmTzTemp = new HashMap<>();

        // fill in the Hash map
        for (int i = 0; i < getActivityLocation().length; i++) {
            int[] eachActivityLocation = getActivityLocation()[i];

            hmActLocIDTravelZone.put(eachActivityLocation[ActivityLocationColumns.ActivityID_Col.getIntValue()],
                    eachActivityLocation[ActivityLocationColumns.TravelZone_Col.getIntValue()]);

            ArrayList<Integer> tempArrayList = hmTzTemp
                    .get(eachActivityLocation[ActivityLocationColumns.TravelZone_Col.getIntValue()]);
            if (tempArrayList == null) {
                tempArrayList = new ArrayList<>();
                tempArrayList.add(eachActivityLocation[ActivityLocationColumns.ActivityID_Col.getIntValue()]);
                hmTzTemp.put(eachActivityLocation[ActivityLocationColumns.TravelZone_Col.getIntValue()], tempArrayList);
            } else {
                tempArrayList.add(eachActivityLocation[ActivityLocationColumns.ActivityID_Col.getIntValue()]);
                hmTzTemp.put(eachActivityLocation[ActivityLocationColumns.TravelZone_Col.getIntValue()], tempArrayList);
            }
        }

        /* CONVERT MAP WITH ARRAYLIST TO ARRAY ONLY */
        for (int travelZone : HardcodedData.travelZones) {
            int[] initialArr = {-1};
            hmTravelZoneActLocID.put(travelZone, initialArr);
        }

        // Display elements
        for (Map.Entry<Integer, ArrayList<Integer>> integerArrayListEntry : hmTzTemp.entrySet()) {
            int tzCode = integerArrayListEntry.getKey();
            ArrayList<Integer> actLocIDArrayList = integerArrayListEntry.getValue();

            int[] actLocIDArr = ArrayUtils.toPrimitive(actLocIDArrayList.toArray(new Integer[actLocIDArrayList.size()]));

            // put the activity locations associated with travel
            // zones in study area (64 travel zones)
            if (hmTravelZoneActLocID.get(tzCode) != null || tzCode == -1) {
                hmTravelZoneActLocID.put(tzCode, actLocIDArr);
            }

        }
    }

    /***
     * Retrieve the x,y coordinators for an activity location ID depending on
     * type of the activity location (home location or facility location).
     *
     * @param activityLocationID
     * @param dwellingAllocator
     * @param household
     * @return a double array of x,y coordinators
     *
     * @author vlcao
     */
    private double[] getCoorOfActLocID(int activityLocationID,
            DwellingAllocator dwellingAllocator, Household household) {
        double[] xyCoordinators = new double[2];

        if (activityLocationID < getMinActivityLocID()
                || (activityLocationID > getMaxActivityLocID())) {

            xyCoordinators[0] = dwellingAllocator.getDwellingPool()
                    .getDwellings().get(household.getDwellingId()).getxCoord();
            xyCoordinators[1] = dwellingAllocator.getDwellingPool()
                    .getDwellings().get(household.getDwellingId()).getyCoord();

        } else {
            try {
                xyCoordinators[0] = getHmActivityLocCordinators().get(activityLocationID)[0];
                xyCoordinators[1] = getHmActivityLocCordinators().get(activityLocationID)[1];
            } catch (Exception e) {
                logger.error("Exception caught", e);

                logger.error(getHmActivityLocCordinators().size()
                        + " : "
                        + String.valueOf(getHmActivityLocCordinators().keySet()));

                logger.error(activityLocationID);
            }
        }

        return xyCoordinators;
    }

    /**
     * Change the current Travel mode to Public Transport Mode (Bus, Train, Taxi, Walk).
     *
     *
     * @author Vu Lam Cao
     */
    private int[] changeToPublicTransport() {
        int[] vehicelIDTravelModeSet = new int[2];

        vehicelIDTravelModeSet[0] = 0; /* vehicelID */
        vehicelIDTravelModeSet[1] = HardcodedData.random.nextInt(2) == 0 ? 3
                : 4; /* travelMode */
        if (vehicelIDTravelModeSet[1] == 4
                && ModelMain.getInstance().getScenarioName().equals("base")) {
            vehicelIDTravelModeSet[1] = 3;

            // when the scenario is "base", individual will chose bus
            // instead of light rail
            if (vehicelIDTravelModeSet[1] == 4
                    && ModelMain.getInstance().getScenarioName()
                            .equalsIgnoreCase("base")) {
                /* change Travel Mode to BUS */
                vehicelIDTravelModeSet[1] = 3; /* travelMode */
            }
        }

        return vehicelIDTravelModeSet;
    }

    private int getActivityLocationBasedOnJTW(int originTZ, int currentMode) {
        int actLoc = -1;

        int destTZ = getDestinationTZFromJTW(originTZ, currentMode);
        if (!hmTravelZoneActLocID.containsKey(destTZ)) {
            return actLoc;
        }

        int[] actLocsInDestTZ = hmTravelZoneActLocID.get(destTZ);
        if (actLocsInDestTZ==null) {
            return actLoc;
        }

        int randInt = HardcodedData.random.nextInt(actLocsInDestTZ.length);

        actLoc = actLocsInDestTZ[randInt];

        return actLoc;
    }

    /**
    *
     * @param originTZ
    * @param currentMode
    * @return
    */
    private int getDestinationTZFromJTW(int originTZ, int currentMode) {
        int destTZ = 0;

        // gets index of originTZ in HardcodedData.travelZones
        int itzO = -1;
        for (int i=0; i<=HardcodedData.travelZones.length-1; i++) {
            if (HardcodedData.travelZones[i]==originTZ) {
                itzO = i;
                break;
            }
        }
        if (itzO==-1) {
            return destTZ;
        }

        // gets index of currentMode in JTW data
        int jtwMode = -1;
        if (currentMode==1 || currentMode==7 || currentMode==12) {
            jtwMode = 0;
        } else if (currentMode==4) {
            jtwMode = 1;
        } else if (currentMode==3) {
            jtwMode = 2;
        } else if (currentMode==2) {
            jtwMode = 3;
        } else if (currentMode==11) {
            jtwMode = 4;
        }

        if (jtwMode==-1) {
            return destTZ;
        }

        // proportions of the work trip counts by mode by destination travel zone departing from the originTZ in JTW data.
        double[][] jtsData = normaliseJTW(originTZ);
        if (jtsData==null) {
            return destTZ;
        }

        // picks the row corresponding to currentMode
        double[] cummulativeProb = jtsData[jtwMode];

        for (int i=1; i<=cummulativeProb.length-1; i++) {
            cummulativeProb[i] = cummulativeProb[i] + cummulativeProb[i-1];
        }

        double randDouble = HardcodedData.random.nextDouble();

        for (int i=0; i<=cummulativeProb.length-2; i++) {
            if (randDouble<=cummulativeProb[i]) {
                destTZ = HardcodedData.travelZones[i];
                return destTZ;
            }
        }

        destTZ = -1;

        return destTZ;
    }

    /**
    * normalises the work trip counts by mode by destination travel zone departing from the originTZ in JTW data.
     * Raw JTW data is stored in HardcodedData.jtw_OD_Tz06, which is a 2d integer array.
     * Notes on the structure of HardcodedData.jtw_OD_Tz06:
     * - the columns are destination travel zones in the order as specified in HardcodedData.travelZones.
     * - the last column represents all travel zones outside the study area.
     * - rows are grouped into blocks of 5, which is the number of valid modes in JTW data.
     * These JTW modes are
     * 0 Other, comprising modes Walk (1), Bike (7), Taxi (12) in the travel diary of each individual
     * 1 Tram, equivalent to mode Lightrail (4) in the current travel diary of each individual
     * 2 Bus, equivalent to mode Bus (3) in the current travel diary of each individual
     * 3 Car driver, equivalent to mode Car Driver (2) in the current travel diary of each individual
     * 4 Car Passenger, equivalent to mode Car Passenger (11) in the current travel diaries
     * - Each block represents an origin travel zone. These blocks are in the same order of the destination travel zones.
     * - The last block represents all travel zones outside the study area.
    *
     * @param originTZ the number of the origin TZ (eg 520)
     * @return 2d array of the normalised trip counts from originTZ.
    * Each row in this 2d array contains the proportion of trips to each of the destination travel zones by a travel mode.
    * There are 5 travel modes (and thus 5 rows): [0] Walk/Bike/Taxi, [1] Tram/Lightrail, [2] Bus, [3] Car driver, [4] Car passenger.
    * The sum of values in each row is 1.
     *
     */
    private double[][] normaliseJTW(int originTZ) {
          int nModesJTW = 5; // this is the number of valid modes from JTW data
          int[][] tripsModeTZ = HardcodedData.getJtwODTz06();

          double[][] normalisedJTW = null;

          // gets index of originTZ in HardcodedData.travelZones
          int itzO = -1;
          for (int i=0; i<=HardcodedData.travelZones.length-1; i++) {
                if (HardcodedData.travelZones[i]==originTZ) {
                      itzO = i;
                      break;
                }
          }

          if (itzO==-1) {
              return normalisedJTW;
          }

          int tmpfirstRow = itzO * nModesJTW;
          int tmplastRow = tmpfirstRow + nModesJTW - 1;
          int[][] jtwCountsThisTZ = new int[nModesJTW][tripsModeTZ[0].length];
          System.arraycopy(tripsModeTZ, tmpfirstRow, jtwCountsThisTZ, 0, tmplastRow + 1 - tmpfirstRow);


          normalisedJTW = new double[nModesJTW][tripsModeTZ[0].length];
          for (int iRow=0; iRow<=normalisedJTW.length-1; iRow++) {
                int tmpSum = (new ArrayHandler()).sumPositiveInArray(jtwCountsThisTZ[iRow]);
                for (int iCol=0; iCol<=normalisedJTW[iRow].length-1; iCol++) {
                      normalisedJTW[iRow][iCol] = (double)jtwCountsThisTZ[iRow][iCol]/(double)tmpSum;
                }
          }

          return normalisedJTW;
    }

    /**
     * Find location ID of origin and destination in each trip for whole Travel Diaries.
     *
     * Travel Diaries' columns are 0 [travel_id] 1 [individual_id] 2
     * [household_id] 3 [age] 4 [gender] 5 [income] 6 [origin] 7
     * [destination] 8 [start_time] 9 [end_time] 10 [duration] 11
     * [travel_mode] 12 [purpose] 13 [vehicle_id] 14 [trip_id]
     * 
     * @param dwellingAllocator
     *
     * @author vlcao
     */
    public void setLocation(DwellingAllocator dwellingAllocator, HouseholdPool householdPool) {

        traceLogger.trace("Into set location");
        logger.info("Total household: " + householdPool.getPoolNumber());
        
        // Reset the parking manager
        ParkingManager.getInstance().resetParkingAllocationsForActivities();

        int countHhold = 0;

        for (Household household : householdPool.getHouseholds().values()) {

            if (traceLogger.isTraceEnabled()) 
                traceLogger.trace("Set location for household: " + household.toString());
            
            countHhold++;
            if (countHhold == 1 || countHhold == 100 || countHhold == 1000 || countHhold == 10000 || countHhold % 10000 == 0) 
                logger.info("...set location for household " + countHhold);
            
            if (!household.isTravelDiariesChanged()) {
                for (Individual indiv : household.getResidents()) 
                    if (indiv.getTravelDiariesWeekdays() != null) 
                        ParkingManager.getInstance().allocateParkingForIndividual(indiv);
                
                continue;
            }

            for (Individual indiv : household.getResidents()) {
                if (indiv.getTravelDiariesWeekdays() == null) continue;
                
                if (traceLogger.isTraceEnabled()) 
                    traceLogger.trace("Set location for individual: " + indiv.toString());
                
                for (int matrixRow = 0; matrixRow < indiv.getTravelDiariesWeekdays().length; matrixRow++) {

                    /* check me if redundant */
                    indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.HouseholdID_Col.getIntValue()] = household.getId();
                    assert household.getId() == indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.HouseholdID_Col.getIntValue()] : "Household id is not identity";
                    /* check me if redundant */

                    // NOT set location for trip that does NOT travel (stay at home)
                    if (indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()] == -1 // origin
                            || indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Destination_Col.getIntValue()] == -1 // destination
                            || indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.StartTime_Col.getIntValue()] == -1 // start_time
                            || indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.EndTime_Col.getIntValue()] == -1 // end_time
                            || indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Duration_Col.getIntValue()] == -1 // duration
                            || indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()] == -1 // travel_mode
                    ) 
                        continue;
                    

                    if (traceLogger.isTraceEnabled()) 
                        traceLogger.trace("Travel Diaries Travel Mode BEFORE: " + indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
                    
                    traceLogger.trace("set origin");
                    int locationIDOrigin = getLocationID(matrixRow, household,
                            indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()],
                            "origin", indiv.getTravelDiariesWeekdays(), dwellingAllocator);
                    // before this step, "indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()]" is only the facility_type ID
                    indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()] = locationIDOrigin;

                    traceLogger.trace("set destination");
                    int locationIDDestination = getLocationID(matrixRow, household,
                            indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Destination_Col.getIntValue()],
                            "destination", indiv.getTravelDiariesWeekdays(), dwellingAllocator);
                    // before this step, "indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Destination_Col.getIntValue()]" is only the facility_type ID
                    indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.Destination_Col.getIntValue()] = locationIDDestination;

                    if (traceLogger.isTraceEnabled()) 
                        traceLogger.trace("Travel Diaries Travel Mode AFTER: " + indiv.getTravelDiariesWeekdays()[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
                }
                ParkingManager.getInstance().allocateParkingForIndividual(indiv);
            }

            household.setTravelDiariesChanged(false);
        }
    }

    final int idxTravelID = 0;
	final int idxIndivID = 1;
	final int idxHholdID = 2;
	final int idxAge = 3;
	final int idxGender = 4;
	final int idxIncome = 5;
	final int idxOrigin = 6;
	final int idxDest = 7;
	final int idxDepTime = 8;
	final int idxArrTime = 9;
	final int idxTripTime = 10;
	final int idxMode = 11;
	final int idxPurpose = 12;
	final int idxVehID = 13;
	final int idxTripID = 14;
	
	// Transims modes
	final int modeCarDriver = 2;
	final int modeCarPassenger = 11;
	final int modeOther = 8;
	
	final int locationTypeHome = 0;
	
	final int purposeWork = 1; // this is the value for 'Work' in the column 'purpose' in the attribute travelDiariesWeekdays of class Individual.
    final int purposeShopping = 2; // this is the value for 'Shopping' in the column 'purpose' in the attribute travelDiariesWeekdays of class Individual.
	
    /**
     * Finds location ID of origin and destination in each trip for whole Travel Diaries.
     * @param dwellingAllocator
     * @param householdPool
     */
    public void setLocation_1(DwellingAllocator dwellingAllocator, HouseholdPool householdPool) {
    	// Reset the parking manager
        ParkingManager.getInstance().resetParkingAllocationsForActivities();

        int countHhold = 0;
        int countCorrectedDriveTrips = 0;

        for (Household household : householdPool.getHouseholds().values()) {
            countHhold++;
            if (countHhold == 1 || countHhold == 100 || countHhold == 1000 || countHhold == 10000 || countHhold % 10000 == 0) 
                logger.info("...set location for household " + countHhold);
            
            if (!household.isTravelDiariesChanged()) {
                for (Individual indiv : household.getResidents()) 
                    if (indiv.getTravelDiariesWeekdays() != null) 
                        ParkingManager.getInstance().allocateParkingForIndividual(indiv);
                continue;
            }
            
            // corrects modes related of an individual's trips so that if two car driver trips are separated by 1 trip, the trip in between is changed to car driver.
            // this is to ensure that the origin of the middle trip is different to its destination.
            for (Individual indiv : household.getResidents()) {
            	int[][] travDiary = indiv.getTravelDiariesWeekdays();
            	int prevDriveTripIdx = -1;
            	for (int i=0; i<=travDiary.length-1; i++) {
            		if (travDiary[i][idxMode]==modeCarDriver) {
            			if (prevDriveTripIdx==-1) {
            				prevDriveTripIdx = i;
            			} else {
            				int dIdx = i-prevDriveTripIdx;
            				if (dIdx==2) {
            					travDiary[i-1][idxMode] = modeCarDriver;
            					countCorrectedDriveTrips += 1;
            				}
            			}
            			
            		}
            	}
            	indiv.setTravelDiariesWeekdays(travDiary);
            }
            
            
            // initialises a map to keep track of locations locked in for trips each individual in this household committed.
            // the format of the map is HashMap<individualID,HashMap<tripID,int[origin,destination]>>
            HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips = new HashMap<Integer,HashMap<Integer,int[]>>(); 
            
            // gets groups of cotravelling trips 
            HashMap<String, ArrayList<int[]>> allCoTravGroups = getGroupsOfCotravelling(household);
//            outputTravGroups(allCoTravGroups, household, String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_allCoTravGroups.csv");
            
            // gets groups of cotravelling trips that have at least a car driver and car passenger
            // these groups (after saved to driverPaxCoTravGroups) are removed from the allCoTravGroups.
            HashMap<String, ArrayList<int[]>> driverPaxCoTravGroups = getDriverPaxCoTravellingGroups(allCoTravGroups, household);
//            outputTravGroups(driverPaxCoTravGroups, household, String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_driverPaxCoTravGroups.csv");
            
            // gets groups of cotravelling trips that have more than 1 trip, no matter what the mode of these trips are
            // these groups (after saved to otherCoTravGroups) are removed from the allCoTravGroups.
            HashMap<String, ArrayList<int[]>> otherCoTravGroups = getOtherCoTravellingGroups(allCoTravGroups, household);
//            outputTravGroups(otherCoTravGroups, household, String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_otherCoTravGroups.csv");
            
            // each group in allCoTravGroups now contains only 1 trip.
//            outputTravGroups(allCoTravGroups, household, String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_loneTravGroups.csv");
            
            // assigns origin/destination location to trips in groups of cotravelling that have at least 1 car passenger and 1 car driver
            if (driverPaxCoTravGroups!=null && driverPaxCoTravGroups.size()!=0)
            	allocateOrigDestToCoTravTripsWithDriverPassenger(driverPaxCoTravGroups, household, lockedInTrips, dwellingAllocator);
            
            // assigns origin/destination locations to trips in groups of cotravelling that have no car passenger or no car driver.
            // note that this group might have trips with mode car passenger. These trips need to have their mode changes to the mode with one cotraveller or to Other.
            if (otherCoTravGroups!=null && otherCoTravGroups.size()!=0)
            	allocateOrigDestToOtherCoTravTrips(otherCoTravGroups, household, lockedInTrips, dwellingAllocator);
            
            // assigns origin/destination locations to trips that do not co-travel. These trips are stored in allCoTravGroups
            if (allCoTravGroups!=null && allCoTravGroups.size()!=0)
            	allocateOrigDestToLoneTrips(allCoTravGroups, household, lockedInTrips, dwellingAllocator);
            
            // at this stage all trips of each individual should have been assigned with a valid origin and a valid destination.
            // this can be verified by going through lockedInTrips and check if all trips of an individual are locked in with values for origin and destination other than -1.
            // also, origins and destinations of individuals travelling together should be matching/consistent.
            checkForUnallocatedTrips(household, lockedInTrips);
//            outputLockedInTrips(lockedInTrips, household);
            
            for (Individual resident : household.getResidents()) {
            	int[][] thisTD = resident.getTravelDiariesWeekdays();
            	for (int i=0; i<=thisTD.length-1; i++) {
            		if (thisTD[i][idxMode]==modeCarDriver) {
            			String carDriver = String.valueOf(household.getId()) + "," + String.valueOf(resident.getId()) + "," + 
            								String.valueOf(thisTD[i][idxTripID]);
                        ModelMain.getInstance().getDriverTimeMap().put(carDriver, new double[]{0, 0, 0, 0, 0});
            		}
            	}
            }
            
            household.setTravelDiariesChanged(false);
        }
        
//        TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+
//        		"_countCorrectedDriveTrips.csv",String.valueOf(countCorrectedDriveTrips) + " corrected",true);
    }
    
    private void outputLockedInTrips(HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips, Household household) {
    	for (Integer indivID : lockedInTrips.keySet()) {
    		HashMap<Integer,int[]> indivTrips  = lockedInTrips.get(indivID);
    		for (Integer tripID : indivTrips.keySet()) {
    			int[] locs = indivTrips.get(tripID);
    			String dataout = String.valueOf(household.getId() + "," + 
    							String.valueOf(indivID) + "," + String.valueOf(tripID) + "," + 
    							String.valueOf(locs[0]) + "," + String.valueOf(locs[1]));
    			TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_lockedInTrips.csv", dataout, true);
    		}
    	}
    }
    
    
    private void outputTravGroups(HashMap<String, ArrayList<int[]>> allCoTravGroups, Household household, String filename) {
    	Iterator<ArrayList<int[]>> itCoTravellers = allCoTravGroups.values().iterator();
    	while (itCoTravellers.hasNext()) {
    		ArrayList<int[]> coTrav = itCoTravellers.next();
    		for (int[] tripDetails : coTrav) {
    			String dataout = String.valueOf(household.getId()) + "," + String.valueOf(tripDetails[0]) + "," + String.valueOf(tripDetails[1]);
    			TextFileHandler.writeToText(filename, dataout, true);
    		}
    	}
    }
    
    
    /**
     * 
     * @param household
     * @param lockedInTrips
     */
    private void checkForUnallocatedTrips(Household household, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips) {
    	for (Individual resident : household.getResidents()) {
    		int[][] thisTD = resident.getTravelDiariesWeekdays(); 
    		for (int i=0; i<=thisTD.length-1; i++) {
    			if (!isOriginLockedIn(resident.getId(),i,lockedInTrips)) {
    				String dataout = String.valueOf(household.getId()) + "," + String.valueOf(resident.getId()) + "," + String.valueOf(i) + "," + 
    									String.valueOf(thisTD[i][idxOrigin]) + "," + String.valueOf(thisTD[i][idxDest]) + "," + 
    									String.valueOf(thisTD[i][idxMode]);
    				TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_unassignedTrips.csv", dataout, true);
    			}
    			if (!isDestinationLockedIn(resident.getId(),i,lockedInTrips)) {
    				String dataout = String.valueOf(household.getId()) + "," + String.valueOf(resident.getId()) + "," + String.valueOf(i) + "," + 
							String.valueOf(thisTD[i][idxOrigin]) + "," + String.valueOf(thisTD[i][idxDest]) + "," + 
							String.valueOf(thisTD[i][idxMode]);
    				TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_unassignedTrips.csv", dataout, true);
    			}
    		}
    		
    	}
    }
    
    
    /**
     * assigns origin/destination locations to trips that do not co-travel. These trips are stored in allCoTravGroups
     * @param allCoTravGroups
     * @param household
     * @param lockedInTrips
     * @param dwellingAllocator
     */
    private void allocateOrigDestToLoneTrips(HashMap<String, ArrayList<int[]>> allCoTravGroups, Household household, 
    		HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips, DwellingAllocator dwellingAllocator) {
    	if (allCoTravGroups==null || allCoTravGroups.size()==0) return; 
    	Iterator<ArrayList<int[]>> itCoTrav = allCoTravGroups.values().iterator();
    	while (itCoTrav.hasNext()) {
    		ArrayList<int[]> coTrav = itCoTrav.next();
        	if (coTrav==null || coTrav.size()==0) return;
        	
        	int indivID = coTrav.get(0)[0];
        	int tripID = coTrav.get(0)[1];
        	Individual indiv = household.getIndividualInHholdByID(indivID);
        	
        	if (indiv.getTravelDiariesWeekdays()[tripID][idxMode]==modeCarPassenger) {
        		String dataout = String.valueOf(household.getId()) + "," + String.valueOf(indivID) + "," + String.valueOf(tripID);
//				TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_badPass_loneTrips.csv", dataout, true);
        		indiv.getTravelDiariesWeekdays()[tripID][idxMode] = modeOther;
        	}
        	
        	if (!isOriginLockedIn(indivID, tripID, lockedInTrips)) {
        		getLocationForOrigin(indiv, tripID, lockedInTrips, household, dwellingAllocator);
        		// recordOriginToLockedInTrips(indiv, tripID, origLoc, lockedInTrips);
        	}
        	if (!isDestinationLockedIn(indivID, tripID, lockedInTrips)) {
        		getLocationForDestination(indiv, tripID, lockedInTrips, household, dwellingAllocator);
        		// recordDestinationToLockedInTrips(indiv, tripID, destLoc, lockedInTrips);
        	}
        	
    	}
    	
    }
    
    
    /**
     * assigns origin/destination locations to trips in groups of cotravelling that have no car passenger or no car driver.
     * note that this group might have trips with mode car passenger. These trips need to have their mode changes to the mode with one cotraveller or to Other.
     * 
     * @param otherCoTravGroups
     * @param household
     * @param lockedInTrips
     * @param dwellingAllocator
     */
    private void allocateOrigDestToOtherCoTravTrips(HashMap<String, ArrayList<int[]>> otherCoTravGroups, Household household, 
    		HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips, DwellingAllocator dwellingAllocator) {
    	Iterator<ArrayList<int[]>> itCoTravellers = otherCoTravGroups.values().iterator();
    	while (itCoTravellers.hasNext()) {
    		ArrayList<int[]> coTravellers = itCoTravellers.next();
    		assignOriDestToOneGroupOtherCoTravTrips(household, lockedInTrips, dwellingAllocator, coTravellers);
    	}
    }
    
    
    /**
     * assigns origin/destination location to trips in groups of cotravelling that have at least 1 car passenger and 1 car driver.
     * @param driverPaxCoTravGroups
     * @param household
     * @param lockedInTrips
     * @param dwellingAllocator
     */
    private void allocateOrigDestToCoTravTripsWithDriverPassenger(HashMap<String, ArrayList<int[]>> driverPaxCoTravGroups, Household household, 
    		HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips, DwellingAllocator dwellingAllocator) {
    	
    	// searches for youngest car passenger (youngestPas) in this household.
        // the destination of trips of other car passengers that have same departure time and trip time in the household will be changed to match with destination of this youngest car passenger.
        int youngestPasID = getYoungestCarPassengerInHousehold(driverPaxCoTravGroups, household);
        
        Iterator<String> itCoTravellersKey = driverPaxCoTravGroups.keySet().iterator();
		while (itCoTravellersKey.hasNext()) {
			String key = itCoTravellersKey.next();
			ArrayList<int[]> coTravellers = driverPaxCoTravGroups.get(key);
			if (coTravellers==null || coTravellers.size()==0) continue;
			
			ArrayList<int[]> carDrivers = getCarDriverIDs(coTravellers, household);
			ArrayList<int[]> carPases = getCarPassengersIDs(coTravellers, household);
			ArrayList<int[]> otherTrips = getOtherTripIDs(coTravellers, household);

			int[] youngestPasDetails = new int[] {-1,-1};
			// checks if this group contains the trip of youngestPas, assign destination and origin of youngestPas to passDest and passOrig
			for (int[] passTripDetails : carPases) 
				if (passTripDetails[0]==youngestPasID) {
					youngestPasDetails[0] = passTripDetails[0];
					youngestPasDetails[1] = passTripDetails[1];
					break;
				}

			if (youngestPasDetails[0]==-1) // trip of youngestPas is not in this group, searches for the youngest passenger in this group
				youngestPasDetails = getYoungestPassengerInCoTravellingGroup(carPases, household);
		
			Individual youngestPas = household.getIndividualInHholdByID(youngestPasDetails[0]);
					
			// assigns origin to the trip of the youngest passenger in this group
			int origLoc = getLocationForOrigin(youngestPas, youngestPasDetails[1], lockedInTrips, household, dwellingAllocator);
			// assigns destination to the trip of the youngest passenger in this group
			int destLoc = getLocationForDestination(youngestPas, youngestPasDetails[1], lockedInTrips, household, dwellingAllocator);
			
			// searches for a car driver in this group that can take the youngest passenger from origLoc to destLoc
			// this search is done before the search for driver for other passengers so that the youngest passenger has a higher chance of finding a successful driver.
			String hitPass = String.valueOf(household.getId()) + "_" + String.valueOf(youngestPas.getId()) + "_" + String.valueOf(youngestPasDetails[1]+1);
			boolean driverForYoungestPassFound = searchDriverForPassWithLockedOrigDest(household, lockedInTrips, carDrivers, origLoc, destLoc, hitPass); 
			if (!driverForYoungestPassFound) {
				String dataout = String.valueOf(household.getId()) + "," + String.valueOf(youngestPasDetails[0]) + "," + String.valueOf(youngestPasDetails[1]);
//				TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_reassignedPass.csv", dataout, true);
				// changes the mode of the youngest passenger trip to Other
				household.getIndividualInHholdByID(youngestPasDetails[0]).getTravelDiariesWeekdays()[youngestPasDetails[1]][idxMode] = modeOther;
			}
			// assigns origin and destination to each of the remaining passenger trips in this group
			assignOriDestToPassesInCoTravGroupWithDrivePass(household, lockedInTrips, dwellingAllocator, carDrivers,
					carPases, youngestPasDetails, origLoc, destLoc, driverForYoungestPassFound);
			
			// assign locations to each of the drive trips in this group
			assignOriDestToDriversInCoTravGroupWithDrivePass(household, lockedInTrips, dwellingAllocator, carDrivers);
			
			// for each of the remaining trip in this cotravelling group (The mode of these trips are other than car driver and car passenger)
			assignOriDestToOneGroupOtherCoTravTrips(household, lockedInTrips, dwellingAllocator, otherTrips);
		}
    }

    
    /**
     * 
     * @param coTravellers
     * @param household
     */
    private void assignOriDestToOneGroupOtherCoTravTrips(Household household, HashMap<Integer, HashMap<Integer, int[]>> lockedInTrips,
			DwellingAllocator dwellingAllocator, ArrayList<int[]> coTravellers) {
    	if (coTravellers==null || coTravellers.size()==0) return;
    	
    	// picks the youngest person in this group
    	int youngestID = -1, youngestTripID = -1;
    	for (int[] tripDetails : coTravellers) {
    		int indivID = tripDetails[0];
			int tripID = tripDetails[1];
			Individual indiv = household.getIndividualInHholdByID(indivID);
			if (youngestID==-1) {
				youngestID = indivID;
				youngestTripID = tripID;
			} else {
				int youngestAge = household.getIndividualInHholdByID(youngestID).getAge();
				if (indiv.getAge()<youngestAge) {
					youngestID = indivID;
					youngestTripID = tripID;
				}
			}
    	}
    	// assigns origin and destination of the trip of this youngest person
    	boolean youngestOrigLockedIn = isOriginLockedIn(youngestID, youngestTripID, lockedInTrips);
    	boolean youngestDestLockedIn = isDestinationLockedIn(youngestID, youngestTripID, lockedInTrips);
    	Individual youngest = household.getIndividualInHholdByID(youngestID);
    	if (youngest.getTravelDiariesWeekdays()[youngestTripID][idxMode]==modeCarPassenger) {
    		String dataout = String.valueOf(household.getId()) + "," + String.valueOf(youngestID) + "," + String.valueOf(youngestTripID);
//			TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_badPass_OtherCoTrav.csv", dataout, true);
    		youngest.getTravelDiariesWeekdays()[youngestTripID][idxMode] = modeOther;
    	}
    	int youngestOrig, youngestDest;
    	if (youngestOrigLockedIn) 
    		youngestOrig = lockedInTrips.get(youngestID).get(youngestTripID)[0];
    	else 
    		youngestOrig = getLocationForOrigin(youngest, youngestTripID, lockedInTrips, household, dwellingAllocator);
    	
    	if (youngestDestLockedIn) 
    		youngestDest = lockedInTrips.get(youngestID).get(youngestTripID)[1];
    	else 
    		youngestDest = getLocationForDestination(youngest, youngestTripID, lockedInTrips, household, dwellingAllocator);
    	
    	for (int[] tripDetails : coTravellers) {
    		if (tripDetails[0]==youngestID) continue;
    		int indivID = tripDetails[0];
			int tripID = tripDetails[1];
			Individual indiv = household.getIndividualInHholdByID(indivID);
			if (indiv.getTravelDiariesWeekdays()[tripID][idxMode]==modeCarPassenger) {
				String dataout = String.valueOf(household.getId()) + "," + String.valueOf(indivID) + "," + String.valueOf(tripID);
//				TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_badPass_OtherCoTrav.csv", dataout, true);
				indiv.getTravelDiariesWeekdays()[tripID][idxMode]=modeOther;
			}
				
			boolean origLockedIn = isOriginLockedIn(indivID, tripID, lockedInTrips);
	    	boolean destLockedIn = isDestinationLockedIn(indivID, tripID, lockedInTrips);
	    	if (!origLockedIn) {
	    		// assigns youngestOrig to origin of this trip
	    		indiv.getTravelDiariesWeekdays()[tripID][idxOrigin] = youngestOrig;
	    		// records origin of this trip to lockedInTrips
	    		recordOriginToLockedInTrips(indiv, tripID, indiv.getTravelDiariesWeekdays()[tripID][idxOrigin], lockedInTrips);
	    	}
	    	if (!destLockedIn) {
	    		// assigns youngestDest to origin of this trip
	    		indiv.getTravelDiariesWeekdays()[tripID][idxDest] = youngestDest;
	    		// records destination of this trip to lockedInTrips
	    		recordDestinationToLockedInTrips(indiv, tripID, indiv.getTravelDiariesWeekdays()[tripID][idxDest], lockedInTrips);
	    	}
	    	
		}
    }
    
    
    /**
     * 
     * @param household
     * @param lockedInTrips
     * @param dwellingAllocator
     * @param carDrivers
     */
    private void assignOriDestToDriversInCoTravGroupWithDrivePass(Household household, HashMap<Integer, HashMap<Integer, int[]>> lockedInTrips,
			DwellingAllocator dwellingAllocator, ArrayList<int[]> carDrivers) {
    	for (int[] driverDetails : carDrivers) {
    		int driverID = driverDetails[0];
    		int driveritrip = driverDetails[1];
    		Individual driver = household.getIndividualInHholdByID(driverID);
    		boolean origDriveTripLockedIn = isOriginLockedIn(driverID, driveritrip, lockedInTrips);
			boolean destDriveTripLockedIn = isDestinationLockedIn(driverID, driveritrip, lockedInTrips);
			if (!origDriveTripLockedIn) {
				int driveOrig = getLocationForOrigin(driver, driveritrip, lockedInTrips, household, dwellingAllocator);
				// records origin of drive trip to lockedInTrips
				// recordOriginToLockedInTrips(driver, driveritrip, driveOrig, lockedInTrips);
			}
			if (!destDriveTripLockedIn) {
				int driveDest = getLocationForDestination(driver, driveritrip, lockedInTrips, household, dwellingAllocator);
				// records destination of drive trip to lockedInTrips
				// recordDestinationToLockedInTrips(driver, driveritrip, driveDest, lockedInTrips);
			}
    	}
    }
    
    
	/**
	 * @param household
	 * @param lockedInTrips
	 * @param dwellingAllocator
	 * @param carDrivers
	 * @param carPases
	 * @param youngestPasDetails
	 * @param origLoc
	 * @param destLoc
	 * @param driverFound
	 * @return
	 */
	private void assignOriDestToPassesInCoTravGroupWithDrivePass(Household household, HashMap<Integer, HashMap<Integer, int[]>> lockedInTrips,
			DwellingAllocator dwellingAllocator, ArrayList<int[]> carDrivers, ArrayList<int[]> carPases, int[] youngestPasDetails, int origLoc, int destLoc,
			boolean driverForYoungestPassFound) {
		
		// if there are no car drivers in this group (which is weird but could happen)
		if (carDrivers==null || carDrivers.size()==0) {
			String textOut = String.valueOf(household.getId()) + "," + String.valueOf(youngestPasDetails[0]) + "," + String.valueOf(youngestPasDetails[1]);
			TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_incorreclyNoDrivers.csv", textOut, true);
			
			for (int[] passTripDetails : carPases) {
				int passID = passTripDetails[0];
				int passitrip = passTripDetails[1];
				Individual pass = household.getIndividualInHholdByID(passID);
				
				// changes the mode of car passengers in this group to Other
				pass.getTravelDiariesWeekdays()[passitrip][idxMode] = modeOther;
				
				boolean passOriginLockedIn = isOriginLockedIn(passID, passitrip, lockedInTrips);
				boolean passDestLockedIn = isDestinationLockedIn(passID, passitrip, lockedInTrips);
				
				if (!passOriginLockedIn) {
					// assigns origin to this passenger trip
					int passOrig = getLocationForOrigin(pass, passitrip, lockedInTrips, household, dwellingAllocator);
				}
				if (!passDestLockedIn) {
					// assigns destination to this passenger trip
					int passDest = getLocationForDestination(pass, passitrip, lockedInTrips, household, dwellingAllocator);
				}
			}
		} else {
			for (int[] passTripDetails : carPases) {
				if (passTripDetails[0]==youngestPasDetails[0]) continue;
				
				int passID = passTripDetails[0];
				int passitrip = passTripDetails[1];
				Individual pass = household.getIndividualInHholdByID(passID);
				boolean passOriginLockedIn = isOriginLockedIn(passID, passitrip, lockedInTrips);
				boolean passDestLockedIn = isDestinationLockedIn(passID, passitrip, lockedInTrips);
				
				String hitPass = String.valueOf(household.getId()) + "_" + String.valueOf(passID) + "_" + String.valueOf(passitrip+1);
				
				// if origin is locked in and destination is locked in
				if (passOriginLockedIn && passDestLockedIn) {
					// if this locked-in origin and destination match with origLoc and destLoc and if a driver was found to go from origLoc to destLoc, 
					// continue to next passenger of this group.
					if (lockedInTrips.get(passID).get(passitrip)[0]==origLoc && lockedInTrips.get(passID).get(passitrip)[0]==destLoc &&
							driverForYoungestPassFound) 
						continue;
					else {
						boolean driverFound = searchDriverForPassWithLockedOrigDest(household, lockedInTrips, carDrivers, 
								lockedInTrips.get(passID).get(passitrip)[0], lockedInTrips.get(passID).get(passitrip)[1], hitPass);
						// if no suitable driver found, changes the mode of this passenger trip to Other
						if (!driverFound) {
							String dataout = String.valueOf(household.getId()) + "," + String.valueOf(passID) + "," + String.valueOf(passitrip);
//							TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_reassignedPass.csv", dataout, true);
							pass.getTravelDiariesWeekdays()[passitrip][idxMode] = modeOther;
						}
					}
				}
				
				// if origin is not locked in and destination is not locked in
				else if (!passOriginLockedIn && !passDestLockedIn) {
					// if a driver was found to go from origLoc to destLoc, this passenger will have same origin and destination with the youngest passenger
					if (driverForYoungestPassFound==true) {
						// assigns origLoc and destLoc to origin and destination of this passenger trip
						pass.getTravelDiariesWeekdays()[passitrip][idxOrigin] = origLoc;
						pass.getTravelDiariesWeekdays()[passitrip][idxDest] = destLoc;
						// records origin and destination of this passenger trip to lockedInTrips
						recordOriginToLockedInTrips(pass, passitrip, origLoc, lockedInTrips);
						recordDestinationToLockedInTrips(pass, passitrip, destLoc, lockedInTrips);
					} else {
						// searches for a driver with both origin and destination locked in
						searchDriverForPassWithOrigDestNotLocked(household, lockedInTrips, dwellingAllocator, carDrivers, passitrip, pass, hitPass);
					}
					
				}
				
				// else if origin is not locked in and destination is locked in
				else if (!passOriginLockedIn && passDestLockedIn) {
					// searches for a potential car driver, i.e. one with destination matching passDest or destination not locked-in
					boolean driverFound = searchDriverForPassWithLockedDest(household, lockedInTrips, dwellingAllocator, carDrivers, passitrip, pass, hitPass);
					// if no suitable driver found, changes the mode of this passenger trip to Other
					if (!driverFound) {
						String dataout = String.valueOf(household.getId()) + "," + String.valueOf(passID) + "," + String.valueOf(passitrip);
//						TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_reassignedPass.csv", dataout, true);
						pass.getTravelDiariesWeekdays()[passitrip][idxMode] = modeOther;
					}
				}
				
				// else if origin is locked in and destination is not locked in
				else if (passOriginLockedIn && !passDestLockedIn) {
					// searches for a potential car driver, i.e. one with origin matching with passOrig or origin not locked-in
					boolean driverFound = searchDriverForPassWithLockedOrig(household, lockedInTrips, dwellingAllocator, carDrivers, passitrip, pass, hitPass);
					// if no suitable driver found, changes the mode of this passenger trip to Other
					if (!driverFound) {
						String dataout = String.valueOf(household.getId()) + "," + String.valueOf(passID) + "," + String.valueOf(passitrip);
//						TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_reassignedPass.csv", dataout, true);
						pass.getTravelDiariesWeekdays()[passitrip][idxMode] = modeOther;
					}
				}
			}
		}
		
		
	}

	/**
	 * @param household
	 * @param lockedInTrips
	 * @param dwellingAllocator
	 * @param carDrivers
	 * @param passitrip
	 * @param pass
	 */
	private void searchDriverForPassWithOrigDestNotLocked(Household household,
			HashMap<Integer, HashMap<Integer, int[]>> lockedInTrips,
			DwellingAllocator dwellingAllocator, ArrayList<int[]> carDrivers,
			int passitrip, Individual pass, String hitPass) {
		boolean driverFound = false;
		for (int[] driverDetails : carDrivers) {
			int driverID = driverDetails[0];
			int driveritrip = driverDetails[1];
			
			String hitDriver = String.valueOf(household.getId()) + "_" + 
					String.valueOf(driverID) + "_" + String.valueOf(driveritrip+1);
			
			if (isOriginLockedIn(driverID, driveritrip, lockedInTrips) && 
					isDestinationLockedIn(driverID, driveritrip, lockedInTrips)) {
				// assign origin and destination of this driver trip to origin and destination of this passenger trip
				pass.getTravelDiariesWeekdays()[passitrip][idxOrigin] = lockedInTrips.get(driverID).get(driveritrip)[0];
				pass.getTravelDiariesWeekdays()[passitrip][idxDest] = lockedInTrips.get(driverID).get(driveritrip)[1];
				// records origin and destination of passenger trip to lockedInTrips
				recordOriginToLockedInTrips(pass, passitrip, pass.getTravelDiariesWeekdays()[passitrip][idxOrigin], lockedInTrips);
				recordDestinationToLockedInTrips(pass, passitrip, pass.getTravelDiariesWeekdays()[passitrip][idxDest], lockedInTrips);
				driverFound = true;
				ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
				break;
			}
		}
		if (!driverFound) { // if not found
			// pick the first one from the carDrivers list and assign
			int[] driverDetails = carDrivers.get(0);
			int driverID = driverDetails[0];
			int driveritrip = driverDetails[1];
			Individual driver = household.getIndividualInHholdByID(driverID);
			boolean origDriveTripLockedIn = isOriginLockedIn(driverID, driveritrip, lockedInTrips);
			boolean destDriveTripLockedIn = isDestinationLockedIn(driverID, driveritrip, lockedInTrips);

			String hitDriver = String.valueOf(household.getId()) + "_" + 
					String.valueOf(driverID) + "_" + String.valueOf(driveritrip+1);
			
			if (!origDriveTripLockedIn && !destDriveTripLockedIn) {
				// assigns origin to this passenger trip
				int passOrig = getLocationForOrigin(pass, passitrip, lockedInTrips, household, dwellingAllocator);
				// assigns destination to this passenger trip
				int passDest = getLocationForDestination(pass, passitrip, lockedInTrips, household, dwellingAllocator);
				// assigns this origin and destination to origin and destination of driver trip
				driver.getTravelDiariesWeekdays()[driveritrip][idxOrigin] = passOrig;
				driver.getTravelDiariesWeekdays()[driveritrip][idxDest] = passDest;
				// records origin and destination of driver trip to lockedInTrips
				recordOriginToLockedInTrips(driver, driveritrip, passOrig, lockedInTrips);
				recordDestinationToLockedInTrips(driver, driveritrip, passDest, lockedInTrips);
			}
			else if (!origDriveTripLockedIn && destDriveTripLockedIn) {
				// assigns origin to this passenger trip
				int passOrig = getLocationForOrigin(pass, passitrip, lockedInTrips, household, dwellingAllocator);
				// assigns this origin to origin of driver trip
				driver.getTravelDiariesWeekdays()[driveritrip][idxOrigin] = passOrig;
				// records origin of driver trip to lockedInTrips
				recordOriginToLockedInTrips(driver, driveritrip, passOrig, lockedInTrips);
				
				// assigns driver destination to destination of the passenger trip
				pass.getTravelDiariesWeekdays()[passitrip][idxDest] = lockedInTrips.get(driverID).get(driveritrip)[1];
				// records destination of passenger trip to lockedInTrips
				recordDestinationToLockedInTrips(pass, passitrip, pass.getTravelDiariesWeekdays()[passitrip][idxDest], lockedInTrips);
			}
			else if (origDriveTripLockedIn && !destDriveTripLockedIn) {
				// assigns destination to this passenger trip
				int passDest = getLocationForDestination(pass, passitrip, lockedInTrips, household, dwellingAllocator);
				// assigns this destination to destination of driver trip
				driver.getTravelDiariesWeekdays()[driveritrip][idxDest] = passDest;
				// records destination of driver trip to lockedInTrips
				recordDestinationToLockedInTrips(driver, driveritrip, passDest, lockedInTrips);
				
				// assigns origin of drive trip to origin of the passenger trip
				pass.getTravelDiariesWeekdays()[passitrip][idxOrigin] = lockedInTrips.get(driverID).get(driveritrip)[0];
				// records origin of passenger trip to lockedInTrips
				recordOriginToLockedInTrips(pass, passitrip, pass.getTravelDiariesWeekdays()[passitrip][idxOrigin], lockedInTrips);
			}
			
			ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
		}
	}

	
	/**
	 * @param household
	 * @param lockedInTrips
	 * @param dwellingAllocator
	 * @param carDrivers
	 * @param passID
	 * @param passitrip
	 * @param pass
	 * @return
	 */
	private boolean searchDriverForPassWithLockedOrig(Household household,
			HashMap<Integer, HashMap<Integer, int[]>> lockedInTrips,
			DwellingAllocator dwellingAllocator, ArrayList<int[]> carDrivers,
			int passitrip, Individual pass, String hitPass) {
		boolean driverFound;
		// gets origin of this passenger
		int passOrig = lockedInTrips.get(pass.getId()).get(passitrip)[0]; 
		// searches for a potential car driver, i.e. one with a matching origin or origin not locked-in (preferably with mode serve passenger)
		driverFound = false;
		for (int[] driverDetails : carDrivers) {
			int driverID = driverDetails[0];
			int driveritrip = driverDetails[1];
			Individual driver = household.getIndividualInHholdByID(driverID);
			
			String hitDriver = String.valueOf(household.getId()) + "_" + String.valueOf(driverID) + "_" + String.valueOf(driveritrip+1);
			
			// if the origin of this car driver is locked in to a location different to passOrig, move on to the next car driver
//			if (isOriginLockedInToAnotherLoc(driverID, driveritrip, lockedInTrips, passOrig)==true) continue;
			if (isOriginLockedIn(driverID, driveritrip, lockedInTrips)) {
				if (lockedInTrips.get(driverID).get(driveritrip)[0]==passOrig) {
					// if destination of this car driver is locked in
					if (isDestinationLockedIn(driverID, driveritrip, lockedInTrips)==true) {
						int driverDest = lockedInTrips.get(driverID).get(driveritrip)[1];
						// assigns this destination to destination of passenger trip
						pass.getTravelDiariesWeekdays()[passitrip][idxDest] = driverDest;
						// records destination of passenger trip into lockedInTrips
						recordDestinationToLockedInTrips(pass, passitrip, driverDest, lockedInTrips);
					} else {
						// gets destination for this passenger trip
						int passDest = getLocationForDestination(pass, passitrip, lockedInTrips, household, dwellingAllocator);
						// assigns this destination to destination of car driver trip
						driver.getTravelDiariesWeekdays()[driveritrip][idxDest] = passDest;
						// records destination of car driver trip into lockedInTrips
						recordDestinationToLockedInTrips(driver, driveritrip, passDest, lockedInTrips);
					}
					driverFound = true;
					ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
					break;
				} else continue;
			} else {
				// assigns passOrig to the origin of this driver trip
				driver.getTravelDiariesWeekdays()[driveritrip][idxOrigin] = passOrig;
				// records origin of this driver trip to lockedInTrips
				recordOriginToLockedInTrips(driver, driveritrip, passOrig, lockedInTrips);
				
				// if destination of this car driver is locked in
				if (isDestinationLockedIn(driverID, driveritrip, lockedInTrips)==true) {
					int driverDest = lockedInTrips.get(driverID).get(driveritrip)[1];
					// assigns this destination to destination of passenger trip
					pass.getTravelDiariesWeekdays()[passitrip][idxDest] = driverDest;
					// records destination of passenger trip into lockedInTrips
					recordDestinationToLockedInTrips(pass, passitrip, driverDest, lockedInTrips);
				} else {
					// gets destination for this passenger trip
					int passDest = getLocationForDestination(pass, passitrip, lockedInTrips, household, dwellingAllocator);
					// assigns this destination to destination of car driver trip
					driver.getTravelDiariesWeekdays()[driveritrip][idxDest] = passDest;
					// records destination of car driver trip into lockedInTrips
					recordDestinationToLockedInTrips(driver, driveritrip, passDest, lockedInTrips);
				}
				driverFound = true;
				ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
				break;
			}
		}
		// if no driver found
		if (!driverFound)
			// generates a location for destination of this passenger trip
			getLocationForDestination(pass, passitrip, lockedInTrips, household, dwellingAllocator);
		
		return driverFound;
	}

	/**
	 * @param household
	 * @param lockedInTrips
	 * @param dwellingAllocator
	 * @param carDrivers
	 * @param passitrip
	 * @param pass
	 * @param passDest
	 * @return
	 */
	private boolean searchDriverForPassWithLockedDest(Household household,
			HashMap<Integer, HashMap<Integer, int[]>> lockedInTrips,
			DwellingAllocator dwellingAllocator, ArrayList<int[]> carDrivers,
			int passitrip, Individual pass, String hitPass) {
		// gets destination of this passenger
		int passDest = lockedInTrips.get(pass.getId()).get(passitrip)[1];
		
		boolean driverFound;
		driverFound = false;
		for (int[] driverDetails : carDrivers) {
			int driverID = driverDetails[0];
			int driveritrip = driverDetails[1];
			Individual driver = household.getIndividualInHholdByID(driverID);
			
			String hitDriver = String.valueOf(household.getId()) + "_" + 
					String.valueOf(driverID) + "_" + String.valueOf(driveritrip+1);
			
			// if the destination of this driver is locked in to a location different to passDest, move on to the next car driver
			//if (isDestinationLockedInToAnotherLoc(driverID, driveritrip, lockedInTrips, passDest)==true) continue;
			if (isDestinationLockedIn(driverID, driveritrip, lockedInTrips)) {
				if (lockedInTrips.get(driverID).get(driveritrip)[1]==passDest) {
					// if origin of car driver is locked-in
					if (isOriginLockedIn(driverID, driveritrip, lockedInTrips)==true) {
						int driverOrig = lockedInTrips.get(driverID).get(driveritrip)[0];
						// assigns this origin to origin of this passenger trip
						pass.getTravelDiariesWeekdays()[passitrip][idxOrigin] = driverOrig;
						// records origin of this passenger trip to lockedInTrips
						recordOriginToLockedInTrips(pass, passitrip, driverOrig, lockedInTrips);
					} else {
						// gets a location for origin of this passenger trip
						int passOrig = getLocationForOrigin(pass, passitrip, lockedInTrips, household, dwellingAllocator);
						// assigns this location to origin of the driver trip
						driver.getTravelDiariesWeekdays()[driveritrip][idxOrigin] = passOrig;
						// records origin of driver trip to lockedInTrips
						recordOriginToLockedInTrips(driver, driveritrip, passOrig, lockedInTrips);
					}
					driverFound = true;
					ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
					break;
				} else continue;
			} else {
				// assigns passDest to the destination of this driver trip
				driver.getTravelDiariesWeekdays()[driveritrip][idxDest] = passDest;
				// records the destination of this driver trip to lockedInTrips
				recordDestinationToLockedInTrips(driver, driveritrip, passDest, lockedInTrips);
				// if origin of car driver is locked-in
				if (isOriginLockedIn(driverID, driveritrip, lockedInTrips)==true) {
					int driverOrig = lockedInTrips.get(driverID).get(driveritrip)[0];
					// assigns this origin to origin of this passenger trip
					pass.getTravelDiariesWeekdays()[passitrip][idxOrigin] = driverOrig;
					// records origin of this passenger trip to lockedInTrips
					recordOriginToLockedInTrips(pass, passitrip, driverOrig, lockedInTrips);
				} else {
					// gets a location for origin of this passenger trip
					int passOrig = getLocationForOrigin(pass, passitrip, lockedInTrips, household, dwellingAllocator);
					// assigns this location to origin of the driver trip
					driver.getTravelDiariesWeekdays()[driveritrip][idxOrigin] = passOrig;
					// records origin of driver trip to lockedInTrips
					recordOriginToLockedInTrips(driver, driveritrip, passOrig, lockedInTrips);
				}
				driverFound = true;
				ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
				break;
			}
		}
		
		if (!driverFound) // if no driver found
			// generates a location for the origin for this passenger trip
			getLocationForOrigin(pass, passitrip, lockedInTrips, household, dwellingAllocator);
		
		return driverFound;
	}

    
    /**
     * 
     * @param household
     * @param lockedInTrips
     * @param carDrivers
     * @param passOrig
     * @param passDest
     * @return
     */
	private boolean searchDriverForPassWithLockedOrigDest(Household household, HashMap<Integer, HashMap<Integer, int[]>> lockedInTrips,
			ArrayList<int[]> carDrivers, int passOrig, int passDest, String hitPass) {
		
		boolean driverFound = false;
		
		// searches for a car driver in this group with matching origin and destination
		for (int[] driverDetails : carDrivers) {
			int driverID = driverDetails[0];
			int driveritrip = driverDetails[1];
			Individual driver = household.getIndividualInHholdByID(driverID);
			boolean origDriveTripLockedIn = isOriginLockedIn(driverID, driveritrip, lockedInTrips);
			boolean destDriveTripLockedIn = isDestinationLockedIn(driverID, driveritrip, lockedInTrips);
			String hitDriver = String.valueOf(household.getId()) + "_" + 
							String.valueOf(driverID) + "_" + String.valueOf(driveritrip+1);
			
			// if both the origin and destination of this driver have been locked in 
			if (origDriveTripLockedIn && destDriveTripLockedIn)  
				// if destination and origin of driver trip matches with destination and origin of passenger trip
				if (lockedInTrips.get(driverID).get(driveritrip)[0]==passOrig && lockedInTrips.get(driverID).get(driveritrip)[1]==passDest) {
					driverFound = true;
					ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
					break;
				} else continue;
			
			// if the origin of this driver is locked in and destination is not
			else if (origDriveTripLockedIn && !destDriveTripLockedIn) {
				if (lockedInTrips.get(driverID).get(driveritrip)[0]==passOrig) {
					// assigns passDest to the destination of this driver trip
					driver.getTravelDiariesWeekdays()[driveritrip][idxDest] = passDest;
					// records destination of driver trip to lockedInTrips
					recordDestinationToLockedInTrips(driver, driveritrip, passDest, lockedInTrips);
					driverFound = true;
					ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
					break;
				} else continue;
			}
			
			// if the origin of this driver is not locked and the destination is
			else if (!origDriveTripLockedIn && destDriveTripLockedIn) {
				if (lockedInTrips.get(driverID).get(driveritrip)[1]==passDest) {
					// assigns passOrig to the origin of this driver trip
					driver.getTravelDiariesWeekdays()[driveritrip][idxOrigin] = passOrig;
					// records origin of driver trip to lockedInTrips
					recordOriginToLockedInTrips(driver, driveritrip, passOrig, lockedInTrips);
					driverFound = true;
					ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
					break;
				} else continue;
			}
			
			// if both origin and destination of this driver are NOT locked in
			else if (!origDriveTripLockedIn && !destDriveTripLockedIn) {
				// assigns passOrig to origin and passDest to destination of this driver trip
				driver.getTravelDiariesWeekdays()[driveritrip][idxOrigin] = passOrig;
				driver.getTravelDiariesWeekdays()[driveritrip][idxDest] = passDest;
				// records origin and destination of driver trip to lockedInTrips
				recordOriginToLockedInTrips(driver, driveritrip, passOrig, lockedInTrips);
				recordDestinationToLockedInTrips(driver, driveritrip, passDest, lockedInTrips);
				driverFound = true;
				ModelMain.getInstance().getOtherHhodCarPaxCarDriverMap().put(hitPass, hitDriver);
				break;
			}
		}
		
		return driverFound;
		
	}
    
    
    /**
     * checks if trip itrip made by individual with ID indivID having its origin recorded in lockedInTrips.
     * the function returns false if:
     * - indivID is not found in lockedInTrips
     * - itrip is not found in lockedInTrips
     * - origin of trip itrip is -1.
     * the function only returns true if the origin of trip itrip not -1 and is different to origLoc.
     * @param indiv
     * @param itrip
     * @param lockedInTrips
     * @return
     */
    private boolean isOriginLockedIn(int indivID, int itrip, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips) {
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indivID);
    	if (thisIndivLockedTrips==null) return false;
    	int[] thisTrip = thisIndivLockedTrips.get(itrip);
    	if (thisTrip==null || thisTrip[0]==-1) return false;
    	
    	return true;
    }
    
    
    /**
     * checks if trip itrip made by individual with ID indivID having its destination recorded in lockedInTrips.
     * the function returns false if:
     * - indivID is not found in lockedInTrips
     * - itrip is not found in lockedInTrips
     * - destination of trip itrip is -1.
     * the function only returns true if the destination of trip itrip not -1 and is different to destLoc.
     * @param indiv
     * @param itrip
     * @param lockedInTrips
     * @return
     */
    private boolean isDestinationLockedIn(int indivID, int itrip, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips) {
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indivID);
    	if (thisIndivLockedTrips==null) return false;
    	int[] thisTrip = thisIndivLockedTrips.get(itrip);
    	if (thisTrip==null || thisTrip[1]==-1) return false;
    	
    	return true;
    }
    
    
    /**
     * checks if trip itrip made by individual with ID indivID having its origin recorded in lockedInTrips.
     * the function returns false if:
     * - indivID is not found in lockedInTrips
     * - itrip is not found in lockedInTrips
     * - origin of trip itrip is -1.
     * - origin of trip itrip equals to origLoc
     * the function only returns true if the origin of trip itrip not -1 and is different to origLoc.
     * @param indiv
     * @param itrip
     * @param lockedInTrips
     * @param origLoc
     * @return
     */
    private boolean isOriginLockedInToAnotherLoc(int indivID, int itrip, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips, int origLoc) {
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indivID);
    	if (thisIndivLockedTrips==null) return false;
    	int[] thisTrip = thisIndivLockedTrips.get(itrip);
    	if (thisTrip==null || thisTrip[0]==-1 || thisTrip[0]==origLoc) return false;
    
    	return true;
    }
    
    
    /**
     * checks if trip itrip made by individual with ID indivID having its destination recorded in lockedInTrips.
     * the function returns false if:
     * - indivID is not found in lockedInTrips
     * - itrip is not found in lockedInTrips
     * - destination of trip itrip is -1.
     * - destination of trip itrip equals to destLoc
     * the function only returns true if the destination of trip itrip not -1 and is different to destLoc.
     * @param indiv
     * @param itrip
     * @param lockedInTrips
     * @param destLoc
     * @return
     */
    private boolean isDestinationLockedInToAnotherLoc(int indivID, int itrip, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips, int destLoc) {
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indivID);
    	if (thisIndivLockedTrips==null) return false;
    	int[] thisTrip = thisIndivLockedTrips.get(itrip);
    	if (thisTrip==null || thisTrip[1]==-1 || thisTrip[1]==destLoc) return false;
    	
    	return true;
    }
    
    
    /**
     * 
     * @param indiv
     * @param itrip
     * @param origin
     * @param lockedInTrips
     */
    private void recordOriginToLockedInTrips(Individual indiv, int itrip, int origin, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips) {
    	int indivID = indiv.getId();
    	
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indivID);
    	if (thisIndivLockedTrips==null){  
    		thisIndivLockedTrips = new HashMap<Integer,int[]>();
    	}
    	
    	// records details of origin of the current trip (trip itrip)
    	lockOriginToSpecificTrip(thisIndivLockedTrips, itrip, origin, indiv);
    	// records details of destination of the previous trip (trip itrip-1)
    	if (itrip>0) 
    		lockDestinationToSpecificTrip(thisIndivLockedTrips, itrip-1, origin, indiv);
    	
    	// if the mode of this trip is car driver, look up for previous trip with mode car driver in this individual's diary
    	if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarDriver) {
    		int iPrevDriveTrip = getPrevDriveTrip(indiv.getTravelDiariesWeekdays(), itrip);
    		if (iPrevDriveTrip!=-1) {
    			// records details of destination of trip iPrevDriveTrip
    			lockDestinationToSpecificTrip(thisIndivLockedTrips, iPrevDriveTrip, origin, indiv);
    			// records details of origin of trip iPrevDriveTrip+1
    			lockOriginToSpecificTrip(thisIndivLockedTrips, iPrevDriveTrip+1, origin, indiv);
    		}
    	}
    	
    	lockedInTrips.put(indivID, thisIndivLockedTrips);
    }
    
    
    /**
     * 
     * @param indiv
     * @param itrip
     * @param destination
     * @param lockedInTrips
     */
    private void recordDestinationToLockedInTrips(Individual indiv, int itrip, int destination, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips) {
    	int indivID = indiv.getId();
    	
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indivID);
    	if (thisIndivLockedTrips==null){  
    		thisIndivLockedTrips = new HashMap<Integer,int[]>();
    	}
    	
    	// records details of destination of the current trip (trip itrip)
    	lockDestinationToSpecificTrip(thisIndivLockedTrips, itrip, destination, indiv);
    	
    	// records details of origin of the next trip (trip itrip+1)
    	if (itrip<indiv.getTravelDiariesWeekdays().length-1) 
    		lockOriginToSpecificTrip(thisIndivLockedTrips, itrip+1, destination, indiv);
    	
    	// if the mode of this trip is car driver, look up for next trip with mode car driver in this individuals's diary
    	if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarDriver) {
    		int iNextDriveTrip = getNextDriveTrip(indiv.getTravelDiariesWeekdays(), itrip);
    		if (iNextDriveTrip!=-1) {
    			// records details of origin for trip iNextDriveTrip
        		lockOriginToSpecificTrip(thisIndivLockedTrips, iNextDriveTrip, destination, indiv);
        		// records details of destination for trip iNextDriveTrip-1
        		lockDestinationToSpecificTrip(thisIndivLockedTrips, iNextDriveTrip-1, destination, indiv);
    		}
    	}
    	
    	lockedInTrips.put(indivID, thisIndivLockedTrips);
    }
    
    
    /**
     * 
     * @param thisIndivLockedTrips
     * @param itrip
     * @param destination
     */
    private void lockDestinationToSpecificTrip(HashMap<Integer,int[]> thisIndivLockedTrips, int itrip, int destination, Individual indiv) {
    	int[] thisTripLocs = thisIndivLockedTrips.get(itrip);
    	if (thisTripLocs==null) {// if no details is recorded for this trip 
    		thisTripLocs = new int[] {-1, destination}; // initialises it
    		// assigns destination to destination of trip itrip in the travel diary of individual indiv if it has not happened
        	if (indiv.getTravelDiariesWeekdays()[itrip][idxDest]!=destination) {
        		indiv.getTravelDiariesWeekdays()[itrip][idxDest] = destination;
        	}
    	}
    	else 
    		if (thisTripLocs[1]==-1) {// records new destination only if the current value of destination is -1. Do not change the value of origin of the current trip.
    			thisTripLocs[1] = destination;
    			// assigns destination to destination of trip itrip in the travel diary of individual indiv if it has not happened
            	if (indiv.getTravelDiariesWeekdays()[itrip][idxDest]!=destination) {
            		indiv.getTravelDiariesWeekdays()[itrip][idxDest] = destination;
            	}
    		}
    	thisIndivLockedTrips.put(itrip, thisTripLocs);
    }
    
    
    /**
     * 
     * @param thisIndivLockedTrips
     * @param itrip
     * @param origin
     */
    private void lockOriginToSpecificTrip(HashMap<Integer,int[]> thisIndivLockedTrips, int itrip, int origin, Individual indiv) {
    	int[] thisTripLocs = thisIndivLockedTrips.get(itrip);
    	if (thisTripLocs==null) {// if no details is recorded for this trip 
    		thisTripLocs = new int[] {origin, -1}; // initialises it
    		// assigns origin to origin of trip itrip in the travel diary of individual indiv if it has not happened
        	if (indiv.getTravelDiariesWeekdays()[itrip][idxOrigin]!=origin) {
        		indiv.getTravelDiariesWeekdays()[itrip][idxOrigin] = origin;
        	}
    	}
    	else 
    		if (thisTripLocs[0]==-1) {// records new origin only if the current value of origin is -1. Do not change the value of destination of the current trip.
    			thisTripLocs[0] = origin;
    			// assigns origin to origin of trip itrip in the travel diary of individual indiv if it has not happened
    			if (indiv.getTravelDiariesWeekdays()[itrip][idxOrigin]!=origin) {
    				indiv.getTravelDiariesWeekdays()[itrip][idxOrigin] = origin;
    			}
    		}
    	
    	thisIndivLockedTrips.put(itrip, thisTripLocs);
    }
    
    
    /**
     * 
     * @param travelDiary
     * @param itrip
     * @return
     */
    private int getNextDriveTrip(int[][] travelDiary, int itrip) {
    	int iNextDriveTrip = -1;
    	for (int i=itrip+1; i<=travelDiary.length-1; i++) 
    		if (travelDiary[i][idxMode]==modeCarDriver) {
    			iNextDriveTrip = i;
    			break;
    		}
    	return iNextDriveTrip;
    }
    
    
    /**
     * 
     * @param travelDiary
     * @param itrip
     * @return
     */
    private int getPrevDriveTrip(int[][] travelDiary, int itrip) {
    	int iPrevDriveTrip = -1;
    	for (int i=itrip-1; i>=0; i--) 
    		if (travelDiary[i][idxMode]==modeCarDriver) {
    			iPrevDriveTrip = i;
    			break;
    		}
    	return iPrevDriveTrip;
    }
    
    
    /**
     * 
     * @param indiv
     * @param itrip
     * @param lockedInTrips
     * @param household
     * @param dwellingAllocator
     * @return
     */
    private int getLocationForOrigin(Individual indiv, int itrip, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips, 
    		Household household, DwellingAllocator dwellingAllocator) {
    	
    	int origLoc = -1;
    	
    	// checks if the origin for this trip has been assigned/locked in
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indiv.getId());
    	if (thisIndivLockedTrips!=null && thisIndivLockedTrips.get(itrip)!=null && thisIndivLockedTrips.get(itrip)[0]!=-1) {
    		origLoc = thisIndivLockedTrips.get(itrip)[0]; // the origin of this trip of this individual has been assigned, return the assigned/locked-in value.
//    		writeToOrigLocCSV(household, indiv, itrip, origLoc, "origAlreadyAssigned");
    	} else { 
    		if (itrip==0) {// if this is origin for the first trip, always sets it to home location of the household
        		origLoc = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getActivityLocation();
//        		writeToOrigLocCSV(household, indiv, itrip, origLoc, "firstTrip");
    		}else { // this origin is not for the first trip
        		// checks if destination of the previous trips was assigned/lockedin
        		if (thisIndivLockedTrips!=null && thisIndivLockedTrips.get(itrip-1)!=null && thisIndivLockedTrips.get(itrip-1)[1]!=-1) { 
    				origLoc = thisIndivLockedTrips.get(itrip-1)[1];
//    				writeToOrigLocCSV(household, indiv, itrip, origLoc, "fromDestPrevTrip");
        		} else {
    				int locationType = indiv.getTravelDiariesWeekdays()[itrip][idxOrigin];
    	    		if (locationType==locationTypeHome) {  
    	    			// sets the origin to the home location of the household
    	    			origLoc = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getActivityLocation();
//    	    			writeToOrigLocCSV(household, indiv, itrip, origLoc, "locationTypeHome");
    	    		} else { 
    	    			// assigns a location to the destination of the previous trip, which is then assigned to be origin of this trip
        				origLoc = getLocationForDestination(indiv, itrip-1, lockedInTrips, household, dwellingAllocator);
//        				writeToOrigLocCSV(household, indiv, itrip, origLoc, "createDestPrevTrip");
    	    		}
    			}
        	}
    	}
    	indiv.getTravelDiariesWeekdays()[itrip][idxOrigin] = origLoc;
    	
    	// records new locations to lockedInTrips
    	recordOriginToLockedInTrips(indiv, itrip, origLoc, lockedInTrips);
    	
    	return origLoc;
    }
    
//    private void writeToOrigLocCSV(Household household, Individual indiv, int itrip, int origLoc, String note) {
//    	String outStr = String.valueOf(household.getId()) + "," + 
//        		String.valueOf(indiv.getId()) + "," +
//    			String.valueOf(indiv.getTravelDiariesWeekdays().length) + "," + 
//				String.valueOf(itrip+1) + "," + 
//				String.valueOf(origLoc) + "," +
//        		String.valueOf(indiv.getTravelDiariesWeekdays()[itrip][idxPurpose]) + "," +
//        		String.valueOf(indiv.getTravelDiariesWeekdays()[itrip][idxMode]) + "," +
//				note;
//        TextFileHandler.writeToText("origLoc.csv", outStr, true);
//    }
    
    /**
     * 
     * @param indiv
     * @param itrip
     * @param lockedInTrips
     * @param household
     * @param dwellingAllocator
     * @return
     */
    private int getLocationForDestination(Individual indiv, int itrip, HashMap<Integer,HashMap<Integer,int[]>> lockedInTrips,
    		Household household, DwellingAllocator dwellingAllocator) {
    	
    	// gets the origin of this trip if it is locked in
    	int origThisTrip = -1;
    	if (isOriginLockedIn(indiv.getId(), itrip, lockedInTrips)) { 
    		origThisTrip = lockedInTrips.get(indiv.getId()).get(itrip)[0];
    	}
    	// gets the destination of next trip if it is locked in
    	int destNextTrip = -1;
    	if (itrip<indiv.getTravelDiariesWeekdays().length-1 && isDestinationLockedIn(indiv.getId(), itrip+1, lockedInTrips)){
    		destNextTrip = lockedInTrips.get(indiv.getId()).get(itrip+1)[1];
    	}
    	
    	int destLoc = -1; 
    	
    	// checks if the destination of this trip has been assigned/locked in
    	HashMap<Integer,int[]> thisIndivLockedTrips = lockedInTrips.get(indiv.getId());
    	if (thisIndivLockedTrips!=null && thisIndivLockedTrips.get(itrip)!=null && thisIndivLockedTrips.get(itrip)[1]!=-1) {
    		destLoc = thisIndivLockedTrips.get(itrip)[1];
//    		writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "destAlreadyAssigned");
    	} else 
    		if (itrip==indiv.getTravelDiariesWeekdays().length-1) {// if this is the last trip
        		// destination is home location of the household
        		destLoc = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getActivityLocation();
//        		writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "finalTrip");
    		} else 
        		// checks if the origin of the next trip has been recorded in lockedInTrips
        		if (thisIndivLockedTrips!=null && thisIndivLockedTrips.get(itrip+1)!=null && thisIndivLockedTrips.get(itrip+1)[0]!=-1) {
        			destLoc = thisIndivLockedTrips.get(itrip+1)[0];
//        			writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "fromNextTripOrig");
        		} else {
        			int locationType = indiv.getTravelDiariesWeekdays()[itrip][idxDest];
            		if (locationType==locationTypeHome) {
            			destLoc = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getActivityLocation();
//            			writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "locationTypeHome");
            		} else {
            			// if the purpose is Work, then uses JTW to determine the destination TZ and randomly pick an activity location 
            			// in this destination travel zone.
                        if (indiv.getTravelDiariesWeekdays()[itrip][idxPurpose]==purposeWork) {

                            int oTZThisTrip = 0;

                            if ((indiv.getTravelDiariesWeekdays()[itrip][idxOrigin] < getMinActivityLocID())
                                    || (indiv.getTravelDiariesWeekdays()[itrip][idxOrigin] > getMaxActivityLocID())) 
                                oTZThisTrip =  dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getTravelZoneId();
                            else 
                                oTZThisTrip = hmActLocIDTravelZone.get(indiv.getTravelDiariesWeekdays()[itrip][idxOrigin]);
                            
                            destLoc = getActivityLocationBasedOnJTW(oTZThisTrip, indiv.getTravelDiariesWeekdays()[itrip][idxMode]);

//                            writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "getActivityLocationBasedOnJTW");
                            
                            if (destLoc==-1) {
                                destLoc = getLocationIDDestination(locationType, itrip, indiv.getTravelDiariesWeekdays(), dwellingAllocator, household, 
                                		origThisTrip, destNextTrip);
//                                writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "getLocIDDest");
                            }
                        } else {
                        	// if the purpose is Shopping and the travel mode is car driver
                        	if (indiv.getTravelDiariesWeekdays()[itrip][idxPurpose]==purposeShopping && indiv.getTravelDiariesWeekdays()[itrip][idxMode]== modeCarDriver) {

                        		int randInt = HardcodedData.random.nextInt(1000);

                        		if (randInt<=HardcodedData.pEastGardens) {
                        			int locidx = HardcodedData.random.nextInt(HardcodedData.eastGardenShoppingLocations.length);
                        			destLoc = HardcodedData.eastGardenShoppingLocations[locidx];
                        		} else if (randInt<=HardcodedData.pRandwickPlaza) {
                        			int locidx = HardcodedData.random.nextInt(HardcodedData.randwickPlazaShoppingLocations.length);
                        			destLoc = HardcodedData.randwickPlazaShoppingLocations[locidx];
                        		} else if (randInt<=HardcodedData.pStocklandsMaroubra) {
                        			int locidx = HardcodedData.random.nextInt(HardcodedData.stocklandsMaroubraShoppingLocations.length);
                        			destLoc = HardcodedData.stocklandsMaroubraShoppingLocations[locidx];
                        		} else {
                        			destLoc = getLocationIDDestination(locationType, itrip, indiv.getTravelDiariesWeekdays(), dwellingAllocator, household, 
                        					origThisTrip, destNextTrip);
//                        			writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "getLocIDDest");
                        		}
                        	} else {
                        		destLoc = getLocationIDDestination(locationType, itrip, indiv.getTravelDiariesWeekdays(), dwellingAllocator, household, 
                        					origThisTrip, destNextTrip);
//                        		writeToDestLocCSV(household, indiv, itrip, origThisTrip, destLoc, "getLocIDDest");
                        	}
                        }
            		}
        		}
    	indiv.getTravelDiariesWeekdays()[itrip][idxDest] = destLoc;

    	// records new locations to lockedInTrips 
    	recordDestinationToLockedInTrips(indiv, itrip, destLoc, lockedInTrips);
    	
    	return destLoc;
    }
    
    
//    private void writeToDestLocCSV(Household household, Individual indiv, int itrip, int origThisTrip, int destLoc, String note) {
//    	String outStr = String.valueOf(household.getId()) + "," + 
//        		String.valueOf(indiv.getId()) + "," + 
//    			String.valueOf(indiv.getTravelDiariesWeekdays().length) + "," + 
//				String.valueOf(itrip+1) + "," + 
//        		String.valueOf(origThisTrip) + "," + 
//				String.valueOf(destLoc) + "," +
//        		String.valueOf(indiv.getTravelDiariesWeekdays()[itrip][idxPurpose]) + "," +
//        		String.valueOf(indiv.getTravelDiariesWeekdays()[itrip][idxMode]) + "," +
//				note;
//        TextFileHandler.writeToText("destLoc.csv", outStr, true);
//    }
    
    /**
     * 
     * @param coTravellers
     * @param household
     * @return
     */
    private ArrayList<int[]> getCarDriverIDs(ArrayList<int[]> coTravellers, Household household) {
		ArrayList<int[]> carDriverIDs = new ArrayList<int[]>();
		for (int[] thisTravDetails : coTravellers) {
			int indivID = thisTravDetails[0];
			int itrip = thisTravDetails[1];
			Individual indiv = household.getIndividualInHholdByID(indivID);
			if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarDriver)
				carDriverIDs.add(thisTravDetails);
		}
		return carDriverIDs;
	}
	
    
    /**
     * 
     * @param coTravellers
     * @param household
     * @return
     */
	private ArrayList<int[]> getCarPassengersIDs(ArrayList<int[]> coTravellers, Household household) {
		ArrayList<int[]> carPasIDs = new ArrayList<int[]>();
		for (int[] thisTravDetails : coTravellers) {
			int indivID = thisTravDetails[0];
			int itrip = thisTravDetails[1];
			Individual indiv = household.getIndividualInHholdByID(indivID);
			if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarPassenger)
				carPasIDs.add(thisTravDetails);
		}
		return carPasIDs;
	}
    
    
	/**
	 * 
	 * @param coTravellers
	 * @param household
	 * @return
	 */
	private ArrayList<int[]> getOtherTripIDs(ArrayList<int[]> coTravellers, Household household) {
		ArrayList<int[]> otherTripIDs = new ArrayList<int[]>();
		for (int[] thisTravDetails : coTravellers) {
			int indivID = thisTravDetails[0];
			int itrip = thisTravDetails[1];
			Individual indiv = household.getIndividualInHholdByID(indivID);
			if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]!=modeCarPassenger &&
					indiv.getTravelDiariesWeekdays()[itrip][idxMode]!=modeCarDriver)
				otherTripIDs.add(thisTravDetails);
		}
		return otherTripIDs;
	}
	
	
    /**
     * 
     * @param departDurationTimeMap
     * @param household
     * @return
     */
    private int getYoungestCarPassengerInHousehold(HashMap<String,ArrayList<int[]>> departDurationTimeMap, Household household) {
		int youngestPasID = -1;
		Iterator<ArrayList<int[]>> itCoTravellers = departDurationTimeMap.values().iterator();
		while (itCoTravellers.hasNext()) {
			ArrayList<int[]> coTravellers = itCoTravellers.next();
			for (int[] thisTravDetails : coTravellers) {
				int indivID = thisTravDetails[0];
				int itrip = thisTravDetails[1];
				Individual indiv = household.getIndividualInHholdByID(indivID);
				if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]!=modeCarPassenger) continue; 
				if (youngestPasID==-1) youngestPasID = indivID;
				else {
					int youngestAge = household.getIndividualInHholdByID(youngestPasID).getAge();
					if (indiv.getAge()<youngestAge) youngestPasID = indivID;
				}
			}
		}
		
		return youngestPasID;
	}
    
    
    /**
     * searches for youngest passenger in a given group of cotravellers
     * @param coTravellers
     * @param household
     * @return
     */
    private int[] getYoungestPassengerInCoTravellingGroup(ArrayList<int[]> coTravellers, Household household) {
    	int[] youngestPasDetails = new int[] {-1, -1};
    	for (int[] thisTravDetails : coTravellers) {
    		int indivID = thisTravDetails[0];
    		int itrip = thisTravDetails[1];
    		Individual indiv = household.getIndividualInHholdByID(indivID);
    		if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarPassenger) 
    			if (youngestPasDetails[0]==-1) {
    				youngestPasDetails[0] = indivID;
    				youngestPasDetails[1] = itrip;
    			} else {
    				int youngestAge = household.getIndividualInHholdByID(youngestPasDetails[0]).getAge();
    				if (indiv.getAge()<youngestAge) {
    					youngestPasDetails[0] = indivID;
        				youngestPasDetails[1] = itrip;
    				}
    			}
    	}
    	return youngestPasDetails;
    }
    
    
    /**
     * Groups all individuals having same departure time and duration (aka trip time) in a household by using hash map with the key is "departure time" and "duration time"
     * @param household
     * @return
     */
    private HashMap<String, ArrayList<int[]>> getGroupsOfCotravelling(Household household) {
    	HashMap<String, ArrayList<int[]>> departDurationTimeMap = new HashMap<String, ArrayList<int[]>>();
		
		for (Individual individual : household.getResidents()) {
			/* skip individuals without travel diary */
			if (individual.getTravelDiariesWeekdays() == null) 
				continue;
			
			/* search each trip in the travel diary of an individual */
			for (int i = 0; i < individual.getTravelDiariesWeekdays().length; i++) {
				//NOT search trips that does NOT travel (stay at home)  
				if (individual.getTravelDiariesWeekdays()[i][idxOrigin]==-1 //origin
						|| individual.getTravelDiariesWeekdays()[i][idxDest]==-1 //destination
						|| individual.getTravelDiariesWeekdays()[i][idxDepTime]==-1 //start_time 
						|| individual.getTravelDiariesWeekdays()[i][idxArrTime]==-1 //end_time
						|| individual.getTravelDiariesWeekdays()[i][idxTripTime]==-1 //duration 
						|| individual.getTravelDiariesWeekdays()[i][idxMode]==-1 ) {//travel_mode
					continue;
				}
				String departDurationTime = String.valueOf(individual.getTravelDiariesWeekdays()[i][idxDepTime]) + "_" + String.valueOf(individual.getTravelDiariesWeekdays()[i][idxTripTime]);
				int[] idIndexIndividual = new int[] {individual.getTravelDiariesWeekdays()[i][idxIndivID], i};

				// group all individuals having same departure time and duration
				ArrayList<int[]> idIndexList = new ArrayList<int[]>();
				if (departDurationTimeMap.get(departDurationTime) != null) 
					idIndexList = departDurationTimeMap.get(departDurationTime);
				idIndexList.add(idIndexIndividual);
				departDurationTimeMap.put(departDurationTime,idIndexList);
			}
		}
		
		return departDurationTimeMap;
    }
    
    
    /**
     * extracts from allCoTravGroups groups of cotravelling trips that have at least a car driver and car passenger.
     * these groups (after saved to the return map) are removed from the allCoTravGroups.
     * @param allCoTravGroups
     * @param household
     * @return
     */
    private HashMap<String, ArrayList<int[]>> getDriverPaxCoTravellingGroups(HashMap<String, ArrayList<int[]>> allCoTravGroups, Household household) {
    	HashMap<String, ArrayList<int[]>> driverPaxCoTravGroups = new HashMap<String, ArrayList<int[]>>();
    	Iterator<String> itCoTravellersKey = allCoTravGroups.keySet().iterator();
    	while (itCoTravellersKey.hasNext()) {
    		boolean hasPassenger = false;
    		boolean hasDriver = false;
    		String key = itCoTravellersKey.next();
    		ArrayList<int[]> coTravellers = allCoTravGroups.get(key);
			for (int[] thisTravDetails : coTravellers) {
				int indivID = thisTravDetails[0];
				int itrip = thisTravDetails[1];
				Individual indiv = household.getIndividualInHholdByID(indivID);
				if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarPassenger) hasPassenger = true;
				if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarDriver) hasDriver = true;
			}
			if (hasPassenger && hasDriver) {
				driverPaxCoTravGroups.put(key,coTravellers);
				itCoTravellersKey.remove();
			}
    	}
    	return driverPaxCoTravGroups;
    }
    
    
    /**
     * extracts from allCoTravGroups groups of cotravelling trips that have more than 1 trip, no matter what the mode of these trips are.
     * these groups (after saved to the return map) are removed from the allCoTravGroups.
     * @param allCoTravGroups
     * @param household
     * @return
     */
    private HashMap<String, ArrayList<int[]>> getOtherCoTravellingGroups(HashMap<String, ArrayList<int[]>> allCoTravGroups, Household household) {
    	HashMap<String, ArrayList<int[]>> otherCoTravGroups = new HashMap<String, ArrayList<int[]>>();
    	Iterator<String> itCoTravellersKey = allCoTravGroups.keySet().iterator();
    	while (itCoTravellersKey.hasNext()) {
    		String key = itCoTravellersKey.next();
    		if (allCoTravGroups.get(key).size()>1) {
    			otherCoTravGroups.put(key, allCoTravGroups.get(key));
    			itCoTravellersKey.remove();
    		}
    	}
		
    	return otherCoTravGroups;
    }
    
    
    /**
     * Locate the location for 1 trip depends on the type of location ("origin"
     * or "destination") and householdID.
     *
     * @param matrixRow
     * @param household
     * @param locationType
     * @param findPurpose
     * @param crnTravDiaries
     * @param dwellingAllocator
     * @return the location ID in Integer depends on the type of location
     *         ("origin" or "destination")
     *
     * @author Vu Lam Cao
     */
    private int getLocationID(int matrixRow, Household household,
            int locationType, String findPurpose,
            int[][] crnTravDiaries, DwellingAllocator dwellingAllocator) {


        // activitylocation's columns are:
        // [facility_id][activity_ID][type][hospot_id][note_bus][note_train]
        // [car_space]

        int locationID = dwellingAllocator.getDwellingPool().getDwellings()
                .get(household.getDwellingId()).getActivityLocation();
        if (traceLogger.isTraceEnabled()) {
            traceLogger.trace("locationID" + locationID);
        }

        // A - get the location base on the household_id of the household location database and the type "origin"
        if (findPurpose.contains("origin")) {
            // when the origin location is "home", select the location of the household in database
            if (locationType == 0) {
                locationID = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getActivityLocation();

                if (traceLogger.isTraceEnabled()) {
                    traceLogger.trace("A - when the origin location is home, select the location of the household in database. locationID" + locationID);
                    traceLogger.trace("Travel Diaries Travel Mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
                }
            }
            // when the location is NOT "home" and the trip is the first trip, select the location of the household in database
            else if (matrixRow == 0) {
                locationID = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getActivityLocation();

                if (traceLogger.isTraceEnabled()) {
                    traceLogger.trace("when the location is NOT home and the trip is the first trip,look up the location in the activityLocation database by type to retrieve back the location ID. locationID" + locationID);
                    traceLogger.trace("Travel Diaries Travel Mode: "+ crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
                }
            }
            // when the location is NOT "home" and because of its "origin" type, the location must be the same with the "destination" location of the previous row
            else {
                locationID = crnTravDiaries[matrixRow - 1][TravelDiaryColumns.Destination_Col.getIntValue()];
                if (traceLogger.isTraceEnabled()) {
                    traceLogger.trace("when the location is NOT home and because of its origin type, the location must be the same with the destination location ofthe previous row. locationID" + locationID);
                    traceLogger.trace("Travel Diaries Travel Mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
                }
            }
        }
        /* B - when the location's type is "destination" */
        else {
            // when the destination location is "home", select the location of the household in database
            if (locationType == 0) {
                locationID = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getActivityLocation();

                if (traceLogger.isTraceEnabled()) {
                    traceLogger.trace("B - when the destination location is home, select the location of the household in database. locationID" + locationID);
                    traceLogger.trace("Travel Diaries Travel Mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
                }
            }

            else {
                // when the destination location is NOT "home", and it is the first trip, select the location of the household in database
                if (matrixRow == crnTravDiaries.length - 1) 
                    locationID = household.getDwellingId();
                
                // when the destination location is NOT "home", and not the last trip, look up it in the activityLocation database by "type" to retrieve back the location ID
                else {

                    int purposeWork = 1; // this is the value for 'Work' in the column 'purpose' in the attribute travelDiariesWeekdays of class Individual.
                    int purposeShopping = 2; // this is the value for 'Shopping' in the column 'purpose' in the attribute travelDiariesWeekdays of class Individual.

                    // if the purpose is Work, then uses JTW to determine the destination TZ and randomly pick an activity location in this destination travel zone.
                    if (crnTravDiaries[matrixRow][TravelDiaryColumns.Purpose_Col.getIntValue()]==purposeWork) {

                        int oTZThisTrip = 0;

                        if ((crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()] < getMinActivityLocID())
                                || (crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()] > getMaxActivityLocID())) 
                            oTZThisTrip =  dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getTravelZoneId();
                        else 
                            oTZThisTrip = hmActLocIDTravelZone.get(crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()]);
                        

                        locationID = getActivityLocationBasedOnJTW(oTZThisTrip, crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);

                        if (locationID==-1) 
                            locationID = getLocationIDDestination(locationType, matrixRow, crnTravDiaries, dwellingAllocator, household, -1, -1);
                    }
                    // if the purpose is Shopping and the travel mode is car driver
                    else if (crnTravDiaries[matrixRow][TravelDiaryColumns.Purpose_Col.getIntValue()]==purposeShopping
                            && crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]== TravelModes.CarDriver.getIntValue()) {

                        int randInt = HardcodedData.random.nextInt(1000);

                        if (randInt<=HardcodedData.pEastGardens) {
                            int locidx = HardcodedData.random.nextInt(HardcodedData.eastGardenShoppingLocations.length);
                            locationID = HardcodedData.eastGardenShoppingLocations[locidx];
                        } else if (randInt<=HardcodedData.pRandwickPlaza) {
                            int locidx = HardcodedData.random.nextInt(HardcodedData.randwickPlazaShoppingLocations.length);
                            locationID = HardcodedData.randwickPlazaShoppingLocations[locidx];
                        } else if (randInt<=HardcodedData.pStocklandsMaroubra) {
                            int locidx = HardcodedData.random.nextInt(HardcodedData.stocklandsMaroubraShoppingLocations.length);
                            locationID = HardcodedData.stocklandsMaroubraShoppingLocations[locidx];
                        } else
                            locationID = getLocationIDDestination(locationType, matrixRow, crnTravDiaries, dwellingAllocator, household, -1, -1);
                    } else {
                        locationID = getLocationIDDestination(locationType, matrixRow, crnTravDiaries, dwellingAllocator, household, -1, -1);
                    }

                    if (traceLogger.isTraceEnabled()) {
                        traceLogger.trace("when the destination location is NOT home, look up it in theactivityLocation database by type to retrieve back the locationID. locationID"+ locationID);
                        traceLogger.trace("Travel Diaries Travel Mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
                    }
                }
            }
        }

        return locationID;
    }

    /**
     * When the Destination location is NOT "home", or the Origin location is
     * NOT "home" and the current trip is the first trip, look up the location
     * in the activityLocation database by "type" to retrieve back the location
     * ID.
     *
     * @param locationType
     * @param matrixRow
     * @param crnTravDiaries
     * @param dwellingAllocator
     * @param household
     * @return
     */

    private int getLocationIDDestination(int locationType, int matrixRow, int[][] crnTravDiaries, DwellingAllocator dwellingAllocator, Household household,
    		int origThisTrip, int destNextTrip) {

        // activitylocation's columns are:
        // [facility_id][activity_ID][type][hospot_id][note_bus][note_train]
        // [travel_zone] [note_entry]

        int locationID = 0;
        ArrayList<Integer> avaliableLocationIds = new ArrayList<>();
        boolean foundCarPark = false;
        int foundParkLoc[] = new int[2];
        ParkingManager parkingManager = ParkingManager.getInstance();

        // get a List of available Locations associated with the location
        // type/facility type
        // TODO this could be optimized if the activity locations were retrieved
        // from the DB by locationType.
        for (int[] activitylocation : getActivityLocation()) {
            if (activitylocation[2] == locationType) {
                avaliableLocationIds.add(activitylocation[1]);
            }
        }

        // when cannot find any location matching with the location type,
        // pick up one location that closet to the origin location
        if (avaliableLocationIds.isEmpty()) {
            int closestLocationID = chooseNearestLocFomPreviousLoc(crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()], locationType,
                    dwellingAllocator, household);
            avaliableLocationIds.add(closestLocationID);

        }

        // when the mode of this trip is car_driver,
        // ==> car_driver will check and choose the location with
        // available car space. If there are more than 2 matching locations,
        // car_driver will pick the closet location to the origin location
        int currTravelMode = crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()];

        if (currTravelMode == TravelModes.CarDriver.getIntValue()) {
            traceLogger
            .trace("A1 - if there are some available locations, car_driver will check and choose the location with available car space."
                    +
                    "If there are more than 2 matching locations, car_driver will pick the closet location to the origin location");

            double shortestDistance = Double.MAX_VALUE;
            int closestParkingLoc = 0;
            if (traceLogger.isTraceEnabled()) {
                traceLogger.trace("travel mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
            }

            // pick up the closest available location with consideration
            // about its car space before go to that location

            for (Integer anAvaliableLocation : avaliableLocationIds) {
                // search for available location's car space to choose it

                foundParkLoc = parkingManager.searchCarPark(anAvaliableLocation, crnTravDiaries[matrixRow]);

                // if there are matching locations with available parking,
                // calculate all distance of matching locations in order to
                // pick up the closest one
                if (foundParkLoc[0] > 0) {
                    Point2D.Double xyCordinator = new Point2D.Double();
                    int availableLocID = foundParkLoc[1];

                    if ((crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()] < getMinActivityLocID())
                            || (crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()] > getMaxActivityLocID())) {
                        try {
                            xyCordinator.setLocation(dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getxCoord(),
                                    dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId()).getyCoord());
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    } else {
                        try {
                            xyCordinator.setLocation(
                                    getHmActivityLocCordinators().get(crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()])[0],
                                    getHmActivityLocCordinators().get(crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()])[1]);
                        } catch (Exception e) {
                            logger.error(e);
                        }

                    }

                    // choose closest distance location
                    double distance = xyCordinator.distance(getHmActivityLocCordinators().get(availableLocID)[0],
                            getHmActivityLocCordinators().get(availableLocID)[1]);

                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        locationID = foundParkLoc[0];
                        closestParkingLoc = foundParkLoc[1];
                        foundCarPark = true;
                    }
                }
            }

             // when there is an available car park, put into Proceess_Link_2 value:
             // [from: locationID, to: closestParkingLoc]
            if (foundParkLoc[0] > 0) {
            	// only put activity location that is not Home location
                if (locationID >= ActivityLocationManager.getInstance().getMinActivityLocID()
                        && (locationID <= ActivityLocationManager.getInstance().getMaxActivityLocID())) {

                	ModelMain.getInstance().getProcessLink().set((locationID - 1),closestParkingLoc);
                    logger.trace("Location ID of hhold_person_trip, " +
                            crnTravDiaries[matrixRow][TravelDiaryColumns.HouseholdID_Col.getIntValue()] + "_" +
                            crnTravDiaries[matrixRow][TravelDiaryColumns.IndividualID_Col.getIntValue()] + "_" +
                            crnTravDiaries[matrixRow][TravelDiaryColumns.TripID_Col.getIntValue()] +
                            " ,after searching car park is: " + locationID);
                }
            }
            traceLogger.trace("travel mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);

            // // when there is no location with available car park,
            // // car_driver has to change to public transportation
            if (!foundCarPark) {

                traceLogger.trace("A2 - if there is no location with available car park, car_driver has to change to public transportation");

                int[] newVehicleIDTravelMode = changeToPublicTransport();

                crnTravDiaries[matrixRow][TravelDiaryColumns.VehicleID_Col.getIntValue()] = newVehicleIDTravelMode[0];
                crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()] = currTravelMode = newVehicleIDTravelMode[1];

                locationID = chooseNearestLocFomPreviousLoc(crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()], locationType, dwellingAllocator, household);

                traceLogger.trace("travel mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);

            }

        }
        // when the mode of this trip is NOT car_driver,
        // choose the destination's location that is nearest to its origin
        else {
            locationID = chooseNearestLocFomPreviousLoc(crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()], locationType, dwellingAllocator, household);

            if (traceLogger.isTraceEnabled()) {
                traceLogger.trace("travel mode: " + crnTravDiaries[matrixRow][TravelDiaryColumns.TravelMode_Col.getIntValue()]);
            }
        }

        if (locationID == 0) {
        	int randomLocation = HardcodedData.random.nextInt(getActivityLocation().length);
        	locationID = getActivityLocation()[randomLocation][1];
        }
        
        //FIXME
        // there needs to be a check here to ensure that locationID (ie destination of this trip) 
        // is not the same with origin of this trip or the destination of next trip.
        // if (locationID == 0 || locationID==origThisTrip || locationID==destNextTrip) {
        while (locationID==origThisTrip || locationID==destNextTrip) {
        	int randomLocation = HardcodedData.random.nextInt(getActivityLocation().length);
        	locationID = getActivityLocation()[randomLocation][1];
//        	TextFileHandler.writeToText("randomLocations.csv", 
//        			String.valueOf(household.getId()) + "," + String.valueOf(crnTravDiaries.length) + "," + String.valueOf(matrixRow+1) + "," +
//        					String.valueOf(crnTravDiaries[matrixRow][TravelDiaryColumns.Origin_Col.getIntValue()]) + "," + String.valueOf(locationID), 
//        			true);
        }

        return locationID;
    }

    /**
     *
     * @param originID
     * @param locationType
     * @param dwellingAllocator
     * @param household
     * @return location ID of the closest location with the input location
     *
     * @author vlcao
     */
    private int chooseNearestLocFomPreviousLoc(int originID, int locationType,
            DwellingAllocator dwellingAllocator, Household household) {

        int locationID = 0;
        double shortestDistance = 9999999;
        ArrayList<double[]> searchValue = new ArrayList<>();
        boolean flagFound = false;

        int[][] activityLocations = getActivityLocation();
        int numberActivityLocations = activityLocations.length;

        // choose the nearest location from the previous location
        if (originID != 0) {
            double[] xyOrigin = getCoorOfActLocID(originID, dwellingAllocator,
                    household);

            // activityLocation's columns are:
            // [facility_id][activity_ID][type][hospot_id][note_bus][note_train]

            // find all the locations that have the same type with input
            // locationType
            for (int[] activityLocation : activityLocations) {
                double[] availableLocation = new double[3];

                if (locationType == activityLocation[2]) {
                    availableLocation[0] = activityLocation[1];
                    availableLocation[1] = getHmActivityLocCordinators().get(activityLocation[1])[0];
                    availableLocation[2] = getHmActivityLocCordinators().get(activityLocation[1])[1];

                    searchValue.add(availableLocation);
                    flagFound = true;
                }
            }

            // when there are matching locations, choose the nearest location
            // to origin location based distance calculated from x,y
            // coordinators
            if (flagFound) {
                for (double[] availableLocation : searchValue) {
                    // availableLocation's columns are:
                    // [activity_ID][x_coord][y_coord]

                    double distance = getDistance(xyOrigin[0], xyOrigin[1],
                            availableLocation[1], availableLocation[2]);
                    
                    //FIXME
                    // if the distance is smaller than 0.1m, the availableLocation is at practically the same location of the xyOrigin
                    // ignores this and move on to the next location in seachValue
                    if (distance<0.1) {
                    	continue;
                    }
                    
                    // pick up the shortest distance
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        locationID = (int) availableLocation[0];
                    }
                }

            }
            // randomly pick up one location when there is no matching location
            else {
                int randomLocation = HardcodedData.random
                        .nextInt(numberActivityLocations);
                locationID = activityLocations[randomLocation][1];
            }
        }

        /* =========================================================== */
        if (locationID == 0) {
            int randomLocation = HardcodedData.random
                    .nextInt(numberActivityLocations);
            locationID = activityLocations[randomLocation][1];
        }

        return locationID;
    }

    /**
     * corrects destinations and modes of work trips following the counts of
     * trips by mode by O/D travel zones as specified in Journey To Work data.
     * The corrections are done to attribute travelDiariesWeekdays of each
     * individual in a given individual pool. Notes on the structure of
     * tripsModeTZ: - the columns are destination travel zones in the order as
     * specified in HardcodedData.travelZones. - the last column represents all
     * travel zones outside the study area. - rows are grouped into blocks of 5,
     * which is the number of valid modes in JTW data. These JTW modes are 0
     * Other, comprising modes Walk (1), Bike (7), Taxi (12) in the current
     * travel diaries 1 Tram, equivalent to mode Lightrail (4) in the current
     * travel diaries 2 Bus, equivalent to mode Bus (3) in the current travel
     * diaries 3 Car driver, equivalent to mode Car Driver (2) in the current
     * travel diaries 4 Car Passenger, equivalent to mode Car Passenger (11) in
     * the current travel diaries - Each block represents an origin travel zone.
     * These blocks are in the same order of the destination travel zones. - The
     * last block represents all travel zones outside the study area.
     *
     * @param individualPool
     *            group of individuals whose work trips are to be corrected.
     */
    public void correctTravelDiariesUsingJTW(IndividualPool individualPool) {

        int[][] tripsModeTZ = HardcodedData.getJtwODTz06();
        Map<Integer, int[]> actLocByTZ = hmTravelZoneActLocID;
        Map<Integer, Integer> tzByActLoc = hmActLocIDTravelZone;

        int purposeCol = 12; // this is the index of column 'purpose' in the
        // attribute travelDiariesWeekdays of class
        // Individual.
        int originCol = 6; // this is the index of column 'origin' in the
        // attribute travelDiariesWeekdays of class
        // Individual.
        int modeCol = 11; // this is the index of column 'travel mode' in the
        // attribute travelDiariesWeekdays of class
        // Individual.
        int destinationCol = 7; // this is the index of column 'destination' in
        // the attribute travelDiariesWeekdays of class
        // Individual.
        int travelIDCol = 0;
        int individualIDCol = 1;

        int purposeWorkTD = 1; // this is value of purpose 'Work' in the
        // attribute travelDiariesWeekdays of class
        // Individual.
        // diary

        int nModesJTW = 5; // this is the number of valid modes from JTW data

        // defines an array that converts a jtw mode to a mode in the travel
        // diary the conversion is as follow:
        // jtw mode Other (index 0): modes 'Walk', 'Bike', 'Taxi' in travel
        // diary
        // jtw mode Tram (index 1): mode 'LightRail' (value 4) in travel diary
        // jtw mode Bus (index 2): mode 'Bus' (value 3) in travel diary
        // jtw mode CarDriver (index 3): mode 'CarDriver' (value 2) in travel
        // diary
        // jtw mode CarPassenger (index 4): mode 'CarPassenger' (value 11) in
        // travel diary
        int[] modeConversionTab = new int[] { 0, 4, 3, 2, 11 };

        ArrayHandler arrHdlr = new ArrayHandler();

        for (int itz = 0; itz <= HardcodedData.travelZones.length - 1; itz++) {
            // this travel zone
            int crnTZ = HardcodedData.travelZones[itz];

            // gets work trips from this travel zone done by all individuals
            ArrayList<int[]> workTripsFromThisTZ = new ArrayList<>();
            for (Individual indiv : individualPool.getIndividuals().values()) {
                for (int iTrip = 0; iTrip <= indiv.getTravelDiariesWeekdays().length - 1; iTrip++) {
                    int purposeThisTrip = indiv.getTravelDiariesWeekdays()[iTrip][purposeCol];
                    int oTZThisTrip = tzByActLoc.get(indiv
                            .getTravelDiariesWeekdays()[iTrip][originCol]);
                    if (purposeThisTrip == purposeWorkTD && oTZThisTrip == crnTZ) {
                        workTripsFromThisTZ.add(indiv.getTravelDiariesWeekdays()[iTrip]);
                    }
                }
            }

            // gets trip counts from this travel zone by modes in JTW data
            int tmpfirstRow = itz * nModesJTW;
            int tmplastRow = tmpfirstRow + nModesJTW - 1;
            int[][] jtwCountsThisTZ = new int[nModesJTW][tripsModeTZ[0].length];
            System.arraycopy(tripsModeTZ, tmpfirstRow, jtwCountsThisTZ, 0, tmplastRow + 1 - tmpfirstRow);

            // reweights countsTripsThisTZ so that sum of all cells in
            // jtwCountsThisTZ equals the size of workTripsFromThisTZ, ie the
            // number of trips from this travel zone
            // first reweights the sum of rows
            int[] sumOfRows = new int[jtwCountsThisTZ.length];
            for (int isr = 0; isr <= sumOfRows.length - 1; isr++) {
                sumOfRows[isr] = arrHdlr.sumPositiveInArray(jtwCountsThisTZ[isr]);
            }
            if (arrHdlr.sumPositiveInArray(sumOfRows) == 0
                    && !workTripsFromThisTZ.isEmpty()) {
                sumOfRows = arrHdlr.makeUniformArray(sumOfRows.length, 1);
            }
            sumOfRows = arrHdlr.allocateProportionally(sumOfRows,
                    workTripsFromThisTZ.size());

            // now reweights each of the rows
            for (int isr = 0; isr <= sumOfRows.length - 1; isr++) {
                if (arrHdlr.sumPositiveInArray(jtwCountsThisTZ[isr]) == 0
                        && sumOfRows[isr] != 0) {
                    jtwCountsThisTZ[isr] = arrHdlr.makeUniformArray(
                            jtwCountsThisTZ[isr].length, 1);
                }
                jtwCountsThisTZ[isr] = arrHdlr.allocateProportionally(
                        jtwCountsThisTZ[isr], sumOfRows[isr]);
            }

            // reassigns destination and mode of trips in workTripsFromThisTZ
            // following the new distribution in jtwCountsThisTZ
            int idxCrnTrip = 0; // this is the index of rows in
            // workTripsFromThisTZ
            // and is increased after a trip is reassigned a
            // mode and a destination
            // First consider jtw mode 'Other' which is equally comprised of
            // Walk, Bike, Taxi
            int iMode = 0;
            for (int iDestTZ = 0; iDestTZ <= jtwCountsThisTZ[0].length - 1; iDestTZ++) { // for
                // each
                // of
                // destination
                // travel
                // zones,
                // including
                // those
                // outside
                // the
                // study
                // area

                int[] tmpnTrips = arrHdlr.allocateProportionally(new int[] { 1,
                        1, 1 }, jtwCountsThisTZ[iMode][iDestTZ]);
                int nWalkTrips = tmpnTrips[0];
                int nBikeTrips = tmpnTrips[1];
                int nTaxiTrips = tmpnTrips[2];

                // gets list of activity locations in this destination travel
                // zone
                int[] actLocsInDestTZ = null;
                if (iDestTZ == jtwCountsThisTZ[0].length - 1) {
                    actLocsInDestTZ = actLocByTZ.get(-1);
                } else {
                    actLocsInDestTZ = actLocByTZ.get(HardcodedData.travelZones[iDestTZ]);
                }
                // assigns mode 'Walk' and an activity location in this TZ as
                // destination to the current trip in workTripsFromThisTZ
                for (int iTrip = 1; iTrip <= nWalkTrips; iTrip++) {
                    // assigns mode Walk
                    workTripsFromThisTZ.get(idxCrnTrip)[modeCol] = TravelModes.Walk.getIntValue();
                    // randomly picks one of the activity locations from
                    // actLocsInDestTZ
                    int randInt = HardcodedData.random.nextInt(actLocsInDestTZ.length);
                    workTripsFromThisTZ.get(idxCrnTrip)[destinationCol] = actLocsInDestTZ[randInt];
                    // increase index of row in workTripsFromThisTZ
                    idxCrnTrip += 1;
                }
                // assigns mode 'Bike' and an activity location in this TZ as
                // destination to the current trip in workTripsFromThisTZ
                for (int iTrip = 1; iTrip <= nBikeTrips; iTrip++) {
                    // assigns mode Bike
                    workTripsFromThisTZ.get(idxCrnTrip)[modeCol] = TravelModes.Bike.getIntValue();
                    // randomly picks one of the activity locations from
                    // actLocsInDestTZ
                    int randInt = HardcodedData.random.nextInt(actLocsInDestTZ.length);
                    workTripsFromThisTZ.get(idxCrnTrip)[destinationCol] = actLocsInDestTZ[randInt];
                    // increase index of row in workTripsFromThisTZ
                    idxCrnTrip += 1;
                }
                // assigns mode 'Taxi' and an activity location in this TZ as
                // destination to the current trip in workTripsFromThisTZ
                for (int iTrip = 1; iTrip <= nTaxiTrips; iTrip++) {
                    // assigns mode Taxi
                    workTripsFromThisTZ.get(idxCrnTrip)[modeCol] = TravelModes.Taxi.getIntValue();
                    // randomly picks one of the activity locations from
                    // actLocsInDestTZ
                    int randInt = HardcodedData.random.nextInt(actLocsInDestTZ.length);
                    workTripsFromThisTZ.get(idxCrnTrip)[destinationCol] = actLocsInDestTZ[randInt];
                    // increase index of row in workTripsFromThisTZ
                    idxCrnTrip += 1;
                }
            }

            // Then considers each of other jtw modes, which are Tram, Bus, Car
            // Driver, and Car Passenger
            for (iMode = 1; iMode <= jtwCountsThisTZ.length - 1; iMode++) {
                int newMode = modeConversionTab[iMode];
                for (int iDestTZ = 0; iDestTZ <= jtwCountsThisTZ[0].length - 1; iDestTZ++) { // for
                    // each
                    // of
                    // destination
                    // travel
                    // zones,
                    // including
                    // those
                    // outside
                    // the
                    // study
                    // area
                    int nTrips = jtwCountsThisTZ[iMode][iDestTZ];

                    // gets list of activity locations in this destination
                    // travel zone
                    int[] actLocsInDestTZ = null;
                    if (iDestTZ == jtwCountsThisTZ[0].length - 1) {
                        actLocsInDestTZ = actLocByTZ.get(-1);
                    } else {
                        actLocsInDestTZ = actLocByTZ.get(HardcodedData.travelZones[iDestTZ]);
                    }

                    // assigns this jtw mode and an activity location in this TZ
                    // as destination to the current trip in workTripsFromThisTZ
                    for (int iTrip = 1; iTrip <= nTrips; iTrip++) {
                        workTripsFromThisTZ.get(idxCrnTrip)[modeCol] = newMode;
                        int randInt = HardcodedData.random
                                .nextInt(actLocsInDestTZ.length);
                        workTripsFromThisTZ.get(idxCrnTrip)[destinationCol] = actLocsInDestTZ[randInt];
                        idxCrnTrip += 1;
                    }
                }
            }

            // At this stage all work trips departing from this travel zone must
            // have been reassigned with a new mode and a new destination
            // activity location.
            // It's time to put them back into travel diaries of each
            // individual.
            int iWTrip = 0;
            while (iWTrip <= workTripsFromThisTZ.size() - 1) {
                // get all work trips belonging to one individual
                int thisIndivID = workTripsFromThisTZ.get(iWTrip)[individualIDCol];
                int itrStart = iWTrip;
                int itrEnd = iWTrip;
                for (int itr = iWTrip + 1; itr <= workTripsFromThisTZ.size() - 1; itr++) {
                    if (workTripsFromThisTZ.get(itr)[individualIDCol] != workTripsFromThisTZ
                            .get(itr - 1)[individualIDCol]) {
                        itrEnd = itr - 1;
                        break;
                    }
                }

                // gets travel diaries of this individual
                int[][] travDiary = individualPool.getByID(thisIndivID)
                        .getTravelDiariesWeekdays();

                // updates rows of work trips in travDiary
                for (int itr = itrStart; itr <= itrEnd; itr++) {
                    for (int tmpi = 0; tmpi <= travDiary.length - 1; tmpi++) {
                        if (workTripsFromThisTZ.get(itr)[travelIDCol] == travDiary[tmpi][travelIDCol]) {
                            travDiary[tmpi] = workTripsFromThisTZ.get(itr);
                            break;
                        }
                    }
                }

                individualPool.getByID(thisIndivID).setTravelDiariesWeekdays(travDiary);

                iWTrip = itrEnd + 1;
            }
        }
    }

    /**
     * gets distance between 2 points.
     *
     * @param x1 X co-ordinate of the first location.
     * @param y1 Y co-ordinate of the first location.
     * @param x2 X co-ordinate of the second location.
     * @param y2 Y co-ordinate of the second location.
     * @return the distance between 2 locations with x, y coordinates.
     */
    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Return the array of activity location IDs associated with the given zoneID.
     * @param zoneId The zone ID to look for.
     * @return Array of activity location IDs or null.
     */
    public int[] getActivityLocationsForZone(int zoneId) {
        return hmTravelZoneActLocID.get(zoneId);
    }
}
