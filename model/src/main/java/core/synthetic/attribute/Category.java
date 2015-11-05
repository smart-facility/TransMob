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
 * The category distribution describes the household type in the individual and
 * household pool
 *
 * @author qun
 *
 */
public enum Category {
    /**
     *  name="nF"
     * @uml.associationEnd
     */
    NF("NF"), /**
     *  name="hF1"
     * @uml.associationEnd
     */
    HF1("HF1"), /**
     *  name="hF2"
     * @uml.associationEnd
     */
    HF2("HF2"), /**
     *  name="hF3"
     * @uml.associationEnd
     */
    HF3("HF3"), /**
     *  name="hF4"
     * @uml.associationEnd
     */
    HF4("HF4"), /**
     *  name="hF5"
     * @uml.associationEnd
     */
    HF5("HF5"), /**
     *  name="hF6"
     * @uml.associationEnd
     */
    HF6("HF6"), /**
     *  name="hF7"
     * @uml.associationEnd
     */
    HF7("HF7"), /**
     *  name="hF8"
     * @uml.associationEnd
     */
    HF8("HF8"), /**
     *  name="hF9"
     * @uml.associationEnd
     */
    HF9("HF9"), /**
     *  name="hF10"
     * @uml.associationEnd
     */
    HF10("HF10"), /**
     *  name="hF11"
     * @uml.associationEnd
     */
    HF11("HF11"), /**
     *  name="hF12"
     * @uml.associationEnd
     */
    HF12("HF12"), /**
     *  name="hF13"
     * @uml.associationEnd
     */
    HF13("HF13"), /**
     *  name="hF14"
     * @uml.associationEnd
     */
    HF14("HF14"), /**
     *  name="hF15"
     * @uml.associationEnd
     */
    HF15("HF15"), /**
     *  name="hF16"
     * @uml.associationEnd
     */
    HF16("HF16");

    private static final Logger logger = Logger.getLogger(Category.class);

    private String value;

    private Category(String value) {
        this.value = value;
    }

    /**
     * Return the Category enum for the given string.
     * @param s The category string value.
     * @return The Category enum matching the string. If no match, then null is returned.
     */
    public static Category identify(String s) {

        for (Category category : Category.values()) {
            if (category.value.equals(s)) {
                return category;
            }
        }

        logger.error(s + " is wrong type");
        return null;
    }

    public static int codeHouseholdCategory(Category category) {

        if (category != null) {
            return category.ordinal();
        }

        logger.error("NULL category");
        return -1;
    }

}
