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
package core.postprocessing.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
public class DatabaseCreatorTest {

	DatabaseCreator databaseCreator = new DatabaseCreator();

	@Before
	public void setUp() throws Exception {
		databaseCreator = new DatabaseCreator();
	}

	@After
	public void tearDown() throws Exception {
		databaseCreator = null;
	}

	@Test @Ignore
	public void testCreateDatabase() {

		databaseCreator.createDatabase();
	}

    @Test @Ignore
	public void testCreateTravelZoneOutputTable() {
		int startYear = 2006;
		int numberOfYear = 20;
		databaseCreator.createTravelZoneOutputTable(startYear, numberOfYear);
	}

    @Test @Ignore
	public void testCreateSyntheticPopulationOutputTable() {
		int startYear = 2006;
		int numberOfYear = 100;
		databaseCreator.createSyntheticPopulationOutputTable(startYear, numberOfYear);
	}

    @Test @Ignore
	public void testCreateTravelmodeChoiceOutputTable() {
		int startYear = 2006;
		int numberOfYear = 20;
		databaseCreator.createTravelModeChoiseOutputTable(startYear, numberOfYear);
	}

}
