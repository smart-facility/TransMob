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
package core.synthetic.dwelling;

import java.util.List;

import core.ApplicationContextHolder;
import hibernate.postgres.DutyRateDAO;
import hibernate.postgres.DutyRateEntity;

/**
 * A class for calculating duties in the purchasing dwellings.
 * 
 * @author qun
 */
public class Duty {

    private final DutyRateDAO dutyRateDao;

    private List<DutyRateEntity> rates;

	public Duty() {
		super();
        dutyRateDao = ApplicationContextHolder.getBean(DutyRateDAO.class);
		initial();
	}

	private void initial() {
		if (rates == null) {
			loadData();
		}

	}

	private void loadData() {
		rates = dutyRateDao.findAll();
	}

	public double calculateDuty(double prices) {
		double duty = 0;

		if (prices <= rates.get(0).getDutiableValue2()) {
			duty = prices * rates.get(0).getDutyRate();
		} else {
			for (int i = 1; i < rates.size(); i++) {
				if (prices > rates.get(i - 1).getDutiableValue2()
						&& prices <= rates.get(i).getDutiableValue2()) {
					duty = (prices - rates.get(i - 1).getDutiableValue2())
							* rates.get(i).getDutyRate() / 100 + rates.get(i).getBaseValue();
				}
			}

		}

		return duty;
	}

}
