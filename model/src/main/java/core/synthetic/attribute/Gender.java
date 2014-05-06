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
