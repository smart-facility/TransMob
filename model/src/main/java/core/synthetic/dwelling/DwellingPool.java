package core.synthetic.dwelling;

import hibernate.postgis.TravelZonesFacilitiesDAO;
import hibernate.postgres.TransimsHouseholdLocationDAO;
import hibernate.postgres.TransimsHouseholdLocationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import jdbc.dao.DwellingCapacity;
import jdbc.dao.DwellingCapacityDAO;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

import core.ApplicationContextHolder;
import core.HardcodedData;
import core.model.TextFileHandler;
import core.synthetic.StatisticalPool;

/**
 * a class contains all dwelling information in database.
 * 
 * @author qun
 * 
 */
public class DwellingPool extends StatisticalPool {

	private final Map<Integer, List<Dwelling>> travelZoneDwellings;

	private Map<Integer, Dwelling> dwellings;

	private final Map<String, List<Dwelling>> dwellingsMap;
	private final Map<String, List<Integer>> dwellingsIDMap; 
	
    private static final Logger logger = Logger.getLogger(DwellingPool.class);
	private final TransimsHouseholdLocationDAO transimsHouseholdLocationDAO;
    private final DwellingCapacityDAO dwellingCapacityDAO;
    private final TravelZonesFacilitiesDAO travelZonesFacilitiesDAO;


	public DwellingPool() {
		super();
		this.travelZoneDwellings = new HashMap<>();
		this.transimsHouseholdLocationDAO = ApplicationContextHolder.getBean(TransimsHouseholdLocationDAO.class);
        this.dwellingCapacityDAO = ApplicationContextHolder.getBean(DwellingCapacityDAO.class);
        this.travelZonesFacilitiesDAO = ApplicationContextHolder.getBean(TravelZonesFacilitiesDAO.class);
		this.dwellings = Maps.newHashMap();
		this.dwellingsMap = Maps.newHashMap();
		this.dwellingsIDMap = Maps.newHashMap();
	}

	public static void main(String[] args) {
		DwellingPool dwellingPool = new DwellingPool();

		Map<Integer, List<TransimsHouseholdLocationEntity>> tzLocations = dwellingPool
				.getTZTransimsHouseholdLocation();

		for (int i = HardcodedData.START_YEAR; i < 2026; i++) {
			dwellingPool.initial("dwelling_test", i, HardcodedData.random, tzLocations);
		}

		for (Entry<Integer, List<Dwelling>> entry : dwellingPool
				.getTravelZoneDwellings().entrySet()) {
			logger.debug(entry.getKey() + ": " + entry.getValue().size());
		}
	}

	public void initialAll(String schema, int endYear) {
		Map<Integer, List<TransimsHouseholdLocationEntity>> tzLocations = getTZTransimsHouseholdLocation();

		for (int i = HardcodedData.START_YEAR; i <= endYear; i++) {
			initial(schema, i, HardcodedData.random, tzLocations);
			
		}
		
	}

	/**
	 * @return the travelZoneDwellings
	 */
	public Map<Integer, List<Dwelling>> getTravelZoneDwellings() {
		return this.travelZoneDwellings;
	}


	public void initial(String schema, int year, Random random,
			Map<Integer, List<TransimsHouseholdLocationEntity>> tzLocations) {

		for (int tzID : HardcodedData.travelZonesLiveable) {
			List<TransimsHouseholdLocationEntity> transimsHouseholdLocations = tzLocations.get(tzID);

            DwellingCapacity currentDwellingCapacity = this.dwellingCapacityDAO.findByTz06(year, tzID, schema);

			int oneBedroom = 0;
			int twoBedrooms = 0;
			int threeBedrooms = 0;
			int fourBedrooms = 0;
			if (year != HardcodedData.START_YEAR) {
				DwellingCapacity previousDwellingCapacity = this.dwellingCapacityDAO.findByTz06(year -1, tzID, schema);

				// logger.debug(year);
				oneBedroom = previousDwellingCapacity.getOneBedroom();
				twoBedrooms = previousDwellingCapacity.getTwoBedroom();
				threeBedrooms = previousDwellingCapacity.getThreeBedroom();
				fourBedrooms = previousDwellingCapacity.getFourBedroom();
			}
			
			if (currentDwellingCapacity==null) {
				String strout = String.valueOf(year) + "," + String.valueOf(tzID) + "," + "currentDwellingCapacity==null";
				TextFileHandler.writeToText("mytest.csv", strout, true);
			}
			else {
				int numberOfOneBedrooms = currentDwellingCapacity.getOneBedroom() - oneBedroom;
				int numberOfTwoBedrooms = currentDwellingCapacity.getTwoBedroom() - twoBedrooms;
				int numberOfThreeBedrooms = currentDwellingCapacity.getThreeBedroom() - threeBedrooms;
				int numberOfFourBedrooms = currentDwellingCapacity.getFourBedroom() - fourBedrooms;

				populateMaps(year, random, tzID, transimsHouseholdLocations, numberOfOneBedrooms, 1);
				populateMaps(year, random, tzID, transimsHouseholdLocations, numberOfTwoBedrooms, 2);
				populateMaps(year, random, tzID, transimsHouseholdLocations, numberOfThreeBedrooms, 3);
				populateMaps(year, random, tzID, transimsHouseholdLocations, numberOfFourBedrooms, 4);
			}
		}
	}

	
	private void populateMaps(int year, Random random, int tzID,
			List<TransimsHouseholdLocationEntity> transimsHouseholdLocations,
			int number, int bedrooms) {

		int size = transimsHouseholdLocations.size();
		Collections.shuffle(transimsHouseholdLocations, random);
		
		// copies dwelling IDs in dwellingsIDMap of previous year to this year.
		String prevYearKey = String.valueOf(tzID) + "@" + String.valueOf(year-1) + "@" + String.valueOf(bedrooms);
		String thisYearKey = String.valueOf(tzID) + "@" + String.valueOf(year) + "@" + String.valueOf(bedrooms);
		if (this.dwellingsIDMap.containsKey(prevYearKey)) {
			List<Integer> prevYearDwellingsID = this.dwellingsIDMap.get(prevYearKey);
			List<Integer> thisYearDwellingsID = new ArrayList<Integer>();
			for (Integer dwellingID : prevYearDwellingsID) {
				thisYearDwellingsID.add(dwellingID);
			}
			this.dwellingsIDMap.put(thisYearKey, thisYearDwellingsID);
		}
		
		if (number < 0) {// the number of dwellings having 'bedrooms' in tzID in this year is smaller than that in previous year
			// randomly picks and removes this number of dwellingIDs from this.dwellingsIDMap.get(thisYearKey)
			List<Integer> thisYearDwellingsID = new ArrayList<Integer>();
			thisYearDwellingsID = this.dwellingsIDMap.get(thisYearKey);
			for (int irm=1; irm<=Math.abs(number); irm++) {
				int randInt = HardcodedData.random.nextInt(thisYearDwellingsID.size());
				thisYearDwellingsID.remove(randInt);
			}
			this.dwellingsIDMap.put(thisYearKey, thisYearDwellingsID);
		} else {
			for (int i = 0; i < number; i++) {
	            TransimsHouseholdLocationEntity transimsHouseholdLocation = null;

				if (i < size) {
					transimsHouseholdLocation = transimsHouseholdLocations.get(i);
				} else {
					transimsHouseholdLocation = transimsHouseholdLocations.get(random.nextInt(transimsHouseholdLocations.size()));
				}

				int id = this.dwellings.size() + 1;
				Dwelling tempDwelling = new Dwelling(-1,
						transimsHouseholdLocation.getxCoord(),
						transimsHouseholdLocation.getyCoord(), bedrooms, year,
						tzID, id, transimsHouseholdLocation.getNoteBus(),
						transimsHouseholdLocation.getNoteTrain(),
						transimsHouseholdLocation.getActivityLocation(),
						this.travelZonesFacilitiesDAO.findById(tzID).getPrice(true, bedrooms));

				this.dwellings.put(id, tempDwelling);

				String key = String.valueOf(tzID) + "@" + String.valueOf(year) + "@" + String.valueOf(bedrooms);

				if (this.dwellingsMap.containsKey(key)) {
					this.dwellingsMap.get(key).add(tempDwelling);
				} else {
					List<Dwelling> tempDwellingList = new ArrayList<>();
					tempDwellingList.add(tempDwelling);
					this.dwellingsMap.put(key, tempDwellingList);
				}

				List<Integer> thisYearDwellingsID = new ArrayList<Integer>();
				if (this.dwellingsIDMap.containsKey(key)) {
					thisYearDwellingsID = this.dwellingsIDMap.get(key);
				}
				thisYearDwellingsID.add(tempDwelling.getId());
				this.dwellingsIDMap.put(key, thisYearDwellingsID);

					
				// FIXME: The below codes don't seem to be used anywhere in the model. 
				// They are commented for now but need to be removed after tests that confirm they don't affect the model in any way.
//				List<Dwelling> tempTravelZoneDwellings = this.travelZoneDwellings.get(tzID);
//				if (tempTravelZoneDwellings == null) {
//					List<Dwelling> newTravelZoneDwellings = new ArrayList<>();
//					newTravelZoneDwellings.add(tempDwelling);
//
//					this.travelZoneDwellings.put(tzID, newTravelZoneDwellings);
//				} else {
//					tempTravelZoneDwellings.add(tempDwelling);
//				}
			}
		}

	}


	@Override
	public void add(Object o) {
		// TODO Auto-generated method stub
		// add(o);
	}

	@Override
	public void remove(Object o) {
		// TODO Auto-generated method stub
		// remove(o);
	}

	@Override
	public void clearPool() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPoolNumber() {
		return this.dwellings.size();
	}

	@Override
	public Object getPoolComponent() {
		return this.dwellings;
	}

	public Map<Integer, List<TransimsHouseholdLocationEntity>> getTZTransimsHouseholdLocation() {

		Map<Integer, List<TransimsHouseholdLocationEntity>> tzLocations = new HashMap<>();

		for (int tzID : HardcodedData.travelZonesLiveable) {
			List<TransimsHouseholdLocationEntity> transimsHouseholdLocations = this.transimsHouseholdLocationDAO.findByTravelZoneId(tzID);
			if (transimsHouseholdLocations != null) {
				tzLocations.put(tzID, transimsHouseholdLocations);
			}

		}

		return tzLocations;
	}

	public Map<Integer, Dwelling> getDwellings() {
		return this.dwellings;
	}

	/**
	 * @return the dwellingsMap
	 */
	public Map<String, List<Dwelling>> getDwellingsMap() {
		return this.dwellingsMap;
	}

	public Map<String, List<Integer>> getDwellingsIDMap() {
		return this.dwellingsIDMap;
	}
	
	public Dwelling getDwellingById(int id) {
		return this.dwellings.get(id);
	}
}
