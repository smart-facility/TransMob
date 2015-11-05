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
package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.model.TextFileHandler;
import core.synthetic.household.Vehicle;


public class TransimsFilesHandler {

    private final int hholdIdxInTRIPS = 0;
	private final int personIdxInTRIPS = 1;
	private final int tripIdxInTRIPS = 2;
	private final int modeIdxInTRIPS = 4;
	private final int vehicleIdxInTRIPS = 5;
	private final int originIdxInTRIPS = 7;
	private final int destIdxInTRIPS = 9;

	private final int modeDRIVE = 2;
	private final int modeHOV4 = 12;

		/**
		 * corrects TRIPS file (TRANSIMS input). 
		 * This correction has 2 parts:
		 * 
		 * 1 - Correction in Origin of next trip and current trip in order to make sure
		 * they are the same. In this correction, the location of taxi also is updated
		 * in the Process_Link_2 file for TRANSIMS
		 *  
		 * 2 - Correction for trips with mode DRIVE (car driver):
		 * 		- IF at least one of the trips of an individual has DRIVE mode
		 * 		and the first trip mode is not DRIVE, change mode of all trips 
		 * 		among these two trips to DRIVE mode. This correction also ensures that 
		 * 		the vehicle IDs of these new DRIVE trips are the same. 
		 * 
		 * 		- IF this person doesn't drive at all, don't do anything.
		 * 
		 * @param tripsList
		 *            2D array containing all trips done by the population, grouped
		 *            by individuals, which are grouped by households. Columns in
		 *            tripsList are in the order of columns in TRANSIMS' TRIPS file,
		 *            which are below 1 household 2 person 3 trip 4 purpose 5 mode 6
		 *            vehicle 7 start time 8 origin 9 end time 10 destination 11
		 *            constraint
		 */
		public void correctTransimsFilesDriveOnly(int[][] tripsList) {

			// 1- correct to make sure that the origin of next trip
			// must be the same with the destination of current trip

            // make HashMap of tripsList index with key [hhold, person, trip]
            HashMap<String, Integer> hmTrips = new HashMap<>();
            String keyStr = "";

            for (int i = 0; i < tripsList.length; i++) {
                // only correct from the second trip of each individual's travel diary
                if (tripsList[i][tripIdxInTRIPS] != 1){
                    tripsList[i][originIdxInTRIPS] = tripsList[i-1][destIdxInTRIPS];
                }

                // put into Syd.VEHICLE the origin location of the taxi
                if (tripsList[i][4] == modeHOV4) {
                    int parkingLocation =  ModelMain.getInstance().getProcessLink().get(tripsList[i][originIdxInTRIPS] - 1);
                    for (Vehicle vehicle : ModelMain.getInstance().getVehicleList()) {
                        if ((vehicle.getVehicleId() == tripsList[i][vehicleIdxInTRIPS])
                                && (vehicle.gethHold() == tripsList[i][hholdIdxInTRIPS])) {

                            vehicle.setLocation(parkingLocation);
                            vehicle.setSubType(2);
                            break;
                        }
                    }
                }
                keyStr = String.valueOf(tripsList[i][hholdIdxInTRIPS]) + "_"
                        + String.valueOf(tripsList[i][personIdxInTRIPS]) + "_"
                        + String.valueOf(tripsList[i][tripIdxInTRIPS]);
                hmTrips.put(keyStr, i);
			}
			
			// get trips belong to each household
			List<int[][]> hholdTrips = getHholdTrips(tripsList);

			// 2 - correct Drive mode trips for each house hold
			for (int ihh = 0; ihh <= hholdTrips.size() - 1; ihh++) {
				// get trips belong to each person process
				int[][] thisHholdTrips = hholdTrips.get(ihh);
				List<int[][]> personsTrips = getPersonTrips(thisHholdTrips);

				// check and correct DRIVE trips of each person in this household
				for (int ipp = 0; ipp <= personsTrips.size() - 1; ipp++) { // for each person
					// get trips belong to each person
					int [][] thisPersonTrips = personsTrips.get(ipp);
					
					// check trips of this person has DRIVE
					int vehicleID=0;
					int idxTripHasDriveMode=0;
					for (int iTrips = 0; iTrips < thisPersonTrips.length; iTrips++) {
						if (thisPersonTrips[iTrips][modeIdxInTRIPS] == modeDRIVE) {
							
							// save the index and vehicle ID of the trip that has DRIVE mode
							vehicleID = thisPersonTrips[iTrips][vehicleIdxInTRIPS];
							idxTripHasDriveMode = iTrips;
						}	
					}
					
					
					// correct if trips of this person has DRIVE mode and
					// the first trip does NOT have DRIVE mode
					for (int i = idxTripHasDriveMode-1; i >= 0 ; i--) {
						// get index of current trip in trip list
						keyStr = String.valueOf(thisPersonTrips[i][hholdIdxInTRIPS]) + "_"
								+ String.valueOf(thisPersonTrips[i][personIdxInTRIPS]) + "_"
								+ String.valueOf(thisPersonTrips[i][tripIdxInTRIPS]);
						TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_toDriver.csv", keyStr, true);
						int idxCurrentTripInTripList = hmTrips.get(keyStr);
						
						// correct and update current trip
						tripsList[idxCurrentTripInTripList][modeIdxInTRIPS] = modeDRIVE;
						tripsList[idxCurrentTripInTripList][vehicleIdxInTRIPS] = vehicleID;
					}
					
				}
			}
			
		}
		
		
	/**
	 * extracts trips belonging to each household from a list of all trips of
	 * the population and stores them in an ArrayList.
	 * 
	 * @param tripsList
	 *            2D array containing all trips done by the population, grouped
	 *            by individuals, which are grouped by households. Columns in
	 *            tripsList are in the order of columns in TRANSIMS' TRIPS file,
	 *            which are below 1 household 2 person 3 trip 4 purpose 5 mode 6
	 *            vehicle 7 start time 8 origin 9 end time 10 destination 11
	 *            constraint
	 * @return an ArrayList of all trips belonging to a household (each record
	 *         in the array list represents a household).
	 */
	private List<int[][]> getHholdTrips(int[][] tripsList) {
		List<int[][]> hholdTrips = new ArrayList<>();

		ArrayList<int[]> crnhhTrips = new ArrayList<>();
		crnhhTrips.add(tripsList[0]);
		for (int itl = 1; itl <= tripsList.length - 2; itl++)
			if (tripsList[itl][this.hholdIdxInTRIPS] == tripsList[itl - 1][this.hholdIdxInTRIPS]) {
				crnhhTrips.add(tripsList[itl]);
            } else {
				int[][] tmpArray = new int[crnhhTrips.size()][crnhhTrips.get(0).length];
                tmpArray = crnhhTrips.toArray(tmpArray);
				hholdTrips.add(tmpArray);

				crnhhTrips = new ArrayList<>();
				crnhhTrips.add(tripsList[itl]);
			}
		crnhhTrips.add(tripsList[tripsList.length - 1]);
		int[][] tmpArray = new int[crnhhTrips.size()][crnhhTrips.get(0).length];
        tmpArray = crnhhTrips.toArray(tmpArray);
		hholdTrips.add(tmpArray);

		return hholdTrips;
	}

	/**
	 * extracts trips belonging to each individual from a list of trips of a
	 * household and stores them in an ArrayList.
	 * 
	 * @param thisHholdTrips
	 *            2D array containing all trips done by a household, grouped by
	 *            individuals. Columns in tripsList are in the order of columns
	 *            in TRANSIMS' TRIPS file, which are below 1 household 2 person
	 *            3 trip 4 purpose 5 mode 6 vehicle 7 start time 8 origin 9 end
	 *            time 10 destination 11 constraint
	 * @return an ArrayList of trips belonging to a person in a household (each
	 *         record in the array list represents a person).
	 */
	private List<int[][]> getPersonTrips(int[][] thisHholdTrips) {
		List<int[][]> personsTrips = new ArrayList<>();

		ArrayList<int[]> crnppTrips = new ArrayList<>();
		crnppTrips.add(thisHholdTrips[0]);
		for (int itl = 1; itl <= thisHholdTrips.length - 2; itl++) {
			if (thisHholdTrips[itl][this.personIdxInTRIPS] == thisHholdTrips[itl - 1][this.personIdxInTRIPS]) {
				crnppTrips.add(thisHholdTrips[itl]);
            } else {
				int[][] tmpArray = new int[crnppTrips.size()][crnppTrips.get(0).length];
                tmpArray = crnppTrips.toArray(tmpArray);
				personsTrips.add(tmpArray);

				crnppTrips = new ArrayList<>();
				crnppTrips.add(thisHholdTrips[itl]);
			}
        }
		crnppTrips.add(thisHholdTrips[thisHholdTrips.length - 1]);
		int[][] tmpArray = new int[crnppTrips.size()][crnppTrips.get(0).length];

        tmpArray = crnppTrips.toArray(tmpArray);

		personsTrips.add(tmpArray);

		return personsTrips;
	}
}
