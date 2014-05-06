package core.synthetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import core.ApplicationContextHolder;
import hibernate.postgis.TravelZonesFacilitiesDAO;
import hibernate.postgis.TravelZonesFacilitiesEntity;
import hibernate.postgres.SyntheticPopulationDAO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Sets;

import core.HardcodedData;
import core.postprocessing.file.PerformanceFileProcessor;
import core.synthetic.dwelling.Duty;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.individual.lifeEvent.LifeEventProbability;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class HouseholdTest {

	// private Household household1, household2, household3;
	private LifeEventProbability lifeEventProbability;
	private Random random;
	private HouseholdPool householdPool;
	// private Mediator mediator;
	private IndividualPool individualPool;
    private static final Logger logger = Logger.getLogger(HouseholdTest.class);
	// private ArrayList<Individual> individuals1, individuals2, individuals3;
	private DwellingAllocator dwellingAllocator;

	private SyntheticPopulationDAO syntheticPopulationDAO;

	// public static HashMap<Integer, String> hmHousehold_OldKey = new
	// HashMap<Integer, String>();
	// public static HashMap<Integer, String> hmHousehold_NewKey = new
	// HashMap<Integer, String>();
	//
	// public static HashMap<Integer, Integer> hmHouseHold_TravelDiariesChange =
	// new HashMap<Integer, Integer>();

	// hmHouseHold_TravelDiariesChange : <hhold_index,-1 OR 0>

	@Before
	public void setUp() throws Exception {

		syntheticPopulationDAO = ApplicationContextHolder.getBean(SyntheticPopulationDAO.class);

		// HardcodedData.prepend(System.getProperty("user.dir"));
		// dwellingPool = new DwellingPool();
		// dwellingPool.initial();
		//
		// dwellingControl = new DwellingControl();
		// dwellingControl.getLimit(2006, true);

		dwellingAllocator = new DwellingAllocator();
		individualPool = new IndividualPool();
		householdPool = new HouseholdPool();
		lifeEventProbability = new LifeEventProbability();
		individualPool.generatePool(330);

		householdPool.generatePool(individualPool);
		householdPool.updateDwellingsIndex(dwellingAllocator);

		// RandomAgentsGenerator randomAgentsGenerator = new
		// RandomAgentsGenerator();
		// randomAgentsGenerator.readFile(HardcodedData.tzsAlongLightRail);
		// randomAgentsGenerator.generateAgents(90626, 60000, new Random(),
		// householdPool, individualPool, dwellingPool, dwellingControl);

		random = HardcodedData.random;

	}

    @Test @Ignore
	public void testChange() {
		int[][] temp = { { 1, 2, 3 }, { 4, 5, 6 } };
		int[][] temp2 = { { 11, 21, 13 }, { 14, 15, 16 } };

		individualPool.getByID(1).setTravelDiariesWeekdays(temp);

		logger.debug(individualPool.getByID(1).getTravelDiariesWeekdays()[0][0]);

		for (Individual individual : householdPool.getByID(1).getResidents()) {
			individual.setTravelDiariesWeekdays(temp2);
			individual.getTravelDiariesWeekdays()[0][0] = 22;
		}

		logger.debug(individualPool.getByID(1).getTravelDiariesWeekdays()[0][0]);
	}

	// public void setUp() throws Exception {
	// individuals1 = new ArrayList<Individual>();
	// individuals2 = new ArrayList<Individual>();
	// individuals3 = new ArrayList<Individual>();
	//
	// Individual individual1 = new Individual(1, 25, Gender.Male,
	// new BigDecimal(1020), HouseholdRelationship.DeFacto,
	// Occupation.Professionals, TransportModeToWork.Bus,
	// HighestEduFinished.PostgraduateDegree, Category.HF2, 1, 1, 1);
	//
	// Individual individual2 = new Individual(2, 23, Gender.Female,
	// new BigDecimal(110), HouseholdRelationship.DeFacto,
	// Occupation.CommunityAndPersonalServiceWorkers,
	// TransportModeToWork.Bus,
	// HighestEduFinished.AdvancedDiplomaAndDiploma, Category.HF2, 1,
	// 1, 1);
	//
	// Individual individual3 = new Individual(3, 1, Gender.Female,
	// new BigDecimal(0), HouseholdRelationship.U15Child,
	// Occupation.InadequatelyDescribedOrNotStated,
	// TransportModeToWork.Other,
	// HighestEduFinished.LevelOfEducationInadequatelyDescribed,
	// Category.HF2, 1, 1, 1);
	//
	// Individual individual4 = new Individual(4, 17, Gender.Female,
	// new BigDecimal(35), HouseholdRelationship.O15Child,
	// Occupation.SalesWorkers, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.CertificateIAndIId, Category.HF2, 1, 1, 1);
	//
	// Individual individual5 = new Individual(5, 18, Gender.Male,
	// new BigDecimal(0), HouseholdRelationship.Student,
	// Occupation.InadequatelyDescribedOrNotStated,
	// TransportModeToWork.Bicycle,
	// HighestEduFinished.CertificateIAndIId, Category.HF2, 1, 1, 1);
	//
	// Individual individual6 = new Individual(6, 26, Gender.Male,
	// new BigDecimal(150), HouseholdRelationship.Relative,
	// Occupation.CommunityAndPersonalServiceWorkers,
	// TransportModeToWork.CarAsDriver,
	// HighestEduFinished.BachelorDegree, Category.HF2, 1, 1, 1);
	//
	// Individual individual7 = new Individual(7, 50, Gender.Female,
	// new BigDecimal(260), HouseholdRelationship.LoneParent,
	// Occupation.CommunityAndPersonalServiceWorkers,
	// TransportModeToWork.WalkedOnly,
	// HighestEduFinished.GraduateDiplomaAndGraduateCertificate,
	// Category.HF8, 2, 1, 1);
	// Individual individual8 = new Individual(8, 25, Gender.Male,
	// new BigDecimal(0), HouseholdRelationship.LonePerson,
	// Occupation.InadequatelyDescribedOrNotStated,
	// TransportModeToWork.Other, HighestEduFinished.BachelorDegree,
	// Category.HF8, 2, 1, 1);
	// Individual individual9 = new Individual(9, 81, Gender.Female,
	// new BigDecimal(220), HouseholdRelationship.Relative,
	// Occupation.InadequatelyDescribedOrNotStated,
	// TransportModeToWork.Other,
	// HighestEduFinished.GraduateDiplomaAndGraduateCertificate,
	// Category.HF8, 2, 1, 1);
	//
	// Individual individual10 = new Individual(10, 54, Gender.Male,
	// new BigDecimal(340), HouseholdRelationship.Married,
	// Occupation.Managers, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.PostgraduateDegree, Category.HF1, 1, 1, 1);
	// Individual individual11 = new Individual(11, 52, Gender.Female,
	// new BigDecimal(270), HouseholdRelationship.Married,
	// Occupation.Professionals, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.GraduateDiplomaAndGraduateCertificate,
	// Category.HF1, 1, 1, 1);
	// Individual individual12 = new Individual(12, 56, Gender.Female,
	// new BigDecimal(220), HouseholdRelationship.Relative,
	// Occupation.SalesWorkers, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.CertificateIIIAndIVc, Category.HF1, 3, 1, 1);
	// Individual individual13 = new Individual(13, 81, Gender.Female,
	// new BigDecimal(2), HouseholdRelationship.Relative,
	// Occupation.InadequatelyDescribedOrNotStated,
	// TransportModeToWork.Other,
	// HighestEduFinished.LevelOfEducationNotStated, Category.HF1, 3,
	// 1, 1);
	//
	// individuals1.add(individual1);
	// individuals1.add(individual2);
	// individuals1.add(individual3);
	// individuals1.add(individual4);
	// individuals1.add(individual5);
	// individuals1.add(individual6);
	//
	// household1 = new Household(1, Category.HF2, individuals1,
	// new BigDecimal(1400));
	//
	// individuals2.add(individual7);
	// individuals2.add(individual8);
	// individuals2.add(individual9);
	//
	// household2 = new Household(2, Category.HF8, individuals2,
	// new BigDecimal(540));
	//
	// individuals3.add(individual10);
	// individuals3.add(individual11);
	// individuals3.add(individual12);
	// individuals3.add(individual13);
	//
	// household3 = new Household(3, Category.HF1, individuals3,
	// new BigDecimal(900));
	//
	// random = new Random();
	//
	// lifeEventProbability = new LifeEventProbability();
	//
	// mediator = new Mediator();
	// mediator.registerHousehold();
	// mediator.registerIndividual();
	//
	// householdPool = new HouseholdPool(mediator);
	// householdPool.add(household1);
	// householdPool.add(household2);
	// householdPool.add(household3);
	//
	// aMenHhPool = new WeddingPool(0);
	// aWomenHhPool = new WeddingPool(0);
	//
	// individualPool = new IndividualPool(mediator);
	// for (Individual individual : individuals1) {
	// individualPool.add(individual);
	// }
	// for (Individual individual : individuals2) {
	// individualPool.add(individual);
	// }
	// for (Individual individual : individuals3) {
	// individualPool.add(individual);
	// }
	//
	// logger = Logger.getLogger(HouseholdTest.class);
	//
	// }

	@After
	public void tearDown() throws Exception {
		// household1 = null;
		lifeEventProbability = null;
		random = null;
		householdPool = null;
		// mediator = null;
		individualPool = null;
		syntheticPopulationDAO = null;
	}

    @Test @Ignore
	// everyone can divorce?
	public void testIsGettingDivorced() {

		assertFalse(householdPool.getByID(1).isGettingDivorced(
				lifeEventProbability, random));
	}

    @Test @Ignore
	public void testGetDivorced() {

		logger.debug(householdPool.toString());
		// household.getDivorced(householdPool);

		logger.debug(householdPool.toString());
	}

    @Test @Ignore
	public void testIndividualDying() {

		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(
					new FileReader(
							new File(
									"C:/Documents and Settings/qun/workspace/TransportNSW/testCases/test cases isIndividualDying.csv")));

			String[] nextLine;

			nextLine = csvReader.readNext();
			StringBuffer tempString;

			while ((nextLine = csvReader.readNext()) != null) {

				setUp();

				if (nextLine[0].equalsIgnoreCase("passed")
						|| nextLine[0].equalsIgnoreCase("error")) {
					continue;
				} else {

					logger.debug(householdPool.toString());
					logger.debug(individualPool.toString());

					double[] probability = { Double.parseDouble(nextLine[0]),
							Double.parseDouble(nextLine[1]),
							Double.parseDouble(nextLine[2]),
							Double.parseDouble(nextLine[3]),
							Double.parseDouble(nextLine[4]),
							Double.parseDouble(nextLine[5]) };

					tempString = new StringBuffer();
					for (double d : probability) {
						tempString.append(d).append(',');
					}

					logger.debug(tempString);

					int i = 0;

					boolean aIndDied = false;
					Individual curInd = null;

					/* Loop over the members of the households */

					Iterator<Individual> itr = householdPool.getByID(1)
							.getResidents().iterator();
					while (itr.hasNext()) {
						// can't be test because random
						curInd = itr.next();
						if (!curInd.isAlive(lifeEventProbability, random)) { // a
							// death
							// occurs
							aIndDied = true;
							// householdPool.getByID(1).killIndividual(
							// individualPool, householdPool, curInd, itr);
						}
						i++;
					}

					if (aIndDied) {
						householdPool.getByID(1).updateDeathOccurred(null);
					}

					if (householdPool.getByID(1).getResidents().size() == 0) {
						householdPool.remove(householdPool.getByID(1));
					}

					logger.debug(householdPool.toString());
					logger.debug(individualPool.toString());

					if (nextLine[12].equalsIgnoreCase("eliminate")) {
						assertEquals(0, householdPool.getPoolNumber());
					} else {
						assertEquals(nextLine[12], householdPool.getByID(1)
								.getCategory().toString());
						for (int j = 0; j < 6; j++) {
							logger.debug(nextLine[j + 6]);
							if (nextLine[j + 6].equalsIgnoreCase("null")
									|| nextLine[j + 6].equalsIgnoreCase("kill")) {

								assertEquals(null,
										individualPool.getByID(j + 1));
							} else {
								assertEquals(nextLine[j + 6], individualPool
										.getByID(j + 1)
										.getHouseholdRelationship().toString());
							}
						}
					}
				}

				logger.debug("Case Passed!");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
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


    @Test @Ignore
	public void testMarryHouseholds() {

		logger.debug(householdPool.totalIndividual());

		Set<Individual> menIndividuals = Sets.newCopyOnWriteArraySet();
		Set<Individual> womenIndividuals = Sets.newCopyOnWriteArraySet();

		for (int i = 1; i <= householdPool.getMaxId(); i++) {

			Household household = householdPool.getByID(i);
			if (household != null) {
				// household.indInHhMarrying(lifeEventProbability, random,
				// householdPool, aMenHhPool, aWomenHhPool);
				household.generateWeddingSet(lifeEventProbability, random,
						menIndividuals, womenIndividuals);
			}

		}

		logger.debug("total: "
				+ (householdPool.totalIndividual() + menIndividuals.size() + womenIndividuals
						.size()));

		// householdPool.marry(menIndividuals, womenIndividuals, random);

		logger.debug(householdPool.totalIndividual());

		// logger.debug(householdPool.toString());
		// logger.debug(aMenHhPool.toString());
		// logger.debug(aWomenHhPool.toString());

		// logger.debug(householdPool.getPoolNumber());
		//
		// logger.debug("household" + householdPool.totalIndividual());
		// logger.debug("men" + aMenHhPool.totalIndividual());
		// logger.debug("women" + aWomenHhPool.totalIndividual());
		// logger.debug("total:"
		// + (householdPool.totalIndividual()
		// + aMenHhPool.totalIndividual() + aWomenHhPool
		// .totalIndividual()));
		//
		// logger.debug(individualPool.getPoolNumber());

		// householdPool.marryHouseholds(householdPool, aMenHhPool,
		// aWomenHhPool,
		// random, null);
		// logger.debug(householdPool.getPoolNumber());
		//
		// logger.debug(householdPool.totalIndividual());
		//
		// for (Household household : householdPool.getHouseholds().values()) {
		// for (Individual individual : household.getResidents()) {
		// System.out.print(individual.getId() + ",");
		// }
		// }

		// householdPool.updateIndividualPool(individualPool);

	}

	@Test @Ignore // Can't run this as the file isn't in SVN
	public void testEvolution() {

		// BufferedWriter bufferedWriter = null;
		// try {
		// bufferedWriter = new BufferedWriter(new FileWriter(
		// "C:/Documents and Settings/qun/Desktop/population.txt"));
		//
		HardcodedData.setPerformanceFileLocation("weekdays/");
		String csvFilename = HardcodedData.linkList + "/Link_list.csv";

		PerformanceFileProcessor performanceFileProcessor = PerformanceFileProcessor
				.getInstance(HardcodedData.getPerformanceFileLocation(),
						csvFilename);
		performanceFileProcessor.readFile();

		Map<String, Double> averageLinkSpeed = performanceFileProcessor
				.getLinkAverageSpeed();
		Map<String, Double> linkCongestion = performanceFileProcessor
				.getLinkCongestion(averageLinkSpeed);
		Map<Integer, Double> travelZoneCongestion = performanceFileProcessor
				.calculateTravelZoneCongestion(linkCongestion);

		// HTS_HouseholdPool hts_HouseholdPool = new HTS_HouseholdPool();
		// LocationTypes locationTypes = new LocationTypes();
		//
		// /* travel diaries initial */
		// try {
		// hts_HouseholdPool.readHTSData();
		// locationTypes.readLocationTypes();
		// householdPool.assignTravelDiary(hts_HouseholdPool, locationTypes);
		// householdPool.writeTravelDiaryToCSV(0);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		for (int i = 2006; i < 2007; i++) {
			// if (i != 2006) {
			// dwellingControl.getLimit(i, false);
			// }
			// int i = 2006;
			logger.debug("YEAR: " + i);
			//
			// hmHousehold_OldKey = householdPool.storeKeys();

			/* householdPool.evolution */
			// householdPool.evolution(individualPool, lifeEventProbability,
			// random, i, travelZoneCongestion, syntheticPopulationDAO,
			// dwellingAllocator);

			// removeHousehold();

			// logger.info("year:" + i);
			logger.info("household:" + householdPool.getPoolNumber() + ":"
					+ householdPool.totalIndividual());
			logger.info("individual:" + individualPool.getPoolNumber());

			logger.debug(individualPool.getByID(327).getSatisfaction());

			double congestion = 0;

			if (travelZoneCongestion.get(individualPool.getByID(327)
					.getLivedTravelZoneid()) != null) {
				congestion = travelZoneCongestion.get(individualPool.getByID(
						327).getLivedTravelZoneid());
			}

			logger.debug(congestion);
			logger.debug(individualPool.getByID(327).getLivedTravelZoneid());

			// individualPool.storeIndividualInfomation("scenario20120320", i);
			// BufferedWriter bufferedWriter = null;
			// try {
			//
			// File file = new File(
			// "C:/Documents and Settings/qun/Desktop/1.txt");
			//
			// bufferedWriter = new BufferedWriter(new FileWriter(file));
			//
			// if (!file.exists()) {
			// file.createNewFile();
			// }
			//
			// for (Individual individual : individualPool.getIndividuals()
			// .values()) {
			// bufferedWriter.write(individual.getId() + ":"
			// + individual.getSatisfaction());
			// bufferedWriter.newLine();
			// }
			//
			// bufferedWriter.close();
			//
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			// individualPool.storeIndividualInfomation("scenario20120320", i);
		}
		//
		// /* travel diaries on the fly */
		// hmHousehold_NewKey = householdPool.storeKeys();
		// hmHouseHold_TravelDiariesChange = householdPool.checkChange(
		// hmHousehold_OldKey, hmHousehold_NewKey);
		//
		// householdPool.reassignTravelDiaries(
		// hmHouseHold_TravelDiariesChange, hts_HouseholdPool,
		// locationTypes);
		//
		// householdPool.writeTravelDiaryToCSV(i);

		//
		// bufferedWriter.write(i + ":" + individualPool.getPoolNumber());
		// bufferedWriter.newLine();
		//
		// //
		// individualPool.storeIndividualInfomation("_2_100years20120515",
		// // i,
		// // householdPool.getLiveabilityUtility());
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// try {
		// bufferedWriter.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	// private void removeHousehold() {
	// ArrayList<Household> arrayList = Lists.newArrayList();
	//
	// arrayList.addAll(householdPool.getHouseholds().values());
	//
	// for (Household household : arrayList) {
	//
	// if (household.getDwellingId() == 0) {
	// householdPool.removeHousehold(household, individualPool);
	// }
	// }
	// }

    @Test @Ignore
	public void testIsBuyingFirstProperty() {
		TravelZonesFacilitiesDAO travelZonesFacilitiesDAO = new TravelZonesFacilitiesDAO();
        TravelZonesFacilitiesEntity travelZonesFacilities = travelZonesFacilitiesDAO
				.findById(270);
		Household household = householdPool.getByID(1);

		Duty duty = new Duty();
		// duty.initial();

		household.isBuyingFirstProperty(household.calculateNumberOfRoomNeed(),
				2006, travelZonesFacilities, 0.07, duty);
	}

    @Test @Ignore
	public void testIsRentingFirstProperty() {
		TravelZonesFacilitiesDAO travelZonesFacilitiesDAO = new TravelZonesFacilitiesDAO();
        TravelZonesFacilitiesEntity travelZonesFacilities = travelZonesFacilitiesDAO
				.findById(270);
		Household household = householdPool.getByID(1);

		household.isRentingProperty(household.calculateNumberOfRoomNeed(),
				2006, travelZonesFacilities);
	}

    @Test @Ignore
	public void testIsBuyingAnotherProperty() {
		TravelZonesFacilitiesDAO travelZonesFacilitiesDAO = new TravelZonesFacilitiesDAO();
        TravelZonesFacilitiesEntity travelZonesFacilities = travelZonesFacilitiesDAO
				.findById(270);
        TravelZonesFacilitiesEntity crntTravelZonesFacilities = travelZonesFacilitiesDAO
				.findById(530);
		Household household = householdPool.getByID(1);

		Duty duty = new Duty();
		// duty.initial();

		household.isBuyingFirstProperty(household.calculateNumberOfRoomNeed(),
				2006, crntTravelZonesFacilities, 0.07, duty);

		household.isBuyingAnotherProperty(
				household.calculateNumberOfRoomNeed(),
				2006,
				travelZonesFacilities, 0.07, duty);
	}

    @Test @Ignore
	public void testGeneratePool() {
		logger.debug(householdPool.getPoolNumber());
	}

    @Test @Ignore
	public void testAllocateToDwelling() {

		// Household household = householdPool.getByID(1);
		// household.allocateToDwelling(dwellingPool, 555, dwellingControl);
		// household.allocateToDwelling(dwellingPool, 270, dwellingControl);
	}

}
