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
package core.synthetic.attribute;

import core.HardcodedData;

/**
 * The occupation distribution in the individual pool.
 * 
 * @author qun
 */
public enum Occupation {
	/**
	 * name="managers"
	 * 
	 * @uml.associationEnd
	 */
	Managers, /**
	 * name="professionals"
	 * 
	 * @uml.associationEnd
	 */
	Professionals, /**
	 * name="techniciansAndTradesWorkers"
	 * 
	 * @uml.associationEnd
	 */
	TechniciansAndTradesWorkers, /**
	 * 
	 * name="communityAndPersonalServiceWorkers"
	 * 
	 * @uml.associationEnd
	 */
	CommunityAndPersonalServiceWorkers,
	/**
	 * name="clericalAndAdministrativeWorkers"
	 * 
	 * @uml.associationEnd
	 */
	ClericalAndAdministrativeWorkers, /**
	 * name="salesWorkers"
	 * 
	 * @uml.associationEnd
	 */
	SalesWorkers, /**
	 * name="machineryOperatorsAndDrivers"
	 * 
	 * @uml.associationEnd
	 */
	MachineryOperatorsAndDrivers, /**
	 * name="labourers"
	 * 
	 * @uml.associationEnd
	 */
	Labourers,
	/**
	 * name="inadequatelyDescribedOrNotStated"
	 * 
	 * @uml.associationEnd
	 */
	InadequatelyDescribedOrNotStated;

	public static Occupation random() {
		// SecureRandom random = GlobalRandom.getInstance();
		return Occupation.values()[HardcodedData.random.nextInt(Occupation
				.values().length)];
	}
}
