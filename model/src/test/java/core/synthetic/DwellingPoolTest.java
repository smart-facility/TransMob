package core.synthetic;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.synthetic.dwelling.DwellingPool;

public class DwellingPoolTest {

	private DwellingPool dwellingPool;

	@Before
	public void setUp() throws Exception {
		dwellingPool = new DwellingPool();
	}

	@After
	public void tearDown() throws Exception {
		dwellingPool = null;
	}

	@Test @Ignore
	public void testInitial() {
		// dwellingPool.initial();
		// // assertEquals(2751, dwellingPool.getTravelZoneDwellings().get(292)
		// // .size());
		// assertEquals(0, dwellingPool.getTravelZoneVacantDwellings().get(279)
		// .size());
		// assertEquals(508,
		// dwellingPool.getTravelZoneVacantDwellings().get(535)
		// .size());
		// assertEquals(4115,
		// dwellingPool.getLivedHouseholdDwellings().get(15098)
		// .getId());
	}

}
