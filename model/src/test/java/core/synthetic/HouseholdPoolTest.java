package core.synthetic;

import hibernate.postgres.SyntheticPopulationDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
import core.synthetic.individual.lifeEvent.LifeEventProbability;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class HouseholdPoolTest {

    private static final Logger logger = Logger.getLogger(HouseholdPoolTest.class);
	/**
	 * name="householdPool"
	 * 
	 * @uml.associationEnd
	 */
	private HouseholdPool householdPool;
	/**
	 * name="mediator"
	 * 
	 * @uml.associationEnd
	 */

	// private Random random;

	private LifeEventProbability lifeEventProbability;

	// private Household household1, household2, household3;
	private IndividualPool individualPool;
	// private ArrayList<Individual> individuals1, individuals2, individuals3;
	private SyntheticPopulationDAO syntheticPopulationDAO;
	private DwellingAllocator dwellingAllocator;

	@Before
	public void setUp() throws Exception {

		syntheticPopulationDAO = new SyntheticPopulationDAO();

		// HardcodedData.prepend(System.getProperty("user.dir"));
		// dwellingPool = new DwellingPool();
		// dwellingPool.initial();

		// dwellingControl = new DwellingControl();
		// dwellingControl.getLimit(2006, true);

		dwellingAllocator = new DwellingAllocator();
		individualPool = new IndividualPool();
		householdPool = new HouseholdPool();		
		lifeEventProbability = new LifeEventProbability();
		individualPool.generatePool(30);
		// individualPool.generatePool(new
		// IntermediateSyntheticPopulationDAO());

		householdPool.generatePool(individualPool);

		// householdPool.updateDwellingsIndex(dwellingAllocator);
		// random = new Random();
		// householdPool.allocateDwellings(dwellingPool, dwellingControl);
		// random = new Random();
		// aMenHhPool = new WeddingPool(0);
		// aWomenHhPool = new WeddingPool(0);
		// householdPool.generatePool(individualPool);
		// random = new Random();

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
		// new BigDecimal(0), HouseholdRelationship.O15Child,
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
		// aRandom = new Random();
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
	}

	@After
	public void tearDown() throws Exception {
		householdPool = null;

		// random = null;
		lifeEventProbability = null;
		syntheticPopulationDAO = null;
		dwellingAllocator = null;
		individualPool = null;
	}

	// @Test
	// public void testAddHousehold() {
	//
	// ApplicationContext applicationContext = new
	// ClassPathXmlApplicationContext(
	// "beans.xml");
	// Household household1 = applicationContext.getBean("SimpleHousehold1",
	// Household.class);
	// // // ((SimpleHousehold)household1).setId(0);
	// // // ((SimpleHousehold)household1).setCategory(Category.HF1);
	// // // ((SimpleHousehold)household1).setGrossIncome(new
	// // BigDecimal(1300));
	// // // ((SimpleHousehold)household1).setNumberResidents(2);
	// // Household household1=new SimpleHousehold(1, Category.HF1, 2, new
	// // ArrayList<Individual>(2), new BigDecimal(1300));
	// Household household2 = new Household(2, Category.HF2, 4,
	// new ArrayList<Individual>(4), new BigDecimal(600));
	// // applicationContext=new ClassPathXmlApplicationContext("beans.xml");
	// // Household household2=applicationContext.getBean("SimpleHousehold2",
	// // Household.class);
	// // // ((SimpleHousehold)household2).setId(1);
	// // // ((SimpleHousehold)household2).setCategory(Category.HF2);
	// // // ((SimpleHousehold)household2).setGrossIncome(new BigDecimal(600));
	// // // ((SimpleHousehold)household2).setNumberResidents(4);
	//
	// householdPool.add(household1);
	// householdPool.add(household2);
	// double distribution = mediator.getGrossIncomeDistribution()
	// .getDistribution(GrossIncome.From1200to1399.toString());
	//
	// assertEquals(0.5, distribution, 0);
	// assertEquals(
	// 0.5,
	// mediator.getCategoryDistribution().getDistribution(
	// Category.HF2.toString()), 0);
	// }

	// // @Test
	// public void testOnChange() {
	// mediator.registerHousehold();
	//
	// Household household1 = new Household(1, Category.HF1, 2,
	// new ArrayList<Individual>(2), new BigDecimal(1300));
	// Household household2 = new Household(2, Category.HF2, 4,
	// new ArrayList<Individual>(4), new BigDecimal(600));
	// // for (int i = 0; i < 800000; i++) {
	// householdPool.add(household1);
	// householdPool.add(household2);
	// // }
	//
	// // for(int i=0;i<20000;i++){
	// // ((SimpleHousehold)household1).setGrossIncome(new BigDecimal(i));
	// // ((SimpleHousehold)household2).setGrossIncome(new BigDecimal(i));
	// // if (i%1000==0) {
	// // logger.debug(((SimpleHousehold)household1).getGrossIncome());
	// // logger.debug(((SimpleHousehold)household2).getGrossIncome());
	// // }
	// // }
	// double distribution = mediator.getGrossIncomeDistribution()
	// .getDistribution(GrossIncome.From600to799.toString());
	// // assertEquals(0.5, distribution,0);
	// household1.setGrossIncome(new BigDecimal(650));
	// // logger.debug(householdPool.getPoolNumber());
	// distribution = mediator.getGrossIncomeDistribution().getDistribution(
	// GrossIncome.From600to799.toString());
	// assertEquals(1, distribution, 0);
	//
	// }

    @Test @Ignore
	public void testEvolution() {
		// householdPool.evolution(individualPool, lifeEventProbability,
		// aRandom,
		// dwellingPool, 2006);
	}

    @Test @Ignore
	public void testGeneratePool() {
		// individualPool.generatePool(100);
		// householdPool.generatePool(individualPool);
		logger.debug(householdPool.toString());
	}

	@Test
	public void testAllocateDwellingsTest() throws Exception {
		householdPool.allocateDwellingsForInitialisation(dwellingAllocator, 2006);

		// for (Household household :
		// this.householdPool.getHouseholds().values()) {
		// if (household.getDwellingId() == 0) {
		// logger.error(household.toString());
		// }
		// }
	}

    @Test @Ignore
	public void calculateDwellingDemand() {
		Map<Integer, int[]> count = new HashMap<Integer, int[]>();

		for (Household household : householdPool.getHouseholds().values()) {
			int livedTravelZoneid = household.getResidents().get(0)
					.getLivedTravelZoneid();
			if (livedTravelZoneid != -1) {
				int number = household.calculateNumberOfRoomNeed();

				if (count.containsKey(livedTravelZoneid)) {
					count.get(livedTravelZoneid)[number - 1]++;
				} else {
					int[] bedrooms = new int[4];
					bedrooms[number - 1] = 1;
					count.put(livedTravelZoneid, bedrooms);
				}
			}
		}

		for (Entry<Integer, int[]> entry : count.entrySet()) {
			System.out.println(entry.getKey() + ";" + entry.getValue()[0] + ";"
					+ entry.getValue()[1] + ";" + entry.getValue()[2] + ";"
					+ entry.getValue()[3]);
		}
	}

    @Test @Ignore
	public void testSortHouseholds() {
		householdPool.sortHouseholds();
	}
}
