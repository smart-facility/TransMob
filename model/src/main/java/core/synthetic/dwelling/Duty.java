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
