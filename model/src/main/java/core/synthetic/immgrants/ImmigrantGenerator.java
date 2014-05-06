package core.synthetic.immgrants;

import java.math.BigDecimal;
import java.util.*;

import core.ArrayHandler;
import core.HardcodedData;
import core.synthetic.HouseholdPool;
import core.synthetic.IndividualPool;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.attribute.HighestEduFinished;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.attribute.Occupation;
import core.synthetic.attribute.TransportModeToWork;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;

public class ImmigrantGenerator {

	private final Map<Integer, List<int[]>> initSP = new HashMap<>();
	private int[] hholdIDs;
	private final Map<Category, List<Integer>> hholdIDsByType = new HashMap<>();

	public ImmigrantGenerator(HouseholdPool householdPool) {
		saveInitSP(householdPool);
	}

	/**
	 * generates a copy of initial synthetic population that will be used later
	 * to generate immigrant households.
	 */
	private void saveInitSP(HouseholdPool householdPool) {
		for (Household hhold : householdPool.getHouseholds().values()) {
			List<int[]> thisHholdDetails = new ArrayList<>();
			for (Individual resident : hhold.getResidents()) {
				int[] thisIndiv = new int[6];
				thisIndiv[0] = resident.getAge();
				thisIndiv[1] = resident.getIncome().intValue();
				thisIndiv[2] = resident.getHouseholdRelationship().ordinal();
				thisIndiv[3] = resident.getHholdCategory().ordinal();
				thisIndiv[4] = (resident.getGender() == Gender.Male) ? 1 : 0;
				thisIndiv[5] = resident.getLivedTravelZoneid();
				thisHholdDetails.add(thisIndiv);
			}
			initSP.put(hhold.getId(), thisHholdDetails);
		}
		hholdIDs = new int[initSP.size()];
		int tmpCount = 0;
		for (Integer hhID : initSP.keySet()) {
			hholdIDs[tmpCount] = hhID;
			tmpCount++;
		}

		// initialises hholdIDsByType
		hholdIDsByType.put(Category.NF,
				getIDsHholdType2(Category.NF, householdPool));
		hholdIDsByType.put(Category.HF1,
				getIDsHholdType2(Category.HF1, householdPool));
		hholdIDsByType.put(Category.HF2,
				getIDsHholdType2(Category.HF2, householdPool));
		hholdIDsByType.put(Category.HF3,
				getIDsHholdType2(Category.HF3, householdPool));
		hholdIDsByType.put(Category.HF4,
				getIDsHholdType2(Category.HF4, householdPool));
		hholdIDsByType.put(Category.HF5,
				getIDsHholdType2(Category.HF5, householdPool));
		hholdIDsByType.put(Category.HF6,
				getIDsHholdType2(Category.HF6, householdPool));
		hholdIDsByType.put(Category.HF7,
				getIDsHholdType2(Category.HF7, householdPool));
		hholdIDsByType.put(Category.HF8,
				getIDsHholdType2(Category.HF8, householdPool));
		hholdIDsByType.put(Category.HF9,
				getIDsHholdType2(Category.HF9, householdPool));
		hholdIDsByType.put(Category.HF10,
				getIDsHholdType2(Category.HF10, householdPool));
		hholdIDsByType.put(Category.HF11,
				getIDsHholdType2(Category.HF11, householdPool));
		hholdIDsByType.put(Category.HF12,
				getIDsHholdType2(Category.HF12, householdPool));
		hholdIDsByType.put(Category.HF13,
				getIDsHholdType2(Category.HF13, householdPool));
		hholdIDsByType.put(Category.HF14,
				getIDsHholdType2(Category.HF14, householdPool));
		hholdIDsByType.put(Category.HF15,
				getIDsHholdType2(Category.HF15, householdPool));
		hholdIDsByType.put(Category.HF16,
				getIDsHholdType2(Category.HF16, householdPool));

		
	}

	/**
	 * generates immigrant households by sampling from the initial synthetic
	 * population. New immigrant individuals are added to the current individual
	 * pool. New immigrant households are added to the current household pool as
	 * well the list of relocated households so they are allocated into
	 * dwellings in the next year.
	 * 
	 * @param nTotalMigrantHholds
	 *            this is the total number of immigrant households.
	 * @param percentageHholdTypes
	 *            array of percentage of each household type in the total number
	 *            of immigrant households. The order of household type in this
	 *            array is below [0] NF [1] HF1 [1] HF2 [1] HF3 [1] HF4 [1] HF5
	 *            [1] HF6 [1] HF7 [1] HF8 [1] HF9 [1] HF10 [1] HF11 [1] HF12 [1]
	 *            HF13 [1] HF14 [1] HF15 [1] HF16
	 * 
	 */
	public void generateMigrantHouseholds(int nTotalMigrantHholds,
			int[] percentageHholdTypes) {
		if (nTotalMigrantHholds == 0)
			return;
		int[] nHholds = new int[percentageHholdTypes.length];
		for (int i = 0; i <= nHholds.length - 1; i++)
			nHholds[i] = (int) ((double) nTotalMigrantHholds
					* (double) percentageHholdTypes[i] / 100 + 0.5);

		// creates
	}

	/**
	 * creates new LonePerson individuals and add them to the individualPool.
	 * Their gender are randomly assigned. Their age and income are randomly
	 * assigned within the ranges provided. The method also creates new NF
	 * household for each of these new LonePerson and add them to householdPool.
	 * These NF households are added to the relocatedHouseholds list to be
	 * allocated to dwelling in next year.
	 * 
	 * @param nLoneImmigrants
	 * @param maxAgeLoneMigrant
	 * @param minAgeLoneMigrant
	 * @param maxIncLoneMigrant
	 * @param minIncLoneMigrant
	 * @param crnIndivPool
	 * @param crnHholdPool
	 * @param relocatedHouseholds
	 */
	public void generateLoneImmigrants(int nLoneImmigrants,
			int maxAgeLoneMigrant, int minAgeLoneMigrant,
			int maxIncLoneMigrant, int minIncLoneMigrant,
			IndividualPool crnIndivPool, HouseholdPool crnHholdPool,
			Set<Household> relocatedHouseholds,
			List<String[]> immigrantRecords) {
		for (int iLM = 0; iLM <= nLoneImmigrants - 1; iLM++) {
			int newAge = HardcodedData.random.nextInt(maxAgeLoneMigrant
					- minAgeLoneMigrant + 1)
					+ minAgeLoneMigrant;
			int newIncome = HardcodedData.random.nextInt(maxIncLoneMigrant
					- minIncLoneMigrant + 1)
					+ minIncLoneMigrant;
			int newGenderInt = HardcodedData.random.nextInt(2);
			Gender newGender = (newGenderInt == 0) ? Gender.Female
					: Gender.Male;
			int tmpIndivID = crnIndivPool.getMaxId() + 1;

			int newTZID = 0;
		int newHholdID = crnHholdPool.getMaxId() + 1;

			Individual newLoneMigrant = new Individual(tmpIndivID, newAge,
					newGender, BigDecimal.valueOf((double) newIncome),
					HouseholdRelationship.LonePerson,
					Occupation.InadequatelyDescribedOrNotStated,
					TransportModeToWork.Other,
					HighestEduFinished.LevelOfEducationNotStated, Category.NF,
					newHholdID, newTZID);
			crnIndivPool.add(newLoneMigrant);

			Household newHh = new Household(newHholdID, Category.NF,
					BigDecimal.valueOf((double) newIncome));
		
			crnHholdPool.add(newHh);
			newHh.addIndividual(newLoneMigrant);
			newHh.setTravelDiariesChanged(true);

			String[] tmpRec = new String[] {
					String.valueOf(newLoneMigrant.getId()),
					String.valueOf(newLoneMigrant.getAge()),
					newLoneMigrant.getGender().toString(),
					newLoneMigrant.getHouseholdRelationship().toString(),
					String.valueOf(newLoneMigrant.getHouseholdId()),
					newLoneMigrant.getHholdCategory().toString(),
					String.valueOf(newHh.getId()) };
			immigrantRecords.add(tmpRec);

			relocatedHouseholds.add(newHh);

		}
	}

	/**
	 * generates immigrant households by sampling from the initial synthetic
	 * population. New immigrant individuals are added to the current individual
	 * pool. New immigrant households are added to the current household pool as
	 * well the list of relocated households so they are allocated into
	 * dwellings in the next year.
	 * 
	 * @param nImmigrants
	 * @param crnIndivPool
	 * @param crnHholdPool
	 * @param relocatedHouseholds
	 */

	public void generateMigrantHouseholds(int nImmigrants,
			IndividualPool crnIndivPool, HouseholdPool crnHholdPool,
			Set<Household> relocatedHouseholds,
			List<String[]> immigrantRecords) {
		if (nImmigrants <= 0)
			return;

		ArrayHandler arrHdlr = new ArrayHandler();
		Category[] hholdCat = Category.values();
		HouseholdRelationship[] hholdRel = HouseholdRelationship.values();

		int nMigrated = 0;
		while (nMigrated < nImmigrants) {
			// randomly picks a household in the initial synthetic population

			int[] pickedID = arrHdlr.pickRandomFromArray(hholdIDs, null, 1,
					HardcodedData.random);

			List<int[]> thisHholdDetails = initSP.get(pickedID[0]);

			// details of new household
			int newHholdID = crnHholdPool.getMaxId() + 1;
			Category newHholdCat = hholdCat[thisHholdDetails.get(0)[3]];
			int newHholdIncome = 0;

			// creates new individuals to represent residents of this household
			// and adds them to crnIndivPool
			List<Individual> thisHholdResidents = new ArrayList<>();
			for (int[] indivDetails : thisHholdDetails) {
				int tmpIndivID = crnIndivPool.getMaxId() + 1;
				int newAge = indivDetails[0];
				Gender newGender = (indivDetails[4] == 1) ? Gender.Male
						: Gender.Female;
				int newIncome = indivDetails[1];
				newHholdIncome += newIncome;
				int newTZID = 0;
				HouseholdRelationship newHholdRel = hholdRel[indivDetails[2]];
				Individual resident = new Individual(tmpIndivID, newAge,
						newGender, BigDecimal.valueOf((double) newIncome),
						newHholdRel,
						Occupation.InadequatelyDescribedOrNotStated,
						TransportModeToWork.Other,
						HighestEduFinished.LevelOfEducationNotStated,
						newHholdCat, newHholdID, newTZID);
				thisHholdResidents.add(resident);

				crnIndivPool.add(resident);

			}

			// creates new household and adds it to crnHholdPool
			Household newHhold = new Household(newHholdID, newHholdCat,
					BigDecimal.valueOf((double) newHholdIncome));
		
			newHhold.setTravelDiariesChanged(true);
			crnHholdPool.add(newHhold);
			for (Individual indiv : thisHholdResidents)
				newHhold.addIndividual(indiv);

			for (Individual resident : newHhold.getResidents()) {
				String[] tmpRec = new String[] {
						String.valueOf(resident.getId()),
						String.valueOf(resident.getAge()),
						resident.getGender().toString(),
						resident.getHouseholdRelationship().toString(),
						String.valueOf(resident.getHouseholdId()),
						resident.getHholdCategory().toString(),
						String.valueOf(newHhold.getId()) };
				immigrantRecords.add(tmpRec);
			}

			// adds the new household to relocatedHouseholds so it will be
			// allocated to a dwelling in the next year.
			relocatedHouseholds.add(newHhold);

			// adds the number of residents in this new household to nMigrated
			nMigrated += thisHholdResidents.size();
		}
	}

	/**
	 * 
	 * @param nNewHhold
	 * @param newHholdCat
	 * @param crnIndivPool
	 * @param crnHholdPool
	 * @param relocatedHouseholds
	 * @param immigrantRecords
	 */
	public void generateImmigrantHholds(int nNewHhold, Category newHholdCat,
			IndividualPool crnIndivPool, HouseholdPool crnHholdPool,
			Set<Household> relocatedHouseholds,
			List<String[]> immigrantRecords) {
		HouseholdRelationship[] hholdRel = HouseholdRelationship.values();

		List<Integer> hholdIDs = hholdIDsByType.get(newHholdCat);

		// if there are no households of type newHholdCat, i.e.
		// hholdIDs.size==0, then quit the method.
		// No new households will be created and added to the crnHholdPool.
		// No new individuals will be created and added to the crnIndivPool.
		if (hholdIDs.isEmpty()) {
			return;
		}

		for (int ihh = 1; ihh <= nNewHhold; ihh++) {
			int pickedID = HardcodedData.random.nextInt(hholdIDs.size());

			List<int[]> thisHholdDetails = initSP.get(hholdIDs
					.get(pickedID));

			// details of new household
			int newHholdID = crnHholdPool.getMaxId() + 1;
			int newHholdIncome = 0;

			// creates new individuals to represent residents of this household
			// and adds them to crnIndivPool
			List<Individual> thisHholdResidents = new ArrayList<>();
			for (int[] indivDetails : thisHholdDetails) {
				int tmpIndivID = crnIndivPool.getMaxId() + 1;
				int newAge = indivDetails[0];
				Gender newGender = (indivDetails[4] == 1) ? Gender.Male
						: Gender.Female;
				int newIncome = indivDetails[1];
				newHholdIncome += newIncome;
				int newTZID = 0;
				HouseholdRelationship newHholdRel = hholdRel[indivDetails[2]];
				Individual resident = new Individual(tmpIndivID, newAge,
						newGender, BigDecimal.valueOf((double) newIncome),
						newHholdRel,
						Occupation.InadequatelyDescribedOrNotStated,
						TransportModeToWork.Other,
						HighestEduFinished.LevelOfEducationNotStated,
						newHholdCat, newHholdID, newTZID);
				thisHholdResidents.add(resident);

				crnIndivPool.add(resident);
			}

			// creates a new household and adds it to crnHholdPool
			Household newHhold = new Household(newHholdID, newHholdCat,
					BigDecimal.valueOf((double) newHholdIncome));
			
			newHhold.setTravelDiariesChanged(true);
			crnHholdPool.add(newHhold);
			for (Individual indiv : thisHholdResidents)
				newHhold.addIndividual(indiv);

			for (Individual resident : newHhold.getResidents()) {
				String[] tmpRec = new String[] {
						String.valueOf(resident.getId()),
						String.valueOf(resident.getAge()),
						resident.getGender().toString(),
						resident.getHouseholdRelationship().toString(),
						String.valueOf(resident.getHouseholdId()),
						resident.getHholdCategory().toString()
//						String.valueOf(newHhold.getId()),
//						newHhold.getCategory().toString() 
						};
				immigrantRecords.add(tmpRec);
			}

			// adds the new household to relocatedHouseholds so it will be
			// allocated to a dwelling in the next year.
			relocatedHouseholds.add(newHhold);
		}
	}

	private List<Integer> getIDsHholdType2(Category hholdCat,
			HouseholdPool crnHholdPool) {

		List<Integer> tmpids = new ArrayList<>();
		for (Household hhold : crnHholdPool.getHouseholds().values())
			if (hhold.getCategory().equals(hholdCat)) {
				tmpids.add(hhold.getId());
			}

		return tmpids;
	}
}
