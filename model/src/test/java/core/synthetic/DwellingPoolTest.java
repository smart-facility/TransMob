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
