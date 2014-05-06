package core.synthetic.survey;

import java.math.BigDecimal;

/**
 * The salary distribution in the fitted model.
 * 
 * @author qun
 * 
 */
public enum Salary {

	Below20800(1, 0, 20800),

	From20800to41600(2, 20800, 41600),

	From41600to67600(3, 41600, 67600),

	From67600to104000(4, 67600, 104000),

	From104000andOver(5, 104000, Integer.MAX_VALUE);

	private final BigDecimal minIncome;
	private final BigDecimal maxIncome;
    private final int intValue;

	Salary(int intValue, int minIncome, int maxIncome) {
		this.minIncome = new BigDecimal(minIncome);
		this.maxIncome = new BigDecimal(maxIncome);
        this.intValue = intValue;
	}

	public static Salary classify(BigDecimal value) {
		if (value.intValue() < 0) {
			throw new IllegalArgumentException("The gross income should not be negative number");
		} else if (value.intValue() < 20800) {
			return Salary.Below20800;
		} else if (value.intValue() < 41600) {
			return Salary.From20800to41600;
		} else if (value.intValue() < 67600) {
			return Salary.From41600to67600;
		} else if (value.intValue() < 104000) {
			return Salary.From67600to104000;
		} else {
			return Salary.From104000andOver;
		}
	}

    public int getIntValue() {
        return intValue;
    }
}
