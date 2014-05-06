package core;

import core.locations.ActivityLocationManager;
import core.model.TextFileHandler;
import core.synthetic.household.VehicleManager;
import hibernate.configuration.ConfigurationDAO;
import hibernate.configuration.ConfigurationEntity;
import hibernate.configuration.RunsDAO;
import hibernate.configuration.RunsEntity;
import hibernate.postgis.TravelZonesFacilitiesDAO;
import hibernate.postgis.TravelZonesFacilitiesEntity;
import hibernate.postgres.ImmigrationRateDAO;
import hibernate.postgres.JTW2006DAO;
import hibernate.postgres.ProcessLinkDAO;
import hibernate.postgres.WeightsDAO;
import hibernate.postgres.WeightsEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import au.com.bytecode.opencsv.CSVReader;

import org.springframework.util.Log4jConfigurer;

import servlet.GeoDBReader;
import core.locations.ParkingManager;
import core.model.TransimsRunner;
import core.postprocessing.database.DatabaseCreator;
import core.postprocessing.database.StoreThread;
import core.postprocessing.file.TimePlanFileProcessor;
import core.postprocessing.file.TripsTimeProcessor;
import core.preprocessing.TranSimsInputGenerator;
import core.preprocessing.Trips;
import core.synthetic.HouseholdPool;
import core.synthetic.IndividualPool;
import core.synthetic.RandomAgentsGenerator;
import core.synthetic.dwelling.Dwelling;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
import core.synthetic.household.Vehicle;
import core.synthetic.immgrants.ImmigrantGenerator;
import core.synthetic.individual.Individual;
import core.synthetic.individual.lifeEvent.LifeEventProbability;
import core.synthetic.liveability.LiveabilityUtility;
import core.synthetic.survey.DemographicInfomation;
import core.synthetic.travel.mode.TravelModes;
import core.synthetic.traveldiary.LocationTypes;
import core.synthetic.traveldiary.TravelDiaryColumns;
import core.traveldiary.HTSDataHandler;
import hibernate.postgres.NewTendbyBerdrDAO;
import hibernate.postgres.NewTendbyBerdrEntity;

/**
 * The Main class for running the whole model.
 * 
 * @author qun
 * 
 */
public final class ModelMain {
	// How often we should check to see if the log4j file has changed. Measured
	// in milliseconds.
	private static final long LOG4J_REFRESH = 30000;

	private Configuration configuration;
	private IndividualPool individualPool;
	private HouseholdPool householdPool;
	private DwellingAllocator dwellingAllocator;

	private final List<Household> removedHouseholds;
	private final List<Individual> deadIndividuals;

	private List<Vehicle> vehicleList;
	private List<Trips> tripsListWeekdays;
	private List<Integer> processLink;

	private static ModelMain modelMain;

	private int seed;

	/**
	 * @return the seed
	 */
	public int getSeed() {
		return this.seed;
	}

	/**
	 * @param seed
	 *            the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/*
	 * processLink: an Integer array with orderOfArray+1 is Activity Location,
	 * and each value according to each order is closest and available Parking
	 * Location
	 */
	private int currentYear;
	private String dwellingSchema;

	private static final double WALK_TO_CAR_RATIO = 0.8 / 11.0;
	private static final double WALK_TO_BUS_RATIO = 0.8 / 8.3;
	private static final double BUS_TO_CAR_RATIO = 8.3 / 11.0;
	private static final double WALK_TO_BIKE_RATIO = 0.8 / 5.5;
	private static final double BIKE_TO_CAR_RATIO = 5.5 / 11.0;
	private static final double BIKE_TO_BUS_RATIO = 5.5 / 8.3;

	private static final Logger logger = Logger.getLogger(ModelMain.class);
	private LifeEventProbability aLifeEventProbability;

	private Map<String, Integer> hmHhPersonTripTravelMode;
	// hmHhPersonTripTravelMode : <[hhold, personInTransims, trip] with Transims
	// format
	// ,travelMode>

	private static Map<String, Integer> hhppTripModeBeforeTMC;
	// Map<hhID_indivID_tripID, transimsModeInteger>

	private final Map<String, String> hmHhPersonTripTDPurpose;
	// hmHhPersonTripTDPurpose : <[hhold, person, trip] with Travel Diary
	// format
	// ,"porpose">

	private Map<String, double[]> ultTravelModeMap;
	// ultTravelModeMap: <"hhPersonTrip", [ultOfCarDriver, ultOfCarPassenger,
	// ultOfBus, ultOfLight_rail, ultOfTaxi, ultOfWalk, ultOfBicycle]>

	private Map<String, double[]> proTravelModeMap;
	// proTravelModeMap: <"hhPersonTrip", [proOfCarDriver, proOfCarPassenger,
	// proOfBus, proOfLight_rail, proOfTaxi, proOfWalk, proOfBicycle]>

	private Map<String, double[]> cumProTravelModeMap;
	// cumProTravelModeMap: <"hhPersonTrip", [cumOfCarDriver, cumOfCarPassenger,
	// cumOfBus, cumOfLight_rail, cumOfTaxi, cumOfWalk, cumOfBicycle]>

	private Map<String, String> hhPersonTripConvertedMap;
	// hhPersonTripConvertedMap: <"HhPersonTrip_Transims",
	// "HhPersonTrip_TravelDiary">

	private Map<String, double[]> tripTimePlanMap;
	// tripTimePlanMap: <"HhPersonTrip_TravelDiary",[DeltaFixedCost,
	// DeltaVariableCost]>

	private Map<String, double[]> driverTimeMap;
	// driverTimeMap:
	// <"HhPersonTrip_TravelDiary",[WalkTime,DriveTime,TransitTime,WaitTime,OtherTime]>

	private Map<String, String> u15HhodCarPaxCarDriverMap = new HashMap<>();
	// u15HhodCarPaxCarDriverMap: <"HhPersonTrip_TravelDiary_U15CarPax",
	// "HhPersonTrip_TravelDiary_CarDriver">

	private Map<String, String> otherHhodCarPaxCarDriverMap = new HashMap<>();
	// otherHhodCarPaxCarDriverMap: <"HhPersonTrip_TravelDiary_otherCarPax",
	// "HhPersonTrip_TravelDiary_CarDriver">

	private int percentageTick;

	private String transimsFrequency = "";
	private int agentNumber = 0;
	private int yearNumber = 0;
	private int agentTravelChanged = 0;

	public static String randNumFile = "randomNumbers.csv";

	private String scenarioName;
	private String selectedDatabaseName;
	private String OutputDatabaseName;

	private static final int MAX_SYNTHETIC_POPULATION = 106066;

	private final ImmigrationRateDAO immigrationRateDAO;

	private final ProcessLinkDAO processLinkDAO;

	private final JTW2006DAO jtw2006DAO;

	private static final double SCALE = 9.357;
	private static final double SHAPE = 0.8;

	private final ModelRunner modelRunner;

	private final NewTendbyBerdrDAO newTendbyBerdrDAO;

	private ImmigrantGenerator immigrantGenerator;
	
	private static RunsDAO ModelrunsDao;
	private static RunsEntity ModelrunsEntity;
	

	// =================================================================
	private ModelMain() {
		super();
		this.jtw2006DAO = ApplicationContextHolder.getBean(JTW2006DAO.class);

		this.immigrationRateDAO = ApplicationContextHolder
				.getBean(ImmigrationRateDAO.class);

		this.processLinkDAO = ApplicationContextHolder
				.getBean(ProcessLinkDAO.class);

		this.hmHhPersonTripTravelMode = new HashMap<>();

		this.hmHhPersonTripTDPurpose = new HashMap<>();

		this.driverTimeMap = new HashMap<>();

		this.removedHouseholds = new ArrayList<>();
		this.deadIndividuals = new ArrayList<>();

		this.modelRunner = new ModelRunner();

		this.newTendbyBerdrDAO = ApplicationContextHolder
				.getBean(NewTendbyBerdrDAO.class);

		hhppTripModeBeforeTMC = new HashMap<String, Integer>();

		this.hhPersonTripConvertedMap = new HashMap<>();

	}

	/**
	 * 
	 * function of transfer_data (transfer_data from the output db into
	 * SID_stsot
	 */
	public void transfer_data() throws Exception {

		try {

			EnvironmentConfig environmentConfig = ApplicationContextHolder
					.getBean(EnvironmentConfig.class);

			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
					new File(environmentConfig.getConfigPath()
							+ "/environment.properties"));

			String hostname = (String) propertiesConfiguration
					.getProperty("postgres.hostname");
			String port = (String) propertiesConfiguration
					.getProperty("postgres.port");
			String user = (String) propertiesConfiguration
					.getProperty("postgres.user");
			String password = (String) propertiesConfiguration
					.getProperty("postgres.password");
			String SIDdb = "SID_stsot";


			String RunId = String.valueOf(ModelrunsEntity.getId());
			String ConfId = String.valueOf(ModelrunsEntity.getConfigurationId());

			Statement stm = null;
			Connection con = null;
			String url = "jdbc:postgresql://" + hostname + ":" + port + "/"
					+ SIDdb + "?user=" + user + "&password=" + password;
			con = DriverManager.getConnection(url);
			stm = con.createStatement();

			String sql = "SELECT transfer_data(\'"
					+ this.getOutputDatabaseName() + "\', \'" + password
					+ "\', " + ConfId + "," + RunId + ");";
			logger.debug(sql);

			stm.execute(sql);

		} catch (Exception e) {
			logger.error("Exception caught in transfer_data: ", e);
			throw e;
		}
	}

	// =================================================================
	/**
	 * main function for running the model.
	 */
	public static void main(String[] args) {

		try {

			ApplicationContext ctx = new ClassPathXmlApplicationContext(
					"applicationContext.xml");

			// Setup log4j
			EnvironmentConfig envConfig = ctx.getBean(EnvironmentConfig.class);
			Log4jConfigurer.initLogging(envConfig.getConfigPath()
					+ File.separator + "log4j.xml", LOG4J_REFRESH);

			ApplicationContextHolder holder = ctx
					.getBean(ApplicationContextHolder.class);

			

			/* model's pre_processing */
			logger.debug("Preprocessing...");
			ModelMain main = getInstance();
			main.initialConfiguration();


			HardcodedData.setTimePlanFileLocation("weekdays/");
			TimePlanFileProcessor timePlanFileProcessor = TimePlanFileProcessor
					.getInstance(HardcodedData.getTimePlanFileLocation());
			LocationTypes locationTypes = new LocationTypes();
			HTSDataHandler htsHdlr = new HTSDataHandler(HardcodedData.random);
			DatabaseCreator databaseCreator = new DatabaseCreator();

			main.initialPool();
			main.initialTravelDiary(locationTypes, htsHdlr);
			main.initialDatabase(databaseCreator);
			main.initialModel();


			if (logger.isTraceEnabled()) {
				// population in householdPool before evolutionOnHouseholdLevel
				ArrayList<String[]> hhPoolBeforeEvolHholdLvl = new ArrayList<>();
				for (Household hhold : ModelMain.getInstance()
						.getHouseholdPool().getHouseholds().values()) {
					Dwelling dwelling = ModelMain.getInstance().dwellingAllocator
							.getDwellingPool().getDwellings()
							.get(hhold.getDwellingId());
					for (Individual indiv : hhold.getResidents()) {
						hhPoolBeforeEvolHholdLvl
								.add(new String[] {
										String.valueOf(indiv.getId()),
										String.valueOf(indiv.getAge()),
										indiv.getGender().toString(),
										String.valueOf(indiv.getIncome()
												.doubleValue()),
										(indiv.getHouseholdRelationship() == null) ? ""
												: indiv.getHouseholdRelationship()
														.toString(),
										String.valueOf(indiv.getHouseholdId()),
										(indiv.getHholdCategory() == null) ? ""
												: indiv.getHholdCategory()
														.toString(),
										String.valueOf(hhold.getGrossIncome()
												.doubleValue()),
										(hhold.getEquity() == null) ? ""
												: String.valueOf(hhold
														.getEquity()),
										(hhold.getSavingsForHouseBuying() == null) ? ""
												: String.valueOf(hhold
														.getSavingsForHouseBuying()),
										String.valueOf(hhold.getTenure()),
										String.valueOf(hhold
												.calculateNumberOfRoomNeed()),
										hhold.getOwnershipStatus() == null ? "null"
												: hhold.getOwnershipStatus()
														.toString(),
										(dwelling == null) ? "" : String
												.valueOf(dwelling
														.getLivedHouseholdId()),
										(dwelling == null) ? "" : String
												.valueOf(dwelling
														.getTravelZoneId()),
										(dwelling == null) ? "" : String
												.valueOf(dwelling
														.getNumberOfRooms()) });
					}
				}
				TextFileHandler
						.writeToCSV(
								String.valueOf(ModelMain.getInstance()
										.getCurrentYear())
										+ "_hhPool_b4_evolHholdLvl.csv",
								hhPoolBeforeEvolHholdLvl, false);
			}

			/* model's running */
			logger.debug("Model's running...");
			ModelrunsEntity.setTimeStart(new Timestamp(new Date().getTime()));
			ModelrunsEntity.setTimeFinished(null);
			ModelrunsEntity.setStatus(1); // starting the model
			ModelrunsEntity.setNameforoutputdb(main.getOutputDatabaseName());
			ModelrunsDao.update(ModelrunsEntity);		
			
			

			main.runStep(locationTypes, htsHdlr, timePlanFileProcessor,
					databaseCreator);
			
			logger.debug("All done --- complete the model");

		} catch (Exception e) {			
			ModelrunsEntity.setStatus(3); // fail to run the model
			ModelrunsDao.update(ModelrunsEntity);
			logger.fatal("Failed to run model", e);
			System.exit(1);
		}

		try {			
			ModelrunsEntity.setStatus(2); // model completed , processing of the
										// output data 
			ModelrunsEntity.setTimeFinished(new Timestamp(new Date().getTime()));			
			ModelrunsDao.update(ModelrunsEntity);
			logger.debug("model completed , processing of the output data commenced");
			
			ModelMain main = getInstance();
			main.transfer_data();
			
			ModelrunsEntity.setStatus(4); // model finished, output data processed
			ModelrunsDao.update(ModelrunsEntity);
			logger.debug("model finished, output data processed");
			
			

		} catch (Exception e) {
			ModelrunsEntity.setStatus(5); // model finished, error in processing the output data
			ModelrunsDao.update(ModelrunsEntity);		
			logger.debug("model finished, error in processing the output data");
		}

	}

	// =================================================================
	private void initialConfiguration() throws ConfigurationException {

		HardcodedData.prepend();

		EnvironmentConfig environmentConfig = ApplicationContextHolder
				.getBean(EnvironmentConfig.class);

		PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
				new File(environmentConfig.getConfigPath()
						+ "/environment.properties"));

		int runsId = Integer.valueOf((String) propertiesConfiguration
				.getProperty("runsid"));
		logger.info("runs id:" + runsId);

		ModelrunsDao = ApplicationContextHolder.getBean(RunsDAO.class);
		ModelrunsEntity = ModelrunsDao.findOne(runsId);
		int configurationId = ModelrunsEntity.getConfigurationId();

		ConfigurationDAO configurationDAO = ApplicationContextHolder
				.getBean(ConfigurationDAO.class);
		ConfigurationEntity configurationEntity = configurationDAO
				.findOne(configurationId);

		this.transimsFrequency = "yearly";
		this.agentNumber = configurationEntity.getAgentNumber();
		this.yearNumber = configurationEntity.getRunningYear();
		this.scenarioName = configurationEntity.getShapefile();
		this.currentYear = configurationEntity.getStartYear();
		this.dwellingSchema = "dwelling";

		HardcodedData.setSeed(ModelrunsEntity.getSeed());
		this.setSeed(ModelrunsEntity.getSeed());
		this.setSelectedDatabaseName(configurationEntity.getNameofscenario());

		// GeoDBReader postgisGeoDBReader = GeoDBReader.getPostgisInstance();
		// postgisGeoDBReader.overwriteDatabase(selectedDatabaseName,"postgres_TransportNSW");
		// GeoDBReader postgresGeoDBReader =
		// GeoDBReader.getPostgresTransportNSWInstance();
		// while (true) {
		// try {
		// if (postgresGeoDBReader.isValidated()) {
		// break;
		// }
		// } catch (Exception e) {
		// logger.error(e);
		// }
		// }

		// load in application.properties file which includes some Transims
		// folder paths
		this.configuration = new PropertiesConfiguration(
				HardcodedData.properties);
		
	}

	// =================================================================
	/**
	 * Create travel diary for individual in the initial year.
	 * 
	 * @param locationTypes
	 * @param htsHdlr
	 */
	private void initialTravelDiary(LocationTypes locationTypes,
			HTSDataHandler htsHdlr)  throws Exception {
		try
		{
			locationTypes.readLocationTypes();

			// list of household with Travel Diary for writing to csv files
			List<List<int[][]>> tdWeekdaysAllHholds = new ArrayList<>();
			List<List<int[][]>> spTDAllHholds = new ArrayList<>();

			/* assign Travel Diary for all households */
			for (Household household : this.householdPool.getHouseholds().values()) {

				/*
				 * create spHhold from Household Pool spHhold = [hholdID, indivID,
				 * hhholdRelationship, hholdType]
				 */
				int[][] spHhold = htsHdlr.transform(household);

				List<int[][]> spTD = htsHdlr.setTDtoSPHhold(spHhold);
				/*
				 * spTD = [hholdID, indivID, tripID, departure time, trip time,
				 * mode, purpose, origin, destination] with origin = destination = 0
				 */

				List<int[][]> spTDwOD = htsHdlr.setODtoSpTD(spTD, locationTypes);
				/*
				 * spTDwOD = [hholdID, indivID, tripID, departure time, trip time,
				 * mode, purpose, origin, destination] with "origin" and
				 * "destination" are facilities type ID
				 */

				/* transfer spTD into individual's travel diary */
				htsHdlr.transferSpTD2IndividualTD(spTDwOD);

				spTDAllHholds.add(spTD);
				tdWeekdaysAllHholds.add(spTDwOD);
			}

			// /* write to csv */
			// TextFileHandler.writeSpTD2CSV(spTDAllHholds, "1_SetTDtoSpHhold.csv");
			//
			// TextFileHandler.writeSpTD2CSV(tdWeekdaysAllHholds,
			// "2_AfterAddODpairs2TD.csv");

			/* clear memory */
			spTDAllHholds = null;
			tdWeekdaysAllHholds = null;

			/* retrieving process link */
			logger.debug("Retrieving ProcessLinks");
			this.processLink = this.processLinkDAO.findAllToIds();

			/* setup the parking manager */
			ParkingManager.getInstance();

			/* setup the activity manager */
			logger.debug("Setting up the Activity Locations.");
			ActivityLocationManager.getInstance().createMapActLocIDTz06();

			/* retrieving vehicle list */
			logger.debug("Retrieving vehiclesList");
			this.vehicleList = VehicleManager.createVehicleList(dwellingAllocator,
					householdPool);

			// household initialise hmHouseHold_TravelDiariesChange to indicate that
			// all households in the population have their travel diaries changed
			this.householdPool.initialiseTravelDiariesChange();

			// /* write to csv */
			// TextFileHandler.writeTravelDiaryToCSV(System.getProperty("user.dir")
			// + "/2_Before_initial_setLocation.csv", modelMain);

			/* setting up location */
			logger.debug("Setting up location");
			ActivityLocationManager.getInstance().setLocation_1(dwellingAllocator,
					householdPool);

			// /* write to csv */
			// TextFileHandler.writeTravelDiaryToCSV(System.getProperty("user.dir")
			// + "/3_After_initial_setLocation.csv", modelMain);

			/* patch 1 for Travel Diary */
			// htsHdlr.runPatch1();

			// /* write to csv */
			// TextFileHandler.writeTravelDiaryToCSV("4_After_Patch1.csv",
			// modelMain);

			/* assign vehicle ID for trips */
			logger.debug("Setting vehicle ID");
			VehicleManager.amendVehicleID(scenarioName, householdPool);

			// /* write to csv */
			// TextFileHandler.writeTravelDiaryToCSV(
			// "6_After_initial_setVehicleID.csv", modelMain);
		}catch (Exception e) {
			logger.error("Exception caught from initialTravelDiary: ", e);
			throw e;
		}
		

	}

	// =================================================================
	/**
	 * Create an output database and input data for the initial year.
	 * 
	 * @param databaseCreator
	 */
	private void initialDatabase(DatabaseCreator databaseCreator)  throws Exception {
		try
		{
			logger.debug("Inputing data for initial year");

			/* create database */
			JdbcTemplate jdbcTemplate = createDatabase(databaseCreator);

			Map<Integer, int[]> occupancy = this.dwellingAllocator
					.getDwellingsOccupied(this.householdPool, this.currentYear);

			StoreThread store = new StoreThread(jdbcTemplate);

			/* input data for initial year */
			logger.info("Storing data for year " + this.currentYear + "...");
			store.storeOccupancyCounts(occupancy, this.currentYear);
			storeTravelModeBeforeTMC();
			// databaseCreator.insertTripsMode(this.currentYear);
			store.storeTravelModeChoiceOutput(savesModeChoiceInfo(),
					this.currentYear);

			// calculates satisfaction of initial population
			TravelZonesFacilitiesDAO tzFacilitiesDAO = ApplicationContextHolder
					.getBean(TravelZonesFacilitiesDAO.class);
			WeightsDAO weightsDAO = ApplicationContextHolder
					.getBean(WeightsDAO.class);
			LiveabilityUtility liveabilityUtility = LiveabilityUtility
					.getInstance();
			for (Household hhold : this.householdPool.getHouseholds().values()) {
				for (Individual indiv : hhold.getResidents()) {
					int travelZoneId = indiv.getLivedTravelZoneid();
					TravelZonesFacilitiesEntity travelZonesFacilities = tzFacilitiesDAO
							.getByMap().get(travelZoneId);
					WeightsEntity weights = weightsDAO
							.findByDemographic(DemographicInfomation
									.classifyIndividual(indiv));
					double liveability = liveabilityUtility.calculateLiveability(
							weights, travelZonesFacilities, indiv.getAge(),
							indiv.getIncome(), indiv.getHholdCategory(), 0);
					double satisfaction = liveabilityUtility
							.calculateSatisfactory((int) hhold.getTenure(),
									indiv.getGender(), liveability);
					indiv.setSatisfaction(satisfaction);
				}
			}

			// stores initial population
			this.individualPool.storeIndividualInfomation(this.currentYear,
					this.householdPool);

			// stores average satisfaction of travel zones
			store.setYear(this.currentYear);
			store.storeSatisfaction();

			// this.individualPool.removeDuplicate();	
		}catch (Exception e) {
			logger.error("Exception caught from initialDatabase: ", e);
			throw e;
		}
		
	}

	// =================================================================
	/**
	 * Store the output from model runs into database.
	 * 
	 * @param travelZoneTripsTime
	 * @param databaseCreator
	 */
	private void storeDatabase(Map<Integer, Double> travelZoneTripsTime,
			DatabaseCreator databaseCreator,
			List<String[]> timeplanOutputArrayList,
			List<String[]> linkDelayOutputArrayList,
			List<String[]> testImmigrants, List<String[]> testEmigrants,
			List<String[]> testEquity, List<String[]> testNotrelocHhold,
			List<String[]> testRelocHhold, List<String[]> testSPAfterReloc,
			List<String[]> deadPeople, List<String[]> newborns,
			List<String[]> travModeChoice) {

		this.individualPool.storeRemovedIndividualInfomation(this.currentYear,
				this.removedHouseholds);

		Map<Integer, int[]> occupancy = this.dwellingAllocator
				.getDwellingsOccupied(householdPool, this.currentYear + 1);

		this.individualPool.storeIndividualInfomation(this.currentYear + 1,
				this.householdPool);

		Thread storeThread = new Thread(new StoreThread(this.currentYear + 1,
				databaseCreator, travelZoneTripsTime, occupancy,
				timeplanOutputArrayList, linkDelayOutputArrayList,
				testImmigrants, testEmigrants, testEquity, testNotrelocHhold,
				testRelocHhold, testSPAfterReloc, deadPeople, newborns,
				travModeChoice));

		storeThread.start();

	}

	// =================================================================
	@SuppressWarnings("boxing")
	private void initialPool() throws Exception {
		try
		{
			this.individualPool = new IndividualPool();
			this.householdPool = new HouseholdPool();
			this.aLifeEventProbability = new LifeEventProbability();
			/**
			 * just for speeding up the debug mode!!! remove "//" the following line
			 * in the run mode.
			 */

			// ----------normal----------
			this.individualPool.generatePool(this.agentNumber);

			// ---------------test----
			// this.individualPool.generatePoolFromHousehold(this.agentNumber,
			// syntheticPopulationDAO);

			// populate householdPool by populated individualPool
			this.householdPool.generatePool(this.individualPool);

			this.dwellingAllocator = new DwellingAllocator();

			this.dwellingAllocator.getDwellingPool().initialAll(
					this.dwellingSchema, this.currentYear + this.yearNumber - 1);

			// initial household index in DB
			this.householdPool.allocateDwellingsForInitialisation(
					this.dwellingAllocator, this.currentYear);

			int individualNumber = this.individualPool.getPoolNumber();
			int householdNumber = this.householdPool.getPoolNumber();

			this.householdPool.removeNoDwellingHousehold(this.individualPool,
					this.dwellingAllocator);

			logger.debug("Before remove no dwelling households: "
					+ individualNumber + " : " + householdNumber);
			logger.debug("After remove no dwelling households: "
					+ this.individualPool.getPoolNumber() + " : "
					+ this.householdPool.getPoolNumber());

			logger.debug(this.currentYear);

			// initialises tenure for households in the population
			// weibull distribution for initial tenture for households in the
			// synthetic population
			WeibullDistribution weibull = new WeibullDistribution(SHAPE, SCALE);
			for (Household hhold : this.householdPool.getHouseholds().values()) {
				double tenure = weibull.sample();
				hhold.setTenure(tenure);
			}

			// randomly create agents if the input value of agents
			// is greater than the default synthetic population
			if (this.agentNumber > MAX_SYNTHETIC_POPULATION) {
				RandomAgentsGenerator randomAgentsGenerator = new RandomAgentsGenerator();
				// randomAgentsGenerator.readFile(HardcodedData.tzsAlongLightRail);
				randomAgentsGenerator.generateAgents(MAX_SYNTHETIC_POPULATION,
						this.agentNumber - MAX_SYNTHETIC_POPULATION,
						HardcodedData.random, this.householdPool,
						this.individualPool, this.dwellingAllocator,
						this.currentYear);
			}

			/*
			 * allocates ownershipStatus to each household in this.householdPool by
			 * checking if their income can afford mortgage payment for the dwelling
			 * they're living in. If yes, set ownershipStatus to "paying". Otherwise
			 * set it to "renting".
			 */
			// this.householdPool.initialiseOwnership();
			List<NewTendbyBerdrEntity> rawtbd = this.newTendbyBerdrDAO.findAll();
			this.householdPool.initialiseOwnership(rawtbd);

			// store a copy of the initial synthetic population for later use in
			// creating migrants.
			this.immigrantGenerator = new ImmigrantGenerator(this.householdPool);

			logger.info("Total Agents: " + this.individualPool.getPoolNumber());
			logger.info("total household: " + this.householdPool.getPoolNumber());
		}catch (Exception e) {
			logger.error("Exception caught from initialPool: ", e);
			throw e;
		}
		
	}

	// =================================================================
	/**
	 * 
	 * @throws Exception
	 */
	private void initialModel() throws Exception {
		File modelDirAsFile = new File(HardcodedData.rsLocation); // the
		// scenario
		// dir

		try {
			this.modelRunner.load(modelDirAsFile); // load the repast scenario
			this.modelRunner.runInitialize(); // initialize the run
		} catch (Exception e) {
			logger.error("Exception caught", e);
			throw e;
		}
		
	}

	// =================================================================
	/**
	 * Run the model.
	 * 
	 * @param locationTypes
	 * @param htsHdlr
	 * @param timePlanFileProcessor
	 * @param databaseCreator
	 */
	public void runStep(LocationTypes locationTypes, HTSDataHandler htsHdlr,
			TimePlanFileProcessor timePlanFileProcessor,
			DatabaseCreator databaseCreator) throws Exception {
		try
		{
			// sets up the flag for controlling the frequency of executing of
			// Transims and agent logic
			boolean isRun = false;

			for (int j = 1; j <= this.yearNumber; j++) {
				List<String[]> timeplanOutputArrayList = new ArrayList<>();
				List<String[]> linkDelayOutputArrayList = new ArrayList<>();

				for (int i = 1; i <= HardcodedData.maxTick; i++) {
					logger.info("Month: " + i);

					switch (this.transimsFrequency) {
					case "monthly":
						isRun = true;
						break;
					case "yearly":
						if (i == 1) {
							isRun = true;
						}
						break;
					case "10% changed":
						if (Double.valueOf(this.agentTravelChanged)
								/ Double.valueOf(this.agentNumber) > 0.1
								|| i == 1) {
							isRun = true;
							this.agentTravelChanged = 0;
						}
						break;
					case "50% changed":
						if (Double.valueOf(this.agentTravelChanged)
								/ Double.valueOf(this.agentNumber) > 0.5
								|| i == 1) {
							isRun = true;
							this.agentTravelChanged = 0;
						}
						break;
					}

					// Call TRANSIMS to run
					if (isRun) {

						/* 1 - TRANSIMS_preprocessing */
						transimsPreprocessing();
						// TextFileHandler.writeTravelDiaryToCSV(String.valueOf(getCurrentYear())+"_AfterTransimsPreprocessing.csv",
						// modelMain);

						/* 2 -Running TRANSIMS */
						logger.info("Started TRANSIMS");
						TransimsRunner transimsRunner = new TransimsRunner();
						transimsRunner.step(1);

						// Running TRANSIMS with simulator
						// transimsRunner.step(2);

						TransimsRunner.getTransimsThreadWeekday().deleteFile();
						TransimsRunner.getTransimsThreadWeekend().deleteFile();
						logger.info("TRANSISM finished");

						/* 3 - TRANSIMS_postprocessing */
						logger.info("TRANSISM's postprocessing...");

						List<List<String[]>> transimsPostProcessingOutputs = transimsPostprocessing(timePlanFileProcessor);
						timeplanOutputArrayList = transimsPostProcessingOutputs
								.get(0);
						linkDelayOutputArrayList = transimsPostProcessingOutputs
								.get(1);

						/* 4 - Agent making a decision */
						// store travel mode before making TMC
						if (getHhppTripModeBeforeTMC().isEmpty()
								|| getHhppTripModeBeforeTMC().size() == 0) {
							storeTravelModeBeforeTMC();
						}
						logger.info("Travel mode choice's running...");
						this.modelRunner.step();
						// TextFileHandler.writeTravelDiaryToCSV(String.valueOf(getCurrentYear())+"_AfterTravelModeChoice.csv",
						// modelMain);

						isRun = false;
					}
				}

				/* model's post_processing */
				logger.debug("Model's postprocessing...");
				modelPostprocessing(timePlanFileProcessor, htsHdlr, locationTypes,
						databaseCreator, timeplanOutputArrayList,
						linkDelayOutputArrayList);

				this.percentageTick = (int) Math.round((float) j / this.yearNumber
						* 100.0);

				this.currentYear++;
				this.removedHouseholds.clear();
			}

			this.modelRunner.stop(); // execute any actions scheduled at run end
			this.modelRunner.cleanUpRun();
			this.modelRunner.cleanUpBatch();	
		}catch (Exception e) {
			logger.error("Exception caught from runStep: ", e);
			throw e;
		}

		

	}

	private void storeTravelModeBeforeTMC() {
		ArrayList<String[]> indivNoTD = new ArrayList<String[]>();
		for (Household hhold : getHouseholdPool().getHouseholds().values()) {
			for (Individual indiv : hhold.getResidents()) {
				int[][] td = indiv.getTravelDiariesWeekdays();
				if (td == null) {
					indivNoTD.add(new String[] { String.valueOf(hhold.getId()),
							String.valueOf(indiv.getId()) });
					continue;
				}
				for (int itd = 0; itd <= td.length - 1; itd++) {
					String hhppTrip = String.valueOf(hhold.getId())
							+ "_"
							+ String.valueOf(indiv.getId())
							+ "_"
							+ String.valueOf(td[itd][TravelDiaryColumns.TripID_Col
									.getIntValue()]);
					getHhppTripModeBeforeTMC().put(
							hhppTrip,
							td[itd][TravelDiaryColumns.TravelMode_Col
									.getIntValue()]);
				}
			}
		}
		// TextFileHandler.writeToCSV(String.valueOf(getCurrentYear())+"_noTD.csv",
		// indivNoTD, false);
	}

	// =================================================================
	/**
	 * All the post_processes that occur after TRANSIMS running.
	 * 
	 * @param timePlanFileProcessor
	 * @param htsHdlr
	 * @param locationTypes
	 * @param databaseCreator
	 */
	private void modelPostprocessing(
			TimePlanFileProcessor timePlanFileProcessor,
			HTSDataHandler htsHdlr, LocationTypes locationTypes,
			DatabaseCreator databaseCreator,
			List<String[]> timeplanOutputArrayList,
			List<String[]> linkDelayOutputArrayList) {

		// creates a foothold to store households that need to be relocated,
		// including migrant households
		Set<Household> relocatedHouseholds = new HashSet<Household>();

		String csvFilename = HardcodedData.linkList + "/Link_list.csv";
		TripsTimeProcessor tripsTimeProcessor = new TripsTimeProcessor(
				csvFilename);

		Map<Integer, Double> travelZoneTripsTime = tripsTimeProcessor
				.calculateNormalizedTravelZoneAverageTripsTime(timePlanFileProcessor
						.getLinksTripsTime());
		// HashMap<Integer, Double> travelZoneTripsTime = new HashMap<Integer,
		// Double>();
		// for (int tmpi=0; tmpi<=HardcodedData.travelZones.length-1; tmpi++) {
		// travelZoneTripsTime.put(HardcodedData.travelZones[tmpi], 0.1);
		// }

		// saves information of travel modes of this population before its
		// evolution.
		// this information will be stored to output database table in step 6
		// below.
		ArrayList<String[]> travelModeChoiceOutput = savesModeChoiceInfo();

		/* 1 - Household evolution */
		ArrayList<String[]> deadPeople = new ArrayList<>();
		ArrayList<String[]> newborns = new ArrayList<>();
		ArrayList<String[]> testEquity = new ArrayList<>();

		Map<Integer, String[][]> hmHouseholdOldKey = this.householdPool
				.storeKeys();
		this.householdPool.evolution(this.individualPool,
				this.aLifeEventProbability, HardcodedData.random,
				this.currentYear, travelZoneTripsTime, this.dwellingAllocator,
				relocatedHouseholds, testEquity, deadPeople, newborns);

		/* 2 - Immigration */
		ArrayList<String[]> testImmigrants = new ArrayList<>();
		householdPool.generateImmigrants(individualPool, relocatedHouseholds,
				this.immigrantGenerator, this.immigrationRateDAO,
				this.currentYear, testImmigrants);

		/*
		 * 3 - Relocate new households, including those from evolution
		 * (marriage, divorce) and from immigration.
		 */
		// increase currentYear by 1 so that the next year supply of dwelling
		// stocks is used for relocation.
		this.dwellingAllocator.outputDwellingStocks(this.currentYear + 1);
		ArrayList<String[]> testEmigrants = new ArrayList<>();
		ArrayList<String[]> testNotRelocHhold = new ArrayList<>();
		ArrayList<String[]> testRelocHhold = new ArrayList<>();
		ArrayList<String[]> testSPAfterReloc = new ArrayList<>();
		this.householdPool.relocate(individualPool, this.currentYear + 1,
				travelZoneTripsTime, dwellingAllocator, relocatedHouseholds,
				this.removedHouseholds, testEmigrants, testNotRelocHhold,
				testRelocHhold, testSPAfterReloc);
		// this.householdPool.removeNoDwellingHousehold(this.individualPool,
		// this.dwellingAllocator);

		/*
		 * 4 - Clear relocated Households so that it stores only households to
		 * be relocated in the next year
		 */
		relocatedHouseholds.clear();
		logger.debug("The number of removed households is: "
				+ this.removedHouseholds.size());

		/* 5 - Assign new Travel Diary for changed household only */
		Map<Integer, String[][]> hmHouseholdNewKey = this.householdPool
				.storeKeys();
		assignTravelDiaryOnTheFly(htsHdlr, locationTypes, hmHouseholdOldKey,
				hmHouseholdNewKey);
		// TextFileHandler.writeTravelDiaryToCSV(String.valueOf(getCurrentYear())+"_AfterTravelDairyOTF.csv",
		// modelMain);

		/* 6 - Store output into database */
		logger.info("Storing data for year " + (this.currentYear + 1) + "...");
		storeDatabase(travelZoneTripsTime, databaseCreator,
				timeplanOutputArrayList, linkDelayOutputArrayList,
				testImmigrants, testEmigrants, testEquity, testNotRelocHhold,
				testRelocHhold, testSPAfterReloc, deadPeople, newborns,
				travelModeChoiceOutput);

		/* write to csv */
		if (logger.isTraceEnabled()) {
			TextFileHandler.writeHashMapStringDoubleArrToCSV(
					getTripTimePlanMap(), -1, "tripTimePlanMap_" + currentYear
							+ ".csv");
			TextFileHandler.writeHashMapStringDoubleArrToCSV(
					getUltTravelModeMap(), -1, "ult_modes_" + currentYear
							+ ".csv");
			TextFileHandler.writeHashMapStringDoubleArrToCSV(
					getProTravelModeMap(), -1, "pro_modes_" + currentYear
							+ ".csv");
			TextFileHandler.writeHashMapStringDoubleArrToCSV(
					getCumProTravelModeMap(), -1, "cumPro_modes_" + currentYear
							+ ".csv");
		}

		// Clear out data structures at end of post processing
		getUltTravelModeMap().clear();
		getProTravelModeMap().clear();
		getCumProTravelModeMap().clear();
		setHhppTripModeBeforeTMC(new HashMap<String, Integer>());
		hmHhPersonTripTravelMode = new HashMap<String, Integer>();
		travelModeChoiceOutput = new ArrayList<String[]>();

	}

	/**
	 * saves information of travel modes of this population before its
	 * evolution.
	 * 
	 * @return
	 */
	private ArrayList<String[]> savesModeChoiceInfo() {
		ArrayList<String[]> travelModeChoiceOutput = new ArrayList<String[]>();
		for (Household hhold : householdPool.getHouseholds().values()) {
			for (Individual indiv : hhold.getResidents()) {
				int[][] td = indiv.getTravelDiariesWeekdays();
				if (td == null)
					continue;
				for (int i = 0; i <= td.length - 1; i++) {
					String hhppTrip = String.valueOf(hhold.getId())
							+ "_"
							+ String.valueOf(indiv.getId())
							+ "_"
							+ String.valueOf(td[i][TravelDiaryColumns.TripID_Col
									.getIntValue()]);
					String oldMode = "N/A";
					if (getHhppTripModeBeforeTMC().containsKey(hhppTrip)
							&& TravelModes.classify(
									getHhppTripModeBeforeTMC().get(hhppTrip))
									.getTranssimsName() != null) {
						oldMode = TravelModes.classify(
								getHhppTripModeBeforeTMC().get(hhppTrip))
								.getTranssimsName();
					}
					String newMode = TravelModes.classify(
							td[i][TravelDiaryColumns.TravelMode_Col
									.getIntValue()]).getTranssimsName();
					String purpose = TimePlanFileProcessor
							.getPurposeString(td[i][TravelDiaryColumns.Purpose_Col
									.getIntValue()]);

					travelModeChoiceOutput
							.add(new String[] {
									String.valueOf(indiv.getId()),
									String.valueOf(hhold.getId()),
									newMode,
									purpose,
									String.valueOf(td[i][TravelDiaryColumns.Origin_Col
											.getIntValue()]),
									String.valueOf(td[i][TravelDiaryColumns.Destination_Col
											.getIntValue()]), oldMode });
				}
			}
		}
		return travelModeChoiceOutput;
	}

	// =================================================================
	/**
	 * 
	 * @return
	 */
	private List<String[]> processLinkDelayFile() {
		HardcodedData.setLinkDelayFileLocation("weekdays/");
		ArrayList<String[]> linkDelayOutputArrayList = new ArrayList<>();

		try {
			String[] line;

			/* PROCESS THE LINK DELAY FILE */
			CSVReader csvReader = new CSVReader(new BufferedReader(
					new FileReader(HardcodedData.getLinkDelayFileLocation())),
					'\t');

			// skip the header rows
			csvReader.readNext();

			while ((line = csvReader.readNext()) != null) {
				linkDelayOutputArrayList.add(line);
			}

			csvReader.close();

		} catch (Exception e) {// Catch exception if any
			logger.error("Exception caught", e);
		}

		return linkDelayOutputArrayList;
	}

	// =================================================================
	/**
	 * Assign the Travel Diary for the households which have changes in their
	 * household type.
	 * 
	 * @param htsHdlr
	 * @param locationTypes
	 * @param hmHouseholdOldKey
	 * @param hmHouseholdNewKey
	 */
	private void assignTravelDiaryOnTheFly(HTSDataHandler htsHdlr,
			LocationTypes locationTypes,
			Map<Integer, String[][]> hmHouseholdOldKey,
			Map<Integer, String[][]> hmHouseholdNewKey) {
		// list of household with Travel Diary for writing to csv files
		List<List<int[][]>> tdWeekdaysAllHholds = new ArrayList<>();
		List<List<int[][]>> spTDAllHholds = new ArrayList<>();

		try {
			this.householdPool
					.checkChange(hmHouseholdOldKey, hmHouseholdNewKey);

			for (Household household : this.householdPool.getHouseholds()
					.values()) {

				if (household.isTravelDiariesChanged()) {

					int[][] spHhold = htsHdlr.transform(household);
					// spHhold = [hholdID, indivID, hhholdRelationship,
					// hholdType]

					List<int[][]> spTD = htsHdlr.setTDtoSPHhold(spHhold);

					List<int[][]> spTDwOD = htsHdlr.setODtoSpTD(spTD,
							locationTypes);
					/*
					 * spTDwOD = [hholdID, indivID, tripID, departure time, trip
					 * time, mode, purpose, origin, destination] with "origin"
					 * and "destination" are facilities type ID
					 */

					/* transfer spTD into individual's travel diary */
					htsHdlr.transferSpTD2IndividualTD(spTDwOD);

					spTDAllHholds.add(spTD);
					tdWeekdaysAllHholds.add(spTDwOD);
				}
			}
		} catch (Exception e) {
			logger.error("Exception caught", e);
		}

		/* clear memory */
		spTDAllHholds = null;
		tdWeekdaysAllHholds = null;

		/* retrieving vehicle list again each year */
		logger.debug("Retrieving vehiclesList");
		this.vehicleList = VehicleManager.createVehicleList(
				this.dwellingAllocator, householdPool);

		logger.debug("Set location");
		ActivityLocationManager.getInstance().setLocation_1(
				this.dwellingAllocator, householdPool);

		/* patch 1 for Travel Diary */
		// htsHdlr.runPatch1();

		logger.debug("Amend vehicle id");
		VehicleManager.amendVehicleID(scenarioName, householdPool);

	}

	// =================================================================
	/**
	 * All processed of creating TRANSIMS input files.
	 * 
	 */
	private void transimsPreprocessing() {
		/* initial the hash maps for travel mode choice algorithm */
		this.ultTravelModeMap = new HashMap<>();
		this.proTravelModeMap = new HashMap<>();
		this.cumProTravelModeMap = new HashMap<>();
		this.tripTimePlanMap = new HashMap<>();

		// add trips with Origin outside study area into the trip
		// list for adjusting the density in the model
		List<String[]> tripListFromOutsideStudyArea = TransimsRunner
				.addJTWtripsFromOutsideStudyArea();

		// create the Syd.TRIPS and Syd.VEHICLE files for TRANSIMS
		TranSimsInputGenerator tranSimsInputGenerator = new TranSimsInputGenerator();

		tranSimsInputGenerator.setFileLocation("weekdays/");
		tranSimsInputGenerator.generateNewInputFiles("weekdays",
				tripListFromOutsideStudyArea);
		// tranSimsInputGenerator.setFileLocation("weekend/");
		// tranSimsInputGenerator.generateNewInputFiles("weekend",
		// tripListFromOutsideStudyArea);
	}

	// =================================================================
	/**
	 * Backup and analyse TRANSIMS output file.
	 * 
	 * @param timePlanFileProcessor
	 */
	private List<List<String[]>> transimsPostprocessing(
			TimePlanFileProcessor timePlanFileProcessor) {

		ArrayList<List<String[]>> returnArrayList = new ArrayList<>();

		/* interaction with TIMEPLANS file */
		timePlanFileProcessor.readFile(HardcodedData.travelCost
				+ this.currentYear + ".csv", this.householdPool);

		/* patch 2 for Travel Diary */
		timePlanFileProcessor.readFileFixedTimeCarPax();

		// post-processing the TIMEPLANS file for output visualisation
		List<String[]> timeplanOutputArrayList = timePlanFileProcessor
				.postProcessTIMEPLANS(HardcodedData.getTimePlanFileLocation(),
						HardcodedData.getTripfileLocation());

		/* post-processing the Link Delay file */
		List<String[]> linkDelayOutputArrayList = processLinkDelayFile();

		returnArrayList.add(timeplanOutputArrayList);
		returnArrayList.add(linkDelayOutputArrayList);

		// reinitialises maps related to travel diaries for next simulation year
		driverTimeMap = new HashMap<>();
		u15HhodCarPaxCarDriverMap = new HashMap<>();
		otherHhodCarPaxCarDriverMap = new HashMap<>();

		backupTransimsOutputs();

		return returnArrayList;
	}

	private void backupTransimsOutputs() {
		/* backup TRANSIMS files */
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(); // get current date time with Date()

		HardcodedData.setTransimsPath("weekdays/");

		File results = new File(HardcodedData.getTransimsPath() + "/results");
		File demand = new File(HardcodedData.getTransimsPath() + "/demand");
		File demand50_TimeplansRaw = new File(HardcodedData.getTransimsPath()
				+ "/demand50/3.Syd.TIMEPLANS");
		File demand50_TimeplansDef = new File(HardcodedData.getTransimsPath()
				+ "/demand50/3.Syd.TIMEPLANS.def");
		File demand50_TimeplansNew = new File(HardcodedData.getTransimsPath()
				+ "/demand50/3.Syd.TIMEPLANS_new.csv");
		File demand50_Trips = new File(HardcodedData.getTransimsPath()
				+ "/demand50/Syd.Trips");

		File resultsBackUp = new File(HardcodedData.getTransimsPath() + "/"
				+ String.valueOf(this.currentYear + 1) + "/results_"
				+ dateFormat.format(date));
		File demand50BackUp = new File(HardcodedData.getTransimsPath() + "/"
				+ String.valueOf(this.currentYear + 1) + "/demand50_"
				+ dateFormat.format(date));
		File demandBackUp = new File(HardcodedData.getTransimsPath() + "/"
				+ String.valueOf(this.currentYear + 1) + "/demand_"
				+ dateFormat.format(date));

		try {
			FileUtils.copyDirectory(results, resultsBackUp);
			FileUtils.copyDirectory(demand, demandBackUp);
			FileUtils
					.copyFileToDirectory(demand50_TimeplansRaw, demand50BackUp);
			FileUtils
					.copyFileToDirectory(demand50_TimeplansDef, demand50BackUp);
			FileUtils
					.copyFileToDirectory(demand50_TimeplansNew, demand50BackUp);
			FileUtils.copyFileToDirectory(demand50_Trips, demand50BackUp);
		} catch (IOException e) {
			logger.error("Exception caught", e);
		}

	}

	// =================================================================
	private JdbcTemplate createDatabase(DatabaseCreator databaseCreator) {
		logger.debug("Create Database");

		JdbcTemplate template = databaseCreator.createDatabase();
		databaseCreator.createTravelZoneOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createSyntheticPopulationOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createRemovedSyntheticPopulationOutputTable(
				this.currentYear, this.yearNumber);
		databaseCreator.createTravelModeChoiseOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createDwellingOccupancyCountsTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createTransimsOutputTimePlansTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createTransimsOutputLinkDelayTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createImmigrantOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createEmigrantOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createEquityOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createNotRelocHHOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createRelocHHOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createSPAfterRelocOutputTable(this.currentYear,
				this.yearNumber);

		databaseCreator.createDeadPeopleOutputTable(this.currentYear,
				this.yearNumber);
		databaseCreator.createNewbornsOutputTable(this.currentYear,
				this.yearNumber);

		// databaseCreator.createDeadSyntheticPopulationOutputTable(startYear,
		// yearNumber);

		return template;
	}

	// =================================================================
	public Configuration getConfiguration() {
		return this.configuration;
	}

	public IndividualPool getIndividualPool() {
		return this.individualPool;
	}

	public HouseholdPool getHouseholdPool() {
		return this.householdPool;
	}

	public List<Vehicle> getVehicleList() {
		return this.vehicleList;
	}

	public void setVehicleList(List<Vehicle> vehicleList) {
		this.vehicleList = vehicleList;
	}

	public List<Trips> getTripsListWeekdays() {
		return this.tripsListWeekdays;
	}

	public void setTripsListWeekdays(List<Trips> tripsListWeekdays) {
		this.tripsListWeekdays = tripsListWeekdays;
	}

	public List<Integer> getProcessLink() {
		return this.processLink;
	}

	public int getCurrentYear() {
		return this.currentYear;
	}

	public DwellingAllocator getDwellingAllocator() {
		return dwellingAllocator;
	}

	public int getAgentNumber() {
		return this.agentNumber;
	}

	public String getScenarioName() {
		return this.scenarioName;
	}

	public double getWalkToCarRatio() {
		return WALK_TO_CAR_RATIO;
	}

	public double getWalkToBusRatio() {
		return WALK_TO_BUS_RATIO;
	}

	public double getBusToCarRatio() {
		return BUS_TO_CAR_RATIO;
	}

	public double getWalkToBikeRatio() {
		return WALK_TO_BIKE_RATIO;
	}

	public double getBikeToCarRatio() {
		return BIKE_TO_CAR_RATIO;
	}

	public double getBikeToBusRatio() {
		return BIKE_TO_BUS_RATIO;
	}

	public Map<String, Integer> getHmHhPersonTripTravelMode() {
		return this.hmHhPersonTripTravelMode;
	}

	public static synchronized ModelMain getInstance() {

		if (modelMain == null) {
			modelMain = new ModelMain();
		}

		return modelMain;
	}

	public Map<String, double[]> getCumProTravelModeMap() {
		return this.cumProTravelModeMap;
	}

	public Map<String, double[]> getProTravelModeMap() {
		return this.proTravelModeMap;
	}

	public Map<String, double[]> getUltTravelModeMap() {
		return this.ultTravelModeMap;
	}

	public Map<String, String> getHhPersonTripConvertedMap() {
		return this.hhPersonTripConvertedMap;
	}

	public Map<String, double[]> getTripTimePlanMap() {
		return this.tripTimePlanMap;
	}

	public Map<String, double[]> getDriverTimeMap() {
		return this.driverTimeMap;
	}

	public Map<String, String> getU15HhodCarPaxCarDriverMap() {
		return this.u15HhodCarPaxCarDriverMap;
	}

	public Map<String, String> getOtherHhodCarPaxCarDriverMap() {
		return this.otherHhodCarPaxCarDriverMap;
	}

	public Map<String, String> getHmHhPersonTripTDPurpose() {
		return this.hmHhPersonTripTDPurpose;
	}

	/**
	 * @return the removedHouseholds
	 */
	public List<Household> getRemovedHouseholds() {
		return this.removedHouseholds;
	}

	/**
	 * @return the deadIndividuals
	 */
	public List<Individual> getDeadIndividuals() {
		return this.deadIndividuals;
	}

	public JTW2006DAO getJtw2006DAO() {
		return jtw2006DAO;
	}

	public String getSelectedDatabaseName() {
		return selectedDatabaseName;
	}

	public void setSelectedDatabaseName(String selectedDatabaseName) {
		this.selectedDatabaseName = selectedDatabaseName;
	}

	public String getOutputDatabaseName() {
		return OutputDatabaseName;
	}

	public void setOutputDatabaseName(String OutputDatabaseName) {
		this.OutputDatabaseName = OutputDatabaseName;
	}

	public static Map<String, Integer> getHhppTripModeBeforeTMC() {
		return hhppTripModeBeforeTMC;
	}

	public static void setHhppTripModeBeforeTMC(
			Map<String, Integer> hhppTripModeBeforeTMC) {
		ModelMain.hhppTripModeBeforeTMC = hhppTripModeBeforeTMC;
	}
}