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
package core;

import java.io.InputStreamReader;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public final class HardcodedData {


    private static final String WINDOWS = "Windows";
	private static final String OS_NAME = "os.name";

	// Suppress default constructor for noninstantiability
	private HardcodedData() {
		throw new AssertionError();
	}

	public static Random random = new Random();

	public static void setSeed(long seed) {
		random.setSeed(seed);
	}

	private static String winTransimsPath;
	private static String unixTransimsPath;
	private static String tripsFileLocation;
	private static String vehiclesFileLocation;
	private static String personsFileLocation;
	private static String eventsFileLocation;
	private static String performanceFileLocation;
	private static String householdsFileLocation;
	private static String timePlanFileLocation;
	private static String proccessLink_2_FileLocation;
	private static String transitScheduleFileLocation;

	public static String vehicleTypeFileLocation;
	public static String transitRouteFileLocation;
	public static String transitStopFileLocation;
	public static String linkFileLocation;
	public static String signalizedNode2FileLocation;
	public static String linkDelayFileLocation;
	

	public static final String[] headerTripsFile = {"HHOLD", "PERSON", "TRIP", "PURPOSE", "MODE", "VEHICLE", "START", "ORIGIN", "ARRIVE", "DESTINATION", "CONSTRAINT"};
	public static final String[] headerVehiclesFile = {"VEHICLE", "HHOLD", "LOCATION", "TYPE", "SUBTYPE"};
	public static final String[] headerProccessLink_2_File = {"ACCESS", "FROM_ID", "FROM_TYPE", "TO_ID", "TO_TYPE", "TIME", "COST", "NOTES"};
	public static final String[] headerPersonsFile = {"HHOLD", "PERSON", "RELATE", "AGE", "GENDER", "WORK", "DRIVE"};
	public static final String[] headerHouseholdsFile = {"HHOLD", "LOCATION", "PERSONS", "WORKERS", "VEHICLES"};

	public static String jtw_OrgOutsideSAFileLocation;
	public static String rsLocation;
	public static String activityFileLocation;
	public static String dwellingFileLocation;
	public static String parkingCapacityFileLocation;
	public static String parkingAccessFileLocation;
	public static String processLinkFileLocation;

	public static String activity_hotspot_blockID_FileLocation;
	
	public static final int pEastGardens = 200; // 20% - 14.5% of shopping trips with travel mode "car driver" go to Eastgardens shopping centre
    public static final int pRandwickPlaza = 310; // 11% - 8.8% of shopping trips with travel mode "car driver" go to Randwick Plaza
    public static final int pStocklandsMaroubra = 410; // 10% - 6.4% of shopping trips with travel mode "car driver" go to Stocklands Mall Maroubra.
    public static final int[] eastGardenShoppingLocations = new int[] {87924}; // these are ID of activity locations at Eastgardens shopping centre.
    public static final int[] randwickPlazaShoppingLocations = new int[] {87654, 87655}; // these are ID of activity locations at Randwick Plaza.
    public static final int[] stocklandsMaroubraShoppingLocations = new int[] {87573,88767}; // these are ID of activity locations at Stocklands Mall Maroubra.


	public static final int[] travelZones = { 270, 276, 277, 278, 279, 283,
			284, 285, 287, 288, 292, 294, 507, 508, 509, 510, 511, 512, 513,
			514, 515, 516, 517, 518, 519, 520, 521, 522, 523, 524, 525, 526,
			527, 528, 529, 530, 531, 532, 533, 534, 535, 536, 537, 538, 539,
			540, 541, 542, 543, 544, 545, 546, 547, 548, 549, 550, 551, 552,
			553, 554, 555, 556, 557, 558 };
//	public static final int[] travelZonesLiveable = { 529, 528, 527, 526, 525,
//			524, 523, 522, 521, 520, 519, 518, 517, 516, 515, 513, 512, 511,
//			510, 509, 508, 270, 276, 277, 278, 283, 284, 285, 287, 288, 292,
//			294, 531, 530, 532, 533, 534, 535, 536, 537, 538, 539, 540, 541,
//			542, 543, 544, 545, 546, 547, 548, 549, 550, 551, 552, 554, 555,
//			557, 558 };
	public static final int[] travelZonesLiveable = { 
		270, 276, 277, 278, 279, 283, 284, 285, 287, 288, 292, 294,
//		507, 
		508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 
		520, 521, 522, 523, 524, 525, 526, 527, 528, 529, 530, 531, 
		532, 533, 534, 535, 536, 537, 538, 539, 540, 541, 542, 543, 
		544, 545, 546, 547, 548, 549, 550, 551, 552, 553, 554, 555, 
		556, 557, 558 };
	
	public static final double[][] betaValues = {
			{ 0.482444055133437, 2.84041079216113, -0.544719256848429,
					-1.05094407925591, 2.21509711861972, 0.0 },
			{ 1.50097654046257, -1.29179328467214, 0.877406693950132,
					0.521029097216393, -1.25691577336647, 0.0 },
			{ -0.0119551881165875, -0.064097795057176, -0.0074223230561478,
					0.0297752546616221, 0.0337922693705046, 0.0 },
			{ 3.10785190055537E-8, 5.28617561828431E-7, -3.0538973549047E-7,
					-5.6363195533433E-8, -6.19308793192598E-7, 0.0 } };
	// with order of columns as
	// "carDriver, carPassenger, bus-light rail-train, taxi, walk, bicycle"

	/*
	 * - "jtw_OD_Tz06" is a 2D array of 64 travel zones inside study area & 1
	 * outside study area with 5 travel modes (number of rows: (64+1) x 5 = 325)
	 * to 64 travel zones inside study area & 1 outside study area (number of
	 * columns: 64+1 = 65).
	 * 
	 * - Value of each number in jtw_OD_Tz06 is the number of trips travel from
	 * 1 Origin travel zone to 1 Destination travel zone.
	 * 
	 * - Travel mode codes according to Journey To Work data: + 1: Others + 2:
	 * Light rail + 3: Bus + 4: Car Driver + 5: Car Passenger
	 */
	private static int[][] jtw_OD_Tz06 = null;


	public static String liveCong;
	public static String linkList;
	public static String configuration;
	public static String properties;
	public static String dwelling;
	public static String travelCost;

	public static enum DBName {
		dwelling
	}

	public static enum DBVariableType {
		int4, text, float8
	}

	public static enum OwnershipStatus {
		rent, paying, own
	}

    // The starting year for the model.
    public static final int START_YEAR = 2006;

    // Maximum number of bedrooms
	public static final int MAX_BEDROOMS = 4;

	public static int maxYearMortgage = 30;
	
	public static double globalEquityScale = 1.0;
	
	public static final int maxTick = 12; // set up 12 for testing only

	public static final int streetWGS84SRID = -1;

	public static ServletContext servletContext;

    /**
     * Load the JTW_OD_TZ06 data from the JSON file.
     * @return
     */
    public static synchronized int[][] getJtwODTz06() {
            if (jtw_OD_Tz06 == null) {
                InputStreamReader inputReader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("core/jtw_OD_Tz06.json"));
                JsonReader reader = new JsonReader(inputReader);
                Gson gson = new Gson();
                jtw_OD_Tz06 = gson.fromJson(reader, int[][].class);
            }
            
//            return jtw_OD_Tz06;
            return (int[][])jtw_OD_Tz06.clone();
    }

	public static String addPath(String filename) {
		return servletContext.getRealPath(filename).replace('\\', '/');
	}

	public static void prepend(HttpServlet servlet) {
		servletContext = servlet.getServletContext();

        prepend();
	}

	public static void prepend() {


        EnvironmentConfig envConfig = ApplicationContextHolder.getBean(EnvironmentConfig.class);

        String pathString = envConfig.getDataPath();
        
		rsLocation = pathString + "/SthRandwickModel/modelsthrandwick.rs";
		liveCong = pathString + "/Live_Cong.txt";
		linkList = pathString + "/link_lists";
		configuration = pathString + "/Configure.txt";
		properties = pathString + "/application.properties";
		activityFileLocation = pathString + "/bootstrap/activity_location_inside_studyarea.csv";
		dwellingFileLocation = pathString + "/bootstrap/new_random_HH_locations_with_streetblockID_n_activityID_n_Note.csv";
		parkingCapacityFileLocation = pathString + "/bootstrap/Parking_Capacity.csv";
		parkingAccessFileLocation = pathString + "/bootstrap/Parking_Access.csv";
		processLinkFileLocation = pathString + "/bootstrap/Process_Link_2.csv";
		dwelling = pathString + "/bootstrap//Dwelling Composition by TZ/";
		travelCost = pathString + "/Travel Cost/";
		jtw_OrgOutsideSAFileLocation = pathString + "/2006JTW_T07_ExpGMA_w_D_InSA_excludeMode9_O_OutSA.csv";
		activity_hotspot_blockID_FileLocation = pathString + "/bootstrap/activity_hotspots_street block.csv";
		
	}

	

	// ========================================================================================================
	public static void setWinTransimsPath(String location) {
		winTransimsPath = "C:/Transims/RandwickSim/" + location;
	}

	public static String getWinTransimsPath() {
		return winTransimsPath;
	}

	public static void setUnixTransimsPath(String location) {
		unixTransimsPath = "/usr/local/transims/RandwickSim/" + location;
	}

	public static String getUnixTransimsPath() {
		return unixTransimsPath;
	}

	public static void setTransimsPath(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);

		} else {
			setUnixTransimsPath(location);
		}

	}

	public static String getTransimsPath() {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {

			return winTransimsPath;
		} else {

			return unixTransimsPath;
		}
	}

	public static void setTripfileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			tripsFileLocation = winTransimsPath + "demand/Syd.TRIPS";
		} else {
			setUnixTransimsPath(location);
			tripsFileLocation = unixTransimsPath + "demand/Syd.TRIPS";
		}

	}

	/**
	 * @return the vehicleTypeFileLocation
	 */
	public static String getVehicleTypeFileLocation() {
		return vehicleTypeFileLocation;
	}

	/**
	 * @param location
	 *            the vehicleTypeFileLocation to set
	 */
	public static void setVehicleTypeFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			vehicleTypeFileLocation= winTransimsPath + "inputs/Vehicle_Type.txt";
		} else {
			setUnixTransimsPath(location);
			vehicleTypeFileLocation= unixTransimsPath + "inputs/Vehicle_Type.txt";
		}
	}

	/**
	 * @return the transitRouteFileLocation
	 */
	public static String getTransitRouteFileLocation() {
		return transitRouteFileLocation;
	}

	/**
	 * @param location
	 *            the transitRouteFileLocation to set
	 */
	public static void setTransitRouteFileLocation(
			String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			transitRouteFileLocation = winTransimsPath + "network/Transit_Route";
		} else {
			setUnixTransimsPath(location);
			transitRouteFileLocation = unixTransimsPath + "network/Transit_Route";
		}
	}

	/**
	 * @return the transitStopFileLocation
	 */
	public static String getTransitStopFileLocation() {
		return transitStopFileLocation;
	}

	/**
	 * @param location
	 *            the transitStopFileLocation to set
	 */
	public static void setTransitStopFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			transitStopFileLocation = winTransimsPath + "network/Transit_Stop";
		} else {
			setUnixTransimsPath(location);
			transitStopFileLocation = unixTransimsPath + "network/Transit_Stop";
		}
	}

	/**
	 * @return the linkFileLocation
	 */
	public static String getLinkFileLocation() {
		return linkFileLocation;
	}

	/**
	 * @param location
	 *            the linkFileLocation to set
	 */
	public static void setLinkFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			linkFileLocation = winTransimsPath + "network/Link";
		} else {
			setUnixTransimsPath(location);
			linkFileLocation = unixTransimsPath + "network/Link";
		}
	}

	/**
	 * @return the linkDelayFileLocation
	 */
	public static String getLinkDelayFileLocation() {
		return linkDelayFileLocation;
	}

	/**
	 * @param location
	 *            the linkDelayFileLocation to set
	 */
	public static void setLinkDelayFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			linkDelayFileLocation = winTransimsPath + "results/3.Syd.2012.Trip.LinkDelay";
		} else {
			setUnixTransimsPath(location);
			linkDelayFileLocation = unixTransimsPath + "results/3.Syd.2012.Trip.LinkDelay";
		}
	}

	
	
	/**
	 * @return the signalizedNode2FileLocation
	 */
	public static String getSignalizedNode2FileLocation() {
		return signalizedNode2FileLocation;
	}

	/**
	 * @param location
	 *            the signalizedNode2FileLocation to set
	 */
	public static void setSignalizedNode2FileLocation(
			String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			signalizedNode2FileLocation = winTransimsPath + "network/Signalized_Node_2";
		} else {
			setUnixTransimsPath(location);
			signalizedNode2FileLocation = unixTransimsPath + "network/Signalized_Node_2";
		}
	}

	public static String getTimePlanFileLocation() {
		return timePlanFileLocation;
	}

	/**
	 * Set up the location for looking up TimePlan file of TRANSIMS on Windows
	 * and Linux operating system
	 * 
	 * @param location
	 */
	public static void setTimePlanFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			timePlanFileLocation = winTransimsPath + "demand50/3.Syd.TIMEPLANS";// _BUSCAPCHECKED";
																				// //test
		} else {
			setUnixTransimsPath(location);
			timePlanFileLocation = unixTransimsPath
					+ "demand50/3.Syd.TIMEPLANS";// _BUSCAPCHECKED";
		}
	}

	public static String getTransitScheduleFileLocation() {
		return transitScheduleFileLocation;
	}

	/**
	 * Set up the location for looking up TransitSchedule file of TRANSIMS on
	 * Windows and Linux operating system
	 * 
	 * @param location
	 */
	public static void setTransitScheduleFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			transitScheduleFileLocation = winTransimsPath
					+ "network/Transit_Schedule";
		} else {
			setUnixTransimsPath(location);
			transitScheduleFileLocation = unixTransimsPath
					+ "network/Transit_Schedule";
		}
	}

	public static String getTripfileLocation() {
		return tripsFileLocation;
	}

	/**
	 * Set up the location for looking up Vehicle file of TRANSIMS on Windows
	 * and Linux operating system
	 * 
	 * @param location
	 */
	public static void setVehiclefileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			vehiclesFileLocation = winTransimsPath + "demand/Syd.VEHICLES";
		} else {
			setUnixTransimsPath(location);
			vehiclesFileLocation = unixTransimsPath + "demand/Syd.VEHICLES";
		}
	}

	public static String getVehiclefileLocation() {
		return vehiclesFileLocation;
	}

	/**
	 * Set up the location for looking up Event file of TRANSIMS on Windows and
	 * Linux operating system
	 * 
	 * @param location
	 */
	public static void setEventsFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			eventsFileLocation = winTransimsPath + "demand50/3.Syd.Events";
		} else {
			setUnixTransimsPath(location);
			eventsFileLocation = unixTransimsPath + "demand50/3.Syd.Events";
		}
	}

	public static String getEventsFileLocation() {
		return eventsFileLocation;
	}

	/**
	 * Set up the location for looking up Performance file of TRANSIMS on
	 * Windows and Linux operating system
	 * 
	 * @param location
	 */
	public static void setPerformanceFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			performanceFileLocation = winTransimsPath
					+ "results/3.Syd.PERFORMANCE";
		} else {
			setUnixTransimsPath(location);
			performanceFileLocation = unixTransimsPath
					+ "results/3.Syd.PERFORMANCE";
		}
	}

	public static String getPerformanceFileLocation() {
		return performanceFileLocation;
	}

	public static String getPersonsFileLocation() {
		return personsFileLocation;
	}

	/**
	 * Set up the location for looking up Persons file of TRANSIMS on Windows
	 * and Linux operating system
	 * 
	 * @param location
	 */
	public static void setPersonsFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			personsFileLocation = winTransimsPath + "demand/Syd.PERSONS";
		} else {
			setUnixTransimsPath(location);
			personsFileLocation = unixTransimsPath + "demand/Syd.PERSONS";
		}
	}

	public static String getHouseholdsFileLocation() {
		return householdsFileLocation;
	}

	/**
	 * Set up the location for looking up Households file of TRANSIMS on Windows
	 * and Linux operating system
	 * 
	 * @param location
	 */
	public static void setHouseholdsFileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			householdsFileLocation = winTransimsPath + "demand/Syd.HOUSEHOLDS";
		} else {
			setUnixTransimsPath(location);
			householdsFileLocation = unixTransimsPath + "demand/Syd.Households";
		}
	}

	/**
	 * Set up the location for looking up ProccessLink_2 file of TRANSIMS on
	 * Windows and Linux operating system
	 * 
	 * @param location
	 */
	public static void setProcessLink2FileLocation(String location) {
		if (System.getProperty(OS_NAME).startsWith(WINDOWS)) {
			setWinTransimsPath(location);
			proccessLink_2_FileLocation = winTransimsPath
					+ "network/Process_Link_2";
		} else {
			setUnixTransimsPath(location);
			proccessLink_2_FileLocation = unixTransimsPath
					+ "network/Process_Link_2";
		}
	}

	public static String getProcessLink2FileLocation() {
		return proccessLink_2_FileLocation;
	}

}
