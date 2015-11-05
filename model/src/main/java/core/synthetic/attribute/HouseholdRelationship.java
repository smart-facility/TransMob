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

import org.apache.log4j.Logger;

/**
 * The household relationship distribution in the household pool.
 * 
 * @author qun
 */
public enum HouseholdRelationship {

    /**
	 *  name="married"
	 * @uml.associationEnd
	 */
	Married(0, "Married"), /**
	 *  name="deFacto"
	 * @uml.associationEnd
	 */
	DeFacto(1, "DeFacto"), /**
	 *  name="loneParent"
	 * @uml.associationEnd
	 */
	LoneParent(2, "LoneParent"), /**
	 *  name="u15Child"
	 * @uml.associationEnd
	 */
	U15Child(3, "U15Child"), /**
	 *  name="student"
	 * @uml.associationEnd
	 */
	Student(4, "Student"), /**
	 *  name="o15Child"
	 * @uml.associationEnd
	 */
	O15Child(5, "O15Child"),
	/**
	 *  name="relative"
	 * @uml.associationEnd
	 */
	Relative(6, "Relative"), /**
	 *  name="nonRelative"
	 * @uml.associationEnd
	 */
	NonRelative(7, "NonRelative"), /**
	 *  name="groupHhold"
	 * @uml.associationEnd
	 */
	GroupHhold(8, "GroupHhold"), /**
	 *  name="lonePerson"
	 * @uml.associationEnd
	 */
	LonePerson(9, "LonePerson"), /**
	 *  name="visitor"
	 * @uml.associationEnd
	 */
	Visitor(10, "Visitor");

    private static final Logger logger = Logger.getLogger(HouseholdRelationship.class);

    private final int intValue;

    private final String stringValue;

    private HouseholdRelationship(int intValue, String stringValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public static HouseholdRelationship identify(String s) {
        for(HouseholdRelationship rel : HouseholdRelationship.values()) {
            if (rel.stringValue.equalsIgnoreCase(s)) {
                return rel;
            }
        }

        logger.error("Could not identify Household Relationship string: " + s);
		return null;
	}

    public int getIntValue() {
        return intValue;
    }
}
