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
package core.synthetic;

import hibernate.postgis.TravelZonesFacilitiesDAO;
import hibernate.postgis.TravelZonesFacilitiesEntity;
import hibernate.postgres.SyntheticPopulationDAO;
import hibernate.postgres.SyntheticPopulationEntity;
import hibernate.postgres.WeightsDAO;
import hibernate.postgres.WeightsEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jdbc.dao.SyntheticPopulationOutputDAO;

import org.apache.log4j.Logger;

import core.ApplicationContextHolder;
import core.HardcodedData;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.liveability.LiveabilityUtility;
import core.synthetic.survey.DemographicInfomation;

/**
 * 
 * The pool of individual. Includes all the individuals here with related
 * operation
 * 
 * @author qun
 * 
 */
public class IndividualPool extends StatisticalPool {

	private static final Logger logger = Logger.getLogger(IndividualPool.class);
	private final Map<Integer, Individual> individuals;

    private final WeightsDAO weightsDao;
    private final SyntheticPopulationDAO syntheticPopulationDAO;
    private final TravelZonesFacilitiesDAO travelZonesFacilitiesDAO;
    private final SyntheticPopulationOutputDAO syntheticPopulationOutputDAO;

	@SuppressWarnings("hiding")
	private int maxId; // keep tracks of the maximum id of individuals in the
						// pool


	public IndividualPool() {

		this.individuals = new ConcurrentHashMap<>();
		this.maxId = 0;
        weightsDao = ApplicationContextHolder.getBean(WeightsDAO.class);
        syntheticPopulationDAO = ApplicationContextHolder.getBean(SyntheticPopulationDAO.class);
        travelZonesFacilitiesDAO = ApplicationContextHolder.getBean(TravelZonesFacilitiesDAO.class);
        syntheticPopulationOutputDAO = ApplicationContextHolder.getBean(SyntheticPopulationOutputDAO.class);

	}

	/**
	 * Adds a individual into the pool
	 * 
	 * @param individual
	 *            the individual will be added into the pool
	 */

	@SuppressWarnings("boxing")
	private void addIndividual(Individual individual) {

		/*
		 * In the simplified net migration, some individuals in the pool are
		 * duplicated. The following if statement is commented out so that an
		 * individual exactly similar to one already existent in the pool can be
		 * added to the pool.
		 */

		if (this.individuals.containsKey(this.maxId + 1)) {
			Set<Integer> keys = this.individuals.keySet();
			this.maxId = Collections.max(keys);
		}

		individual.setId(this.maxId + 1);

		this.individuals.put(individual.getId(), individual);
		this.maxId++;
	}

	/**
	 * Returns the individuals' maximum id in the pool
	 * 
	 * @return maxId
	 * 
	 * @author Johan Barthelemy
	 */
	@Override
	public int getMaxId() {
		return this.maxId;
	}

	/**
	 * Removes a individual into the pool and mediator will notice the change of
	 * the attributes distribution.
	 * 
	 * @param individual
	 *            the individual will be removed out of the pool.
	 */

	@SuppressWarnings("boxing")
	private void removeIndividual(Individual individual) {
		this.individuals.remove(individual.getId());
	}

	public Map<Integer, Individual> getIndividuals() {
		return this.individuals;
	}

	/**
	 * generates individual pool
	 * 
	 * @param number
	 *            required number of individuals
	 */
	@SuppressWarnings({ "unchecked", "boxing" })
	public void generatePool(int number) {

		List<SyntheticPopulationEntity> syntheticPopulations = syntheticPopulationDAO.findFromFirst(number);

		for (SyntheticPopulationEntity syntheticPopulation : syntheticPopulations) {
			Individual individual = new Individual(syntheticPopulation);
			this.individuals.put(individual.getId(), individual);
		}

		Set<Integer> keys = this.individuals.keySet();
		this.maxId = Collections.max(keys);
	}

	@SuppressWarnings({ "boxing", "unchecked" })
	public void generatePoolFromHousehold(int number) {
		// TODO Auto-generated method stub

		int tempNumber = number;

		for (int i = 0; i < tempNumber; i++) {

			int randomId = HardcodedData.random.nextInt(106066);

			if (this.individuals.containsKey(randomId)) {
				tempNumber--;
				continue;
			}
			SyntheticPopulationEntity selected = syntheticPopulationDAO.findById(randomId);

			int householdId = selected.getHouseholdId();

			List<SyntheticPopulationEntity> syntheticPopulations = syntheticPopulationDAO.findByHouseholdId(householdId);

			for (SyntheticPopulationEntity syntheticPopulation : syntheticPopulations) {

				Individual individual = new Individual(syntheticPopulation);
				add(individual);
			}
		}
	}


	/**
	 * Clears the pool
	 */
	@Override
	public void clearPool() {
		this.individuals.clear();
	}

	@Override
	/**
	 * Gets the number of individuals in the pool
	 * @return the number of individuals in the pool
	 */
	public int getPoolNumber() {
		if (this.individuals != null) {
			return this.individuals.size();
		}
		return 0;

	}

	@Override
	/**
	 * Adds a individual into the pool and mediator will notice the change of the attributes distribution. 
	 * @param o the individual will be add into the pool
	 */
	public void add(Object o) {
		addIndividual((Individual) o);
	}

	@Override
	/**
	 * Removes a individual into the pool and mediator will notice the change of the attributes distribution. 
	 * @param o the individual will be removed out of the pool
	 */
	public void remove(Object o) {
		removeIndividual((Individual) o);
	}

	/**
	 * 
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IndividualPool [individuals=" + this.individuals.toString()
				+ " maxId=" + this.maxId + ']';
	}

	@SuppressWarnings("boxing")
	public Individual getByID(int id) {
		Individual individual = this.individuals.get(id);
		if (individual == null) {
			return null;
		}
		return individual;
	}

	@Override
	public Object getPoolComponent() {
		// TODO Auto-generated method stub
		return getIndividuals();
	}

	/**
	 * calculates individual's satisfaction
	 * 
	 * @param liveabilityUtility
	 * @return a set of individual's id that their satisfaction is lower than
	 *         threshold
	 */
	@SuppressWarnings("boxing")
	public Set<Integer> calculateIndividualSatisfaction(
			LiveabilityUtility liveabilityUtility,
			Map<Integer, Double> travelZoneCongestion,
			HouseholdPool householdPool) {

		final double threshold = -0.5;

		Set<Integer> lowSatisfactionHouseholds = new HashSet<>();

		Map<Integer, TravelZonesFacilitiesEntity> travelZonesFacilitiesMap = travelZonesFacilitiesDAO.getByMap();

		for (Individual individual : this.individuals.values()) {
			// try {

			WeightsEntity weights = weightsDao.findByDemographic(DemographicInfomation.classifyIndividual(individual));

			double congestion = 0;
			double satisfaction = 0;

			int travelZoneid = individual.getLivedTravelZoneid();

			if ((travelZoneCongestion.get(travelZoneid) != null)
					&& (!travelZoneCongestion.get(travelZoneid).isInfinite())) {
				congestion = travelZoneCongestion.get(travelZoneid);
			}

			TravelZonesFacilitiesEntity travelZonesFacilities = travelZonesFacilitiesMap.get(travelZoneid);

			if (travelZonesFacilities == null) {
				lowSatisfactionHouseholds.add(individual.getHouseholdId());
				continue;
			}

			double liveability = liveabilityUtility.calculateLiveability(
					weights, travelZonesFacilities, individual.getAge(),
					individual.getIncome(), individual.getHholdCategory(),
					congestion);

			satisfaction = liveabilityUtility.calculateSatisfactory(
					(int) householdPool.getByID(individual.getHouseholdId())
							.getTenure(), individual.getGender(), liveability);

			if (satisfaction < threshold) {
				lowSatisfactionHouseholds.add(individual.getHouseholdId());
				// }
			}
			individual.setSatisfaction(satisfaction);

		}

		return lowSatisfactionHouseholds;
	}

	/**
	 * calculates number of population in travel zones
	 * 
	 * @return a map which its key is travel zone id and its value is population
	 */
	@SuppressWarnings("boxing")
	public Map<Integer, Integer> calculateTravelZonePopulation() {
		Map<Integer, Integer> travelZonePopulation = new HashMap<>();
		int id;
		for (Individual individual : this.individuals.values()) {

			id = individual.getLivedTravelZoneid();
			Integer storedNumber = travelZonePopulation.get(id);
			if (storedNumber == null) {
				travelZonePopulation.put(id, 1);
			} else {
				storedNumber++;
				travelZonePopulation.put(id, storedNumber);
			}
		}

		return travelZonePopulation;
	}

	/**
	 * stores individuals' information into selected table
	 * 
	 * 
	 * @param year
	 *            the year of output
	 */
	@SuppressWarnings("null")
	public void storeIndividualInfomation(int year, HouseholdPool householdPool) {

        List<Individual> individuals = new ArrayList<>(getIndividuals().values());
        syntheticPopulationOutputDAO.storeIndividuals(householdPool, individuals, year);

	}



    @SuppressWarnings("null")
	public void storeRemovedIndividualInfomation(int year, List<Household> removedHouseholds) {
            syntheticPopulationOutputDAO.storeRemovedIndividualInfomation(year, removedHouseholds);
	}
}
