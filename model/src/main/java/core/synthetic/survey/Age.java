package core.synthetic.survey;

import core.HardcodedData;

/**
 * The age distribution in the fitted model.
 * 
 * @author qun
 */
public enum Age {

	From0to29years(1, 0, 29),

	From30to49years(2, 30, 49),

	From50to64years(3, 50, 64),

	From65andOlder(4, 65, 104);

	private final int startAge;
	private final int endAge;
    private final int intValue;

    Age(int intValue, int startAge, int endAge) {
		this.startAge = startAge;
		this.endAge = endAge;
        this.intValue = intValue;
	}

    /**
	 * Generates random age within it's age group
	 * 
	 * @return random age within it's age group
	 */
	public int generateRandomAge() {
		return HardcodedData.random.nextInt(endAge - startAge) + startAge;
	}

	/**
	 * Classifies a integer age to a age group
	 * 
	 * @param age
	 *            a integer age
	 * @return a age category
	 */
	public static Age classify(int age) {
		String hintString = "The age should be from 1 to 120";
		if (age < 0) {
			throw new IllegalArgumentException(hintString
					+ ",cannot equal or less than 0 years old");
		} else if (age <= 29) {
			return Age.From0to29years;
		} else if (age <= 49) {
			return Age.From30to49years;
		} else if (age <= 64) {
			return Age.From50to64years;
		} else {
			return Age.From65andOlder;
		}
	}

    public int getIntValue() {
        return intValue;
    }
}
