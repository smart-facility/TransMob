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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import core.HardcodedData;
import core.ModelMain;
import core.model.TextFileHandler;
import org.apache.log4j.Logger;

import core.synthetic.HouseholdPool;
import core.synthetic.household.Household;

/**
 * Class for fetching information for DB and handling dwelling allocation
 * 
 * @author qun
 * 
 */
public class DwellingAllocator {

	private static final Logger logger = Logger.getLogger(DwellingAllocator.class);
	private final DwellingPool dwellingPool;

	public DwellingAllocator() {
		this.dwellingPool = new DwellingPool();
	}

	/**
	 * allocates a dwelling for a household in a travel zone.
	 * 
	 * @param travelZoneId
	 *            the id of selected travel zone
	 * @param year
	 *            the selected year
	 * @param household
	 *            the household want to relocate
	 * @return whether allocation is successful
	 */
	public boolean allocateAvailableDwelling(int travelZoneId, int year, Household household) {

		for (int i = household.calculateNumberOfRoomNeed(); i <= HardcodedData.MAX_BEDROOMS; i++) {
//			for (int j = HardcodedData.START_YEAR; j <= year; j++) {
				String key = travelZoneId + "@" + year + "@" + i;

				List<Integer> listDwellingsID = dwellingPool.getDwellingsIDMap().get(key);

				if (listDwellingsID != null) {

					for (Integer dwellingID : listDwellingsID) {

						Dwelling dwelling = dwellingPool.getDwellingById(dwellingID);
						
						if (dwelling.getLivedHouseholdId() == -1) {

							if (household.getDwellingId() != 0) {
								moveOutDwelling(household);
							}
							moveInDwelling(household, dwelling);

							return true;
						}
					}
				}
//			}
		}

		return false;
	}

	/**
	 * allocates a dwelling for a household in a travel zone.
	 * 
	 * @param travelZoneId
	 *            the id of selected travel zone
	 * @param year
	 *            the selected year
	 * @param household
	 *            the household want to relocate
	 * @return whether allocation is successful
	 */
//	public boolean allocateAvailableDwellingForRelocation(int travelZoneId, int year, Household household) {
//
//		int maxnbrSearched = household.calculateNumberOfRoomNeed()+1;
//		if (maxnbrSearched > HardcodedData.MAX_BEDROOMS) {
//            maxnbrSearched = HardcodedData.MAX_BEDROOMS;
//        }
//		for (int i = household.calculateNumberOfRoomNeed(); i <= maxnbrSearched; i++) {
//			for (int j = HardcodedData.START_YEAR; j <= year; j++) {
//				String key = travelZoneId + "@" + j + "@" + i;
//
//				List<Dwelling> listDwellings = dwellingPool.getDwellingsMap().get(key);
//
//				if (listDwellings != null) {
//
//					for (Dwelling dwelling : listDwellings) {
//
//						if (dwelling.getLivedHouseholdId() == -1) {
//
//							if (household.getDwellingId() != 0) {
//								moveOutDwelling(household);
//							}
//							moveInDwelling(household, dwelling);
//
//							return true;
//						}
//					}
//				}
//			}
//		}
//
//		return false;
//	}
	
	public void outputDwellingStocks(int year) {
		for (int itz=0; itz<=HardcodedData.travelZonesLiveable.length-1; itz++) {
			int travelZoneId = HardcodedData.travelZonesLiveable[itz];
			int[] nAllDwellings = new int[] {0,0,0,0};
			
			for (int i=1; i<=HardcodedData.MAX_BEDROOMS; i++) {
				int nDwellings = 0;
				String key = travelZoneId + "@" + year + "@" + i;
				List<Integer> listDwellings = dwellingPool.getDwellingsIDMap().get(key);
				if (listDwellings != null) {
					nDwellings = listDwellings.size();
				}
				nAllDwellings[i-1] = nDwellings;
			}
			
			String textOut = String.valueOf(travelZoneId) + "," + 
							String.valueOf(nAllDwellings[0]) + "," +
							String.valueOf(nAllDwellings[1]) + "," +
							String.valueOf(nAllDwellings[2]) + "," +
							String.valueOf(nAllDwellings[3]);
			TextFileHandler.writeToText(String.valueOf(year)+"_dwellingStocks.csv", textOut, true);
		}
	}
	
	public HashMap<Integer,int[]> getDwellingStocksForYear(int year) {
		HashMap<Integer,int[]> totalDwellingStocks = new HashMap<Integer,int[]>();
		for (int itz=0; itz<=HardcodedData.travelZonesLiveable.length-1; itz++) {
			int travelZoneId = HardcodedData.travelZonesLiveable[itz];
			int[] nAllDwellings = new int[] {0,0,0,0};
			
			for (int i=1; i<=HardcodedData.MAX_BEDROOMS; i++) {
				int nDwellings = 0;
				String key = travelZoneId + "@" + year + "@" + i;
				List<Integer> listDwellings = dwellingPool.getDwellingsIDMap().get(key);
				if (listDwellings != null) {
					nDwellings = listDwellings.size();
				}
				nAllDwellings[i-1] = nDwellings;
			}
			
			totalDwellingStocks.put(travelZoneId, nAllDwellings);
		}
		return totalDwellingStocks;
	}
	
	public boolean allocateAvailableDwellingForRelocation(int travelZoneId, int nbrSelected, int year, Household household) {

		int nbr = nbrSelected;
		if (nbr>HardcodedData.MAX_BEDROOMS) {
			nbr = HardcodedData.MAX_BEDROOMS;
		}
		
		//for (int j = HardcodedData.START_YEAR; j <= year; j++) {
			String key = travelZoneId + "@" + year + "@" + nbr;

			List<Integer> listDwellingsID = dwellingPool.getDwellingsIDMap().get(key);

			if (listDwellingsID != null) {

				for (Integer dwellingID : listDwellingsID) {

					Dwelling dwelling = dwellingPool.getDwellingById(dwellingID);
					
					if (dwelling.getLivedHouseholdId() == -1) {

						if (household.getDwellingId() != 0) {
							moveOutDwelling(household);
						}
						moveInDwelling(household, dwelling);

						return true;
					} else {
						Household hholdHere = ModelMain.getInstance().getHouseholdPool().getHouseholds().get(dwelling.getLivedHouseholdId());
						if (hholdHere==null) {
							dwelling.setLivedHouseholdId(-1);
							if (household.getDwellingId() != 0) {
								moveOutDwelling(household);
							}
							moveInDwelling(household, dwelling);
							return true;
						} else if (hholdHere.getResidents()==null || hholdHere.getResidents().size()==0) {
							ModelMain.getInstance().getHouseholdPool().getHouseholds().remove(hholdHere);
							dwelling.setLivedHouseholdId(-1);
							if (household.getDwellingId() != 0) {
								moveOutDwelling(household);
							}
							moveInDwelling(household, dwelling);
							return true;
						}
					}
				}
			}
		//}
		

		return false;
	}
	
	
	/**
	 * The selected household move into a TransimsHouseholdLocation.
	 * 
	 * @param household
	 *            the selected household want to move
	 * @param dwelling
	 *            the selected Dwelling will be moved in
	 */
	public void moveInDwelling(Household household, Dwelling dwelling) {

		dwelling.setLivedHouseholdId(household.getId());

		household.setDwellingId(dwelling.getId());
		household.setLivedTravelZone(dwelling.getTravelZoneId());
	}

	/**
	 * the selected household want to move out current dwelling.
	 * 
	 * @param household
	 *            the selected household
	 */
	public void moveOutDwelling(Household household) {
		int currentDwellingID = household.getDwellingId();
		Dwelling dwelling = dwellingPool.getDwellings().get(currentDwellingID);

		dwelling.setLivedHouseholdId(-1);

		household.setDwellingId(0);
		household.setLivedTravelZone(0);
	}

	public void emptyDwelling(int dwellingId) {

		dwellingPool.getDwellings().get(dwellingId).setLivedHouseholdId(-1);
	}

//	/**
//	 * check whether a dwelling in a travel zone with bedrooms number before
//	 * certain year.
//	 * 
//	 * @param travelZoneId
//	 *            the selected travel zone
//	 * @param bedroomsNumber
//	 *            the minimal number of bedrooms
//	 * @param year
//	 *            the maximal year
//	 * @return whether the selected dwelling is available
//	 */
//	public boolean isTravelZoneAvailable(int travelZoneId, int bedroomsNumber, int year) {
//		int maxnbrSearched = bedroomsNumber + 1; // households will only search for dwellings that have same or 1 more bedrooms than what they need.
//		if (maxnbrSearched > HardcodedData.MAX_BEDROOMS) {
//            maxnbrSearched = HardcodedData.MAX_BEDROOMS;
//        }
//		for (int i = bedroomsNumber; i <= maxnbrSearched; i++) {
//			for (int j = HardcodedData.START_YEAR; j <= year; j++) {
//				String key = travelZoneId + "@" + j + "@" + i;
//				List<Dwelling> listDwellings = dwellingPool.getDwellingsMap().get(key);
//
//				if (listDwellings != null) {
//					for (Dwelling dwelling : listDwellings) {
//
//						if (dwelling.getLivedHouseholdId() == -1) {
//							return true;
//						}
//					}
//				}
//			}
//		}
//		return false;
//	}

	/**
	 * counts dwellings occupied by households living in TZ travelZoneId and compares with dwelling stocks in this TZ.
	 * only dwellings with bedroomsNumber or bedroomsNumber+1 are counted.
	 * returns true if the count is smaller than dwelling stocks in this travel zone.
	 * 
	 * @param travelZoneId
	 * @param bedroomsNumber
	 * @param year
	 * @return
	 */
	public boolean isTravelZoneAvailable(int travelZoneId, int bedroomsNumber, 
			Map<Integer, int[]> dwellingsOccupiedByTZ, HashMap<Integer,int[]> totalDwellingStocks) {
		int nbrSearched = bedroomsNumber;
		if (nbrSearched > HardcodedData.MAX_BEDROOMS) {
			nbrSearched = HardcodedData.MAX_BEDROOMS;
		}
		
//		Map<Integer, int[]> dwellingsOccupiedByTZ = getDwellingsOccupied(ModelMain.getInstance().getHouseholdPool(), year);
//		if (dwellingsOccupiedByTZ.get(travelZoneId)==null) {
//			return false;
//		}
//		int nDwellingsOccupiedThisTZ = dwellingsOccupiedByTZ.get(travelZoneId)[nbrSearched-1];
//
//		int nTotalDwellings = 0;
//		for (int j = HardcodedData.START_YEAR; j <= year; j++) {
//			String key = String.valueOf(travelZoneId) + "@" + String.valueOf(j) + "@" + String.valueOf(nbrSearched);
//			if (dwellingPool.getDwellingsMap().get(key)!=null) {
//				nTotalDwellings += dwellingPool.getDwellingsMap().get(key).size();
//			}
//		}
		
		if (dwellingsOccupiedByTZ.get(travelZoneId)==null || totalDwellingStocks.get(travelZoneId)==null) {
			return false;
		}
		int nDwellingsOccupiedThisTZ = dwellingsOccupiedByTZ.get(travelZoneId)[nbrSearched-1];
		int nTotalDwellings = totalDwellingStocks.get(travelZoneId)[nbrSearched-1];
		
		if (nTotalDwellings>nDwellingsOccupiedThisTZ) {
			return true;
		}
		
		return false;
	}
	
	
	public int clearEmptyDwelling(HouseholdPool householdPool) {
		int count = 0;
		for (Dwelling dwelling : dwellingPool.getDwellings().values()) {
			Integer hholdIndex = dwelling.getLivedHouseholdId();
			if (hholdIndex != null && hholdIndex != -1) {
				if (householdPool.getByID(hholdIndex) == null) {
					dwelling.setLivedHouseholdId(-1);
					count++;
				}
			}
		}

		return count;
	}

	public Map<Integer, int[]> calculateOccupancy() {
		Map<Integer, int[]> results = new HashMap<>();

		for (Dwelling dwelling : dwellingPool.getDwellings().values()) {
			Integer hholdIndex = dwelling.getLivedHouseholdId();
			if (hholdIndex != -1) {
				int tzid = dwelling.getTravelZoneId();
				int bedrooms = dwelling.getNumberOfRooms();
				int[] is = results.get(tzid);
				if (is == null) {
					int[] lived = new int[HardcodedData.MAX_BEDROOMS];
					lived[bedrooms - 1] = 1;
					results.put(tzid, lived);
				} else {
					is[bedrooms - 1]++;
				}
			}
		}
		return results;
	}

	public Map<Integer, double[]> calculateOccupancyRate(Map<Integer, int[]> results, int year, String schema) {

		DwellingControl dwellingControl = new DwellingControl();
		dwellingControl.getLimit(year, schema);

		Map<Integer, double[]> rateResults = new HashMap<>();

		for (Entry<Integer, int[]> entry : dwellingControl
				.getCurrentDwellingsCapacity().entrySet()) {
			int tzID = entry.getKey();
			double[] rates = new double[HardcodedData.MAX_BEDROOMS];

			int[] lived = results.get(tzID);
			int[] capacity = entry.getValue();

			if (lived != null) {
				for (int i = 0; i < rates.length; i++) {
					if (capacity[i] == 0) {
						rates[i] = 0;
					} else {
						rates[i] = lived[i] / (double) capacity[i];
					}
				}

			}
			rateResults.put(tzID, rates);
		}

		return rateResults;
	}

	/**
	 * @return the dwellingPool
	 */
	public DwellingPool getDwellingPool() {
		return dwellingPool;
	}

	/**
	 * returns the number of dwellings by number of bedrooms currently occupied in each travel zone.
	 * This is done by iterating through each household in a household pool.
	 * @param householdPool the household pool in which the occupied dwellings are counted.
	 * @return
	 */
	public Map<Integer, int[]> getDwellingsOccupied(HouseholdPool householdPool, int year) {
		
		HashMap<Integer,int[]> dwellingsByTZ = new HashMap<>();
		
		for (Household hhold : householdPool.getHouseholds().values()) {
			if (hhold==null) continue;
			
			Dwelling dwelling = dwellingPool.getDwellings().get(hhold.getDwellingId());
			if (dwelling==null) continue;
			
			int nBedrooms = dwelling.getNumberOfRooms();
			if (nBedrooms<0) continue;
			
			int tz = dwelling.getTravelZoneId();
			int[] nDwellings = new int[HardcodedData.MAX_BEDROOMS];
			if (dwellingsByTZ.containsKey(tz)) 
				nDwellings = dwellingsByTZ.get(tz);

            if (nBedrooms <= HardcodedData.MAX_BEDROOMS) {
                nDwellings[nBedrooms - 1] += 1;
            } else {
                nDwellings[HardcodedData.MAX_BEDROOMS-1] += 1;
            }

			dwellingsByTZ.put(tz, nDwellings);
		}

        if (logger.isTraceEnabled()) {
            ArrayList<String[]> dwellingStocks = new ArrayList<>();
            for (Entry<Integer, int[]> aDwellingByTz : dwellingsByTZ.entrySet()) {
                int[] crnDwellings = aDwellingByTz.getValue();
                dwellingStocks.add(new String[] {
                        String.valueOf(aDwellingByTz.getKey()),
                        String.valueOf(crnDwellings[0]),
                        String.valueOf(crnDwellings[1]),
                        String.valueOf(crnDwellings[2]),
                        String.valueOf(crnDwellings[3])});
            }

            TextFileHandler.writeToCSV(String.valueOf(year) + "_dwellingsOccupied.csv", dwellingStocks, false);
        }
		
		return dwellingsByTZ;
	}
	
}
