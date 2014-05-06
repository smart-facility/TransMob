package core.synthetic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import core.ArrayHandler;
import core.HardcodedData;
import core.PostgresHandler;
import core.model.TextFileHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

public class SynPopGenerator {

    private static Logger logger = Logger.getLogger(SynPopGenerator.class);

	public static final String My_POSTGRESURL = "jdbc:postgresql://localhost:5432/";
	public static final String My_USERNAME = "postgres";
	public static final String My_PASSWORD = "password";
	public static final String GoodMessage = " - OK";
	public static final String BadMessage = " - BAD!!!";
	
	public static final int mean_dAgeParents = 2;
	public static final int min_dAgeParentChild = 16;
	public static final int max_dAgeYoungerParentEldestChild = 50;

	private static int[][] hhRelMale;
	private static int[][] hhRelFemale;
	private static int[][] nHF;
	private static int[][] nppInHF;
	private static int[][] hhComp;
	private static int[][] incomeMale;
	private static int[][] incomeFemale;
	private static int[][] minResidents;

	private static int[][] hhRelMaleOri;
	private static int[][] hhRelFemaleOri;
	private static int[][] nHFOri;
	private static int[][] nppInHFOri;
	private static int[][] hhCompOri;

	private static int[] pfIdx;
	private static int[] pmIdx;
	private static int[] p1fIdx;
	private static int[] p1mIdx;
	private static int[] fu15Idx;
	private static int[] mu15Idx;
	private static int[] u15Idx;
	private static int[] fstuIdx;
	private static int[] mstuIdx;
	private static int[] stuIdx;
	private static int[] fo15Idx;
	private static int[] mo15Idx;
	private static int[] o15Idx;
	private static int[] frelIdx;
	private static int[] mrelIdx;
	private static int[] fghIdx;
	private static int[] mghIdx;
	private static int[] lonePersonIdx;

	private static int[][] indivRecords;

	private static List<String[]> indivRecordsStr =  new ArrayList<>();
	private static List<String[]> hholdRecordsStr =  new ArrayList<>();


	public enum DBName {
		individual, household
	};

	public enum DBVariableType {
		int4, text
	};

	public enum IndivSchema {
		highest_education, household_relationship, income, occupation, population, registered_marital_status, social_marital_status, work_travel_mode;
	}; // is the ending not in an error?

	public enum HholdSchema {
		family_composition, family_composition_bysex, gross_family, gross_household, household_composition, min_individuals
	};

	public enum CDs {
		gs_1400508, gs_1400509, gs_1400512, gs_1400606, gs_1400607, gs_1400608, gs_1400611, gs_1400612, gs_1400615, gs_1400616, gs_1400617, gs_1400704, gs_1400705, gs_1400706, gs_1400707, gs_1400708, gs_1400709, gs_1400710, gs_1400711, gs_1400712, gs_1400713, gs_1400714, gs_1400715, gs_1400716, gs_1400717, gs_1400718, gs_1400719, gs_1400720, gs_1400721, gs_1400722, gs_1400723, gs_1400812, gs_1400813, gs_1400815, rw_1430102, rw_1430103, rw_1430104, rw_1430105, rw_1430106, rw_1430107, rw_1430108, rw_1430109, rw_1430110, rw_1430111, rw_1430201, rw_1430202, rw_1430203, rw_1430204, rw_1430205, rw_1430206, rw_1430207, rw_1430208, rw_1430209, rw_1430210, rw_1430211, rw_1430212, rw_1430213, rw_1430301, rw_1430302, rw_1430303, rw_1430304, rw_1430305, rw_1430306, rw_1430307, rw_1430308, rw_1430309, rw_1430310, rw_1430311, rw_1430312, rw_1430401, rw_1430402, rw_1430403, rw_1430404, rw_1430405, rw_1430406, rw_1430407, rw_1430408, rw_1430409, rw_1430410, rw_1430411, rw_1430412, rw_1430501, rw_1430502, rw_1430503, rw_1430504, rw_1430505, rw_1430506, rw_1430507, rw_1430508, rw_1430509, rw_1430510, rw_1430511, rw_1430512, rw_1430513, rw_1430601, rw_1430602, rw_1430603, rw_1430604, rw_1430605, rw_1430606, rw_1430607, rw_1430608, rw_1430609, rw_1430610, rw_1430611, rw_1430612, rw_1430613, rw_1430701, rw_1430702, rw_1430703, rw_1430704, rw_1430705, rw_1430706, rw_1430707, rw_1430708, rw_1430709, rw_1430710, rw_1430711, rw_1430712, rw_1430801, rw_1430802, rw_1430803, rw_1430804, rw_1430805, rw_1430806, rw_1430807, rw_1430808, rw_1430809, rw_1430810, rw_1430811, rw_1430812, rw_1430901, rw_1430902, rw_1430903, rw_1430904, rw_1430905, rw_1430906, rw_1430907, rw_1430908, rw_1430909, rw_1430910, rw_1430911, rw_1430912, rw_1431001, rw_1431002, rw_1431003, rw_1431004, rw_1431005, rw_1431006, rw_1431007, rw_1431008, rw_1431009, rw_1431010, rw_1431011, rw_1431012, rw_1431101, rw_1431102, rw_1431103, rw_1431104, rw_1431105, rw_1431106, rw_1431107, rw_1431108, rw_1431109, rw_1431110, rw_1431111, rw_1431112, rw_1431113, rw_1431114, rw_1431201, rw_1431202, rw_1431203, rw_1431204, rw_1431205, rw_1431206, rw_1431207, rw_1431208, rw_1431209, rw_1431210, rw_1431211, rw_1431301, rw_1431302, rw_1431303, rw_1431304, rw_1431305, rw_1431306, rw_1431307, rw_1431308, rw_1431309, rw_1431310, rw_1431311, rw_1431312, rw_1431401, rw_1431402, rw_1431403, rw_1431404, rw_1431405, rw_1431406, rw_1431407, rw_1431408, rw_1431409, rw_1431410, rw_1431411, rw_1431412, rw_1431413, rw_1431501, rw_1431502, rw_1431503, rw_1431504, rw_1431505, rw_1431506, rw_1431507, rw_1431508, rw_1431509, rw_1431510, rw_1431511, rw_1431512, rw_1431513, rw_1431514, rw_1431515, rw_1431601, rw_1431602, rw_1431603, rw_1431604, rw_1431605, rw_1431606, rw_1431607, rw_1431608, rw_1431609, rw_1431610, rw_1431611, rw_1431612, rw_1431701, rw_1431702, rw_1431703, rw_1431704, rw_1431705, rw_1431706, rw_1431707, rw_1431708, rw_1431709, rw_1431710, rw_1431711, rw_1431712, rw_1431801, rw_1431802, rw_1431803, rw_1431804, rw_1431806, rw_1431807, rw_1431808, rw_1431809, rw_1431810, rw_1431811, rw_1431812, rw_1431814, rw_1430101,
		// lt_1400701, lt_1400702, lt_1400703, lt_1400704, lt_1400705,
		// lt_1400707, lt_1400708, lt_1400709, lt_1400710, lt_1400711,
		// lt_1400712, lt_1400713, lt_1400714, lt_1400801, lt_1400807,
		// lt_1400809, lt_1400810, lt_1400811, lt_1400812, lt_1400813,
		// lt_1400814, lt_1400815, lt_1400901, lt_1400902, lt_1400903,
		// lt_1400904, lt_1400905, lt_1400906, lt_1400907, lt_1400908,
		// lt_1400909, lt_1400910, lt_1400911, lt_1400912, lt_1400913,
		// lt_1400914, lt_1400915, lt_1401001, lt_1401002, lt_1401003,
		// lt_1401004, lt_1401005, lt_1401006, lt_1401007, lt_1401008,
		// lt_1401009, lt_1401010, lt_1401011, lt_1401012, lt_1401101,
		// lt_1401102, lt_1401103, lt_1401104, lt_1401105, lt_1401106,
		// lt_1401107, lt_1401108, lt_1401109, lt_1401110, lt_1401111,
		// lt_1401112, lt_1401113, lt_1401114, lt_1401115, lt_1401116,
		// lt_1401201, lt_1401202, lt_1401203, lt_1401204, lt_1401205,
		// lt_1401206, lt_1401207, lt_1401208, lt_1401209, lt_1401210,
		// lt_1401211, lt_1401212, lt_1401213, lt_1401214, lt_1401215,
		// lt_1401216, lt_1401217, lt_1401301, lt_1401302, lt_1401303,
		// lt_1401305, lt_1401307, lt_1401308, lt_1401309, lt_1401310,
		// lt_1401311, lt_1402013, lt_1402014, lt_1402020, lt_1440101,
		// lt_1440102, lt_1440103, lt_1440104, lt_1440105, lt_1440106,
		// lt_1440107, lt_1440108, lt_1440109, lt_1400601, lt_1400602,
		// lt_1400603, lt_1400605, lt_1400614
		// rw_1431804
	};

	public enum HouseholdRelationship {
		id, year_1, year_2, Married, DeFacto, LoneParent, U15Child, Student, O15Child, Relative, NonRelative, GroupHhold, LonePerson, Visitor, note
	};

	public enum HouseholdRelationshipNote {
		_male, _female
	};

	public enum Income {
		id(-1, -1), year_1(-1, -1), year_2(-1, -1), zero(0, 0), $1_$149(1, 149), $150_$249(
				150, 249), $250_$399(250, 399), $400_$599(400, 599), $600_$799(
				600, 799), $800_$999(800, 999), $1000_$1299(1000, 1299), $1300_$1599(
				1300, 1599), $1600_$1999(1600, 1999), $2000_$10000(2000, 10000), na(
				-1, -1), note(-1, -1);
		private int val;

		Income(int lowBound, int upBound) {
			if (lowBound < 0 || upBound < 0){
				this.val = -1;
			}
			else {
				this.val = lowBound
						+ (int) ((upBound - lowBound) * HardcodedData.random.nextDouble());
			}
		}
	};

	public enum IncomeNote {
		_male, _female
	};

	public enum Population {
		year_1, year_2, males, females, persons, note
	};

	public enum FamilyComposition {
		id, families, note
	};

	public enum FamilyCompositionNote {
		HF1, HF2, HF3, HF4, HF5, HF6, HF7, HF8, HF9, HF10, HF11, HF12, HF13, HF14, HF15, HF16
	};

	public enum FamilyCompositionBySex {
		id, males, females, persons, note
	};

	public enum FamilyCompositionBySexNote {
		HF1, HF2, HF3, HF4, HF5, HF6, HF7, HF8, HF9, HF10, HF11, HF12, HF13, HF14, HF15, HF16
	};

	public enum HouseholdComposition {
		id, family_households, non_family_households, note
	};

	public enum MinIndividuals {
		index, min_parents, min_u15, min_students, min_o15, min_relatives, min_non_family, min_residents, note
	};

	public enum MinIndividualsNote {
		NF, HF1, HF2, HF3, HF4, HF5, HF6, HF7, HF8, HF9, HF10, HF11, HF12, HF13, HF14, HF15, HF16
	};

	private String verificationFile;
	private String ununifiedIndivRecsFile;
	private String ununifiedHholdRecsFile;
	private String finalSynPopFile;
	private String parentFolder;

	/**
	 * Class constructor.
	 */
	public SynPopGenerator() {
	}

	/**
	 * Class constructor.
	 */
	public SynPopGenerator(String folder) {
		parentFolder = folder;
		verificationFile = parentFolder + "SPverification.csv";
		ununifiedIndivRecsFile = parentFolder + "indivRecordsNotUnified.csv";
		ununifiedHholdRecsFile = parentFolder + "hholdRecordsNotUnified.csv";
		finalSynPopFile = parentFolder + "finSP.csv";

		File fVerification = new File(verificationFile);
		if (fVerification.exists())
			fVerification.delete();

		File fIndivRecs = new File(ununifiedIndivRecsFile);
		if (fIndivRecs.exists())
			fIndivRecs.delete();

		File fHholdRecs = new File(ununifiedHholdRecsFile);
		if (fHholdRecs.exists())
			fHholdRecs.delete();

		File fFinSP = new File(finalSynPopFile);
		if (fFinSP.exists())
			fFinSP.delete();
	}

	public String getFinalSynPopFile() {
		return finalSynPopFile;
	}

	/**
	 * generates the synthetic populatio and saves output files to a specified
	 * folder.
	 * 
	 */
	public void generateSP() {
		String verificationHeaders = "CD," + "Total population SP,"
				+ "Total population Census," + "Total population % deviation,"
				+ "Total HF population SP," + "Total HF population Census,"
				+ "Total HF population % deviation,"
				+ "Total NF population SP," + "Total NF population Census,"
				+ "Total NF population % deviation";
		TextFileHandler.writeToText(verificationFile, verificationHeaders, true);

		PostgresHandler indivDB = new PostgresHandler(My_POSTGRESURL
				+ DBName.individual, My_USERNAME, My_PASSWORD);
		PostgresHandler hholdDB = new PostgresHandler(My_POSTGRESURL
				+ DBName.household, My_USERNAME, My_PASSWORD);
		correctTables(indivDB, hholdDB);
		// close connections
		indivDB.closeConnection();
		hholdDB.closeConnection();

		makeFinalIndivRecords2(finalSynPopFile);
		// makeFinalIndivRecords("indivRecords.csv", 108195, 7,
		// "hholdRecords.csv", 57823, 3);
	}

	/**
	 * converts household indices from local scale (CD by CD) to global (across
	 * all CDs in the study area). The columns in outputFile are [0] age [1]
	 * gender [2] household_relationship [3] houshold_index [4] household_type
	 * [5] income [6] cd (for example, gs_1400508) [7] id (global index of
	 * individuals) [8] diary_weekdays_start (dummy data, the value is 0 in
	 * decoupling synthetic population) [9] diary_weekdays_end (dummy data, the
	 * value is 0 in decoupling synthetic population) [10] diary_weekend_start
	 * (dummy data, the value is 0 in decoupling synthetic population) [11]
	 * diary_weekend_end (dummy data, the value is 0 in decoupling synthetic
	 * population) [12] org_satisfaction (dummy data, the value is 0 in
	 * decoupling synthetic population) [13] travel_zone (the value is the
	 * numeric part in cd name, eg 1400508. This applies only in decoupling
	 * synthetic population)
	 * 
	 * @param outputFile
	 *            csv file where the final individual records are writen to.
	 */
	private void makeFinalIndivRecords2(String outputFile) {

		String[][] finIndRec = new String[indivRecordsStr.size()][indivRecordsStr
				.get(0).length + 7];

		String[][] indRec = new String[indivRecordsStr.size()][indivRecordsStr
				.get(0).length];
		String[][] hhRec = new String[hholdRecordsStr.size()][hholdRecordsStr
				.get(0).length];
		for (int i = 0; i <= indivRecordsStr.size() - 1; i++) {
			indRec[i] = indivRecordsStr.get(i);
		}
		for (int i = 0; i <= hholdRecordsStr.size() - 1; i++) {
			hhRec[i] = hholdRecordsStr.get(i);
		}

		for (int i = 0; i <= indivRecordsStr.size() - 1; i++) {
			for (int j = 0; j <= indivRecordsStr.get(i).length - 1; j++) {
				finIndRec[i][j] = indivRecordsStr.get(i)[j];
			}
		}

		for (int i = 0; i <= indRec.length - 1; i++) {
			String lochhid = indRec[i][3];
			String cd = indRec[i][6];
			boolean found = false;
			for (int ihh = 0; ihh <= hhRec.length - 1; ihh++) {
				if (hhRec[ihh][0].equals(lochhid) && hhRec[ihh][1].equals(cd)) {
					found = true;
					finIndRec[i][3] = Integer.toString(ihh);
					break;
				}
			}
			
			if (!found) {
				logger.debug("Can't find " + lochhid + " and " + cd
						+ " in household records!");
			}
			
			finIndRec[i][indivRecordsStr.get(0).length] = Integer.toString(i);
			/* creates dummy data for 5 columns in the table of initial
			 synthetic population, 'public.synthetic_population', in main 
			 model database.
			 These columns are 'diary_weekdays_start', 'diary_weekdays_end',
			 'diary_weekend_start', 'diary_weekend_end', 'org_satisfaction'
			 which are irrelevant/not required in decoupling of synthetic
			 population.
			
			 */
			for (int tmpi = 1; tmpi <= 5; tmpi++) {
				finIndRec[i][indivRecordsStr.get(0).length + tmpi] = Integer
						.toString(0);
			}
			// 'travel zone' is extracted from 'cd', e.g. if 'cd' gs_1400508,
			// the 'travel_zone' is 1400508 (because travel zone must be an
			// integer)
			int tmpi = 6;
			finIndRec[i][indivRecordsStr.get(0).length + tmpi] = indRec[i][6]
					.substring(indRec[i][6].lastIndexOf("_") + 1,
							indRec[i][6].length());
		}
		TextFileHandler.writeToCSV(outputFile, finIndRec, true);
	}

	/**
	 * corrects tables (obviously) for each CD in the study area
	 * 
	 * @param dbIndiv
	 *            object handling connection to the database that has ABS tables
	 *            of individuals' characteristics
	 * @param dbHhold
	 *            object handling connection to the database that has ABS tables
	 *            of households' characteristics
	 */
	private void correctTables(PostgresHandler dbIndiv, PostgresHandler dbHhold) {
		ArrayHandler arrHdlr = new ArrayHandler();

		for (CDs cd : CDs.values()) {
			Random random = HardcodedData.random;
			String cdName = cd.toString();
			logger.debug("CD name is: " + cdName);
			// get table B04
			// int[][] ageSex = dbIndiv.getIntArrayFromAllRows(
			// IndivSchema.population.toString(), cdName, arrHdlr
			// .makeIncrementalIntArray(1,
			// population.values().length - 1, 1));
			// get table B22a for Male
			hhRelMale = null;
			hhRelFemale = null;
			;
			nHF = null;
			nppInHF = null;
			hhComp = null;
			incomeMale = null;
			incomeFemale = null;
			minResidents = null;
			hhRelMale = dbIndiv.getIntArrayFromRows(
					IndivSchema.household_relationship.toString(),
					cdName,
					HouseholdRelationshipNote._male.toString(),
					HouseholdRelationship.note.toString(),
					arrHdlr.makeIncrementalIntArray(1,
							HouseholdRelationship.values().length - 1, 1));
			// get table B22a for Female
			hhRelFemale = dbIndiv.getIntArrayFromRows(
					IndivSchema.household_relationship.toString(),
					cdName,
					HouseholdRelationshipNote._female.toString(),
					HouseholdRelationship.note.toString(),
					arrHdlr.makeIncrementalIntArray(1,
							HouseholdRelationship.values().length - 1, 1));
			// get table B24 and B25
			for (FamilyCompositionNote fcn : FamilyCompositionNote.values()) {
				nHF = arrHdlr.concateMatrices(nHF, dbHhold.getIntArrayFromRows(
						HholdSchema.family_composition.toString(),
						cdName,
						fcn.toString(),
						FamilyComposition.note.toString(),
						arrHdlr.makeIncrementalIntArray(1,
								FamilyComposition.values().length - 1, 1)));
				nppInHF = arrHdlr
						.concateMatrices(
								nppInHF,
								dbHhold.getIntArrayFromRows(
										HholdSchema.family_composition_bysex
												.toString(),
										cdName,
										fcn.toString(),
										FamilyCompositionBySex.note
												.toString(),
										arrHdlr.makeIncrementalIntArray(1,
												FamilyCompositionBySex
														.values().length - 1, 1)));
			}
			// get table B30
			hhComp = dbHhold
					.getIntArrayFromAllRows(
							HholdSchema.household_composition.toString(),
							cdName,
							new int[] {
									HouseholdComposition.id.ordinal() + 1,
									HouseholdComposition.family_households
											.ordinal() + 1,
									HouseholdComposition.non_family_households
											.ordinal() + 1 });
			// get table B16 for Male
			incomeMale = dbIndiv.getIntArrayFromRows(IndivSchema.income
					.toString(), cdName, IncomeNote._male.toString(),
					Income.note.toString(), arrHdlr.makeIncrementalIntArray(1,
							Income.values().length - 2, 1)); // exclude the last
																// 2 fields,
																// which are
																// 'na' and
																// 'note'
			// get table B16 for Female
			incomeFemale = dbIndiv.getIntArrayFromRows(IndivSchema.income
					.toString(), cdName, IncomeNote._female.toString(),
					Income.note.toString(), arrHdlr.makeIncrementalIntArray(1,
							Income.values().length - 2, 1)); // exclude
																// the
																// last
																// 2
																// fields,
																// which
																// are
																// 'na'
																// and
																// 'note'

			// get table S1
			minResidents = dbHhold.getIntArrayFromAllRows(
					HholdSchema.min_individuals.toString(),
					HholdSchema.min_individuals.toString(), new int[] {
							MinIndividuals.index.ordinal() + 1,
							MinIndividuals.min_parents.ordinal() + 1,
							MinIndividuals.min_u15.ordinal() + 1,
							MinIndividuals.min_students.ordinal() + 1,
							MinIndividuals.min_o15.ordinal() + 1,
							MinIndividuals.min_relatives.ordinal() + 1,
							MinIndividuals.min_non_family.ordinal() + 1,
							MinIndividuals.min_residents.ordinal() + 1 });

			// 1. adjust the total number of people in each age group in B22
			// according to B04
			// ====================================================================================================================
			// adjust hhold relationship for Males
			// int[][] newhhRelMale =
			// this.correctHouseholdRelationshipTable(cdName, dbIndiv, ageSex,
			// hhRelMale, population.males.ordinal());
			// int[][] newhhRelMale = this.correctHholdRelTable(cdName, dbIndiv,
			// ageSex, hhRelMale, population.males.ordinal());
			// adjust hhold relationship for Females
			// int[][] newhhRelFemale =
			// this.correctHouseholdRelationshipTable(cdName, dbIndiv, ageSex,
			// hhRelFemale, population.females.ordinal());
			// int[][] newhhRelFemale = this.correctHholdRelTable(cdName,
			// dbIndiv, ageSex, hhRelFemale, population.females.ordinal());
			// logger.info("\tStep 1&2 done!");
			// int[][] newhhRelMale = hhRelMale;
			// int[][] newhhRelFemale = hhRelFemale;
			// --------------------------------------------------------------------------------------------------------------------

			// saves the original tables for verification purposes
			hhRelMaleOri = hhRelMale;
			hhRelFemaleOri = hhRelFemale;
			nHFOri = nHF;
			nppInHFOri = nppInHF;
			hhCompOri = hhComp;

			// 2. check and correct newhhRelMale,newhhRelFemale to ensure the
			// sum of defacto and married males and females is
			// an even number
			correctNumberOfMarriedAndDefacto(hhRelMale, hhRelFemale);

			// 3. adjust the number of family households in table B24 according
			// to the new values in B22
			// so that the number of people in HF in B22 is greater than or
			// equal to min people required.
			// ====================================================================================================================

			nHF = adjustNumberOfHF(nHF);
			boolean badCD = false;
			if (nHF == null) {
				badCD = true;
				logger.debug(cdName + "   BAD CD!!!");
				TextFileHandler.writeToText("badCD.csv", cdName, true);

				// String[][] hhCompStr = new String[1][rawhhComp.length+1];
				// hhCompStr[0][0] = cd.toString();
				// hhCompStr[0][1] = Integer.toString(rawhhComp[0][2]);
				// for (int i=1; i<=rawhhComp.length-1; i++)
				// hhCompStr[0][i+1] = Integer.toString(rawhhComp[i][1] +
				// rawhhComp[i][2]);
				// (new TextFileHandler()).writeToCSV("hhComp.csv", hhCompStr,
				// true);

				continue;
			}
			// logger.info("\tStep 3 done!");
			// --------------------------------------------------------------------------------------------------------------------

			// 4. adjust the total number of males and females in each HF type
			// (i.e. correcting table B25)
			// int[][] newnppInHF = null;
			if (!badCD) {
				int colMarried = HouseholdRelationship.Married.ordinal();
				int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
				int colLoneParent = HouseholdRelationship.LoneParent.ordinal();
				int colU15 = HouseholdRelationship.U15Child.ordinal();
				int colStu = HouseholdRelationship.Student.ordinal();
				int colO15 = HouseholdRelationship.O15Child.ordinal();
				int colRelatives = HouseholdRelationship.Relative.ordinal();
				int colNonRelatives = HouseholdRelationship.NonRelative
						.ordinal();

				// get the total number of married and defacto persons
				int nppCouple = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
						hhRelMale, colMarried))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelMale, colDeFacto))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colMarried))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colDeFacto));

				// get the number of lone parents
				int nppLoneParent = arrHdlr.sumPositiveInArray(arrHdlr
						.getColumn(hhRelMale, colLoneParent))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colLoneParent));
				// get the number of children < 15
				int nppU15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
						hhRelMale, colU15))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colU15));
				// get the number of students
				int nppStu = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
						hhRelMale, colStu))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colStu));
				// get the number of children > 15
				int nppO15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
						hhRelMale, colO15))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colO15));
				// get the total number of relatives and non-relatives
				int nppRelatives = arrHdlr.sumPositiveInArray(arrHdlr
						.getColumn(hhRelMale, colRelatives))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelMale, colRelatives))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colNonRelatives))
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colNonRelatives));

				int totFamiPeople = nppCouple + nppLoneParent + nppU15 + nppStu
						+ nppO15 + nppRelatives;
				int dpp = testMinNumPersonsInHhold(nHF, minResidents,
						totFamiPeople);
				if (dpp < 0){
					int hhRelMaleIndex=0;
					int hhRelFemaleIndex=1;
					hhRelMale = correctRelativesInHholdRel(hhRelMale, hhRelFemale,
							nppRelatives - dpp).get(hhRelMaleIndex);
					hhRelFemale = correctRelativesInHholdRel(hhRelMale, hhRelFemale,
							nppRelatives - dpp).get(hhRelFemaleIndex);
				}
				
				nppInHF = correctNumPersonsInFamilyHhold1(nHF, minResidents,
						hhRelMale, hhRelFemale, nppInHF);

			}
			// logger.info("\tStep 4 done!");
			// --------------------------------------------------------------------------------------------------------------------

			// 5. adjust number of non-family households (table B30)
			// ====================================================================================================================
			// int[][] hhComp = null;
			if (!badCD)
				hhComp = adjustNumberOfNFHhold(hhRelMale, hhRelFemale, hhComp);

			// testAssumption15();
			// displayDataHholdRel(hhRelMale, hhRelFemale);
			// testB22aAndB24Consistency();
			// testB22aAndB25Consistency();
			// testB24AndB25Consistency();
			// if (!badCD)
			// testTableCorrections(cdName, "testTableCorrections.csv", true);

			// String[][] hhCompStr = new String[1][rawhhComp.length+1];
			// hhCompStr[0][0] = cd.toString();
			// if (hhComp!=null) {
			//
			// hhCompStr[0][1] = Integer.toString(hhComp[0][2]);
			// for (int i=1; i<=hhComp.length-1; i++)
			// hhCompStr[0][i+1] = Integer.toString(hhComp[i][1] +
			// hhComp[i][2]);
			// } else {
			// hhCompStr[0][1] = Integer.toString(rawhhComp[0][2]);
			// for (int i=1; i<=rawhhComp.length-1; i++)
			// hhCompStr[0][i+1] = Integer.toString(rawhhComp[i][1] +
			// rawhhComp[i][2]);
			// }
			// (new TextFileHandler()).writeToCSV("hhComp.csv", hhCompStr,
			// true);
			// logger.debug("\tStep 5 done!");
			// --------------------------------------------------------------------------------------------------------------------

			// 6. allocate number of people to each family household as well as
			// each non family household
			// use finnHF and newnppInHF determined in step 3
			// each record of nppInEachHhold = [hhIdx minpp nhh nM nF ntot]
			// (hhIdx is index in min_individuals_note)
			// ====================================================================================================================
			// fields of nppInEachHhold are:
			// [0]index in min_individuals_note,
			// [1] number of parents,
			// [2] min number of U15,
			// [3] min number of Stu,
			// [4] min number of O15,
			// [5] min number of Rel,
			// [6] min number of NonFamily,
			// [7] nM,
			// [8] nF
			int[][] nppInEachHhold = null;
			if (!badCD)
				nppInEachHhold = this.allocateNumPersonsToHhold(nHF, hhComp,
						nppInHF, minResidents, hhRelMale, hhRelFemale);

			// logger.debug("\tStep 6 done!");
			// --------------------------------------------------------------------------------------------------------------------

			// 7. create a pool for individuals. Fields in a record [id age
			// gender hhRel hhType income]
			// use nppInEachHhold determined in step 6
			// ====================================================================================================================
			// fields of indivPool are:
			// [0] age,
			// [1] gender (0-female or 1-male),
			// [2] index in hhold_relationship
			int[][] indivPool = createIndivPool(minResidents, hhRelMale,
					hhRelFemale, random);

			// logger.debug("\tStep 7 done!");
			// --------------------------------------------------------------------------------------------------------------------

			// this.displayDataHholdRel(hhRelMale, hhRelFemale);
			// this.displayDatanHF(nHF);
			// this.displayDatanppInHF(nppInHF);

			// 8a. allocate minimum number of individuals into households
			// ====================================================================================================================
			// fields of indivRecords are
			// [0] age,
			// [1] gender (0-female or 1-male),
			// [2] index in hhold_relationship
			// [3] index of hhold in this travel zone
			// [4] index in min_individuals_note (ie index of household
			// category)
			indivRecords = null;
			if (nppInEachHhold != null & indivPool != null) {
				Map<Integer, ArrayList<int[]>> tmpIndivRecords = this
						.allocateIndivToHhold2(indivPool, nppInEachHhold,
								cdName, random);
				int nTotIndivs = 0;
				for (ArrayList<int[]> tmpHhold : tmpIndivRecords.values()) {
					nTotIndivs += tmpHhold.size();
				}
				indivRecords = new int[nTotIndivs][tmpIndivRecords.get(0)
						.get(0).length];
				int tmpCount = -1;
				for (ArrayList<int[]> tmpHhold : tmpIndivRecords.values()) {
					for (int iRes = 0; iRes <= tmpHhold.size() - 1; iRes++) {
						tmpCount += 1;
						indivRecords[tmpCount] = tmpHhold.get(iRes);
					}
				}
				TextFileHandler.writeToCSV(parentFolder + cdName + ".csv", indivRecords, true);
			}

			// 9. allocates income to individuals
			// ====================================================================================================================
			int[][] indivRecordsWithIncome = this.allocateIncome(indivRecords,
					incomeMale, incomeFemale, random);
			// --------------------------------------------------------------------------------------------------------------------

			// 10. saves indivRecords and hhold indices
			// ====================================================================================================================
			// logger.debug("\nFINAL POPULATION:");
			MinIndividualsNote[] minIndivs = MinIndividualsNote.values();
			HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();
			String[][] indivRecStr = new String[indivRecordsWithIncome.length][indivRecordsWithIncome[0].length + 1];
			for (int i = 0; i <= indivRecordsWithIncome.length - 1; i++) {

				indivRecStr[i][0] = Integer
						.toString(indivRecordsWithIncome[i][0]);
				indivRecStr[i][1] = Integer
						.toString(indivRecordsWithIncome[i][1]);
				indivRecStr[i][2] = hhrel8[indivRecordsWithIncome[i][2]]
						.toString();
				indivRecStr[i][3] = Integer
						.toString(indivRecordsWithIncome[i][3]);
				indivRecStr[i][4] = minIndivs[indivRecordsWithIncome[i][4]]
						.toString();
				indivRecStr[i][5] = Integer
						.toString(indivRecordsWithIncome[i][5]);
				indivRecStr[i][6] = cd.toString();
				
				indivRecordsStr.add(indivRecStr[i]);
			}

			int[] uHholdIdx = arrHdlr.getUnique(arrHdlr.getColumn(
					indivRecordsWithIncome, 3));
			String[][] uhhIdxStr = new String[uHholdIdx.length][3];
			for (int i = 0; i <= uHholdIdx.length - 1; i++) {
				uhhIdxStr[i][0] = Integer.toString(uHholdIdx[i]);
				uhhIdxStr[i][1] = cd.toString();
				int npp = nppInEachHhold[uHholdIdx[i]][7]
						+ nppInEachHhold[uHholdIdx[i]][8];
				uhhIdxStr[i][2] = Integer.toString(npp);
				
				hholdRecordsStr.add(uhhIdxStr[i]);
			}
			// --------------------------------------------------------------------------------------------------------------------

			// verifySyntheticPopulation(verificationFile, cdName);

			/*
			 * if (!badCD) { allO_hhRelMale =
			 * arrHdlr.addPositiveIn2DArrays(allO_hhRelMale, hhRelMale);
			 * allO_hhRelFemale =
			 * arrHdlr.addPositiveIn2DArrays(allO_hhRelFemale, hhRelFemale);
			 * allO_nHF = arrHdlr.addPositiveIn2DArrays(allO_nHF,nHF);
			 * allO_nppInHF =
			 * arrHdlr.addPositiveIn2DArrays(allO_nppInHF,nppInHF);
			 * 
			 * allC_hhRelMale = arrHdlr.addPositiveIn2DArrays(allC_hhRelMale,
			 * newhhRelMale); allC_hhRelFemale =
			 * arrHdlr.addPositiveIn2DArrays(allC_hhRelFemale, newhhRelFemale);
			 * allC_nHF = arrHdlr.addPositiveIn2DArrays(allC_nHF,finnHF);
			 * allC_nppInHF =
			 * arrHdlr.addPositiveIn2DArrays(allC_nppInHF,newnppInHF);
			 * 
			 * allC_hhComp = arrHdlr.addPositiveIn2DArrays(allC_hhComp, hhComp);
			 * all_ageSex = arrHdlr.addPositiveIn2DArrays(all_ageSex, ageSex);
			 * 
			 * // restore first column(s) allO_hhRelMale =
			 * arrHdlr.replaceColumn(
			 * allO_hhRelMale,household_relationship.id.ordinal(),
			 * arrHdlr.getColumn(hhRelMale,
			 * household_relationship.id.ordinal())); allO_hhRelMale =
			 * arrHdlr.replaceColumn
			 * (allO_hhRelMale,household_relationship.year_1.ordinal(),
			 * arrHdlr.getColumn
			 * (hhRelMale,household_relationship.year_1.ordinal()));
			 * allO_hhRelMale =
			 * arrHdlr.replaceColumn(allO_hhRelMale,household_relationship
			 * .year_2.ordinal(),
			 * arrHdlr.getColumn(hhRelMale,household_relationship
			 * .year_2.ordinal())); allO_hhRelFemale =
			 * arrHdlr.replaceColumn(allO_hhRelFemale
			 * ,household_relationship.id.ordinal(),
			 * arrHdlr.getColumn(hhRelFemale,
			 * household_relationship.id.ordinal())); allO_hhRelFemale =
			 * arrHdlr.
			 * replaceColumn(allO_hhRelFemale,household_relationship.year_1
			 * .ordinal(),
			 * arrHdlr.getColumn(hhRelFemale,household_relationship.year_1
			 * .ordinal())); allO_hhRelFemale =
			 * arrHdlr.replaceColumn(allO_hhRelFemale
			 * ,household_relationship.year_2.ordinal(),
			 * arrHdlr.getColumn(hhRelFemale
			 * ,household_relationship.year_2.ordinal())); allO_nHF =
			 * arrHdlr.replaceColumn
			 * (allO_nHF,family_composition.id.ordinal(),arrHdlr.getColumn(nHF,
			 * family_composition.id.ordinal())); allO_nppInHF =
			 * arrHdlr.replaceColumn
			 * (allO_nppInHF,family_composition_bysex.id.ordinal
			 * (),arrHdlr.getColumn(nppInHF,
			 * family_composition_bysex.id.ordinal()));
			 * 
			 * // restore first column(s) allC_hhRelMale =
			 * arrHdlr.replaceColumn(
			 * allC_hhRelMale,household_relationship.id.ordinal(),
			 * arrHdlr.getColumn(hhRelMale,
			 * household_relationship.id.ordinal())); allC_hhRelMale =
			 * arrHdlr.replaceColumn
			 * (allC_hhRelMale,household_relationship.year_1.ordinal(),
			 * arrHdlr.getColumn
			 * (hhRelMale,household_relationship.year_1.ordinal()));
			 * allC_hhRelMale =
			 * arrHdlr.replaceColumn(allC_hhRelMale,household_relationship
			 * .year_2.ordinal(),
			 * arrHdlr.getColumn(hhRelMale,household_relationship
			 * .year_2.ordinal())); allC_hhRelFemale =
			 * arrHdlr.replaceColumn(allC_hhRelFemale
			 * ,household_relationship.id.ordinal(),
			 * arrHdlr.getColumn(hhRelFemale,
			 * household_relationship.id.ordinal())); allC_hhRelFemale =
			 * arrHdlr.
			 * replaceColumn(allC_hhRelFemale,household_relationship.year_1
			 * .ordinal(),
			 * arrHdlr.getColumn(hhRelFemale,household_relationship.year_1
			 * .ordinal())); allC_hhRelFemale =
			 * arrHdlr.replaceColumn(allC_hhRelFemale
			 * ,household_relationship.year_2.ordinal(),
			 * arrHdlr.getColumn(hhRelFemale
			 * ,household_relationship.year_2.ordinal())); allC_nHF =
			 * arrHdlr.replaceColumn
			 * (allC_nHF,family_composition.id.ordinal(),arrHdlr.getColumn(nHF,
			 * family_composition.id.ordinal())); allC_nppInHF =
			 * arrHdlr.replaceColumn(allC_nppInHF,family_composition_bysex.i
			 * d.ordinal(),arrHdlr.getColumn(nppInHF,
			 * family_composition_bysex.id.ordinal()));
			 * 
			 * // restore first column(s) allC_hhComp =
			 * arrHdlr.replaceColumn(allC_hhComp
			 * ,household_composition.id.ordinal(),arrHdlr.getColumn(hhComp,
			 * household_composition.id.ordinal()));
			 * 
			 * // restore first column(s) all_ageSex =
			 * arrHdlr.replaceColumn(all_ageSex, population.year_1.ordinal(),
			 * arrHdlr.getColumn(ageSex, population.year_1.ordinal()));
			 * all_ageSex = arrHdlr.replaceColumn(all_ageSex,
			 * population.year_2.ordinal(), arrHdlr.getColumn(ageSex,
			 * population.year_2.ordinal())); }
			 */
		}

		// chi-squared test
		// ====================================================================================================================
		// this.chiSquaredTest(allO_hhRelMale, allO_hhRelFemale, allC_hhRelMale,
		// allC_hhRelFemale, allO_nHF, allC_nHF, allO_nppInHF, allC_nppInHF);
		// --------------------------------------------------------------------------------------------------------------------

		// z test
		// ====================================================================================================================
		// this.zTest(allO_hhRelMale, allO_hhRelFemale, allC_hhRelMale,
		// allC_hhRelFemale, allO_nHF, allC_nHF, allO_nppInHF, allC_nppInHF);
		// --------------------------------------------------------------------------------------------------------------------

		// comparing with HTS data
		// ====================================================================================================================
		// this.compareWithHTS(all_ageSex, allC_nHF, allC_hhComp);
		// --------------------------------------------------------------------------------------------------------------------

		// displaying tables
		// ====================================================================================================================
		// this.displayTables(allO_hhRelMale, allO_hhRelFemale, allC_hhRelMale,
		// allC_hhRelFemale, allO_nHF, allC_nHF, allO_nppInHF,
		// allC_nppInHF, all_ageSex, allC_hhComp);
		// --------------------------------------------------------------------------------------------------------------------
	}

	/**
	 * counts the number of people in each family household category in the
	 * household pool. These counts are then compared with the household
	 * composition by sex, i.e. nppInHF This method should normally be called
	 * after method allocateNumPersonsToHhold
	 * 
	 * @param nppInEachHhold
	 *            the household pool, having the following column [0] index in
	 *            min_individuals_note, [1] number of parents, [2] min number of
	 *            U15, [3] min number of Stu, [4] min number of O15, [5] min
	 *            number of Rel, [6] min number of NonFamily, [7] nM, [8] nF
	 */
	private int[] verifyNumberOfPeopleAllocatedToHholds(int[][] nppInEachHhold) {
		int[] hhCounts = new int[MinIndividualsNote.values().length];
		for (int ihh = 0; ihh <= nppInEachHhold.length - 1; ihh++){
			hhCounts[nppInEachHhold[ihh][0]] = hhCounts[nppInEachHhold[ihh][0]]
					+ nppInEachHhold[ihh][7] + nppInEachHhold[ihh][8];
		}
		return hhCounts;
	}

	/**
	 * calculates and writes to a csv file the following: - the number of people
	 * in the synthetic population - the number of people from the (corrected)
	 * table B22a - the number of people living in family households in the
	 * synthetic population - the number of people living in family households
	 * from the (corrected) table B22a - the number of people living in
	 * non-family households in the synthetic population - the number of people
	 * living in non-family households from the (corrected) table B22a
	 * 
	 * @param filename
	 *            the path and name of file where the verification outputs will
	 *            be written to.
	 * @param cdName
	 *            ID of the current census district.
	 */
	private void verifySyntheticPopulation(String filename, String cdName) {
		// compare indivReocrds against number of males and females in B22
		ArrayHandler arrHdlr = new ArrayHandler();

		// logger.debug("\nverifySyntheticPopulation");

		int colMarried = HouseholdRelationship.Married.ordinal();
		int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
		int colLoneParent = HouseholdRelationship.LoneParent.ordinal();
		int colU15 = HouseholdRelationship.U15Child.ordinal();
		int colStu = HouseholdRelationship.Student.ordinal();
		int colO15 = HouseholdRelationship.O15Child.ordinal();
		int colRelatives = HouseholdRelationship.Relative.ordinal();
		int colNonRelatives = HouseholdRelationship.NonRelative.ordinal();
		int colGroupHhold = HouseholdRelationship.GroupHhold.ordinal();
		int colLonePerson = HouseholdRelationship.LonePerson.ordinal();

		// calculates the number of people living in HF households from the
		// corrected B22a
		int nMaleHF_B22a = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelMale, colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colO15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colNonRelatives));
		int nFemaleHF_B22a = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelFemale, colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colO15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colNonRelatives));
		int nTotalHF_B22a = nMaleHF_B22a + nFemaleHF_B22a;

		// calculates the number of people living in NF households from the
		// corrected B22a
		int nMaleNF_B22a = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelMale, colGroupHhold))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colLonePerson));
		int nFemaleNF_B22a = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelFemale, colGroupHhold))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colLonePerson));
		int nTotalNF_B22a = nMaleNF_B22a + nFemaleNF_B22a;

		// calculates the total number of people from the (corrected) table B22a
		int nTotalB22a = nTotalNF_B22a + nTotalHF_B22a;

		// calculates the number of people living in family households
		// and non-family households in the synthetic population
		int nLivingInHF_SP = 0;
		int nLivingInNF_SP = 0;
		for (int i = 0; i <= indivRecords.length - 1; i++) {
			if (indivRecords[i][4] == 0) {
				// if this indiviudal belongs to a NF household
				nLivingInNF_SP += 1;
			}
			else {
				// this indiviudal belongs to an HF household
				nLivingInHF_SP += 1;
			}
		}

		double totPopPercent = (double) Math.abs(indivRecords.length
				- nTotalB22a)
				/ (double) nTotalB22a * 100;
		double totHFPercent = (double) Math.abs(nLivingInHF_SP - nTotalHF_B22a)
				/ (double) nTotalHF_B22a * 100;
		double totNFPercent = (double) Math.abs(nLivingInNF_SP - nTotalNF_B22a)
				/ (double) nTotalNF_B22a * 100;
		// String[][] results = {{Integer.toString(indivRecords.length),
		// Integer.toString(nTotalB22a),
		// Double.toString(totPopPercent),
		// Integer.toString(nLivingInHF_SP),
		// Integer.toString(nTotalHF_B22a),
		// Double.toString(totHFPercent),
		// Integer.toString(nLivingInNF_SP),
		// Integer.toString(nTotalNF_B22a),
		// Double.toString(totNFPercent)}};

		String results = cdName + "," + Integer.toString(indivRecords.length)
				+ "," + Integer.toString(nTotalB22a) + ","
				+ Double.toString(totPopPercent) + ","
				+ Integer.toString(nLivingInHF_SP) + ","
				+ Integer.toString(nTotalHF_B22a) + ","
				+ Double.toString(totHFPercent) + ","
				+ Integer.toString(nLivingInNF_SP) + ","
				+ Integer.toString(nTotalNF_B22a) + ","
				+ Double.toString(totNFPercent);

		TextFileHandler.writeToText(filename, results, true);

	}

	/**
	 * tests if the sum of U15Child + Students + O15Child + LoneParent + coupled
	 * parents is greater than or equal to the total number of people in
	 * households HF1 to HF15 (as specified in table B25). In other words, this
	 * is to ensure that the number of residents allocated to households
	 * HF2-HF15 when creating household pool is large enough to contain the
	 * number of individuals (from the individual pool) that rightfully live in
	 * these households.
	 * 
	 * (Assumption 15: the total number of ���U15Child���, ���Student���, ���O15Child���,
	 * ���LoneParent���, ���Married���, ���DeFacto��� individuals (table B22a) must be
	 * smaller than or equal to the number of residents allocated to households
	 * HF1-HF15.)
	 */
	private void testAssumption15() {
		ArrayHandler arrHdlr = new ArrayHandler();

		int colMarried = HouseholdRelationship.Married.ordinal();
		int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
		int colLoneParent = HouseholdRelationship.LoneParent.ordinal();
		int colU15 = HouseholdRelationship.U15Child.ordinal();
		int colStu = HouseholdRelationship.Student.ordinal();
		int colO15 = HouseholdRelationship.O15Child.ordinal();
		int colRelatives = HouseholdRelationship.Relative.ordinal();
		int colNonRelatives = HouseholdRelationship.NonRelative.ordinal();

		// get the total number of ���U15Child���, ���Student���, ���O15Child���,
		// ���LoneParent���, ���Married���,
		// ���DeFacto��� individuals from table B22a.
		int nppB22a = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colO15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colO15));

		// calculates the number of people allocated to households of types HF1
		// to HF15
		int nppB25 = 0;
		for (int i = 0; i <= nppInHF.length - 2; i++){
			// looping from HF1 to HF15
			nppB25 = nppB25 + nppInHF[i][1] + nppInHF[i][2];
		}
		
		// get the number of 'Relative' and 'NonRelative' individuals from B22a
		int nRelB22a = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colNonRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colNonRelatives));
		// get the number of 'Relative' and 'NonRelative' individuals from B25
		int nRelB25 = nppInHF[nppInHF.length - 1][1]
				+ nppInHF[nppInHF.length - 1][2];

		if (nppB25 < nppB22a) {
			logger.debug("nppB22a=" + nppB22a + ", nppB25=" + nppB25
					+ " - BAD");
			int dppReq = nppB22a - nppB25;
			int dppAvail = nppInHF[0][1] + nppInHF[0][2] - nHF[0][1] * 2 
									// relatives in HF1 hholds
					+ nRelB25 - nHF[nHF.length - 1][1] * 2; 
									// extra relatives in HF16 hholds
			if (dppAvail > dppReq) {
				logger.debug("\t... but fixable");
			}
			else {
				logger.debug("\tno easy fix");
			}
		} else {
			logger.debug("nppB22a=" + nppB22a + ", nppB25=" + nppB25
					+ GoodMessage);
			logger.debug("nRelB22a=" + nRelB22a + ", nRelB25=" + nRelB25);
		}
	}

	/**
	 * tests if the total number of males and and the total number of females
	 * living in family households as specified in B25 are equal to those from
	 * B22.
	 * 
	 */
	private void testB22aAndB25Consistency() {

		ArrayHandler arrHdlr = new ArrayHandler();

		int colMarried = HouseholdRelationship.Married.ordinal();
		int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
		int colLoneParent = HouseholdRelationship.LoneParent.ordinal();
		int colU15 = HouseholdRelationship.U15Child.ordinal();
		int colStu = HouseholdRelationship.Student.ordinal();
		int colO15 = HouseholdRelationship.O15Child.ordinal();
		int colRelatives = HouseholdRelationship.Relative.ordinal();
		int colNonRelatives = HouseholdRelationship.NonRelative.ordinal();

		int nMaleInHF = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colO15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colNonRelatives));
		int nFemaleInHF = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelFemale, colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colO15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colNonRelatives));

		int nMaleB25 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(nppInHF,
				FamilyCompositionBySex.males.ordinal()));
		int nFemaleB25 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(nppInHF,
				FamilyCompositionBySex.females.ordinal()));

		if (nMaleInHF == nMaleB25 && nFemaleInHF == nFemaleB25) {
			logger.debug("testB25 - OK\n");
		}
		else {
			logger.debug("testB25 - BAD!!!\n" + "\tnMaleInHF="
					+ nMaleInHF + ", nMaleB25=" + nMaleB25 + "\n"
					+ "\tnFemaleInHF=" + nFemaleInHF + ", nFemaleB25="
					+ nFemaleB25);
		}

	}

	/**
	 * tests if the number of households of each household type specified in B24
	 * is enough to accommodate the number of males and females of the
	 * corresponding household type as specified in B25.
	 */
	private void testB24AndB25Consistency() {
		ArrayHandler arrHdlr = new ArrayHandler();
		logger.debug("test B25 versus minimum residents ");
		for (FamilyCompositionNote fcn : FamilyCompositionNote.values()) {
			int iminRes = ArrayUtils.indexOf(arrHdlr.getColumn(minResidents,
							MinIndividuals.index.ordinal()), fcn.ordinal() + 2);
			int inHF = ArrayUtils.indexOf(
					arrHdlr.getColumn(nHF, FamilyComposition.id.ordinal()),
					fcn.ordinal() + 1);
			int minPeople = nHF[inHF][FamilyComposition.families.ordinal()]
					* minResidents[iminRes][MinIndividuals.min_residents
							.ordinal()];
			int totMF = nppInHF[inHF][FamilyCompositionBySex.males.ordinal()]
					+ nppInHF[inHF][FamilyCompositionBySex.females.ordinal()];
			
			if (totMF >= minPeople) {
				logger.debug("\tminResidents=" + minResidents[0][0]
						+ ", totMF=" + totMF + GoodMessage);
			}
			else {
				logger.debug("\tminResidents=" + minResidents[0][0]
						+ ", totMF=" + totMF + BadMessage);
			}
		}
	}

	/**
	 * tests if the number of households of each household type specified in B24
	 * is enough to accommodate the number of people of household relationships
	 * as specified in table B22a.
	 */
	private void testB22aAndB24Consistency() {
		ArrayHandler arrHdlr = new ArrayHandler();

		int colMarried = HouseholdRelationship.Married.ordinal();
		int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
		int colLoneParent = HouseholdRelationship.LoneParent.ordinal();
		int colU15 = HouseholdRelationship.U15Child.ordinal();
		int colStu = HouseholdRelationship.Student.ordinal();
		int colO15 = HouseholdRelationship.O15Child.ordinal();
		int colRelatives = HouseholdRelationship.Relative.ordinal();
		int colNonRelatives = HouseholdRelationship.NonRelative.ordinal();

		// get the total number of married and defacto persons
		int nppCouple = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colDeFacto));
		// get the number of lone parents
		int nppLoneParent = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelMale, colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colLoneParent));
		// get the number of children < 15
		int nppU15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colU15));
		// get the number of students
		int nppStu = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colStu));
		// get the number of children > 15
		int nppO15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colO15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colO15));
		// get the total number of Relatives and NonRelatives
		int nppRel = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colNonRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colNonRelatives));

		int nFami2P = 0;
		for (int i = FamilyCompositionNote.HF1.ordinal(); i <= FamilyCompositionNote.HF8
				.ordinal(); i++) {
			nFami2P = nFami2P + nHF[i][1];
		}
		
		int nFami1P = 0;
		for (int i = FamilyCompositionNote.HF9.ordinal(); i <= FamilyCompositionNote.HF15
				.ordinal(); i++) {
			nFami1P = nFami1P + nHF[i][1];
		}
		
		int nFamiU15 = nHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF5.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF12.ordinal()][1];

		int nFamiStu = nHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF7.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF14.ordinal()][1];

		int nFamiO15 = nHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF8.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF15.ordinal()][1];

		int nFamiRelOnly = nHF[FamilyCompositionNote.HF16.ordinal()][1];

		if (nFami2P != nppCouple / 2) {
			logger.debug("nFami2P = " + nFami2P + ", nppCouple = "
					+ nppCouple + BadMessage);
		}
		else {
			logger.debug("nFami2P = " + nFami2P + ", nppCouple = "
					+ nppCouple + GoodMessage);
		}
		
		if (nFami1P != nppLoneParent) {
			logger.debug("nFami1P = " + nFami1P + ", nppLoneParent = "
					+ nppLoneParent + BadMessage);
		}
		else {
			logger.debug("nFami1P = " + nFami1P + ", nppLoneParent = "
					+ nppLoneParent + GoodMessage);
		}
		
		if (nFamiU15 > nppU15) {
			logger.debug("nFamiU15 = " + nFamiU15 + ", nppU15 = "
					+ nppU15 + BadMessage);
		}
		else {
			logger.debug("nFamiU15 = " + nFamiU15 + ", nppU15 = "
					+ nppU15 + GoodMessage);
		}
		
		if (nFamiStu > nppStu) {
			logger.debug("nFamiStu = " + nFamiStu + ", nppStu = "
					+ nppStu + BadMessage);
		}
		else {
			logger.debug("nFamiStu = " + nFamiStu + ", nppStu = "
					+ nppStu + GoodMessage);
		}
		
		if (nFamiO15 > nppO15) {
			logger.debug("nFamiO15 = " + nFamiO15 + ", nppO15 = "
					+ nppO15 + BadMessage);
		}
		else {
			logger.debug("nFamiO15 = " + nFamiO15 + ", nppO15 = "
					+ nppO15 + GoodMessage);
		}
		
		if (nFamiRelOnly > nppO15 / 2) {
			logger.debug("nFamiRelOnly = " + nFamiRelOnly + ", nppRel = "
					+ nppRel + BadMessage);
		}
		else {
			logger.debug("nFamiRelOnly = " + nFamiRelOnly + ", nppRel = "
					+ nppRel + GoodMessage);
		}
	}

	/**
	 * allocates income to individuals (after having allocated them to
	 * households)
	 * 
	 * @param indivRecords
	 *            2D integer array, each record represents 1 individual. Columns
	 *            are [age, gender, index of household relationship, index of
	 *            household (in nppInHhold), hhold category index]
	 * @param incomeM
	 *            array of number of males by income groups by age groups
	 *            (should be from the original ABS table B16)
	 * @param incomeF
	 *            array of number of females by income groups by age groups
	 *            (should be from the original ABS table B16)
	 * @return a 2D integer array, each record represents 1 individual. Columns
	 *         are [age, gender, index of household relationship, index of
	 *         household (in nppInHhold), hhold category index, income]
	 */
	private int[][] allocateIncome(int[][] indivRecords, int[][] incomeM,
			int[][] incomeF, Random random) {
		int[][] newIndivRec = new int[indivRecords.length][indivRecords[0].length + 1];
		for (int i = 0; i <= indivRecords.length - 1; i++) {
			for (int j = 0; j <= indivRecords[0].length - 1; j++) {
				newIndivRec[i][j] = indivRecords[i][j];
			}
		}
		ArrayHandler arrHdlr = new ArrayHandler();
		Income[] inc = Income.values();
		// min_individuals_note[] minIndivs = min_individuals_note.values();
		// household_relationship[] hhrel8 = household_relationship.values();

		// males
		for (int incM = 0; incM <= incomeM.length - 1; incM++) {
			int year_1 = incomeM[incM][Income.year_1.ordinal()];
			int year_2 = incomeM[incM][Income.year_2.ordinal()];
			int[] idxIndivThisAge = this.getIndivInAgeGroup(indivRecords,
					year_1, year_2, 1);
			int[] nppIncThisAge = arrHdlr.extractArray(incomeM[incM],
					Income.zero.ordinal(), Income.$2000_$10000.ordinal());
			if (arrHdlr.sumPositiveInArray(nppIncThisAge) > 0
					&& idxIndivThisAge != null) {
				int[] nppIncThisAgeCor = arrHdlr.allocateProportionally(
						nppIncThisAge, idxIndivThisAge.length);
				for (int itac = 0; itac <= nppIncThisAgeCor.length - 1; itac++) {
					if (nppIncThisAgeCor[itac] <= 0) {
						continue;
					}
					
					int[] idxPickedInd = arrHdlr.pickRandomFromArray(
							idxIndivThisAge, null, nppIncThisAgeCor[itac],
							random);
					for (int iPicked = 0; iPicked <= idxPickedInd.length - 1; iPicked++) {
						newIndivRec[idxPickedInd[iPicked]][indivRecords[0].length] = inc[itac + 3].val;
						idxIndivThisAge = ArrayUtils.remove(idxIndivThisAge, ArrayUtils.indexOf(idxIndivThisAge,
												idxPickedInd[iPicked]));
					}
				}
			}
		}
		// females
		for (int incF = 0; incF <= incomeF.length - 1; incF++) {
			int year1 = incomeF[incF][Income.year_1.ordinal()];
			int year2 = incomeF[incF][Income.year_2.ordinal()];
			int[] idxIndivThisAge = this.getIndivInAgeGroup(indivRecords,
					year1, year2, 0);
			int[] nppIncThisAge = arrHdlr.extractArray(incomeF[incF],
					Income.zero.ordinal(), Income.$2000_$10000.ordinal());
			if (arrHdlr.sumPositiveInArray(nppIncThisAge) > 0
					&& idxIndivThisAge != null) {
				int[] nppIncThisAgeCor = arrHdlr.allocateProportionally(
						nppIncThisAge, idxIndivThisAge.length);
				for (int itac = 0; itac <= nppIncThisAgeCor.length - 1; itac++) {
					if (nppIncThisAgeCor[itac] <= 0) {
						continue;
					}
					int[] idxPickedInd = arrHdlr.pickRandomFromArray(
							idxIndivThisAge, null, nppIncThisAgeCor[itac],
							random);
					for (int iPicked = 0; iPicked <= idxPickedInd.length - 1; iPicked++) {
						newIndivRec[idxPickedInd[iPicked]][indivRecords[0].length] = inc[itac + 3].val;
						idxIndivThisAge = ArrayUtils.remove(idxIndivThisAge, ArrayUtils.indexOf(idxIndivThisAge,
												idxPickedInd[iPicked]));
					}
				}
			}
		}

		return newIndivRec;
	}

	/**
	 * get indices of male or female individuals in the final synthetic
	 * population that have age inclusively within a specified range.
	 * 
	 * @param indivRecords
	 *            2D integer array, each record represents 1 individual. Columns
	 *            are [age, gender, index of household relationship, index of
	 *            household (in nppInHhold), hhold category index]
	 * @param year1
	 *            lower bound of age range.
	 * @param year2
	 *            upper bound of age range
	 * @param gender
	 *            1 for male, 0 for female
	 * @return indices of male or female individuals in indivRecords that have
	 *         age inclusively within the range specified by year1 and year2.
	 */
	private int[] getIndivInAgeGroup(int[][] indivRecords, int year1,
			int year2, int gender) {
		int[] finIdx = null;

		for (int i = 0; i <= indivRecords.length - 1; i++)
			if (indivRecords[i][0] >= year1 && indivRecords[i][0] <= year2
					&& indivRecords[i][1] == gender)
				finIdx = ArrayUtils.add(finIdx, i);
		return finIdx;
	}

	/**
	 * allocates individuals from individual pool to households in household
	 * pool to satisfy the minimum number of residents.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship]
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [0]index in
	 *            min_individuals_note, [1] number of parents, [2] min number of
	 *            U15, [3] min number of Stu, [4] min number of O15, [5] min
	 *            number of Rel, [6] min number of NonFamily, [7] nM, [8] nF
	 * @return a hashmap of which key is index index of a household (in
	 *         nppInHhold) and value is an ArrayList of integer arrays
	 *         representing individuals in the household. Columns of the integer
	 *         arrays are [age, gender, index of household relationship, index
	 *         of household (in nppInHhold), hhold category index]
	 */
	private Map<Integer, ArrayList<int[]>> allocateIndivToHhold2(
			int[][] indivPool, int[][] nppInHhold, String cdName, Random random) {

		HashMap<Integer, ArrayList<int[]>> hmIndivRecords = new HashMap<Integer, ArrayList<int[]>>();
		int[][] tempIndivPool = indivPool.clone();
		
		// married or defacto parents
		pfIdx = this.getIdxOfParents(tempIndivPool, 0);
		pmIdx = this.getIdxOfParents(tempIndivPool, 1);
		// lone parents
		p1fIdx = this.getIdxOfSingleParents(tempIndivPool, 0);
		p1mIdx = this.getIdxOfSingleParents(tempIndivPool, 1);
		// create arrays of index of male and female U15 children
		u15Idx = this.getIdxOfU15(tempIndivPool);
		// create arrays of index of male and female Student children
		stuIdx = this.getIdxOfStu(tempIndivPool);
		// create arrays of index of male and female O15 children
		o15Idx = this.getIdxOfO15(tempIndivPool);
		// create arrays of index of male and female relatives
		frelIdx = this.getIdxOfRel(tempIndivPool, 0);
		mrelIdx = this.getIdxOfRel(tempIndivPool, 1);
		// create arrays of index of GroupHousehold people in non-family
		// households
		fghIdx = this.getIdxOfGH(tempIndivPool, 0);
		mghIdx = this.getIdxOfGH(tempIndivPool, 1);
		// create arrays of index of GroupHousehold people in non-family
		// households
		lonePersonIdx = this.getIdxOfLP(tempIndivPool);

		int[][] hhLog = new int[nppInHhold.length][6];
		for (int i = 0; i <= hhLog.length - 1; i++) {
			hhLog[i][0] = nppInHhold[i][7]; // initiate number of males in
											// household i
			hhLog[i][1] = nppInHhold[i][8]; // initiate number of females in
											// household i
			// hhLog[i][2] is the smaller age of the parents in this household i
			// hhLog[i][3] is the larger age of the parents in this household i
			// hhLog[i][4] is the index of smaller age parent in tempIndivPool
			// hhLog[i][5] is the index of larger age parent in tempIndivPool
		}

		
		// generate records for couple parents
		int[][] rec2Par = generateRecordsCoupleParents(tempIndivPool, nppInHhold,
				hhLog);
		ArrayList<int[]> thisHholdRes = new ArrayList<int[]>();
		if (rec2Par != null) {
			thisHholdRes.add(rec2Par[0]);
			hmIndivRecords.put(rec2Par[0][3], thisHholdRes);
			for (int i = 1; i <= rec2Par.length - 1; i++) {
				int thisHholdIdx = rec2Par[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)) {
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else {
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(rec2Par[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}

		// generate records for lone parents
		int[][] rec1Par = generateRecordsLoneParents(tempIndivPool, nppInHhold,
				hhLog, random);
		if (rec1Par != null) {
			for (int i = 0; i <= rec1Par.length - 1; i++) {
				int thisHholdIdx = rec1Par[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)) {
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else {
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(rec1Par[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}


		// generate records for the minimum number of U15 in each hhold
		int childType = 2;
		int[][] recU15 = generateRecordsMinimumU15Children(tempIndivPool,
				nppInHhold, hhLog, childType, hmIndivRecords, cdName, random);
		if (recU15 != null) {
			for (int i = 0; i <= recU15.length - 1; i++) {
				int thisHholdIdx = recU15[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)) {
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else {
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(recU15[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}


		childType = 3;
		int[][] recStu = generateRecordsMinimumStuChildren(tempIndivPool,
				nppInHhold, hhLog, childType, hmIndivRecords, cdName, random);
		if (recStu != null) {
			for (int i = 0; i <= recStu.length - 1; i++) {
				int thisHholdIdx = recStu[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)) {
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else {
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(recStu[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}


		childType = 4;
		int[][] recO15 = generateRecordsMinimumO15Children(tempIndivPool,
				nppInHhold, hhLog, childType, hmIndivRecords, cdName, random);
		if (recO15 != null) {
			for (int i = 0; i <= recO15.length - 1; i++) {
				int thisHholdIdx = recO15[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)) {
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else {
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(recO15[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}


		correctBigGapParentChildAge(hmIndivRecords, random);

		// generate records for the minimum number of Rel in each hhold
		// int frellen = 0, mrellen = 0;
		// if (frelIdx!=null) frellen = frelIdx.length;
		// if (mrelIdx!=null) mrellen = mrelIdx.length;
		// logger.debug("\nRel before allocation");
		// logger.debug("frelIdx=" + frellen + ", mrellen=" + mrellen);
		int indivType = 5;
		int[] usedRel = null;
		int[][] recRel = generateRecordsMinimumRelativesOrGroupHhold(tempIndivPool,
				nppInHhold, hhLog, indivType, usedRel, random);
		// frellen = 0; mrellen = 0;
		// if (frelIdx!=null) frellen = frelIdx.length;
		// if (mrelIdx!=null) mrellen = mrelIdx.length;
		// logger.debug("Rel after allocation");
		// logger.debug("frelIdx=" + frellen + ", mrellen=" + mrellen);
		if (recRel != null) {
			for (int i = 0; i <= recRel.length - 1; i++) {
				int thisHholdIdx = recRel[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)){
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else {
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(recRel[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}

		// generate records for the minimum number of GroupHhold in each hhold.
		// logger.debug("nonfamily people:\n");
		// int lplen = 0;
		// if (lonePersonIdx!=null) lplen = lonePersonIdx.length;
		// int fghlen = 0, mghlen = 0;
		// if (fghIdx!=null) fghlen = fghIdx.length;
		// if (mghIdx!=null) mghlen = mghIdx.length;
		// logger.debug("\nLP and GH before allocation");
		// logger.debug("lonePersonIdx=" + lplen + ", fghIdx=" + fghlen +
		// ", mghlen=" + mghlen);
		indivType = 6;
		int[] usedGH = null;
		int[][] recGH = generateRecordsMinimumRelativesOrGroupHhold(tempIndivPool,
				nppInHhold, hhLog, indivType, usedGH, random);
		if (recGH != null) {
			for (int i = 0; i <= recGH.length - 1; i++) {
				int thisHholdIdx = recGH[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)){
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else{
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(recGH[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}

		int[] usedLP = null;
		int[][] recLP = generateRecordsForLonePersons(tempIndivPool, nppInHhold,
				hhLog, indivType, usedLP);
		if (recLP != null) {
			for (int i = 0; i <= recLP.length - 1; i++) {
				int thisHholdIdx = recLP[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)){
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else{
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(recLP[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}

		// lplen = 0;
		// if (lonePersonIdx!=null) lplen = lonePersonIdx.length;
		// fghlen = 0; mghlen = 0;
		// if (fghIdx!=null) fghlen = fghIdx.length;
		// if (mghIdx!=null) mghlen = mghIdx.length;
		// logger.debug("\nLP and GH after allocation");
		// logger.debug("lonePersonIdx=" + lplen + ", fghIdx=" + fghlen +
		// ", mghlen=" + mghlen);

		int[][] additionalIndivRecords = allocateRemainingIndivsIntoHholds2(
				tempIndivPool, nppInHhold, hhLog, random);
		if (additionalIndivRecords != null) {
			for (int i = 0; i <= additionalIndivRecords.length - 1; i++) {
				int thisHholdIdx = additionalIndivRecords[i][3];
				if (hmIndivRecords.containsKey(thisHholdIdx)){
					thisHholdRes = hmIndivRecords.get(thisHholdIdx);
				}
				else {
					thisHholdRes = new ArrayList<int[]>();
				}
				thisHholdRes.add(additionalIndivRecords[i]);
				hmIndivRecords.put(thisHholdIdx, thisHholdRes);
			}
		}

		return hmIndivRecords;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void correctBigGapParentChildAge(
			Map<Integer, ArrayList<int[]>> hmIndivRecords, Random random) {
		
		Set set = hmIndivRecords.entrySet();
		Iterator i = set.iterator();
		
		//goes through each of the households in indivRecords
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			
			int hholdID = (int) me.getKey();
			ArrayList<int[]> existRes = (ArrayList<int[]>) me.getValue();
			// gets the age of the parents
			HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();
			ArrayList<int[]> parentsDetails = new ArrayList<int[]>();
			for (int ier = 0; ier <= existRes.size() - 1; ier++) {
				if (hhrel8[existRes.get(ier)[2]]
						.equals(HouseholdRelationship.Married)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.DeFacto)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.LoneParent)) {
					// the 2 values in each row of parentsDetails are index of
					// the parent in arraylist hhold and the parent age.
					parentsDetails.add(new int[] { ier, existRes.get(ier)[0] });
				}
			}
			int minParentAge = parentsDetails.get(0)[1];
			int iYParent = parentsDetails.get(0)[0];
			int maxParentAge = -1;
			int iOParent = -1;
			if (parentsDetails.size() == 2) {
				maxParentAge = parentsDetails.get(1)[1];
				iOParent = parentsDetails.get(1)[0];
				if (parentsDetails.get(1)[1] < minParentAge) {
					minParentAge = parentsDetails.get(1)[1];
					iYParent = parentsDetails.get(1)[0];
					maxParentAge = parentsDetails.get(0)[1];
					iOParent = parentsDetails.get(0)[0];
				}
			}

			// gets the age of children and the eldest child
			ArrayList<int[]> childrenDetails = new ArrayList<int[]>();
			int[] eldestChild = new int[2];
			for (int ier = 0; ier <= existRes.size() - 1; ier++) {
				if (hhrel8[existRes.get(ier)[2]]
						.equals(HouseholdRelationship.U15Child)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.Student)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.O15Child)) {
					// the 2 values in each row of childrenDetails are index of
					// the children in arraylist hhold and their age.
					childrenDetails
							.add(new int[] { ier, existRes.get(ier)[0] });
					if (existRes.get(ier)[0] > eldestChild[1]){
						eldestChild = new int[] { ier, existRes.get(ier)[0] };
					}
				}
			}

			// if the age difference of the younger parent and the eldest child
			// > max_dAgeYoungerParentEldestChild
			if (minParentAge - eldestChild[1] > max_dAgeYoungerParentEldestChild) {
				// new age of the younger parent = age of eldest kid +
				// random_between[min_dAgeParentChild:max_dAgeYoungerParentEldestChild];
				existRes.get(iYParent)[0] = eldestChild[1]
						+ random.nextInt(max_dAgeYoungerParentEldestChild
								- min_dAgeParentChild) + min_dAgeParentChild;
				if (existRes.get(iYParent)[0] < 18){
					existRes.get(iYParent)[0] = 18;
				}
				// ageChange = new age of the younger parent - old age of
				// younger parent
				int ageChange = minParentAge - existRes.get(iYParent)[0];
				minParentAge = existRes.get(iYParent)[0];
				if (parentsDetails.size() == 2) {
					// new age of elder parent = age of elder parent - ageChange
					existRes.get(iOParent)[0] = existRes.get(iOParent)[0]
							- ageChange;
					maxParentAge = existRes.get(iOParent)[0];
				}
				// // adjust the age of younger children in the family = max of
				// their age groups - random(0:3)
				// for (int iCh=0; iCh<=childrenDetails.size()-1; iCh++) {
				// if (childrenDetails.get(iCh)[0]==eldestChild[0]) continue;
				// existRes.get(childrenDetails.get(iCh)[0])[0] =
				// adjustToEldestInAgeGroup(childrenDetails.get(iCh)[0]) -
				// random.nextInt(4);
				// }
			}

			hmIndivRecords.put(hholdID, existRes);
		}
	}

	/**
	 * allocates individuals from individual pool to households in household
	 * pool to satisfy the minimum number of residents.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship]
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [0]index in
	 *            min_individuals_note, [1] number of parents, [2] min number of
	 *            U15, [3] min number of Stu, [4] min number of O15, [5] min
	 *            number of Rel, [6] min number of NonFamily, [7] nM, [8] nF
	 * @return a 2D integer array, each record represents 1 individual. Columns
	 *         are [age, gender, index of household relationship, index of
	 *         household (in nppInHhold), hhold category index]
	 */
	private int[][] allocateIndivToHhold(int[][] indivPool, int[][] nppInHhold,
			Random random) {
		int[][] indivRecords = null;
		ArrayHandler arrHdlr = new ArrayHandler();
		int[][] tempIndvPool = indivPool.clone();
		
		// - create arrays of index (in indivPool) of male parents and female
		// parents
		pfIdx = this.getIdxOfParents(tempIndvPool, 0);
		pmIdx = this.getIdxOfParents(tempIndvPool, 1);
		// - create arrays of index (in tempIndvPool) of single male parents and
		// single female parents
		p1fIdx = this.getIdxOfSingleParents(tempIndvPool, 0);
		p1mIdx = this.getIdxOfSingleParents(tempIndvPool, 1);
		// - create arrays of index of male and female U15 children
		fu15Idx = this.getIdxOfU15(tempIndvPool, 0);
		mu15Idx = this.getIdxOfU15(tempIndvPool, 1);
		// u15Idx = this.getIdxOfU15(tempIndvPool);
		// - create arrays of index of male and female Student children
		fstuIdx = this.getIdxOfStu(tempIndvPool, 0);
		mstuIdx = this.getIdxOfStu(tempIndvPool, 1);
		// stuIdx = this.getIdxOfStu(tempIndvPool);
		// - create arrays of index of male and female O15 children
		fo15Idx = this.getIdxOfO15(tempIndvPool, 0);
		mo15Idx = this.getIdxOfO15(tempIndvPool, 1);
		// o15Idx = this.getIdxOfO15(tempIndvPool);
		// - create arrays of index of male and female relatives
		frelIdx = this.getIdxOfRel(tempIndvPool, 0);
		mrelIdx = this.getIdxOfRel(tempIndvPool, 1);
		// - create arrays of index of GroupHousehold people in non-family
		// households
		fghIdx = this.getIdxOfGH(tempIndvPool, 0);
		mghIdx = this.getIdxOfGH(tempIndvPool, 1);
		// - create arrays of index of GroupHousehold people in non-family
		// households
		lonePersonIdx = this.getIdxOfLP(tempIndvPool);

		int[][] hhLog = new int[nppInHhold.length][6];
		for (int i = 0; i <= hhLog.length - 1; i++) {
			hhLog[i][0] = nppInHhold[i][7]; // initiate number of males in
											// household i
			hhLog[i][1] = nppInHhold[i][8]; // initiate number of females in
											// household i
			// hhLog[i][2] is the smaller age of the parents in this household i
			// hhLog[i][3] is the larger age of the parents in this household i
			// hhLog[i][4] is the index of smaller age parent in tempIndvPool
			// hhLog[i][5] is the index of larger age parent in tempIndvPool
		}

		// generate records for couple parents
		int[][] rec2Par = generateRecordsCoupleParents(tempIndvPool, nppInHhold,
				hhLog);
		// generate records for lone parents
		int[][] rec1Par = generateRecordsLoneParents(tempIndvPool, nppInHhold,
				hhLog, random);

		// generate records for the minimum number of U15 in each hhold
		int fu15len = 0, mu15len = 0;
		if (fu15Idx != null) {
			fu15len = fu15Idx.length;
		}
		if (mu15Idx != null) {
			mu15len = mu15Idx.length;
		}
		logger.info("U15 before allocation");
        logger.info("fu15Idx=" + fu15len + ", mu15len=" + mu15len);
		int childType = 2;
		int[] usedU15 = null;
		int[][] recU15 = generateRecordsMinimumChildren(tempIndvPool, nppInHhold,
				hhLog, childType, usedU15);
		fu15len = 0;
		mu15len = 0;
		if (fu15Idx != null) {
			fu15len = fu15Idx.length;
		}
		if (mu15Idx != null) {
			mu15len = mu15Idx.length;
		}
        logger.info("U15 after allocation");
        logger.info("fu15Idx=" + fu15len + ", mu15len=" + mu15len);

		// generate records for the minimum number of Student in each hhold
		int fstulen = 0, mstulen = 0;
		if (fstuIdx != null) {
			fstulen = fstuIdx.length;
		}
		if (mstuIdx != null) {
			mstulen = mstuIdx.length;
		}
        logger.info("\nStu before allocation");
        logger.info("fstuIdx=" + fstulen + ", mstulen=" + mstulen);
		childType = 3;
		int[] usedStu = null;
		int[][] recStu = generateRecordsMinimumChildren(tempIndvPool, nppInHhold,
				hhLog, childType, usedStu);
		fstulen = 0;
		mstulen = 0;
		if (fstuIdx != null) {
			fstulen = fstuIdx.length;
		}
		if (mstuIdx != null) {
			mstulen = mstuIdx.length;
		}
        logger.info("Stu after allocation");
        logger.info("fstuIdx=" + fstulen + ", mstulen=" + mstulen);

		// generate records for the minimum number of O15 in each hhold
		int fo15len = 0, mo15len = 0;
		if (fo15Idx != null) {
			fo15len = fo15Idx.length;
		}
		if (mo15Idx != null) {
			mo15len = mo15Idx.length;
		}
        logger.info("\nO15 before allocation");
        logger.info("fo15Idx=" + fo15len + ", mo15len=" + mo15len);
		childType = 4;
		int[] usedO15 = null;
		int[][] recO15 = generateRecordsMinimumChildren(tempIndvPool, nppInHhold,
				hhLog, childType, usedO15);
		fo15len = 0;
		mo15len = 0;
		if (fo15Idx != null) {
			fo15len = fo15Idx.length;
		}
		if (mo15Idx != null){
			mo15len = mo15Idx.length;
		}
        logger.info("O15 after allocation");
		logger.debug("fo15Idx=" + fo15len + ", mo15len=" + mo15len);
		// int[][] recO15 = generateRecordsMinimumO15Children(tempIndvPool,
		// nppInHhold, hhLog, childType);

		// generate records for the minimum number of Rel in each hhold
		int frellen = 0, mrellen = 0;
		if (frelIdx != null){
			frellen = frelIdx.length;
		}
		if (mrelIdx != null) {
			mrellen = mrelIdx.length;
		}
		logger.debug("Rel before allocation");
		logger.debug("frelIdx=" + frellen + ", mrellen=" + mrellen);
		int indivType = 5;
		int[] usedRel = null;
		int[][] recRel = generateRecordsMinimumRelativesOrGroupHhold(tempIndvPool,
				nppInHhold, hhLog, indivType, usedRel, random);
		frellen = 0;
		mrellen = 0;
		if (frelIdx != null) {
			frellen = frelIdx.length;
		}
		if (mrelIdx != null) {
			mrellen = mrelIdx.length;
		}
		logger.debug("Rel after allocation");
		logger.debug("frelIdx=" + frellen + ", mrellen=" + mrellen);

		// generate records for the minimum number of GroupHhold in each hhold.
		logger.debug("nonfamily people:");
		int lplen = 0;
		if (lonePersonIdx != null) {
			lplen = lonePersonIdx.length;
		}
		int fghlen = 0, mghlen = 0;
		if (fghIdx != null) {
			fghlen = fghIdx.length;
		}
		if (mghIdx != null) {
			mghlen = mghIdx.length;
		}
		logger.debug("LP and GH before allocation");
		logger.debug("lonePersonIdx=" + lplen + ", fghIdx=" + fghlen
				+ ", mghlen=" + mghlen);
		indivType = 6;
		int[] usedGH = null;
		int[][] recGH = generateRecordsMinimumRelativesOrGroupHhold(tempIndvPool,
				nppInHhold, hhLog, indivType, usedGH, random);

		int[] usedLP = null;
		int[][] recLP = generateRecordsForLonePersons(tempIndvPool, nppInHhold,
				hhLog, indivType, usedLP);

		lplen = 0;
		if (lonePersonIdx != null) {
			lplen = lonePersonIdx.length;
		}
		fghlen = 0;
		mghlen = 0;
		if (fghIdx != null) {
			fghlen = fghIdx.length;
		}
		if (mghIdx != null) {
			mghlen = mghIdx.length;
		}
		logger.debug("LP and GH after allocation");
		logger.debug("lonePersonIdx=" + lplen + ", fghIdx=" + fghlen
				+ ", mghlen=" + mghlen);

		indivRecords = arrHdlr.concateMatrices(indivRecords, rec2Par);
		indivRecords = arrHdlr.concateMatrices(indivRecords, rec1Par);
		indivRecords = arrHdlr.concateMatrices(indivRecords, recU15);
		indivRecords = arrHdlr.concateMatrices(indivRecords, recStu);
		indivRecords = arrHdlr.concateMatrices(indivRecords, recO15);
		indivRecords = arrHdlr.concateMatrices(indivRecords, recRel);
		indivRecords = arrHdlr.concateMatrices(indivRecords, recGH);
		indivRecords = arrHdlr.concateMatrices(indivRecords, recLP);

		// allocating the remaining individuals to households
		// int[][] additionalIndivRecords =
		// allocateRemainingIndivsIntoHholds(tempIndvPool, nppInHhold, hhLog,
		// fu15Idx, mu15Idx, fstuIdx, mstuIdx, fo15Idx, mo15Idx, frelIdx,
		// mrelIdx, fnfIdx, mnfIdx);
		int[][] additionalIndivRecords = allocateRemainingIndivsIntoHholds(
				tempIndvPool, nppInHhold, hhLog, random);
		indivRecords = arrHdlr.concateMatrices(indivRecords,
				additionalIndivRecords);

		return indivRecords;
	}

	/**
	 * allocates remaining individuals from individual pool to households in
	 * household pool
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
     *
	 * @return records of the remaining individuals in invidiual pool allocated
	 *         to all households in the household pool. Columns are [age,
	 *         gender, index of household relationship, index of household (in
	 *         nppInHhold), hhold category index].
	 */
	private int[][] allocateRemainingIndivsIntoHholds2(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, Random random) {
		int[][] indivRecords = null;
		ArrayHandler arrHdlr = new ArrayHandler();

		logger.debug("allocateRemainingIndivsIntoHholds");

		// codes to allocate remaining relatives go in here
		int[][] additionalRel = allocateRemainingRelOrNF(indivPool, nppInHhold,
				hhLog, 5, random);
		indivRecords = arrHdlr.concateMatrices(indivRecords, additionalRel);

		// codes to allocate remaining non-family persons go in here
		int[][] additionalNF = allocateRemainingRelOrNF(indivPool, nppInHhold,
				hhLog, 6, random);
		indivRecords = arrHdlr.concateMatrices(indivRecords, additionalNF);

		return indivRecords;
	}

	/**
	 * allocates remaining individuals from individual pool to households in
	 * household pool
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
     *
	 * @return records of the remaining individuals in invidiual pool allocated
	 *         to all households in the household pool. Columns are [age,
	 *         gender, index of household relationship, index of household (in
	 *         nppInHhold), hhold category index].
	 */
	private int[][] allocateRemainingIndivsIntoHholds(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, Random random) {
		// private int[][] allocateRemainingIndivsIntoHholds(int[][] indivPool,
		// int[][] nppInHhold, int[][] hhLog, int[] fu15Idx, int[] mu15Idx,
		// int[] fstuIdx, int[] mstuIdx, int[] fo15Idx, int[] mo15Idx,
		// int[] frelIdx, int[] mrelIdx, int[] fnfIdx, int[] mnfIdx) {
		int[][] indivRecords = null;
		ArrayHandler arrHdlr = new ArrayHandler();

		logger.debug("allocateRemainingIndivsIntoHholds");

		int[][] additionalChildRecords = null;
		int additionalChildRecordsLength = 0;
		// for (int ic=0; ic<=2; ic++) {
		while (fu15Idx != null || mu15Idx != null || fstuIdx != null
				|| mstuIdx != null || fo15Idx != null || mo15Idx != null) {

			logger.debug("===================");
			int fu15len = 0, mu15len = 0;
			if (fu15Idx != null) {
				fu15len = fu15Idx.length;
			}
			if (mu15Idx != null) {
				mu15len = mu15Idx.length;
			}
			logger.debug("U15 before allocation");
			logger.debug("fu15Idx=" + fu15len + ", mu15len=" + mu15len);
			if (fu15Idx != null || mu15Idx != null) {
				// allocates 1 'U15Child' individual to each of the households
				// of types HF2 to HF5 and HF9 to HF12 that are not full.
				// this is similar to generating records for the minimum number
				// of U15 in each hhold
				int childType = 2;
				int[] usedU15 = null;
				int[][] recU15 = generateRecordsMinimumChildren(indivPool,
						nppInHhold, hhLog, childType, usedU15);
				// int[][] recU15 = generateRecordsMinimumChildren(indivPool,
				// nppInHhold,
				// fu15Idx, mu15Idx, hhLog, childType, usedU15);
				additionalChildRecords = arrHdlr.concateMatrices(
						additionalChildRecords, recU15);
			}
			fu15len = 0;
			mu15len = 0;
			if (fu15Idx != null) {
				fu15len = fu15Idx.length;
			}
			if (mu15Idx != null) {
				mu15len = mu15Idx.length;
			}
			logger.debug("U15 after allocation");
			logger.debug("fu15Idx=" + fu15len + ", mu15len=" + mu15len);

			int fstulen = 0, mstulen = 0;
			if (fstuIdx != null) {
				fstulen = fstuIdx.length;
			}
			if (mstuIdx != null) {
				mstulen = mstuIdx.length;
			}
			logger.debug("Stu before allocation");
			logger.debug("fstuIdx=" + fstulen + ", mstulen=" + mstulen);
			if (fstuIdx != null || mstuIdx != null) {
				// allocates 1 'Student' individual to each of the households of
				// types HF2, HF3, HF6, HF7, HF9, HF10, HF13, HF14 that are not
				// full.
				// this is similar to generating records for the minimum number
				// of Student in each hhold
				int childType = 3;
				int[] usedStu = null;
				int[][] recStu = generateRecordsMinimumChildren(indivPool,
						nppInHhold, hhLog, childType, usedStu);
				// int[][] recStu = generateRecordsMinimumChildren(indivPool,
				// nppInHhold,
				// fstuIdx, mstuIdx, hhLog, childType, usedStu);
				additionalChildRecords = arrHdlr.concateMatrices(
						additionalChildRecords, recStu);
			}
			fstulen = 0;
			mstulen = 0;
			if (fstuIdx != null) {
				fstulen = fstuIdx.length;
			}
			if (mstuIdx != null) {
				mstulen = mstuIdx.length;
			}
			logger.debug("Stu after allocation");
			logger.debug("fstuIdx=" + fstulen + ", mstulen=" + mstulen);

			int fo15len = 0, mo15len = 0;
			if (fo15Idx != null) {
				fo15len = fo15Idx.length;
			}
			if (mo15Idx != null) {
				mo15len = mo15Idx.length;
			}
			logger.debug("O15 before allocation");
			logger.debug("fo15Idx=" + fo15len + ", mo15len=" + mo15len);
			if (fo15Idx != null || mo15Idx != null) {
				// allocates 1 'O15Child' individual to each of the households
				// of types HF2, HF4, HF6, HF8, HF9, HF11, HF13, HF15 that are
				// not full.
				// this is similar to generating records for the minimum number
				// of O15 in each hhold
				int childType = 4;
				int[] usedO15 = null;
				int[][] recO15 = generateRecordsMinimumChildren(indivPool,
						nppInHhold, hhLog, childType, usedO15);
				// int[][] recO15 = generateRecordsMinimumChildren(indivPool,
				// nppInHhold,
				// fo15Idx, mo15Idx, hhLog, childType, usedO15);
				additionalChildRecords = arrHdlr.concateMatrices(
						additionalChildRecords, recO15);
			}
			fo15len = 0;
			mo15len = 0;
			if (fo15Idx != null) {
				fo15len = fo15Idx.length;
			}
			if (mo15Idx != null) {
				mo15len = mo15Idx.length;
			}
			logger.debug("O15 after allocation");
			logger.debug("fo15Idx=" + fo15len + ", mo15len=" + mo15len);

			if (additionalChildRecords != null) {
				if (additionalChildRecordsLength == additionalChildRecords.length) {
					break;
				}
				else {
					additionalChildRecordsLength = additionalChildRecords.length;
				}
			}
			else {
				break;
			}
		}
		indivRecords = arrHdlr.concateMatrices(indivRecords,
				additionalChildRecords);

		// codes to allocate remaining relatives go in here
		int[][] additionalRel = allocateRemainingRelOrNF(indivPool, nppInHhold,
				hhLog, 5, random);
		indivRecords = arrHdlr.concateMatrices(indivRecords, additionalRel);

		// codes to allocate remaining non-family persons go in here
		int[][] additionalNF = allocateRemainingRelOrNF(indivPool, nppInHhold,
				hhLog, 6, random);
		indivRecords = arrHdlr.concateMatrices(indivRecords, additionalNF);

		return indivRecords;
	}

	/**
	 * allocates the remaining non-family persons or relatives to corresponding
	 * households
	 * 
	 */
	private int[][] allocateRemainingRelOrNF(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, int indivType, Random random) {
		int[][] additionalIndiv = null;

		ArrayHandler arrHdlr = new ArrayHandler();

		int[] fppIdx = null, mppIdx = null;
		switch (indivType) {
		case 5:
			fppIdx = frelIdx;
			mppIdx = mrelIdx;
			break;
		case 6:
			fppIdx = fghIdx;
			mppIdx = mghIdx;
			break;
		default:
			break;
		}

		int frellen = 0, mrellen = 0;
		if (fppIdx != null) {
			frellen = fppIdx.length;
		}
		if (mppIdx != null) {
			mrellen = mppIdx.length;
		}
		logger.debug("Rel before allocation");
		logger.debug("frelIdx=" + frellen + ", mrellen=" + mrellen);

		int[] hhM = null;
		int[] hhF = null;
		int[] hhMF = null;
		if (indivType == 5) {
			// R1.1 gets a list of households having minimum number of relatives
			// greater than or equal to 0
			// that have nM>0 and nF==0
			for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++)
				if (nppInHhold[ihh][indivType] >= 0 && hhLog[ihh][0] > 0
						&& hhLog[ihh][1] == 0) {
					hhM = ArrayUtils.add(hhM, ihh);
				}

			// R1.2 gets a list of households having minimum number of relatives
			// greater than or equal to 0
			// that have nM==0 and nF>0
			for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
				if (nppInHhold[ihh][indivType] >= 0 && hhLog[ihh][0] == 0
						&& hhLog[ihh][1] > 0) {
					hhF = ArrayUtils.add(hhF, ihh);
				}
			}

			// R1.3 gets a list of households having minimum number of relatives
			// greater than or equal to 0
			// that have nM>0 and nF>0
			for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
				if (nppInHhold[ihh][indivType] >= 0 && hhLog[ihh][0] > 0
						&& hhLog[ihh][1] > 0) {
					hhMF = ArrayUtils.add(hhMF, ihh);
				}
			}
		} else if (indivType == 6) {
			// R1.1 gets a list of households having minimum number of relatives
			// greater than or equal to 0
			// that have nM>0 and nF==0
			for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
				if (nppInHhold[ihh][indivType] >= 2 && hhLog[ihh][0] > 0
						&& hhLog[ihh][1] == 0) {
					hhM = ArrayUtils.add(hhM, ihh);
				}
			}

			// R1.2 gets a list of households having minimum number of relatives
			// greater than or equal to 0
			// that have nM==0 and nF>0
			for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
				if (nppInHhold[ihh][indivType] >= 2 && hhLog[ihh][0] == 0
						&& hhLog[ihh][1] > 0) {
					hhF = ArrayUtils.add(hhF, ihh);
				}
			}

			// R1.3 gets a list of households having minimum number of relatives
			// greater than or equal to 0
			// that have nM>0 and nF>0
			for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
				if (nppInHhold[ihh][indivType] >= 2 && hhLog[ihh][0] > 0
						&& hhLog[ihh][1] > 0) {
					hhMF = ArrayUtils.add(hhMF, ihh);
				}
			}
		}
		int hhMLen = ArrayUtils.getLength(hhM);
		int hhFLen = ArrayUtils.getLength(hhF);
		int hhMFLen = ArrayUtils.getLength(hhMF);

		// R2.1.0 allocates male relatives into hhM households
		if (mppIdx != null) {
			int[] fullHholds = null;
			for (int ihh = 0; ihh <= hhMLen - 1; ihh++) {
				if (mppIdx == null) {
					break;
				}
				int nM = hhLog[hhM[ihh]][0];
				int[] pickedIdx = null;
				if (nM <= mppIdx.length) {
					// randomly pick nM values from mrelIdx
					pickedIdx = arrHdlr.pickRandomFromArray(mppIdx, null, nM,
							random);
					hhLog[hhM[ihh]][0] = 0; // because we will have allocated nM
											// male relatives to this households
					fullHholds = ArrayUtils.add(fullHholds, hhM[ihh]);
				} else {
					// pick all remainging male relatives
					pickedIdx = mppIdx;
					hhLog[hhM[ihh]][0] = nM - mppIdx.length;
				}
				int[][] tmpRec = new int[pickedIdx.length][indivPool[0].length + 2];
				for (int irel = 0; irel <= pickedIdx.length - 1; irel++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++) {
						tmpRec[irel][i] = indivPool[pickedIdx[irel]][i];
					}
					tmpRec[irel][indivPool[0].length] = hhM[ihh]; // hhold ID
					tmpRec[irel][indivPool[0].length + 1] = nppInHhold[hhM[ihh]][0]; // hhold
																						// type
																						// index
					mppIdx = ArrayUtils.remove(mppIdx,
							ArrayUtils.indexOf(mppIdx, pickedIdx[irel]));
				}
				additionalIndiv = arrHdlr.concateMatrices(additionalIndiv,
						tmpRec);
			}
			// remove households that are now full from hhM list
			hhM = arrHdlr.getNonCommonInArrays(hhM, fullHholds);
			hhMLen = ArrayUtils.getLength(hhM);
		}

		// at this stage there are 2 possibilities
		// R2.1.1 if no hhM households left and some male relatives remain
		if (hhM == null && mppIdx != null) {
			// allocate these remaining male relatives into hhMF households
			for (int ihh = 0; ihh <= hhMFLen - 1; ihh++) {
				if (mppIdx == null) {
					break;
				}
				int nM = hhLog[hhMF[ihh]][0];
				int[] pickedIdx = null;
				if (nM <= mppIdx.length) {
					// randomly pick nM values from mrelIdx
					pickedIdx = arrHdlr.pickRandomFromArray(mppIdx, null, nM,
							random);
					hhLog[hhMF[ihh]][0] = 0;
				} else {
					// pick all remainging male relatives
					pickedIdx = mppIdx;
					hhLog[hhMF[ihh]][0] = nM - mppIdx.length;
				}
				int[][] tmpRec = new int[pickedIdx.length][indivPool[0].length + 2];
				for (int irel = 0; irel <= pickedIdx.length - 1; irel++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++){
						tmpRec[irel][i] = indivPool[pickedIdx[irel]][i];
					}
					tmpRec[irel][indivPool[0].length] = hhMF[ihh]; // hhold ID
					tmpRec[irel][indivPool[0].length + 1] = nppInHhold[hhMF[ihh]][0]; // hhold
																						// type
																						// index
					mppIdx = ArrayUtils.remove(mppIdx,
							ArrayUtils.indexOf(mppIdx, pickedIdx[irel]));
				}
				additionalIndiv = arrHdlr.concateMatrices(additionalIndiv,
						tmpRec);
			}
		}
		// R2.1.2 else, there are hhM households remain, no male relatives left
		// -> this will be dealt with later

		// R2.2.0 allocates female relatives into hhF households
		if (fppIdx != null) {
			int[] fullHholds = null;
			for (int ihh = 0; ihh <= hhFLen - 1; ihh++) {
				if (fppIdx == null){
					break;
				}
				int nF = hhLog[hhF[ihh]][1];
				int[] pickedIdx = null;
				if (nF <= fppIdx.length) {
					// randomly pick nF values from mrelIdx
					pickedIdx = arrHdlr.pickRandomFromArray(fppIdx, null, nF,
							random);
					hhLog[hhF[ihh]][1] = 0; // because we will have allocated nF
											// female relatives to this
											// household
					fullHholds = ArrayUtils.add(fullHholds, hhF[ihh]);
				} else {
					// pick all remainging female relatives
					pickedIdx = fppIdx;
					hhLog[hhF[ihh]][1] = nF - fppIdx.length;
				}
				int[][] tmpRec = new int[pickedIdx.length][indivPool[0].length + 2];
				for (int irel = 0; irel <= pickedIdx.length - 1; irel++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++) {
						tmpRec[irel][i] = indivPool[pickedIdx[irel]][i];
					}
					tmpRec[irel][indivPool[0].length] = hhF[ihh]; // hhold ID
					tmpRec[irel][indivPool[0].length + 1] = nppInHhold[hhF[ihh]][0]; // hhold
																						// type
																						// index
					fppIdx = ArrayUtils.remove(fppIdx,
							ArrayUtils.indexOf(fppIdx, pickedIdx[irel]));
				}
				additionalIndiv = arrHdlr.concateMatrices(additionalIndiv,
						tmpRec);
			}
			// remove households that are now full from hhF list
			hhF = arrHdlr.getNonCommonInArrays(hhF, fullHholds);
//			hhFLen = ArrayUtils.getLength(hhF);
		}
		// at this stage there are 2 possibilities
		// R2.2.1 if no hhF households left and some female relatives remain
		if (hhF == null && fppIdx != null) {
			// recalculates a list of households having minimum number of
			// relatives greater than or equal to 0
			// that have nM>0 and nF>0
			hhMF = null;
			if (indivType == 5) {
				for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
					if (nppInHhold[ihh][indivType] >= 0 && hhLog[ihh][0] > 0
							&& hhLog[ihh][1] > 0) {
						hhMF = ArrayUtils.add(hhMF, ihh);
					}
				}
			} else if (indivType == 6) {
				for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++){
					if (nppInHhold[ihh][indivType] >= 2 && hhLog[ihh][0] > 0
							&& hhLog[ihh][1] > 0){
						hhMF = ArrayUtils.add(hhMF, ihh);
					}
				}
			}
			hhMFLen = ArrayUtils.getLength(hhMF);
			// allocates these remaining female relatives into hhMF households
			for (int ihh = 0; ihh <= hhMFLen - 1; ihh++) {
				if (fppIdx == null){
					break;
				}
				int nF = hhLog[hhMF[ihh]][1];
				int[] pickedIdx = null;
				if (nF <= fppIdx.length) {
					// randomly pick nF values from frelIdx
					pickedIdx = arrHdlr.pickRandomFromArray(fppIdx, null, nF,
							random);
					hhLog[hhMF[ihh]][1] = 0;
				} else {
					// pick all remaining female relatives
					pickedIdx = fppIdx;
					hhLog[hhMF[ihh]][1] = nF - fppIdx.length;
				}
				int[][] tmpRec = new int[pickedIdx.length][indivPool[0].length + 2];
				for (int irel = 0; irel <= pickedIdx.length - 1; irel++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++){
						tmpRec[irel][i] = indivPool[pickedIdx[irel]][i];
					}
					
					// hhold ID
					tmpRec[irel][indivPool[0].length] = hhMF[ihh]; 
					// hhold type index
					tmpRec[irel][indivPool[0].length + 1] = nppInHhold[hhMF[ihh]][0];
					
					fppIdx = ArrayUtils.remove(fppIdx,
							ArrayUtils.indexOf(fppIdx, pickedIdx[irel]));
				}
				additionalIndiv = arrHdlr.concateMatrices(additionalIndiv,
						tmpRec);
			}
		}
		// R2.2.2 else, there are hhF households remain, no female relatives
		// left -> this will be dealt with later

		// At this stage we have have done our best to allocate males to
		// male-only households (hhM) and females to female-only households
		// (hhF)
		// thus the next step is, for any households of types HF1-HF15 that are
		// still not full,
		// we allocate any remaining relative individuals we have left
		// (regardless male or female)
		// to fulfill these households.
		if (mppIdx != null || fppIdx != null) { 
			// if there are mrel or frel remaining
			// put all of them into a basket
			int[] relIdx = ArrayUtils.addAll(mppIdx, fppIdx);
			// putting all HF1-HF15 households that are not full into a basket
			int[] remhh = ArrayUtils.addAll(hhM, hhF);
			remhh = ArrayUtils.addAll(remhh, hhMF);
			// if there are actually some
			if (remhh != null) {
				for (int ihh = 0; ihh <= remhh.length - 1; ihh++) {
					if (relIdx == null){
						break;
					}
					int nMF = hhLog[remhh[ihh]][0] + hhLog[remhh[ihh]][1];
					int[] pickedIdx = null;
					if (nMF < relIdx.length) {
						// get nMF random values from relIdx
						pickedIdx = arrHdlr.pickRandomFromArray(relIdx, null,
								nMF, random);
						// updates the number of remaining males and females of
						// this household
						hhLog[remhh[ihh]][0] = 0;
						hhLog[remhh[ihh]][1] = 0;
					} else {
						// get all values in relIdx
						pickedIdx = relIdx;
						// updates the number of remaining males and females of
						// this household
						int[] nMFalloc = arrHdlr.allocateProportionally(
								new int[] { hhLog[remhh[ihh]][0],
										hhLog[remhh[ihh]][1] }, relIdx.length);
						hhLog[remhh[ihh]][0] = hhLog[remhh[ihh]][0]
								- nMFalloc[0];
						hhLog[remhh[ihh]][1] = hhLog[remhh[ihh]][1]
								- nMFalloc[1];
					}
					int[][] tmpRec = new int[pickedIdx.length][indivPool[0].length + 2];
					for (int irel = 0; irel <= pickedIdx.length - 1; irel++) {
						for (int i = 0; i <= indivPool[0].length - 1; i++){
							tmpRec[irel][i] = indivPool[pickedIdx[irel]][i];
						}
						
						// hholdID
						tmpRec[irel][indivPool[0].length] = remhh[ihh];
						// hhold type index
						tmpRec[irel][indivPool[0].length + 1] = nppInHhold[remhh[ihh]][0]; 
						
						relIdx = ArrayUtils.remove(relIdx,
								ArrayUtils.indexOf(relIdx, pickedIdx[irel]));
					}
					additionalIndiv = arrHdlr.concateMatrices(additionalIndiv,
							tmpRec);
				}
			}
		}

		if (additionalIndiv != null){
			logger.debug("number of Rel allocated additionalRel = "
					+ additionalIndiv.length);
		}
		else {
			logger.debug("number of Rel allocated additionalRel = 0");
		}
		
		switch (indivType) {
		case 5:
			frelIdx = fppIdx;
			mrelIdx = mppIdx;
			break;
		case 6:
			fghIdx = fppIdx;
			mghIdx = mppIdx;
			break;
		default:
			break;
		}

		return additionalIndiv;
	}

	/**
	 * returns the sum of minimum number of people required in a set of
	 * households
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param idxArray
	 *            indices of households of interest (in pool of households)
	 * @param indivType
	 *            the type of individual of interest. 1 is parent(s), 2 is
	 *            U15Child, 3 is Student, 4 is O15Child, 5 is
	 *            Relative/NonRelative, 6 is GroupHhold/LonePerson.
	 * 
	 * @return the sum of minimum number of people required in the set of
	 *         households specified by idxArray
	 */
	private int getMinPersonsRequired(int[][] nppInHhold, int[] idxArray,
			int indivType) {
		int tmpmin = 0;
		if (idxArray == null) {
			return tmpmin;
		}
		for (int i = 0; i <= idxArray.length - 1; i++){
			tmpmin = tmpmin + nppInHhold[idxArray[i]][indivType];
		}
		return tmpmin;
	}

	private int[][] generateRecordsForLonePersons(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, int indivType, int[] usedLP) {
		int[][] tmpLPRec = null;
		ArrayHandler arrHdlr = new ArrayHandler();

		// get index of households having loneperson (both males and females)
		int[] hhLP = null;
		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][indivType] == 1) {
				hhLP = ArrayUtils.add(hhLP, ihh);
			}
		}

		if (hhLP != null) {
			for (int idxhh = 0; idxhh <= hhLP.length - 1; idxhh++) {
				if (lonePersonIdx == null || lonePersonIdx.length == 0) {
					continue;
				}
				// picks the first individual in lonePersonIdx and creates a
				// record for this individual
				int[][] tmpRec = new int[1][indivPool[lonePersonIdx[0]].length + 2];
				for (int i = 0; i <= indivPool[lonePersonIdx[0]].length - 1; i++){
					tmpRec[0][i] = indivPool[lonePersonIdx[0]][i];
				}
				tmpRec[0][indivPool[lonePersonIdx[0]].length] = hhLP[idxhh]; // hhold
																				// ID
				tmpRec[0][indivPool[lonePersonIdx[0]].length + 1] = nppInHhold[hhLP[idxhh]][0]; // hhold
																								// category
																								// index
				tmpLPRec = arrHdlr.concateMatrices(tmpLPRec, tmpRec);
				// updates hhLog
				hhLog[hhLP[idxhh]][0] = 0;
				hhLog[hhLP[idxhh]][1] = 0;
				// removes this individual from lonePersonIdx
				lonePersonIdx = ArrayUtils.remove(lonePersonIdx, 0);
			}
		}
		return tmpLPRec;
	}

	/**
	 * allocates the minimum number of people of each of the 2 types
	 * Relative/NonRelative and GroupHhold/LonePerson to households. This method
	 * actually assigns household index and household type to the corresponding
	 * records of these individuals in the individual pool.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param indivType
	 *            index of this person type, 5 is Relative/NonRelative, 6 is
	 *            GroupHhold/LonePerson.
	 * @param usedIndivIdx
	 *            index in the indivPool of individuals that have been
	 *            allocated.
	 * @return records of the minimum number of people of this type for all
	 *         applicable households in the household pool. Columns are [age,
	 *         gender, index of household relationship, index of household (in
	 *         nppInHhold), hhold category index].
	 */
	private int[][] generateRecordsMinimumRelativesOrGroupHhold(
			int[][] indivPool, int[][] nppInHhold, int[][] hhLog,
			int indivType, int[] usedIndivIdx, Random random) {
		int[][] tmpRelRec = null;
		MinIndividualsNote[] minIndivs = MinIndividualsNote.values();
//		household_relationship[] hhrel8 = household_relationship.values();
		ArrayHandler arrHdlr = new ArrayHandler();

		int[] fppIdx = null, mppIdx = null;
		switch (indivType) {
		case 5:
			fppIdx = frelIdx;
			mppIdx = mrelIdx;
			break;
		case 6:
			fppIdx = fghIdx;
			mppIdx = mghIdx;
			break;
		default:
			break;
		}

		// get the index of hholds that have at least 1 relative/non-family
		// resident and zero male
		int[] idxRel0M = getIdxOfHholdWithRelOrGroupHholdAndZeroMales(
				nppInHhold, hhLog, indivType);
		// get the index of hholds that have at least 1 relative/non-family
		// resident and zero female
		int[] idxRel0F = getIdxOfHholdWithRelOrGroupHholdAndZeroFemales(
				nppInHhold, hhLog, indivType);
		// get the index of hholds that have at least 1 relative/non-family
		// resident and nonzero female and nonzero male
		int[] idxRel = getIdxOfHholdWithRelOrGroupHholdAndNonZeroMalesAndNonZeroFemales(
				nppInHhold, hhLog, indivType);

		int frelIdxlen = 0;
		int mrelIdxlen = 0;
		if (fppIdx != null) {
			// number of females of this individual type in indivPool.
			frelIdxlen = fppIdx.length; 
		}
		if (mppIdx != null) {
			// number of males of this individual type in indivPool.
			mrelIdxlen = mppIdx.length; 
		}
		// get the minimum number of female required for idxRel0M
		int minreqF = getMinPersonsRequired(nppInHhold, idxRel0M, indivType);
		// get the minimum number of male required for idxRel0F
		int minreqM = getMinPersonsRequired(nppInHhold, idxRel0F, indivType);

		// if the number of female required for idxRel0M is greater than the number of
		// number of females of this individual type in indivPool.
		if (minreqF > frelIdxlen) { 
			while (minreqF > frelIdxlen) {
				// pick out 0M hhold that has most females
				int tmpiMax = 0;
				for (int i = 0; i <= idxRel0M.length - 1; i++) {
					if (hhLog[idxRel0M[i]][1] > hhLog[idxRel0M[tmpiMax]][1]) {
						tmpiMax = i;
					}
				}
				// minus 1 female and plus 1 male in the hhLog record as well as
				// nppInHhold record
				hhLog[idxRel0M[tmpiMax]][0] = hhLog[idxRel0M[tmpiMax]][0] + 1;
				hhLog[idxRel0M[tmpiMax]][1] = hhLog[idxRel0M[tmpiMax]][1] - 1;
				nppInHhold[idxRel0M[tmpiMax]][7] = nppInHhold[idxRel0M[tmpiMax]][7] + 1;
				nppInHhold[idxRel0M[tmpiMax]][8] = nppInHhold[idxRel0M[tmpiMax]][8] - 1;
				// get new idxRel0M, idxRel0F
				idxRel0M = getIdxOfHholdWithRelOrGroupHholdAndZeroMales(
						nppInHhold, hhLog, indivType);
				idxRel0F = getIdxOfHholdWithRelOrGroupHholdAndZeroFemales(
						nppInHhold, hhLog, indivType);
				// get new minreqF and new minreqM
				minreqM = getMinPersonsRequired(nppInHhold, idxRel0F, indivType);
				minreqF = getMinPersonsRequired(nppInHhold, idxRel0M, indivType);
				// now check and correct if number of males are not ok
				boolean testFailed = false;
				while (minreqM > mrelIdxlen) {
					// pick out the 0F hhold that has at least 2 males
					tmpiMax = 0;
					for (int i = 0; i <= idxRel0F.length - 1; i++){
						if (hhLog[idxRel0F[i]][0] > hhLog[idxRel0F[tmpiMax]][0]){
							tmpiMax = i;
						}
					}
					if (hhLog[idxRel0F[tmpiMax]][0] < 2) { // if not found
						testFailed = true;
						break;
					}
					// minus 1 male and plus 1 female in the hhLog and
					// nppInHhold records
					hhLog[idxRel0F[tmpiMax]][0] = hhLog[idxRel0F[tmpiMax]][0] - 1;
					hhLog[idxRel0F[tmpiMax]][1] = hhLog[idxRel0F[tmpiMax]][1] + 1;
					nppInHhold[idxRel0F[tmpiMax]][7] = nppInHhold[idxRel0F[tmpiMax]][7] - 1;
					nppInHhold[idxRel0F[tmpiMax]][8] = nppInHhold[idxRel0F[tmpiMax]][8] + 1;
					// get new idxRel0M
					idxRel0M = getIdxOfHholdWithRelOrGroupHholdAndZeroMales(
							nppInHhold, hhLog, indivType);
					// get new minreqM
					minreqM = getMinPersonsRequired(nppInHhold, idxRel0F,
							indivType);
				}
				if (testFailed) {
					break;
				}
			}
		} else {// if (minreqM>mrelIdxlen) {
			while (minreqM > mrelIdxlen) {
				// pick out 0F hhold that has most males
				int tmpiMax = 0;
				for (int i = 0; i <= idxRel0F.length - 1; i++) {
					if (hhLog[idxRel0F[i]][0] > hhLog[idxRel0F[tmpiMax]][0]){
						tmpiMax = i;
					}
				}
				// minus 1 male and plus 1 female in the hhLog record as well as
				// in nppInHhold
				hhLog[idxRel0F[tmpiMax]][0] = hhLog[idxRel0F[tmpiMax]][0] - 1;
				hhLog[idxRel0F[tmpiMax]][1] = hhLog[idxRel0F[tmpiMax]][1] + 1;
				nppInHhold[idxRel0F[tmpiMax]][7] = nppInHhold[idxRel0F[tmpiMax]][7] - 1;
				nppInHhold[idxRel0F[tmpiMax]][8] = nppInHhold[idxRel0F[tmpiMax]][8] + 1;
				// get new idxRel0M, idxRel0F
				idxRel0M = getIdxOfHholdWithRelOrGroupHholdAndZeroMales(
						nppInHhold, hhLog, indivType);
				idxRel0F = getIdxOfHholdWithRelOrGroupHholdAndZeroFemales(
						nppInHhold, hhLog, indivType);
				// get new minreqF and new minreqM
				minreqM = getMinPersonsRequired(nppInHhold, idxRel0F, indivType);
				minreqF = getMinPersonsRequired(nppInHhold, idxRel0M, indivType);
				// now check and correct if number of females are not ok
				boolean testFailed = false;
				while (minreqF > frelIdxlen) {
					// pick out the 0M hhold that has at least 2 females
					tmpiMax = 0;
					for (int i = 0; i <= idxRel0M.length - 1; i++) {
						if (hhLog[idxRel0M[i]][1] > hhLog[idxRel0M[tmpiMax]][1]) {
							tmpiMax = i;
						}
					}
					if (hhLog[idxRel0M[tmpiMax]][1] < 2) { // if not found
						testFailed = true;
						break;
					}
					// minus 1 female and plus 1 male in the hhLog record
					hhLog[idxRel0M[tmpiMax]][0] = hhLog[idxRel0M[tmpiMax]][0] + 1;
					hhLog[idxRel0M[tmpiMax]][1] = hhLog[idxRel0M[tmpiMax]][1] - 1;
					nppInHhold[idxRel0M[tmpiMax]][7] = nppInHhold[idxRel0M[tmpiMax]][7] + 1;
					nppInHhold[idxRel0M[tmpiMax]][8] = nppInHhold[idxRel0M[tmpiMax]][8] - 1;
					// get new idxRel0F
					idxRel0F = getIdxOfHholdWithRelOrGroupHholdAndZeroFemales(
							nppInHhold, hhLog, indivType);
					// get new minreqF
					minreqF = getMinPersonsRequired(nppInHhold, idxRel0M,
							indivType);
				}
				if (testFailed) {
					break;
				}
			}
		}

		// int frelIdxlen = 0; int mrelIdxlen = 0; int len0M = 0; int len0F = 0;
		// int len = 0;
		// if (frelIdx!=null) frelIdxlen = frelIdx.length;
		// if (mrelIdx!=null) mrelIdxlen = mrelIdx.length;
		// if (idxRel0M!=null) len0M=idxRel0M.length;
		// if (idxRel0F!=null) len0F=idxRel0F.length;
		// if (idxRel!=null) len=idxRel.length;
		//
		// if (len0M>frelIdxlen) {
		// if (len0F+len+(len0M-frelIdxlen)<=mrelIdxlen) {
		// //logger.debug("OK");
		// for (int i=frelIdxlen; i<=len0M-1; i++) {
		// nppInHhold[idxRel0M[i]][7]=nppInHhold[idxRel0M[i]][7]+1;
		// nppInHhold[idxRel0M[i]][8]=nppInHhold[idxRel0M[i]][8]-1;
		// hhLog[idxRel0M[i]][0] = hhLog[idxRel0M[i]][0]+1;
		// hhLog[idxRel0M[i]][1] = hhLog[idxRel0M[i]][1]-1;
		// }
		// }
		// } else if (len0F>mrelIdxlen) {
		// if (len0M+len+(len0F-mrelIdxlen)<=frelIdxlen) {
		// //logger.debug("OK");
		// for (int i=mrelIdxlen; i<=len0F-1; i++) {
		// nppInHhold[idxRel0F[i]][7]=nppInHhold[idxRel0F[i]][7]-1;
		// nppInHhold[idxRel0F[i]][8]=nppInHhold[idxRel0F[i]][8]+1;
		// hhLog[idxRel0F[i]][0] = hhLog[idxRel0F[i]][0]-1;
		// hhLog[idxRel0F[i]][1] = hhLog[idxRel0F[i]][1]+1;
		// }
		// }
		// }

		idxRel0M = getIdxOfHholdWithRelOrGroupHholdAndZeroMales(nppInHhold,
				hhLog, indivType);
		idxRel0F = getIdxOfHholdWithRelOrGroupHholdAndZeroFemales(nppInHhold,
				hhLog, indivType);
		idxRel = getIdxOfHholdWithRelOrGroupHholdAndNonZeroMalesAndNonZeroFemales(
				nppInHhold, hhLog, indivType);

		// create records of relatives in hholds that have zero male
		if (idxRel0M != null) {
			// logger.debug("idxRel0M: " + idxRel0M.length);
			for (int tmpi = 0; tmpi <= idxRel0M.length - 1; tmpi++) {
				int nM = hhLog[idxRel0M[tmpi]][0];
				int nF = hhLog[idxRel0M[tmpi]][1];
				if (fppIdx == null
						|| fppIdx.length < nppInHhold[idxRel0M[tmpi]][indivType]
						|| nF < nppInHhold[idxRel0M[tmpi]][indivType]) {
                    logger.warn("WARNING: cannot generate minimum number of relative for record "
									+ idxRel0M[tmpi]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idxRel0M[tmpi]][0]]
											.toString()
									+ ")! Not enough female relative individuals found in indivPool!");
					hhLog[idxRel0M[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idxRel0M[tmpi]].length, -1);
					continue;
				}
				// randomly pick the minimum number of relatives from frelIdx
				int[] relativeIdx = arrHdlr.pickRandomFromArray(fppIdx, null,
						nppInHhold[idxRel0M[tmpi]][indivType], random);
				int[][] tmpRec = new int[relativeIdx.length][indivPool[0].length + 2];
				for (int irel = 0; irel <= relativeIdx.length - 1; irel++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++) {
						tmpRec[irel][i] = indivPool[relativeIdx[irel]][i];
					}
					tmpRec[irel][indivPool[0].length] = idxRel0M[tmpi]; // hhold
																		// ID
					tmpRec[irel][indivPool[0].length + 1] = nppInHhold[idxRel0M[tmpi]][0]; // hhold
																							// cataegory
																							// index
					usedIndivIdx = ArrayUtils.add(usedIndivIdx, relativeIdx[irel]);
					fppIdx = ArrayUtils.remove(fppIdx,
							ArrayUtils.indexOf(fppIdx, relativeIdx[irel]));
				}
				nF = nF - nppInHhold[idxRel0M[tmpi]][indivType];
				hhLog[idxRel0M[tmpi]][0] = nM;
				hhLog[idxRel0M[tmpi]][1] = nF;
				tmpRelRec = arrHdlr.concateMatrices(tmpRelRec, tmpRec);
				// for (int myi=0; myi<=tmpRec.length-1; myi++)
				// logger.debug(tmpRec[myi][0] + ", " + tmpRec[myi][1] +
				// ", " + hhrel8[tmpRec[myi][2]] + ", " +
				// tmpRec[myi][3] + ", " +
				// minIndivs[tmpRec[myi][4]].toString());
			}
		}

		// create records of relatives in hholds that have zero female
		if (idxRel0F != null) {
			// logger.debug("idxRel0F: " + idxRel0F.length);
			for (int tmpi = 0; tmpi <= idxRel0F.length - 1; tmpi++) {
				int nM = hhLog[idxRel0F[tmpi]][0];
				int nF = hhLog[idxRel0F[tmpi]][1];
				// mrelIdxlen = 0; frelIdxlen = 0;
				// if (mrelIdx!=null) mrelIdxlen = mrelIdx.length;
				// if (frelIdx!=null) frelIdxlen = frelIdx.length;
				// logger.debug("\tidxRel0F: " + tmpi + "/" +
				// idxRel0F.length);
				// logger.debug("\t\tnM=" + nM + ", nF=" + nF +
				// ", mrelIdxlen=" + mrelIdxlen + ", frelIdxlen=" + frelIdxlen +
				// ", minpp=" +
				// nppInHhold[idxRel0F[tmpi]][indivType]);
				if (mppIdx == null
						|| mppIdx.length < nppInHhold[idxRel0F[tmpi]][indivType]
						|| nM < nppInHhold[idxRel0F[tmpi]][indivType]) {
                    logger.warn("WARNING: cannot generate minimum number of relative for record "
									+ idxRel0F[tmpi]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idxRel0F[tmpi]][0]]
											.toString()
									+ ")! Not enough male relative individuals found in indivPool!");
					hhLog[idxRel0F[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idxRel0F[tmpi]].length, -1);
					continue;
				}
				// randomly pick the minimum number of relatives from mrelIdx
				int[] relativeIdx = arrHdlr.pickRandomFromArray(mppIdx, null,
						nppInHhold[idxRel0F[tmpi]][indivType], random);
				int[][] tmpRec = new int[relativeIdx.length][indivPool[0].length + 2];
				for (int irel = 0; irel <= relativeIdx.length - 1; irel++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++) {
						tmpRec[irel][i] = indivPool[relativeIdx[irel]][i];
					}
					tmpRec[irel][indivPool[0].length] = idxRel0F[tmpi];
					tmpRec[irel][indivPool[0].length + 1] = nppInHhold[idxRel0F[tmpi]][0];
					usedIndivIdx = ArrayUtils.add(usedIndivIdx, relativeIdx[irel]);
					mppIdx = ArrayUtils.remove(mppIdx,
							ArrayUtils.indexOf(mppIdx, relativeIdx[irel]));
				}
				nM = nM - nppInHhold[idxRel0F[tmpi]][indivType];
				hhLog[idxRel0F[tmpi]][0] = nM;
				hhLog[idxRel0F[tmpi]][1] = nF;
				tmpRelRec = arrHdlr.concateMatrices(tmpRelRec, tmpRec);
			}
		}

		// create records of relatives in hholds that have nonzero male and
		// nonzero female
		if (idxRel != null) {
			// logger.debug("idxRel: " + idxRel.length);
			for (int tmpi = 0; tmpi <= idxRel.length - 1; tmpi++) {
				int nM = hhLog[idxRel[tmpi]][0];
				int nF = hhLog[idxRel[tmpi]][1];
				mrelIdxlen = 0;
				frelIdxlen = 0;
				if (mppIdx != null) {
					mrelIdxlen = mppIdx.length;
				}
				if (fppIdx != null) {
					frelIdxlen = fppIdx.length;
				}
				
				if (nM + nF < nppInHhold[idxRel[tmpi]][indivType]) {
                    logger.warn("WARNING: cannot generate minimum number of relative for record "
									+ idxRel[tmpi]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idxRel[tmpi]][0]]
											.toString()
									+ ")! Not enough male and female relative individuals in this hhold!");
					hhLog[idxRel[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idxRel[tmpi]].length, -1);
					continue;
				}
				if (mrelIdxlen + frelIdxlen < nppInHhold[idxRel[tmpi]][indivType]) {
                    logger.warn("WARNING: cannot generate minimum number of relative for record "
									+ idxRel[tmpi]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idxRel[tmpi]][0]]
											.toString()
									+ ")! Not enough male and female relative individuals in indivPool!");
					hhLog[idxRel[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idxRel[tmpi]].length, -1);
					continue;
				}
				int[][] tmpRec = new int[nppInHhold[idxRel[tmpi]][indivType]][indivPool[0].length + 2];
				for (int irel = 0; irel <= tmpRec.length - 1; irel++) {
					if ((nM > nF || (nF >= nM && fppIdx == null))
							&& mppIdx != null) {
						// randomly pick a relative from mrelIdx
						int[] relativeIdx = arrHdlr.pickRandomFromArray(mppIdx,
								null, 1, random);
						for (int i = 0; i <= indivPool[0].length - 1; i++) {
							tmpRec[irel][i] = indivPool[relativeIdx[0]][i];
						}
						tmpRec[irel][indivPool[0].length] = idxRel[tmpi];
						tmpRec[irel][indivPool[0].length + 1] = nppInHhold[idxRel[tmpi]][0];
						nM = nM - 1;
						usedIndivIdx = ArrayUtils.add(usedIndivIdx, relativeIdx[0]);
						mppIdx = ArrayUtils.remove(mppIdx,
								ArrayUtils.indexOf(mppIdx, relativeIdx[0]));
					} else if ((nF >= nM || (nM > nF && mppIdx == null))
							&& fppIdx != null) {
						// randomly pick a relative from frelIdx
						int[] relativeIdx = arrHdlr.pickRandomFromArray(fppIdx,
								null, 1, random);
						for (int i = 0; i <= indivPool[0].length - 1; i++) {
							tmpRec[irel][i] = indivPool[relativeIdx[0]][i];
						}
						tmpRec[irel][indivPool[0].length] = idxRel[tmpi];
						tmpRec[irel][indivPool[0].length + 1] = nppInHhold[idxRel[tmpi]][0];
						nF = nF - 1;
						usedIndivIdx = ArrayUtils.add(usedIndivIdx, relativeIdx[0]);
						fppIdx = ArrayUtils.remove(fppIdx,
								ArrayUtils.indexOf(fppIdx, relativeIdx[0]));
					}
			}
				hhLog[idxRel[tmpi]][0] = nM;
				hhLog[idxRel[tmpi]][1] = nF;
				tmpRelRec = arrHdlr.concateMatrices(tmpRelRec, tmpRec);
			}
		}

		switch (indivType) {
		case 5:
			frelIdx = fppIdx;
			mrelIdx = mppIdx;
			break;
		case 6:
			fghIdx = fppIdx;
			mghIdx = mppIdx;
			break;
		default:
			break;
		}

		return tmpRelRec;
	}

	/**
	 * 
	 * @param indivPool
	 * @param nppInHhold
	 * @param hhLog
	 * @param childType
	 * @param usedChildIdx
	 * @return
	 */
	private int[][] generateRecordsMinimumU15Children(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, int childType,
			HashMap<Integer, ArrayList<int[]>> crnIndRecords, String cdName,
			Random random) {
		logger.debug("CHILDREN TYPE " + childType);
		int[][] tmpChiRec = null;
		ArrayHandler arrHdlr = new ArrayHandler();

		// logger.debug("children details");
		// for (int ich=0; ich<=u15Idx.length-1; ich++)
		// logger.debug(indivPool[u15Idx[ich]][0] + "," +
		// indivPool[u15Idx[ich]][1] + "," + indivPool[u15Idx[ich]][2]);

		// get the index of hholds that have at least 1 childType and zero male
		int[] idxhh0M = getIdxOfHholdWithChildAndZeroMales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and zero
		// female
		int[] idxhh0F = getIdxOfHholdWithChildAndZeroFemales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and nonzero
		// female and nonzero male
		int[] idxhh = getIdxOfHholdWithChildAndNonZeroMalesAndNonZeroFemales(
				nppInHhold, hhLog, childType);

		int[] remIChi = ArrayUtils.addAll(idxhh, idxhh0M);
		remIChi = ArrayUtils.addAll(remIChi, idxhh0F);
		int remIChiLen = ArrayUtils.getLength(remIChi);
		// logger.debug("remIChiLen=" + remIChiLen +
		// ", nppInHhold.length=" + nppInHhold.length);

		int[] usedi = null;
		int usediLen = ArrayUtils.getLength(usedi);

		// ArrayList<String> dAge1stChildParent = new ArrayList<String>();

		logger.debug("u15Idx.length before allocation "
				+ ArrayUtils.getLength(u15Idx));
		while (usediLen < remIChiLen) {
			if (u15Idx == null) {
                logger.warn("WARNING: No male nor female children of type "
								+ childType
								+ " found in the population! Aborting generateRecordsMinimumChildren");
				break;
			}

			// get elements in remIChi that are not in usedi
			int[] remi = arrHdlr.getNonCommonInArrays(remIChi, usedi);

			// picks out the household having the youngest parent
			int tmpimin = 0;
			for (int i = 0; i <= remi.length - 1; i++) {
				if (hhLog[remi[i]][2] <= hhLog[remi[tmpimin]][2]) {
					tmpimin = i;
				}
			}
			// updates usedi
			usedi = ArrayUtils.add(usedi, remi[tmpimin]);
			usediLen = usedi.length;

			int imin = ArrayUtils.indexOf(remIChi, remi[tmpimin]);
			int nM = hhLog[remIChi[imin]][0];
			int nF = hhLog[remIChi[imin]][1];

			// if there are no residents left to allocate in
			// this household, ie it has enough residents.
			if (nM + nF <= 0) { 
				continue; // ignore it
			}
			
			ArrayList<int[]> existRes = crnIndRecords.get(remIChi[imin]);
			HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();
			ArrayList<int[]> parentsDetails = new ArrayList<int[]>();
			for (int ier = 0; ier <= existRes.size() - 1; ier++) {
				if (hhrel8[existRes.get(ier)[2]]
						.equals(HouseholdRelationship.Married)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.DeFacto)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.LoneParent)) {
					// the 2 values in each row of parentsDetails are index of
					// the parent in arraylist existRes and the parent age.
					parentsDetails.add(new int[] { ier, existRes.get(ier)[0] });
				}
			}

			if (parentsDetails.size() == 0) {
				logger.warn("WARNGIN: Household " + remIChi[imin]
						+ " needs at least 1 U15Child but has no parents!!!");
				continue;
			} else if (parentsDetails.size() > 2) {
                logger.warn("WARNGIN: Household "
								+ remIChi[imin]
								+ " needs at least 1 U15Child but has more than 2 parents!!!");
				continue;
			}
			int minParentAge = parentsDetails.get(0)[1];
			int iYParent = parentsDetails.get(0)[0];
			int maxParentAge = -1;
			int iOParent = -1;
			if (parentsDetails.size() == 2) {
				maxParentAge = parentsDetails.get(1)[1];
				iOParent = parentsDetails.get(1)[0];
				if (parentsDetails.get(1)[1] < minParentAge) {
					minParentAge = parentsDetails.get(1)[1];
					iYParent = parentsDetails.get(1)[0];
					maxParentAge = parentsDetails.get(0)[1];
					iOParent = parentsDetails.get(0)[0];
				}
			}

			// starts a loop here to assign another child to this household if
			// the number of children remaining in u15Idx is greater than ()
			boolean isFirstChild = true;
			while (ArrayUtils.getLength(u15Idx) > (remIChiLen - usediLen)) {

				if (nM <= 0 && nF <= 0) {
					break;
				}

				int[] dAgeParChi = this.makedAgeParentChild(u15Idx, indivPool,
						minParentAge);
				int selectedChildIdx = arrHdlr.getIdxOfNextGreaterValue(
						dAgeParChi, min_dAgeParentChild);
				if (selectedChildIdx < 0) {
					logger.info("Corrections of child age and parent age for U15Child");
					if (!isFirstChild) {
						break;
					}
					// selectedChildIdx = index of the max value in dAgeParChi
					selectedChildIdx = arrHdlr.getIndexOfMax(dAgeParChi);
					// changes the age of the youngest child to the minimum age
					// of his age group + random(0:3)
					indivPool[u15Idx[selectedChildIdx]][0] = this
							.adjustToYoungestInAgeGroup(indivPool[u15Idx[selectedChildIdx]][0])
							+ random.nextInt(4);
					// age of younger parent = new age of youngest child +
					// min_dAgeParentChild +
					// random(max_dAgeYoungerParentEldestChild-min_dAgeParentChild)
					minParentAge = indivPool[u15Idx[selectedChildIdx]][0]
							+ min_dAgeParentChild
							+ random.nextInt(max_dAgeYoungerParentEldestChild
									- min_dAgeParentChild);
					if (minParentAge < 18) {
						minParentAge = 18;
					}
					// updates new age of younger parent to existRes
					existRes.get(iYParent)[0] = minParentAge;
					if (parentsDetails.size() == 2
							&& minParentAge > maxParentAge) {// if new age of
																// younger
																// parent > age
																// of elder
																// parent
						maxParentAge = minParentAge;// new age of elder parent =
													// new age of younger parent
						existRes.get(iOParent)[0] = maxParentAge; // updates new
																	// age of
																	// elder
																	// parent to
																	// existRes
					}
					// updates existRes to crnIndRecords
					crnIndRecords.put(remIChi[imin], existRes);
				}
				if (indivPool[u15Idx[selectedChildIdx]][1] == 0) {// if the
																	// selected
																	// child is
																	// a female
					if (nF <= 0) { // and yet the number of females available to
									// this household is 0
						// swap 1 male to female
						hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] - 1;
						hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] + 1;
						nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] - 1;
						nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] + 1;
						// then reassign values to nM and nF
						nM = hhLog[remIChi[imin]][0];
						nF = hhLog[remIChi[imin]][1];
					}
					// update nF
					nF = nF - 1;
					// update hhLog
					hhLog[remIChi[imin]][1] = nF;
				} else { // if the selected child is a male
					if (nM <= 0) { // and yet the number of males available to
									// this household is 0
						// swap 1 female to male
						hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] + 1;
						hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] - 1;
						nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] + 1;
						nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] - 1;
						// then reassign values to nM and nF
						nM = hhLog[remIChi[imin]][0];
						nF = hhLog[remIChi[imin]][1];
					}
					// update nM
					nM = nM - 1;
					// update hhLog
					hhLog[remIChi[imin]][0] = nM;
				}

				int[][] tmpRec = new int[1][indivPool[0].length + 2];
				tmpRec[0] = arrHdlr.makeUniformArray(tmpRec[0].length, -1);
				for (int i = 0; i <= indivPool[0].length - 1; i++) {
					tmpRec[0][i] = indivPool[u15Idx[selectedChildIdx]][i];
				}
				tmpRec[0][indivPool[0].length] = remIChi[imin];
				tmpRec[0][indivPool[0].length + 1] = nppInHhold[remIChi[imin]][0];

				// update chIdx
				u15Idx = ArrayUtils.remove(u15Idx, selectedChildIdx);
				tmpChiRec = arrHdlr.concateMatrices(tmpChiRec, tmpRec);

				// if (isFirstChild) {
				// dAge1stChildParent.add(cdName + "," + remIChi[imin] + "," +
				// minParentAge + "," + tmpRec[0][0]);
				// }

				isFirstChild = false;
			}

		}

		// (new TextFileHandler()).writeToText("dAge1stChildParent.csv",
		// dAge1stChildParent, true);

		if (ArrayUtils.getLength(u15Idx) == 0) {
			logger.debug("u15Idx.length after allocation "
					+ ArrayUtils.getLength(u15Idx));
		}
		else {
			logger.debug("u15Idx.length after allocation "
					+ ArrayUtils.getLength(u15Idx) + " - NONZERO!!!");
		}
		
		return tmpChiRec;
	}

	/**
	 * 
	 * @param indivPool
	 * @param nppInHhold
	 * @param hhLog
	 * @param childType
	 * @param usedChildIdx
	 * @return
	 */
	private int[][] generateRecordsMinimumStuChildren(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, int childType,
			HashMap<Integer, ArrayList<int[]>> crnIndRecords, String cdName,
			Random random) {
		logger.debug("CHILDREN TYPE " + childType);
		int[][] tmpChiRec = null;
		ArrayHandler arrHdlr = new ArrayHandler();

		// logger.debug("children details");
		// for (int ich=0; ich<=stuIdx.length-1; ich++)
		// logger.debug(indivPool[stuIdx[ich]][0] + "," +
		// indivPool[stuIdx[ich]][1] + "," + indivPool[stuIdx[ich]][2]);

		// get the index of hholds that have at least 1 childType and zero male
		int[] idxhh0M = getIdxOfHholdWithChildAndZeroMales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and zero
		// female
		int[] idxhh0F = getIdxOfHholdWithChildAndZeroFemales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and nonzero
		// female and nonzero male
		int[] idxhh = getIdxOfHholdWithChildAndNonZeroMalesAndNonZeroFemales(
				nppInHhold, hhLog, childType);

		int[] remIChi = ArrayUtils.addAll(idxhh, idxhh0M);
		remIChi = ArrayUtils.addAll(remIChi, idxhh0F);
		int remIChiLen = ArrayUtils.getLength(remIChi);
		// logger.debug("remIChiLen=" + remIChiLen +
		// ", nppInHhold.length=" + nppInHhold.length);

		int[] usedi = null;
		int usediLen = ArrayUtils.getLength(usedi);

		// ArrayList<String> dAge1stChildParent = new ArrayList<String>();

		logger.debug("stuIdx.length before allocation "
				+ ArrayUtils.getLength(stuIdx));
		while (usediLen < remIChiLen) {
			if (stuIdx == null) {
                logger.warn("WARNING: No male nor female children of type "
								+ childType
								+ " found in the population! Aborting generateRecordsMinimumChildren");
				break;
			}

			// get elements in remIChi that are not in usedi
			int[] remi = arrHdlr.getNonCommonInArrays(remIChi, usedi);

			// picks out the household having the youngest parent
			int tmpimin = 0;
			for (int i = 0; i <= remi.length - 1; i++) {
				if (hhLog[remi[i]][2] <= hhLog[remi[tmpimin]][2]) {
					tmpimin = i;
				}
			}
			// updates usedi
			usedi = ArrayUtils.add(usedi, remi[tmpimin]);
			usediLen = usedi.length;

			int imin = ArrayUtils.indexOf(remIChi, remi[tmpimin]);
			int nM = hhLog[remIChi[imin]][0];
			int nF = hhLog[remIChi[imin]][1];
			
			// if there are no residents left to allocate in
			// this household, ie it has enough residents.
			if (nM + nF <= 0)  {
				continue; // ignore it
			}
			
			ArrayList<int[]> existRes = crnIndRecords.get(remIChi[imin]);
			HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();
			ArrayList<int[]> parentsDetails = new ArrayList<int[]>();
			for (int ier = 0; ier <= existRes.size() - 1; ier++) {
				if (hhrel8[existRes.get(ier)[2]]
						.equals(HouseholdRelationship.Married)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.DeFacto)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.LoneParent)) {
					// the 2 values in each row of parentsDetails are index of
					// the parent in arraylist existRes and the parent age.
					parentsDetails.add(new int[] { ier, existRes.get(ier)[0] });
				}
			}

			if (parentsDetails.size() == 0) {
				logger.debug("WARNGIN: Household " + remIChi[imin]
						+ " needs at least 1 Student but has no parents!!!");
				continue;
			} else if (parentsDetails.size() > 2) {
                logger.warn("WARNGIN: Household "
								+ remIChi[imin]
								+ " needs at least 1 Student but has more than 2 parents!!!");
				continue;
			}
			int minParentAge = parentsDetails.get(0)[1];
			int iYParent = parentsDetails.get(0)[0];
			int maxParentAge = -1;
			int iOParent = -1;
			if (parentsDetails.size() == 2) {
				maxParentAge = parentsDetails.get(1)[1];
				iOParent = parentsDetails.get(1)[0];
				if (parentsDetails.get(1)[1] < minParentAge) {
					minParentAge = parentsDetails.get(1)[1];
					iYParent = parentsDetails.get(1)[0];
					maxParentAge = parentsDetails.get(0)[1];
					iOParent = parentsDetails.get(0)[0];
				}
			}

			// starts a loop here to assign another child to this household if
			// the number of children remaining in o15Idx is greater than ()
			boolean isFirstChild = true;
			while (ArrayUtils.getLength(stuIdx) > (remIChiLen - usediLen)) {

				if (nM <= 0 && nF <= 0) {
					break;
				}

				int[] dAgeParChi = this.makedAgeParentChild(stuIdx, indivPool,
						minParentAge);
				int selectedChildIdx = arrHdlr.getIdxOfNextGreaterValue(
						dAgeParChi, min_dAgeParentChild);
				if (selectedChildIdx < 0) {
					logger.info("Corrections of child age and parent age for Student");
					if (!isFirstChild) {
						break;
					}
					// selectedChildIdx = index of the max value in dAgeParChi
					selectedChildIdx = arrHdlr.getIndexOfMax(dAgeParChi);
					// changes the age of the youngest child to the minimum age
					// of his age group + random(0:3)
					indivPool[stuIdx[selectedChildIdx]][0] = this
							.adjustToYoungestInAgeGroup(indivPool[stuIdx[selectedChildIdx]][0])
							+ random.nextInt(4);
					// age of younger parent = new age of youngest child +
					// min_dAgeParentChild +
					// random(max_dAgeYoungerParentEldestChild-min_dAgeParentChild)
					minParentAge = indivPool[stuIdx[selectedChildIdx]][0]
							+ min_dAgeParentChild
							+ random.nextInt(max_dAgeYoungerParentEldestChild
									- min_dAgeParentChild);
					if (minParentAge < 18) {
						minParentAge = 18;
					}
					// updates new age of younger parent to existRes
					existRes.get(iYParent)[0] = minParentAge;
					
					// if new age of younger parent > age of elder parent
					if (parentsDetails.size() == 2
							&& minParentAge > maxParentAge) {
						// new age of elder parent = new age of younger parent
						maxParentAge = minParentAge;
						// updates new age of elder parent to existRes
						existRes.get(iOParent)[0] = maxParentAge; 
					}
					// updates existRes to crnIndRecords
					crnIndRecords.put(remIChi[imin], existRes);

				}
				
				// if the selected child is a female
				if (indivPool[stuIdx[selectedChildIdx]][1] == 0) {
					// and yet the number of females available to
					// this household is 0
					if (nF <= 0) { 
						// swap 1 male to female
						hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] - 1;
						hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] + 1;
						nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] - 1;
						nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] + 1;
						// then reassign values to nM and nF
						nM = hhLog[remIChi[imin]][0];
						nF = hhLog[remIChi[imin]][1];
					}
					// update nF
					nF = nF - 1;
					// update hhLog
					hhLog[remIChi[imin]][1] = nF;
				}
				// if the selected child is a male
				else { 
					// and yet the number of males available to
					// this household is 0
					if (nM <= 0) { 
						// swap 1 female to male
						hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] + 1;
						hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] - 1;
						nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] + 1;
						nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] - 1;
						// then reassign values to nM and nF
						nM = hhLog[remIChi[imin]][0];
						nF = hhLog[remIChi[imin]][1];
					}
					// update nM
					nM = nM - 1;
					// update hhLog
					hhLog[remIChi[imin]][0] = nM;
				}

				int[][] tmpRec = new int[1][indivPool[0].length + 2];
				tmpRec[0] = arrHdlr.makeUniformArray(tmpRec[0].length, -1);
				for (int i = 0; i <= indivPool[0].length - 1; i++) {
					tmpRec[0][i] = indivPool[stuIdx[selectedChildIdx]][i];
				}
				tmpRec[0][indivPool[0].length] = remIChi[imin];
				tmpRec[0][indivPool[0].length + 1] = nppInHhold[remIChi[imin]][0];

				// update chIdx
				stuIdx = ArrayUtils.remove(stuIdx, selectedChildIdx);
				tmpChiRec = arrHdlr.concateMatrices(tmpChiRec, tmpRec);

				// if (isFirstChild) {
				// dAge1stChildParent.add(cdName + "," + remIChi[imin] + "," +
				// minParentAge + "," + tmpRec[0][0]);
				// }

				isFirstChild = false;
			}

		}

		if (ArrayUtils.getLength(stuIdx) == 0) {
			logger.debug("stuIdx.length after allocation "
					+ ArrayUtils.getLength(stuIdx));
		}
		else {
			logger.debug("stuIdx.length after allocation "
					+ ArrayUtils.getLength(stuIdx) + " - NONZERO!!!");
		}
		
		return tmpChiRec;
	}

	/**
	 * 
	 * @param indivPool
	 * @param nppInHhold
	 * @param hhLog
	 * @param childType
	 * @param usedChildIdx
	 * @return
	 */
	private int[][] generateRecordsMinimumO15Children(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, int childType,
			HashMap<Integer, ArrayList<int[]>> crnIndRecords, String cdName,
			Random random) {
		logger.debug("CHILDREN TYPE " + childType);
		int[][] tmpChiRec = null;
		ArrayHandler arrHdlr = new ArrayHandler();

		// get the index of hholds that have at least 1 childType and zero male
		int[] idxhh0M = getIdxOfHholdWithChildAndZeroMales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and zero
		// female
		int[] idxhh0F = getIdxOfHholdWithChildAndZeroFemales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and nonzero
		// female and nonzero male
		int[] idxhh = getIdxOfHholdWithChildAndNonZeroMalesAndNonZeroFemales(
				nppInHhold, hhLog, childType);

		int[] remIChi = ArrayUtils.addAll(idxhh, idxhh0M);
		remIChi = ArrayUtils.addAll(remIChi, idxhh0F);
		int remIChiLen = ArrayUtils.getLength(remIChi);

		int[] usedi = null;
		int usediLen = ArrayUtils.getLength(usedi);

		// ArrayList<String> dAge1stChildParent = new ArrayList<String>();

		logger.debug("o15Idx.length before allocation "
				+ ArrayUtils.getLength(o15Idx));
		while (usediLen < remIChiLen) {
			if (o15Idx == null) {
                logger.warn("WARNING: No male nor female children of type "
								+ childType
								+ " found in the population! Aborting generateRecordsMinimumChildren");
				break;
			}

			// get elements in remIChi that are not in usedi
			int[] remi = arrHdlr.getNonCommonInArrays(remIChi, usedi);

			// picks out the household having the youngest parent
			int tmpimin = 0;
			for (int i = 0; i <= remi.length - 1; i++) {
				if (hhLog[remi[i]][2] <= hhLog[remi[tmpimin]][2]) {
					tmpimin = i;
				}
			}
			// updates usedi
			usedi = ArrayUtils.add(usedi, remi[tmpimin]);
			usediLen = ArrayUtils.getLength(usedi);

			int imin = ArrayUtils.indexOf(remIChi, remi[tmpimin]);
			int nM = hhLog[remIChi[imin]][0];
			int nF = hhLog[remIChi[imin]][1];
			
			// if there are no residents left to allocate in
			// this household, ie it has enough residents.
			if (nM + nF <= 0)  {
				// ignore it
				continue; 
			}
			logger.debug("allocating O15Child, hhold " + remIChi[imin]);

			// logger.debug("parents details");
			ArrayList<int[]> existRes = crnIndRecords.get(remIChi[imin]);
			HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();
			ArrayList<int[]> parentsDetails = new ArrayList<int[]>();
			for (int ier = 0; ier <= existRes.size() - 1; ier++) {
				if (hhrel8[existRes.get(ier)[2]]
						.equals(HouseholdRelationship.Married)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.DeFacto)
						|| hhrel8[existRes.get(ier)[2]]
								.equals(HouseholdRelationship.LoneParent)) {
					// the 2 values in each row of parentsDetails are index of
					// the parent in arraylist existRes and the parent age.
					parentsDetails.add(new int[] { ier, existRes.get(ier)[0] });
					// arrHdlr.displayArray(existRes.get(ier));
				}
			}

			if (parentsDetails.size() == 0) {
				logger.debug("WARNGIN: Household " + remIChi[imin]
						+ " needs at least 1 O15Child but has no parents!!!");
				continue;
			} else if (parentsDetails.size() > 2) {
                logger.warn("WARNGIN: Household "
								+ remIChi[imin]
								+ " needs at least 1 O15Child but has more than 2 parents!!!");
				continue;
			}
			int minParentAge = parentsDetails.get(0)[1];
			int iYParent = parentsDetails.get(0)[0];
			int maxParentAge = -1;
			int iOParent = -1;
			if (parentsDetails.size() == 2) {
				maxParentAge = parentsDetails.get(1)[1];
				iOParent = parentsDetails.get(1)[0];
				if (parentsDetails.get(1)[1] < minParentAge) {
					minParentAge = parentsDetails.get(1)[1];
					iYParent = parentsDetails.get(1)[0];
					maxParentAge = parentsDetails.get(0)[1];
					iOParent = parentsDetails.get(0)[0];
				}
			}

			// starts a loop here to assign another child to this household if
			// the number of children remaining in o15Idx is greater than ()
			boolean isFirstChild = true;
			while (ArrayUtils.getLength(o15Idx) > (remIChiLen - usediLen)) {
				if (nM <= 0 && nF <= 0) {
					break;
				}

				int[] dAgeParChi = this.makedAgeParentChild(o15Idx, indivPool,
						minParentAge);
				int selectedChildIdx = arrHdlr.getIdxOfNextGreaterValue(
						dAgeParChi, min_dAgeParentChild);

				// no suitable child found for this household
				if (selectedChildIdx < 0) { 
					// if this is not the first child of this type
					if (!isFirstChild) {
						break;
					}
					logger.info("Corrections of child age and parent age for O15Child, hholdID "
									+ remIChi[imin]);
					// selectedChildIdx = index of the max value in dAgeParChi
					selectedChildIdx = arrHdlr.getIndexOfMax(dAgeParChi);
					// changes the age of the youngest child to the minimum age
					// of his age group
					indivPool[o15Idx[selectedChildIdx]][0] = this
							.adjustToYoungestInAgeGroup(indivPool[o15Idx[selectedChildIdx]][0]);
					// age of younger parent = new age of youngest child +
					// min_dAgeParentChild +
					// random(max_dAgeYoungerParentEldestChild-min_dAgeParentChild)
					minParentAge = indivPool[o15Idx[selectedChildIdx]][0]
							+ min_dAgeParentChild
							+ random.nextInt(max_dAgeYoungerParentEldestChild
									- min_dAgeParentChild);
					if (minParentAge < 18) {
						minParentAge = 18;
					}
					// updates new age of younger parent to existRes
					existRes.get(iYParent)[0] = minParentAge;
					
					// if new age of younger parent > age of elder parent
					if (parentsDetails.size() == 2
							&& minParentAge > maxParentAge) {
						// new age of elder parent = new age of younger parent
						maxParentAge = minParentAge;
						// updates new age of elder parent to existRes						
						existRes.get(iOParent)[0] = maxParentAge; 
					}
					// updates existRes to crnIndRecords
					crnIndRecords.put(remIChi[imin], existRes);
				}

				// if the selected child is a female
				if (indivPool[o15Idx[selectedChildIdx]][1] == 0) {
					// and yet the number of females available to
					// this household is 0
					if (nF <= 0) { 
						// swap 1 male to female
						hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] - 1;
						hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] + 1;
						nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] - 1;
						nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] + 1;
						// then reassign values to nM and nF
						nM = hhLog[remIChi[imin]][0];
						nF = hhLog[remIChi[imin]][1];
					}
					// update nF
					nF = nF - 1;
					// update hhLog
					hhLog[remIChi[imin]][1] = nF;
				} 
				// if the selected child is a male
				else { 
					// and yet the number of males available to
					// this household is 0
					if (nM <= 0) { 
						// swap 1 female to male
						hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] + 1;
						hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] - 1;
						nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] + 1;
						nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] - 1;
						// then reassign values to nM and nF
						nM = hhLog[remIChi[imin]][0];
						nF = hhLog[remIChi[imin]][1];
					}
					// update nM
					nM = nM - 1;
					// update hhLog
					hhLog[remIChi[imin]][0] = nM;
				}

				int[][] tmpRec = new int[1][indivPool[0].length + 2];
				tmpRec[0] = arrHdlr.makeUniformArray(tmpRec[0].length, -1);
				for (int i = 0; i <= indivPool[0].length - 1; i++) {
					tmpRec[0][i] = indivPool[o15Idx[selectedChildIdx]][i];
				}
				tmpRec[0][indivPool[0].length] = remIChi[imin];
				tmpRec[0][indivPool[0].length + 1] = nppInHhold[remIChi[imin]][0];

				// update chIdx
				o15Idx = ArrayUtils.remove(o15Idx, selectedChildIdx);
				tmpChiRec = arrHdlr.concateMatrices(tmpChiRec, tmpRec);

				isFirstChild = false;
			}
		}

		if (ArrayUtils.getLength(o15Idx) == 0) {
			logger.debug("o15Idx.length after allocation "
					+ ArrayUtils.getLength(o15Idx));
		}
		else {
			logger.debug("o15Idx.length after allocation "
					+ ArrayUtils.getLength(o15Idx) + " - NONZERO!!!");
		}
		
		return tmpChiRec;
	}

	/**
	 * allocates the minimum number of children of each of the 3 types
	 * (U15Child,Stdent,O15Child) to households. This method actually assigns
	 * household index and household type to the corresponding records of these
	 * children in individual pool.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param fchIdx
	 *            indices of females of the current child type in the individual
	 *            pool.
	 * @param mchIdx
	 *            indices of males of the current child type in the individual
	 *            pool.
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param childType
	 *            index of this children type, 2 is U15Child, 3 is Student, 4 is
	 *            O15Child.
	 * @param usedChildIdx
	 *            index in the indivPool of individuals that have been
	 *            allocated.
	 * @return records of the minimum number of children of this type for all
	 *         applicable households in the household pool. Columns are [age,
	 *         gender, index of household relationship, index of household (in
	 *         nppInHhold), hhold category index].
	 */
	private int[][] generateRecordsMinimumChildren(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, int childType, int[] usedChildIdx) {

		logger.debug("CHILDREN TYPE " + childType);

		int[] fchIdx = null, mchIdx = null;
		switch (childType) {
		case 2:
			fchIdx = fu15Idx;
			mchIdx = mu15Idx;
			break;
		case 3:
			fchIdx = fstuIdx;
			mchIdx = mstuIdx;
			break;
		case 4:
			fchIdx = fo15Idx;
			mchIdx = mo15Idx;
			break;
		default:
			break;
		}

		int[][] tmpChiRec = null;
		MinIndividualsNote[] minIndivs = MinIndividualsNote.values();
//		household_relationship[] hhrel8 = household_relationship.values();
		ArrayHandler arrHdlr = new ArrayHandler();

		// get the index of hholds that have at least 1 childType and zero male
		int[] idxhh0M = getIdxOfHholdWithChildAndZeroMales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and zero
		// female
		int[] idxhh0F = getIdxOfHholdWithChildAndZeroFemales(nppInHhold, hhLog,
				childType);
		// get the index of hholds that have at least 1 childType and nonzero
		// female and nonzero male
		int[] idxhh = getIdxOfHholdWithChildAndNonZeroMalesAndNonZeroFemales(
				nppInHhold, hhLog, childType);

		int[] remIChi = ArrayUtils.addAll(idxhh, idxhh0M);
		remIChi = ArrayUtils.addAll(remIChi, idxhh0F);
		int remIChiLen = 0;
		if (remIChi != null) {
			remIChiLen = remIChi.length;
		}
		
		int flen = 0, mlen = 0;
		if (fchIdx != null) {
			flen = fchIdx.length;
		}
		if (mchIdx != null) {
			mlen = mchIdx.length;
		}
        logger.warn("remIChiLen=" + remIChiLen + ", fchIdx=" + flen
						+ ", mchIdx=" + mlen + "nppInHhold.length="
						+ nppInHhold.length);

		int[] usedi = null;
		int usediLen = 0;
		while (usediLen < remIChiLen) {
			if (fchIdx == null && mchIdx == null) {
                logger.warn("WARNING: No male nor female children of type "
								+ childType
								+ " found in the population! "
								+ "Aborting generateRecordsMinimumChildren");
				break;
			}

			// get elements in remIChi that are not in usedi
			int[] remi = arrHdlr.getNonCommonInArrays(remIChi, usedi);
			// if (remi==null) break;

			// picks out the household having the youngest parent
			int tmpimin = 0;
			for (int i = 0; i <= remi.length - 1; i++)
				if (hhLog[remi[i]][2] <= hhLog[remi[tmpimin]][2]) {
					tmpimin = i;
				}
			
			usedi = ArrayUtils.add(usedi, remi[tmpimin]);
			usediLen = usedi.length;

			int imin = ArrayUtils.indexOf(remIChi, remi[tmpimin]);
			int nM = hhLog[remIChi[imin]][0];
			int nF = hhLog[remIChi[imin]][1];
			int minParentAge = hhLog[remIChi[imin]][2];
			int maxParentAge = hhLog[remIChi[imin]][3];
			int iYParent = hhLog[remIChi[imin]][4];
			int iOParent = hhLog[remIChi[imin]][5];


			// the below check is unneccesary because functions that return
			// idxChi0M, idxChi0F and idxChi already did this check
			// if (nM<=0&&nF<=0) {
			// logger.debug("No male nor female children of type " +
			// childType + " found in household " + remIChi[imin] +
			// "! Aborting generateRecordsMinimumChildren");
			// continue;
			// }

			int[][] tmpRec = new int[1][indivPool[0].length + 2];
			tmpRec[0] = arrHdlr.makeUniformArray(tmpRec[0].length, -1);

			boolean pickMale = false;
			boolean pickFemale = false;
			int fChildIdx = -1;
			int mChildIdx = -1;
			if (mchIdx == null && fchIdx != null) {
				int[] dAgeParFChi = this.makedAgeParentChild(fchIdx, indivPool,
						minParentAge);
				fChildIdx = arrHdlr.getIdxOfNextGreaterValue(dAgeParFChi,
						min_dAgeParentChild);
				if (fChildIdx >= 0) {
					pickFemale = true;
				}
				else {
					// get the index of max in dAgeParChi, ie the index of the
					// youngest child
					int iYoungest = arrHdlr.getIndexOfMax(dAgeParFChi);
					indivPool[fchIdx[iYoungest]][0] = this
							.adjustToYoungestInAgeGroup(indivPool[fchIdx[iYoungest]][0]);
					int newMaxParentAge = this
							.adjustToEldestInAgeGroup(maxParentAge);
					// if (newMaxParentAge - indivPool[fchIdx[iYoungest]][0] >=
					// min_dAgeParentChild) {
					fChildIdx = iYoungest;
					pickFemale = true;
					// correcting age of parents
					indivPool[iYParent][0] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					indivPool[iOParent][0] = newMaxParentAge;
					hhLog[remIChi[imin]][2] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					hhLog[remIChi[imin]][3] = newMaxParentAge;
					minParentAge = hhLog[remIChi[imin]][2];
					maxParentAge = hhLog[remIChi[imin]][3];
					// }
				}
			} else if (mchIdx != null && fchIdx == null) {
				int[] dAgeParMChi = this.makedAgeParentChild(mchIdx, indivPool,
						minParentAge);
				mChildIdx = arrHdlr.getIdxOfNextGreaterValue(dAgeParMChi,
						min_dAgeParentChild);
				if (mChildIdx >= 0) {
					pickMale = true;
				}
				else {
					// get the index of max in dAgeParChi, ie the index of the
					// youngest child
					int iYoungest = arrHdlr.getIndexOfMax(dAgeParMChi);
					indivPool[mchIdx[iYoungest]][0] = this
							.adjustToYoungestInAgeGroup(indivPool[mchIdx[iYoungest]][0]);
					int newMaxParentAge = this
							.adjustToEldestInAgeGroup(maxParentAge);
					// if (newMaxParentAge - indivPool[mchIdx[iYoungest]][0] >=
					// min_dAgeParentChild) {
					mChildIdx = iYoungest;
					pickMale = true;
					// correcting age of parents
					indivPool[iOParent][0] = newMaxParentAge;
					indivPool[iYParent][0] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					hhLog[remIChi[imin]][2] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					hhLog[remIChi[imin]][3] = newMaxParentAge;
					minParentAge = hhLog[remIChi[imin]][2];
					maxParentAge = hhLog[remIChi[imin]][3];
					// }
				}
			} else if (mchIdx != null && fchIdx != null) {
				int[] dAgeParFChi = this.makedAgeParentChild(fchIdx, indivPool,
						minParentAge);
				fChildIdx = arrHdlr.getIdxOfNextGreaterValue(dAgeParFChi,
						min_dAgeParentChild);
				int[] dAgeParMChi = this.makedAgeParentChild(mchIdx, indivPool,
						minParentAge);
				mChildIdx = arrHdlr.getIdxOfNextGreaterValue(dAgeParMChi,
						min_dAgeParentChild);
				if (mChildIdx >= 0) {
					if (fChildIdx >= 0
							&& indivPool[fchIdx[fChildIdx]][0] > indivPool[mchIdx[mChildIdx]][0]){
						pickFemale = true;
					}
					else{
						pickMale = true;
					}
				}
				else if (fChildIdx >= 0){
					pickFemale = true;
				}
				else if (arrHdlr.max(dAgeParFChi) > arrHdlr.max(dAgeParMChi)) {
					// get the index of max in dAgeParChi, ie the index of the
					// youngest child
					int iYoungest = arrHdlr.getIndexOfMax(dAgeParFChi);
					indivPool[fchIdx[iYoungest]][0] = this
							.adjustToYoungestInAgeGroup(indivPool[fchIdx[iYoungest]][0]);
					int newMaxParentAge = this
							.adjustToEldestInAgeGroup(maxParentAge);
					// if (newMaxParentAge - indivPool[fchIdx[iYoungest]][0] >=
					// min_dAgeParentChild) {
					fChildIdx = iYoungest;
					pickFemale = true;
					// correcting age of parents
					indivPool[iOParent][0] = newMaxParentAge;
					indivPool[iYParent][0] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					hhLog[remIChi[imin]][2] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					hhLog[remIChi[imin]][3] = newMaxParentAge;
					minParentAge = hhLog[remIChi[imin]][2];
					maxParentAge = hhLog[remIChi[imin]][3];
					// }
				} else {
					// get the index of max in dAgeParChi, ie the index of the
					// youngest child
					int iYoungest = arrHdlr.getIndexOfMax(dAgeParMChi);
					indivPool[mchIdx[iYoungest]][0] = this
							.adjustToYoungestInAgeGroup(indivPool[mchIdx[iYoungest]][0]);
					int newMaxParentAge = this
							.adjustToEldestInAgeGroup(maxParentAge);
					// if (newMaxParentAge - indivPool[mchIdx[iYoungest]][0] >=
					// min_dAgeParentChild) {
					mChildIdx = iYoungest;
					pickMale = true;
					// correcting age of parents
					indivPool[iOParent][0] = newMaxParentAge;
					indivPool[iYParent][0] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					hhLog[remIChi[imin]][2] = minParentAge
							+ (newMaxParentAge - maxParentAge);
					hhLog[remIChi[imin]][3] = newMaxParentAge;
					minParentAge = hhLog[remIChi[imin]][2];
					maxParentAge = hhLog[remIChi[imin]][3];
					// }
				}
			}
			if (pickMale) {
				if (nM == 0) { // note that nF must already be >0 thanks to
								// checking (nM<=0&&nF<=0) done above
					// swap 1 female to male
					hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] + 1;
					hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] - 1;
					nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] + 1;
					nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] - 1;
					// then reassign values to nM and nF
					nM = hhLog[remIChi[imin]][0];
					nF = hhLog[remIChi[imin]][1];
				}
				for (int i = 0; i <= indivPool[0].length - 1; i++) {
					tmpRec[0][i] = indivPool[mchIdx[mChildIdx]][i];
				}
				tmpRec[0][indivPool[0].length] = remIChi[imin];
				tmpRec[0][indivPool[0].length + 1] = nppInHhold[remIChi[imin]][0];
				// update nM
				nM = nM - 1;
				// update hhLog
				hhLog[remIChi[imin]][0] = nM;
				// add index (in indivPool) of the child just allocated to the
				// list of used children.
				usedChildIdx = ArrayUtils.add(usedChildIdx, mchIdx[mChildIdx]);
				// update mchIdx
				mchIdx = ArrayUtils.remove(mchIdx, mChildIdx);

			}
			if (pickFemale) {
				// note that nM must already be >0 thanks to
				// checking (nM<=0&&nF<=0) done above
				if (nF == 0) { 
					// swap 1 male to female
					hhLog[remIChi[imin]][0] = hhLog[remIChi[imin]][0] - 1;
					hhLog[remIChi[imin]][1] = hhLog[remIChi[imin]][1] + 1;
					nppInHhold[remIChi[imin]][7] = nppInHhold[remIChi[imin]][7] - 1;
					nppInHhold[remIChi[imin]][8] = nppInHhold[remIChi[imin]][8] + 1;
					// then reassign values to nM and nF
					nM = hhLog[remIChi[imin]][0];
					nF = hhLog[remIChi[imin]][1];
				}
				for (int i = 0; i <= indivPool[0].length - 1; i++) {
					tmpRec[0][i] = indivPool[fchIdx[fChildIdx]][i];
				}
				tmpRec[0][indivPool[0].length] = remIChi[imin];
				tmpRec[0][indivPool[0].length + 1] = nppInHhold[remIChi[imin]][0];
				// update nF
				nF = nF - 1;
				// update hhLog
				hhLog[remIChi[imin]][1] = nF;
				// add index (in indivPool) of the child just allocated to the
				// list of used children.
				usedChildIdx = ArrayUtils.add(usedChildIdx, fchIdx[fChildIdx]);
				// update fchIdx
				fchIdx = ArrayUtils.remove(fchIdx, fChildIdx);
			}

			if (tmpRec[0][0] != -1) {
				tmpChiRec = arrHdlr.concateMatrices(tmpChiRec, tmpRec);
			} else {
                logger.warn("WARNING: Cannot pick any male nor female children of type "
								+ childType
								+ " satisfying min_dAgeParentChild! "
								+ "Aborting generateRecordsMinimumChildren ("
								+ minIndivs[nppInHhold[remIChi[imin]][0]]
										.toString() + ")");
				hhLog[remIChi[imin]] = arrHdlr.makeUniformArray(
						hhLog[remIChi[imin]].length, -1);
			}
		}

		switch (childType) {
		case 2:
			fu15Idx = fchIdx;
			mu15Idx = mchIdx;
			break;
		case 3:
			fstuIdx = fchIdx;
			mstuIdx = mchIdx;
			break;
		case 4:
			fo15Idx = fchIdx;
			mo15Idx = mchIdx;
			break;
		default:
			break;
		}

		// flen = 0; mlen = 0;
		// if (fchIdx != null)
		// flen = fchIdx.length;
		// if (mchIdx != null)
		// mlen = mchIdx.length;
		// logger.debug(
		// "End of generateRecordsMinimumChildren" + ", fchIdx=" + flen
		// + ", mchIdx=" + mlen);

		return tmpChiRec;
	}

	/**
	 * changes an input age to the lower bound of the age group that input age
	 * belongs to. Age groups are
	 * {0,14},{15,24},{25,34},{35,44},{45,54},{55,64},{65,74},{75,84},{85,100}.
	 * 
	 * @param crnAge
	 * @return int value of the lower bound of the age group the crnAge belongs
	 *         to
	 */
	private int adjustToYoungestInAgeGroup(int crnAge) {
		int newAge = crnAge;
		int[][] ageGroups = new int[][] { { 0, 14 }, { 15, 24 }, { 25, 34 },
				{ 35, 44 }, { 45, 54 }, { 55, 64 }, { 65, 74 }, { 75, 84 },
				{ 85, 100 } };
		for (int i = ageGroups.length - 1; i >= 0; i--) {
			if (crnAge >= ageGroups[i][0]) {
				newAge = ageGroups[i][0];
				break;
			}
		}
		
		return newAge;
	}

	/**
	 * changes an input age to the upper bound of the age group that input age
	 * belongs to. Age groups are
	 * {0,14},{15,24},{25,34},{35,44},{45,54},{55,64},{65,74},{75,84},{85,100}.
	 * 
	 * @param crnAge
	 * @return int value of the upper bound of the age group the crnAge belongs
	 *         to
	 */
	private int adjustToEldestInAgeGroup(int crnAge) {
		int newAge = crnAge;
		int[][] ageGroups = new int[][] { { 0, 14 }, { 15, 24 }, { 25, 34 },
				{ 35, 44 }, { 45, 54 }, { 55, 64 }, { 65, 74 }, { 75, 84 },
				{ 85, 100 } };
		for (int i = ageGroups.length - 1; i >= 0; i--){
			if (crnAge >= ageGroups[i][0]) {
				newAge = ageGroups[i][1];
				break;
			}
		}
		
		return newAge;
	}

	/**
	 * gets indices of households in the household pool that have 1 or 2 parents
	 * and nonzero males and nonzero females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param nParents
	 *            number of parents, either 1 or 2.
	 * @return indices of households in household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdNonZeroMaleAndFemaleParents(int[][] nppInHhold,
			int nParents) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][1] == nParents && nppInHhold[ihh][7] > 0
					&& nppInHhold[ihh][8] > 0){
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		
		return finIdx;
	}

	/**
	 * gets indices of households in the houehold pool having 1 or 2 parents and
	 * zero males and nonzero females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param nParents
	 *            number of parents, either 1 or 2.
	 * @return indices of households in household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdZeroMaleParents(int[][] nppInHhold, int nParents) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][1] == nParents && nppInHhold[ihh][7] == 0
					&& nppInHhold[ihh][8] > 0) {
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		return finIdx;
	}

	/**
	 * gets indices of households in the houehold pool having 1 or 2 parents and
	 * nonzero males and zero females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param nParents
	 *            number of parents, either 1 or 2.
	 * @return indices of households in household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdZeroFemaleParents(int[][] nppInHhold,
			int nParents) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][1] == nParents && nppInHhold[ihh][7] > 0
					&& nppInHhold[ihh][8] == 0) {
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		
		return finIdx;
	}

	/**
	 * gets the indices of households that require at least 1 individual of type
	 * either Relative/NonRelative or GroupHhold and have zero males and nonzero
	 * females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param indivType
	 *            index of this person type, 5 is Relative/NonRelative, 6 is
	 *            GroupHhold.
	 * @return indices of households in the household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdWithRelOrGroupHholdAndZeroMales(
			int[][] nppInHhold, int[][] hhLog, int indivType) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++){
			if (nppInHhold[ihh][indivType] >= 2 && hhLog[ihh][0] == 0
					&& hhLog[ihh][1] > 0){
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		
		return finIdx;
	}

	/**
	 * gets the indices of households that require at least 1 individual of type
	 * either Relative/NonRelative or GroupHhold and have nonzero males and zero
	 * females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param indivType
	 *            index of this person type, 5 is Relative/NonRelative, 6 is
	 *            GroupHhold.
	 * @return indices of households in the household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdWithRelOrGroupHholdAndZeroFemales(
			int[][] nppInHhold, int[][] hhLog, int indivType) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++){
			if (nppInHhold[ihh][indivType] >= 2 && hhLog[ihh][1] == 0
					&& hhLog[ihh][0] > 0){
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		
		return finIdx;
	}

	/**
	 * gets the indices of households that require at least 1 individual of type
	 * either Relative/NonRelative or GroupHhold and have nonzero males and
	 * nonzero females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param indivType
	 *            index of this person type, 5 is Relative/NonRelative, 6 is
	 *            GroupHhold.
	 * @return indices of households in the household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdWithRelOrGroupHholdAndNonZeroMalesAndNonZeroFemales(
			int[][] nppInHhold, int[][] hhLog, int indivType) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][indivType] >= 2 && hhLog[ihh][0] > 0
					&& hhLog[ihh][1] > 0) {
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		
		return finIdx;
	}

	/**
	 * gets the indices of households that require at least 1 child of type
	 * U15Child or Student or O15Child and having zero males and nonzero
	 * females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param childType
	 *            index of this children type, 2 is U15Child, 3 is Student, 4 is
	 *            O15Child.
	 * @return indices of households in the household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdWithChildAndZeroMales(int[][] nppInHhold,
			int[][] hhLog, int childType) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][childType] > 0 && hhLog[ihh][0] == 0
					&& hhLog[ihh][1] > 0) {
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		
		return finIdx;
	}

	/**
	 * gets the indices of households that require at least 1 child of type
	 * U15Child or Student or O15Child and having nonzero males and zero
	 * females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param childType
	 *            index of this children type, 2 is U15Child, 3 is Student, 4 is
	 *            O15Child.
	 * @return indices of households in the household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdWithChildAndZeroFemales(int[][] nppInHhold,
			int[][] hhLog, int childType) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][childType] > 0 && hhLog[ihh][1] == 0
					&& hhLog[ihh][0] > 0) {
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		return finIdx;
	}

	/**
	 * gets the indices of households that require at least 1 child of type
	 * U15Child or Student or O15Child and having nonzero males and nonzero
	 * females.
	 * 
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @param childType
	 *            index of this children type, 2 is U15Child, 3 is Student, 4 is
	 *            O15Child.
	 * @return indices of households in the household pool satisfying the above
	 *         conditions.
	 */
	private int[] getIdxOfHholdWithChildAndNonZeroMalesAndNonZeroFemales(
			int[][] nppInHhold, int[][] hhLog, int childType) {
		int[] finIdx = null;

		for (int ihh = 0; ihh <= nppInHhold.length - 1; ihh++) {
			if (nppInHhold[ihh][childType] > 0 && hhLog[ihh][0] > 0
					&& hhLog[ihh][1] > 0) {
				finIdx = ArrayUtils.add(finIdx, ihh);
			}
		}
		
		return finIdx;
	}

	/**
	 * allocates LoneParents individuals into households having 1 parent.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household. Columns in each record are [hhold
	 *            category index, min residents, number of households of the
	 *            same category, number of males, number of females, total
	 *            residents].
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @return records of LoneParent individuals in all applicable households in
	 *         the household pool. Columns are [age, gender, index of household
	 *         relationship, index of household (in nppInHhold), hhold category
	 *         index].
	 */
	private int[][] generateRecordsLoneParents(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog, Random random) {

		// logger.debug("LONE PARENTS");

		MinIndividualsNote[] minIndivs = MinIndividualsNote.values();
//		household_relationship[] hhrel8 = household_relationship.values();
		ArrayHandler arrHdlr = new ArrayHandler();
		int[][] tmp1ParRec = null;

		// - get indices (in nppInHhold) of hholds having only 1 parent and
		// having zero males
		int[] idx1P0M = this.getIdxOfHholdZeroMaleParents(nppInHhold, 1);
		// - get indices (in nppInHhold) of hholds having only 1 parent and
		// having zero females
		int[] idx1P0F = this.getIdxOfHholdZeroFemaleParents(nppInHhold, 1);
		// get the index of hholds having 1 parent and having nonzero males and
		// nonzero females
		int[] idx1P = this.getIdxOfHholdNonZeroMaleAndFemaleParents(nppInHhold,
				1);

		int len0M = 0, len0F = 0, lenMF = 0, lenp1f = 0, lenp1m = 0;
		if (idx1P0M != null) {
			len0M = idx1P0M.length;
		}
		if (idx1P0F != null) {
			len0F = idx1P0F.length;
		}
		if (idx1P != null) {
			lenMF = idx1P.length;
		}
		if (p1fIdx != null) {
			lenp1f = p1fIdx.length;
		}
		if (p1mIdx != null) {
			lenp1m = p1mIdx.length;
		}

		// logger.debug("len0M=" + len0M + "\n" +
		// "len0F=" + len0F + "\n" +
		// "lenMF=" + lenMF + "\n" +
		// "lenp1f=" + lenp1f + "\n" +
		// "lenp1m=" + lenp1m + "\n\n");

		if (idx1P0M != null && p1fIdx != null && idx1P0M.length > p1fIdx.length) {
			len0F = 0;
			int len = 0;
			if (idx1P0F != null) {
				len0F = idx1P0F.length;
			}
			if (idx1P != null) {
				len = idx1P.length;
			}
			if (lenp1f == 0) {
				// remove 1 female and add 1 male in each of idx1p0M household
				for (int i = 0; i <= len0M - 1; i++) {
					nppInHhold[idx1P0M[i]][7] = nppInHhold[idx1P0M[i]][7] + 1;
					nppInHhold[idx1P0M[i]][8] = nppInHhold[idx1P0M[i]][8] - 1;
					hhLog[idx1P0M[i]][0] = nppInHhold[idx1P0M[i]][7];
					hhLog[idx1P0M[i]][1] = nppInHhold[idx1P0M[i]][8];
				}
			}
			
			if (len0F + len + (len0M - lenp1f) <= lenp1m) {
				// logger.debug("OK");
				for (int i = lenp1f; i <= len0M - 1; i++) {
					nppInHhold[idx1P0M[i]][7] = nppInHhold[idx1P0M[i]][7] + 1;
					nppInHhold[idx1P0M[i]][8] = nppInHhold[idx1P0M[i]][8] - 1;
					hhLog[idx1P0M[i]][0] = nppInHhold[idx1P0M[i]][7];
					hhLog[idx1P0M[i]][1] = nppInHhold[idx1P0M[i]][8];
				}
			}
		} else if (len0F > lenp1m) {// if (idx1P0F!=null && p1mIdx!=null &&
									// idx1P0F.length>p1mIdx.length) {
			len0M = 0;
			int len = 0;
			if (idx1P0M != null) {
				len0M = idx1P0M.length;
			}
			if (idx1P != null) {
				len = idx1P.length;
			}
			if (lenp1m == 0) {
				// remove 1 male and add 1 female in each of idx1p0F household
				for (int i = 0; i <= len0F - 1; i++) {
					nppInHhold[idx1P0F[i]][7] = nppInHhold[idx1P0F[i]][7] - 1;
					nppInHhold[idx1P0F[i]][8] = nppInHhold[idx1P0F[i]][8] + 1;
					hhLog[idx1P0F[i]][0] = nppInHhold[idx1P0F[i]][7];
					hhLog[idx1P0F[i]][1] = nppInHhold[idx1P0F[i]][8];
				}
			}
			
			if (len0M + len + (len0F - lenp1m) <= lenp1f) {
				// logger.debug("OK");
				for (int i = lenp1m; i <= len0F - 1; i++) {
					nppInHhold[idx1P0F[i]][7] = nppInHhold[idx1P0F[i]][7] - 1;
					nppInHhold[idx1P0F[i]][8] = nppInHhold[idx1P0F[i]][8] + 1;
					hhLog[idx1P0F[i]][0] = nppInHhold[idx1P0F[i]][7];
					hhLog[idx1P0F[i]][1] = nppInHhold[idx1P0F[i]][8];
				}
			}
		}
		// get indices (in nppInHhold) of hholds having only 1 parent and having
		// zero males AGAIN
		idx1P0M = this.getIdxOfHholdZeroMaleParents(nppInHhold, 1);
		// get indices (in nppInHhold) of hholds having only 1 parent and having
		// zero females AGAIN
		idx1P0F = this.getIdxOfHholdZeroFemaleParents(nppInHhold, 1);
		// get the index of hholds having 1 parent and having nonzero males and
		// nonzero females AGAIN
		idx1P = this.getIdxOfHholdNonZeroMaleAndFemaleParents(nppInHhold, 1);

		len0M = 0;
		len0F = 0;
		lenMF = 0;
		lenp1f = 0;
		lenp1m = 0;
		if (idx1P0M != null) {
			len0M = idx1P0M.length;
		}
		if (idx1P0F != null) {
			len0F = idx1P0F.length;
		}
		if (idx1P != null) {
			lenMF = idx1P.length;
		}
		if (p1fIdx != null) {
			lenp1f = p1fIdx.length;
		}
		if (p1mIdx != null) {
			lenp1m = p1mIdx.length;
		}

		// logger.debug("len0M=" + len0M + "\n" +
		// "len0F=" + len0F + "\n" +
		// "lenMF=" + lenMF + "\n" +
		// "lenp1f=" + lenp1f + "\n" +
		// "lenp1m=" + lenp1m + "\n");

		if (idx1P0M != null) {
			for (int tmpi = 0; tmpi <= idx1P0M.length - 1; tmpi++) {
				if (p1fIdx == null) {
					logger.warn("WARNING: cannot generate parent for record "
									+ idx1P0M[tmpi]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx1P0M[tmpi]][0]]
											.toString()
									+ ") ! No female lone parent individuals found in indivPool!");
					hhLog[idx1P0M[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idx1P0M[tmpi]].length, -1);
					continue;
					// hhLog[idx1P0M[tmpi]][0] = hhLog[idx1P0M[tmpi]][0]+1;
					// hhLog[idx1P0M[tmpi]][1] = hhLog[idx1P0M[tmpi]][1]-1;
					// if (hhLog[idx1P0M[tmpi]][1]==0) idx1P0F =
					// ArrayUtils.add(idx1P0F, idx1P0M[tmpi]);
					// else idx1P = ArrayUtils.add(idx1P, idx1P0M[tmpi]);
					// continue;
				}
				int nM = hhLog[idx1P0M[tmpi]][0];
				int nF = hhLog[idx1P0M[tmpi]][1];
				// randomly pick a parent
				int[] parentIdx = arrHdlr.pickRandomFromArray(p1fIdx, null, 1,
						random);
				// now generate a record for this parent
				int[][] tmpRec = new int[1][indivPool[0].length + 2];
				for (int i = 0; i <= indivPool[0].length - 1; i++) {
					tmpRec[0][i] = indivPool[parentIdx[0]][i];
				}
				
				// index of the hhold
				tmpRec[0][indivPool[0].length] = idx1P0M[tmpi]; 
				// index of hhold type
				tmpRec[0][indivPool[0].length + 1] = nppInHhold[idx1P0M[tmpi]][0]; 
				
				tmp1ParRec = arrHdlr.concateMatrices(tmp1ParRec, tmpRec);
				// update nF
				nF = nF - 1;
				// update p1fIdx
				p1fIdx = ArrayUtils.remove(p1fIdx,
						ArrayUtils.indexOf(p1fIdx, parentIdx[0]));
				// update hhLog
				hhLog[idx1P0M[tmpi]][0] = nM;
				hhLog[idx1P0M[tmpi]][1] = nF;
				hhLog[idx1P0M[tmpi]][2] = tmpRec[0][0];
				hhLog[idx1P0M[tmpi]][3] = tmpRec[0][0];
				hhLog[idx1P0M[tmpi]][4] = parentIdx[0];
				hhLog[idx1P0M[tmpi]][5] = parentIdx[0];

				// logger.debug(idx1P0M[tmpi] + ", " +
				// hhLog[idx1P0M[tmpi]][2] + " --- " + tmpRec[0][0]);

				// for (int myi=0; myi<=tmpRec.length-1; myi++)
				// logger.debug(tmpRec[myi][0] + ", " + tmpRec[myi][1] +
				// ", " + hhrel8[tmpRec[myi][2]] + ", " +
				// tmpRec[myi][3] + ", " +
				// minIndivs[tmpRec[myi][4]].toString());
			}
		}

		if (idx1P0F != null) {
			for (int tmpi = 0; tmpi <= idx1P0F.length - 1; tmpi++) {
				if (p1mIdx == null) {
                    logger.warn("WARNING: cannot generate parent for record "
									+ idx1P0F[tmpi]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx1P0F[tmpi]][0]]
											.toString()
									+ ") ! No male lone parent individuals found in indivPool!");
					hhLog[idx1P0F[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idx1P0F[tmpi]].length, -1);
					continue;
				}
				int nM = hhLog[idx1P0F[tmpi]][0];
				int nF = hhLog[idx1P0F[tmpi]][1];
				// randomly pick a parent
				int[] parentIdx = arrHdlr.pickRandomFromArray(p1mIdx, null, 1,
						random);
				// now generate a record for this parent
				int[][] tmpRec = new int[1][indivPool[0].length + 2];
				for (int i = 0; i <= indivPool[0].length - 1; i++) {
					tmpRec[0][i] = indivPool[parentIdx[0]][i];
				}
				// index of the hhold
				tmpRec[0][indivPool[0].length] = idx1P0F[tmpi]; 
				// index of hhold type
				tmpRec[0][indivPool[0].length + 1] = nppInHhold[idx1P0F[tmpi]][0];
				
				tmp1ParRec = arrHdlr.concateMatrices(tmp1ParRec, tmpRec);
				// update nM
				nM = nM - 1;
				// update p1mIdx
				p1mIdx = ArrayUtils.remove(p1mIdx,
						ArrayUtils.indexOf(p1mIdx, parentIdx[0]));
				// update hhLog
				hhLog[idx1P0F[tmpi]][0] = nM;
				hhLog[idx1P0F[tmpi]][1] = nF;
				hhLog[idx1P0F[tmpi]][2] = tmpRec[0][0];
				hhLog[idx1P0F[tmpi]][3] = tmpRec[0][0];
				hhLog[idx1P0F[tmpi]][4] = parentIdx[0];
				hhLog[idx1P0F[tmpi]][5] = parentIdx[0];
			}
		}
		
		if (idx1P != null) {
			for (int tmpi = 0; tmpi <= idx1P.length - 1; tmpi++) {
				int nM = hhLog[idx1P[tmpi]][0];
				int nF = hhLog[idx1P[tmpi]][1];
				if (p1mIdx == null && p1fIdx == null) {
                    logger.warn("WARNING: cannot generate parents for record "
									+ tmpi
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx1P[tmpi]][0]]
											.toString()
									+ ") ! No lone parent individuals found in indivPool!");
					hhLog[idx1P[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idx1P[tmpi]].length, -1);
					continue;
				}
				if ((nM > 0 && p1mIdx != null) || (nF > 0 && p1fIdx != null)) {
					int[][] tmpRec = new int[1][indivPool[0].length + 2];
					ArrayList<int[]> parentIdxList = new ArrayList<>();
					
					if ((nM > nF || (nF >= nM && p1fIdx == null))
							&& p1mIdx != null) {
						// randomly pick a parent
						int[] tempParentIdx = arrHdlr.pickRandomFromArray(p1mIdx, null,
								1, random);
						// update nM
						nM = nM - 1;
						// update pm1Idx
						p1mIdx = ArrayUtils.remove(p1mIdx,
								ArrayUtils.indexOf(p1mIdx, tempParentIdx[0]));
						
						parentIdxList.add(tempParentIdx);
						
					} else if ((nF >= nM || (nM > nF && p1mIdx == null))
							&& p1fIdx != null) {
						// randomly pick a parent
						int[] tempParentIdx = arrHdlr.pickRandomFromArray(p1fIdx, null,
								1, random);
						// update nF
						nF = nF - 1;
						// update pf1Idx
						p1fIdx = ArrayUtils.remove(p1fIdx,
								ArrayUtils.indexOf(p1fIdx, tempParentIdx[0]));
						
						parentIdxList.add(tempParentIdx);
					}
					
					int[] parentIdx = parentIdxList.get(0);
					parentIdxList = null;
					
					// now generate a record for this parent
					for (int i = 0; i <= indivPool[0].length - 1; i++){
						tmpRec[0][i] = indivPool[parentIdx[0]][i];
					}
					
					// index of the hhold
					tmpRec[0][indivPool[0].length] = idx1P[tmpi];
					// index of hhold type
					tmpRec[0][indivPool[0].length + 1] = nppInHhold[idx1P[tmpi]][0]; 
					
					tmp1ParRec = arrHdlr.concateMatrices(tmp1ParRec, tmpRec);
					// update hhLog
					hhLog[idx1P[tmpi]][0] = nM;
					hhLog[idx1P[tmpi]][1] = nF;
					hhLog[idx1P[tmpi]][2] = tmpRec[0][0];
					hhLog[idx1P[tmpi]][3] = tmpRec[0][0];
					hhLog[idx1P[tmpi]][4] = parentIdx[0];
					hhLog[idx1P[tmpi]][5] = parentIdx[0];
					
				} else {
                    logger.warn("WARNING: cannot generate parent for record "
									+ idx1P[tmpi]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx1P[tmpi]][0]]
											.toString()
									+ ") ! No lone parent individuals found in indivPool or gender not satisfied!");
					hhLog[idx1P[tmpi]] = arrHdlr.makeUniformArray(
							hhLog[idx1P[tmpi]].length, -1);
					continue;
				}

			}
		}

		return tmp1ParRec;
	}


	/**
	 * allocates Married/DeFacto individuals as parents into households having 2
	 * parents.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param nppInHhold
	 *            pool of households. This is a 2D integer array. Each record
	 *            represents 1 household.
	 * @param hhLog
	 *            log of the process of allocating individuals into households.
	 *            The number of rows is the number of households in household
	 *            pool. The columns are [number of males remaining in this
	 *            household, number of females remaining in this household, age
	 *            of the younger parent in this household, age of the elder
	 *            parent in this household, index of the younger parent in this
	 *            household in individual pool, index of the elder parent in
	 *            this household in individual pool].
	 * @return records of Married/DeFacto individuals as parents in all
	 *         applicable households in the household pool. Columns are [age,
	 *         gender, index of household relationship, index of household (in
	 *         nppInHhold), hhold category index].
	 */
	private int[][] generateRecordsCoupleParents(int[][] indivPool,
			int[][] nppInHhold, int[][] hhLog) {

		// logger.debug("COUPLED PARENTS");

		ArrayHandler arrHdlr = new ArrayHandler();
		MinIndividualsNote[] minIndivs = MinIndividualsNote.values();
//		household_relationship[] hhrel8 = household_relationship.values();
		int[][] tmpParRec = null;

		// - get indices (in nppInHhold) of hholds having 2 parents and having
		// zero males
		int[] idx2P0M = this.getIdxOfHholdZeroMaleParents(nppInHhold, 2);
		// - get indices (in nppInHhold) of hholds having 2 parents and having
		// zero females
		int[] idx2P0F = this.getIdxOfHholdZeroFemaleParents(nppInHhold, 2);
		// get the index of hholds having 2 parents and having nonzero males and
		// nonzero females
		int[] idx2P = this.getIdxOfHholdNonZeroMaleAndFemaleParents(nppInHhold,
				2);

		// START CORRECTING idx2P0M, idx2P0F, idx2P
		// =========================================================================================================================
		int pfIdxlen = 0;
		int pmIdxlen = 0;
		int len2P0M = 0;
		int len2P0F = 0;
		int len2P = 0;
		if (pfIdx != null) {
			pfIdxlen = pfIdx.length;
		}
		if (pmIdx != null) {
			pmIdxlen = pmIdx.length;
		}
		if (idx2P0M != null) {
			len2P0M = idx2P0M.length;
		}
		if (idx2P0F != null) {
			len2P0F = idx2P0F.length;
		}
		if (idx2P != null) {
			len2P = idx2P.length;
		}

		// get the minimum number of female required for idx2P0M
		int minreqF = getMinPersonsRequired(nppInHhold, idx2P0M, 1);
		// get the minimum number of male required for idx2P0F
		int minreqM = getMinPersonsRequired(nppInHhold, idx2P0F, 1);

		if (minreqF > pfIdxlen) {
			while (minreqF > pfIdxlen) {
				// pick out 0M hhold that has most females
				int tmpiMax = 0;
				for (int i = 0; i <= idx2P0M.length - 1; i++) {
					if (hhLog[idx2P0M[i]][1] > hhLog[idx2P0M[tmpiMax]][1]) {
						tmpiMax = i;
					}
				}
				
				// minus 1 female and plus 1 male in the hhLog record as well as
				// nppInHhold record
				hhLog[idx2P0M[tmpiMax]][0] = hhLog[idx2P0M[tmpiMax]][0] + 1;
				hhLog[idx2P0M[tmpiMax]][1] = hhLog[idx2P0M[tmpiMax]][1] - 1;
				nppInHhold[idx2P0M[tmpiMax]][7] = nppInHhold[idx2P0M[tmpiMax]][7] + 1;
				nppInHhold[idx2P0M[tmpiMax]][8] = nppInHhold[idx2P0M[tmpiMax]][8] - 1;
				// get new idxRel0M, idxRel0F
				idx2P0M = this.getIdxOfHholdZeroMaleParents(nppInHhold, 2);
				idx2P0F = this.getIdxOfHholdZeroFemaleParents(nppInHhold, 2);
				// get new minreqF and new minreqM
				minreqM = getMinPersonsRequired(nppInHhold, idx2P0F, 1);
				minreqF = getMinPersonsRequired(nppInHhold, idx2P0M, 1);
				// now check and correct if number of males are not ok
				boolean testFailed = false;
				while (minreqM > pmIdxlen) {
					// pick out the 0F hhold that has at least 2 males
					tmpiMax = 0;
					for (int i = 0; i <= idx2P0F.length - 1; i++) {
						if (hhLog[idx2P0F[i]][0] > hhLog[idx2P0F[tmpiMax]][0]) {
							tmpiMax = i;
						}
					}
					// if not found
					if (hhLog[idx2P0F[tmpiMax]][0] < 2) { 
						testFailed = true;
						break;
					}
					// minus 1 male and plus 1 female in the hhLog record
					hhLog[idx2P0F[tmpiMax]][0] = hhLog[idx2P0F[tmpiMax]][0] - 1;
					hhLog[idx2P0F[tmpiMax]][1] = hhLog[idx2P0F[tmpiMax]][1] + 1;
					nppInHhold[idx2P0F[tmpiMax]][7] = nppInHhold[idx2P0F[tmpiMax]][7] - 1;
					nppInHhold[idx2P0F[tmpiMax]][8] = nppInHhold[idx2P0F[tmpiMax]][8] + 1;
					// get new idxRel0M
					idx2P0M = this.getIdxOfHholdZeroMaleParents(nppInHhold, 2);
					// get new minreqM
					minreqM = getMinPersonsRequired(nppInHhold, idx2P0F, 1);
				}
				if (testFailed) {
					break;
				}
			}
		} else {
			while (minreqM > pmIdxlen) {
				// pick out 0F hhold that has most males
				int tmpiMax = 0;
				for (int i = 0; i <= idx2P0F.length - 1; i++) {
					if (hhLog[idx2P0F[i]][0] > hhLog[idx2P0F[tmpiMax]][0]) {
						tmpiMax = i;
					}
				}
				// minus 1 male and plus 1 female in the hhLog record as well as
				// in nppInHhold
				hhLog[idx2P0F[tmpiMax]][0] = hhLog[idx2P0F[tmpiMax]][0] - 1;
				hhLog[idx2P0F[tmpiMax]][1] = hhLog[idx2P0F[tmpiMax]][1] + 1;
				nppInHhold[idx2P0F[tmpiMax]][7] = nppInHhold[idx2P0F[tmpiMax]][7] - 1;
				nppInHhold[idx2P0F[tmpiMax]][8] = nppInHhold[idx2P0F[tmpiMax]][8] + 1;
				// get new idxRel0M, idxRel0F
				idx2P0M = this.getIdxOfHholdZeroMaleParents(nppInHhold, 2);
				idx2P0F = this.getIdxOfHholdZeroFemaleParents(nppInHhold, 2);
				// get new minreqF and new minreqM
				minreqM = getMinPersonsRequired(nppInHhold, idx2P0F, 1);
				minreqF = getMinPersonsRequired(nppInHhold, idx2P0M, 1);
				// now check and correct if number of females are not ok
				boolean testFailed = false;
				while (minreqF > pfIdxlen) {
					// pick out the 0M hhold that has at least 2 females
					tmpiMax = 0;
					for (int i = 0; i <= idx2P0M.length - 1; i++) {
						if (hhLog[idx2P0M[i]][1] > hhLog[idx2P0M[tmpiMax]][1]) {
							tmpiMax = i;
						}
					}
					// if not found
					if (hhLog[idx2P0M[tmpiMax]][1] < 2) { 
						testFailed = true;
						break;
					}
					// minus 1 female and plus 1 male in the hhLog record
					hhLog[idx2P0M[tmpiMax]][0] = hhLog[idx2P0M[tmpiMax]][0] + 1;
					hhLog[idx2P0M[tmpiMax]][1] = hhLog[idx2P0M[tmpiMax]][1] - 1;
					nppInHhold[idx2P0M[tmpiMax]][7] = nppInHhold[idx2P0M[tmpiMax]][7] + 1;
					nppInHhold[idx2P0M[tmpiMax]][8] = nppInHhold[idx2P0M[tmpiMax]][8] - 1;
					// get new idxRel0F
					idx2P0F = this
							.getIdxOfHholdZeroFemaleParents(nppInHhold, 2);
					// get new minreqF
					minreqF = getMinPersonsRequired(nppInHhold, idx2P0M, 1);
				}
				if (testFailed) {
					break;
				}
			}
		}
		idx2P0M = this.getIdxOfHholdZeroMaleParents(nppInHhold, 2);
		idx2P0F = this.getIdxOfHholdZeroFemaleParents(nppInHhold, 2);
		idx2P = this.getIdxOfHholdNonZeroMaleAndFemaleParents(nppInHhold, 2);
		pfIdxlen = 0;
		pmIdxlen = 0;
		len2P0M = 0;
		len2P0F = 0;
		len2P = 0;
		if (pfIdx != null) {
			pfIdxlen = pfIdx.length;
		}
		if (pmIdx != null) {
			pmIdxlen = pmIdx.length;
		}
		if (idx2P0M != null) {
			len2P0M = idx2P0M.length;
		}
		if (idx2P0F != null) {
			len2P0F = idx2P0F.length;
		}
		if (idx2P != null) {
			len2P = idx2P.length;
		}
		// =========================================================================================================================
		// END CORRECTING idx2P0M, idx2P0F, idx2P
		// logger.debug("After correction: pmIdxlen=" + pmIdxlen +
		// ", pfIdxlen=" + pfIdxlen);

		if (idx2P0M != null) {
			// logger.debug("idx2P0M households: " + idx2P0M.length);
			for (int ihh = 0; ihh <= idx2P0M.length - 1; ihh++) {
				
				int nM = hhLog[idx2P0M[ihh]][0];
				int nF = hhLog[idx2P0M[ihh]][1];
				int[][] tmpRec = new int[2][indivPool[0].length];
				if (pfIdx.length == 0 || pfIdx.length < 2) {
					logger.warn("WARNING: cannot generate dAgeParents for record "
									+ idx2P0M[ihh]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx2P0M[ihh]][0]]
											.toString()
									+ ") ! Only 1 female parent individual found in indivPool!");
					hhLog[idx2P0M[ihh]] = arrHdlr.makeUniformArray(
							hhLog[idx2P0M[ihh]].length, -1);
					continue;
				}
				int[][] dAgeParents = this.makeMatrixOfdAgeParents(pfIdx,
						pfIdx, indivPool);
				// get the index of female and male parents that have dAge
				// closest to the mean_dAgeParents
				int[] parentsIdx = arrHdlr.getIndexofClosestToValue(
						dAgeParents, mean_dAgeParents, true);
				// now generate records for these parents and assign this hhold
				// index to them
				for (int j = 0; j <= 1; j++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++) {
						tmpRec[j][i] = indivPool[pfIdx[parentsIdx[j]]][i];
					}
				}
				// update nF
				nF = nF - 2;

				// create records for hhLog
				hhLog[idx2P0M[ihh]][0] = nM;
				hhLog[idx2P0M[ihh]][1] = nF;
				hhLog[idx2P0M[ihh]][2] = arrHdlr.min(arrHdlr.getColumn(tmpRec,
						0));
				hhLog[idx2P0M[ihh]][3] = arrHdlr.max(arrHdlr.getColumn(tmpRec,
						0));
				hhLog[idx2P0M[ihh]][4] = pfIdx[parentsIdx[0]];
				hhLog[idx2P0M[ihh]][5] = pfIdx[parentsIdx[1]];
				if (indivPool[pfIdx[parentsIdx[0]]][0] > indivPool[pfIdx[parentsIdx[1]]][0]) {
					hhLog[idx2P0M[ihh]][4] = pfIdx[parentsIdx[1]];
					hhLog[idx2P0M[ihh]][5] = pfIdx[parentsIdx[0]];
				}

				// update pmIdx and pfIdx
				int[] tmpval = new int[] { pfIdx[parentsIdx[0]],
						pfIdx[parentsIdx[1]] };
				pfIdx = arrHdlr.removeValueInArray(pfIdx, tmpval[0]);
				pfIdx = arrHdlr.removeValueInArray(pfIdx, tmpval[1]);

				// index of the hhold
				tmpRec = arrHdlr.concateColumns(tmpRec,
						arrHdlr.makeUniformArray(tmpRec.length, idx2P0M[ihh]));
				// index of hhold type
				tmpRec = arrHdlr.concateColumns(tmpRec, arrHdlr
						.makeUniformArray(tmpRec.length,
								nppInHhold[idx2P0M[ihh]][0])); 
				tmpParRec = arrHdlr.concateMatrices(tmpParRec, tmpRec);
			}
		}
		// pfIdxlen = 0; pmIdxlen = 0;
		// if (pfIdx != null) pfIdxlen = pfIdx.length;
		// if (pmIdx != null) pmIdxlen = pmIdx.length;
		// logger.debug("After allocation to 2P0M: pmIdxlen=" + pmIdxlen +
		// ", pfIdxlen=" + pfIdxlen);

		if (idx2P0F != null) {
			// logger.debug("idx2P0F households: " + idx2P0F.length);
			for (int ihh = 0; ihh <= idx2P0F.length - 1; ihh++) {
				
				int nM = hhLog[idx2P0F[ihh]][0];
				int nF = hhLog[idx2P0F[ihh]][1];
				int[][] tmpRec = new int[2][indivPool[0].length];
				if (pmIdx.length == 0 || pmIdx.length < 2) {
					logger.warn("WARNING: cannot generate dAgeParents for record "
									+ idx2P0F[ihh]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx2P0F[ihh]][0]]
											.toString()
									+ ") ! Only 1 male parent individual found in indivPool!");
					hhLog[idx2P0F[ihh]] = arrHdlr.makeUniformArray(
							hhLog[idx2P0F[ihh]].length, -1);
					continue;
				}
				int[][] dAgeParents = this.makeMatrixOfdAgeParents(pmIdx,
						pmIdx, indivPool);
				// get the index of female and male parents that have dAge
				// closest to the mean_dAgeParents
				int[] parentsIdx = arrHdlr.getIndexofClosestToValue(
						dAgeParents, mean_dAgeParents, true);
				// now generate records for these parents
				for (int j = 0; j <= 1; j++) {
					for (int i = 0; i <= indivPool[0].length - 1; i++) {
						tmpRec[j][i] = indivPool[pmIdx[parentsIdx[j]]][i];
					}
				}
				
				// update nM
				nM = nM - 2;
				// create records for hhLog
				hhLog[idx2P0F[ihh]][0] = nM;
				hhLog[idx2P0F[ihh]][1] = nF;
				hhLog[idx2P0F[ihh]][2] = arrHdlr.min(arrHdlr.getColumn(tmpRec,
						0));
				hhLog[idx2P0F[ihh]][3] = arrHdlr.max(arrHdlr.getColumn(tmpRec,
						0));
				hhLog[idx2P0F[ihh]][4] = pmIdx[parentsIdx[0]];
				hhLog[idx2P0F[ihh]][5] = pmIdx[parentsIdx[1]];
				if (indivPool[pmIdx[parentsIdx[0]]][0] > indivPool[pmIdx[parentsIdx[1]]][0]) {
					hhLog[idx2P0F[ihh]][4] = pmIdx[parentsIdx[1]];
					hhLog[idx2P0F[ihh]][5] = pmIdx[parentsIdx[0]];
				}
				// update pmIdx and pfIdx
				int[] tmpval = new int[] { pmIdx[parentsIdx[0]],
						pmIdx[parentsIdx[1]] };
				pmIdx = arrHdlr.removeValueInArray(pmIdx, tmpval[0]);
				pmIdx = arrHdlr.removeValueInArray(pmIdx, tmpval[1]);

				// index of the hhold
				tmpRec = arrHdlr.concateColumns(tmpRec,
						arrHdlr.makeUniformArray(tmpRec.length, idx2P0F[ihh]));
				// index of hhold type
				tmpRec = arrHdlr.concateColumns(tmpRec, arrHdlr
						.makeUniformArray(tmpRec.length,
								nppInHhold[idx2P0F[ihh]][0])); 
				tmpParRec = arrHdlr.concateMatrices(tmpParRec, tmpRec);
			}
		}
		// pfIdxlen = 0; pmIdxlen = 0;
		// if (pfIdx != null) pfIdxlen = pfIdx.length;
		// if (pmIdx != null) pmIdxlen = pmIdx.length;
		// logger.debug("After allocation to 2P0F: pmIdxlen=" + pmIdxlen +
		// ", pfIdxlen=" + pfIdxlen);

		if (idx2P != null) {
			// logger.debug("idx2P households: " + idx2P.length);
			for (int ihh = 0; ihh <= idx2P.length - 1; ihh++) {
				int nM = hhLog[idx2P[ihh]][0];
				int nF = hhLog[idx2P[ihh]][1];
				int[][] tmpRec = new int[2][indivPool[0].length];
				if (nM < 0 || nF < 0 || nM + nF < 2) {
					logger.warn("WARNING: cannot generate dAgeParents for household "
									+ idx2P[ihh]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx2P[ihh]][0]]
											.toString()
									+ ") ! Insufficient population in this household!");
					hhLog[idx2P[ihh]] = arrHdlr.makeUniformArray(
							hhLog[idx2P[ihh]].length, -1);
					continue;
				}
				if (pmIdx == null && pfIdx == null) {
					logger.warn("WARNING: cannot generate dAgeParents for household "
									+ idx2P[ihh]
									+ " in nppInHhold ("
									+ minIndivs[nppInHhold[idx2P[ihh]][0]]
											.toString()
									+ ") ! No parent individuals found in indivPool!");
					hhLog[idx2P[ihh]] = arrHdlr.makeUniformArray(
							hhLog[idx2P[ihh]].length, -1);
					continue;
				}
				if (pmIdx == null) {
					int[][] dAgeParents = this.makeMatrixOfdAgeParents(pfIdx,
							pfIdx, indivPool);
					// get the index of female and male parents that have dAge
					// closest to the mean_dAgeParents
					int[] parentsIdx = arrHdlr.getIndexofClosestToValue(
							dAgeParents, mean_dAgeParents, true);
					// now generate records for these parents and assign this
					// hhold index to them
					for (int j = 0; j <= 1; j++)
						for (int i = 0; i <= indivPool[0].length - 1; i++)
							tmpRec[j][i] = indivPool[pfIdx[parentsIdx[j]]][i];

					if (nF < 2) {
						int dnF = 2 - nF;
						nF = nF + dnF;
						nM = nM - dnF;
						
						// updates the number of males in this hhold
						nppInHhold[idx2P[ihh]][7] -= dnF; 
						// updates the number of females in this hhold
						nppInHhold[idx2P[ihh]][8] += dnF; 
					}
					// update nF
					nF = nF - 2;
					// create records for hhLog
					hhLog[idx2P[ihh]][0] = nM;
					hhLog[idx2P[ihh]][1] = nF;
					hhLog[idx2P[ihh]][2] = arrHdlr.min(arrHdlr.getColumn(
							tmpRec, 0));
					hhLog[idx2P[ihh]][3] = arrHdlr.max(arrHdlr.getColumn(
							tmpRec, 0));
					hhLog[idx2P[ihh]][4] = pfIdx[parentsIdx[0]];
					hhLog[idx2P[ihh]][5] = pfIdx[parentsIdx[1]];
					if (indivPool[pfIdx[parentsIdx[0]]][0] > indivPool[pfIdx[parentsIdx[1]]][0]) {
						hhLog[idx2P[ihh]][4] = pfIdx[parentsIdx[1]];
						hhLog[idx2P[ihh]][5] = pfIdx[parentsIdx[0]];
					}
					// update pmIdx and pfIdx
					int[] tmpval = new int[] { pfIdx[parentsIdx[0]],
							pfIdx[parentsIdx[1]] };
					pfIdx = arrHdlr.removeValueInArray(pfIdx, tmpval[0]);
					pfIdx = arrHdlr.removeValueInArray(pfIdx, tmpval[1]);
				} else if (pfIdx == null) {
					int[][] dAgeParents = this.makeMatrixOfdAgeParents(pmIdx,
							pmIdx, indivPool);
					// get the index of female and male parents that have dAge
					// closest to the mean_dAgeParents
					int[] parentsIdx = arrHdlr.getIndexofClosestToValue(
							dAgeParents, mean_dAgeParents, true);
					// now generate records for these parents
					for (int j = 0; j <= 1; j++) {
						for (int i = 0; i <= indivPool[0].length - 1; i++) {
							tmpRec[j][i] = indivPool[pmIdx[parentsIdx[j]]][i];
						}
					}

					if (nM < 2) {
						int dnM = 2 - nM;
						nM = nM + dnM;
						nF = nF - dnM;
					
						// updates the number of males in this hhold
						nppInHhold[idx2P[ihh]][7] += dnM; 
						// updates the number of females in this hhold 
						nppInHhold[idx2P[ihh]][8] -= dnM; 
					}
					// update nM
					nM = nM - 2;
					// create records for hhLog
					hhLog[idx2P[ihh]][0] = nM;
					hhLog[idx2P[ihh]][1] = nF;
					hhLog[idx2P[ihh]][2] = arrHdlr.min(arrHdlr.getColumn(
							tmpRec, 0));
					hhLog[idx2P[ihh]][3] = arrHdlr.max(arrHdlr.getColumn(
							tmpRec, 0));
					hhLog[idx2P[ihh]][4] = pmIdx[parentsIdx[0]];
					hhLog[idx2P[ihh]][5] = pmIdx[parentsIdx[1]];
					if (indivPool[pmIdx[parentsIdx[0]]][0] > indivPool[pmIdx[parentsIdx[1]]][0]) {
						hhLog[idx2P[ihh]][4] = pmIdx[parentsIdx[1]];
						hhLog[idx2P[ihh]][5] = pmIdx[parentsIdx[0]];
					}
					// update pmIdx and pfIdx
					int[] tmpval = new int[] { pmIdx[parentsIdx[0]],
							pmIdx[parentsIdx[1]] };
					pmIdx = arrHdlr.removeValueInArray(pmIdx, tmpval[0]);
					pmIdx = arrHdlr.removeValueInArray(pmIdx, tmpval[1]);
				} else {
					int[][] dAgeParents = this.makeMatrixOfdAgeParents(pmIdx,
							pfIdx, indivPool);
					// get the index of female and male parents that have dAge
					// closest to the mean_dAgeParents
					int[] parentsIdx = arrHdlr.getIndexofClosestToValue(
							dAgeParents, mean_dAgeParents, false);
					// now generate records for these parents and assign this
					// hhold index to them
					for (int i = 0; i <= indivPool[0].length - 1; i++) {
						tmpRec[0][i] = indivPool[pfIdx[parentsIdx[0]]][i];
						tmpRec[1][i] = indivPool[pmIdx[parentsIdx[1]]][i];
					}
					// update nM and nF
					nM = nM - 1;
					nF = nF - 1;
					// create records for hhLog
					hhLog[idx2P[ihh]][0] = nM;
					hhLog[idx2P[ihh]][1] = nF;
					hhLog[idx2P[ihh]][2] = arrHdlr.min(arrHdlr.getColumn(
							tmpRec, 0));
					hhLog[idx2P[ihh]][3] = arrHdlr.max(arrHdlr.getColumn(
							tmpRec, 0));
					hhLog[idx2P[ihh]][4] = pfIdx[parentsIdx[0]];
					hhLog[idx2P[ihh]][5] = pmIdx[parentsIdx[1]];
					if (indivPool[pfIdx[parentsIdx[0]]][0] > indivPool[pmIdx[parentsIdx[1]]][0]) {
						hhLog[idx2P[ihh]][4] = pmIdx[parentsIdx[1]];
						hhLog[idx2P[ihh]][5] = pfIdx[parentsIdx[0]];
					}
					// update pmIdx and pfIdx
					pfIdx = ArrayUtils.remove(pfIdx, parentsIdx[0]);
					pmIdx = ArrayUtils.remove(pmIdx, parentsIdx[1]);
				}

				// index of the hhold
				tmpRec = arrHdlr.concateColumns(tmpRec,
						arrHdlr.makeUniformArray(tmpRec.length, idx2P[ihh]));
				// index of hhold type
				tmpRec = arrHdlr.concateColumns(tmpRec, arrHdlr
						.makeUniformArray(tmpRec.length,
								nppInHhold[idx2P[ihh]][0])); 
				tmpParRec = arrHdlr.concateMatrices(tmpParRec, tmpRec);

			}
		}
		// pfIdxlen = 0; pmIdxlen = 0;
		// if (pfIdx != null) pfIdxlen = pfIdx.length;
		// if (pmIdx != null) pmIdxlen = pmIdx.length;
		// logger.debug("After allocation to 2P: pmIdxlen=" + pmIdxlen +
		// ", pfIdxlen=" + pfIdxlen);

		return tmpParRec;
	}

	/**
	 * gets indices in the pool of individuals of male or female individuals
	 * type GroupHhold.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param gender
	 *            0 for female, 1 for male
	 * @return indices in the pool of individuals of male or female individuals
	 *         type GroupHhold.
	 */
	private int[] getIdxOfGH(int[][] indivPool, int gender) {
		int[] nfIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]]
					.equals(HouseholdRelationship.GroupHhold)
					&& indivPool[i][1] == gender) {
				nfIdx = ArrayUtils.add(nfIdx, i);
			}

		}
		return nfIdx;
	}

	/**
	 * gets indices in the pool of individuals of male and female individuals
	 * type LonePerson.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @return indices in the pool of individuals of male or female individuals
	 *         type LonePerson.
	 */
	private int[] getIdxOfLP(int[][] indivPool) {
		int[] nfIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]]
					.equals(HouseholdRelationship.LonePerson)) {
				nfIdx = ArrayUtils.add(nfIdx, i);
			}
		}

		return nfIdx;
	}

	/**
	 * gets indices in the pool of individuals of male and female individuals
	 * type U15Child.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @return indices in the pool of individuals of male or female individuals
	 *         type U15Child.
	 */
	private int[] getIdxOfU15(int[][] indivPool) {
		int[] chIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]].equals(HouseholdRelationship.U15Child)) {
				chIdx = ArrayUtils.add(chIdx, i);
			}
		}
		return chIdx;
	}

	/**
	 * gets indices in the pool of individuals of male or female individuals
	 * type U15Child.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param gender
	 *            0 for female, 1 for male
	 * @return indices in the pool of individuals of male or female individuals
	 *         type U15Child.
	 */
	private int[] getIdxOfU15(int[][] indivPool, int gender) {
		int[] chIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]].equals(HouseholdRelationship.U15Child)
					&& indivPool[i][1] == gender) {
				chIdx = ArrayUtils.add(chIdx, i);
			}
		}
		return chIdx;
	}

	/**
	 * gets indices in the pool of individuals of male and female individuals
	 * type Student.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @return indices in the pool of individuals of male or female individuals
	 *         type Student.
	 */
	private int[] getIdxOfStu(int[][] indivPool) {
		int[] chIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]].equals(HouseholdRelationship.Student)) {
				chIdx = ArrayUtils.add(chIdx, i);
			}
		}
		return chIdx;
	}

	/**
	 * gets indices in the pool of individuals of male or female individuals
	 * type Student.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param gender
	 *            0 for female, 1 for male
	 * @return indices in the pool of individuals of male or female individuals
	 *         type Student.
	 */
	private int[] getIdxOfStu(int[][] indivPool, int gender) {
		int[] chIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]].equals(HouseholdRelationship.Student)
					&& indivPool[i][1] == gender) {
				chIdx = ArrayUtils.add(chIdx, i);
			}
		}
		return chIdx;
	}

	/**
	 * gets indices in the pool of individuals of male and female individuals
	 * type O15Child.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @return indices in the pool of individuals of male or female individuals
	 *         type O15Child.
	 */
	private int[] getIdxOfO15(int[][] indivPool) {
		int[] chIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]].equals(HouseholdRelationship.O15Child)) {
				chIdx = ArrayUtils.add(chIdx, i);
			}
		}
		return chIdx;
	}

	/**
	 * gets indices in the pool of individuals of male or female individuals
	 * type O15Child.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param gender
	 *            0 for female, 1 for male
	 * @return indices in the pool of individuals of male or female individuals
	 *         type O15Child.
	 */
	private int[] getIdxOfO15(int[][] indivPool, int gender) {
		int[] chIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if (hhrel8[indivPool[i][2]].equals(HouseholdRelationship.O15Child)
					&& indivPool[i][1] == gender){
				chIdx = ArrayUtils.add(chIdx, i);
			}
		}
		return chIdx;
	}

	/**
	 * gets indices in the pool of individuals of male or female individuals
	 * type Relative and NonRelative.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param gender
	 *            0 for female, 1 for male
	 * @return indices in the pool of individuals of male or female individuals
	 *         type Relative and NonRelative.
	 */
	private int[] getIdxOfRel(int[][] indivPool, int gender) {
		int[] relIdx = null;
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		for (int i = 0; i <= indivPool.length - 1; i++) {
			if ((hhrel8[indivPool[i][2]]
					.equals(HouseholdRelationship.Relative) || hhrel8[indivPool[i][2]]
					.equals(HouseholdRelationship.NonRelative))
					&& indivPool[i][1] == gender) {
				relIdx = ArrayUtils.add(relIdx, i);
			}
		}
		return relIdx;
	}

	/**
	 * gets indices in the pool of individuals of male or female individuals
	 * type Married and DeFacto.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param gender
	 *            0 for female, 1 for male
	 * @return indices in the pool of individuals of male or female individuals
	 *         type Married and DeFacto.
	 */
	private int[] getIdxOfParents(int[][] indivPool, int gender) {
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		int[] parIdx = null;
		for (int i = 0; i <= indivPool.length - 1; i++) {
			// hhrel8[femaleRecords[femaleRecords.length-1][2]]
			if ((hhrel8[indivPool[i][2]].equals(HouseholdRelationship.Married) || hhrel8[indivPool[i][2]]
					.equals(HouseholdRelationship.DeFacto))
					&& indivPool[i][1] == gender) {
				parIdx = ArrayUtils.add(parIdx, i);
			}
		}
		return parIdx;
	}

	/**
	 * gets indices in the pool of individuals of male or female individuals
	 * type LoneParent.
	 * 
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param gender
	 *            0 for female, 1 for male
	 * @return indices in the pool of individuals of male or female individuals
	 *         type LoneParent.
	 */
	private int[] getIdxOfSingleParents(int[][] indivPool, int gender) {
		HouseholdRelationship[] hhrel8 = HouseholdRelationship.values();

		int[] parIdx = null;
		for (int i = 0; i <= indivPool.length - 1; i++) {
			// hhrel8[femaleRecords[femaleRecords.length-1][2]]
			if (hhrel8[indivPool[i][2]]
					.equals(HouseholdRelationship.LoneParent)
					&& indivPool[i][1] == gender) {
				parIdx = ArrayUtils.add(parIdx, i);
			}
		}
		return parIdx;
	}

	/**
	 * creates an array of difference between age of a group of children and the
	 * age of a parent.
	 * 
	 * @param chIdx
	 *            indices of child individuals in the pool of individuals.
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param parentAge
	 *            age of the parent
	 * @return array of age difference between the selected children and a
	 *         parent.
	 */
	private int[] makedAgeParentChild(int[] chIdx, int[][] indivPool,
			int parentAge) {
		if (chIdx == null)
			return null;
		int[] dAge = new int[chIdx.length];
		for (int i = 0; i <= chIdx.length - 1; i++){
			dAge[i] = parentAge - indivPool[chIdx[i]][0];
		}
		return dAge;
	}

	/**
	 * creates an array of difference between age of a group of children and the
	 * age of a parent.
	 * 
	 * @param chIdx
	 *            indices of child individuals in the pool of individuals.
	 * @param indivPool
	 *            pool of individuals. This is a 2D integer array. Each record
	 *            represents 1 individual. Columns are [age, gender, index of
	 *            household relationship].
	 * @param parentAge
	 *            age of the parent
	 * @return array of age difference between the selected children and a
	 *         parent.
	 */
	private int[][] makeMatrixOfdAgeParents(int[] pmIdx, int[] pfIdx,
			int[][] indivPool) {
		int[][] dAge = new int[pfIdx.length][pmIdx.length];
		for (int ifm = 0; ifm <= pfIdx.length - 1; ifm++) {
			for (int im = 0; im <= pmIdx.length - 1; im++) {
				dAge[ifm][im] = indivPool[pmIdx[im]][0]
						- indivPool[pfIdx[ifm]][0];
			}
		}
		return dAge;
	}

	/**
	 * creates a pool of individuals.
	 * 
	 * @param minRes
	 *            number of minimum number of people of each household
	 *            relationship category (from table S1)
	 * @param hhRelMale
	 *            array of number of males in each household relationship
	 *            category by age groups (should be after corrected)
	 * @param hhRelFemale
	 *            array of number of males in each household relationship
	 *            category by age groups (should be after corrected)
	 * @return a 2D integer array in which each record represents an individual.
	 *         Each record has the following columns [age gender(1 male, 0
	 *         female), index of household relationship]
	 */
	private int[][] createIndivPool(int[][] minRes, int[][] hhRelMale,
			int[][] hhRelFemale, Random random) {
		int[][] indivRecords = null;
		int[][] maleRecords = null;
		int[][] femaleRecords = null;
		ArrayHandler arrHdlr = new ArrayHandler();

		for (int i = 0; i <= hhRelMale.length - 1; i++) {
			int year1 = hhRelMale[i][HouseholdRelationship.year_1.ordinal()];
			int year2 = hhRelMale[i][HouseholdRelationship.year_2.ordinal()];
			// logger.debug("Age group: " + year1 + " - " + year2);
			for (HouseholdRelationship hhrel : HouseholdRelationship.values()) {
				if (hhrel.equals(HouseholdRelationship.id)
						|| hhrel.equals(HouseholdRelationship.year_1)
						|| hhrel.equals(HouseholdRelationship.year_2)
						|| hhrel.equals(HouseholdRelationship.Visitor)
						|| hhrel.equals(HouseholdRelationship.note)) {
					continue;
				}
				int tmpnpp = hhRelMale[i][hhrel.ordinal()];
				if ((hhrel.equals(HouseholdRelationship.Married)
						|| hhrel.equals(HouseholdRelationship.DeFacto) || hhrel
							.equals(HouseholdRelationship.LoneParent))
						&& tmpnpp > 0 && year1 < 18) {
					year1 = 18;
				}

				// maleRecords = [age gender hholdRelIdx];
				for (int iM = 0; iM <= tmpnpp - 1; iM++) {
					maleRecords = arrHdlr.concateMatrices(
							maleRecords,
							new int[][] { {
									year1
											+ (int) ((year2 - year1) * random
													.nextDouble()), 1,
									hhrel.ordinal() } });
				}
			}
		}

		for (int i = 0; i <= hhRelFemale.length - 1; i++) {
			int year1 = hhRelFemale[i][HouseholdRelationship.year_1.ordinal()];
			int year2 = hhRelFemale[i][HouseholdRelationship.year_2.ordinal()];
			// logger.debug("Age group: " + year1 + " - " + year2);
			for (HouseholdRelationship hhrel : HouseholdRelationship.values()) {
				if (hhrel.equals(HouseholdRelationship.id)
						|| hhrel.equals(HouseholdRelationship.year_1)
						|| hhrel.equals(HouseholdRelationship.year_2)
						|| hhrel.equals(HouseholdRelationship.Visitor)
						|| hhrel.equals(HouseholdRelationship.note)) {
					continue;
				}
				int tmpnpp = hhRelFemale[i][hhrel.ordinal()];
				if ((hhrel.equals(HouseholdRelationship.Married)
						|| hhrel.equals(HouseholdRelationship.DeFacto) || hhrel
							.equals(HouseholdRelationship.LoneParent))
						&& tmpnpp > 0 && year1 < 18) {
					year1 = 18;
				}
				
				// femaleRecords = [age gender hholdRelIdx];
				for (int iM = 0; iM <= tmpnpp - 1; iM++) {
					femaleRecords = arrHdlr.concateMatrices(
							femaleRecords,
							new int[][] { {
									year1
											+ (int) ((year2 - year1) * random
													.nextDouble()), 0,
									hhrel.ordinal() } });
				}
			}
		}
		indivRecords = arrHdlr.concateMatrices(maleRecords, femaleRecords);
		return indivRecords;
	}

	/**
	 * checks (and corrects if neccessary) if sum of defacto and married males
	 * and females is not an even number.
	 * 
	 * @param newhhRelMale
	 *            number of males in each household relationship by age groups
	 *            (from ABS table B22)
	 * @param newhhRelFemale
	 *            number of females in each household relationship by age groups
	 *            (from ABS table B22)
	 */
	private void correctNumberOfMarriedAndDefacto(int[][] newhhRelMale,
			int[][] newhhRelFemale) {
		ArrayHandler arrHdlr = new ArrayHandler();

		int colMarried = HouseholdRelationship.Married.ordinal();
		int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
		int colRelatives = HouseholdRelationship.Relative.ordinal();

		// get the total number of married and defacto persons
		int nppCouple = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				newhhRelMale, colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(newhhRelMale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(newhhRelFemale,
						colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(newhhRelFemale,
						colDeFacto));
		// nppCouple is an odd number, adjust accordingly
		if (nppCouple % 2 != 0) { 
			int[] colIndices = new int[] { colMarried, colDeFacto };
			int[][] tmpMales = arrHdlr.getColumns(newhhRelMale, colIndices);
			int[][] tmpFemales = arrHdlr.getColumns(newhhRelFemale, colIndices);
			int[] iMxMales = arrHdlr.getIndexOfMax(tmpMales);
			int[] iMxFemales = arrHdlr.getIndexOfMax(tmpFemales);
			if (tmpMales[iMxMales[0]][iMxMales[1]] > tmpFemales[iMxFemales[0]][iMxFemales[1]]) {
				int[] iMx = iMxMales;
				newhhRelMale[iMx[0]][colIndices[iMx[1]]] = newhhRelMale[iMx[0]][colIndices[iMx[1]]] - 1;
				newhhRelMale[iMx[0]][colRelatives] = newhhRelMale[iMx[0]][colRelatives] + 1;
			} else {
				int[] iMx = iMxFemales;
				newhhRelFemale[iMx[0]][colIndices[iMx[1]]] = newhhRelFemale[iMx[0]][colIndices[iMx[1]]] - 1;
				newhhRelFemale[iMx[0]][colRelatives] = newhhRelFemale[iMx[0]][colRelatives] + 1;
			}
		}
	}

	/**
	 * adjusts the number of family households in table B24 according to the new
	 * values in B22. The objective is to make the number of people in HF in B22
	 * greater than or equal to minimum number of people required.
	 * 
	 * @param newhhRelMale
	 *            number of males in each household relationship by age groups
	 *            (from ABS table B22)
	 * @param newhhRelFemale
	 *            number of females in each household relationship by age groups
	 *            (from ABS table B22)
	 * @param nHF
	 *            array specifying the number of households in each family
	 *            household cateagory (formatted originally from ABS table 24).
	 *            Columns are [index of household category as in
	 *            min_individuals_note, number of households].
	 * @return corrected number of households in each family household category
	 *         (formatted similar to nHF)
	 */
	private int[][] adjustNumberOfHF(int[][] nHF) {
		ArrayHandler arrHdlr = new ArrayHandler();

		int colMarried = HouseholdRelationship.Married.ordinal();
		int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
		int colLoneParent = HouseholdRelationship.LoneParent.ordinal();
		int colU15 = HouseholdRelationship.U15Child.ordinal();
		int colStu = HouseholdRelationship.Student.ordinal();
		int colO15 = HouseholdRelationship.O15Child.ordinal();
		int colRelatives = HouseholdRelationship.Relative.ordinal();
		int colNonRelatives = HouseholdRelationship.NonRelative.ordinal();

		// get the total number of married and defacto persons
		int nppCouple = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colDeFacto))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colMarried))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colDeFacto));

		// get the number of lone parents
		int nppLoneParent = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelMale, colLoneParent))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colLoneParent));
		// get the number of children < 15
		int nppU15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colU15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colU15));
		// get the number of students
		int nppStu = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colStu))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colStu));
		// get the number of children > 15
		int nppO15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colO15))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colO15));
		// get the total number of relatives and non-relatives
		int nppRelatives = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelMale, colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						colRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colNonRelatives))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						colNonRelatives));

		// initial estimation of number of each family household type
		// gets the current total number of family households
		int oldTotFamiNum = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(nHF,
				FamilyComposition.families.ordinal()));
		// gets the current total number of family households that have
		// parent(s)
		int oldNumFamilyWithParents = arrHdlr.sumPositiveInArray(arrHdlr
				.extractArray(
						arrHdlr.getColumn(nHF,
								FamilyComposition.families.ordinal()),
						FamilyCompositionNote.HF1.ordinal(),
						FamilyCompositionNote.HF15.ordinal()));
		// calculates the new total number of family households (HF1 to HF15 AND
		// HF16)
		// based on the proportion of current and new number of family
		// households with parents.
		// Note that the new new number of family households with parents is
		// calculated based on
		// the number of 'Married' and 'DeFacto' individuals and 'LoneParent'
		// individuals.
		int newTotFamiNum = arrHdlr.roundToCloserInt((double) oldTotFamiNum
				/ (double) oldNumFamilyWithParents
				* ((double) nppCouple / 2 + nppLoneParent));

		// calculates the number of family households in categories HF1 to HF8
		// to satisfy
		// Assumption 2, which says 'The total number of 'Married' and 'DeFacto'
		// individuals
		// is two times the number of family households that have two parents'
		int[] hf1_hf8;
		if (arrHdlr.sumPositiveInArray(arrHdlr.extractArray(
				arrHdlr.getColumn(nHF, FamilyComposition.families.ordinal()),
				FamilyCompositionNote.HF1.ordinal(),
				FamilyCompositionNote.HF8.ordinal())) == 0) {
			
			hf1_hf8 = arrHdlr.allocateProportionally(arrHdlr.makeUniformArray(
					FamilyCompositionNote.HF8.ordinal()
							- FamilyCompositionNote.HF1.ordinal() + 1, 1),
					nppCouple / 2);
		}
		else {
			hf1_hf8 = arrHdlr.allocateProportionally(arrHdlr.extractArray(
					arrHdlr.getColumn(nHF,
							FamilyComposition.families.ordinal()),
					FamilyCompositionNote.HF1.ordinal(),
					FamilyCompositionNote.HF8.ordinal()), nppCouple / 2);
		}
		
		// calculates the number of family households in categories HF9 to HF15
		// to satisfy
		// Assumption 3, which says 'The total number of 'LoneParent'
		// individuals is equal
		// to the number of single parent family households'
		int[] hf9_hf15;
		if (arrHdlr.sumPositiveInArray(arrHdlr.extractArray(
				arrHdlr.getColumn(nHF, FamilyComposition.families.ordinal()),
				FamilyCompositionNote.HF9.ordinal(),
				FamilyCompositionNote.HF15.ordinal())) == 0) {
			hf9_hf15 = arrHdlr.allocateProportionally(arrHdlr.makeUniformArray(
					FamilyCompositionNote.HF15.ordinal()
							- FamilyCompositionNote.HF9.ordinal() + 1, 1),
					nppLoneParent);
		}
		else {
			hf9_hf15 = arrHdlr.allocateProportionally(arrHdlr.extractArray(
					arrHdlr.getColumn(nHF,
							FamilyComposition.families.ordinal()),
					FamilyCompositionNote.HF9.ordinal(),
					FamilyCompositionNote.HF15.ordinal()), nppLoneParent);
		}
		
		int[][] newnHF = new int[nHF.length][nHF[0].length];
		newnHF = arrHdlr.replaceColumn(newnHF, FamilyComposition.id.ordinal(),
				arrHdlr.getColumn(nHF, FamilyComposition.id.ordinal()));
		for (int i = FamilyCompositionNote.HF1.ordinal(); i <= FamilyCompositionNote.HF8
				.ordinal(); i++) {
			newnHF[i][1] = hf1_hf8[i];
		}
		for (int i = FamilyCompositionNote.HF9.ordinal(); i <= FamilyCompositionNote.HF15
				.ordinal(); i++){
			newnHF[i][1] = hf9_hf15[i - FamilyCompositionNote.HF9.ordinal()];
		}

		// adjusts the new total number of family households (newTotFamiNum) if
		// it is
		// smaller than the number of households having parents (just calculated
		// above).
		// This adjustment implicitly assumes that the number of HF16 households
		// equals 0.
		if (arrHdlr.sumPositiveInArray(hf9_hf15)
				+ arrHdlr.sumPositiveInArray(hf1_hf8) > newTotFamiNum){
		
			newTotFamiNum = arrHdlr.sumPositiveInArray(hf9_hf15)
					+ arrHdlr.sumPositiveInArray(hf1_hf8);
		}

		newnHF[FamilyCompositionNote.HF16.ordinal()][1] = newTotFamiNum
				- (arrHdlr.sumPositiveInArray(hf9_hf15) + arrHdlr
						.sumPositiveInArray(hf1_hf8));

		int[][] finnHF = testNumberofHhold2(newnHF, nppU15, nppStu, nppO15,
				nppRelatives);

		return finnHF;
	}

	/**
	 * adjusts the number of nonfamily households in the array of number of
	 * households having 1,2,3,4,5,6+ residents. This adjustment is to make the
	 * number of nonfamily households big enough to accommodate the number of
	 * 'GroupHhold' and 'LonePerson' individuals as specified in the arrays of
	 * males and females in each household relationship category by age groups.
	 * 
	 * @param newhhRelMale
	 *            array of number of males in each household relationship
	 *            category by age groups (should be after corrected)
	 * @param newhhRelFemale
	 *            array of number of males in each household relationship
	 *            category by age groups (should be after corrected)
	 * @param rawhhComp
	 *            array of number of households having 1,2,3,4,5,6+ residents
	 *            (should be the original from ABS table B30)
	 * @return the corrected array of number of households hvaing 1,2,3,4,5,6+
	 *         residents
	 */
	private int[][] adjustNumberOfNFHhold(int[][] newhhRelMale,
			int[][] newhhRelFemale, int[][] rawhhComp) {
		ArrayHandler arrHdlr = new ArrayHandler();
		int[][] hhComp = new int[rawhhComp.length][rawhhComp[0].length];
		for (int i = 0; i <= hhComp.length - 1; i++) {
			for (int j = 0; j <= hhComp[0].length - 1; j++) {
				hhComp[i][j] = rawhhComp[i][j];
			}
		}

		int colGrHholdMem = HouseholdRelationship.GroupHhold.ordinal();
		int colLonePerson = HouseholdRelationship.LonePerson.ordinal();
		int totLonePerson = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				newhhRelMale, colLonePerson))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(newhhRelFemale,
						colLonePerson));
		int totGrHholdMem = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				newhhRelMale, colGrHholdMem))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(newhhRelFemale,
						colGrHholdMem));
		if (totGrHholdMem == 1) {
			totGrHholdMem = 0;
			totLonePerson = totLonePerson + 1;
			int tmpiM = ArrayUtils.indexOf(
                    arrHdlr.getColumn(newhhRelMale, colGrHholdMem), 1);
			int tmpiF = ArrayUtils.indexOf(
					arrHdlr.getColumn(newhhRelFemale, colGrHholdMem), 1);
			if (tmpiM != -1) {
				newhhRelMale[tmpiM][colGrHholdMem] = 0;
				newhhRelMale[tmpiM][colLonePerson] = newhhRelMale[tmpiM][colLonePerson] + 1;
			} else {
				newhhRelFemale[tmpiF][colGrHholdMem] = 0;
				newhhRelFemale[tmpiF][colLonePerson] = newhhRelFemale[tmpiF][colLonePerson] + 1;
			}
		}

		int nRes = 1;
		int itmp = ArrayUtils.indexOf(
				arrHdlr.getColumn(hhComp, HouseholdComposition.id.ordinal()),
				nRes);
		hhComp[itmp][HouseholdComposition.non_family_households.ordinal()] = totLonePerson;

		int minGrHholdMem = 0;
		int[][] nhhGHM = null;
		for (int i = 0; i <= hhComp.length - 1; i++) {
			if (hhComp[i][HouseholdComposition.id.ordinal()] != nRes) {
				minGrHholdMem = minGrHholdMem
						+ hhComp[i][HouseholdComposition.id.ordinal()]
						* hhComp[i][HouseholdComposition.non_family_households
								.ordinal()];
				nhhGHM = arrHdlr
						.concateMatrices(
								nhhGHM,
								new int[][] { {
										hhComp[i][HouseholdComposition.id
												.ordinal()],
										hhComp[i][HouseholdComposition.non_family_households
												.ordinal()] } });
			}
		}
		
		if (minGrHholdMem == 0) {
			int nRes2 = 2;
			int nRes3 = 3;
			int nhh2pp, nhh3pp;
			if (totGrHholdMem == 2) {
				nhh2pp = 1;
				nhh3pp = 0;
			} else if (totGrHholdMem % 2 == 0) {
				nhh2pp = totGrHholdMem / nRes2;
				nhh3pp = 0;
			} else {
				nhh2pp = totGrHholdMem / nRes2 - 1;
				nhh3pp = 1;
			}
			itmp = ArrayUtils.indexOf(
					arrHdlr.getColumn(hhComp,
							HouseholdComposition.id.ordinal()), nRes2);
			hhComp[itmp][HouseholdComposition.non_family_households.ordinal()] = nhh2pp;
			itmp = ArrayUtils.indexOf(
					arrHdlr.getColumn(hhComp,
							HouseholdComposition.id.ordinal()), nRes3);
			hhComp[itmp][HouseholdComposition.non_family_households.ordinal()] = nhh3pp;
			// for displaying and testing
			/*
			 * minGrHholdMem = 0; for (int i=0; i<=hhComp.length-1; i++) if
			 * (hhComp[i][household_composition.id.ordinal()]!=nRes)
			 * minGrHholdMem = minGrHholdMem +
			 * hhComp[i][household_composition.id.ordinal()]*
			 * hhComp[i][household_composition.non_family_households.ordinal()]
			 * ;
			 */
		} else {
			int imaxpp = ArrayUtils.indexOf(arrHdlr.getColumn(nhhGHM, 0),
					arrHdlr.max(arrHdlr.getColumn(nhhGHM, 0)));
			if ((minGrHholdMem > totGrHholdMem && nhhGHM[imaxpp][1] != 0)
					|| (minGrHholdMem != totGrHholdMem && nhhGHM[imaxpp][1] == 0)) {
				if (totGrHholdMem <= 6) {
					for (int i = 0; i <= nhhGHM.length - 1; i++)
						nhhGHM[i][1] = 0;
					int nRes2 = 2;
					int nRes3 = 3;
					int nhh2pp, nhh3pp;
					if (totGrHholdMem == 2) {
						nhh2pp = 1;
						nhh3pp = 0;
					} else if (totGrHholdMem % 2 == 0) {
						nhh2pp = totGrHholdMem / nRes2;
						nhh3pp = 0;
					} else {
						nhh2pp = totGrHholdMem / nRes2 - 1;
						nhh3pp = 1;
					}
					nhhGHM[ArrayUtils.indexOf(arrHdlr.getColumn(nhhGHM, 0),
							nRes2)][1] = nhh2pp;
					nhhGHM[ArrayUtils.indexOf(arrHdlr.getColumn(nhhGHM, 0),
							nRes3)][1] = nhh3pp;
					// for displaying and testing
					/*
					 * minGrHholdMem = 0; for (int i=0;i<=nhhGHM.length-1;i++)
					 * minGrHholdMem = minGrHholdMem +
					 * nhhGHM[i][0]*nhhGHM[i][1];
					 */
				} else {
					int iNZ = -1;
					for (int i = 0; i <= nhhGHM.length - 1; i++) {
						if (nhhGHM[i][1] > 0) {
							iNZ = i;
						}
					}
					
					double tmp = nhhGHM[iNZ][0];
					for (int i = 0; i <= nhhGHM.length - 1; i++) {
						if (i != iNZ) {
							tmp = tmp + (double) nhhGHM[i][0]
									* (double) nhhGHM[i][1] / nhhGHM[iNZ][1];
						}
					}
					for (int i = 0; i <= nhhGHM.length - 1; i++) {
						if (i != iNZ) {
							nhhGHM[i][1] = (int) (((double) nhhGHM[i][1] / (double) nhhGHM[iNZ][1]) * (totGrHholdMem / tmp));
						}
					}
					
					nhhGHM[iNZ][1] = (int) (totGrHholdMem / tmp);

					if (nhhGHM[imaxpp][1] == 0) {
						nhhGHM[imaxpp][1] = 1;
					}

					minGrHholdMem = 0;
					for (int i = 0; i <= nhhGHM.length - 1; i++) {
						minGrHholdMem = minGrHholdMem + nhhGHM[i][0]
								* nhhGHM[i][1];
					}

					while (minGrHholdMem > totGrHholdMem){
						for (int i = 0; i <= nhhGHM.length - 1; i++){
							if (nhhGHM[i][0] != arrHdlr.max(arrHdlr.getColumn(
									nhhGHM, 0)) && nhhGHM[i][1] > 0) {
								nhhGHM[i][1] = nhhGHM[i][1] - 1;
								minGrHholdMem = minGrHholdMem - nhhGHM[i][0];
							
								if (minGrHholdMem <= totGrHholdMem) {
									break;
								}
							}
						}
					}
				}
				for (int i = 0; i <= nhhGHM.length - 1; i++) {
					hhComp[ArrayUtils.indexOf(
							arrHdlr.getColumn(hhComp,
									HouseholdComposition.id.ordinal()),
							nhhGHM[i][0])][HouseholdComposition.non_family_households
							.ordinal()] = nhhGHM[i][1];
				}
			}
		}

		return hhComp;
	}

	/**
	 * allocates number of people to each family household as well as each non
	 * family household.
	 * 
	 * @param nHF
	 *            number of household in each family households (should be after
	 *            corrected)
	 * @param hhComp
	 *            array of number of households having 1,2,3,4,5,6+ residents
	 *            (should be after corrected)
	 * @param nppInHF
	 *            total number of males and females in each family household
	 *            category (should be after corrected)
	 * @param minRes
	 *            number of minimum number of people of each household
	 *            relationship category (from table S1)
	 * @param hhRelMale
	 *            array of number of males in each household relationship
	 *            category by age groups (should be after corrected)
	 * @param hhRelFemale
	 *            array of number of males in each household relationship
	 *            category by age groups (should be after corrected)
	 * @return a 2D integer array in which each record contains info of a
	 *         household. Each record has these columns [0]index in
	 *         min_individuals_note, [1] number of parents, [2] min number of
	 *         U15, [3] min number of Stu, [4] min number of O15, [5] min number
	 *         of Rel, [6] min number of NonFamily, [7] nM, [8] nF
	 */
	private int[][] allocateNumPersonsToHhold(int[][] nHF, int[][] hhComp,
			int[][] nppInHF, int[][] minRes, int[][] hhRelMale,
			int[][] hhRelFemale) {
		ArrayHandler arrHdlr = new ArrayHandler();
		MinIndividualsNote[] minIndivs = MinIndividualsNote.values();

		int[][] nppInEachHF = null;
		// first create the number of people in each family hhold
		// for each type of family household		
		for (int inHF = 0; inHF <= nHF.length - 1; inHF++) { 
			// get the number of households of this type
			int nhh = nHF[inHF][FamilyComposition.families.ordinal()]; 
			if (nhh == 0) {
				// if there is no hholds of this type, go to next
				// family household type
				continue; 
			}

			int inInHF = ArrayUtils.indexOf(
					arrHdlr.getColumn(nppInHF,
							FamilyCompositionBySex.id.ordinal()),
					nHF[inHF][FamilyComposition.id.ordinal()]);
			int iminRes = ArrayUtils.indexOf(
					arrHdlr.getColumn(minRes, MinIndividuals.index.ordinal()),
					nHF[inHF][FamilyComposition.id.ordinal()] + 1);

//			int minpp = minRes[iminRes][min_individuals.min_residents.ordinal()];
			int totMales = nppInHF[inInHF][FamilyCompositionBySex.males
					.ordinal()];
			int totFemales = nppInHF[inInHF][FamilyCompositionBySex.females
					.ordinal()];

			int avgMales = totMales / nhh;
			int avgFemales = totFemales / nhh;
			int[] tmp_nM = arrHdlr.makeUniformArray(nhh, avgMales);
			int[] tmp_nF = arrHdlr.makeUniformArray(nhh, avgFemales);
			int[] tmp_sumNF = arrHdlr.addValuesInColumns(tmp_nM, tmp_nF);
			while (arrHdlr.sumPositiveInArray(tmp_nM) < totMales) {
				int[] tmpIdx = arrHdlr.sortedIndices(tmp_sumNF);
				for (int i = 0; i <= tmpIdx.length - 1; i++) {
					tmp_nM[tmpIdx[i]] = tmp_nM[tmpIdx[i]] + 1;
					tmp_sumNF[tmpIdx[i]] = tmp_sumNF[tmpIdx[i]] + 1;
					if (arrHdlr.sumPositiveInArray(tmp_nM) >= totMales) {
						break;
					}
				}

			}
			while (arrHdlr.sumPositiveInArray(tmp_nF) < totFemales) {
				int[] tmpIdx = arrHdlr.sortedIndices(tmp_sumNF);
				for (int i = 0; i <= tmpIdx.length - 1; i++) {
					tmp_nF[tmpIdx[i]] = tmp_nF[tmpIdx[i]] + 1;
					tmp_sumNF[tmpIdx[i]] = tmp_sumNF[tmpIdx[i]] + 1;
					if (arrHdlr.sumPositiveInArray(tmp_nF) >= totFemales) {
						break;
					}
				}
			}

			int[] tmp_hhold = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.index.ordinal()] - 1);
			int[] tmp_parents = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_parents.ordinal()]);
			int[] tmp_u15 = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_u15.ordinal()]);
			int[] tmp_stu = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_students.ordinal()]);
			int[] tmp_o15 = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_o15.ordinal()]);
			int[] tmp_rel = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_relatives.ordinal()]);
			int[] tmp_nonfamily = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_non_family.ordinal()]);

			int[][] tmpnppInEachHF = null;
			tmpnppInEachHF = arrHdlr.concateColumns(tmp_hhold, tmp_parents);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_u15);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_stu);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_o15);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_rel);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
					tmp_nonfamily);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nM);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nF);
			nppInEachHF = arrHdlr.concateMatrices(nppInEachHF, tmpnppInEachHF);
		}


		// create number of people in each nonfamily household
		int hhIdx = -1;
		// int minpp = -1;
		int iminRes = -1;
		for (int i = 0; i <= minIndivs.length - 1; i++)
			if (minIndivs[i].equals(MinIndividualsNote.NF)) {
				hhIdx = i;
				iminRes = ArrayUtils.indexOf(
						arrHdlr.getColumn(minRes,
								MinIndividuals.index.ordinal()), hhIdx + 1);
				// minpp =
				// minRes[iminRes][min_individuals.min_residents.ordinal()];
				break;
			}
		// starting with NF hholds that have only 1 person
		int tmpnPerson = 1;
		int ihhComp = ArrayUtils.indexOf(
				arrHdlr.getColumn(hhComp, HouseholdComposition.id.ordinal()),
				tmpnPerson);
		int nhh = hhComp[ihhComp][HouseholdComposition.non_family_households
				.ordinal()];
		if (nhh != 0) {
			int colLonePerson = HouseholdRelationship.LonePerson.ordinal();
			int[] tmp_nM = arrHdlr.allocateProportionally(arrHdlr
					.makeUniformArray(nhh, 1), arrHdlr
					.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							colLonePerson)));
			int[] tmp_nF = new int[nhh];
			for (int i = 0; i <= tmp_nF.length - 1; i++) {
				if (tmp_nM[i] == 0) {
					tmp_nF[i] = 1;
				}
			}
			int[] tmp_ntot = arrHdlr.addValuesInColumns(tmp_nM, tmp_nF);

			for (int i = 0; i <= tmp_ntot.length - 1; i++) {
				if (tmp_ntot[i] != tmpnPerson) {
					logger.debug("BADSHIT!!! " + i + ", " + tmp_ntot[i]);
				}
			}
			
			if (arrHdlr.sumPositiveInArray(tmp_nM) != arrHdlr
					.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							colLonePerson))
					|| arrHdlr.sumPositiveInArray(tmp_nF) != arrHdlr
							.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
									colLonePerson))) {
				logger.debug("BADSHIT!!! tmpnPerson = " + tmpnPerson);
				logger.debug("Males: "
						+ arrHdlr.sumPositiveInArray(tmp_nM)
						+ ", "
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelMale, colLonePerson)));
				logger.debug("Females: "
						+ arrHdlr.sumPositiveInArray(tmp_nF)
						+ ", "
						+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
								hhRelFemale, colLonePerson)));
			}

			int[] tmp_hhold = arrHdlr.makeUniformArray(nhh, hhIdx);
			int[] tmp_parents = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_parents.ordinal()]);
			int[] tmp_u15 = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_u15.ordinal()]);
			int[] tmp_stu = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_students.ordinal()]);
			int[] tmp_o15 = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_o15.ordinal()]);
			int[] tmp_rel = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_relatives.ordinal()]);
			int[] tmp_nonfamily = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_non_family.ordinal()]);
			// int[] tmp_minpp = arrHdlr.makeUniformArray(nhh,minpp);
			// int[] tmp_nhh = arrHdlr.makeUniformArray(nhh,nhh);

			int[][] tmpnppInEachHF = null;
			// tmpnppInEachHF = arrHdlr.concateColumns(tmp_hhold, tmp_minpp);
			// tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nhh);
			tmpnppInEachHF = arrHdlr.concateColumns(tmp_hhold, tmp_parents);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_u15);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_stu);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_o15);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_rel);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
					tmp_nonfamily);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nM);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nF);
			// tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
			// tmp_ntot);
			nppInEachHF = arrHdlr.concateMatrices(nppInEachHF, tmpnppInEachHF);
		}
		// now with NF hholds that have 2 to 5 people
		int colGrHholdMem = HouseholdRelationship.GroupHhold.ordinal();
		int nppGHM = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
				colGrHholdMem));
		int nppGHF = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
				colGrHholdMem));
		for (tmpnPerson = 2; tmpnPerson <= 5; tmpnPerson++) {
			ihhComp = ArrayUtils.indexOf(
					arrHdlr.getColumn(hhComp,
							HouseholdComposition.id.ordinal()), tmpnPerson);
			nhh = hhComp[ihhComp][HouseholdComposition.non_family_households
					.ordinal()];
			if (nhh != 0) {
				int[] tmp_nM = new int[nhh];
				int[] tmp_nF = new int[nhh];
				int tmpM = (int) ((double) nhh * (double) tmpnPerson / 2);
				if (tmpM > nppGHM) {
					tmpM = nppGHM;
				}
				int tmpF = nhh * tmpnPerson - tmpM;
				if (tmpF <= nppGHF) {
					tmp_nM = arrHdlr.allocateProportionally(
							arrHdlr.makeUniformArray(nhh, 1), tmpM);
					for (int i = 0; i <= tmp_nF.length - 1; i++)
						if (tmpnPerson - tmp_nM[i] >= 0)
							tmp_nF[i] = tmpnPerson - tmp_nM[i];
						else
							tmp_nF[i] = 0;
				} else {
					tmpF = (int) ((double) nhh * (double) tmpnPerson / 2);
					if (tmpF > nppGHF) {
						tmpF = nppGHF;
					}
					tmpM = nhh * tmpnPerson - tmpF;
					if (tmpM <= nppGHM) {
						tmp_nF = arrHdlr.allocateProportionally(
								arrHdlr.makeUniformArray(nhh, 1), tmpF);
						for (int i = 0; i <= tmp_nM.length - 1; i++) {
							if (tmpnPerson - tmp_nF[i] >= 0) {
								tmp_nM[i] = tmpnPerson - tmp_nF[i];
							}
							else {
								tmp_nM[i] = 0;
							}
							
						}
					} else
						logger.debug("BADSHIT!!! tmpnPerson = "
								+ tmpnPerson);
				}
				int[] tmp_ntot = arrHdlr.addValuesInColumns(tmp_nM, tmp_nF);

				for (int i = 0; i <= tmp_ntot.length - 1; i++) {
					if (tmp_ntot[i] != tmpnPerson) {
						logger.debug("BADSHIT!!! " + i + ", "
								+ tmp_ntot[i]);
					}
				}
				
				if (arrHdlr.sumPositiveInArray(tmp_nM)
						+ arrHdlr.sumPositiveInArray(tmp_nF) != nhh
						* tmpnPerson) {
					logger.debug("BADSHIT!!! tmpnPerson = " + tmpnPerson);
					logger.debug("Males: "
							+ arrHdlr.sumPositiveInArray(tmp_nM)
							+ ", Females: "
							+ arrHdlr.sumPositiveInArray(tmp_nF) + ", Sum: "
							+ nhh * tmpnPerson);
				}

				int[] tmp_hhold = arrHdlr.makeUniformArray(nhh, hhIdx);
				int[] tmp_parents = arrHdlr.makeUniformArray(nhh,
						minRes[iminRes][MinIndividuals.min_parents.ordinal()]);
				int[] tmp_u15 = arrHdlr.makeUniformArray(nhh,
						minRes[iminRes][MinIndividuals.min_u15.ordinal()]);
				int[] tmp_stu = arrHdlr
						.makeUniformArray(nhh,
								minRes[iminRes][MinIndividuals.min_students
										.ordinal()]);
				int[] tmp_o15 = arrHdlr.makeUniformArray(nhh,
						minRes[iminRes][MinIndividuals.min_o15.ordinal()]);
				int[] tmp_rel = arrHdlr
						.makeUniformArray(nhh,
								minRes[iminRes][MinIndividuals.min_relatives
										.ordinal()]);
				
				int minimumNumOfResInNonFamilyHhold = 2;
				int[] tmp_nonfamily = arrHdlr.makeUniformArray(nhh, minimumNumOfResInNonFamilyHhold);
				// int[] tmp_minpp = arrHdlr.makeUniformArray(nhh,minpp);
				// int[] tmp_nhh = arrHdlr.makeUniformArray(nhh,nhh);

				int[][] tmpnppInEachHF = null;
				// tmpnppInEachHF = arrHdlr.concateColumns(tmp_hhold,
				// tmp_minpp);
				// tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
				// tmp_nhh);
				tmpnppInEachHF = arrHdlr.concateColumns(tmp_hhold, tmp_parents);
				tmpnppInEachHF = arrHdlr
						.concateColumns(tmpnppInEachHF, tmp_u15);
				tmpnppInEachHF = arrHdlr
						.concateColumns(tmpnppInEachHF, tmp_stu);
				tmpnppInEachHF = arrHdlr
						.concateColumns(tmpnppInEachHF, tmp_o15);
				tmpnppInEachHF = arrHdlr
						.concateColumns(tmpnppInEachHF, tmp_rel);
				tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
						tmp_nonfamily);
				tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nM);
				tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nF);
				// tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
				// tmp_ntot);
				nppInEachHF = arrHdlr.concateMatrices(nppInEachHF,
						tmpnppInEachHF);
				nppGHM = nppGHM - tmpM;
				nppGHF = nppGHF - tmpF;
			}
		}

		// finally with NF hholds that have 6 people
		tmpnPerson = 6;
		ihhComp = ArrayUtils.indexOf(
				arrHdlr.getColumn(hhComp, HouseholdComposition.id.ordinal()),
				tmpnPerson);
		nhh = hhComp[ihhComp][HouseholdComposition.non_family_households
				.ordinal()];
		if (nhh != 0) {
			int[] tmp_nM = new int[nhh];
			int[] tmp_nF = new int[nhh];
			if (nppGHM + nppGHF >= nhh * tmpnPerson) {
				tmp_nM = arrHdlr.allocateProportionally(
						arrHdlr.makeUniformArray(nhh, 1), nppGHM);
				for (int i = 0; i <= tmp_nF.length - 1; i++) {
					if (tmpnPerson - tmp_nM[i] >= 0) {
						tmp_nF[i] = tmpnPerson - tmp_nM[i];
					}
					else {
						tmp_nF[i] = 0;
					}
				}
				
				if (nppGHF > arrHdlr.sumPositiveInArray(tmp_nF)) {
					int[] remF = arrHdlr.allocateProportionally(
							arrHdlr.makeUniformArray(nhh, 1),
							nppGHF - arrHdlr.sumPositiveInArray(tmp_nF));
					tmp_nF = arrHdlr.addValuesInColumns(tmp_nF, remF);
				}
			} else
				logger.debug("BADSHIT!!! tmpnPerson = " + tmpnPerson);
			int[] tmp_ntot = arrHdlr.addValuesInColumns(tmp_nM, tmp_nF);

			for (int i = 0; i <= tmp_ntot.length - 1; i++) {
				if (tmp_ntot[i] < tmpnPerson) {
					logger.debug("BADSHIT!!! " + i + ", " + tmp_ntot[i]);
				}
			}
			if (arrHdlr.sumPositiveInArray(tmp_nM)
					+ arrHdlr.sumPositiveInArray(tmp_nF) != nppGHM + nppGHF) {
				logger.debug("BADSHIT!!!  tmpnPerson = " + tmpnPerson);
				logger.debug("Males: "
						+ arrHdlr.sumPositiveInArray(tmp_nM) + ", Females: "
						+ arrHdlr.sumPositiveInArray(tmp_nF) + ", Sum: "
						+ (nppGHM + nppGHF));
			}
			int[] tmp_hhold = arrHdlr.makeUniformArray(nhh, hhIdx);
			int[] tmp_parents = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_parents.ordinal()]);
			int[] tmp_u15 = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_u15.ordinal()]);
			int[] tmp_stu = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_students.ordinal()]);
			int[] tmp_o15 = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_o15.ordinal()]);
			int[] tmp_rel = arrHdlr.makeUniformArray(nhh,
					minRes[iminRes][MinIndividuals.min_relatives.ordinal()]);
			
			int minimumNumOfResInNonFamilyHhold = 2;
			int[] tmp_nonfamily = arrHdlr.makeUniformArray(nhh, minimumNumOfResInNonFamilyHhold); 
			// int[] tmp_minpp = arrHdlr.makeUniformArray(nhh,minpp);
			// int[] tmp_nhh = arrHdlr.makeUniformArray(nhh,nhh);

			int[][] tmpnppInEachHF = null;
			// tmpnppInEachHF = arrHdlr.concateColumns(tmp_hhold, tmp_minpp);
			// tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nhh);
			tmpnppInEachHF = arrHdlr.concateColumns(tmp_hhold, tmp_parents);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_u15);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_stu);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_o15);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_rel);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
					tmp_nonfamily);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nM);
			tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF, tmp_nF);
			// tmpnppInEachHF = arrHdlr.concateColumns(tmpnppInEachHF,
			// tmp_ntot);
			nppInEachHF = arrHdlr.concateMatrices(nppInEachHF, tmpnppInEachHF);
		}

		return nppInEachHF;
	}

	/**
	 * corrects number of males and females in each family household category
	 * (originally from ABS table B25) This correction is to make the total
	 * number of males and females satisfy (1) the total number of males and
	 * females for family households in table 22, and (2) the minimum number of
	 * people required for each household type.
	 * 
	 * @param nHF
	 *            number of household in each family households (which should be
	 *            ABS table B24 after corrected)
	 * @param minResidents
	 *            number of minimum number of people of each household
	 *            relationship category (from table S1)
	 * @param hhRelMale
	 *            array of number of males in each household relationship
	 *            category by age groups
	 * @param hhRelFemale
	 *            array of number of females in each household relationship
	 *            category by age groups
	 * @param nppInHF
	 *            total number of males and females in each family household
	 *            category (originally from ABS table B25)
	 * @return corrected total number of males and females in each family
	 *         household category (same format as nppInHF)
	 */
	private int[][] correctNumPersonsInFamilyHhold1(int[][] nHF,
			int[][] minResidents, int[][] hhRelMale, int[][] hhRelFemale,
			int[][] nppInHF) {
		ArrayHandler arrHdlr = new ArrayHandler();
		int[][] newnppInHF = new int[nppInHF.length][nppInHF[0].length];
		int[][] mfInHF = new int[nppInHF.length][nppInHF[0].length - 1];
		for (int i = 0; i <= newnppInHF.length - 1; i++) {
			for (int j = 0; j <= newnppInHF[0].length - 1; j++) {
				newnppInHF[i][j] = nppInHF[i][j];
			}
			for (int j = 1; j <= newnppInHF[0].length - 1; j++) {
				mfInHF[i][j - 1] = nppInHF[i][j];
			}
		}

		int colMarried = HouseholdRelationship.Married.ordinal();
		int colDeFacto = HouseholdRelationship.DeFacto.ordinal();
		int colLoneParent = HouseholdRelationship.LoneParent.ordinal();
		int colU15 = HouseholdRelationship.U15Child.ordinal();
		int colStu = HouseholdRelationship.Student.ordinal();
		int colO15 = HouseholdRelationship.O15Child.ordinal();
		int colRelatives = HouseholdRelationship.Relative.ordinal();
		int colNonRelatives = HouseholdRelationship.NonRelative.ordinal();

		// get the number of males and females in table B22
		int totMales = 0;
		int totFemales = 0;
		for (int i = 0; i <= hhRelMale.length - 1; i++) {
			totMales = totMales
					+ arrHdlr.sumPositiveInArray(new int[] {
							hhRelMale[i][colMarried], hhRelMale[i][colDeFacto],
							hhRelMale[i][colLoneParent], hhRelMale[i][colU15],
							hhRelMale[i][colStu], hhRelMale[i][colO15],
							hhRelMale[i][colRelatives],
							hhRelMale[i][colNonRelatives] });
			totFemales = totFemales
					+ arrHdlr.sumPositiveInArray(new int[] {
							hhRelFemale[i][colMarried],
							hhRelFemale[i][colDeFacto],
							hhRelFemale[i][colLoneParent],
							hhRelFemale[i][colU15], hhRelFemale[i][colStu],
							hhRelFemale[i][colO15],
							hhRelFemale[i][colRelatives],
							hhRelFemale[i][colNonRelatives] });
		}

		// allocate number of males and females to each HF type to satisfy
		// minimum number of residents.
		for (FamilyCompositionNote fcn : FamilyCompositionNote.values()) {
			int iminRes = ArrayUtils.indexOf(arrHdlr.getColumn(minResidents,
							MinIndividuals.index.ordinal()), fcn.ordinal() + 2);
			int inHF = ArrayUtils.indexOf(
					arrHdlr.getColumn(nHF, FamilyComposition.id.ordinal()),
					fcn.ordinal() + 1);
			int inInHF = ArrayUtils.indexOf(
					arrHdlr.getColumn(newnppInHF,
							FamilyCompositionBySex.id.ordinal()),
					fcn.ordinal() + 1);
			int minPeople = nHF[inHF][FamilyComposition.families.ordinal()]
					* minResidents[iminRes][MinIndividuals.min_residents
							.ordinal()];
			if (nHF[inHF][FamilyComposition.families.ordinal()] != 0
					&& mfInHF[inInHF][0] + mfInHF[inInHF][1] == 0) {
				mfInHF[inInHF][0] = 1;
				mfInHF[inInHF][1] = 1;
			}
			mfInHF[inInHF] = arrHdlr.allocateProportionally(new int[] {
					mfInHF[inInHF][0], mfInHF[inInHF][1] }, minPeople);
		}

		// sumMinMales is sum of males in mfInHF
		int[] mInHF = arrHdlr.getColumn(mfInHF, 0);
		int sumMinMales = arrHdlr.sumPositiveInArray(mInHF);
		// sumFemales is sum of females in mfInHF
		int[] fInHF = arrHdlr.getColumn(mfInHF, 1);
		int sumMinFemales = arrHdlr.sumPositiveInArray(fInHF);

		if (sumMinMales <= totMales) {
			if (sumMinFemales <= totFemales) {
				mInHF = arrHdlr.addValuesInColumns(
						mInHF,
						arrHdlr.allocateProportionally(mInHF, totMales
								- sumMinMales));
				fInHF = arrHdlr.addValuesInColumns(
						fInHF,
						arrHdlr.allocateProportionally(fInHF, totFemales
								- sumMinFemales));
			} else { // if sumMinFemales>totFemales
				int[] dfInHF = arrHdlr.allocateProportionally(fInHF,
						sumMinFemales - totFemales);
				fInHF = arrHdlr.subtractValuesInColumns(fInHF, dfInHF);
				mInHF = arrHdlr.addValuesInColumns(mInHF, dfInHF);
				int sumMales = arrHdlr.sumPositiveInArray(mInHF);
				if (sumMales < totMales)
					mInHF = arrHdlr.addValuesInColumns(
							mInHF,
							arrHdlr.allocateProportionally(mInHF, totMales
									- sumMales));
			}
		}
		else if (sumMinFemales <= totFemales) {
			int[] dmInHF = arrHdlr.allocateProportionally(mInHF, sumMinMales
					- totMales);
			mInHF = arrHdlr.subtractValuesInColumns(mInHF, dmInHF);
			fInHF = arrHdlr.addValuesInColumns(fInHF, dmInHF);
			int sumFemales = arrHdlr.sumPositiveInArray(fInHF);
			if (sumFemales < totFemales)
				fInHF = arrHdlr.addValuesInColumns(
						fInHF,
						arrHdlr.allocateProportionally(fInHF, totFemales
								- sumFemales));
		}

		// FIXME
		// should have a test here to check (and correct if neccesary)
		// assumption 5
		// which says the total number of 'Relative' and 'NonRelative'
		// individuals in B22a
		// must be greater than or equal to the total number of males and
		// females living
		// in HF16 households.

		// replace 2nd and 3rd columns in newnppInHF by mInHF and fInHF,
		// respectively
		newnppInHF = arrHdlr.replaceColumn(newnppInHF,
				FamilyCompositionBySex.males.ordinal(), mInHF);
		newnppInHF = arrHdlr.replaceColumn(newnppInHF,
				FamilyCompositionBySex.females.ordinal(), fInHF);

		return newnppInHF;
	}

	/**
	 * corrects the number of male and female 'Relative' and 'NonRelative'
	 * individuals in each age group. This correction is done so that the total
	 * number of 'Relative' and 'NonRelative' individuals equals to a given
	 * value.
	 * 
	 * @param hhRelM
	 *            array of number of males in each household relationship
	 *            category by age groups.
	 * @param hhRelF
	 *            array of number of females in each household relationship
	 *            category by age groups.
	 * @param newnRel
	 *            the new total number of 'Relative' and 'NonRelative'
	 *            individuals (both males and females).
	 * @return 
	 */
	private ArrayList<int[][]> correctRelativesInHholdRel(int[][] hhRelM, int[][] hhRelF,
			int newnRel) {
		ArrayHandler arrHdlr = new ArrayHandler();
		int[][] hhRelMale = hhRelM;
		int[][] hhRelFemale = hhRelF;
		ArrayList<int [][]> hhRelArrayList = new ArrayList<>();
		
		int relIdx = HouseholdRelationship.Relative.ordinal();
		int nonRelIdx = HouseholdRelationship.NonRelative.ordinal();
		int[] allRelVal = new int[] {
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale, relIdx)),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale, nonRelIdx)),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale, relIdx)),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale, nonRelIdx)) };
		allRelVal = arrHdlr.allocateProportionally(allRelVal, newnRel);
		int[] relM = arrHdlr.allocateProportionally(
				arrHdlr.getColumn(hhRelMale, relIdx), allRelVal[0]);
		int[] nonrelM = arrHdlr.allocateProportionally(
				arrHdlr.getColumn(hhRelMale, nonRelIdx), allRelVal[1]);
		int[] relF = arrHdlr.allocateProportionally(
				arrHdlr.getColumn(hhRelFemale, relIdx), allRelVal[2]);
		int[] nonrelF = arrHdlr.allocateProportionally(
				arrHdlr.getColumn(hhRelFemale, nonRelIdx), allRelVal[3]);

		hhRelMale = arrHdlr.replaceColumn(hhRelMale, relIdx, relM);
		hhRelMale = arrHdlr.replaceColumn(hhRelMale, nonRelIdx, nonrelM);
		hhRelFemale = arrHdlr.replaceColumn(hhRelFemale, relIdx, relF);
		hhRelFemale = arrHdlr.replaceColumn(hhRelFemale, nonRelIdx, nonrelF);
		
		hhRelArrayList.add(hhRelMale);
		hhRelArrayList.add(hhRelFemale);
		
		return hhRelArrayList;
	}

	/**
	 * calculates the difference between the actual and minimum total number of
	 * people living in family households.
	 * 
	 * @param finnHF
	 *            number of household in each family households (which should be
	 *            ABS table B24 after corrections)
	 * @param minResidents
	 *            number of minimum number of people of each household
	 *            relationship category (from table S1)
	 * @param totPeople
	 *            total number of males and females living in family households
	 *            (as specified in finnHF)
	 * @return the difference between total number of people living in family
	 *         households (as specified in finnHF) and the minimum number of
	 *         people required to live in these households
	 */
	private int testMinNumPersonsInHhold(int[][] finnHF, int[][] minResidents,
			int totPeople) {
		ArrayHandler arrHdlr = new ArrayHandler();
		int totMinPeople = 0;
		// logger.debug("Calculating min number of pp in hholds");
		for (FamilyCompositionNote fcn : FamilyCompositionNote.values()) {
			int iminRes = ArrayUtils.indexOf(arrHdlr.getColumn(minResidents,
							MinIndividuals.index.ordinal()), fcn.ordinal() + 2);
		
			int inHF = ArrayUtils.indexOf(
					arrHdlr.getColumn(finnHF, FamilyComposition.id.ordinal()),
					fcn.ordinal() + 1);
		
			totMinPeople = totMinPeople
					+ finnHF[inHF][FamilyComposition.families.ordinal()]
					* minResidents[iminRes][MinIndividuals.min_residents
							.ordinal()];
		}
		
		return totPeople - totMinPeople;
		
	}

	/**
	 * adjusts the number of households in each family household type. In other
	 * words, this methods adjusts the values in ABS table B24. This correction
	 * is to ensure that the number of households is enough to accommodate the
	 * number of children (including U15Child, Student, O51Child) and
	 * Relative/NonRelative individuals. Note that these numbers must have been
	 * adjusted once (in adjustNumberOfHF) to ensure that number of family
	 * households with 1 and 2 parents matches with the number of
	 * Married/DeFacto and LonePerson individuals, respectively.
	 * 
	 * @param orinHF
	 *            array specifying the number of households in each family
	 *            household cateagory (formatted originally from ABS table 24).
	 *            Columns are [index of household category as in
	 *            min_individuals_note, number of households]
	 * @param nppU15
	 *            total number of children under 15 (males and females).
	 * @param nppStu
	 *            total number of students (males and females).
	 * @param nppO15
	 *            total number of children over 15 (males and females).
	 * @param nppRelatives
	 *            total number of Relative and NonRelative individuals (males
	 *            and females).
	 * @return corrected number of households in each family household category
	 *         (formatted similar to nHF)
	 */
	private int[][] testNumberofHhold1(int[][] orinHF, int nppU15, int nppStu,
			int nppO15, int nppRelatives) {
		int[][] newnHF = new int[orinHF.length][orinHF[0].length];
		for (int i = 0; i <= newnHF.length - 1; i++) {
			for (int j = 0; j <= newnHF[0].length - 1; j++) {
				newnHF[i][j] = orinHF[i][j];
			}
		}
		ArrayHandler arrHdlr = new ArrayHandler();

		boolean u15Corrected = false;
		// check if number of family hholds satisfies number of U15 children
		// This check (and correction if neccesary) is to satisfy Assumption
		int nhh2Pcrn = newnHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF5.ordinal()][1];
		int nhh1Pcrn = newnHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF12.ordinal()][1];
		int maxFamiU15 = nhh2Pcrn + nhh1Pcrn;

		if (maxFamiU15 != 0 && nppU15 == 0) {
			// this is kind of a special case.
			// Ideally we should be strict on using numbers from B22a as a
			// reference, and thus in this case
			// adjust the number of families having U15Child to 0.
			// Even though this approach is fairly easily implemented for
			// U15Child,
			// it likely will open the pandora's box when dealing with the case
			// nppStu==0 or nppO15==0.
			// (Because we got to set number of households, say having O15Child,
			// to 0
			// while maintaining satisfactory numbers of households having
			// U15Child and those having
			// Student - quite a headache for Namy!)
			// Thus for the sake of having a simple and consitent solution to
			// this issue/case, we set the
			// number of U15Child (or Student or O15Child if these cases happen)
			// to 1.
			// - Whether 1 male or 1 female depends on which one has the smaller
			// number of children
			// (towards balancing number of males and females).
			// - For U15Child, set 1 to age group 0-14
			// - For Student and O15Child, set 1 to age group 15-24
			int totMchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelMale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.O15Child.ordinal()));
			int totFchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelFemale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.O15Child.ordinal()));
			int U15ChildInAgeGroup0_4Code = 1;
			if (totFchildren <= totMchildren){
				// set 1 to U15Child in age group 0-4
				hhRelFemale[0][HouseholdRelationship.U15Child.ordinal()] = U15ChildInAgeGroup0_4Code;
			}
			else {
				// set 1 to U15Child in age group 0-4
				hhRelMale[0][HouseholdRelationship.U15Child.ordinal()] = U15ChildInAgeGroup0_4Code; 
			}
			// set the total number of U15Child to 1
			int totalNumberOfU15Child = 1;
			nppU15 = totalNumberOfU15Child;
			logger.debug("nppU15 is set to 1");
		}

		if (maxFamiU15 == 0 && nppU15 != 0) {
			if (newnHF[FamilyCompositionNote.HF1.ordinal()][1] - nppU15 < 0) {
                logger.warn("Can't adjust households to fit nppU15! More sophisticated algorithm required!");
				return null;
			}
			u15Corrected = true;
			// sets the number of households of type HF5 to the number of
			// U15Child individuals
			// this is because this household type has only U15Child, no
			// Students nor O15Child
			// thus the number of households of this type is not affected by the
			// number
			// of 'Student' or 'O15Child' individuals
			newnHF[FamilyCompositionNote.HF5.ordinal()][1] = nppU15;
			// reduce the number of households in HF1 to reserve the total
			// number of households
			// having 2 parents
			newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
					.ordinal()][1] - nppU15;
		} else if (maxFamiU15 > nppU15) { // if number of hholds having U15 is
											// greater than the number of U15
											// indiv
			logger.debug("maxFamiU15=" + maxFamiU15 + ", nppU15="
					+ nppU15 + ". maxFamiU15 > nppU15");
			u15Corrected = true;
			int nhh2Prem = nhh2Pcrn;
			int nhh1Prem = 0;
			if (nppU15 - nhh1Pcrn >= 0)
				nhh2Prem = nhh2Pcrn - (nppU15 - nhh1Pcrn);
			else
				nhh1Prem = nhh1Pcrn - nppU15;
			// start correcting hhold with 2 parents and U15
			int[] tmpnHF = new int[] {
					newnHF[FamilyCompositionNote.HF2.ordinal()][1],
					newnHF[FamilyCompositionNote.HF3.ordinal()][1],
					newnHF[FamilyCompositionNote.HF4.ordinal()][1],
					newnHF[FamilyCompositionNote.HF5.ordinal()][1] };
			int[] tmpnHFIdx = new int[] {
					FamilyCompositionNote.HF2.ordinal(),
					FamilyCompositionNote.HF3.ordinal(),
					FamilyCompositionNote.HF4.ordinal(),
					FamilyCompositionNote.HF5.ordinal() };
			int[] newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, nhh2Pcrn
					- nhh2Prem);
			for (int i = 0; i <= newtmpnHF.length - 1; i++) {
				newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
			}
			// dissipating the number of hholds just removed to HF1
			newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
					.ordinal()][1] + nhh2Prem;

			// start correcting hhold with 1 parents and U15
			tmpnHF = new int[] {
					newnHF[FamilyCompositionNote.HF9.ordinal()][1],
					newnHF[FamilyCompositionNote.HF10.ordinal()][1],
					newnHF[FamilyCompositionNote.HF11.ordinal()][1],
					newnHF[FamilyCompositionNote.HF12.ordinal()][1] };
			tmpnHFIdx = new int[] { FamilyCompositionNote.HF9.ordinal(),
					FamilyCompositionNote.HF10.ordinal(),
					FamilyCompositionNote.HF11.ordinal(),
					FamilyCompositionNote.HF12.ordinal() };
			newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, nhh1Pcrn
					- nhh1Prem);
			for (int i = 0; i <= newtmpnHF.length - 1; i++){
				newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
			}
			// dissipating the number of hholds just removed to HF13, HF14, HF15
			int tmpnhh = newnHF[FamilyCompositionNote.HF13.ordinal()][1]
					+ newnHF[FamilyCompositionNote.HF14.ordinal()][1]
					+ newnHF[FamilyCompositionNote.HF15.ordinal()][1];
			tmpnHF = new int[] {
					newnHF[FamilyCompositionNote.HF13.ordinal()][1],
					newnHF[FamilyCompositionNote.HF14.ordinal()][1],
					newnHF[FamilyCompositionNote.HF15.ordinal()][1] };
			tmpnHFIdx = new int[] { FamilyCompositionNote.HF13.ordinal(),
					FamilyCompositionNote.HF14.ordinal(),
					FamilyCompositionNote.HF15.ordinal() };
			if (tmpnhh == 0){
				tmpnHF = new int[] { 1, 1, 1 };
			}
			
			newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
					+ nhh1Prem);
			for (int i = 0; i <= newtmpnHF.length - 1; i++){
				newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
			}
		}
		logger.debug("nHF after correcting hholds for U15Child");
		for (int i = 0; i <= newnHF.length - 1; i++){
			logger.debug(newnHF[i][1]);
		}

		boolean stuCorrected = false;
		// check if number of family hholds satisfy number of students
		nhh2Pcrn = newnHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF7.ordinal()][1];
		nhh1Pcrn = newnHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF14.ordinal()][1];
		int maxFamiStu = nhh2Pcrn + nhh1Pcrn;

		if (maxFamiStu != 0 && nppStu == 0) {
			// this is kind of a special case (again)! see further comments
			// above in U15 section.
			int totMchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelMale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.O15Child.ordinal()));
			int totFchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelFemale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.O15Child.ordinal()));
			int studentInAgeGroup15_24Code = 1;
			if (totFchildren <= totMchildren){
				hhRelFemale[1][HouseholdRelationship.Student.ordinal()] = studentInAgeGroup15_24Code; 
			}
			else {
				hhRelMale[1][HouseholdRelationship.Student.ordinal()] = studentInAgeGroup15_24Code; 
			}
			
			// set the total number of Student to 1
			int totalNumberOfStudent = 1;
			nppStu = totalNumberOfStudent;
		}


		if (maxFamiStu == 0 && nppStu != 0) {
			if (newnHF[FamilyCompositionNote.HF1.ordinal()][1] - nppStu < 0) {
                logger.warn("Can't adjust households to fit nppStu! More sophisticated algorithm required!");
				return null;
			}
			stuCorrected = true;
			// sets the number of households of type HF7 to the number of
			// Student individuals.
			// this is because this household type has only Student, no U15Child
			// nor O15Child
			// thus the number of households of this type is not affected by the
			// number
			// of 'U15Child' or 'O15Child' individuals
			newnHF[FamilyCompositionNote.HF7.ordinal()][1] = nppStu;
			// reduce the number of households in HF1 to reserve the total
			// number of households
			// having 2 parents
			newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
					.ordinal()][1] - nppStu;
		} else if (maxFamiStu > nppStu) {
			stuCorrected = true;
			int nhh2Prem = nhh2Pcrn;
			int nhh1Prem = 0;
			if (nppStu - nhh1Pcrn >= 0) {
				nhh2Prem = nhh2Pcrn - (nppStu - nhh1Pcrn);
			}
			else {
				nhh1Prem = nhh1Pcrn - nppStu;
			}
			
			int[] newtmpnHF = null;
			int[] tmpnHFIdx = null;
			if (u15Corrected) {
				// start correcting hholds having 2 parents and Stu
				int tmpnhh = newnHF[FamilyCompositionNote.HF6.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF7.ordinal()][1];
				if (tmpnhh - nhh2Prem >= 0) {
					int[] tmpnHF = new int[] {
							newnHF[FamilyCompositionNote.HF6.ordinal()][1],
							newnHF[FamilyCompositionNote.HF7.ordinal()][1] };
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF6.ordinal(),
							FamilyCompositionNote.HF7.ordinal() };
					newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
							- nhh2Prem);
					// dissipating the number of hholds just removed to HF1
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + nhh2Prem;
				} else {
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF6.ordinal(),
							FamilyCompositionNote.HF7.ordinal() };
					newtmpnHF = new int[] { 0, 0 };
					// dissipating the number of hholds just removed to HF1
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + tmpnhh;
					nhh1Prem = nhh1Prem + nhh2Prem - tmpnhh;
				}
				for (int i = 0; i <= newtmpnHF.length - 1; i++) {
					newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				}

				// start correcting hholds having 1 parent and Stu
				tmpnhh = newnHF[FamilyCompositionNote.HF13.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF14.ordinal()][1];
				if (tmpnhh - nhh1Prem >= 0) {
					// take households away from HF13 and HF14 only
					int[] tmpnHF = new int[] {
							newnHF[FamilyCompositionNote.HF13.ordinal()][1],
							newnHF[FamilyCompositionNote.HF14.ordinal()][1] };
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF13.ordinal(),
							FamilyCompositionNote.HF14.ordinal() };
					newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
							- nhh1Prem);
				} else {
                    logger.warn("(2) Can't adjust households to fit nppStu! More sophisticated algorithm required!");
					return null;
				}
				// dissipating the number of hholds just removed to HF15
				newnHF[FamilyCompositionNote.HF15.ordinal()][1] = newnHF[FamilyCompositionNote.HF15
						.ordinal()][1] + nhh1Prem;
				for (int i = 0; i <= newtmpnHF.length - 1; i++) {
					newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				}
			} else {
				// start correcting hholds having 2 parents and Stu
				int[] tmpnHF = new int[] {
						newnHF[FamilyCompositionNote.HF2.ordinal()][1],
						newnHF[FamilyCompositionNote.HF3.ordinal()][1],
						newnHF[FamilyCompositionNote.HF6.ordinal()][1],
						newnHF[FamilyCompositionNote.HF7.ordinal()][1] };
				tmpnHFIdx = new int[] { FamilyCompositionNote.HF2.ordinal(),
						FamilyCompositionNote.HF3.ordinal(),
						FamilyCompositionNote.HF6.ordinal(),
						FamilyCompositionNote.HF7.ordinal() };
				newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, nhh2Pcrn
						- nhh2Prem);
				for (int i = 0; i <= newtmpnHF.length - 1; i++) {
					newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				}
				
				// dissipating the number of hholds just removed to HF1
				newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
						.ordinal()][1] + nhh2Prem;

				// start correcting hholds having 1 parent and Stu
				tmpnHF = new int[] {
						newnHF[FamilyCompositionNote.HF9.ordinal()][1],
						newnHF[FamilyCompositionNote.HF10.ordinal()][1],
						newnHF[FamilyCompositionNote.HF13.ordinal()][1],
						newnHF[FamilyCompositionNote.HF14.ordinal()][1] };
				tmpnHFIdx = new int[] { FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF14.ordinal() };
				newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, nhh1Pcrn
						- nhh1Prem);
				for (int i = 0; i <= newtmpnHF.length - 1; i++) {
					newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				}
				
				/*
				 * // dissipating the number of hholds just removed to HF11,
				 * HF12, HF15 tmpnHF = new int[] {
				 * newnHF[family_composition_note.HF11.ordinal()][1],
				 * newnHF[family_composition_note.HF12.ordinal()][1],
				 * newnHF[family_composition_note.HF15.ordinal()][1] };
				 * tmpnHFIdx = new int[] {
				 * family_composition_note.HF11.ordinal(),
				 * family_composition_note.HF12.ordinal(),
				 * family_composition_note.HF15.ordinal() }; int tmpnhh =
				 * newnHF[family_composition_note.HF11.ordinal()][1] +
				 * newnHF[family_composition_note.HF12.ordinal()][1] +
				 * newnHF[family_composition_note.HF15.ordinal()][1]; newtmpnHF
				 * = arrHdlr.allocateProportionally(tmpnHF, tmpnhh + nhh1Prem);
				 * for (int i = 0; i <= newtmpnHF.length - 1; i++)
				 * newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				 */
				// dissipating the number of hholds just removed to HF15
				newnHF[FamilyCompositionNote.HF15.ordinal()][1] = newnHF[FamilyCompositionNote.HF15
						.ordinal()][1] + nhh1Prem;
			}
		}

		// check if number of family hholds satisfy number of O15 children
		boolean o15Corrected = false;
		nhh2Pcrn = newnHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF8.ordinal()][1];
		nhh1Pcrn = newnHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF15.ordinal()][1];
		int maxFamiO15 = nhh2Pcrn + nhh1Pcrn;

		if (maxFamiO15 != 0 && nppO15 == 0) {
			// this is kind of a special case (again)! see further comments
			// above in U15 section.
			int totMchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelMale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.O15Child.ordinal()));
			int totFchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelFemale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.O15Child.ordinal()));
			
			int o15ChildInAgeGroup15_40Code = 1;
			if (totFchildren <= totMchildren) {
				hhRelFemale[1][HouseholdRelationship.O15Child.ordinal()] = o15ChildInAgeGroup15_40Code; 
			}
			else  {
				hhRelMale[1][HouseholdRelationship.O15Child.ordinal()] = o15ChildInAgeGroup15_40Code; 
			}
			// set the total number of O15Child to 1
			int totalNumberOfO15Child = 1;
			nppO15 = totalNumberOfO15Child;
		}


		if (maxFamiO15 == 0 && nppO15 != 0) {
			if (newnHF[FamilyCompositionNote.HF1.ordinal()][1] - nppO15 < 0) {
                logger.warn("Can't adjust households to fit nppO15! More sophisticated algorithm required!");
				return null;
			}
			o15Corrected = true;
			// sets the number of households of type HF8 to the number of
			// O15Child individuals.
			// this is because this household type has only O15Child, no
			// U15Child nor Student
			// thus the number of households of this type is not affected by the
			// number
			// of 'U15Child' or 'Student' individuals
			newnHF[FamilyCompositionNote.HF8.ordinal()][1] = nppO15;
			// reduce the number of households in HF1 to reserve the total
			// number of households
			// having 2 parents
			newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
					.ordinal()][1] - nppO15;
		} else if (maxFamiO15 > nppO15) {
			o15Corrected = true;
			int nhh2Prem = nhh2Pcrn;
			int nhh1Prem = 0;
			if (nppO15 - nhh1Pcrn >= 0) {
				nhh2Prem = nhh2Pcrn - (nppO15 - nhh1Pcrn);
			}
			else {
				nhh1Prem = nhh1Pcrn - nppO15;
			}
			
			int[] newtmpnHF = null;
			int[] tmpnHFIdx = null;
			if (!u15Corrected && !stuCorrected) {
				// start correcting hholds having 2 parents and O15
				int[] tmpnHF = new int[] {
						newnHF[FamilyCompositionNote.HF2.ordinal()][1],
						newnHF[FamilyCompositionNote.HF4.ordinal()][1],
						newnHF[FamilyCompositionNote.HF6.ordinal()][1],
						newnHF[FamilyCompositionNote.HF8.ordinal()][1] };
				tmpnHFIdx = new int[] { FamilyCompositionNote.HF2.ordinal(),
						FamilyCompositionNote.HF4.ordinal(),
						FamilyCompositionNote.HF6.ordinal(),
						FamilyCompositionNote.HF8.ordinal() };
				newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, nhh2Pcrn
						- nhh2Prem);
				for (int i = 0; i <= newtmpnHF.length - 1; i++) {
					newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				}
				
				// dissipating the number of hholds just removed to HF1
				newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
						.ordinal()][1] + nhh2Prem;

				// start correcting hholds having 1 parents and O15
				tmpnHF = new int[] {
						newnHF[FamilyCompositionNote.HF9.ordinal()][1],
						newnHF[FamilyCompositionNote.HF11.ordinal()][1],
						newnHF[FamilyCompositionNote.HF13.ordinal()][1],
						newnHF[FamilyCompositionNote.HF15.ordinal()][1] };
				tmpnHFIdx = new int[] { FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF11.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF15.ordinal() };
				newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, nhh1Pcrn
						- nhh1Prem);
				for (int i = 0; i <= newtmpnHF.length - 1; i++) {
					newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				}
				
				// dissipating the number of hholds just removed to HF10, HF12,
				// HF14
				int tmpnhh = newnHF[FamilyCompositionNote.HF10.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF12.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF14.ordinal()][1];
				tmpnHF = new int[] {
						newnHF[FamilyCompositionNote.HF10.ordinal()][1],
						newnHF[FamilyCompositionNote.HF12.ordinal()][1],
						newnHF[FamilyCompositionNote.HF14.ordinal()][1] };
				tmpnHFIdx = new int[] { FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF12.ordinal(),
						FamilyCompositionNote.HF14.ordinal() };
				newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
						+ nhh1Prem);
				for (int i = 0; i <= newtmpnHF.length - 1; i++) {
					newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
				}

			} else if (u15Corrected && !stuCorrected) {
				// start correcting hholds having 2 parents and O15
				int tmpnhh = newnHF[FamilyCompositionNote.HF6.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF8.ordinal()][1];
				if (tmpnhh - nhh2Prem >= 0) {
					int[] tmpnHF = new int[] {
							newnHF[FamilyCompositionNote.HF6.ordinal()][1],
							newnHF[FamilyCompositionNote.HF8.ordinal()][1] };
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF6.ordinal(),
							FamilyCompositionNote.HF8.ordinal() };
					newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
							- nhh2Prem);
					for (int i = 0; i <= newtmpnHF.length - 1; i++) {
						newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
					}
					
					// dissipating the number of hholds just removed to HF1
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + nhh2Prem;
				} else {
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF6.ordinal(),
							FamilyCompositionNote.HF8.ordinal() };
					newtmpnHF = new int[] { 0, 0 };
					for (int i = 0; i <= newtmpnHF.length - 1; i++) {
						newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
					}
					
					// dissipating the number of hholds just removed to HF1
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + tmpnhh;
					nhh1Prem = nhh1Prem + nhh2Prem - tmpnhh;
				}

				// start correcting hholds having 1 parent and O15
				tmpnhh = newnHF[FamilyCompositionNote.HF13.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF15.ordinal()][1];
				if (tmpnhh - nhh1Prem >= 0) {
					int[] tmpnHF = new int[] {
							newnHF[FamilyCompositionNote.HF13.ordinal()][1],
							newnHF[FamilyCompositionNote.HF15.ordinal()][1] };
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF13.ordinal(),
							FamilyCompositionNote.HF15.ordinal() };
					newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
							- nhh1Prem);
					for (int i = 0; i <= newtmpnHF.length - 1; i++) {
						newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
					}
					
					// dissipating the number of hholds just removed to HF14
					newnHF[FamilyCompositionNote.HF14.ordinal()][1] = newnHF[FamilyCompositionNote.HF14
							.ordinal()][1] + nhh1Prem;
				} else {
                    logger.warn("(2) Can't adjust households to fit nppO15! More sophisticated algorithm required!");
					return null;
				}
			} else if (!u15Corrected && stuCorrected) {
				// start correcting hholds having 2 parents and O15
				int tmpnhh = newnHF[FamilyCompositionNote.HF4.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF8.ordinal()][1];
				if (tmpnhh - nhh2Prem >= 0) {
					int[] tmpnHF = new int[] {
							newnHF[FamilyCompositionNote.HF4.ordinal()][1],
							newnHF[FamilyCompositionNote.HF8.ordinal()][1] };
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF4.ordinal(),
							FamilyCompositionNote.HF8.ordinal() };
					newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
							- nhh2Prem);
					for (int i = 0; i <= newtmpnHF.length - 1; i++) {
						newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
					}
					
					// dissipating the number of hholds just removed to HF1
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + nhh2Prem;
				} else {
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF4.ordinal(),
							FamilyCompositionNote.HF8.ordinal() };
					newtmpnHF = new int[] { 0, 0 };
					for (int i = 0; i <= newtmpnHF.length - 1; i++) {
						newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
					}
					
					// dissipating the number of hholds just removed to HF1
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + tmpnhh;
					nhh1Prem = nhh1Prem + nhh2Prem - tmpnhh;
				}

				// start correcting hholds having 1 parent and O15
				tmpnhh = newnHF[FamilyCompositionNote.HF11.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF15.ordinal()][1];
				if (tmpnhh - nhh1Prem >= 0) {
					int[] tmpnHF = new int[] {
							newnHF[FamilyCompositionNote.HF11.ordinal()][1],
							newnHF[FamilyCompositionNote.HF15.ordinal()][1] };
					tmpnHFIdx = new int[] {
							FamilyCompositionNote.HF11.ordinal(),
							FamilyCompositionNote.HF15.ordinal() };
					newtmpnHF = arrHdlr.allocateProportionally(tmpnHF, tmpnhh
							- nhh1Prem);
					for (int i = 0; i <= newtmpnHF.length - 1; i++) {
						newnHF[tmpnHFIdx[i]][1] = newtmpnHF[i];
					}
					// dissipating the number of hholds just removed to HF12
					newnHF[FamilyCompositionNote.HF12.ordinal()][1] = newnHF[FamilyCompositionNote.HF12
							.ordinal()][1] + nhh1Prem;
				} else {
                    logger.warn("(2) Can't adjust households to fit nppO15! More sophisticated algorithm required!");
					return null;
				}
			} else if (u15Corrected && stuCorrected) {
				// start correcting hholds having 2 parents and O15
				int tmpnhh = newnHF[FamilyCompositionNote.HF8.ordinal()][1];
				if (tmpnhh - nhh2Prem >= 0) {
					newnHF[FamilyCompositionNote.HF8.ordinal()][1] = tmpnhh
							- nhh2Prem;
					// dissipating the number of hholds just removed to HF1
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + nhh2Prem;
				} else {
                    logger.warn("(2) Can't adjust households to fit nppO15! More sophisticated algorithm required!");
					return null;
				}
			}
		}

		// check if number of 'Other' families <= half of number of relatives
		// and
		// non-relatives
		if (newnHF[FamilyCompositionNote.HF16.ordinal()][1] > nppRelatives / 2) {
			newnHF[FamilyCompositionNote.HF16.ordinal()][1] = nppRelatives / 2;
		}

		return newnHF;
	}

	/**
	 * adjusts tables hhRelMale and hhRelFemale so that the total number of
	 * U15Child, Student, and O15Child individuals
	 * 
	 * @param tot1Phhold
	 */
	private void correctChildrenInHholdRelTable(int tot1Phhold) {
		ArrayHandler arrHdlr = new ArrayHandler();
		int[] totChildType = new int[] {
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.U15Child.ordinal())),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.Student.ordinal())),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.O15Child.ordinal())),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.U15Child.ordinal())),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.Student.ordinal())),
				arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.O15Child.ordinal())) };

		if (arrHdlr.sumPositiveInArray(totChildType) == 0)
			totChildType = new int[] { 1, 1, 1, 1, 1, 1 };

		totChildType = arrHdlr.allocateProportionally(totChildType, tot1Phhold);

		int[] crnMU15 = arrHdlr.getColumn(hhRelMale,
				HouseholdRelationship.U15Child.ordinal());
		if (arrHdlr.sumPositiveInArray(crnMU15) == 0) {
			for (int i = 0; i <= crnMU15.length - 1; i++) {
				if (crnMU15[i] < 0) {
					crnMU15[i] = -1;
				}
				else {
					crnMU15[i] = 1;
				}
			}
		}
		
		int[] newMU15 = arrHdlr
				.allocateProportionally(crnMU15, totChildType[0]);

		int[] crnMStu = arrHdlr.getColumn(hhRelMale,
				HouseholdRelationship.Student.ordinal());
		if (arrHdlr.sumPositiveInArray(crnMStu) == 0) {
			for (int i = 0; i <= crnMStu.length - 1; i++) {
				if (crnMStu[i] < 0) {
					crnMStu[i] = -1;
				}
				else {
					crnMStu[i] = 1;
				}
			}
		}
		
		int[] newMStu = arrHdlr
				.allocateProportionally(crnMStu, totChildType[1]);

		int[] crnMO15 = arrHdlr.getColumn(hhRelMale,
				HouseholdRelationship.O15Child.ordinal());
		if (arrHdlr.sumPositiveInArray(crnMO15) == 0) {
			for (int i = 0; i <= crnMO15.length - 1; i++) {
				if (crnMO15[i] < 0) {
					crnMO15[i] = -1;
				}
				else {
					crnMO15[i] = 1;
				}
			}
		}
		
		int[] newMO15 = arrHdlr
				.allocateProportionally(crnMO15, totChildType[2]);

		int[] crnFU15 = arrHdlr.getColumn(hhRelFemale,
				HouseholdRelationship.U15Child.ordinal());
		if (arrHdlr.sumPositiveInArray(crnFU15) == 0) {
			for (int i = 0; i <= crnFU15.length - 1; i++) {
				if (crnFU15[i] < 0) {
					crnFU15[i] = -1;
				}
				else {
					crnFU15[i] = 1;
				}
			}
		}
		
		int[] newFU15 = arrHdlr
				.allocateProportionally(crnFU15, totChildType[3]);

		int[] crnFStu = arrHdlr.getColumn(hhRelFemale,
				HouseholdRelationship.Student.ordinal());
		if (arrHdlr.sumPositiveInArray(crnFStu) == 0) {
			for (int i = 0; i <= crnFStu.length - 1; i++) {
				if (crnFStu[i] < 0) {
					crnFStu[i] = -1;
				}
				else {
					crnFStu[i] = 1;
				}
			}
		}
		
		int[] newFStu = arrHdlr
				.allocateProportionally(crnFStu, totChildType[4]);

		int[] crnFO15 = arrHdlr.getColumn(hhRelFemale,
				HouseholdRelationship.O15Child.ordinal());
		if (arrHdlr.sumPositiveInArray(crnFO15) == 0) {
			for (int i = 0; i <= crnFO15.length - 1; i++) {
				if (crnFO15[i] < 0) {
					crnFO15[i] = -1;
				}
				else {
					crnFO15[i] = 1;
				}
			}
		}
		int[] newFO15 = arrHdlr
				.allocateProportionally(crnFO15, totChildType[5]);

		hhRelMale = arrHdlr.replaceColumn(hhRelMale,
				HouseholdRelationship.U15Child.ordinal(), newMU15);
		hhRelMale = arrHdlr.replaceColumn(hhRelMale,
				HouseholdRelationship.Student.ordinal(), newMStu);
		hhRelMale = arrHdlr.replaceColumn(hhRelMale,
				HouseholdRelationship.O15Child.ordinal(), newMO15);
		hhRelFemale = arrHdlr.replaceColumn(hhRelFemale,
				HouseholdRelationship.U15Child.ordinal(), newFU15);
		hhRelFemale = arrHdlr.replaceColumn(hhRelFemale,
				HouseholdRelationship.Student.ordinal(), newFStu);
		hhRelFemale = arrHdlr.replaceColumn(hhRelFemale,
				HouseholdRelationship.O15Child.ordinal(), newFO15);

	}

	/**
	 * adjusts the number of households in each family household type. In other
	 * words, this methods adjusts the values in ABS table B24. This correction
	 * is to ensure that the number of households is enough to accommodate the
	 * number of children (including U15Child, Student, O51Child) and
	 * Relative/NonRelative individuals. Note that these numbers must have been
	 * adjusted once (in adjustNumberOfHF) to ensure that number of family
	 * households with 1 and 2 parents matches with the number of
	 * Married/DeFacto and LonePerson individuals, respectively.
	 * 
	 * @param orinHF
	 *            array specifying the number of households in each family
	 *            household cateagory (formatted originally from ABS table 24).
	 *            Columns are [index of household category as in
	 *            min_individuals_note, number of households]
	 * @param nppU15
	 *            total number of children under 15 (males and females).
	 * @param nppStu
	 *            total number of students (males and females).
	 * @param nppO15
	 *            total number of children over 15 (males and females).
	 * @param nppRelatives
	 *            total number of Relative and NonRelative individuals (males
	 *            and females).
	 * @return corrected number of households in each family household category
	 *         (formatted similar to nHF)
	 */
	private int[][] testNumberofHhold2(int[][] orinHF, int nppU15, int nppStu,
			int nppO15, int nppRelatives) {
		int[][] newnHF = new int[orinHF.length][orinHF[0].length];
		for (int i = 0; i <= newnHF.length - 1; i++) {
			for (int j = 0; j <= newnHF[0].length - 1; j++) {
				newnHF[i][j] = orinHF[i][j];
			}
		}
		ArrayHandler arrHdlr = new ArrayHandler();

		// check if number of 'Other' families <= half of number of relatives
		// and
		// non-relatives
		if (newnHF[FamilyCompositionNote.HF16.ordinal()][1] > nppRelatives / 2) {
			newnHF[FamilyCompositionNote.HF16.ordinal()][1] = nppRelatives / 2;
			// logger.debug("'Other' families corrected");
		}

		// now starts correcting number of households having parents/children
		// but considers very special cases first

		// if the total number of children <= number of lone parents
		// (or number of households in HF9 to HF15)
		int totChildren = nppU15 + nppStu + nppO15;
		int tot1Phhold = newnHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF12.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF14.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF15.ordinal()][1];
		if (totChildren < tot1Phhold) {
			// adjusts hhRelMale and/or hhRelFemale so that the total number of
			// children
			// increases by an amount equal to tot1Phhold.
			correctChildrenInHholdRelTable(tot1Phhold);

			// recalculates nppU15, nppStu, and nppO15
			int colU15 = HouseholdRelationship.U15Child.ordinal();
			int colStu = HouseholdRelationship.Student.ordinal();
			int colO15 = HouseholdRelationship.O15Child.ordinal();
			nppU15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
					colU15))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							colU15));
			nppStu = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
					colStu))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							colStu));
			nppO15 = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
					colO15))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							colO15));

			// then recalculates totChildren
			totChildren = nppU15 + nppStu + nppO15;
		}

		if (totChildren == tot1Phhold) {
			// This special case implies that all children must be in 1 parent
			// households.
			// More specifically all nppU15 will be in HF12 households (which
			// contain U15Child ONLY),
			// all nppStu will be in HF14 households (which contain Student
			// ONLY),
			// and all nppO15 will be in HF15 households (which contain O15Child
			// ONLY).
			// Another implication of this is types HF2 to HF8 have 0
			// households, or
			// number of households in HF1 equals to half of Married/Defacto
			// individuals.

			// pushes all households in HF2-HF8 to HF1
			int[] nHF2HF8 = new int[] { FamilyCompositionNote.HF2.ordinal(),
					FamilyCompositionNote.HF3.ordinal(),
					FamilyCompositionNote.HF4.ordinal(),
					FamilyCompositionNote.HF5.ordinal(),
					FamilyCompositionNote.HF6.ordinal(),
					FamilyCompositionNote.HF7.ordinal(),
					FamilyCompositionNote.HF8.ordinal() };
			int sumHF2HF8 = 0;
			for (int i = 0; i <= nHF2HF8.length - 1; i++) {
				sumHF2HF8 = sumHF2HF8 + newnHF[nHF2HF8[i]][1];
				newnHF[nHF2HF8[i]][1] = 0;
			}
			newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
					.ordinal()][1] + sumHF2HF8;

			// allocates nppU15 households to HF12
			newnHF[FamilyCompositionNote.HF12.ordinal()][1] = nppU15;
			// allocates nppStu households to HF14
			newnHF[FamilyCompositionNote.HF14.ordinal()][1] = nppStu;
			// allocates nppO15 households to HF15
			newnHF[FamilyCompositionNote.HF15.ordinal()][1] = nppO15;

			// sets number of households in HF9, HF10, HF11, HF13 to 0.
			newnHF[FamilyCompositionNote.HF9.ordinal()][1] = 0;
			newnHF[FamilyCompositionNote.HF10.ordinal()][1] = 0;
			newnHF[FamilyCompositionNote.HF11.ordinal()][1] = 0;
			newnHF[FamilyCompositionNote.HF13.ordinal()][1] = 0;

			return newnHF;
		}

		// boolean u15Corrected = false;
		int nhh2Pcrn = newnHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF5.ordinal()][1];
		int nhh1Pcrn = newnHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF12.ordinal()][1];
		int maxFamiU15 = nhh2Pcrn + nhh1Pcrn;

		if (maxFamiU15 != 0 && nppU15 == 0) {
			// this is kind of a special case.
			// Ideally we should be strict on using numbers from B22a as a
			// reference, and thus in this case
			// adjust the number of families having U15Child to 0.
			// Even though this approach is fairly easily implemented for
			// U15Child,
			// it likely will open the pandora's box when dealing with the case
			// nppStu==0 or nppO15==0.
			// (Because we got to set number of households, say having O15Child,
			// to 0
			// while maintaining satisfactory numbers of households having
			// U15Child and those having
			// Student - quite a headache for Namy!)
			// Thus for the sake of having a simple and consitent solution to
			// this issue/case, we set the
			// number of U15Child (or Student or O15Child if these cases happen)
			// to 1.
			// - Whether 1 male or 1 female depends on which one has the smaller
			// number of children
			// (towards balancing number of males and females).
			// - For U15Child, set 1 to age group 0-14
			// - For Student and O15Child, set 1 to age group 15-24
			int totMchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelMale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.O15Child.ordinal()));
			int totFchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelFemale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.O15Child.ordinal()));
			
			// set 1 to U15Child in age group 0-4
			int u15ChildInAgeGroup0_4Code = 1;
			if (totFchildren <= totMchildren){
				hhRelFemale[0][HouseholdRelationship.U15Child.ordinal()] = u15ChildInAgeGroup0_4Code; 
			}
			// set 1 to U15Child in age group 0-4
			else {
				hhRelMale[0][HouseholdRelationship.U15Child.ordinal()] = u15ChildInAgeGroup0_4Code;
			}
			// set the total number of U15Child to 1
			int totalNumOfU15Child = 1;
			nppU15 = totalNumOfU15Child;
		}

		if (maxFamiU15 == 0 && nppU15 != 0) {
			if (newnHF[FamilyCompositionNote.HF1.ordinal()][1] - nppU15 < 0) {
                logger.warn("Can't adjust households to fit nppU15! More sophisticated algorithm required!");
				return null;
			}
			// sets the number of households of types HF12 and HF5 (if needed)
			// to the number of
			// U15Child individuals.
			// this is because these household types have only U15Child, no
			// Students nor O15Child,
			// thus the number of households of this type is not affected by the
			// number of 'Student'
			// or 'O15Child' individuals.
			if (nppU15 <= tot1Phhold) {
				newnHF[FamilyCompositionNote.HF12.ordinal()][1] = nppU15;
				// reduces the number of households in other 1P households by an
				// amount of nppU15
				int[] other1Phhold = new int[] {
						newnHF[FamilyCompositionNote.HF9.ordinal()][1],
						newnHF[FamilyCompositionNote.HF10.ordinal()][1],
						newnHF[FamilyCompositionNote.HF11.ordinal()][1],
						newnHF[FamilyCompositionNote.HF13.ordinal()][1],
						newnHF[FamilyCompositionNote.HF14.ordinal()][1],
						newnHF[FamilyCompositionNote.HF15.ordinal()][1] };
				int[] other1PhholdIdx = new int[] {
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF11.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF14.ordinal(),
						FamilyCompositionNote.HF15.ordinal() };
				other1Phhold = arrHdlr.allocateProportionally(other1Phhold,
						tot1Phhold - nppU15);
				for (int i = 0; i <= other1PhholdIdx.length - 1; i++) {
					newnHF[other1PhholdIdx[i]][1] = other1Phhold[i];
				}
			} else {
				newnHF[FamilyCompositionNote.HF12.ordinal()][1] = tot1Phhold;
				int[] other1PhholdIdx = new int[] {
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF11.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF14.ordinal(),
						FamilyCompositionNote.HF15.ordinal() };
				for (int i = 0; i <= other1PhholdIdx.length - 1; i++) {
					newnHF[other1PhholdIdx[i]][1] = 0;
				}

				// assigns the remaining number of households required to HF5
				newnHF[FamilyCompositionNote.HF5.ordinal()][1] = nppU15
						- tot1Phhold;
				// reduces the number of households in HF1 by the same amount to
				// reserve the
				// total number of households having 2 parents
				newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
						.ordinal()][1] - (nppU15 - tot1Phhold);
			}
		} else if (maxFamiU15 > nppU15) { // if number of hholds having U15 is
											// greater than the number of U15
											// indiv
			int nhh2Prem = nhh2Pcrn;
			int nhh1Prem = 0;
			if (nppU15 - nhh1Pcrn >= 0) {
				nhh2Prem = nhh2Pcrn - (nppU15 - nhh1Pcrn);
			}
			else {
				nhh1Prem = nhh1Pcrn - nppU15;
			}

			// start correcting hhold with 2 parents and U15
			// by reducing the number of hholds of types HF2 to HF5
			int dissnHF2Phh = 0;
			if (nhh2Pcrn - nhh2Prem >= 0) {
				int[] nHF2P = new int[] {
						newnHF[FamilyCompositionNote.HF2.ordinal()][1],
						newnHF[FamilyCompositionNote.HF3.ordinal()][1],
						newnHF[FamilyCompositionNote.HF4.ordinal()][1],
						newnHF[FamilyCompositionNote.HF5.ordinal()][1] };
				int[] nHF2PIdx = new int[] {
						FamilyCompositionNote.HF2.ordinal(),
						FamilyCompositionNote.HF3.ordinal(),
						FamilyCompositionNote.HF4.ordinal(),
						FamilyCompositionNote.HF5.ordinal() };
				nHF2P = arrHdlr.allocateProportionally(nHF2P, nhh2Pcrn
						- nhh2Prem);
				for (int i = 0; i <= nHF2P.length - 1; i++) {
					newnHF[nHF2PIdx[i]][1] = nHF2P[i];
				}
				
				dissnHF2Phh = nhh2Prem;
			} else {
				int[] nHF2P = new int[] { 0, 0, 0, 0 };
				int[] nHF2PIdx = new int[] {
						FamilyCompositionNote.HF2.ordinal(),
						FamilyCompositionNote.HF3.ordinal(),
						FamilyCompositionNote.HF4.ordinal(),
						FamilyCompositionNote.HF5.ordinal() };
				for (int i = 0; i <= nHF2P.length - 1; i++) {
					newnHF[nHF2PIdx[i]][1] = nHF2P[i];
				}
				
				dissnHF2Phh = nhh2Pcrn;
				nhh1Prem = nhh1Prem + (nhh2Prem - nhh2Pcrn);
			}
			// dissipating the number of hholds just removed to hhold types HF1,
			// HF6, HF7, and HF8
			int[] dissnHF = new int[] {
					newnHF[FamilyCompositionNote.HF1.ordinal()][1],
					newnHF[FamilyCompositionNote.HF6.ordinal()][1],
					newnHF[FamilyCompositionNote.HF7.ordinal()][1],
					newnHF[FamilyCompositionNote.HF8.ordinal()][1] };
			if (arrHdlr.sumPositiveInArray(dissnHF) == 0) {
				dissnHF = new int[] { 1, 1, 1, 1 };
			}
			
			dissnHF = arrHdlr.allocateProportionally(dissnHF, dissnHF2Phh);
			int[] dissnHFIdx = new int[] {
					FamilyCompositionNote.HF1.ordinal(),
					FamilyCompositionNote.HF6.ordinal(),
					FamilyCompositionNote.HF7.ordinal(),
					FamilyCompositionNote.HF8.ordinal() };
			for (int i = 0; i <= dissnHFIdx.length - 1; i++) {
				newnHF[dissnHFIdx[i]][1] = newnHF[dissnHFIdx[i]][1]
						+ dissnHF[i];
			}
			

			// start correcting hhold with 1 parents and U15
			if (nhh1Pcrn - nhh1Prem >= 0) {
				int[] nHF1P = new int[] {
						newnHF[FamilyCompositionNote.HF12.ordinal()][1],
						newnHF[FamilyCompositionNote.HF9.ordinal()][1],
						newnHF[FamilyCompositionNote.HF10.ordinal()][1],
						newnHF[FamilyCompositionNote.HF11.ordinal()][1] };
				int[] nHF1PIdx = new int[] {
						FamilyCompositionNote.HF12.ordinal(),
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF11.ordinal() };
				nHF1P = arrHdlr.allocateProportionally(nHF1P, nhh1Pcrn
						- nhh1Prem);
				for (int i = 0; i <= nHF1PIdx.length - 1; i++) {
					newnHF[nHF1PIdx[i]][1] = nHF1P[i];
				}
				
				// dissipating the number of hholds just removed to HF13, HF14,
				// HF15
				dissnHF = new int[] {
						newnHF[FamilyCompositionNote.HF13.ordinal()][1],
						newnHF[FamilyCompositionNote.HF14.ordinal()][1],
						newnHF[FamilyCompositionNote.HF15.ordinal()][1] };

				if (arrHdlr.sumPositiveInArray(dissnHF) == 0) {
					dissnHF = new int[] { 1, 1, 1 };
				}
				
				dissnHF = arrHdlr.allocateProportionally(dissnHF, nhh1Prem);
				dissnHFIdx = new int[] {
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF14.ordinal(),
						FamilyCompositionNote.HF15.ordinal() };
				for (int i = 0; i <= dissnHFIdx.length - 1; i++) {
					newnHF[dissnHFIdx[i]][1] = newnHF[dissnHFIdx[i]][1]
							+ dissnHF[i];
				}
			} else {
                logger.warn("Can't adjust households to fit nppU15! More sophisticated algorithm required!");
				return null;
			}
		}

		// boolean stuCorrected = false;
		// check if number of family hholds satisfy number of students
		nhh2Pcrn = newnHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF7.ordinal()][1];
		nhh1Pcrn = newnHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF14.ordinal()][1];
		int maxFamiStu = nhh2Pcrn + nhh1Pcrn;

		if (maxFamiStu != 0 && nppStu == 0) {
			// this is kind of a special case (again)! see further comments
			// above in U15 section.
			int totMchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelMale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.O15Child.ordinal()));
			int totFchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelFemale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.O15Child.ordinal()));
			
			// set 1 to Student in age group 15-24
			int studentInAgeGroup15_24Code = 1;
			if (totFchildren <= totMchildren) {
				hhRelFemale[1][HouseholdRelationship.Student.ordinal()] = studentInAgeGroup15_24Code;
			}
			 // set 1 to Student in age group 15-24
			else {
				hhRelMale[1][HouseholdRelationship.Student.ordinal()] = studentInAgeGroup15_24Code; 
			}
			
			// set the total number of Student to 1
			int totalNumOfStudent = 1;
			nppStu = totalNumOfStudent;
		}

		if (maxFamiStu == 0 && nppStu != 0) {
			if (newnHF[FamilyCompositionNote.HF1.ordinal()][1] - nppStu < 0) {
                logger.warn("Can't adjust households to fit nppStu! More sophisticated algorithm required!");
				return null;
			}
			// sets the number of households of types HF14 and HF7 (if needed)
			// to the number of
			// Student individuals.
			// this is because these household types have only Student, no
			// U15Child nor O15Child,
			// thus the number of households of this type is not affected by the
			// number
			// of 'U15Child' or 'O15Child' individuals
			if (nppStu <= tot1Phhold) {
				newnHF[FamilyCompositionNote.HF14.ordinal()][1] = nppStu;
				// reduces the number of households in other 1P households by an
				// amount of nppStu
				int[] other1Phhold = new int[] {
						newnHF[FamilyCompositionNote.HF9.ordinal()][1],
						newnHF[FamilyCompositionNote.HF10.ordinal()][1],
						newnHF[FamilyCompositionNote.HF11.ordinal()][1],
						newnHF[FamilyCompositionNote.HF12.ordinal()][1],
						newnHF[FamilyCompositionNote.HF13.ordinal()][1],
						newnHF[FamilyCompositionNote.HF15.ordinal()][1] };
				int[] other1PhholdIdx = new int[] {
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF11.ordinal(),
						FamilyCompositionNote.HF12.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF15.ordinal() };
				other1Phhold = arrHdlr.allocateProportionally(other1Phhold,
						tot1Phhold - nppStu);
				for (int i = 0; i <= other1PhholdIdx.length - 1; i++) {
					newnHF[other1PhholdIdx[i]][1] = other1Phhold[i];
				}
			} else {
				newnHF[FamilyCompositionNote.HF14.ordinal()][1] = tot1Phhold;
				int[] other1PhholdIdx = new int[] {
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF11.ordinal(),
						FamilyCompositionNote.HF12.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF15.ordinal() };
				for (int i = 0; i <= other1PhholdIdx.length - 1; i++) {
					newnHF[other1PhholdIdx[i]][1] = 0;
				}
				
				// assigns the remaining number of households required to HF7
				newnHF[FamilyCompositionNote.HF7.ordinal()][1] = nppStu
						- tot1Phhold;
				// reduces the number of households in HF1 by the same amount to
				// reserve the
				// total number of households having 2 parents
				newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
						.ordinal()][1] - (nppStu - tot1Phhold);
			}
		} else if (maxFamiStu > nppStu) {
			// stuCorrected = true;
			int nhh2Prem = nhh2Pcrn;
			int nhh1Prem = 0;
			if (nppStu - nhh1Pcrn >= 0) {
				nhh2Prem = nhh2Pcrn - (nppStu - nhh1Pcrn);
			}
			else {
				nhh1Prem = nhh1Pcrn - nppStu;
			}

			// start correcting hholds having 2 parents and Stu
			int[] nHF2P = new int[] {
					newnHF[FamilyCompositionNote.HF6.ordinal()][1],
					newnHF[FamilyCompositionNote.HF7.ordinal()][1] };
			int[] nHF2PIdx = new int[] { FamilyCompositionNote.HF6.ordinal(),
					FamilyCompositionNote.HF7.ordinal() };
			int dissnHF2Phh = 0;
			if (arrHdlr.sumPositiveInArray(nHF2P) - nhh2Prem >= 0) {
				nHF2P = arrHdlr.allocateProportionally(nHF2P,
						arrHdlr.sumPositiveInArray(nHF2P) - nhh2Prem);
				dissnHF2Phh = nhh2Prem;
			} else {
				nHF2P = new int[] {
						newnHF[FamilyCompositionNote.HF2.ordinal()][1],
						newnHF[FamilyCompositionNote.HF3.ordinal()][1],
						newnHF[FamilyCompositionNote.HF6.ordinal()][1],
						newnHF[FamilyCompositionNote.HF7.ordinal()][1] };
				nHF2PIdx = new int[] { FamilyCompositionNote.HF2.ordinal(),
						FamilyCompositionNote.HF3.ordinal(),
						FamilyCompositionNote.HF6.ordinal(),
						FamilyCompositionNote.HF7.ordinal() };
				if (arrHdlr.sumPositiveInArray(nHF2P) - nhh2Prem >= 0) {
					nHF2P = arrHdlr.allocateProportionally(nHF2P,
							arrHdlr.sumPositiveInArray(nHF2P) - nhh2Prem);
					dissnHF2Phh = nhh2Prem;
				} else {
					nHF2P = new int[] { 0, 0, 0, 0 };
					dissnHF2Phh = nhh2Pcrn;
					nhh1Prem = nhh1Prem + nhh2Prem
							- arrHdlr.sumPositiveInArray(nHF2P);
				}
			}
			for (int i = 0; i <= nHF2PIdx.length - 1; i++) {
				newnHF[nHF2PIdx[i]][1] = nHF2P[i];
			}
			
			// dissipating the number of households just removed to HF1 and HF8
			int[] dissnHF2P = new int[] {
					newnHF[FamilyCompositionNote.HF1.ordinal()][1],
					newnHF[FamilyCompositionNote.HF8.ordinal()][1] };
			if (arrHdlr.sumPositiveInArray(dissnHF2P) == 0){
				dissnHF2P = new int[] { 1, 1 };
			}
			
			dissnHF2P = arrHdlr.allocateProportionally(dissnHF2P, dissnHF2Phh);
			int[] dissnHF2PIdx = new int[] {
					FamilyCompositionNote.HF1.ordinal(),
					FamilyCompositionNote.HF8.ordinal() };
			for (int i = 0; i <= dissnHF2PIdx.length - 1; i++) {
				newnHF[dissnHF2PIdx[i]][1] = newnHF[dissnHF2PIdx[i]][1]
						+ dissnHF2P[i];
			}

			// start correcting hholds having 1 parent and Stu
			int[] nHF1P = new int[] {
					newnHF[FamilyCompositionNote.HF14.ordinal()][1],
					newnHF[FamilyCompositionNote.HF13.ordinal()][1] };
			int[] nHF1PIdx = new int[] {
					FamilyCompositionNote.HF14.ordinal(),
					FamilyCompositionNote.HF13.ordinal() };
			if (arrHdlr.sumPositiveInArray(nHF1P) >= nhh1Prem) {
				nHF1P = arrHdlr.allocateProportionally(nHF1P,
						arrHdlr.sumPositiveInArray(nHF1P) - nhh1Prem);
			} else {
				nHF1P = new int[] {
						newnHF[FamilyCompositionNote.HF14.ordinal()][1],
						newnHF[FamilyCompositionNote.HF9.ordinal()][1],
						newnHF[FamilyCompositionNote.HF10.ordinal()][1],
						newnHF[FamilyCompositionNote.HF13.ordinal()][1] };
				nHF1PIdx = new int[] { FamilyCompositionNote.HF14.ordinal(),
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF13.ordinal() };
				if (arrHdlr.sumPositiveInArray(nHF1P) >= nhh1Prem) {
					nHF1P = arrHdlr.allocateProportionally(nHF1P,
							arrHdlr.sumPositiveInArray(nHF1P) - nhh1Prem);
				} else {
                    logger.warn("Can't adjust households to fit nppStu! More sophisticated algorithm required!");
					return null;
				}
			}
			for (int i = 0; i <= nHF1PIdx.length - 1; i++) {
				newnHF[nHF1PIdx[i]][1] = nHF1P[i];
			}
			
			// dissipating the number of households just removed to HF15
			newnHF[FamilyCompositionNote.HF15.ordinal()][1] = newnHF[FamilyCompositionNote.HF15
					.ordinal()][1] + nhh1Prem;
		}


		// check if number of family hholds satisfy number of O15 children
		// boolean o15Corrected = false;
		nhh2Pcrn = newnHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF8.ordinal()][1];
		nhh1Pcrn = newnHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ newnHF[FamilyCompositionNote.HF15.ordinal()][1];
		int maxFamiO15 = nhh2Pcrn + nhh1Pcrn;

		if (maxFamiO15 != 0 && nppO15 == 0) {
			// this is kind of a special case (again)! see further comments
			// above in U15 section.
			int totMchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelMale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
							HouseholdRelationship.O15Child.ordinal()));
			int totFchildren = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
					hhRelFemale, HouseholdRelationship.U15Child.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.Student.ordinal()))
					+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
							HouseholdRelationship.O15Child.ordinal()));
			// set 1 to O15Child in age group 15-24
			int o15ChildInAgeGroup15_24Code = 1;
			if (totFchildren <= totMchildren) {
				hhRelFemale[1][HouseholdRelationship.O15Child.ordinal()] = o15ChildInAgeGroup15_24Code;
			}
			else {
				hhRelMale[1][HouseholdRelationship.O15Child.ordinal()] = o15ChildInAgeGroup15_24Code;
			}
			
			// set the total number of O15Child to 1
			int totalNumOfO15Child = 1;
			nppO15 = totalNumOfO15Child;
		}

		if (maxFamiO15 == 0 && nppO15 != 0) {
			if (newnHF[FamilyCompositionNote.HF1.ordinal()][1] - nppO15 < 0) {
                logger.warn("Can't adjust households to fit nppO15! More sophisticated algorithm required!");
				return null;
			}
			// sets the number of households of types HF15 and HF8 (if needed)
			// to the number of
			// O15Child individuals.
			// this is because these household types have only O15Child, no
			// U15Child nor Student,
			// thus the number of households of this type is not affected by the
			// number
			// of 'U15Child' or 'Student' individuals
			if (nppO15 <= tot1Phhold) {
				newnHF[FamilyCompositionNote.HF15.ordinal()][1] = nppO15;
				// reduces the number of households in other 1P households by an
				// amount of nppO15
				int[] other1Phhold = new int[] {
						newnHF[FamilyCompositionNote.HF9.ordinal()][1],
						newnHF[FamilyCompositionNote.HF10.ordinal()][1],
						newnHF[FamilyCompositionNote.HF11.ordinal()][1],
						newnHF[FamilyCompositionNote.HF12.ordinal()][1],
						newnHF[FamilyCompositionNote.HF13.ordinal()][1],
						newnHF[FamilyCompositionNote.HF14.ordinal()][1] };
				int[] other1PhholdIdx = new int[] {
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF11.ordinal(),
						FamilyCompositionNote.HF12.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF14.ordinal() };
				other1Phhold = arrHdlr.allocateProportionally(other1Phhold,
						tot1Phhold - nppO15);
				for (int i = 0; i <= other1PhholdIdx.length - 1; i++) {
					newnHF[other1PhholdIdx[i]][1] = other1Phhold[i];
				}
			} else {
				newnHF[FamilyCompositionNote.HF15.ordinal()][1] = tot1Phhold;
				int[] other1PhholdIdx = new int[] {
						FamilyCompositionNote.HF9.ordinal(),
						FamilyCompositionNote.HF10.ordinal(),
						FamilyCompositionNote.HF11.ordinal(),
						FamilyCompositionNote.HF12.ordinal(),
						FamilyCompositionNote.HF13.ordinal(),
						FamilyCompositionNote.HF14.ordinal() };
				for (int i = 0; i <= other1PhholdIdx.length - 1; i++) {
					newnHF[other1PhholdIdx[i]][1] = 0;
				}
				// assigns the remaining number of households required to HF8
				newnHF[FamilyCompositionNote.HF8.ordinal()][1] = nppO15
						- tot1Phhold;
				// reduces the number of households in HF1 by the same amount to
				// reserve the
				// total number of households having 2 parents
				newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
						.ordinal()][1] - (nppO15 - tot1Phhold);
			}
		} else if (maxFamiO15 > nppO15) {
			int nhh2Prem = nhh2Pcrn;
			int nhh1Prem = 0;
			if (nppO15 - nhh1Pcrn >= 0) {
				nhh2Prem = nhh2Pcrn - (nppO15 - nhh1Pcrn);
			}
			else {
				nhh1Prem = nhh1Pcrn - nppO15;
			}

			// remove some households from HF2, HF4, HF6, and HF8
			int[] nHF2PtoRemove = new int[] {
					newnHF[FamilyCompositionNote.HF2.ordinal()][1],
					newnHF[FamilyCompositionNote.HF4.ordinal()][1],
					newnHF[FamilyCompositionNote.HF6.ordinal()][1],
					newnHF[FamilyCompositionNote.HF8.ordinal()][1] };
			int[] nHF2PIdxtoRemove = new int[] {
					FamilyCompositionNote.HF2.ordinal(),
					FamilyCompositionNote.HF4.ordinal(),
					FamilyCompositionNote.HF6.ordinal(),
					FamilyCompositionNote.HF8.ordinal() };
			if (arrHdlr.sumPositiveInArray(nHF2PtoRemove) - nhh2Prem >= 0) {
				nHF2PtoRemove = arrHdlr.allocateProportionally(nHF2PtoRemove,
						nhh2Prem);
			}
			else {
				nhh1Prem = nhh1Prem + nhh2Prem
						- arrHdlr.sumPositiveInArray(nHF2PtoRemove);
			}
			
			for (int i = 0; i <= nHF2PIdxtoRemove.length - 1; i++) {
				newnHF[nHF2PIdxtoRemove[i]][1] = newnHF[nHF2PIdxtoRemove[i]][1]
						- nHF2PtoRemove[i];
			}
			
			// dissipating the number of households just removed to HF3, HF5,
			// HF7, and HF1
			int[] dissnHF2PIdx = new int[] {
					FamilyCompositionNote.HF3.ordinal(),
					FamilyCompositionNote.HF5.ordinal(),
					FamilyCompositionNote.HF7.ordinal(),
					FamilyCompositionNote.HF1.ordinal() };
			for (int i = 0; i <= dissnHF2PIdx.length - 1; i++) {
				newnHF[dissnHF2PIdx[i]][1] = newnHF[dissnHF2PIdx[i]][1]
						+ nHF2PtoRemove[i];
			}

			// removes some households from hhold types having 1 parent and O15.
			int[] nHF1PtoRemove = new int[] {
					newnHF[FamilyCompositionNote.HF9.ordinal()][1],
					newnHF[FamilyCompositionNote.HF11.ordinal()][1],
					newnHF[FamilyCompositionNote.HF13.ordinal()][1],
					newnHF[FamilyCompositionNote.HF15.ordinal()][1] };
//			int[] nHF1PIdxtoRemove = new int[] {
//					family_composition_note.HF9.ordinal(),
//					family_composition_note.HF11.ordinal(),
//					family_composition_note.HF13.ordinal(),
//					family_composition_note.HF15.ordinal() };
			if (arrHdlr.sumPositiveInArray(nHF1PtoRemove) >= nhh1Prem) {
				nHF1PtoRemove = arrHdlr.allocateProportionally(nHF1PtoRemove,
						nhh1Prem);

				// shifting households from HF9 to HF10
				newnHF[FamilyCompositionNote.HF9.ordinal()][1] = newnHF[FamilyCompositionNote.HF9
						.ordinal()][1] - nHF1PtoRemove[0];
				newnHF[FamilyCompositionNote.HF10.ordinal()][1] = newnHF[FamilyCompositionNote.HF10
						.ordinal()][1] + nHF1PtoRemove[0];

				// shifting households from HF11 to HF12
				newnHF[FamilyCompositionNote.HF11.ordinal()][1] = newnHF[FamilyCompositionNote.HF11
						.ordinal()][1] - nHF1PtoRemove[1];
				newnHF[FamilyCompositionNote.HF12.ordinal()][1] = newnHF[FamilyCompositionNote.HF12
						.ordinal()][1] + nHF1PtoRemove[1];

				// shifting households from HF13 to HF14
				newnHF[FamilyCompositionNote.HF13.ordinal()][1] = newnHF[FamilyCompositionNote.HF13
						.ordinal()][1] - nHF1PtoRemove[2];
				newnHF[FamilyCompositionNote.HF14.ordinal()][1] = newnHF[FamilyCompositionNote.HF14
						.ordinal()][1] + nHF1PtoRemove[2];

				// shifting households from HF15 to HF1 (!!?? oh yes!)
				// this is done via a couple of steps.
				// 1. reduces hholds in HF15
				// 2. proportionally increases numbers of hholds in HF12 and
				// HF14 by the same amount
				// (to reserve number of 1 parent households)
				// 3. decreases numbers of hholds in HF5 and HF7 respectively by
				// the same amount
				// (to reserve the number of U15Child hholds and Student hholds,
				// respectively)
				// 4. increases hholds in HF1 by the same amount
				// (to reserve the number of households having 2 parents)
				//
				// so for the above steps to be viable, the number of hholds in
				// HF5 and HF7 needs to be
				// greater than or equal to number of households to be removed
				// from HF15.
				if (newnHF[FamilyCompositionNote.HF5.ordinal()][1]
						+ newnHF[FamilyCompositionNote.HF7.ordinal()][1] >= nHF1PtoRemove[3]) {
					// 1. reduces hholds in HF15
					newnHF[FamilyCompositionNote.HF15.ordinal()][1] = newnHF[FamilyCompositionNote.HF15
							.ordinal()][1] - nHF1PtoRemove[3];
					// 2. proportionally increases numbers of hholds in HF12 and
					// HF14 by the same amount
					// (to reserve number of 1 parent households)
					int[] tmpnhhRem = arrHdlr.allocateProportionally(new int[] {
							newnHF[FamilyCompositionNote.HF5.ordinal()][1],
							newnHF[FamilyCompositionNote.HF7.ordinal()][1] },
							nHF1PtoRemove[3]);
					newnHF[FamilyCompositionNote.HF12.ordinal()][1] = newnHF[FamilyCompositionNote.HF12
							.ordinal()][1] + tmpnhhRem[0];
					newnHF[FamilyCompositionNote.HF14.ordinal()][1] = newnHF[FamilyCompositionNote.HF14
							.ordinal()][1] + tmpnhhRem[1];
					// 3. decreases numbers of hholds in HF5 and HF7
					// respectively by the same amount
					// (to reserve the number of U15Child hholds and Student
					// hholds, respectively)
					newnHF[FamilyCompositionNote.HF5.ordinal()][1] = newnHF[FamilyCompositionNote.HF5
							.ordinal()][1] - tmpnhhRem[0];
					newnHF[FamilyCompositionNote.HF7.ordinal()][1] = newnHF[FamilyCompositionNote.HF7
							.ordinal()][1] - tmpnhhRem[1];
					// 4. increases hholds in HF1 by the same amount
					// (to reserve the number of households having 2 parents)
					newnHF[FamilyCompositionNote.HF1.ordinal()][1] = newnHF[FamilyCompositionNote.HF1
							.ordinal()][1] + nHF1PtoRemove[3];
				} else {
                    logger.warn("Can't adjust households to fit nppO15! More sophisticated algorithm required!");
					return null;
				}
			} else {
                logger.warn("Can't adjust households to fit nppO15! More sophisticated algorithm required!");
				return null;
			}

			// int[] nHF1PtoRemove = new int[] {
			// newnHF[family_composition_note.HF9.ordinal()][1],
			// newnHF[family_composition_note.HF11.ordinal()][1],
			// newnHF[family_composition_note.HF13.ordinal()][1]};
			// int[] nHF1PIdxtoRemove = new int[] {
			// family_composition_note.HF9.ordinal(),
			// family_composition_note.HF11.ordinal(),
			// family_composition_note.HF13.ordinal()};
			// if (arrHdlr.sumPositiveInArray(nHF1PtoRemove)-nhh1Prem>=0) {
			// nHF1PtoRemove = arrHdlr.allocateProportionally(nHF1PtoRemove,
			// nhh1Prem);
			// for (int i=0; i<=nHF1PIdxtoRemove.length-1; i++)
			// newnHF[nHF1PIdxtoRemove[i]][1] =
			// newnHF[nHF1PIdxtoRemove[i]][1] - nHF1PtoRemove[i];
			// // dissipates number of households just removed to HF10, HF12,
			// HF14
			// int[] dissnHF1PIdx = new int[] {
			// family_composition_note.HF10.ordinal(),
			// family_composition_note.HF12.ordinal(),
			// family_composition_note.HF14.ordinal()};
			// for (int i=0; i<=dissnHF1PIdx.length-1; i++)
			// newnHF[dissnHF1PIdx[i]][1] = newnHF[dissnHF1PIdx[i]][1] +
			// nHF1PtoRemove[i];
			// } else
			// if (arrHdlr.sumPositiveInArray(nHF1PtoRemove)+
			// newnHF[family_composition_note.HF15.ordinal()][1]-nhh1Prem>=0) {
			// // not sure about the below commented block!!!
			// // don't (want to) worry about it till shit happens!
			//
			// /*
			// logger.debug("starts shifting HF15 to HF9, HF11, HF13");
			// // Shifting some households from HF15 to HF9, HF11, HF13 so these
			// hhold types have
			// // enough hholds to be removed later.
			// // calculates the number of households to be shifted
			// int nHholdShifted =
			// nhh1Prem-arrHdlr.sumPositiveInArray(nHF1PtoRemove);
			// // removes this number of households from HF15
			// newnHF[family_composition_note.HF15.ordinal()][1] =
			// newnHF[family_composition_note.HF15.ordinal()][1] -
			// nHholdShifted;
			// // proportionally allocates this number of households to HF9,
			// HF11, HF13
			// if (arrHdlr.sumPositiveInArray(nHF1PtoRemove)==0)
			// nHF1PtoRemove = new int[] {1,1,1};
			// int[] tmpnHF = arrHdlr.allocateProportionally(nHF1PtoRemove,
			// nHholdShifted);
			// for (int i=0; i<=nHF1PIdxtoRemove.length-1; i++)
			// newnHF[nHF1PIdxtoRemove[i]][1] = newnHF[nHF1PIdxtoRemove[i]][1] +
			// tmpnHF[i];
			// // reduces the number of hholds in HF10, HF12, HF14 accordingly
			// newnHF[family_composition_note.HF10.ordinal()][1] =
			// newnHF[family_composition_note.HF10.ordinal()][1] - tmpnHF[0];
			// newnHF[family_composition_note.HF12.ordinal()][1] =
			// newnHF[family_composition_note.HF12.ordinal()][1] - tmpnHF[1];
			// newnHF[family_composition_note.HF14.ordinal()][1] =
			// newnHF[family_composition_note.HF14.ordinal()][1] - tmpnHF[2];
			// // check if HF10, HF12, HF14 has negative number of households,
			// // correction fail, then return null
			// if (newnHF[family_composition_note.HF10.ordinal()][1]<0 ||
			// newnHF[family_composition_note.HF12.ordinal()][1]<0 ||
			// newnHF[family_composition_note.HF14.ordinal()][1]<0) {
			// logger.debug("Can't adjust households to fit nppO15! " +
			// "More sophisticated algorithm required!");
			// return null;
			// }
			// // now the total of households in HF9, HF11, HF13 must be equal
			// to the number of
			// // households to be removed from HF9, HF11, HF13 and HF15 (i.e.
			// equal to nhh1Prem).
			// // thus we can simply set the number of households in HF9, HF11,
			// HF13 to 0.
			// for (int i=0; i<=nHF1PIdxtoRemove.length-1; i++)
			// newnHF[nHF1PIdxtoRemove[i]][1] = 0;
			// // then dissipates the number of households just removed from
			// HF9, HF11, HF13
			// // to HF10, HF12, HF14 respectively
			// newnHF[family_composition_note.HF10.ordinal()][1] =
			// newnHF[family_composition_note.HF10.ordinal()][1] +
			// newnHF[family_composition_note.HF9.ordinal()][1];
			// newnHF[family_composition_note.HF12.ordinal()][1] =
			// newnHF[family_composition_note.HF12.ordinal()][1] +
			// newnHF[family_composition_note.HF11.ordinal()][1];
			// newnHF[family_composition_note.HF14.ordinal()][1] =
			// newnHF[family_composition_note.HF14.ordinal()][1] +
			// newnHF[family_composition_note.HF13.ordinal()][1];
			// */
			// } else {
			// logger.debug(
			// "Can't adjust households to fit nppO15! More sophisticated algorithm required!");
			// return null;
			// }
		}

		return newnHF;
	}

	/**
	 * creates a table having the following columns cd, sumB22aM_original,
	 * sumB22aM_corrected, sumB22aM_dev, sumB22aF_original, sumB22aF_corrected,
	 * sumB22aF_dev, sumB24_original, sumB24_corrected, sumB24_dev,
	 * sumB25_original, sumB25_corrected, sumB25_dev, sumB30_original,
	 * sumB30_corrected, sumB30_dev,
	 * 
	 */
	private void testTableCorrections(String cdName, String filename,
			boolean append) {
		ArrayHandler arrHdlr = new ArrayHandler();
		int sumhhRelM_Ori = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelMaleOri, HouseholdRelationship.Married.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.DeFacto.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.LoneParent.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.U15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.Student.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.O15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.Relative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.NonRelative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.GroupHhold.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.LonePerson.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMaleOri,
						HouseholdRelationship.Visitor.ordinal()));
		int sumhhRelM_Cor = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelMale, HouseholdRelationship.Married.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.DeFacto.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.LoneParent.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.U15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.Student.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.O15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.Relative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.NonRelative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.GroupHhold.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.LonePerson.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelMale,
						HouseholdRelationship.Visitor.ordinal()));
		double sumhhRelM_dev = Math
				.abs((double) (sumhhRelM_Cor - sumhhRelM_Ori))
				/ sumhhRelM_Ori
				* 100;

		int sumhhRelF_Ori = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelFemaleOri, HouseholdRelationship.Married.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.DeFacto.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.LoneParent.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.U15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.Student.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.O15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.Relative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.NonRelative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.GroupHhold.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.LonePerson.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemaleOri,
						HouseholdRelationship.Visitor.ordinal()));
		int sumhhRelF_Cor = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhRelFemale, HouseholdRelationship.Married.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.DeFacto.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.LoneParent.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.U15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.Student.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.O15Child.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.Relative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.NonRelative.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.GroupHhold.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.LonePerson.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhRelFemale,
						HouseholdRelationship.Visitor.ordinal()));
		double sumhhRelF_dev = Math
				.abs((double) (sumhhRelF_Cor - sumhhRelF_Ori))
				/ sumhhRelF_Ori
				* 100;

		int sumnHF_Ori = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(nHFOri,
				FamilyComposition.families.ordinal()));
		int sumnHF_Cor = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(nHF,
				FamilyComposition.families.ordinal()));
		double sumnHF_dev = Math.abs((double) (sumnHF_Cor - sumnHF_Ori))
				/ sumnHF_Ori * 100;

		int sumnppInHF_Ori = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				nppInHFOri, FamilyCompositionBySex.males.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(nppInHFOri,
						FamilyCompositionBySex.females.ordinal()));
		int sumnppInHF_Cor = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				nppInHF, FamilyCompositionBySex.males.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(nppInHF,
						FamilyCompositionBySex.females.ordinal()));
		double sumnppInHF_dev = Math
				.abs((double) (sumnppInHF_Cor - sumnppInHF_Ori))
				/ sumnppInHF_Ori * 100;

		int sumhhComp_Ori = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhCompOri, HouseholdComposition.family_households.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhCompOri,
						HouseholdComposition.non_family_households.ordinal()));
		int sumhhComp_Cor = arrHdlr.sumPositiveInArray(arrHdlr.getColumn(
				hhComp, HouseholdComposition.family_households.ordinal()))
				+ arrHdlr.sumPositiveInArray(arrHdlr.getColumn(hhComp,
						HouseholdComposition.non_family_households.ordinal()));
		double sumhhComp_dev = Math
				.abs((double) (sumhhComp_Cor - sumhhComp_Ori))
				/ sumhhComp_Ori
				* 100;

		String str2Write = cdName + "," + Integer.toString(sumhhRelM_Ori) + ","
				+ Integer.toString(sumhhRelM_Cor) + ","
				+ Double.toString(sumhhRelM_dev) + ","
				+ Integer.toString(sumhhRelF_Ori) + ","
				+ Integer.toString(sumhhRelF_Cor) + ","
				+ Double.toString(sumhhRelF_dev) + ","
				+ Integer.toString(sumnHF_Ori) + ","
				+ Integer.toString(sumnHF_Cor) + ","
				+ Double.toString(sumnHF_dev) + ","
				+ Integer.toString(sumnppInHF_Ori) + ","
				+ Integer.toString(sumnppInHF_Cor) + ","
				+ Double.toString(sumnppInHF_dev) + ","
				+ Integer.toString(sumhhComp_Ori) + ","
				+ Integer.toString(sumhhComp_Cor) + ","
				+ Double.toString(sumhhComp_dev);

		TextFileHandler.writeToText(filename, str2Write, append);
	}


	private void displayDataHholdRel(int[][] hhRelMale, int[][] hhRelFemale) {
		logger.debug("MALE");
		logger.info("id,year1,year2,married,defacto,loneparent,U15,student,O15,reltives,nonrelatives,groupsHousehold,lonePerson,visitor");
		for (int i = 0; i <= hhRelMale.length - 1; i++) {
			logger.debug(hhRelMale[i][0] + ", " + hhRelMale[i][1] + ", "
					+ hhRelMale[i][2] + ", " + hhRelMale[i][3] + ", "
					+ hhRelMale[i][4] + ", " + hhRelMale[i][5] + ", "
					+ hhRelMale[i][6] + ", " + hhRelMale[i][7] + ", "
					+ hhRelMale[i][8] + ", " + hhRelMale[i][9] + ", "
					+ hhRelMale[i][10] + ", " + hhRelMale[i][11] + ", "
					+ hhRelMale[i][12] + ", " + hhRelMale[i][13]);
		}
		logger.debug("FEMALE");
		for (int i = 0; i <= hhRelFemale.length - 1; i++) {
			logger.debug(hhRelFemale[i][0] + ", " + hhRelFemale[i][1]
					+ ", " + hhRelFemale[i][2] + ", " + hhRelFemale[i][3]
					+ ", " + hhRelFemale[i][4] + ", " + hhRelFemale[i][5]
					+ ", " + hhRelFemale[i][6] + ", " + hhRelFemale[i][7]
					+ ", " + hhRelFemale[i][8] + ", " + hhRelFemale[i][9]
					+ ", " + hhRelFemale[i][10] + ", " + hhRelFemale[i][11]
					+ ", " + hhRelFemale[i][12] + ", " + hhRelFemale[i][13]);
		}
	}

	private void displayNumHF(int[][] nHF) {
		int nFami2P = 0;
		for (int i = FamilyCompositionNote.HF1.ordinal(); i <= FamilyCompositionNote.HF8
				.ordinal(); i++) {
			nFami2P = nFami2P + nHF[i][1];
		}
		
		int nFami1P = 0;
		for (int i = FamilyCompositionNote.HF9.ordinal(); i <= FamilyCompositionNote.HF15
				.ordinal(); i++) {
			nFami1P = nFami1P + nHF[i][1];
		}

		int nFamiU15 = nHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF5.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF12.ordinal()][1];

		int nFamiStu = nHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF3.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF7.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF10.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF14.ordinal()][1];

		int nFamiO15 = nHF[FamilyCompositionNote.HF2.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF4.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF6.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF8.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF9.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF11.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF13.ordinal()][1]
				+ nHF[FamilyCompositionNote.HF15.ordinal()][1];

		logger.debug("nHF:");
		for (FamilyCompositionNote fcn : FamilyCompositionNote.values()) {
			logger.debug(fcn.toString() + "," + nHF[fcn.ordinal()][1]);
		}

		logger.debug("Couple: " + nFami2P + " families");
		logger.debug("Lone parent: " + nFami1P + " families");
		logger.debug("U15: " + nFamiU15 + " families");
		logger.debug("Student: " + nFamiStu + " families");
		logger.debug("O15: " + nFamiO15 + " families");
	}
}
