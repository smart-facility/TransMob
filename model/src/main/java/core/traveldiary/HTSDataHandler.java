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
package core.traveldiary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import core.*;
import core.synthetic.travel.mode.TravelModes;
import hibernate.postgres.HtsDataDAO;
import hibernate.postgres.HtsDataEntity;
import org.apache.log4j.Logger;

import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.traveldiary.LocationTypes;

public class HTSDataHandler {

	private final int hholdIDCol = 0;
	private final int personNoCol = 1;
	private final int tripNoCol = 2;
	private final int hfCol = 3;
	private final int attendSchoolCol = 4;
	private final int adultCatCol = 5;
	private final int numTripsCol = 6;
	private final int departCol = 7;
	private final int tripTimeCol = 8;
	private final int tmodeCol = 9;
	private final int purpos11Col = 10;

	private final ModelMain main = ModelMain.getInstance();

	private Map<Integer, List<HTSHouseholdDiary>> htsTDsByHholdType;
	private List<int[][]> htsStudentsTDPool;
	private List<int[][]> htsOtherAdultsTDPool;

	private Random random = HardcodedData.random;
    private static final Logger logger = Logger.getLogger(HTSDataHandler.class);

	public HTSDataHandler(Random aRandom) {
		this.setRandom(aRandom);

		Map<Integer, List<int[][]>> rawHholdDiaries = this.readHTSFromDB();
		
		this.setHtsTDsByHholdType(this.transformRawTDFromDataBase(rawHholdDiaries));
		this.setHtsStudentsTDPool();
		this.setHtsOtherAdultsTDPool();

	}

	public HTSDataHandler(String filename, Random aRandom) {
		this.setRandom(aRandom);
		Map<Integer, List<int[][]>> rawHholdDiaries = this
				.readHTSFromCSV(filename, true);
		this.setHtsTDsByHholdType(this.transformRawTDFromDataBase(rawHholdDiaries));
		this.setHtsStudentsTDPool();
		this.setHtsOtherAdultsTDPool();

	}

	/**
	 * reads HTS data from a csv file. Trips made by all individuals in a
	 * household are stored in a 2d integer array. 2d array of households of the
	 * same type are then put together in an ArrayList<int[][]>. The columns in
	 * this 2d array are (in order): 0:household id, 1:individual id, 2:trip id,
	 * 3:household type, 4:attend school, 5:adult priority cat, 6:number of
	 * trips made, 7:departure time, 8:trip time, 9:purpose and 10:mode.
	 * 
	 * - Index of these columns in the HTS data given to SMART in later 2012 are
	 * as follows. These values can be changed in private attributes hholdIDCol,
	 * personNoCol, etc. 0 Household ID, 1 PersonNo , 2 TripNo , 5 hf , 15
	 * ATTEND_SCHOOL, 16 ADULT_PRIORITY_CAT, 44 PERS_NUM_TRIPS , 45 DEPART , 46
	 * TRIP_TIME, 47 TMODE , 48 PURPOSE11
	 * 
	 * - Value of attend school is either 1 (attending school) or 0 (not
	 * attending school). This value is used to distinguish U15 children to
	 * other individuals in HTS data.
	 * 
	 * - Value adult priority cat is the index in the HTS list
	 * ADULT_PRIORITY_CAT 1 Full time work 2 Retired / Aged pensioner 3 Full
	 * time study 4 Unemployed 5 Part time /Casual work 6 Part time study 7
	 * Other pensioner 8 Keeping house 9 Voluntary work 98 Other
	 * 
	 * - Value of departure time of a trip is the number of seconds from time
	 * 00:00:00 of a day.
	 * 
	 * - Value of trip time of a trip is in seconds.
	 * 
	 * - Value of purpose of a trip is its index in the HTS list PURPOS11 1
	 * Change mode 2 Home 3 Go to work 4 Return to work 5 Work related business
	 * 6 Education 7 Shopping 8 Personal business/services 9 Social/recreation
	 * 10 Serve passenger 11 Other
	 * 
	 * - Value of mode of a trip is its index in the HTS list TMODE. 1 Vehicle
	 * driver 2 Vehicle passenger 3 Train 4 Bus nightride 5 Bus gov 6 Bus priv 7
	 * School bus gov 8 School bus priv 9 Ferry gov 10 Ferry priv 11 Monorail 12
	 * Light rail 13 Taxi: booked 14 Taxi: flagged down 15 Aircraft 16 Walk 17
	 * Wheelchair 18 Bicycle 98 Other (specify) 99 Other (not specify)
	 * 
	 * @param filename
	 *            location of the csv file of HTS data.
	 * @return a hashmap which is a collection of trips made by households of
	 *         the same type. Key of the hashmap is index of household type, as
	 *         followed 0 NF 1 HF1 2 HF2 3 HF3 4 HF4 5 HF5 6 HF6 7 HF7 8 HF8 9
	 *         HF9 10 HF10 11 HF11 12 HF12 13 HF13 14 HF14 15 HF15 16 HF16
	 */
	public Map<Integer, List<int[][]>> readHTSFromCSV(String filename,
			boolean hasHeader) {
		Map<Integer, List<int[][]>> rawHholdDiaries = new HashMap<>();

		int[] col2Read = new int[] { hholdIDCol, personNoCol, tripNoCol, hfCol,
				attendSchoolCol, adultCatCol, numTripsCol, departCol,
				tripTimeCol, tmodeCol, purpos11Col };

        BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = null;
			if (hasHeader) {
				line = br.readLine();
				logger.trace(line);
			}

			StringTokenizer st=null;
			// reads the first row of csv file
			if ((line = br.readLine())!=null) {
				st = new StringTokenizer(line, ","); 
			}

			int[] crnRow = new int[col2Read.length];
			int countCol = -1;
			int iCol2Read = 0;
			
			if (st!=null) {
				while (st.hasMoreTokens() && iCol2Read <= col2Read.length - 1) {
					countCol += 1;
					if (countCol == col2Read[iCol2Read]) {
						String tmpval = st.nextToken();
						crnRow[iCol2Read] = Integer.parseInt(tmpval);
					}
					iCol2Read += 1;
				}	
			}
			List<int[]> thisHholdTD = new ArrayList<int[]>();
			thisHholdTD.add(crnRow);

			// starts reading the rest of the csv file
			while ((line = br.readLine()) != null) {
				st = new StringTokenizer(line, ","); 
				crnRow = new int[col2Read.length];
				countCol = -1;
				iCol2Read = 0;
				while (st.hasMoreTokens() && iCol2Read <= col2Read.length - 1) {
					countCol += 1;
					if (countCol == col2Read[iCol2Read]) {
						String tmpval = st.nextToken();
						crnRow[iCol2Read] = Integer.parseInt(tmpval);
					}
					iCol2Read += 1;
				}
				if (crnRow[0] == thisHholdTD.get(thisHholdTD.size() - 1)[0]) {
					thisHholdTD.add(crnRow);
				} else {
					int tmpHholdType = thisHholdTD.get(thisHholdTD.size() - 1)[3];
					// converts thisHholdTD into a 2d array
					int[][] thisHholdTDArray = new int[thisHholdTD.size()][thisHholdTD
							.get(0).length];
					for (int tmpi = 0; tmpi <= thisHholdTD.size() - 1; tmpi++) {
						thisHholdTDArray[tmpi] = thisHholdTD.get(tmpi);
                    }
					// adds this new 2d array to arraylist rawHholdDiaries
					List<int[][]> thisHholdTypeTD = new ArrayList<int[][]>();
					if (rawHholdDiaries.containsKey(tmpHholdType)) {
					    // if rawHholdID already has this household type
                        thisHholdTypeTD = rawHholdDiaries.get(tmpHholdType);
                    }
					thisHholdTypeTD.add(thisHholdTDArray);
					rawHholdDiaries.put(tmpHholdType, thisHholdTypeTD);

					// creates records for the next household
					thisHholdTD = new ArrayList<int[]>();
					thisHholdTD.add(crnRow);
				}
			}

			int tmpHholdType = thisHholdTD.get(thisHholdTD.size() - 1)[3];
			// converts thisHholdTD into a 2d array
			int[][] thisHholdTDArray = new int[thisHholdTD.size()][thisHholdTD.get(0).length];
			for (int tmpi = 0; tmpi <= thisHholdTD.size() - 1; tmpi++) {
				thisHholdTDArray[tmpi] = thisHholdTD.get(tmpi);
            }
			// adds this new 2d array to arraylist rawHholdDiaries
			List<int[][]> thisHholdTypeTD = new ArrayList<>();
			if (rawHholdDiaries.containsKey(tmpHholdType)) {
			    // if rawHholdID already has this household type
				thisHholdTypeTD = rawHholdDiaries.get(tmpHholdType);
            }
			thisHholdTypeTD.add(thisHholdTDArray);
			rawHholdDiaries.put(tmpHholdType, thisHholdTypeTD);

		} catch (IOException e) {
            logger.error("Failed to read file: " + filename, e);
		} finally {
            try {
                if (br != null ) {
                    br.close();
                }
            } catch (IOException e) {
                logger.error("Failed to close file after reading: " + filename, e);
            }
        }

		return rawHholdDiaries;
	}

	
	
	public Map<Integer, List<int[][]>> readHTSFromDB() {

		Map<Integer, List<int[][]>> rawHholdDiaries = new HashMap<>();

        HtsDataDAO htsDataDao = ApplicationContextHolder.getBean(HtsDataDAO.class);

        List<HtsDataEntity> htsDataEntities = htsDataDao.findAll();

		try {

			int currentId = 0, previousId = 1;
			List<int[]> currentHousehold = new ArrayList<int[]>();

			for (HtsDataEntity htsData : htsDataEntities) {
				currentId = htsData.getKey().getHouseholdId();

				if (currentId != previousId) {

					int[][] entry = new int[currentHousehold.size()][];

					for (int i = 0; i < currentHousehold.size(); i++) {
						entry[i] = currentHousehold.get(i);
					}

					int hf = entry[0][3];
					List<int[][]> sameHouseholdCategory = rawHholdDiaries.get(hf);
					if (sameHouseholdCategory != null) {
						sameHouseholdCategory.add(entry);
					} else {

						List<int[][]> newHouseholdCategory = new ArrayList<int[][]>();
						newHouseholdCategory.add(entry);

						rawHholdDiaries.put(hf, newHouseholdCategory);
					}

					currentHousehold.clear();
				}

				int[] records = new int[] { currentId, htsData.getKey().getPersonNo(),
						htsData.getKey().getTripNo(), htsData.getHf(), htsData.getAttendSchool(), htsData.getAdultPriorityCat(),
						htsData.getPersNumTrips(), htsData.getDepart(), htsData.getTripTime(),
						htsData.getTmode(), htsData.getPurpose11() };
				currentHousehold.add(records);
				previousId = currentId;

			}

		} catch (Exception e) {
            logger.error("Failure to process HTS Data", e);
		}

		return rawHholdDiaries;
	}

	/**
	 * ensures that rawTD is sorted by trip ID of each individual (3rd column in rawTD).
	 * @param rawTD travel diaries of individuals in the same household (HTS data).
	 * @return sorted rawTD
	 */
	private int[][] sortsRawTDOfAHhold(int[][] rawTD) {
		// extracts travel diary of each individual 
		final int colIndivID = 1;
		final int colTripID = 2;
		
		HashMap<Integer,ArrayList<int[]>> tdAllIndivs = new HashMap<>();
		for (int i=0; i<=rawTD.length-1; i++) {
			int indivID = rawTD[i][colIndivID];
			ArrayList<int[]> tdIndiv = new ArrayList<>();
			if (tdAllIndivs.containsKey(indivID)) {
				tdIndiv = tdAllIndivs.get(indivID);
            }
			tdIndiv.add(rawTD[i]);
			tdAllIndivs.put(indivID, tdIndiv);
		}
		
		HashMap<Integer, int[][]> newTDAllIndivs = new HashMap<>();
		int nTripsThisHholdMade = 0;
		for (Map.Entry<Integer, ArrayList<int[]>> anEntry : tdAllIndivs.entrySet()) {
			ArrayList<int[]> tdIndiv = anEntry.getValue();
			int[][] tdArray = new int[tdIndiv.size()][tdIndiv.get(0).length];
			int[] tripIDs = new int[tdIndiv.size()];
			int iTrip = -1;
			for (int[] trip : tdIndiv) {
				iTrip += 1;
				tdArray[iTrip] = trip;
				tripIDs[iTrip] = trip[colTripID];
			}
			
			int[] sortedIndices = (new ArrayHandler()).sortedIndices(tripIDs);
			int[][] newtdArray = new int[tdIndiv.size()][tdIndiv.get(0).length];
			for (int i=0; i<=sortedIndices.length-1; i++) {
				newtdArray[i] = tdArray[sortedIndices[i]];
            }
			newTDAllIndivs.put(anEntry.getKey(), newtdArray);
			
			nTripsThisHholdMade += newtdArray.length;
		}
		
		int[][] sortedRawTD = new int[nTripsThisHholdMade][rawTD[0].length];
		int iTrip = -1;
		for (int[][] tdIndiv : newTDAllIndivs.values()) {
			for (int tmpi=0; tmpi<=tdIndiv.length-1; tmpi++) {
				iTrip += 1;
				sortedRawTD[iTrip] = tdIndiv[tmpi];
			}
		}
		
		return sortedRawTD;
	}
	

	/**
	 * transforms travel diaries read from csv file from type
	 * HashMap<Integer,ArrayList<int[][]>> to type
	 * HashMap<Integer,ArrayList<HTSHouseholdDiary>>. This is solely for the
	 * convenience in assessing properties of an HTS household (e.g. TD of
	 * dependent children, number of dependent child, number of residents,
	 * number of adults, etc.) when assigning its travel diaries to a SP
	 * household.
	 * 
	 * @param rawHholdDiaries
	 * @return hashmap which is a collection of trips made by households of the
	 *         same type. Key of the hashmap is index of household type, as
	 *         followed 0 NF 1 HF1 2 HF2 3 HF3 4 HF4 5 HF5 6 HF6 7 HF7 8 HF8 9
	 *         HF9 10 HF10 11 HF11 12 HF12 13 HF13 14 HF14 15 HF15 16 HF16
	 */
	public Map<Integer, List<HTSHouseholdDiary>> transformRawTDFromDataBase(Map<Integer, List<int[][]>> rawHholdDiaries) {
		Map<Integer, List<HTSHouseholdDiary>> transformedHholdTDs = new HashMap<>();

		for (Map.Entry<Integer, List<int[][]>> entry: rawHholdDiaries.entrySet()) {
			List<int[][]> thisHholdTypeTD = entry.getValue();
			List<HTSHouseholdDiary> newThisHholdTypeTD = new ArrayList<>();
			for (int[][] rawhholdTD : thisHholdTypeTD) {
				// ensures that hhDiary is sorted by trip ID of each individual (3rd column)
				int[][] sortedRawTD = sortsRawTDOfAHhold(rawhholdTD);
				HTSHouseholdDiary newhhTD = new HTSHouseholdDiary(sortedRawTD);
				newThisHholdTypeTD.add(newhhTD);
			}
			transformedHholdTDs.put(entry.getKey(), newThisHholdTypeTD);
		}
		
//		for (Integer hhType : rawHholdDiaries.keySet()) {
//			List<int[][]> allHhDiaries = rawHholdDiaries.get(hhType);
//			for (int[][] hhDiaries : allHhDiaries) 
//				for (int i=0; i<=hhDiaries.length-1; i++) {
//					String recStr = String.valueOf(hhType) + "," + String.valueOf(hhDiaries.length) + ",";
//					for (int j=0; j<=hhDiaries[i].length-1; j++) 
//						recStr = recStr + String.valueOf(hhDiaries[i][j]) + ",";
//					TextFileHandler.writeToText("rawHholdDiaries.csv", recStr, true);
//				}
//		}
		
//		for (Integer hhType : transformedHholdTDs.keySet()) {
//			List<HTSHouseholdDiary> allHhDiaries = transformedHholdTDs.get(hhType);
//			for (HTSHouseholdDiary hhDiaries : allHhDiaries) {
//				List<int[][]> childTDs = hhDiaries.getDependentChildTD();
//				for (int[][] td : childTDs) {
//					for (int i=0; i<=td.length-1; i++) {
//						String recStr = String.valueOf(hhType) + "," + String.valueOf(td.length) + ",";
//						for (int j=0; j<=td[i].length-1; j++) 
//							recStr = recStr + String.valueOf(td[i][j]) + ",";
//						TextFileHandler.writeToText("transformedHholdDiaries.csv", recStr, true);
//					}
//				}
//				List<int[][]> adultTDs = hhDiaries.getOtherAdultsTD();
//				for (int[][] td : adultTDs) {
//					for (int i=0; i<=td.length-1; i++) {
//						String recStr = String.valueOf(hhType) + "," + String.valueOf(td.length) + ",";
//						for (int j=0; j<=td[i].length-1; j++) 
//							recStr = recStr + String.valueOf(td[i][j]) + ",";
//						TextFileHandler.writeToText("transformedHholdDiaries.csv", recStr, true);
//					}
//				}
//				List<int[][]> studentTDs = hhDiaries.getStudentTD();
//				for (int[][] td : studentTDs) {
//					for (int i=0; i<=td.length-1; i++) {
//						String recStr = String.valueOf(hhType) + "," + String.valueOf(td.length) + ",";
//						for (int j=0; j<=td[i].length-1; j++) 
//							recStr = recStr + String.valueOf(td[i][j]) + ",";
//						TextFileHandler.writeToText("transformedHholdDiaries.csv", recStr, true);
//					}
//				}
//			}
//		}

		return transformedHholdTDs;
	}
	
	/**
	 * transforms travel diaries read from csv file from type
	 * HashMap<Integer,ArrayList<int[][]>> to type
	 * HashMap<Integer,ArrayList<HTSHouseholdDiary>>. This is solely for the
	 * convenience in assessing properties of an HTS household (e.g. TD of
	 * dependent children, number of dependent child, number of residents,
	 * number of adults, etc.) when assigning its travel diaries to a SP
	 * household.
	 * 
	 * @param rawHholdDiaries
	 * @return hashmap which is a collection of trips made by households of the
	 *         same type. Key of the hashmap is index of household type, as
	 *         followed 0 NF 1 HF1 2 HF2 3 HF3 4 HF4 5 HF5 6 HF6 7 HF7 8 HF8 9
	 *         HF9 10 HF10 11 HF11 12 HF12 13 HF13 14 HF14 15 HF15 16 HF16
	 */
	public Map<Integer, List<HTSHouseholdDiary>> transformRawTD(
			Map<Integer, List<int[][]>> rawHholdDiaries) {
		Map<Integer, List<HTSHouseholdDiary>> transformedHholdTDs = new HashMap<>();

		for (Map.Entry<Integer, List<int[][]>> entry: rawHholdDiaries.entrySet()) {
			List<int[][]> thisHholdTypeTD = entry.getValue();
			List<HTSHouseholdDiary> newThisHholdTypeTD = new ArrayList<>();
			for (int[][] rawhholdTD : thisHholdTypeTD) {
				HTSHouseholdDiary newhhTD = new HTSHouseholdDiary(rawhholdTD);
				newThisHholdTypeTD.add(newhhTD);
			}
			transformedHholdTDs.put(entry.getKey(), newThisHholdTypeTD);
		}
		
		return transformedHholdTDs;
	}

	/**
	 * 
	 * @param spHhold
	 *            details of individuals in a SP household. Each record
	 *            represents an individual. The columns in spHhold are (in
	 *            order) - hholdID, ID of the SP household (from householdPool)
	 *            - indivID, ID of the SP individual (from individualPool) -
	 *            hhholdRelationship, values of householdRelationship need to be
	 *            converted to integers as followed 0 Married 1 DeFacto 2
	 *            LoneParent 3 U15Child 4 Student 5 O15Child 6 Relative 7
	 *            NonRelative 8 GroupHhold 9 LonePerson 10 Visitor - hholdType,
	 *            values of hholdType need to be converted to integers as
	 *            followed 0 NF 1 HF1 2 HF2 3 HF3 4 HF4 5 HF5 6 HF6 7 HF7 8 HF8
	 *            9 HF9 10 HF10 11 HF11 12 HF12 13 HF13 14 HF14 15 HF15 16 HF16
	 * 
	 * @return a collection of diaries of individuals in spHhold. If a travel
	 *         diary cannot be assigned to an individual in spHhold, travel
	 *         diary of the whole household of spHhold will be null. The diary
	 *         of an individual is a 2d integer array and comprises these
	 *         columns (in order) - hholdID, ID of the SP household as specified
	 *         in spHhold - indivID, ID of the SP individual as specified in
	 *         spHhold - tripID, ID of trips made by an individual - departure
	 *         time, the number of seconds from time 00:00:00 of a day - trip
	 *         time, in seconds - mode, mode of a trip, value of which is the
	 *         index in HTS list TMODE: 1 Vehicle driver 2 Vehicle passenger 3
	 *         Train 4 Bus nightride 5 Bus gov 6 Bus priv 7 School bus gov 8
	 *         School bus priv 9 Ferry gov 10 Ferry priv 11 Monorail 12 Light
	 *         rail 13 Taxi: booked 14 Taxi: flagged down 15 Aircraft 16 Walk 17
	 *         Wheelchair 18 Bicycle 98 Other (specify) 99 Other (not specify) -
	 *         purpose, purpose of a trip, value of which is the index in HTS
	 *         list PURPOS11: 1 Change mode 2 Home 3 Go to work 4 Return to work
	 *         5 Work related business 6 Education 7 Shopping 8 Personal
	 *         business/services 9 Social/recreation 10 Serve passenger 11 Other
	 * 
	 */
	public List<int[][]> setTDtoSPHhold(int[][] spHhold) {
		Map<Integer, List<HTSHouseholdDiary>> htsDiaries = this.getHtsTDsByHholdType();
		List<int[][]> tdSPHhold = new ArrayList<>();
		ArrayHandler arrHdlr = new ArrayHandler();

		int spHholdHhIDCol = 0;
		int spHholdIndivIDCol = 1;
		int spHholdHhRelCol = 2;
		int spHholdHhTypeCol = 3;

		final int marriedInt = 0;
		final int defactoInt = 1;
		final int loneParentInt = 2;
		final int u15ChildInt = 3;
		final int studentInt = 4;
		final int o15ChildInt = 5;
		final int relativeInt = 6;
		final int nonRelativeInt = 7;
		final int groupHholdInt = 8;
		final int lonePersonInt = 9;
		final int visitorInt = 10;

		int spHholdID = spHhold[0][spHholdHhIDCol];
		int spHholdType = spHhold[0][spHholdHhTypeCol];

        List<Integer> spStudents = new ArrayList<>();
        List<Integer> spDepChild = new ArrayList<>();
        List<Integer> spAdults = new ArrayList<>();

        for (int[] household : spHhold) {
            switch (household[spHholdHhRelCol]) {
                case studentInt:
                    // gets index of students in spHhold
                    spStudents.add(household[spHholdIndivIDCol]);
                    break;
                case u15ChildInt:
                    // gets index of SP dependent children in spHhold
                    spDepChild.add(household[spHholdIndivIDCol]);
                    break;
                case marriedInt:
                case defactoInt:
                case loneParentInt:
                case o15ChildInt:
                case relativeInt:
                case nonRelativeInt:
                case groupHholdInt:
                case lonePersonInt:
                case visitorInt:
                    // gets index of other adults in spHhold
                    spAdults.add(household[spHholdIndivIDCol]);
                    break;
                default:
                    logger.error("Unknown individual type: " + household[spHholdHhRelCol]);

            }
        }

		int nSPDepChild = spDepChild.size();

		if (nSPDepChild >= 1) { // if this SP hhold has at least 1 dependent child

			// search for HTS hholds having same type with spHhold and having
			// greater or equal number of dependent children
			List<HTSHouseholdDiary> matchingHTSHholds = new ArrayList<>();
			for (HTSHouseholdDiary tdSameHholdType : htsDiaries.get(spHholdType)) {
				if (tdSameHholdType.getnDependentChild() >= nSPDepChild) {
					matchingHTSHholds.add(tdSameHholdType);
                }
            }
			HTSHouseholdDiary selectedHhold = null;

			// if at least 1 HTS household found
			if (!matchingHTSHholds.isEmpty()) {
				// pick the HTS household that has the number of dependent
				// children closest to nSPDepChild
				int iSelectedHhold = pickHTSHholdClosestNoOfDepChildren(matchingHTSHholds, nSPDepChild);

				selectedHhold = matchingHTSHholds.get(iSelectedHhold);

				// logger.debug("Same type-1 HTS household found:"
				// + selectedHhold.getHholdID());

				// randomly pick TD of HTS dependent children and assign it to SP dependent children
				int nHTSDepChild = selectedHhold.getnDependentChild();
				int[] htsDepChildIndices = arrHdlr.pickRandomFromArray(
						arrHdlr.makeIncrementalIntArray(0, nHTSDepChild, 1),
						null, nSPDepChild, this.getRandom());
				for (int i = 0; i <= htsDepChildIndices.length - 1; i++) {
					int[][] tdChild = assignTravelDiary(selectedHhold
							.getDependentChildTD().get(htsDepChildIndices[i]),
							spHholdID, spDepChild.get(i));
					tdSPHhold.add(tdChild);
				}

			} else { // no HTS households having same type with the SP household
						// and having >= nSPDepChild
				// search for all HTS households of different types and having
				// same or greater number of dependent children
				matchingHTSHholds = new ArrayList<>();
				for (List<HTSHouseholdDiary> tdByHholdType : htsDiaries.values()) {
					for (HTSHouseholdDiary hholdTD : tdByHholdType) {
						if (hholdTD.getnDependentChild() >= nSPDepChild) {
							matchingHTSHholds.add(hholdTD);
                        }
                    }
                }
				if (!matchingHTSHholds.isEmpty()) {
					// pick the HTS household that has the number of dependent
					// children closest to nSPDepChild
					int iSelectedHhold = pickHTSHholdClosestNoOfDepChildren(
							matchingHTSHholds, nSPDepChild);
					selectedHhold = matchingHTSHholds.get(iSelectedHhold);

					// logger.debug("All type-1 HTS household found:"
					// + selectedHhold.getHholdID());

					// randomly pick TD of HTS dependent children and assign it
					// to SP dependent children
					int nHTSDepChild = selectedHhold.getnDependentChild();
					int[] htsDepChildIndices = arrHdlr.pickRandomFromArray(
							arrHdlr.makeIncrementalIntArray(0,
									nHTSDepChild - 1, 1), null, nSPDepChild,
							this.getRandom());
					for (int i = 0; i <= htsDepChildIndices.length - 1; i++) {
						int[][] tdChild = assignTravelDiary(
								selectedHhold.getDependentChildTD().get(
										htsDepChildIndices[i]), spHholdID,
								spDepChild.get(i));
						tdSPHhold.add(tdChild);
					}

				} else {
					// search for all HTS hholds having same type and the
					// largest number of dependent children
					int iSelectedHhold = 0;
					for (int i = 1; i <= htsDiaries.get(Integer.valueOf(spHholdType)).size() - 1; i++) {
						if (htsDiaries.get(Integer.valueOf(spHholdType)).get(i)
								.getnDependentChild() > htsDiaries
								.get(Integer.valueOf(spHholdType))
								.get(iSelectedHhold).getnDependentChild()) {
							iSelectedHhold = i;
                        }
                    }
					selectedHhold = htsDiaries.get(Integer.valueOf(spHholdType)).get(iSelectedHhold);

					// logger.debug("All type-1 HTS household with largest number of dependent children:"
					// + selectedHhold.getHholdID());

					int nHTSDepChild = selectedHhold.getnDependentChild();
					if (nHTSDepChild == 0) {
						logger.warn("Fail to assign travel diary to a U15Child in a SP household. " 
										+ "Cannot find any households in HTS data that have dependent children."); 
						return null;
					} else {
						// assign TD of dependent children in HTS household to
						// dependent children in spHhold

						// logger.debug("assign TD of dependent children in HTS household to dependent children in spHhold");
						for (int i = 0; i <= nHTSDepChild - 1; i++) {
							int[][] tdChild = assignTravelDiary(selectedHhold
									.getDependentChildTD().get(i), spHholdID,
									spDepChild.get(i));
							tdSPHhold.add(tdChild);
						}
						// get the list of remaining dependent children in
						// spHhold
						List<Integer> spRemDepChild = new ArrayList<>();
						for (int i = nHTSDepChild; i <= nSPDepChild - 1; i++) {
							spRemDepChild.add(spDepChild.get(i));
                        }
						// randomly duplicate TDs of dependent children in HTS
						// household and assign them to the remaining dependent
						// children in spHhold
						for (int i = 0; i <= spRemDepChild.size() - 1; i++) {
							int randInt = this.getRandom()
									.nextInt(nHTSDepChild);
							int[][] tdChild = assignTravelDiary(selectedHhold
									.getDependentChildTD().get(randInt),
									spHholdID, spRemDepChild.get(i));
							tdSPHhold.add(tdChild);
						}
					}
				}
			}

			// assign TD to students in spHhold
			if (!spStudents.isEmpty()) {
				List<int[][]> tmpStudentsTD = assignTDToSPStudents(
						selectedHhold, spHholdID, spStudents);
				if (tmpStudentsTD == null) {
					return null;
                }
				for (int[][] tmptd : tmpStudentsTD) {
					tdSPHhold.add(tmptd);
                }
			}

			// assign TD to other adults in spHhold
			if (!spAdults.isEmpty()) {
				List<int[][]> tmpAdultsTD = assignTDToSPAdults(
						selectedHhold, spHholdID, spAdults);
				if (tmpAdultsTD == null) {
					return null;
                }
				for (int[][] tmptd : tmpAdultsTD) {
					tdSPHhold.add(tmptd);
                }
			}

		} else { // this SP hhold doesn't have any dependent children
			// search for HTS households having same type and having same or
			// greater number of residents
			List<HTSHouseholdDiary> matchingHTSHholds = new ArrayList<>();
			for (HTSHouseholdDiary tdSameHholdType : htsDiaries.get(spHholdType)) {
				if (tdSameHholdType.getnResidents() >= spHhold.length) {
					matchingHTSHholds.add(tdSameHholdType);
                }
            }

			HTSHouseholdDiary selectedHhold = null;
			if (!matchingHTSHholds.isEmpty()) {
				// picks the HTS household that has number of residents closest
				// to the number of residents in spHhold.
				int iSelectedHhold = pickHTSHholdClosestNoOfResidents(
						matchingHTSHholds, spHhold.length);
				selectedHhold = matchingHTSHholds.get(iSelectedHhold);
			} else {
				// searches for HTS household having same type and largest
				// number of residents
				// int iSelectedHhold = 0;
				//
				// for (int i = 0; i <= htsDiaries.get(
				// Integer.valueOf(spHholdType)).size() - 1; i++) {
				// if (htsDiaries.get(Integer.valueOf(spHholdType)).get(i)
				// .getnResidents() > htsDiaries
				// .get(Integer.valueOf(spHholdType))
				// .get(iSelectedHhold).getnResidents())
				// iSelectedHhold = i;
				// }
				// selectedHhold = htsDiaries.get(Integer.valueOf(spHholdType))
				// .get(iSelectedHhold);

				List<Integer> largestNumberHousehold = new ArrayList<>();
				int largestNumber = Integer.MIN_VALUE;

				for (int i = 0; i <= htsDiaries.get(spHholdType).size() - 1; i++) {
					int getnResidents = htsDiaries.get(spHholdType).get(i).getnResidents();
					if (getnResidents > largestNumber) {

						largestNumberHousehold.clear();
						largestNumberHousehold.add(i);

						largestNumber = getnResidents;
					} else if (getnResidents == largestNumber) {
						largestNumberHousehold.add(i);
					}
				}

				int iSelectedHhold = random.nextInt(largestNumberHousehold
						.size());

				selectedHhold = htsDiaries.get(spHholdType).get(iSelectedHhold);

			}

			// assign TD to students in spHhold
			if (!spStudents.isEmpty()) {
				List<int[][]> tmpStudentsTD = assignTDToSPStudents(
						selectedHhold, spHholdID, spStudents);
				if (tmpStudentsTD == null) {
					return null;
                }
				for (int[][] tmptd : tmpStudentsTD) {
					tdSPHhold.add(tmptd);
                }
			}

			// assign TD to other adults in spHhold
			if (!spAdults.isEmpty()) {
				List<int[][]> tmpAdultsTD = assignTDToSPAdults(
						selectedHhold, spHholdID, spAdults);
				if (tmpAdultsTD == null) {
					return null;
                }
				for (int[][] tmptd : tmpAdultsTD) {
					tdSPHhold.add(tmptd);
                }
			}
		}

		return tdSPHhold;
	}

	/**
	 * picks the index of the HTS household in matchingHTSHholds that has the
	 * number of dependent children closest to nSPDepChild. if there are more
	 * than 1 HTS household having the same number of dependent children closest
	 * to nSPDepChild, randomly picks index of one of these households.
	 * 
	 * @param matchingHTSHholds
	 * @param nSPDepChild
	 * @return index of the selected HTS household in matchingHTSHholds
	 */
	private int pickHTSHholdClosestNoOfDepChildren(
			List<HTSHouseholdDiary> matchingHTSHholds, int nSPDepChild) {

        int closestDifference = Integer.MAX_VALUE;
        List<Integer> indiceswithClosestDifference = new ArrayList<>();

        for (int i = 0; i < matchingHTSHholds.size(); i++) {
            // Find the difference between our household and this one.
            int difference = matchingHTSHholds.get(i).getnDependentChild() - nSPDepChild;

            if (difference < closestDifference) {
                // This index is closer so clear out our list of indices and add this one in.
                closestDifference = difference;
                indiceswithClosestDifference.clear();
                indiceswithClosestDifference.add(i);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found household with a closer diff of: " + difference);
                }
            } else if (difference == closestDifference) {
                // Add another index with the same difference.
                indiceswithClosestDifference.add(i);
                if (logger.isTraceEnabled()) {
                    logger.trace("Adding another household with same difference of: " + difference);
                }
            }
        }

        int randInt = this.getRandom().nextInt(indiceswithClosestDifference.size());
        return indiceswithClosestDifference.get(randInt);
	}

	/**
	 * picks the HTS household that has number of residents closest to the
	 * number of residents in the SP hhold (nSPResidents). if there are more
	 * than 1 HTS household having the same number of residents closest to
	 * nSPResidents, randomly picks index of one of these households.
	 * 
	 * @param matchingHTSHholds
	 * @param nSPResidents
	 * @return
     *
	 */
	private int pickHTSHholdClosestNoOfResidents(
			List<HTSHouseholdDiary> matchingHTSHholds, int nSPResidents) {

        int closestDifference = Integer.MAX_VALUE;
        List<Integer> indiceswithClosestDifference = new ArrayList<>();

        for (int i = 0; i < matchingHTSHholds.size(); i++) {
            // Find the difference between our household and this one.
            int difference = matchingHTSHholds.get(i).getnResidents() - nSPResidents;

            if (difference < closestDifference) {
                // This index is closer so clear out our list of indices and add this one in.
                closestDifference = difference;
                indiceswithClosestDifference.clear();
                indiceswithClosestDifference.add(i);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found household with a closer diff of: " + difference);
                }
            } else if (difference == closestDifference) {
                // Add another index with the same difference.
                indiceswithClosestDifference.add(i);
                if (logger.isTraceEnabled()) {
                    logger.trace("Adding another household with same difference of: " + difference);
                }
            }
        }

		int randInt = this.getRandom().nextInt(indiceswithClosestDifference.size());
		return indiceswithClosestDifference.get(randInt);
	}

	/**
	 * deep copies departure time, trip time, purpose, and mode from travelDiary
	 * to create travel diary of an SP individual.
	 * 
	 * @param travelDiary
	 *            , a travel diary selected from HTS data
	 * @param spHholdID
	 *            , index of the SP household (in householdPool) the individual
	 *            belongs to.
	 * @param spIndivID
	 *            , index of the SP individual (in individualPool). Note, this
	 *            is not the local index of the individual in the spHholdID.
	 * @return travel diary of the SP individual with the following columns (in
	 *         order) - hholdID, ID of the SP household as specified in spHhold
	 *         - indivID, ID of the SP individual as specified in spHhold -
	 *         tripID, ID of trips made by an individual - departure time, the
	 *         number of seconds from time 00:00:00 of a day. - trip time, in
	 *         seconds - purpose, purpose of a trip, value of which is the index
	 *         in HTS list PURPOS11 1 Change mode 2 Home 3 Go to work 4 Return
	 *         to work 5 Work related business 6 Education 7 Shopping 8 Personal
	 *         business/services 9 Social/recreation 10 Serve passenger 11 Other
	 *         - mode, mode of a trip, value of which is the index in HTS list
	 *         TMODE. 1 Vehicle driver 2 Vehicle passenger 3 Train 4 Bus
	 *         nightride 5 Bus gov 6 Bus priv 7 School bus gov 8 School bus priv
	 *         9 Ferry gov 10 Ferry priv 11 Monorail 12 Light rail 13 Taxi:
	 *         booked 14 Taxi: flagged down 15 Aircraft 16 Walk 17 Wheelchair 18
	 *         Bicycle 98 Other (specify) 99 Other (not specify)
	 */
	private int[][] assignTravelDiary(int[][] travelDiary, int spHholdID,
			int spIndivID) {
		int[][] newTD = new int[travelDiary.length][9];
		for (int i = 0; i <= newTD.length - 1; i++) {
			newTD[i][0] = spHholdID; // id of this sp household
			newTD[i][1] = spIndivID; // id of this sp individual
			newTD[i][2] = i + 1; // id of this trip
			for (int j = 3; j <= newTD[0].length - 1 - 2; j++) {
				// ignore last 2
				// columns for
				// origin and
				// destination
				newTD[i][j] = travelDiary[i][j + 4];
            }
        }

//		logger.debug("assignTravelDiary for " + spHholdID + ":" + spIndivID
//				+ " using travel diary " + travelDiary[0][0] + ":"
//				+ travelDiary[0][1]);

		// Test.selected.add(travelDiary[0][0]);

		return newTD;
	}

	/**
	 * assigns TD to students in spHhold based on the TDs of students in the
	 * selectedHTSHhold. If the number of students in the selectedHTSHhold is
	 * smaller than nSPStudents (number of students in SP household), randomly
	 * picks up TD from the pool of student TDs in all HTS households and assign
	 * these to the remaining SP students.
	 * 
	 * @param selectedHTSHhold
	 * @param spHholdID
	 * @param spStudents
	 * @return
	 */
	private List<int[][]> assignTDToSPStudents(
			HTSHouseholdDiary selectedHTSHhold, int spHholdID,
			List<Integer> spStudents) {
		List<int[][]> tdSPStudents = new ArrayList<>();
		ArrayHandler arrHdlr = new ArrayHandler();
		int nSPStudents = spStudents.size();
		int nHTSStudents = selectedHTSHhold.getnStudents();

		if (this.getHtsStudentsTDPool() == null
				|| this.getHtsStudentsTDPool().size() == 0) {
			logger.warn("Fail to assign travel diary to students in a SP household. " 
							+ "Cannot find any households in HTS data that have students."); 
			return null;
		}

		if (nSPStudents <= nHTSStudents) {// the number of SP students is
											// smaller than the number of HTS
											// students
			// randomly pick TD of HTS students and assign it to SP students
			int[] htsStudentIndices = arrHdlr.pickRandomFromArray(
					arrHdlr.makeIncrementalIntArray(0, nHTSStudents, 1), null,
					nSPStudents, this.getRandom());
			for (int i = 0; i <= htsStudentIndices.length - 1; i++) {
				int[][] tdStudent = assignTravelDiary(selectedHTSHhold
						.getStudentTD().get(htsStudentIndices[i]), spHholdID,
						spStudents.get(i));
				tdSPStudents.add(tdStudent);
			}
		} else { // randomly pick TD of students in any HTS hholds and assign it
					// to the remaining students in spHhold
			// first assign TD of students in the selected HTS hhold to students
			// in spHhold
			for (int i = 0; i <= nHTSStudents - 1; i++) {
				int[][] tdStudent = assignTravelDiary(selectedHTSHhold
						.getStudentTD().get(i), spHholdID, spStudents.get(i));
				tdSPStudents.add(tdStudent);
			}
			// randomly picks up TD from the pool of student TDs in all HTS
			// households and assigns these TDs to the remaining SP students
			for (int i = nHTSStudents; i <= nSPStudents - 1; i++) {
                int individualId = spStudents.get(i);
                int randInt = getRandom().nextInt(getHtsStudentsTDPool().size());
                int[][] tdStudent = assignTravelDiary(getHtsStudentsTDPool().get(randInt), spHholdID,
                        individualId);
                tdSPStudents.add(tdStudent);
            }
		}

		return tdSPStudents;
	}

	/**
	 * 
	 * @param selectedHTSHhold
	 * @param spHholdID
	 * @param spAdults
	 * @return
	 */
	private List<int[][]> assignTDToSPAdults(
			HTSHouseholdDiary selectedHTSHhold, int spHholdID,
			List<Integer> spAdults) {
		List<int[][]> tdSPAdults = new ArrayList<>();
		ArrayHandler arrHdlr = new ArrayHandler();
		int nSPAdults = spAdults.size();
		int nHTSOtherAdults = selectedHTSHhold.getnOtherAdults();

		if (this.getHtsOtherAdultsTDPool() == null
				|| this.getHtsOtherAdultsTDPool().size() == 0) {
			logger.warn("Fail to assign travel diary to non-student adults in a SP household. " 
							+ "Cannot find any households in HTS data that have non-student adults."); 
			return null;
		}

		if (nSPAdults < nHTSOtherAdults) {
			// randomly pick TD of HTS adults and assign it to SP students
			int[] htsAdultsIndices = arrHdlr.pickRandomFromArray(
					arrHdlr.makeIncrementalIntArray(0, nHTSOtherAdults - 1, 1),
					null, nSPAdults, this.getRandom());
			for (int i = 0; i <= htsAdultsIndices.length - 1; i++) {
				int[][] tdOtherAdult = assignTravelDiary(selectedHTSHhold
						.getOtherAdultsTD().get(htsAdultsIndices[i]),
						spHholdID, spAdults.get(i));
				tdSPAdults.add(tdOtherAdult);
			}
		} else { // the number of non-student adults in the selected HTS hhold
					// is smaller than the number of non-student adults in
					// spHhold
			// first assigns TD of the HTS non-student adults to non-student
			// adults in spHhold
			for (int i = 0; i <= nHTSOtherAdults - 1; i++) {
				int[][] tdOtherAdult = assignTravelDiary(selectedHTSHhold
						.getOtherAdultsTD().get(i), spHholdID, spAdults.get(i));
				tdSPAdults.add(tdOtherAdult);
			}
			// randomly picks from the pool of TDs of non-student adults in all
			// HTS households
			for (int i = nHTSOtherAdults; i <= nSPAdults - 1; i++) {
                int individualId = spAdults.get(i);
                int randInt = getRandom().nextInt(getHtsOtherAdultsTDPool().size());
                int[][] tdOtherAdult = assignTravelDiary(getHtsOtherAdultsTDPool().get(randInt), spHholdID,
                        individualId);
                tdSPAdults.add(tdOtherAdult);
            }
		}
		return tdSPAdults;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public List<int[][]> getHtsStudentsTDPool() {
		return htsStudentsTDPool;
	}

	public void setHtsStudentsTDPool() {
		List<int[][]> tmpStudentTDPool = new ArrayList<>();
		Map<Integer, List<HTSHouseholdDiary>> htsDiaries = this
				.getHtsTDsByHholdType();

		for (List<HTSHouseholdDiary> tdsByHholdType : htsDiaries.values()) {
			for (HTSHouseholdDiary tdOfHhold : tdsByHholdType) {
				for (int[][] tdStudent : tdOfHhold.getStudentTD()) {
					tmpStudentTDPool.add(tdStudent);
                }
            }
        }

		this.htsStudentsTDPool = tmpStudentTDPool;
	}

	public List<int[][]> getHtsOtherAdultsTDPool() {
		return htsOtherAdultsTDPool;
	}

	public void setHtsOtherAdultsTDPool() {
		List<int[][]> tmpAdultTDPool = new ArrayList<>();
		Map<Integer, List<HTSHouseholdDiary>> htsDiaries = this.getHtsTDsByHholdType();

		for (List<HTSHouseholdDiary> tdsByHholdType : htsDiaries.values()) {
			for (HTSHouseholdDiary tdOfHhold : tdsByHholdType) {
				for (int[][] tdAdult : tdOfHhold.getOtherAdultsTD()) {
					tmpAdultTDPool.add(tdAdult);
                }
            }
        }

		this.htsOtherAdultsTDPool = tmpAdultTDPool;
	}

	public Map<Integer, List<HTSHouseholdDiary>> getHtsTDsByHholdType() {
		return htsTDsByHholdType;
	}

	public void setHtsTDsByHholdType(
			Map<Integer, List<HTSHouseholdDiary>> htsTDsByHholdType) {
		this.htsTDsByHholdType = htsTDsByHholdType;
	}

	public void testReadHSTFromCSV(String filename) {
		Map<Integer, List<int[][]>> rawHholdDiaries = this.readHTSFromCSV(filename, true);
		for (Map.Entry<Integer, List<int[][]>> entry : rawHholdDiaries.entrySet()) {
			List<int[][]> thisHholdTypeTD = entry.getValue();
			logger.debug("Hhold type " + thisHholdTypeTD.get(0)[0][3]); 
			for (int[][] hholdTD : thisHholdTypeTD) {
				logger.debug("Hhold ID " + hholdTD[0][0]); 
				for (int tmpi = 0; tmpi <= hholdTD.length - 1; tmpi++) {
					logger.debug(hholdTD[tmpi][0] + "," 
							+ hholdTD[tmpi][1] + "," + hholdTD[tmpi][2] + ","  
							+ hholdTD[tmpi][3] + "," + hholdTD[tmpi][4] + ","  
							+ hholdTD[tmpi][5] + "," + hholdTD[tmpi][6] + ","  
							+ hholdTD[tmpi][7] + "," + hholdTD[tmpi][8] + ","  
							+ hholdTD[tmpi][9] + "," + hholdTD[tmpi][10]); 
                }
			}
		}
	}

	public String displayHtsTDsByHholdType() {
        StringBuilder builder = new StringBuilder();

		Map<Integer, List<HTSHouseholdDiary>> newHholdTD = this
				.getHtsTDsByHholdType();
		for (Map.Entry<Integer, List<HTSHouseholdDiary>> entry : newHholdTD.entrySet()) {
			List<HTSHouseholdDiary> thisHholdTypeTD = entry.getValue();
			builder.append("Hhold type ").append(entry.getKey()); 
			for (HTSHouseholdDiary td : thisHholdTypeTD) {
                builder.append("\n\tHhold ID ").append(td.getHholdID()).append(", nResidents ").append(td.getnResidents());

                builder.append("\n\tnStudents ").append(td.getnStudents());
				for (int[][] stuTD : td.getStudentTD()) {
                    builder.append("\n\t\tindiv ID ").append(stuTD[0][1]);
					for (int tmpi = 0; tmpi <= stuTD.length - 1; tmpi++) {
                        builder.append("\n\t\t").append(stuTD[tmpi][0]).append(',')
								.append(stuTD[tmpi][1]).append(',').append(stuTD[tmpi][2]).append(',')
                                .append(stuTD[tmpi][3]).append(',').append(stuTD[tmpi][4]).append(',')
                                .append(stuTD[tmpi][5]).append(',').append(stuTD[tmpi][6]).append(',')
                                .append(stuTD[tmpi][7]).append(',').append(stuTD[tmpi][8]).append(',')
							    .append(stuTD[tmpi][9]).append(',').append(stuTD[tmpi][10]);
                    }
				}

                builder.append("\n\tnDependentChild ").append(td.getnDependentChild());
				for (int[][] childTD : td.getDependentChildTD()) {
                    builder.append("\n\t\tindiv ID ").append(childTD[0][1]);
					for (int tmpi = 0; tmpi <= childTD.length - 1; tmpi++) {
                        builder.append("\n\t\t").append(childTD[tmpi][0]).append(',')
								.append(childTD[tmpi][1]).append(',').append(childTD[tmpi][2]).append(',')
                                .append(childTD[tmpi][3]).append(',').append(childTD[tmpi][4]).append(',')
                                .append(childTD[tmpi][5]).append(',').append(childTD[tmpi][6]).append(',')
                                .append(childTD[tmpi][7]).append(',').append(childTD[tmpi][8]).append(',')
                                .append(childTD[tmpi][9]).append(',').append(childTD[tmpi][10]);
                    }
				}

                builder.append("\n\tnOtherAdults ").append(td.getnOtherAdults()); 
				for (int[][] adultTD : td.getOtherAdultsTD()) {
                    builder.append("\n\t\tindiv ID ").append(adultTD[0][1]); 
					for (int tmpi = 0; tmpi <= adultTD.length - 1; tmpi++) {
                        builder.append("\n\t\t").append(adultTD[tmpi][0]).append(',')
                                .append(adultTD[tmpi][1]).append(',').append(adultTD[tmpi][2]).append(',')
                                .append(adultTD[tmpi][3]).append(',').append(adultTD[tmpi][4]).append(',')
                                .append(adultTD[tmpi][5]).append(',').append(adultTD[tmpi][6]).append(',')
                                .append(adultTD[tmpi][7]).append(',').append(adultTD[tmpi][8]).append(',')
                                .append(adultTD[tmpi][9]).append(',').append(adultTD[tmpi][10]);
                    }
				}
			}
		}

        return builder.toString();
	}

	/**
	 * 
	 * @param spTD
	 *            : list of all TD of all individuals in a household with
	 *            structure of int[][] as [hholdID, indivID, tripID, departure
	 *            time, trip time, mode, purpose, origin, destination] hholdID -
	 *            indivID - tripID : integer departure time - trip time : in
	 *            seconds - purpose: 1 Change mode 2 Home 3 Go to work 4 Return to
	 *            work 5 Work related business 6 Education 7 Shopping 8 Personal
	 *            business/services 9 Social/recreation 10 Serve passenger 11
	 *            Other - mode: 1 Vehicle driver 2 Vehicle passenger 3 Train 4
	 *            Bus nightride 5 Bus gov 6 Bus priv 7 School bus gov 8 School
	 *            bus priv 9 Ferry gov 10 Ferry priv 11 Monorail 12 Light rail
	 *            13 Taxi: booked 14 Taxi: flagged down 15 Aircraft 16 Walk 17
	 *            Wheelchair 18 Bicycle 98 Other (specify) 99 Other (not
	 *            specify) origin - destination : 0 as initial
	 * 
	 * @author vlcao
	 */
	public List<int[][]> setODtoSpTD(List<int[][]> spTD,
			LocationTypes locationTypes) {
		List<int[][]> spTDwOD = new ArrayList<>();

		for (int i = 0; i < spTD.size(); i++) {
			int[][] tdHhold = spTD.get(i); // all travel diaries of an
											// individual
			int origin = 0, destination = 0;

			for (int j = 0; j < tdHhold.length; j++) {
				/*
				 * when this is the first trip, the origin will be home
				 * location, and when this is NOT the first trip, the origin
				 * will be the same with the previous destination
				 */
				if (j == 0) {
					origin = 0; // "Home"
				} else {
					origin = tdHhold[j - 1][8];
				}

				/*
				 * when this is the last trip, the destination will be home
				 * location, and when this is NOT the last trip, the destination
				 * will be randomly pick up from possible destinations
				 * associated with the purpose of the trip, except the
				 * "education", "home", and "change mode" purposes
				 */
				if (j == (tdHhold.length - 1)) {
					destination = 0; // "Home"
				} else {
					int individualAge = main.getIndividualPool()
							.getByID(tdHhold[j][1]).getAge();
					int thismode = 0, nextmode = 0;
					List<FacilityType> listOfDestinations = locationTypes
							.getPurposeLocations().get(tdHhold[j][6]);

					if (tdHhold[j][6] == 6) {// "Education"
						if (individualAge <= 4) {
							destination = FacilityType.CHILD_CARE_CENTRE.getValue();
						} else if (individualAge == 5) {
							destination = FacilityType.KINDERGARTEN.getValue();
						} else if (individualAge <= 11) {
							destination = FacilityType.EDUCATION_PRIMARY.getValue();
						} else if (individualAge <= 17) {
							destination = FacilityType.EDUCATION_SCHOOL.getValue();
						} else {
							destination = FacilityType.EDUCATION_UNIVERSITY.getValue();
						}

					} else if (tdHhold[j][6] == 2) { // "Home"
						destination = 0;

					} else if (tdHhold[j][6] == 1) { // "Change mode"

						thismode = tdHhold[j][5];
						nextmode = tdHhold[j + 1][5];

						if (nextmode == 12) { // ("Light rail"
							destination = FacilityType.LIGHT_RAIL_STATION.getValue();
						} else if (nextmode >= 4 && nextmode <= 8) { // "Bus"
							destination = FacilityType.BUS_STATION.getValue();
						} else if (nextmode == 13 || nextmode == 14 // Taxi
								|| nextmode == 1 || nextmode == 2) { // Car
																		// drive
																		// or
																		// Car
							// passenger
							destination = FacilityType.CARPARK.getValue();
						} else if (thismode == 12) { // ("Light rail"
							destination = FacilityType.LIGHT_RAIL_STATION.getValue();
						} else if (thismode >= 4 && thismode <= 8) { // "Bus"
							destination = FacilityType.BUS_STATION.getValue();
						} else if (thismode == 13 || thismode == 14 // Taxi
								|| thismode == 1 || thismode == 2) { // Car drive
																	// or Car
							// passenger
							destination = FacilityType.CARPARK.getValue();
                        } else {
							destination = listOfDestinations.get(random
									.nextInt(listOfDestinations.size())).getValue();
						}
					} else {
						destination = listOfDestinations.get(random
								.nextInt(listOfDestinations.size())).getValue();
					}
				}

				/* save the OD pair of each trip of an individual */
				tdHhold[j][7] = origin;
				tdHhold[j][8] = destination;
			}

			spTDwOD.add(tdHhold);
		}

		return spTDwOD;
	}

	/*
	 * Each record represents an individual. The columns in spHhold are (in
	 * order) - hholdID, ID of the SP household (from householdPool) - indivID,
	 * ID of the SP individual (from individualPool) - hhholdRelationship,
	 * values of householdRelationship need to be converted to integers as
	 * followed 0 Married 1 DeFacto 2 LoneParent 3 U15Child 4 Student 5 O15Child
	 * 6 Relative 7 NonRelative 8 GroupHhold 9 LonePerson 10 Visitor hholdType,
	 * values of hholdType need to be converted to integers as followed 0 NF 1
	 * HF1 2 HF2 3 HF3 4 HF4 5 HF5 6 HF6 7 HF7 8 HF8 9 HF9 10 HF10 11 HF11 12
	 * HF12 13 HF13 14 HF14 15 HF15 16 HF16
	 */
	public int[][] transform(Household household) {

		int[][] spHhold = new int[household.getNumberResidents()][4];

		for (int i = 0; i < household.getResidents().size(); i++) {
			spHhold[i][0] = household.getId();
			spHhold[i][1] = household.getResidents().get(i).getId();
			spHhold[i][2] = household.getResidents().get(i).getHouseholdRelationship().getIntValue();
			spHhold[i][3] = Category.codeHouseholdCategory(household.getResidents().get(i).getHholdCategory());
		}

		return spHhold;
	}

	// ==============================================================================
	/**
	 * Transfer data from Travel Diary with HTS format into Transims format.
	 * 
	 * @param spTDwOD
	 * 
	 * @author vlcao
	 */
	public void transferSpTD2IndividualTD(List<int[][]> spTDwOD) {
		/*
		 * TravelDiaries columns are:
		 * 0.[travel_id]1.[individual_id]2.[household_id]
		 * 3.[age]4.[gender]5.[income]6.[origin]7.[destination]
		 * 8.[start_time]9.[end_time]10.[duration]
		 * 11.[travel_mode]12.[purpose]13.[vehicle_id] 14.[trip_id]
		 */

		int travelID = 1;
        for (int[][] tdHhold : spTDwOD) {
            /*
			 * tdHhold = [hholdID, indivID, tripID, departure time, trip time,
			 * mode, purpose, origin, destination]
			 */
            int[][] travelDiariesWeekdays = new int[tdHhold.length][15];
            Individual thisIndividual = main.getIndividualPool().getByID(tdHhold[0][1]);

			/*
			 * transfer values of travel diaries from HTS format to Transims
			 * format
			 */
            for (int m = 0; m < tdHhold.length; m++) {
                travelDiariesWeekdays[m][0] = travelID++; // travel_id
                travelDiariesWeekdays[m][1] = tdHhold[m][1]; // individual_id
                travelDiariesWeekdays[m][2] = tdHhold[m][0]; // household_id
                travelDiariesWeekdays[m][3] = thisIndividual.getAge(); // age
                travelDiariesWeekdays[m][4] = thisIndividual.getGender() == Gender.Male ? 1 : 0; // gender
                travelDiariesWeekdays[m][5] = thisIndividual.getIncome().intValue(); // income
                travelDiariesWeekdays[m][6] = tdHhold[m][7]; // origin
                travelDiariesWeekdays[m][7] = tdHhold[m][8]; // destination
                travelDiariesWeekdays[m][8] = tdHhold[m][3]; // start_time
                travelDiariesWeekdays[m][9] = tdHhold[m][3] + tdHhold[m][4]; // end_time
                travelDiariesWeekdays[m][10] = tdHhold[m][4]; // duration
                travelDiariesWeekdays[m][11] = getTravelModeCodeTransims(tdHhold[m][5]).getIntValue(); // travel_mode
                travelDiariesWeekdays[m][12] = getPurposeCodeTransims(tdHhold[m][6]); // purpose
                travelDiariesWeekdays[m][14] = tdHhold[m][2]; // trip_id

				/* save purpose into hash map */
                String hHoldPersonTripTD = String.valueOf(tdHhold[m][0]) + "_"  
                        + tdHhold[m][1] + "_" + tdHhold[m][2]; 
                main.getHmHhPersonTripTDPurpose().put(hHoldPersonTripTD,getPurposeHTSString(tdHhold[m][6]));

                // identify trips does not travel (stay at home, with
                // origin=destination=start_time=end_time=duration=0_
                if (travelDiariesWeekdays[m][6] == 0 && travelDiariesWeekdays[m][7] == 0 && travelDiariesWeekdays[m][8] == 0 &&
                        travelDiariesWeekdays[m][9] == 0 && travelDiariesWeekdays[m][10] == 0) {
                    // assign -1 for origin, destination, end_time, duration & travel_mode
                    travelDiariesWeekdays[m][6] = travelDiariesWeekdays[m][7] = travelDiariesWeekdays[m][8] =
                            travelDiariesWeekdays[m][9] = travelDiariesWeekdays[m][10] = travelDiariesWeekdays[m][11] = -1;
                }

            }

			/* save to the property of the individual */
            thisIndividual.setTravelDiariesWeekdays(travelDiariesWeekdays);
        }
	}

	// ========================================================================================================
	/**
	 * Get the purpose code according to TRANSIMS' Trip purpose format.
	 * 
	 * @param purposeHTS : 1 Change mode 2 Home 3 Go to work 4 Return to
	 *            work 5 Work related business 6 Education 7 Shopping 8 Personal
	 *            business/services 9 Social/recreation 10 Serve passenger 11
	 *            Other
	 * @return TRANSIMS' Trip purpose code 0 =Home / 1 = Work / 2 = Shop / 3 =
	 *         Visit / 4 = Social Recreation / 5 = Other 6 = Serve Passenger / 7
	 *         = School / 8 = College
	 * 
	 * @author Vu Lam Cao
	 */
	public int getPurposeCodeTransims(int purposeHTS) {
		int purposeCode = 5;

//		if (purposeHTS == 2) { // Home
//			purposeCode = 0;
//		} else if (purposeHTS == 3 || purposeHTS == 4) {
//			purposeCode = 1;
//		} else if (purposeHTS == 7) {
//			purposeCode = 2;
//		} else if (purposeHTS == 9) {
//			purposeCode = 4;
//		} else if (purposeHTS == 6) {
//			purposeCode = 8;
//		} else {
//			purposeCode = 5;
//        }

		// new purpose conversions by NH. Modified on 11 September 2013.
		if (purposeHTS == 2) { // Home
			purposeCode = 0;
		} else if (purposeHTS == 3 || purposeHTS == 4 || purposeHTS==5) {
			purposeCode = 1;
		} else if (purposeHTS == 6) {
			purposeCode = 8;
		} else if (purposeHTS == 7) {
			purposeCode = 2;
		} else if (purposeHTS == 8) {
			purposeCode = 3;
		} else if (purposeHTS == 9) {
			purposeCode = 4;
		} else if (purposeHTS == 10) {
			purposeCode = 6;
		} else {
			purposeCode = 5;
        }
		
		return purposeCode;
	}


	// ========================================================================================================

	/**
	 * Get the Travel mode code according to version 4 TRANSIMS' Trips mode
	 * format
	 * 
	 * @param modeHTS: 1 Vehicle driver 2 Vehicle passenger 3 Train 4
	 *            Bus nightride 5 Bus gov 6 Bus priv 7 School bus gov 8 School
	 *            bus priv 9 Ferry gov 10 Ferry priv 11 Monorail 12 Light rail
	 *            13 Taxi: booked 14 Taxi: flagged down 15 Aircraft 16 Walk 17
	 *            Wheelchair 18 Bicycle 98 Other (specify) 99 Other (not
	 *            specify)
	 * @return ersion 4 TRANSIMS' Trips mode: 1 = Walk / 2 = Drive alone / 3 =
	 *         Transit (bus) / 4 = Transit (rail) / 5 = Park-&-ride outbound / 6
	 *         = Park-&-ride inbound / 7 = Bicycle / 8 = Magic move / 9 = School
	 *         bus / 10 = 2 person carpool 11 = 3 person carpool (carpassenger)
	 *         / 12 = 4+ person carpool (taxi)/ 13 = Kiss-&-ride outbound / 14 =
	 *         Kiss-&-ride inbound
	 * 
	 * @author Vu Lam Cao
	 */
	private TravelModes getTravelModeCodeTransims(int modeHTS) {

		if (modeHTS == 1) {
			return TravelModes.CarDriver;
		} else if (modeHTS == 2) {
			return TravelModes.CarPassenger;
		} else if (modeHTS == 4 || modeHTS == 5 || modeHTS == 6 || modeHTS == 7
				|| modeHTS == 8 || modeHTS == 3) {
			return TravelModes.Bus;
		} else if (modeHTS == 11 || modeHTS == 12) {
			return TravelModes.Bus;
//			if (ModelMain.getInstance().getScenarioName().equalsIgnoreCase("base")) {
//				return TravelModes.Bus;
//			} else if (ModelMain.getInstance().getScenarioName().equalsIgnoreCase("m") ||
//					ModelMain.getInstance().getScenarioName().equalsIgnoreCase("n") ||
//					ModelMain.getInstance().getScenarioName().equalsIgnoreCase("o")) {
//				return TravelModes.LightRail;
//			} else {
//				try {
//					throw new Exception("wrong scenario name");
//				} catch (Exception e) {
//                    logger.error("Exception caught", e);
//				}
//			}
		} else if (modeHTS == 13 || modeHTS == 14) {
			return TravelModes.Taxi;
		} else if (modeHTS == 16) {
			return TravelModes.Walk;
		} else if (modeHTS == 18) {
			return TravelModes.Bike;
		}

		return TravelModes.Other;
	}

	private void recordCorrectedTrip(Individual indiv, int itrip, int lockedOrig, int lockedDest, HashMap<Integer,ArrayList<int[]>> correctedTrips) {
	}
	
	/**
	 * checks if the drive trip before trip itrip in travel diary of a car driver (indiv) have been corrected (ie they are listed in correctedDriverTrips).
	 * @param indiv
	 * @param itrip
	 * @param groupOrig
	 * @return
	 */
	private boolean isPreviousDriveTripCorrected(Individual indiv, int itrip, ArrayList<int[]> correctedDriverTrips) {
		if (itrip==0) return false;
		
		final int idxMode = 11;
		// Transims modes
		final int modeCarDriver = 2;
		
		// searches for the drive trip right before trip itrip
		int[][] thisIndivTD = indiv.getTravelDiariesWeekdays();
		int iprevDriveTrip = -1;
		for (int i=itrip-1; itrip>=0; itrip--)
			if (thisIndivTD[i][idxMode]==modeCarDriver) {
				iprevDriveTrip = i;
				break;
			}
		
		int[] tripDetails = new int[] {indiv.getId(), iprevDriveTrip};
		if (isTripCorrected(correctedDriverTrips, tripDetails)) return true;
		
		return false;
	}
	
	/**
	 * checks if the drive trip after trip itrip in travel diary of a car driver (indiv) have been corrected (ie they are listed in correctedDriverTrips).
	 * @param indiv
	 * @param itrip
	 * @param groupOrig
	 * @return
	 */
	private boolean isLaterDriveTripCorrected(Individual indiv, int itrip, ArrayList<int[]> correctedDriverTrips) {
		final int idxMode = 11;
		// Transims modes
		final int modeCarDriver = 2;
		
		int[][] thisIndivTD = indiv.getTravelDiariesWeekdays();
		if (itrip==thisIndivTD.length-1) return false;
		
		// searches for the drive trip right after trip itrip
		int ilaterDriveTrip = -1;
		for (int i=itrip+1; itrip<=thisIndivTD.length-1; itrip++)
			if (thisIndivTD[i][idxMode]==modeCarDriver) {
				ilaterDriveTrip = i;
				break;
			}
		
		int[] tripDetails = new int[] {indiv.getId(), ilaterDriveTrip};
		if (isTripCorrected(correctedDriverTrips, tripDetails)) return true;
		
		return false;
	}
	
	private ArrayList<int[]> getCarDriverIDs(ArrayList<int[]> coTravellers, Household household) {
		final int idxMode = 11;
		final int modeCarDriver = 2;
		
		ArrayList<int[]> carDriverIDs = new ArrayList<int[]>();
		for (int[] thisTravDetails : coTravellers) {
			int indivID = thisTravDetails[0];
			int itrip = thisTravDetails[1];
			Individual indiv = household.getIndividualInHholdByID(indivID);
			if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarDriver)
				carDriverIDs.add(thisTravDetails);
		}
		return carDriverIDs;
	}
	
	private ArrayList<int[]> getCarPassengersIDs(ArrayList<int[]> coTravellers, Household household) {
		final int idxMode = 11;
		final int modeCarPassenger = 11;
		ArrayList<int[]> carPasIDs = new ArrayList<int[]>();
		for (int[] thisTravDetails : coTravellers) {
			int indivID = thisTravDetails[0];
			int itrip = thisTravDetails[1];
			Individual indiv = household.getIndividualInHholdByID(indivID);
			if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]==modeCarPassenger)
				carPasIDs.add(thisTravDetails);
		}
		return carPasIDs;
	}
	
	private boolean isGroupHavingYoungestPas(ArrayList<int[]> coTravellers, int youngestPasID) {
		for (int[] trip : coTravellers) 
			if (trip[0]==youngestPasID) return true;
		return false;
	}
	
	
	private boolean isTripCorrected(ArrayList<int[]> correctedTrips, int[] newTrip) {
		for (int[] trip : correctedTrips) 
			if (trip[0]==newTrip[0] && trip[1]==newTrip[1])
				return true;
		return false;
	}
	
	
	private int getYoungestCarPassenger(HashMap<String,ArrayList<int[]>> departDurationTimeMap, Household household) {
		final int idxMode = 11;
		final int modeCarPassenger = 11;
		
		int youngestPasID = -1;
		Iterator<ArrayList<int[]>> itCoTravellers = departDurationTimeMap.values().iterator();
		while (itCoTravellers.hasNext()) {
			ArrayList<int[]> coTravellers = itCoTravellers.next();
			for (int[] thisTravDetails : coTravellers) {
				int indivID = thisTravDetails[0];
				int itrip = thisTravDetails[1];
				Individual indiv = household.getIndividualInHholdByID(indivID);
				if (indiv.getTravelDiariesWeekdays()[itrip][idxMode]!=modeCarPassenger) continue; 
				if (youngestPasID==-1) youngestPasID = indivID;
				else {
					int youngestAge = household.getIndividualInHholdByID(youngestPasID).getAge();
					if (indiv.getAge()<youngestAge) youngestPasID = indivID;
				}
			}
		}
		
		return youngestPasID;
	}

	
	
	/**
	 * Correct the travel diary created from HTS data by:
	 *  1 - Identify trips does not travel (stay at home) 
	 *  by assign value -1 for origin, destination, start_time, 
	 *  end_time, duration and mode when these parameter equal 0.  
	 *  These trips will not participate to any activity in the whole model. 
	 *  
	 *  2 - Assign 1 individual become a serve passenger one when 
	 * individuals in a household have trips with same departure time
	 * and duration. After that, set up the location and modes become 
	 * the same between the "serve passenger" individual and "passenger" 
	 * individuals 
	 * 
	 * Exception: 
	 * 		- When "serve passenger" individual has Car_driver mode,
	 * 		the "passenger" individuals have to become Car_passenger
	 * 		- In a household that has children, all children with trips 
	 * 		that have same departure time and duration with adults must
	 * 		become "passenger" ones. 
	 * 		  When children don't have any trips that have same departure time 
	 * 		and duration with adults, they will NOT travel (stay at home with 
	 * 		origin, destination, start_time,end_time, duration and mode 
	 * 		are assigned value -1) 
	 * 
	 * @author vlcao
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void runPatch1() {
		try {
			/*
			 * TravelDiaries columns are:
			 * 0.[travel_id]1.[individual_id]2.[household_id]
			 * 3.[age]4.[gender]5.[income]6.[origin]7.[destination]
			 * 8.[start_time]9.[end_time]10.[duration]
			 * 11.[travel_mode]12.[purpose]13.[vehicle_id] 14.[trip_id]
			 */

			for (Household household : main.getHouseholdPool().getHouseholds().values()) {
				Map<String, List<String>> departDurationTimeMap = new HashMap<>();

				/*
				 * A - Group all individuals having same departure time and duration (aka trip time) in a household by using hash map with the key is "departure time" and "duration time"
				 */
				for (Individual individual : household.getResidents()) {
					/* skip individuals without travel diary */
					if (individual.getTravelDiariesWeekdays() == null) 
						continue;
					
					/* search each trip in the travel diary of an individual */
					for (int i = 0; i < individual.getTravelDiariesWeekdays().length; i++) {
						//NOT search trips that does NOT travel (stay at home)  
						if (individual.getTravelDiariesWeekdays()[i][6]==-1 //origin
								|| individual.getTravelDiariesWeekdays()[i][7]==-1 //destination
								|| individual.getTravelDiariesWeekdays()[i][8]==-1 //start_time 
								|| individual.getTravelDiariesWeekdays()[i][9]==-1 //end_time
								|| individual.getTravelDiariesWeekdays()[i][10]==-1 //duration 
								|| individual.getTravelDiariesWeekdays()[i][11]==-1 ) //travel_mode
							continue;
						
						String departDurationTime = String.valueOf(individual.getTravelDiariesWeekdays()[i][8]) + "_" + individual.getTravelDiariesWeekdays()[i][10];
						String idIndexIndividual = String.valueOf(individual.getTravelDiariesWeekdays()[i][1]) + "_" + i;

						// group all individuals having same departure time and duration
						List<String> idIndexList = new ArrayList<>();
						if (departDurationTimeMap.get(departDurationTime) != null) 
							idIndexList = departDurationTimeMap.get(departDurationTime);
						idIndexList.add(idIndexIndividual);
						departDurationTimeMap.put(departDurationTime,idIndexList);
					}
				}

				/* B - Analyse each group of same departure time and duration */
                for (Map.Entry<String, List<String>> departDurationGroup : departDurationTimeMap.entrySet()) {
                    List<String> idIndexList = departDurationGroup.getValue();

                    boolean setServePassenger = false;
                    List<String> u15HholdCarPaxList = new ArrayList<>();
                    List<String> otherHholdCarPaxList = new ArrayList<>();
                    String carDriver = ""; 
                    
                    if (idIndexList.size() > 1) { // consider only groups that have at least 2 trips to match location and mode
                        boolean hasChildren = false, hasCarDriver = false;
                        int originChildren = 0, desChildren = 0, modeParent = 0;
                        int originServePax = 0, desServePax = 0, oldPurpose = 0;
                        int idxServePax = 0, idIndServePax = 0;

						/*
						 * B1 - Check whether the group has children (by checking the household relationship) as well as any Car driver in the group 
						 */
                        for (int j = 0; j < idIndexList.size(); j++) { // for each of the individuals that have same departure time and duration time
							/* get back the ID of individual and the Index of the selected trip of thisIndividual */
                            int[] idIndexArray = getbackIdNIndex(idIndexList.get(j));
                            int individualID = idIndexArray[0];
                            int index = idIndexArray[1];

                            // check whether this individual is a child by checking the household relationship
                            Individual thisIndividual = household.getIndividualInHholdByID(individualID);

                            if (thisIndividual.getHouseholdRelationship() == HouseholdRelationship.U15Child) {
                                // save the origin and destination of children's trips to assign for the parent's locations later
                                originChildren = thisIndividual.getTravelDiariesWeekdays()[index][6];
                                desChildren = thisIndividual.getTravelDiariesWeekdays()[index][7];
                                hasChildren = true;
                            } else { // when this individual is NOT a child check if this individual is a Car driver
                            	// save information about this individual
                                idxServePax = index;
                                idIndServePax = thisIndividual.getTravelDiariesWeekdays()[index][1];
                                oldPurpose = thisIndividual.getTravelDiariesWeekdays()[index][12];
                                originServePax = thisIndividual.getTravelDiariesWeekdays()[index][6];
                                desServePax = thisIndividual.getTravelDiariesWeekdays()[index][7];
                                modeParent = thisIndividual.getTravelDiariesWeekdays()[index][11];
                            	if (thisIndividual.getTravelDiariesWeekdays()[index][11] == 2) { // when this individual is a Car Driver
                                    hasCarDriver = true;
                                    setServePassenger = true;
                                    // provisionally assign this individual with "Serve Passenger" purpose. Old purpose will be assigned back later
                                    thisIndividual.getTravelDiariesWeekdays()[index][12] = 6; // "Serve Passenger"
                                    // initial the hash map "DriverTimeMap"
                                    carDriver = String.valueOf(household.getId()) + "," + thisIndividual.getId() + "," + thisIndividual.getTravelDiariesWeekdays()[index][14];
                                    main.getDriverTimeMap().put(carDriver, new double[]{0, 0, 0, 0, 0});
                                }
                            }
							
							/* condition break for time consuming saving */
                            //FIXME WHY break????!!! variables like modeParent may take different values between having break and not having break.
//                            if (hasCarDriver && hasChildren) 
//                            	break;
                        }
						
						/*
						 * B2 - Assign an individual to be as "Serve Passenger" when after the previous search, there is no one that is assigned this duty, 
						 * except the household has children but all children's trips are NOT same departure time and duration with any adult in the house
						 */
                        try {
                            Map<Integer, Boolean> individualNotTravelMap = new HashMap<>();

                            if (!setServePassenger) 
                                // when household has children but all children's trips are NOT same departure time and duration with any adult in the house, assign children NOT travel (stay at home)
                                if (hasChildren && idIndServePax == 0 && idxServePax == 0) {
                                    for (int j = 0; j < idIndexList.size(); j++) {
										/* get back the ID of individual and the Index of the selected trip of thisIndividual */
                                        int[] idIndexArray = getbackIdNIndex(idIndexList.get(j));
                                        int individualID = idIndexArray[0];
                                        individualNotTravelMap.put(individualID, true);
                                    }
                                } else { // otherwise, assign the last individual in this household (due to inform is saved from B1 step) as "Serve Passenger"
                                    Individual servePaxIndividual = household.getIndividualInHholdByID(idIndServePax);
                                    servePaxIndividual.getTravelDiariesWeekdays()[idxServePax][12] = 6; // "Serve Passenger"
                                    setServePassenger = true;
                                    servePaxIndividual = null;
                                }

                            // assign value -1 for all saved children to let them NOT travel (stay at home)
                            for (Individual individual : household.getResidents()) 
                                if (individualNotTravelMap.get(individual.getId()) != null) {
                                    int[][] diary = individual.getTravelDiariesWeekdays();
                                    for (int j = 0; j < diary.length; j++) {
                                        diary[j][6] = diary[j][7] = diary[j][8] = diary[j][9] = diary[j][10] = diary[j][11] = -1;
                                    }
                                }
                            
							/* free memory */
                            individualNotTravelMap = null;

                        } catch (Exception e) {
                            logger.error("idIndServePax: " + idIndServePax + "_" + "idxServePax: " + idxServePax, e);
                        }
						
						/*
						 * B3 - Assign the location and mode values for all trips in the group that has children
						 */
                        if (hasChildren) {
                            // NOT assign when household has children but all children's trips are NOT same departure time and duration with any adult in the house
                            if (idIndServePax == 0 && idxServePax == 0) 
                                continue;

                            // assignment process
                            for (int j = 0; j < idIndexList.size(); j++) {
								/* get back the ID of individual and the Index of the selected trip of thisIndividual */
                                int[] idIndexArray = getbackIdNIndex(idIndexList.get(j));
                                int individualID = idIndexArray[0];
                                int index = idIndexArray[1];

                                Individual thisIndividual = household.getIndividualInHholdByID(individualID);

                                // assign the mode for this individual who is a child
                                if (thisIndividual.getHouseholdRelationship() == HouseholdRelationship.U15Child) 
                                    // exception: when the mode of parents are "Car driver", assign the mode for this child is "Car passenger"
                                    if (modeParent == 2) {
                                        thisIndividual.getTravelDiariesWeekdays()[index][11] = 11;
                                        // save the hhPersonTrip number of this child to put into the map later
                                        String hhPersonTrip = String.valueOf(household.getId()) + "," + thisIndividual.getId() + "," + (index + 1);
                                        // put this U15 car passenger into a list
                                        u15HholdCarPaxList.add(hhPersonTrip);
                                    } else // when the mode of parent is NOT "Car driver", assign the mode for this child is the same as parents' mode
                                        thisIndividual.getTravelDiariesWeekdays()[index][11] = modeParent;
                                else { // assign the locations and mode for this individual who is NOT a child
                                    // assign the locations for this individual who is NOT a child as same as the child's
                                    thisIndividual.getTravelDiariesWeekdays()[index][6] = originChildren;
                                    thisIndividual.getTravelDiariesWeekdays()[index][7] = desChildren;

                                    // assign the modes for this individual who is NOT a child
                                    if (thisIndividual.getTravelDiariesWeekdays()[index][12] == 6)  // "Serve Passenger"
                                        // when this individual is the "serve passenger" individual, change back the old purpose to this individual
                                        thisIndividual.getTravelDiariesWeekdays()[index][12] = oldPurpose;
                                    // when this individual is NOT the "serve passenger" individual, set this individual's mode as same as the "serve passenger" individual
                                    else 
                                        // exception: when "serve passenger" individual's mode is "Car driver", the mode of this individual must be "Car passenger"
                                        if (modeParent == 2) {
                                            thisIndividual.getTravelDiariesWeekdays()[index][11] = 11; // "Car passenger"
                                            // save the hhPersonTrip number of this "Car passenger" to put into the map later
                                            String hhPersonTrip = String.valueOf(household.getId()) + "," + thisIndividual.getId() + "," + (index + 1);
                                            otherHholdCarPaxList.add(hhPersonTrip);
                                        } else 
                                            thisIndividual.getTravelDiariesWeekdays()[index][11] = modeParent;
                                }
                            }
                        }

						/*
						 * B4 - Assign the locations and mode values for all trips in the group that has NO children
						 */
                        else {
                            for (int j = 0; j < idIndexList.size(); j++) {
								/* get back the ID of individual and the Index of the selected trip of thisIndividual */
                                int[] idIndexArray = getbackIdNIndex(idIndexList.get(j));
                                int individualID = idIndexArray[0];
                                int index = idIndexArray[1];

                                Individual thisIndividual = household.getIndividualInHholdByID(individualID);

                                // when this individual is the "serve passenger" individual
                                if (thisIndividual.getTravelDiariesWeekdays()[index][12] == 6) // "Serve Passenger"
                                    // change back the old purpose to this "serve passenger" individual
                                    thisIndividual.getTravelDiariesWeekdays()[index][12] = oldPurpose;
                                
                                // when this individual is NOT the "serve passenger" individual, set this individual's mode and locations as same as the "serve passenger" individual
                                else {
                                    thisIndividual.getTravelDiariesWeekdays()[index][6] = originServePax;
                                    thisIndividual.getTravelDiariesWeekdays()[index][7] = desServePax;

                                    // exception: when "serve passenger" individual's mode is "Car driver", the mode of this individual must be "Car passenger"
                                    if (modeParent == 2) {
                                        thisIndividual.getTravelDiariesWeekdays()[index][11] = 11; // "Car passenger"
                                        // save the hhPersonTrip number of this "Car passenger" to put into the map later
                                        String hhPersonTrip = String.valueOf(household.getId()) + "," + thisIndividual.getId() + "," + (index + 1);
                                        otherHholdCarPaxList.add(hhPersonTrip);
                                    } else 
                                        thisIndividual.getTravelDiariesWeekdays()[index][11] = modeParent;
                                }
                            }
                        }

						/*
						 * B5 - Put values for the hash map of Car driver andCar Passenger
						 */
                        for (String u15Passenger : u15HholdCarPaxList) 
                            main.getU15HhodCarPaxCarDriverMap().put(u15Passenger, carDriver);
                        for (String u15Passenger : otherHholdCarPaxList) 
                            main.getOtherHhodCarPaxCarDriverMap().put(u15Passenger, carDriver);

                    }
                }

				departDurationTimeMap = null;
			}
		} catch (Exception e) {
            logger.error("Exception caught", e);
		}
	}

	// ========================================================================================================
	/**
	 * Get the purpose according to HTS purpose code format.
	 * 
	 * @param purposeCode
	 * @return HTS purpose as 1 Change mode 2 Home 3 Go to work 4 Return to work
	 *         5 Work related business 6 Education 7 Shopping 8 Personal
	 *         business/services 9 Social/recreation 10 Serve passenger 11 Other
	 * 
	 * @author Vu Lam Cao
	 */
	public String getPurposeHTSString(Integer purposeCode) {
		String purpose = "";

        switch (purposeCode) {
            case 1:
                purpose = "change_mode";
                break;
            case 2:
                purpose = "home";
                break;
            case 3:
                purpose = "go_to_work";
                break;
            case 4:
                purpose = "return_to_work";
                break;
            case 5:
                purpose = "work_related_business";
                break;
            case 6:
                purpose = "education";
                break;
            case 7:
                purpose = "shopping";
                break;
            case 8:
                purpose = "personal_business_services";
                break;
            case 9:
                purpose = "social_recreation";
                break;
            case 10:
                purpose = "serve_passenger";
                break;
            default:
                purpose = "other";
                break;
        }

		return purpose;
	}

	public int[] getbackIdNIndex(String idIndexString) {
		int[] idIndexArray = new int[2];
		
		char[] idIndexIndividual = idIndexString.toCharArray();
        StringBuilder individualIDString = new StringBuilder();

		/* get back the ID of individual */
		int breakPoint = 0;
		for (int k = 0; k < idIndexIndividual.length; k++) {
			if (idIndexIndividual[k] == '_') {
				breakPoint = k;
				break;
			} else {
				individualIDString.append(idIndexIndividual[k]);
            }
        }

		// get back the Index of the selected trip of
		// thisIndividual
        StringBuilder indexString = new StringBuilder();
		for (int k = breakPoint + 1; k < idIndexIndividual.length; k++) {
			indexString.append(idIndexIndividual[k]);
		}
		
		idIndexArray[0] = Integer.parseInt(individualIDString.toString());
		idIndexArray[1] = Integer.parseInt(indexString.toString());

		return idIndexArray;
	}
}
