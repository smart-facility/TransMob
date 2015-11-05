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

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;

import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.attribute.HighestEduFinished;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.attribute.Occupation;
import core.synthetic.attribute.TransportModeToWork;
import core.synthetic.individual.Individual;


public class IndividualTest {

	/**
	 * name="indiviaual"
	 * 
	 * @uml.associationEnd
	 */

	@Before
	public void setUp() throws Exception {

		Individual individualInstance1 = new Individual(1, 30, Gender.Male,
				new BigDecimal(60000), HouseholdRelationship.LonePerson,
				Occupation.ClericalAndAdministrativeWorkers,
				TransportModeToWork.Bicycle, HighestEduFinished.BachelorDegree,
				Category.NF, 1, 1);
		Individual individualInstance2 = new Individual(1, 30, Gender.Male,
				new BigDecimal(50000), HouseholdRelationship.LonePerson,
				Occupation.ClericalAndAdministrativeWorkers,
				TransportModeToWork.Bicycle, HighestEduFinished.BachelorDegree,
				Category.NF, 1, 1);
	}

	@After
	public void tearDown() throws Exception {
	}

}
