package core.preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import core.synthetic.household.Household;
import core.synthetic.household.Vehicle;
import core.synthetic.individual.Individual;
import core.synthetic.travel.mode.TravelModes;
import core.synthetic.traveldiary.TravelDiaryColumns;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.StringValueTransformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import core.HardcodedData;
import core.ModelMain;
import core.model.TextFileHandler;

public class TranSimsInputGenerator {

    private static final Logger logger = Logger.getLogger(TranSimsInputGenerator.class);
    private static final int BATCH_SIZE = 1000;
    private final ModelMain main = ModelMain.getInstance();

	/**
	 * name="tripFileLocation"
	 */
	private String tripFileLocation;
	/**
	 * name="vehicleFileLocation"
	 */
	private String vehicleFileLocation;
	/**
	 * name="personsFileLocation"
	 */
	private String personsFileLocation;
	/**
	 * name="householdsFileLocation"
	 */
	private String householdsFileLocation;
	/**
	 * name="processLinkFile2Location"
	 */
	private String processLinkFile2Location;


	public TranSimsInputGenerator(String location) {

		HardcodedData.setTripfileLocation(location);
		HardcodedData.setVehiclefileLocation(location);
		HardcodedData.setPersonsFileLocation(location);
		HardcodedData.setHouseholdsFileLocation(location);
		HardcodedData.setProcessLink2FileLocation(location);
		this.tripFileLocation = HardcodedData.getTripfileLocation();
		this.vehicleFileLocation = HardcodedData.getVehiclefileLocation();
		this.personsFileLocation = HardcodedData.getPersonsFileLocation();
		this.householdsFileLocation = HardcodedData.getHouseholdsFileLocation();
		this.processLinkFile2Location = HardcodedData
				.getProcessLink2FileLocation();

	}

	public TranSimsInputGenerator() {

	}

	/**
	 * @param tripFileLocation
	 *            name="tripFileLocation"
	 */
	public void setTripFileLocation(String tripFileLocation) {
		HardcodedData.setTripfileLocation(tripFileLocation);
		this.tripFileLocation = HardcodedData.getTripfileLocation();
	}

	public void setProcessLinkFile2Location(String proccessLinkFileLocation) {
		HardcodedData.setProcessLink2FileLocation(proccessLinkFileLocation);
		this.processLinkFile2Location = HardcodedData.getProcessLink2FileLocation();
	}

	/**
	 * @param vehicleFileLocation
	 *            name="vehicleFileLocation"
	 */
	public void setVehicleFileLocation(String vehicleFileLocation) {
		HardcodedData.setVehiclefileLocation(vehicleFileLocation);
		this.vehicleFileLocation = HardcodedData.getVehiclefileLocation();
	}

	public void setFileLocation(String fileLocation) {
		setTripFileLocation(fileLocation);
		setVehicleFileLocation(fileLocation);
		setProcessLinkFile2Location(fileLocation);
	}

	/**
	 * Generates Input files for TRANSIMS
	 * 
	 * @param weekMode
	 *            the week mode chosen, weekdays or weekend
	 */
	public void generateNewInputFiles(String weekMode, List<String[]> tripListFromOutsideStudyArea) {

		if ("weekdays".equals(weekMode)) {
			this.writeTripsList(tripListFromOutsideStudyArea);
			// } else if (weekMode == "weekend") {
			// this.writeTripsList(outputHandler.getTripsListWeekend());
		} else {
			logger.info("Invalid week mode, please choose weekdays or weekend");
		}

		this.writeVehiclesList(main.getVehicleList());
		this.writeProccessLink2(main);

	}

	public void writeTripsList(List<String[]> tripListFromOutsideStudyArea) {

        // Clear the output file first
        File output = new File(tripFileLocation);
        if (!output.delete()) {
            logger.error("Failed to delete trips file before starting to append to it");
        }

        // write all trips inside study area
        String constrainCode = "0";
        CSVWriter csvWriter = null;
        try {
        	Writer fw = new BufferedWriter(new FileWriter(tripFileLocation, true));
            csvWriter = new CSVWriter(fw, '\t', CSVWriter.NO_QUOTE_CHARACTER);

        	csvWriter.writeNext(HardcodedData.headerTripsFile);

        	// Iterates the data and write it out 
        	for (Household hhold : main.getHouseholdPool().getHouseholds().values()) {
        		int indIDInTransims = 0;
        		
        		for (Individual individual : hhold.getResidents()) {
        			indIDInTransims++;
        			
        			int[][] diaries = individual.getTravelDiariesWeekdays();
        			
        			if (diaries == null) {
        				continue;
        			}
        			
        			for (int[] diary : diaries) {
        				int tmpMode = diary[TravelDiaryColumns.TravelMode_Col.getIntValue()];
        				// if mode is car passenger, changes this mode to Other so that Transims does not create another drive trip for this passenger
        				if (tmpMode==TravelModes.CarPassenger.getIntValue()) {
        					tmpMode = TravelModes.Other.getIntValue();
        				}
        				Integer[] diaryAsObj = ArrayUtils.toObject(diary);
        				Collection<String> stringValues = CollectionUtils.collect(Arrays.asList(diaryAsObj), StringValueTransformer.getInstance());
        				String[] diaryString = stringValues.toArray(new String[stringValues.size()]);
        				
        				
        				String[] trip = {
        						diaryString[TravelDiaryColumns.HouseholdID_Col.getIntValue()] , 
        						String.valueOf(indIDInTransims),
        						diaryString[TravelDiaryColumns.TripID_Col.getIntValue()] , 
        						diaryString[TravelDiaryColumns.Purpose_Col.getIntValue()] ,
        						String.valueOf(tmpMode) ,
        						diaryString[TravelDiaryColumns.VehicleID_Col.getIntValue()] ,
        						diaryString[TravelDiaryColumns.StartTime_Col.getIntValue()] ,
        						diaryString[TravelDiaryColumns.Origin_Col.getIntValue()] , 
        						diaryString[TravelDiaryColumns.EndTime_Col.getIntValue()] ,
        						diaryString[TravelDiaryColumns.Destination_Col.getIntValue()],
        						constrainCode };
        				csvWriter.writeNext(trip);
        			
    		            
    		            /* initial the Hash Maps */
    		            String hhPersonTripTransims = diaryString[TravelDiaryColumns.HouseholdID_Col.getIntValue()] + "_" 
    		            									+ String.valueOf(indIDInTransims) + "_"  
    		            									+ diaryString[TravelDiaryColumns.TripID_Col.getIntValue()] ;
    		        	
    		            String hhPersonTripTravelDiary =  diaryString[TravelDiaryColumns.HouseholdID_Col.getIntValue()] + "_" 
								+ diaryString[TravelDiaryColumns.IndividualID_Col.getIntValue()]  + "_"  
								+ diaryString[TravelDiaryColumns.TripID_Col.getIntValue()] ;
    					
    		            main.getHmHhPersonTripTravelMode().put(hhPersonTripTransims, 
    		            		Integer.valueOf(diaryString[TravelDiaryColumns.TravelMode_Col.getIntValue()]));
    				
    					main.getHhPersonTripConvertedMap().put(hhPersonTripTransims, hhPersonTripTravelDiary);
        			}
        		}
        	}
        	// write all trips outside study area
        	csvWriter.writeAll(tripListFromOutsideStudyArea);
        	
        } catch (Exception e) {
        	logger.error("Failed to create Trips file", e);
        } finally {
        	try {
        		if (csvWriter != null) {
        			csvWriter.close();
        		}
        	} catch (Exception e) {
        		logger.error("Failed to close file", e);
        	}
        }
	}

	// ===========================================================================
	
	public void writeTripsList2CSVfile(List<Trips> trips, ModelMain main, String fileName) {

        List<String[]> tripsForCsv = new ArrayList<>();

		for (Trips trip : trips) {
            String [] columns = new String[11];

			columns[0] = trip.getHhold().toString();
			columns[1] = trip.getPerson().toString();
			columns[2] = trip.getTrip().toString();
			columns[3] = trip.getPurpose().toString();
			columns[4] = trip.getMode().toString();
			columns[5] = trip.getVehicle().toString();
			columns[6] = trip.getStart().toString();
			columns[7] = trip.getOrigin().toString();
			columns[8] = trip.getEnd().toString();
			columns[9] = trip.getDestination().toString();
			columns[10] = trip.getConstraint().toString();

            tripsForCsv.add(columns);
			
		}

		TextFileHandler.writeToCSV(System.getProperty("user.dir") + "/" + fileName,
                HardcodedData.headerTripsFile, tripsForCsv);
	}
	
	// ===========================================================================
	/**
	 * Assign all input parameter for writeTabLimited function of class
	 * TextFileHandler
	 * 
	 * @author vlcao
	 */
	public void writeProccessLink2(ModelMain main) {
        TextFileHandler.writeTabLimited(this.processLinkFile2Location,
				HardcodedData.headerProccessLink_2_File, main);
	}

	public void writeVehiclesList(List<Vehicle> vehiclesList) {

        List<String[]> vehicleAsString = new ArrayList<>(vehiclesList.size());

        for (Vehicle vehicle : vehiclesList) {
            vehicleAsString.add(vehicle.toTransimsFormat());
        }

        TextFileHandler.writeTabLimited(this.vehicleFileLocation,
				HardcodedData.headerVehiclesFile, vehicleAsString);
	}

	public void writePersonsList(List<Persons> personsList) {
        List<String[]> persons = new ArrayList<>();
		for (Persons person : personsList) {
            String [] personStrArray = new String[7];
            personStrArray[0] = person.getHhold().toString();
            personStrArray[1] = person.getPerson().toString();
            personStrArray[2] = person.getRelate().toString();
            personStrArray[3] = person.getAge().toString();
            personStrArray[4] = person.getGender().toString();
            personStrArray[5] = person.getWork().toString();
            personStrArray[6] = person.getDrive().toString();

            persons.add(personStrArray);

		}
        TextFileHandler.writeTabLimited(this.personsFileLocation,
				HardcodedData.headerPersonsFile, persons);
	}

	public void writeHouseholdsList(List<Households> householdsList) {
        List<String[]> households = new ArrayList<>();
		for (Households household : householdsList) {
            String[] houseHoldStrArray = new String[5];
            houseHoldStrArray[0] = household.getHhold().toString();
            houseHoldStrArray[1] = household.getLocation().toString();
            houseHoldStrArray[2] = household.getPersons().toString();
            houseHoldStrArray[3] = household.getWorkers().toString();
            houseHoldStrArray[4] = household.getVehicles().toString();

            households.add(houseHoldStrArray);

		}
        TextFileHandler.writeTabLimited(this.householdsFileLocation,
				HardcodedData.headerHouseholdsFile, households);
	}
}
