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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import core.statistics.FacilitiesMapping;

public class FacilitiesMappingTest {

	private FacilitiesMapping facilitiesMapping;

	// @Before Missing needed CSV file
	public void setUp() throws Exception {
		facilitiesMapping = new FacilitiesMapping();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test @Ignore
	public void testInitialMapping() {
		assertEquals(101, facilitiesMapping.getMapping().size(), 0);
	}

}
