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


/**
 * The gender distribution in the individual pool
 * @author   qun
 */
public enum Gender{
	/**
	 *   name="male"
	 * @uml.associationEnd  
	 */
	Male(1), /**
	 *   name="female"
	 * @uml.associationEnd  
	 */
	Female(2);

    private final int intValue;

    private Gender(int value) {
        intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }
}
