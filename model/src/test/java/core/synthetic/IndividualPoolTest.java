package core.synthetic;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.HardcodedData;
import core.synthetic.liveability.LiveabilityUtility;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class IndividualPoolTest {

	private LiveabilityUtility liveabilityUtility;
	private IndividualPool individualPool;
	private HouseholdPool householdPool;
	private Map<Integer, Double> travelZoneCongestion;

	private static final Logger logger = Logger.getLogger(IndividualPoolTest.class);

	@Before
	public void setup() throws Exception {
		HardcodedData.prepend();
		individualPool = new IndividualPool();
		individualPool.generatePool(90626);
		liveabilityUtility = LiveabilityUtility.getInstance();
		liveabilityUtility.setPopulation(individualPool);
		travelZoneCongestion = new HashMap<Integer, Double>();
		HardcodedData.setPerformanceFileLocation("weekdays/");
		String csvFilename = HardcodedData.linkList + "/Link_list.csv";

		/*PerformanceFileProcessor performanceFileProcessor = PerformanceFileProcessor
				.getInstance(HardcodedData.getPerformanceFileLocation(),
						csvFilename);
		performanceFileProcessor.readFile();

		Map<String, Double> averageLinkSpeed = performanceFileProcessor
				.getLinkAverageSpeed();
		Map<String, Double> linkCongestion = performanceFileProcessor
				.getLinkCongestion(averageLinkSpeed);
		travelZoneCongestion = performanceFileProcessor
				.calculateTravelZoneCongestion(linkCongestion); */
	}

	@After
	public void tearDown() throws Exception {
		individualPool = null;
	}

    @Test
	public void testGeneratePool() {
		assertEquals("Pool size isn't as expected", 90626, individualPool.getPoolNumber());
	}

	// // @Test
	// public void testOnChange() {
	//
	// Individual individual1 = new Individual(1, 56, Gender.Male,
	// new BigDecimal(497), HouseholdRelationship.DeFacto,
	// Occupation.Professionals, TransportModeToWork.Bus,
	// HighestEduFinished.PostgraduateDegree, Category.NF, 1, 1, 1);
	// Individual individual2 = new Individual(2, 53, Gender.Female,
	// new BigDecimal(196), HouseholdRelationship.DeFacto,
	// Occupation.CommunityAndPersonalServiceWorkers,
	// TransportModeToWork.Bus,
	// HighestEduFinished.GraduateDiplomaAndGraduateCertificate,
	// Category.HF7, 1, 1, 1);
	// Individual individual3 = new Individual(3, 24, Gender.Male,
	// new BigDecimal(1350), HouseholdRelationship.Married,
	// Occupation.Managers, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.PostgraduateDegree, Category.HF1, 1, 1, 1);
	// Individual individual4 = new Individual(4, 32, Gender.Female,
	// new BigDecimal(1280), HouseholdRelationship.Married,
	// Occupation.Professionals, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.GraduateDiplomaAndGraduateCertificate,
	// Category.HF1, 1, 1, 1);
	//
	// individualPool.add(individual1);
	//
	// individualPool.add(individual2);
	// individualPool.add(individual3);
	// individualPool.add(individual4);
	//
	// // ((SimpleIndividual)individual1).registerMediator(mediator);
	// individual1.setAge(23);
	// individual1.setGender(Gender.Female);
	// individual1.setIncome(new BigDecimal(1000));
	//
	// /*
	// * Code Added by Appu to test agent lifecycle. It will be useful for
	// * testing.
	// *
	// *
	// * for (int i=0;i < ((IndividualPool)
	// * individualPool).getIndividuals().size();i++) {
	// * System.out.println("Agent:" + i); ((SimpleIndividual)
	// * ((IndividualPool)
	// * individualPool).getIndividuals().get(i)).lifecycle(); }
	// */
	//
	// assertEquals(
	// 0.5,
	// mediator.getAgeDistribution().getDistribution(
	// Age.From20to24years.toString()), 0);
	// assertEquals(
	// 0,
	// mediator.getAgeDistribution().getDistribution(
	// Age.From15to19years.toString()), 0);
	// assertEquals(
	// 0.75,
	// mediator.getGenderDistribution().getDistribution(
	// Gender.Female.toString()), 0);
	// assertEquals(
	// 0.25,
	// mediator.getGenderDistribution().getDistribution(
	// Gender.Male.toString()), 0);
	// assertEquals(
	// 0.5,
	// mediator.getIncomeDistribution().getDistribution(
	// Income.From1000to1299.toString()), 0);
	//
	// individual2.setAge(23);
	// assertEquals(
	// 0.75,
	// mediator.getAgeDistribution().getDistribution(
	// Age.From20to24years.toString()), 0);
	//
	// individual4.setAge(23);
	// assertEquals(
	// 1,
	// mediator.getAgeDistribution().getDistribution(
	// Age.From20to24years.toString()), 0);
	// }
	//
	// // @Test
	// public void testAddIndividual() {
	// mediator.registerIndividual();
	//
	// Individual individual1 = new Individual(1, 23, Gender.Male,
	// new BigDecimal(497), HouseholdRelationship.DeFacto,
	// Occupation.Professionals, TransportModeToWork.Bus,
	// HighestEduFinished.PostgraduateDegree, Category.NF, 1, 1, 1);
	// Individual individual2 = new Individual(2, 53, Gender.Female,
	// new BigDecimal(196), HouseholdRelationship.DeFacto,
	// Occupation.CommunityAndPersonalServiceWorkers,
	// TransportModeToWork.Bus,
	// HighestEduFinished.GraduateDiplomaAndGraduateCertificate,
	// Category.HF7, 1, 1, 1);
	// Individual individual3 = new Individual(3, 24, Gender.Male,
	// new BigDecimal(1350), HouseholdRelationship.Married,
	// Occupation.Managers, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.PostgraduateDegree, Category.HF1, 1, 1, 1);
	// Individual individual4 = new Individual(4, 32, Gender.Female,
	// new BigDecimal(1280), HouseholdRelationship.Married,
	// Occupation.Professionals, TransportModeToWork.WalkedOnly,
	// HighestEduFinished.GraduateDiplomaAndGraduateCertificate,
	// Category.HF1, 1, 1, 1);
	//
	// individualPool.add(individual1);
	// individualPool.add(individual2);
	// individualPool.add(individual3);
	// individualPool.add(individual4);
	//
	// double distribution = mediator.getIncomeDistribution().getDistribution(
	// Income.From1000to1299.toString());
	//
	// assertEquals(0.25, distribution, 0);
	// assertEquals(
	// 0.5,
	// mediator.getGenderDistribution().getDistribution(
	// Gender.Female.toString()), 0);
	// assertEquals(0.5, mediator.getHholdRelationshipDistribution()
	// .getDistribution(HouseholdRelationship.DeFacto.toString()), 0);
	// assertEquals(
	// 0.5,
	// mediator.getOccupationDistribution().getDistribution(
	// Occupation.Professionals.toString()), 0);
	// assertEquals(
	// 0.5,
	// mediator.getAgeDistribution().getDistribution(
	// Age.From20to24years.toString()), 0);
	// assertEquals(
	// 0.5,
	// mediator.getHighestEducationFinishedDistribution()
	// .getDistribution(
	// HighestEduFinished.PostgraduateDegree
	// .toString()), 0);
	//
	// }

    @Test @Ignore
	public void testStoreIndividualInfomation() {

		individualPool.storeIndividualInfomation(2006, householdPool);

	}

    @Test @Ignore // Won't work until householdPool is properly allocated first.
   	public void testCalculateIndividualSatisfaction() {
		// individualPool.generatePool(4473);
		// ((IndividualPool) individualPool).calculateIndividualSatisfaction();

		individualPool.calculateIndividualSatisfaction(liveabilityUtility,
				travelZoneCongestion, householdPool);

		logger.debug(individualPool.getByID(327).getSatisfaction());

	}
}
