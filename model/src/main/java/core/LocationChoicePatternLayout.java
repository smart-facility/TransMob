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
package core;

import org.apache.log4j.PatternLayout;

/**
 * this class is required as a property of LocChoiceFile in lo4j.properties.
 * @author nhuynh
 *
 */
public class LocationChoicePatternLayout extends PatternLayout {
	/**
	 * returns headers for locationChoice.csv.
	 * 
	 */
	@Override
	public String getHeader() {
		return "hholdId,hholdType,reasonToMove,cdMoveFr,cdMoveTo,houseBudget,cdMoveToHousePrice_Sale,cdMoveToHousePrice_Rent,ownershipStatus,"
				+ "indivId,indivAge,indivGender,indivIncome,indivSatIdx_crnLoc,indivSatIdx_nxtLoc,year"
				+ System.getProperty("line.separator");
	}
}