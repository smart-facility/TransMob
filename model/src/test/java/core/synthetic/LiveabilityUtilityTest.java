package core.synthetic;


import hibernate.postgis.TravelZonesFacilitiesDAO;
import hibernate.postgres.MortgageInterestRateDAO;
import hibernate.postgres.MortgageInterestRateEntity;
import hibernate.postgres.WeightsDAO;
import hibernate.postgres.WeightsEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;
import core.ApplicationContextHolder;
import core.HardcodedData;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.liveability.LiveabilityUtility;
import core.synthetic.survey.DemographicInfomation;
import core.synthetic.LiveabilityTestCase;
import core.synthetic.SatisfactionTestCase;

public class LiveabilityUtilityTest {

	private LiveabilityUtility liveabilityUtility;
	private List<LiveabilityTestCase> liveabilityTestCases;
	private List<SatisfactionTestCase> satisfactionTestCases;
	private TravelZonesFacilitiesDAO travelZonesFacilitiesDAO;
    private WeightsDAO weightsDao;
    private MortgageInterestRateDAO ratesDao;

    private static final Logger logger = Logger.getLogger(LiveabilityUtilityTest.class);

	/**
	 * Setup related input
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// HardcodedData.prepend(System.getProperty("user.dir"));
		// dwellingAllocator = new DwellingAllocator();
		// syntheticPopulationDAO = new SyntheticPopulationDAO();
		// individualPool = new IndividualPool();
		// individualPool.generatePool(90626, syntheticPopulationDAO);
		liveabilityUtility = LiveabilityUtility.getInstance();
		// liveabilityUtility.setPopulation(individualPool);
		// householdPool = new HouseholdPool();
		// householdPool.generatePool(individualPool);
		// householdPool.updateDwellingsIndex(dwellingAllocator);
		// travelZoneCongestion = new HashMap<Integer, Double>();
		liveabilityTestCases = new ArrayList<LiveabilityTestCase>();
		satisfactionTestCases = new ArrayList<SatisfactionTestCase>();
		travelZonesFacilitiesDAO = new TravelZonesFacilitiesDAO();
        weightsDao = ApplicationContextHolder.getBean(WeightsDAO.class);
        ratesDao = ApplicationContextHolder.getBean(MortgageInterestRateDAO.class);
		//
		// readTestCases(file);

		HardcodedData.setPerformanceFileLocation("weekdays/");
		String csvFilename = HardcodedData.linkList + "/Link_list.csv";

		// dwellingAllocator = new DwellingAllocator();
		//
		// PerformanceFileProcessor performanceFileProcessor =
		// PerformanceFileProcessor
		// .getInstance(
		// "C:/Transims/RandwickSim/weekdays/demand50/3.Syd.TIMEPLANS",
		// csvFilename);
		// performanceFileProcessor.readFile();
		//
		// Map<String, Double> averageLinkSpeed = performanceFileProcessor
		// .getLinkAverageSpeed();
		// Map<String, Double> linkCongestion = performanceFileProcessor
		// .getLinkCongestion(averageLinkSpeed);
		// travelZoneCongestion = performanceFileProcessor
		// .calculateTravelZoneCongestion(linkCongestion);

		// for (int i = 0; i < HardcodedData.travelZonesLiveable.length; i++) {
		// travelZoneCongestion.put(HardcodedData.travelZonesLiveable[i], 0.5);
		// }

		// logger.debug(liveabilityTestCases.toString());

	}

	@After
	public void tearDown() throws Exception {
		liveabilityUtility = null;
		// individualPool = null;
		// householdPool = null;
		// travelZoneCongestion = null;
		// syntheticPopulationDAO = null;
		travelZonesFacilitiesDAO = null;
	}

	/**
	 * pick a random individual then put he/she into a random travel zone, then
	 * calculate the liveability.
	 */
	@Test
	public void testCalculateLiveability() {
		// readLiveabilityTestCases(new File(System.getProperty("user.dir")
		// + "/testCases/liveabilityTestCases.csv"));
		// //
		// // Individual individual1 = new Individual(1, 45, Gender.Male,
		// // new BigDecimal(2000), HouseholdRelationship.DeFacto,
		// // Occupation.Professionals, TransportModeToWork.Bus,
		// // HighestEduFinished.PostgraduateDegree, Category.HF2, 1, 1, 1);
		// // for() {
		// // int[] testId = { 1, 2 };
		// // int[] travelzoneId = { 294, 294 };
		// // for (int i = 0; i < testId.length; i++) {
		// // Individual individual1 = individualPool.getByID(testId[i]);
		// // int travelZoneID = travelzoneId[i];
		// //
		// // Weights weights = liveabilityUtility.getWeightsMap().getWeights(
		// // DemographicInfomation.classifyIndividual(individual1));
		// //
		// // TravelZonesFacilitiesEntity travelZonesFacilities =
		// // travelZonesFacilitiesDAO
		// // .findById(travelZoneID);
		// // liveabilityUtility.calculateLiveability(weights,
		// // travelZonesFacilities, individual1.getAge(),
		// // individual1.getIncome(), individual1.getHholdCategory(),
		// // travelZoneCongestion.get(travelZoneID));
		// // }
		// // }
		//
		// // System.out.println(individual1.toString());
		// //
		// // System.out.println("travelZoneID zone:" + travelZoneID);
		// //
		// // System.out.println(travelZonesFacilities.toString());
		// // System.out.println(weights.toString());
		// //
		// // System.out.println("travelZoneCongestion: "
		// // + travelZoneCongestion.get(travelZoneID));
		// // System.out.println("population: "
		// // + liveabilityUtility.getPopulation(travelZonesFacilities
		// // .getTz2006()));
		// //
		// //
		// System.out.println(liveabilityUtility.calculateLiveability(weights,
		// // travelZonesFacilities, individual1.getAge(),
		// // individual1.getIncome(), individual1.getHholdCategory(),
		// // travelZoneCongestion.get(travelZoneID)));
		// //
		// // System.out.println(liveabilityUtility.getTravelZonePopulation()
		// // .toString());
		// for (LiveabilityTestCase liveabilityTestCase : liveabilityTestCases)
		// {
		// logger.debug(liveabilityTestCase.toString());
		//
		// double liveability = liveabilityUtility.calculateLiveability(
		// liveabilityTestCase.getWeights(),
		// liveabilityTestCase.getTravelZonesFacilities(),
		// liveabilityTestCase.getAge(),
		// liveabilityTestCase.getIncomeBigDecimal(),
		// liveabilityTestCase.getCategory(),
		// liveabilityTestCase.getTravelZoneCongestion(),
		// liveabilityTestCase.getPopulation());
		//
		// // assertEquals(liveability, liveabilityTestCase.getExpectation(),
		// // 0.0001);
		//
		Individual individual = new Individual();
		individual.setAge(25);
		individual.setGender(Gender.Male);
		individual.setIncome(new BigDecimal(18000 / 52));

		WeightsEntity weights = weightsDao.findByDemographic(DemographicInfomation.classifyIndividual(individual));

		double liveability = liveabilityUtility.calculateLiveability(weights,
				7, 10, 5, 0, 6, 25, new BigDecimal(18000 / 52), Category.HF16,
				0.5, 500);
		System.out.println(liveability);
		// }

	}

	/**
	 * pick a random individual then put he/she into a random travel zone, then
	 * calculate the satisfaction.
	 */
    @Test @Ignore
	public void testCalculateSatisfaction() {

		readSatisfactionTestCases(new File(System.getProperty("user.dir")
				+ "/testCases/satisfactionTestCases.csv"));
		// Individual individual1 = new Individual(1, 45, Gender.Male,
		// new BigDecimal(2000), HouseholdRelationship.DeFacto,
		// Occupation.Professionals, TransportModeToWork.Bus,
		// HighestEduFinished.PostgraduateDegree, Category.HF2, 1, 1, 1);
		// int[] testId = { 4, 8 };
		// int[] travelzoneId = { 527, 528 };
		// for (int i = 0; i < testId.length; i++) {
		// Individual individual1 = individualPool.getByID(testId[i]);
		// int travelZoneID = travelzoneId[i];
		//
		// Weights weights = liveabilityUtility.getWeightsMap().getWeights(
		// DemographicInfomation.classifyIndividual(individual1));
		//
		// TravelZonesFacilitiesDAO travelZonesFacilitiesDAO = new
		// TravelZonesFacilitiesDAO();
		// TravelZonesFacilitiesEntity travelZonesFacilities =
		// travelZonesFacilitiesDAO
		// .findById(travelZoneID);
		//
		// double liveability = liveabilityUtility.calculateLiveability(
		// weights, travelZonesFacilities, individual1.getAge(),
		// individual1.getIncome(), individual1.getHholdCategory(),
		// -0.03149021464646468);
		//
		// // System.out.println(individual1.toString());
		// //
		// // System.out.println("travelZoneID zone:" + travelZoneID);
		// // System.out.println("liveability: " + liveability);
		// //
		// // System.out.println(liveabilityUtility.calculateSatisfactory(
		// // individual1.getYearsLivedIn(), individual1.getGender(),
		// // liveability));
		// }

		logger.debug(satisfactionTestCases.toString());

		for (SatisfactionTestCase satisfactionTestCase : satisfactionTestCases) {
			logger.debug(satisfactionTestCase.toString());

			double satisfaction = liveabilityUtility.calculateSatisfactory(
					satisfactionTestCase.getLivedYear(),
					satisfactionTestCase.getGender(),
					satisfactionTestCase.getLiveability());

			// assertEquals(satisfaction, satisfactionTestCase.getExpectation(),
			// 0.0001);
			System.out.println(satisfaction);
		}
	}

	/**
	 * test relocate function with special households
	 */
	// @Ignore("Not Ready to Run")
    @Test @Ignore
	public void testRelocate() {

		Set<Household> relocatedHouseholds = new HashSet<Household>();
		Map<Household, Integer> moveReason = new HashMap<Household, Integer>();

		// for (int i = 42; i < 43; i++) {
		// relocatedHouseholds.add(householdPool.getByID(i));
		// moveReason.put(householdPool.getByID(i), 1);
		// }

		// for (int year = 2006; year < 2007; year++) {
		//
		// householdPool.relocate(individualPool, year, travelZoneCongestion,
		// dwellingAllocator, relocatedHouseholds, moveReason);
		// }

	}

	/**
	 * test choose location function.
	 */
    @Test @Ignore
	public void testChooseLocation() {

		List<MortgageInterestRateEntity> rates = ratesDao.findAll();

		// Household household = householdPool.getByID(1);
		// Individual individual = household.getResidents().get(0);

		// liveabilityUtility
		// .chooseLocation(individual, household, 1, 2006,
		// interestRate.get(2006), travelZoneCongestion,
		// dwellingAllocator);
	}

	private void readLiveabilityTestCases(File file) {
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(file));

			String[] nextLine = csvReader.readNext();

			while ((nextLine = csvReader.readNext()) != null) {

				int id = Integer.parseInt(nextLine[0]);
				int age = Integer.parseInt(nextLine[5]);

				// int age = 0;
				// if (nextLine[5].equals("<30")) {
				// age = 25;
				// } else if (nextLine[5].equals("30--65")) {
				// age = 45;
				// } else if (nextLine[5].equals(">65")) {
				// age = 70;
				// }

				Gender gender = null;
				if (nextLine[6].equals("male")) {
					gender = Gender.Male;
				} else if (nextLine[6].equals("female")) {
					gender = Gender.Female;
				}

				double income = Integer.parseInt(nextLine[4]) / 52;
				// int income = 0;
				//
				// if (nextLine[4].equals("<20000")) {
				// income = 18000;
				// } else if (nextLine[4].equals("20000--40000")) {
				// income = 30000;
				// } else if (nextLine[4].equals("40000--100000")) {
				// income = 70000;
				// } else if (nextLine[4].equals(">100000")) {
				// income = 120000;
				// }

				BigDecimal incomeBigDecimal = new BigDecimal(income);

				int travelZoneId = Integer.parseInt(nextLine[2]);
				// travelZoneId =
				// HardcodedData.travelZonesLiveable[(travelZoneId - 1) * 8];

				Individual individual = new Individual();
				individual.setAge(age);
				individual.setGender(gender);
				individual.setIncome(incomeBigDecimal);

				WeightsEntity weights = weightsDao.findByDemographic(DemographicInfomation.classifyIndividual(individual));

				double travelZoneCongestion = Double.parseDouble(nextLine[7]);

				// double expectation = Double.parseDouble(nextLine[8]);

				Category category = Category.identify(nextLine[3]);

				int population = Integer.parseInt(nextLine[1]);

				double expectation = Double.parseDouble(nextLine[8]);

				LiveabilityTestCase liveabilityTestCase = new LiveabilityTestCase(
						id, weights,
						travelZonesFacilitiesDAO.findById(travelZoneId), age,
						incomeBigDecimal, category, travelZoneCongestion,
						expectation, population);

				liveabilityTestCases.add(liveabilityTestCase);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				csvReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readSatisfactionTestCases(File file) {
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(file));

			String[] nextLine = csvReader.readNext();

			while ((nextLine = csvReader.readNext()) != null) {
				int id = Integer.parseInt(nextLine[0]);
				double liveability = Double.parseDouble(nextLine[1]);
				int livedYear = Integer.parseInt(nextLine[2]);
				Gender gender = null;
				if (nextLine[3].equals("male")) {
					gender = Gender.Male;
				} else if (nextLine[3].equals("female")) {
					gender = Gender.Female;
				}

				double expectation = Double.parseDouble(nextLine[4]);

				SatisfactionTestCase satisfactionTestCase = new SatisfactionTestCase(
						livedYear, gender, liveability, expectation);
				satisfactionTestCases.add(satisfactionTestCase);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				csvReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
