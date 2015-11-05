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
