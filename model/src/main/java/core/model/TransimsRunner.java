package core.model;

import core.locations.ActivityLocationManager;
import hibernate.postgres.JTW2006Entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import core.ContextCreator;
import core.HardcodedData;
import core.ModelMain;
import core.synthetic.household.Household;
import core.synthetic.household.Vehicle;
import core.synthetic.travel.mode.TravelModes;

public class TransimsRunner {

	private static final Logger logger = Logger.getLogger(TransimsRunner.class);

	private static TransimsThread transimsThreadWeekday;
	private static TransimsThread transimsThreadWeekend;

	// ========================================================================================================
	public TransimsRunner() {
	}

	// ========================================================================================================
	public static TransimsThread getTransimsThreadWeekday() {
		return transimsThreadWeekday;
	}

	public static void setTransimsThreadWeekday(TransimsThread transimsThreadWeekday) {
		TransimsRunner.transimsThreadWeekday = transimsThreadWeekday;
	}

	public static TransimsThread getTransimsThreadWeekend() {
		return transimsThreadWeekend;
	}

	public static void setTransimsThreadWeekend(TransimsThread transimsThreadWeekend) {
		TransimsRunner.transimsThreadWeekend = transimsThreadWeekend;
	}

	// ========================================================================================================
	/**
	 * Set all functions in all steps to let TRANSIMS run in the model.
	 * 
	 * @param stepNumber
	 *            : the order of step
	 */
	public void step(int stepNumber) {

        ModelMain main = ModelMain.getInstance();
		setTransimsThreadWeekday(new TransimsThread("weekdays/", main.getScenarioName()));
		setTransimsThreadWeekend(new TransimsThread("weekend/", main.getScenarioName()));

		if (ContextCreator.isRunTransims()) {
			logger.debug("Running transims step " + stepNumber);

			transimsThreadWeekday.setStep(stepNumber, main.getScenarioName());

			transimsThreadWeekday.start();
			// transimsThreadWeekend.start();

			try {
				transimsThreadWeekday.join();
				// transimsThreadWeekend.join();

			} catch (InterruptedException e) {
                logger.error("Exception caught", e);
			}
		}
	}

	// ========================================================================================================
	/**
	 * Add more trips from JTW data into the current trips list. There are 2
	 * kinds of added trips: 1 - "go_to_work" trips: which have Origin locations
	 * are outside study area and Destination locations are inside study area
	 * 
	 * 2 - "go_home" trips: which have Origin locations are inside study area
	 * and Destination locations are outside study area
	 * 
	 *
	 * @author vlcao
	 */
	public static List<String[]> addJTWtripsFromOutsideStudyArea() {
        ModelMain main = ModelMain.getInstance();
        List<String[]> tripListFromOutsideStudyArea = new ArrayList<>();
        
		try {
			int modeIdxInArray = 1;
			int destinationIdxInArry = 0;

			// store the trip list, vehicle list and process link
			// before add more trips from JTW data
			
			List<Vehicle> vehicleList = main.getVehicleList();

			// get the last household ID in the pool
			int lastHholdID = 0;
			for (Household household : main.getHouseholdPool().getHouseholds()
					.values()) {
				if (household.getId() > lastHholdID) {
					lastHholdID = household.getId();
                }
			}
						
			List<JTW2006Entity> jtw2006Entities = ModelMain.getInstance().getJtw2006DAO().findAll();
			
			// create trips with the data from database
			for (JTW2006Entity jtw2006Entity : jtw2006Entities) {

				int[] desMode = new int[2];
                desMode[destinationIdxInArry] = jtw2006Entity.getD_Tz06();

                switch (jtw2006Entity.getMode9()) {
                case 1:
                    desMode[modeIdxInArray] = TravelModes.Taxi.getIntValue(); 
                    break;

                case 2:
                    desMode[modeIdxInArray] = TravelModes.LightRail.getIntValue(); 
                    break;

                case 3:
                    desMode[modeIdxInArray] = TravelModes.Bus.getIntValue(); 
                    break;

                case 4:
                    desMode[modeIdxInArray] = TravelModes.CarDriver.getIntValue(); 
                    break;

                case 5:
                    desMode[modeIdxInArray] = TravelModes.CarPassenger.getIntValue(); 
                    break;

                case 6:
                    desMode[modeIdxInArray] = TravelModes.Bike.getIntValue(); 
                    break;

                case 7:
                    desMode[modeIdxInArray] = TravelModes.Walk.getIntValue(); 
                    break;

                default:
                	break;
                }


				// add more trips from JTW data
				/*
				 * randomly choose the start time: 
				 * 1- in the morning from 6AM (21600)
				 * to 9.AM (32400) for "go_to_work" trip (purpose = 1)
				 * 
				 * 2- in the afternoon from 3PM (54000) to 7PM 
				 * for "go_home" trip (purpose = 0)
				 */
                int inSecond_6AM = 21600;
                int inSecond_3PM = 54000;
                int inSecond_2hours = 2 * 3600;
                int inSecond_3hours = 3 * 3600;
                int inSecond_4hours = 4 * 3600;
                int inSecond_halfHour = 1800;
                
                int startTime = inSecond_6AM + HardcodedData.random.nextInt(inSecond_3hours);
				int endTime = startTime + inSecond_halfHour
						+ HardcodedData.random.nextInt(inSecond_2hours);

				int startTimeHome = inSecond_3PM + HardcodedData.random.nextInt(inSecond_4hours);
				int endTimeHome = startTimeHome + inSecond_halfHour
						+ HardcodedData.random.nextInt(inSecond_2hours);

				int newHholdID = lastHholdID + jtw2006Entity.getID();
				int vehicleID = 0, origin, destination;

				int[] desArr=null;

				// set up origin and destination for new trips
				int[] orgArr = ActivityLocationManager.getInstance().getActivityLocationsForZone(-1);
				origin = orgArr[HardcodedData.random.nextInt(orgArr.length)];

				// when there are no matching activity locations in the destination 
				// travel zone, randomly pick up one activity location in the study area
				if (ActivityLocationManager.getInstance().getActivityLocationsForZone(desMode[destinationIdxInArry])==null
						|| ActivityLocationManager.getInstance().getActivityLocationsForZone(desMode[destinationIdxInArry])[0]==-1) {
					
					int randomLocation = HardcodedData.random.nextInt(
                            ActivityLocationManager.getInstance().getActivityLocation().length);
					
					destination = ActivityLocationManager.getInstance().getActivityLocation()[randomLocation][1];
				}
				else {
					desArr = ActivityLocationManager.getInstance().getActivityLocationsForZone(desMode[destinationIdxInArry]);
					destination = desArr[HardcodedData.random.nextInt(desArr.length)];	
				}
				
				// set up the vehicle ID for new trips
				switch (TravelModes.classify(desMode[modeIdxInArray])) {
				case CarDriver:
					vehicleID = 1;
					
					//add a car at origin location
                    Vehicle newCar = new Vehicle();
                    newCar.setVehicleId(vehicleID);
                    newCar.sethHold(newHholdID);
                    newCar.setLocation(main.getProcessLink().get(origin));
                    newCar.setType(1);
                    newCar.setSubType(1);

					vehicleList.add(newCar);
					break;

				case Taxi:
					vehicleID = 1;

					// add a taxi at origin location
                    Vehicle newTaxi = new Vehicle();
                    newTaxi.setVehicleId(vehicleID);
                    newTaxi.sethHold(newHholdID);
                    newTaxi.setLocation(main.getProcessLink().get(origin));
                    newTaxi.setType(1);
                    newTaxi.setSubType(2);

					vehicleList.add(newTaxi);
					break;
				default:
					break;
				}

				String personID = "1";
				String goToWorkTripID = "1";
				String goToWorkTripPurposeCode = "1";
				String constraintCode = "0";
				
				/* 1 -  "go_to_work" trip */
				String[] newTrip = {
						String.valueOf(newHholdID),
						personID, 
						goToWorkTripID, 
						goToWorkTripPurposeCode,
						String.valueOf(desMode[modeIdxInArray]),
						String.valueOf(vehicleID), 
						String.valueOf(startTime),
						String.valueOf(origin), 
						String.valueOf(endTime), 
						String.valueOf(destination),
						constraintCode }; 
						

				// initial the Hash Maps
				String hhPersonTripString = newHholdID + "_" + personID + "_" + goToWorkTripID;
				main.getHmHhPersonTripTravelMode().put(hhPersonTripString, desMode[modeIdxInArray]);

				// add new trip into the trip list
				tripListFromOutsideStudyArea.add(newTrip);

				/* 2 - "go_home" trip */
				String goHomeTripID = "2";
				String goHomeTripPurposeCode = "0";
				
				String[] newTripHome = {
						String.valueOf(newHholdID),
						personID,
						goHomeTripID,
						goHomeTripPurposeCode,
						String.valueOf(desMode[modeIdxInArray]),
						String.valueOf(vehicleID),
						String.valueOf(startTimeHome),
						/* originHome is the destination of "go_to_work" trip */
						String.valueOf(destination),
						String.valueOf(endTimeHome),
						/* destinationHome is the origin of "go_to_work" trip */
						String.valueOf(origin),
						constraintCode }; 

				// initial the Hash Maps
				hhPersonTripString = newHholdID+ "_" + personID + "_" + goHomeTripID;
				main.getHmHhPersonTripTravelMode().put(hhPersonTripString, desMode[modeIdxInArray]);

				// add new trip into the trip list
				tripListFromOutsideStudyArea.add(newTripHome);

			}

			// Assign back vehicle list
			main.setVehicleList(vehicleList);

		} catch (Exception e) {
            logger.error("Exception caught", e);
		}

		return tripListFromOutsideStudyArea;
	}
}
