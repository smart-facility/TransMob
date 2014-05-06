package core.synthetic;

import java.math.BigDecimal;
import java.util.Random;

import org.apache.log4j.Logger;

import core.HardcodedData;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.attribute.HighestEduFinished;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.attribute.Occupation;
import core.synthetic.attribute.TransportModeToWork;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;

//import java.security.SecureRandom;
/**
 * a class for generating agents randomly
 * 
 * @author qun
 * 
 */
public class RandomAgentsGenerator {

	private static final int averageWeeklyIncome = 1125;
    private static final Logger logger = Logger.getLogger(RandomAgentsGenerator.class);

	public RandomAgentsGenerator() {
	}


	/**
	 * generates agents according required number (maximum) and available
	 * dwellings. a typical household is 2 adults without children
	 * 
	 * @param id
	 *            exist maximum id
	 * @param agentNumber
	 *            number of random generated agents
	 * @param random
	 * @param householdPool
	 * @param individualPool
	 * @param dwellingAllocator
	 * @param year
	 */
	public void generateAgents(int id, int agentNumber, Random random,
			HouseholdPool householdPool, IndividualPool individualPool,
			DwellingAllocator dwellingAllocator, int year) {

		for (int i = 0; i < agentNumber; i += 2) {

			Individual individual1 = new Individual();
			Individual individual2 = new Individual();

			constructIndividual(id, random, householdPool, i, individual1,
					individual2);

			Household household = new Household();
			household.addIndividual(individual1);
			household.addIndividual(individual2);

			boolean isAdd = addIfHaveDwellings(dwellingAllocator, household,
					year);

			if (isAdd) {
				householdPool.add(household);
				individualPool.add(individual1);
				individualPool.add(individual2);
				logger.debug("Add Random household: "
						+ household.toString());
			} else {
				break;
			}

		}

	}

	private void constructIndividual(int id, Random random,
			HouseholdPool householdPool, int i, Individual individual1,
			Individual individual2) {
		individual1.setId(id + i + 1);
		individual2.setId(id + i + 2);

		int minimalYear = 25;
		int maximumAge = 31;
		individual1.setAge(minimalYear
				+ Math.abs(random.nextInt() % (maximumAge - minimalYear)));
		individual2.setAge(minimalYear
				+ Math.abs(random.nextInt() % (maximumAge - minimalYear)));

		individual1.setGender(Gender.Male);
		individual2.setGender(Gender.Female);

		individual1.setIncome(new BigDecimal(averageWeeklyIncome));
		individual2.setIncome(new BigDecimal(1125));

		individual1.setOccupation(Occupation.random());
		individual2.setOccupation(Occupation.random());

		individual1.setTransportModeToWork(TransportModeToWork.random());
		individual2.setTransportModeToWork(TransportModeToWork.random());

		individual1.setHighestEduFinished(HighestEduFinished.random());
		individual2.setHighestEduFinished(HighestEduFinished.random());

		individual1.setHholdCategory(Category.HF1);
		individual2.setHholdCategory(Category.HF1);

		if (random.nextInt() % 2 == 0) {
			individual1.setHouseholdRelationship(HouseholdRelationship.Married);
			individual2.setHouseholdRelationship(HouseholdRelationship.Married);
		} else {
			individual1.setHouseholdRelationship(HouseholdRelationship.DeFacto);
			individual2.setHouseholdRelationship(HouseholdRelationship.DeFacto);
		}

		individual1.setHouseholdId(householdPool.getMaxId() + i / 2 + 1);
		individual2.setHouseholdId(householdPool.getMaxId() + i / 2 + 1);
	}

	private boolean addIfHaveDwellings(DwellingAllocator dwellingaAllocator,
			Household household, int year) {
		// boolean isAdd = false;

		for (int tz : HardcodedData.travelZonesLiveable) {

			// if (household.allocateToDwelling(dwellingPool, tz,
			// dwellingControl,
			// this.main.getHouseholdPool().getLiveabilityUtility()
			// .getTravelZonesFacilitiesMap())) {
			if (dwellingaAllocator.allocateAvailableDwelling(tz, year,
					household)) {
				return true;
			}

		}
		return false;
	}
}
