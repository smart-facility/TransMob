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
