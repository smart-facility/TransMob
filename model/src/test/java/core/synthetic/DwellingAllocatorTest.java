package core.synthetic;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class DwellingAllocatorTest {

	private DwellingAllocator dwellingAllocator;
	private IndividualPool individualPool;

	@Before
	@Ignore
	public void setUp() throws Exception {
		dwellingAllocator = new DwellingAllocator();
//		dwellingAllocator.getDwellingPool().initialAll("dwelling_test", 2006);

	}

	@After
	public void tearDown() throws Exception {
		dwellingAllocator = null;
	}

	// @Test
	public void testDwellingAllocator() {
		// dwellingAllocator.getDwellingPool().initialAll("dwelling_test",
		// 2025);

		// assertEquals(dwellingAllocator.getDwellingPool()
		// .getTravelZoneDwellings().get(270).size(),
		// 405 + 463 + 245 + 71, 0);
		//
		// assertEquals(dwellingAllocator.getDwellingPool()
		// .getTravelZoneDwellings().get(558).size(), 41 + 65 + 78 + 68, 0);

	}

	@Test
	public void testAllocateAvailableDwelling() {
		// this.individualPool = new IndividualPool();
		// this.householdPool = new HouseholdPool();
		// this.dwellingAllocator.allocateAvailableDwelling(270, 2006,
		// household);
	}


    @Test @Ignore
	public void testMoveOutDwelling() {
		Household household = new Household();
		household.setDwellingId(1);

		dwellingAllocator.moveOutDwelling(household);

	}

    @Test @Ignore
	public void testcalculateOccupancy() {
		dwellingAllocator.calculateOccupancy();
	}

}
