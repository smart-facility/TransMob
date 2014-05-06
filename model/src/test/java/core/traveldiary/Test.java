package core.traveldiary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import core.HardcodedData;
import core.synthetic.HouseholdPool;
import core.synthetic.IndividualPool;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.traveldiary.LocationTypes;

public class Test {

    private static final Logger logger = Logger.getLogger(HTSDataHandler.class);
	private IndividualPool individualPool;
	private HouseholdPool householdPool;
	public static Set<Integer> selected = new HashSet<>();

	public static void main(String[] args) {
		// HTSDataHandler htsHdlr = new HTSDataHandler(
		// "C:/Documents and Settings/qun/Desktop/project/Req12602 - HTS data sent to SMART (all SLAs) for DB.csv",
		// new Random(0));
		HTSDataHandler htsHdlr = new HTSDataHandler(HardcodedData.random);

		LocationTypes locationTypes = new LocationTypes();
		locationTypes.readLocationTypes();

		List<Individual> individuals = new ArrayList<>();

		Individual individual1 = new Individual();
		individual1.setId(1);
		individual1.setHouseholdRelationship(HouseholdRelationship.Married);
		individual1.setHholdCategory(Category.HF5);

		Individual individual2 = new Individual();
		individual2.setId(2);
		individual2.setHouseholdRelationship(HouseholdRelationship.Married);
		individual2.setHholdCategory(Category.HF5);

		Individual individual3 = new Individual();
		individual3.setId(3);
		individual3.setHouseholdRelationship(HouseholdRelationship.U15Child);
		individual3.setHholdCategory(Category.HF5);

		individuals.add(individual1);
		individuals.add(individual2);
		individuals.add(individual3);

		Household household = new Household();

		household.setResidents(individuals);

		household.setId(1);

		int[][] spHhold = htsHdlr.transform(household);
		List<int[][]> spTD = htsHdlr.setTDtoSPHhold(spHhold);

		htsHdlr.setODtoSpTD(spTD, locationTypes);
		// HashMap<Integer, ArrayList<HTSHouseholdDiary>> newHholdTD = htsHdlr
		// .getHtsTDsByHholdType();
		// ArrayHandler arrHdlr = new ArrayHandler();
		//
		// SyntheticPopulationDAO syntheticPopulationDAO = new
		// SyntheticPopulationDAO();
		//
		// Test test = new Test();
		// test.test(htsHdlr, syntheticPopulationDAO, 113016);
		//
		// logger.info("number of selected HTS household: " + selected.size());
		// logger.info(htsHdlr.displayHtsTDsByHholdType());

		// testSelectedCases(htsHdlr);

	}

	/**
	 * test assigning travel diary with selected number of agents in the
	 * database
	 * 
	 * @param htsHdlr
	 *            hts data handler
	 * @param number
	 */
	private void test(HTSDataHandler htsHdlr, int number) {

		this.individualPool = new IndividualPool();
		householdPool = new HouseholdPool();

		this.individualPool.generatePool(number);

		this.householdPool.generatePool(this.individualPool);
		List<int[][]> spTD = null;

		for (Household household : householdPool.getHouseholds().values()) {
			int[][] spHhold = htsHdlr.transform(household);
			spTD = htsHdlr.setTDtoSPHhold(spHhold);
		}
	}

	// /*
	// * Each record represents an individual. The columns in spHhold are (in
	// * order) - hholdID, ID of the SP household (from householdPool) -
	// indivID,
	// * ID of the SP individual (from individualPool) - hhholdRelationship,
	// * values of householdRelationship need to be converted to integers as
	// * followed 0 Married 1 DeFacto 2 LoneParent 3 U15Child 4 Student 5
	// O15Child
	// * 6 Relative 7 NonRelative 8 GroupHhold 9 LonePerson 10 Visitor
	// hholdType,
	// * values of hholdType need to be converted to integers as followed 0 NF 1
	// * HF1 2 HF2 3 HF3 4 HF4 5 HF5 6 HF6 7 HF7 8 HF8 9 HF9 10 HF10 11 HF11 12
	// * HF12 13 HF13 14 HF14 15 HF15 16 HF16
	// */
	// private int[][] transform(Household household) {
	//
	// int[][] spHhold = new int[household.getNumberResidents()][4];
	//
	// for (int i = 0; i < household.getResidents().size(); i++) {
	// spHhold[i][0] = household.getId();
	// spHhold[i][1] = household.getResidents().get(i).getId();
	// spHhold[i][2] = codeHouseholdRelationship(household.getResidents()
	// .get(i).getHouseholdRelationship());
	// spHhold[i][3] = codeHouseholdCategory(household.getResidents()
	// .get(i).getHholdCategory());
	// }
	//
	// return spHhold;
	// }
	//
	// private int codeHouseholdCategory(Category category) {
	// String s = category.toString();
	// if (s.equals("HF1")) {
	// return 1;
	// } else if (s.equals("HF2")) {
	// return 2;
	// } else if (s.equals("HF3")) {
	// return 3;
	// } else if (s.equals("HF4")) {
	// return 4;
	// } else if (s.equals("HF5")) {
	// return 5;
	// } else if (s.equals("HF6")) {
	// return 6;
	// } else if (s.equals("HF7")) {
	// return 7;
	// } else if (s.equals("HF8")) {
	// return 8;
	// } else if (s.equals("HF9")) {
	// return 9;
	// } else if (s.equals("HF10")) {
	// return 10;
	// } else if (s.equals("HF11")) {
	// return 11;
	// } else if (s.equals("HF12")) {
	// return 12;
	// } else if (s.equals("HF13")) {
	// return 13;
	// } else if (s.equals("HF14")) {
	// return 14;
	// } else if (s.equals("HF15")) {
	// return 15;
	// } else if (s.equals("HF16")) {
	// return 16;
	// } else if (s.equals("NF")) {
	// return 0;
	// } else {
	// logger.error(s + "is wrong type");
	// }
	// return -1;
	// }
	//
	// private int codeHouseholdRelationship(
	// HouseholdRelationship householdRelationship) {
	// if (householdRelationship.toString().equals("Married")) {
	// return 0;
	// } else if (householdRelationship.toString().equals("DeFacto")) {
	// return 1;
	// } else if (householdRelationship.toString().equals("LoneParent")) {
	// return 2;
	// } else if (householdRelationship.toString().equals("U15Child")) {
	// return 3;
	// } else if (householdRelationship.toString().equals("Student")) {
	// return 4;
	// } else if (householdRelationship.toString().equals("O15Child")) {
	// return 5;
	// } else if (householdRelationship.toString().equals("Relative")) {
	// return 6;
	// } else if (householdRelationship.toString().equals("NonRelative")) {
	// return 7;
	// } else if (householdRelationship.toString().equals("GroupHhold")) {
	// return 8;
	// } else if (householdRelationship.toString().equals("LonePerson")) {
	// return 9;
	// } else if (householdRelationship.toString().equals("Visitor")) {
	// return 10;
	// } else {
	// logger.error("wrong type");
	// System.exit(1);
	// }
	// return -1;
	// }

	private void testSelectedCases(HTSDataHandler htsHdlr) {
		int[][] spHhold = new int[][] { { 0, 203, 1, 3 }, { 0, 205, 1, 3 },
				{ 0, 105, 4, 3 }, { 0, 106, 3, 3 }, { 0, 107, 3, 3 },
				{ 0, 108, 3, 3 }, { 0, 109, 3, 3 }, { 0, 305, 6, 3 },
				{ 0, 306, 6, 3 }, { 0, 303, 6, 3 } };
		logger.debug("set td to sp household which has dependent children and students");
		List<int[][]> spTD = htsHdlr.setTDtoSPHhold(spHhold);

		int[][] spHhold2 = new int[][] { { 0, 203, 1, 7 }, { 0, 205, 1, 7 },
				{ 0, 105, 4, 7 }, { 0, 106, 4, 7 } };
		logger.debug("set td to sp household2 which doesn�t have dependent children, and has students");
		spTD = htsHdlr.setTDtoSPHhold(spHhold2);

		int[][] spHhold3 = new int[][] { { 0, 203, 1, 5 }, { 0, 205, 1, 5 },
				{ 0, 305, 6, 5 }, { 0, 306, 6, 5 } };
		logger.debug("set td to sp household3 which  has dependent children, but doesn�t have any students.");
		spTD = htsHdlr.setTDtoSPHhold(spHhold3);

		int[][] spHhold4 = new int[][] { { 0, 203, 1, 1 }, { 0, 205, 1, 1 } };
		logger.debug("set td to sp household4 which  doesn�t have dependent children and doesn�t have students.");
		spTD = htsHdlr.setTDtoSPHhold(spHhold4);

		int[][] spHhold5 = new int[][] { { 0, 203, 1, 5 }, { 0, 205, 1, 5 },
				{ 0, 305, 6, 5 }, { 0, 306, 6, 5 }, { 0, 307, 6, 5 },
				{ 0, 308, 6, 5 }, { 0, 309, 6, 5 }, { 0, 310, 6, 5 },
				{ 0, 311, 6, 5 }, { 0, 312, 6, 5 }, { 0, 313, 6, 5 },
				{ 0, 314, 6, 5 } };
		logger.debug("set td to sp household5 which has a large number of dependent children, say 10, which is larger than number of dependent children in any HTS households.");
		spTD = htsHdlr.setTDtoSPHhold(spHhold5);
	}
}
