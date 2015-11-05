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
