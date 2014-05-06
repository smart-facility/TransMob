package core.postprocessing.file;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.HardcodedData;
import core.postprocessing.file.TimePlanFileProcessor;
import core.synthetic.HouseholdPool;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TimePlanFileProcessorTest {

	private TimePlanFileProcessor timePlanFileProcessor;

	// private HouseholdPool householdPool;
	// private SyntheticPopulationDAO syntheticPopulationDAO;
	// private IndividualPool individualPool;
	// private LifeEventProbability lifeEventProbability;

	@Before
	public void setUp() throws Exception {

		HardcodedData.prepend();

		// syntheticPopulationDAO = new SyntheticPopulationDAO();
		//
		// // dwellingPool = new DwellingPool();
		// // dwellingPool.initial();
		// //
		// // dwellingControl = new DwellingControl();
		// // dwellingControl.getLimit(2006, true);
		// individualPool = new IndividualPool();
		// householdPool = new HouseholdPool();
		// lifeEventProbability = new LifeEventProbability();
		// individualPool.generatePool(100, syntheticPopulationDAO);
		// householdPool.generatePool(individualPool);
		// householdPool.allocateDwellings(dwellingPool, dwellingControl);

		HardcodedData.setTimePlanFileLocation("weekdays/");
		timePlanFileProcessor = TimePlanFileProcessor.getInstance(HardcodedData
				.getTimePlanFileLocation());

	}

	@After
	public void tearDown() throws Exception {
		timePlanFileProcessor = null;
	}

    @Test @Ignore
	public void testReadFile() {

		HouseholdPool householdPool = new HouseholdPool();

		timePlanFileProcessor.readFile(HardcodedData.travelCost + "2006.csv",
				householdPool);

		System.out
				.println(timePlanFileProcessor.getLinksTripsTime().toString());
	}
}
