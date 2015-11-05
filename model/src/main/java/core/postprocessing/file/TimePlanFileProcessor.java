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
package core.postprocessing.file;

import hibernate.postgres.ActivityHotspotsStreetBlockDAO;
import hibernate.postgres.ActivityHotspotsStreetBlockEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Maps;

import core.ApplicationContextHolder;
import core.ModelMain;
import core.synthetic.HouseholdPool;
import core.synthetic.household.Household;
import core.synthetic.travel.mode.TravelModeSelection;
import core.synthetic.travel.mode.TravelModes;

/**
 * A class process time plan file.
 * 
 * @author qun
 * 
 */
public final class TimePlanFileProcessor {

	private String timePlanFilePath;

	private Map<Integer, Integer> activityIDMap;

	private Map<String, TimePlan> timePlanMap;

	private TravelModeSelection travelModeSelection;

	private static TimePlanFileProcessor timePlanFileProcessor;

	private Map<Integer, List<Double>> linksTripsTime;

    private static final Logger logger = Logger.getLogger(TimePlanFileProcessor.class);
	private final ModelMain main = ModelMain.getInstance();


	private TimePlanFileProcessor(String timePlanFile) {
		this.timePlanFilePath = timePlanFile;
		timePlanMap = Maps.newHashMap();
		activityIDMap = Maps.newHashMap();
		travelModeSelection = TravelModeSelection.getInstance();
		linksTripsTime = Maps.newHashMap();

		readInMapping();
	}

	private TimePlanFileProcessor() {
	}

	public static synchronized TimePlanFileProcessor getInstance(
			String timePlanFile) {
		if (timePlanFileProcessor == null) {
			timePlanFileProcessor = new TimePlanFileProcessor(timePlanFile);
		}

		return timePlanFileProcessor;
	}


	/**
	 * reads in time plan file, including start, end, depart, arrive,
	 * origin,destination, time of walk, drive and transit, store them into a
	 * map. the key of this map is a describe of this time plan, for example
	 * from which hotspots to which streets block vice verse, the value is the
	 * time plan.
	 */
	public void readFile(String travelCostFilePath, HouseholdPool householdPool) {

		String pattern = "\\d+";

		final int hholdColumn = 0;

		final int personColumn = 1;

		final int tripColumn = 3;

		// final int modeColumn = 10;

		final int originColumn = 7;

		final int destinationColumn = 8;

		// final int purposeColumn = 9;

		final int walkTimeColumn = 19;

		final int driveTimeColumn = 20;

		final int transitTimeColumn = 21;

		final int waitTimeColumn = 22;

		final int otherTimeColumn = 23;

		final int costColumn = 25;

		// =======2nd row

		final int legTypeColumn = 1;
		final int legIdColumn = 2;
		final int legTimeColumn = 3;

		String[] nextline;
		CSVReader csvReader = null;
		CSVWriter csvWriter = null;

        if (logger.isTraceEnabled()) {
            logger.trace("Dumping household pool");
            for (Map.Entry<Integer, Household> household : householdPool.getHouseholds().entrySet()) {
                logger.trace("Household: " + household.getKey());
                logger.trace(household.getValue().toString());
            }
        }

		String[] headings = { "household id", "person id", "trip id",
				"pirpose", "mode", "total travel time", "income", "travel cost" };

		try {
            // Skip first two lines as they are headers.
			csvReader = new CSVReader(
					new BufferedReader(new FileReader(new File(timePlanFilePath))), '\t', CSVParser.DEFAULT_QUOTE_CHARACTER, 2);

			csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(travelCostFilePath)));
			csvWriter.writeNext(headings);

			double[] legs = new double[2];

            int lastHholdID = 0;
            // get the last household ID in the pool to determine trips started within the study area.
            for (Household household : main.getHouseholdPool().getHouseholds().values()) {
                if (household.getId() > lastHholdID) {
                    lastHholdID = household.getId();
                }
            }

			while ((nextline = csvReader.readNext()) != null) {
				if (nextline[0].matches(pattern)) {
					// ========trips time=====
					if (legs[0] != 0) {
						ArrayList<Double> trips = (ArrayList<Double>) linksTripsTime
								.get((int) legs[0]);
						if (trips == null) {
							ArrayList<Double> temp = new ArrayList<>();
							temp.add(legs[1]);
							linksTripsTime.put((int) legs[0], temp);
						} else {
							trips.add(legs[1]);
						}

					}
					legs[0] = legs[1] = 0;

					// ======time plan=====
					/*
					 * Only read Time plan with trips that have Origin inside
					 * study area, which means all trips with ID less than or
					 * equal to "maxID" in HholdPool. The other trips with ID,
					 * which is greater than "maxID" in HholdPool, have Origin
					 * outside study area
					 */
					if (Integer.parseInt(nextline[hholdColumn].trim()) <= lastHholdID) {

						TimePlan timePlan = new TimePlan();

						String editedValue = nextline[originColumn].replace("\"", "").trim();
						timePlan.setOrigin(Integer.parseInt(editedValue));

						editedValue = nextline[destinationColumn].replace("\"", "").trim();
						timePlan.setDestination(Integer.parseInt(editedValue));

						editedValue = nextline[walkTimeColumn].replace("\"", "").trim();
						timePlan.setWalkTime(Double.parseDouble(editedValue));

						editedValue = nextline[driveTimeColumn].replace("\"", "").trim();
						timePlan.setDriveTime(Double.parseDouble(editedValue));

						editedValue = nextline[transitTimeColumn].replace("\"", "").trim();
						timePlan.setTransitTime(Double.parseDouble(editedValue));

						editedValue = nextline[waitTimeColumn].replace("\"", "").trim();
						timePlan.setWaitTime(Double.parseDouble(editedValue));

						editedValue = nextline[otherTimeColumn].replace("\"", "").trim();
						timePlan.setWaitTime(Double.parseDouble(editedValue));

						editedValue = nextline[hholdColumn].replace("\"", "").trim();
						int hhold = Integer.parseInt(editedValue);
						timePlan.setHhold(hhold);

						editedValue = nextline[personColumn].replace("\"", "").trim();
                        int person = Integer.parseInt(editedValue);
						timePlan.setPerson(person);

						editedValue = nextline[tripColumn].replace("\"", "").trim();
						int trip = Integer.parseInt(editedValue);
						timePlan.setTrip(trip);

						timePlan.setStart(0);
						timePlan.setEnd(0);
						timePlan.setDepart(0);
						timePlan.setArrive(0);

						/* save successful into Hash Map */
						String hhPersonTrip = String.valueOf(hhold) + "_" + person + "_"
								+ trip;
						// logger.debug(hhPersonTrip);

						// for testing only if commented
						try {
							int mode = main.getHmHhPersonTripTravelMode().get(
									hhPersonTrip);
							timePlan.setMode(TravelModes.classify(mode));
						} catch (Exception e) {
                            logger.error("Exception caught", e);
                            logger.debug(hhPersonTrip);
						}

						String label = generateLabel(timePlan.getOrigin(),
								timePlan.getDestination());

						timePlan.setParkingFee(new BigDecimal(Double
								.parseDouble(nextline[costColumn])));

						if (main.getHmHhPersonTripTravelMode().get(
								hhPersonTrip) != null) {
							timePlan.setCost(travelModeSelection.calculateCost(
                                    TravelModes.classify(main
                                            .getHmHhPersonTripTravelMode()
                                            .get(String.valueOf(hhold) + "_" + person
                                                    + "_" + trip)), timePlan)
                                    .doubleValue());
						}
						TimePlan storedTimePlan = timePlanMap.get(label);
						if (storedTimePlan == null) {
							timePlanMap.put(label, timePlan);
						} else {
							timePlanMap.put(label,
									compareCost(timePlan, storedTimePlan));
						}

						// store travel cost of each individual in the csv files
						// for testing only if commented
//						logger.debug(householdPool.getByID(hhold));
//
//                        Household household = householdPool.getByID(hhold);
//
//                        if (household != null) {
//                            if (household.getResidents().size() > (person - 1)) {
//                                storeTravelCost(
//                                        household.getResidents()
//                                                .get(person - 1).getIncome(),
//                                        timePlan, csvWriter);
//                            } else {
//                                logger.warn("Individual with ID: " + person + " doesn't exist in household: " + hhold + ". Skipping entry");
//                            }
//                        } else {
//                            logger.warn("Household with ID: " + hhold + " doesn't exist in Pool. Skipping entry");
//                        }

						/*
						 * setup Delta FixedCost & Variable Cost for new Travel
						 * Mode Choice design
						 */
						setupDeltaFixedCostVariableCost(timePlan, hhPersonTrip);

					}
				} else {
					if (nextline[legTypeColumn].contains("LINK")) {
						if (legs[0] == 0) {
							legs[0] = Math.abs(Double
									.parseDouble(nextline[legIdColumn]));
							legs[1] = Double
									.parseDouble(nextline[legTimeColumn]);
						} else {
							if (legs[1] < Double
									.parseDouble(nextline[legTimeColumn])) {
								legs[0] = Math.abs(Double
										.parseDouble(nextline[legIdColumn]));
								legs[1] = Double
										.parseDouble(nextline[legTimeColumn]);
							}
						}
					}
				}

			}

		} catch (FileNotFoundException e) {
            logger.error("Exception caught", e);
		} catch (IOException e) {
            logger.error("Exception caught", e);
		} finally {
			try {
				if (csvReader != null) {
                    csvReader.close();
                }
				if (csvWriter != null) {
                    csvWriter.close();
                }
			} catch (IOException e) {
                logger.error("Exception caught", e);
			}
		}

	}

	/**
	 * Reads in time plan file, including start, end, depart, arrive,
	 * origin,destination, time of walk, drive and transit, store them into a
	 * map. The key of this map is a describe of this time plan, for example
	 * from which hot-spots to which streets block vice verse, the value is the
	 * time plan.
	 * 
	 * After that, using time of walk, drive and transit of the car driver to
	 * assign for the car passenger and calculate the delta fixed cost and
	 * variable cost in order to make the travel mode choice in the next step
	 * 
	 *
	 * @author vlcao
	 * 
	 */
	public void readFileFixedTimeCarPax() {

		String pattern = "\\d+";

		final int hholdColumn = 0;

		final int personColumn = 1;

		final int tripColumn = 3;

		final int originColumn = 7;

		final int destinationColumn = 8;

		final int walkTimeColumn = 19;

		final int driveTimeColumn = 20;

		final int transitTimeColumn = 21;

		final int waitTimeColumn = 22;

		final int otherTimeColumn = 23;

		final int costColumn = 25;

		// =======2nd row

		final int legTypeColumn = 1;
		final int legIdColumn = 2;
		final int legTimeColumn = 3;

		String[] nextline;
		CSVReader csvReader = null;

		try {
			csvReader = new CSVReader(
					new BufferedReader(new FileReader(new File(timePlanFilePath))), '\t');

			nextline = csvReader.readNext();
			nextline = csvReader.readNext();

			double[] legs = new double[2];

            int lastHholdID = 0;
            // get the last household ID in the pool to determine trips started within the study area.
            for (Household household : main.getHouseholdPool().getHouseholds().values()) {
                if (household.getId() > lastHholdID) {
                    lastHholdID = household.getId();
                }
            }

			while ((nextline = csvReader.readNext()) != null) {
				if (nextline[0].matches(pattern)) {
					// ========trips time=====
					if (legs[0] != 0) {
						ArrayList<Double> trips = (ArrayList<Double>) linksTripsTime
								.get((int) legs[0]);
						if (trips == null) {
							ArrayList<Double> temp = new ArrayList<>();
							temp.add(legs[1]);
							linksTripsTime.put((int) legs[0], temp);
						} else {
							trips.add(legs[1]);
						}

					}
					legs[0] = legs[1] = 0;

					// ======time plan=====
					/*
					 * Only read Time plan with trips that have Origin inside
					 * study area, which means all trips with ID less than or
					 * equal to "maxID" in HholdPool. The other trips with ID,
					 * which is greater than "maxID" in HholdPool, have Origin
					 * outside study area
					 */
					if (Integer.parseInt(nextline[hholdColumn].trim()) <= lastHholdID) {

						TimePlan timePlan = new TimePlan();

						String editedValue = nextline[originColumn].replace("\"", "").trim();
						timePlan.setOrigin(Integer.parseInt(editedValue));

						editedValue = nextline[destinationColumn].replace("\"", "").trim();
						timePlan.setDestination(Integer.parseInt(editedValue));

						editedValue = nextline[walkTimeColumn].replace("\"", "").trim();
						timePlan.setWalkTime(Double.parseDouble(editedValue));

						editedValue = nextline[driveTimeColumn].replace("\"", "").trim();
						timePlan.setDriveTime(Double.parseDouble(editedValue));

						editedValue = nextline[transitTimeColumn].replace("\"", "").trim();
						timePlan.setTransitTime(Double.parseDouble(editedValue));

						editedValue = nextline[waitTimeColumn].replace("\"", "").trim();
						timePlan.setWaitTime(Double.parseDouble(editedValue));

						editedValue = nextline[otherTimeColumn].replace("\"", "").trim();
						timePlan.setWaitTime(Double.parseDouble(editedValue));

						editedValue = nextline[hholdColumn].replace("\"", "").trim();
						int hhold = Integer.parseInt(editedValue);
						timePlan.setHhold(hhold);

						editedValue = nextline[personColumn].replace("\"", "").trim();
						int person = Integer.parseInt(editedValue);
						timePlan.setPerson(person);

						editedValue = nextline[tripColumn].replace("\"", "").trim();
						int trip = Integer.parseInt(editedValue);
						timePlan.setTrip(trip);

						timePlan.setStart(0);
						timePlan.setEnd(0);
						timePlan.setDepart(0);
						timePlan.setArrive(0);

						/* save successful into Hash Map */
						String hhPersonTrip = String.valueOf(hhold) + "_" + person + "_"
								+ trip;
						// logger.debug(hhPersonTrip);

						// for testing only if commented
						try {
							int mode = main.getHmHhPersonTripTravelMode().get(
									hhPersonTrip);
							timePlan.setMode(TravelModes.classify(mode));
						} catch (Exception e) {
                            logger.error("Exception caught", e);
                            logger.debug(hhPersonTrip);
						}

						String label = generateLabel(timePlan.getOrigin(),
								timePlan.getDestination());

						timePlan.setParkingFee(new BigDecimal(Double
								.parseDouble(nextline[costColumn])));

						if (main.getHmHhPersonTripTravelMode().get(
								hhPersonTrip) != null) {
							timePlan.setCost(travelModeSelection.calculateCost(
									TravelModes.classify(main
											.getHmHhPersonTripTravelMode()
											.get(String.valueOf(hhold) + "_" + person
													+ "_" + trip)), timePlan)
									.doubleValue());
						}
						TimePlan storedTimePlan = timePlanMap.get(label);
						if (storedTimePlan == null) {
							timePlanMap.put(label, timePlan);
						} else {
							timePlanMap.put(label,
									compareCost(timePlan, storedTimePlan));
						}

						/* patch 2 for fixed the time of car passenger */

						/* get the "hhPersonTrip" and "person" in Travel Diary */
						String hhPersonTripTD = main.getHhPersonTripConvertedMap().get(hhPersonTrip);
						
						if (hhPersonTripTD != null) {
							char[] hhPersonTripTDArray = hhPersonTripTD
									.toCharArray();
							String personTD = "";
							int countUS = 0;

	                        for (char aHhPersonTripTDArray : hhPersonTripTDArray) {
	                            if (countUS == 1) {
	                                personTD = personTD + aHhPersonTripTDArray;
	                            }

	                            if (aHhPersonTripTDArray == '_') {
	                                countUS++;

	                                if (countUS == 2) {
	                                    personTD = personTD.replace("_", "");
	                                    break;
	                                }
	                            }
	                        }

							/*
							 * only assign travel time when this individual exists
							 * in the Individual Pool
							 */
							if (main.getIndividualPool().getByID(
									Integer.parseInt(personTD)) != null) {

								/*
								 * when this individual is the Car Passenger, assign the
								 * travel time of his/her Car Driver to his/her
								 * travel time
								 */
								if (main.getOtherHhodCarPaxCarDriverMap()
												.get(hhPersonTripTD) != null) {
									String carDriver = main
											.getOtherHhodCarPaxCarDriverMap()
											.get(hhPersonTripTD);

									// driverTimeMap:
									// <"HhPersonTrip_TravelDiary",[WalkTime,DriveTime,TransitTime,WaitTime,OtherTime]>

									if (main.getDriverTimeMap().get(carDriver) != null) {
										timePlan.setWalkTime(main
												.getDriverTimeMap().get(
														carDriver)[0]);
										timePlan.setDriveTime(main
												.getDriverTimeMap().get(
														carDriver)[1]);
										timePlan.setTransitTime(main
												.getDriverTimeMap().get(
														carDriver)[2]);
										timePlan.setWaitTime(main
												.getDriverTimeMap().get(
														carDriver)[3]);
										timePlan.setOtherTime(main
												.getDriverTimeMap().get(
														carDriver)[4]);
									}
								}
								
//								/*
//								 * when this individual is Under15 child and is the
//								 * Car Passenger, assign the travel time of his/her
//								 * Car Driver to his/her travel time
//								 */
//								if (main.getIndividualPool()
//										.getByID(Integer.parseInt(personTD))
//										.getHouseholdRelationship() == HouseholdRelationship.U15Child
//										&& main.getU15HhodCarPaxCarDriverMap()
//												.get(hhPersonTripTD) != null) {
//
//									String carDriver = main
//											.getU15HhodCarPaxCarDriverMap().get(
//													hhPersonTripTD);
//
//									// driverTimeMap:
//									// <"HhPersonTrip_TravelDiary",[WalkTime,DriveTime,TransitTime,WaitTime,OtherTime]>
//
//									if (main.getDriverTimeMap().get(carDriver) != null) {
//										timePlan.setWalkTime(main
//												.getDriverTimeMap().get(carDriver)[0]);
//										timePlan.setDriveTime(main
//												.getDriverTimeMap().get(carDriver)[1]);
//										timePlan.setTransitTime(main
//												.getDriverTimeMap().get(carDriver)[2]);
//										timePlan.setWaitTime(main
//												.getDriverTimeMap().get(carDriver)[3]);
//										timePlan.setOtherTime(main
//												.getDriverTimeMap().get(carDriver)[4]);
//									}
//
//								} else {
//									/*
//									 * when this individual is NOT Under15 child,
//									 * but is still the Car Passenger, assign the
//									 * travel time of his/her Car Driver to his/her
//									 * travel time
//									 */
//									if (main.getIndividualPool()
//											.getByID(Integer.parseInt(personTD))
//											.getHouseholdRelationship() != HouseholdRelationship.U15Child
//											&& main.getOtherHhodCarPaxCarDriverMap()
//													.get(hhPersonTripTD) != null) {
//										String carDriver = main
//												.getOtherHhodCarPaxCarDriverMap()
//												.get(hhPersonTripTD);
//
//										// driverTimeMap:
//										// <"HhPersonTrip_TravelDiary",[WalkTime,DriveTime,TransitTime,WaitTime,OtherTime]>
//
//										if (main.getDriverTimeMap().get(carDriver) != null) {
//											timePlan.setWalkTime(main
//													.getDriverTimeMap().get(
//															carDriver)[0]);
//											timePlan.setDriveTime(main
//													.getDriverTimeMap().get(
//															carDriver)[1]);
//											timePlan.setTransitTime(main
//													.getDriverTimeMap().get(
//															carDriver)[2]);
//											timePlan.setWaitTime(main
//													.getDriverTimeMap().get(
//															carDriver)[3]);
//											timePlan.setOtherTime(main
//													.getDriverTimeMap().get(
//															carDriver)[4]);
//										}
//									}
//								}
							} else {
								logger.error("Null pointer exception at:"
										+ hhPersonTripTD);
							}	
						}
					}
				} else {
					if (nextline[legTypeColumn].contains("LINK")) {
						if (legs[0] == 0) {
							legs[0] = Math.abs(Double
									.parseDouble(nextline[legIdColumn]));
							legs[1] = Double
									.parseDouble(nextline[legTimeColumn]);
						} else {
							if (legs[1] < Double
									.parseDouble(nextline[legTimeColumn])) {
								legs[0] = Math.abs(Double
										.parseDouble(nextline[legIdColumn]));
								legs[1] = Double
										.parseDouble(nextline[legTimeColumn]);
							}
						}
					}
				}

			}

		} catch (FileNotFoundException e) {
            logger.error("Exception caught", e);
		} catch (IOException e) {
            logger.error("Exception caught", e);
		} finally {
			try {
				if (csvReader != null) {
                    csvReader.close();
                }
			} catch (IOException e) {
                logger.error("Exception caught", e);
			}
		}

	}

	/**
	 * Use the fixed cost and variable cost in the model to calculate the delta
	 * between the cost of current mode and cheapest mode.
	 * 
	 * @param timePlan
	 * @param hhPersonTrip
	 *            : a combined string of HholdID, PersonID and TripID with
	 *            TimePlan file format
	 * 
	 * @author vlcao
	 */
	public void setupDeltaFixedCostVariableCost(TimePlan timePlan,
                                                String hhPersonTrip) {
		double carTime = 0;
		double transitTime = 0;
		double walkTime = 0;
		double bikeTime = 0;
		double travelTime = 0;

		switch (timePlan.getMode()) {
		case Walk:
			travelTime = timePlan.getWalkTime();
			walkTime = travelTime;
			carTime = (travelTime * main.getWalkToCarRatio());
			transitTime = (travelTime * main.getWalkToBusRatio());
			bikeTime = (travelTime * main.getWalkToBikeRatio());
			break;
		case CarDriver:
			travelTime = timePlan.getDriveTime();
			walkTime = (travelTime / main.getWalkToCarRatio());
			transitTime = (travelTime / main.getBusToCarRatio());
			carTime = travelTime;
			bikeTime = (travelTime / main.getBikeToCarRatio());
			break;
		case Bus:
			travelTime = timePlan.getTransitTime();
			walkTime = (travelTime / main.getWalkToBusRatio());
			transitTime = travelTime;
			carTime = (travelTime * main.getBusToCarRatio());
			bikeTime = (travelTime / main.getBikeToBusRatio());
			break;
		case LightRail:
			travelTime = timePlan.getTransitTime();
			walkTime = (travelTime / main.getWalkToBusRatio());
			transitTime = travelTime;
			carTime = (travelTime * main.getBusToCarRatio());
			bikeTime = (travelTime / main.getBikeToBusRatio());
			break;
		case Bike:
			travelTime = timePlan.getOtherTime();
			walkTime = (travelTime / main.getWalkToBikeRatio());
			transitTime = (travelTime * main.getBikeToBusRatio());
			carTime = (travelTime * main.getBikeToCarRatio());
			bikeTime = travelTime;
			break;
		case CarPassenger:
			travelTime = timePlan.getDriveTime();
			walkTime = (travelTime / main.getWalkToCarRatio());
			transitTime = (travelTime / main.getBusToCarRatio());
			carTime = travelTime;
			bikeTime = (travelTime / main.getBikeToCarRatio());
			break;
		case Taxi:
			travelTime = timePlan.getDriveTime(); // checkme
			walkTime = (travelTime / main.getWalkToCarRatio());
			transitTime = (travelTime / main.getBusToCarRatio());
			carTime = travelTime;
			bikeTime = (travelTime / main.getBikeToCarRatio());
			break;
		default:
			break;
		}

		// set delta fixed cost and delta variable cost
		if (main.getHmHhPersonTripTravelMode().get(hhPersonTrip) != null) {

			timePlan.setDeltaFixedCost(travelModeSelection
					.calculateDeltaFixedCost(TravelModes.classify(main
							.getHmHhPersonTripTravelMode().get(hhPersonTrip)),
							bikeTime, walkTime, transitTime));

			timePlan.setDeltaVariableCost(travelModeSelection
					.calculateDeltaVariableCost(TravelModes.classify(main
							.getHmHhPersonTripTravelMode().get(hhPersonTrip)),
							bikeTime, walkTime, transitTime, carTime));
		}

		// add the delta fixed cost and variable cost into a hash map
		hhPersonTrip = main.getHhPersonTripConvertedMap().get(hhPersonTrip);

		main.getTripTimePlanMap().put(
                hhPersonTrip,
				new double[] { timePlan.getDeltaFixedCost(),
						timePlan.getDeltaVariableCost() });


		// add the values for hash map of Car driver with details of trip time
		// driverTimeMap:
		// <"HhPersonTrip_TravelDiary",[WalkTime,DriveTime,TransitTime,WaitTime,OtherTime]>
		if (main.getDriverTimeMap().get(hhPersonTrip) != null) {
			double[] detailsTripTime = { timePlan.getWalkTime(),
					timePlan.getDriveTime(), timePlan.getTransitTime(),
					timePlan.getWaitTime(), timePlan.getOtherTime() };

			main.getDriverTimeMap().put(hhPersonTrip, detailsTripTime);
		}
	}

	/**
	 * Compares costs of 2 time plan. to evaluate the cost, it need to get the
	 * travel time in different mode.Then by different travel mode, calculate
	 * different cost and sum up for a single time plan.
	 * 
	 * @param timePlan1
	 *            the compare candidate time plan 1
	 * @param timePlan2
	 *            the compare candidate time plan 2
	 * @return the time plan is less cost
	 */
	public TimePlan compareCost(TimePlan timePlan1, TimePlan timePlan2) {
		double cost1 = timePlan1.getCost();
		double cost2 = timePlan2.getCost();

		if (cost1 > cost2) {
			return timePlan2;
		} else {
			return timePlan1;
		}
	}

	/**
	 * reads in mapping file from data base.
	 */
	public void readInMapping() {

        ActivityHotspotsStreetBlockDAO activityDao = ApplicationContextHolder.getBean(ActivityHotspotsStreetBlockDAO.class);

        List<ActivityHotspotsStreetBlockEntity> activityHotspots = activityDao.findAll();

        logger.info("Processing Activity hotspots. Count is:" + activityHotspots.size());

        for (ActivityHotspotsStreetBlockEntity hotspot : activityHotspots) {
            activityIDMap.put(hotspot.getActivityId(), hotspot.getHotspotStreetBlockId());
        }
	}


	/**
	 * Stores travel cost (total time * income) in files.
	 * 
	 * @param income
	 * @param timePlan
	 * @param csvWriter
	 */
	public void storeTravelCost(BigDecimal income, TimePlan timePlan,
			CSVWriter csvWriter) {

		final int workHoursPerWeek = 35;
		final int minutesPerHour = 60;
		final int totalMinutes = workHoursPerWeek * minutesPerHour;

		double totalTime = timePlan.getDriveTime() + timePlan.getWaitTime()
				+ timePlan.getWalkTime() + timePlan.getTransitTime()
				+ timePlan.getOtherTime();
		BigDecimal travelCost = income.multiply(new BigDecimal(totalTime))
				.divide(new BigDecimal(totalMinutes), 3, RoundingMode.HALF_UP);

		String[] entries = { String.valueOf(timePlan.getHhold()),
				String.valueOf(timePlan.getPerson()),
				String.valueOf(timePlan.getTrip()),
				String.valueOf(timePlan.getPurpose()),
				String.valueOf(timePlan.getMode()), String.valueOf(totalTime),
				String.valueOf(income), String.valueOf(travelCost) };
		csvWriter.writeNext(entries);

	}

	/**
	 * generates a label like from the hot spot or street block of origin to the
	 * hot spot or street block of destination, for example "11TO22". find
	 * references find activity map file
	 * 
	 * @param origin
	 *            the origin activity id
	 * @param destination
	 *            the destination activity id
	 * @return from the hot spot or street block of origin to the hot spot or
	 *         street block of destination, for example "11TO22";
	 */
	public String generateLabel(int origin, int destination) {
		return activityIDMap.get(origin) + "TO" + activityIDMap.get(destination);
	}

	@Override
	public Object clone() throws CloneNotSupportedException

	{

		throw new CloneNotSupportedException();

	}

	public Map<Integer, List<Double>> getLinksTripsTime() {
		return linksTripsTime;
	}

	/**
	 * Extract trips among travel modes: BUS, LIGHT RAIL, CAR_DRIVER,
	 * CAR_PASSENGER in order to write into CSV file and database.
	 * 
	 * @param timePlanFileName
	 * @param tripFileName
	 */
	public ArrayList<String[]> postProcessTIMEPLANS(String timePlanFileName, String tripFileName) {
		ArrayList<String[]> outputLineArrayList = new ArrayList<>();
        final String[] outputHeader = {"HHOLD,PERSON,TRIP,MODE,DEPART,PURPOSE,TRIP_LENGTH"};
        
		try {
			String[] line;
//			HashMap<String, String> hmHHoldPersonTripMode = new HashMap<>();
//
//			/* A - PROCESS THE TRIPS FILE */
//			CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(tripFileName)), '\t');
//
//			// skip the header rows
//            csvReader.readNext();
//
//			while ((line = csvReader.readNext()) != null) {
//                String hHold = line[0];
//                String person = line[1];
//                String trip = line[2];
//                String mode = line[4];
//
//				String hHoldPersonTrip = hHold + "_" + person + "_" + trip;
//				hmHHoldPersonTripMode.put(hHoldPersonTrip,
//                        TravelModes.classify(Integer.parseInt(mode)).getTranssimsName());
//			}
//            csvReader.close();
			

			/* B - PROCESS THE TIMEPLANS FILE */
			CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(timePlanFileName)), '\t');
            CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(timePlanFileName + "_new.csv")),
                    CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			// skip the 2 header rows
            String[] firstHeaderLine = csvReader.readNext();
            csvReader.readNext();

			// write the header for .csv output file
            csvWriter.writeNext(outputHeader);

//            int lastHholdID = 0;
//			// get the last household ID in the pool to determine trips started within the study area.
//			for (Household household : main.getHouseholdPool().getHouseholds().values()) {
//				if (household.getId() > lastHholdID) {
//					lastHholdID = household.getId();
//				}
//			}
			
			while ((line = csvReader.readNext()) != null) {
				// only save the overall rows not the leg rows
				if (line.length == firstHeaderLine.length) {

					String hHold = line[0];
	                String person = line[1];
	                String trip = line[3];
	                String depart = line[16];
	                String purpose = line[9];
	                String tripLength = line[24];
	                
	                String hHoldPersonTrip = hHold + "_" + person + "_" + trip;
					String purposeString = getPurposeString(Integer.parseInt(purpose));

					// only save and write the travel modes: BUS, LIGHT RAIL,
					// CAR_DRIVER, CAR_PASSENGER
					// if (!mode.equalsIgnoreCase("WALK")
					// && !mode.equalsIgnoreCase("HOV4")
					// && !mode.equalsIgnoreCase("BIKE")) {
					// mode = hmHHoldPersonTripMode.get(hHoldPersonTrip);
					int modeTransimInteger = ModelMain.getInstance().getHmHhPersonTripTravelMode().get(hHoldPersonTrip);
					String mode = TravelModes.classify(modeTransimInteger).getTranssimsName();

					String[] outputLine = {hHold, person, trip, mode, depart, purposeString, tripLength};
					csvWriter.writeNext(outputLine);

					outputLineArrayList.add(outputLine);
				}
			}
//			}
			// logger.debug("");
			csvReader.close();
			csvWriter.close();

		} catch (Exception e) {// Catch exception if any
            logger.error("Exception caught", e);
		}
		
		return outputLineArrayList;
	}
	
	public static String getPurposeString(int code) {
		String purposeStr;
		switch (code) {
		case 0:
			purposeStr = "home";
			break;
		case 1:
			purposeStr = "go_to_work";
			break;
		case 2:
			purposeStr = "shopping";
			break;
		case 3:
			purposeStr = "visit";
			break;
		case 4:
			purposeStr = "social_recreation";
			break;
		case 5:
			purposeStr = "other";
			break;
		case 6:
			purposeStr = "serve_passenger";
			break;
		case 7:
			purposeStr = "school";
			break;
		case 8:
			purposeStr = "college";
			break;
		default:
			purposeStr = "";
			break;
		}
		return purposeStr;

	}
}
