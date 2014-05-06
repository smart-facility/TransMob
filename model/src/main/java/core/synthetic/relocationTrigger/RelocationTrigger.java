package core.synthetic.relocationTrigger;

import hibernate.postgres.SyntheticPopulationDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.dao.SyntheticPopulationOutputDAO;

import core.ApplicationContextHolder;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;

public class RelocationTrigger {

	private final double beta0;
	private final double beta1;
	private final double beta2;
	private final double beta3;

    private final SyntheticPopulationDAO syntheticPopulationDAO;
    private final SyntheticPopulationOutputDAO syntheticPopulationOutputDAO;

	public RelocationTrigger() {
		this.beta0 = 0.623;
		this.beta1 = -0.000125;
		this.beta2 = -0.06874;
		this.beta3 = -0.5766;

        this.syntheticPopulationDAO = ApplicationContextHolder.getBean(SyntheticPopulationDAO.class);
        this.syntheticPopulationOutputDAO = ApplicationContextHolder.getBean(SyntheticPopulationOutputDAO.class);
	}

	public double calculateProbability(Household household, int[] previous) {
		double deltaIncome = household.getGrossIncome().doubleValue()
				- previous[0];
		double deltaBedrooms = calcualteBedrooms(household) - previous[1];
		double utility = calculateUtility(deltaIncome, deltaBedrooms,
				household.getTenure());

		double exp = Math.exp(utility);
		return exp / (1 + exp);
	}

	public double calculateUtility(double deltaIncome, double deltaBedrooms,
			double tenure) {

		return this.beta0 + this.beta1 * deltaIncome + this.beta2
				* deltaBedrooms + this.beta3 * tenure;

	}

	public int calcualteBedrooms(Household household) {

		int couple = 0;
		int children = 0;
		int other = 0;

		for (Individual individual : household.getResidents()) {

			if (individual.getHouseholdRelationship() == HouseholdRelationship.Married
					|| individual.getHouseholdRelationship() == HouseholdRelationship.DeFacto) {
				couple++;
				continue;
			}

			if (individual.getAge() < 10) {
				children++;
				continue;
			}

			other++;
		}

		return (int) (Math.ceil((double) couple / 2)
				+ Math.ceil((double) children / 3) + other);

	}

	public Map<Integer, int[]> getBaseData() {

		Map<Integer, int[]> results = new HashMap<>();

        List<int[]> householdData = this.syntheticPopulationDAO.findHouseholdData();

        for (int[] household : householdData) {
            int householdId = household[0];
            int income = household[1];
            int bedrooms = household[2];

            int[] temp = { income, bedrooms };

            results.put(householdId, temp);

        }

		return results;
	}

    /**
     * Retrieves Synthetic population data for the given year from the output database.
     * @param year The year to retrieve.
     * @return key: household_id, value: income, bedrooms
     */
	public Map<Integer, int[]> getDataForYear(int year) {
        return this.syntheticPopulationOutputDAO.getDataForYear(year);

	}
}
