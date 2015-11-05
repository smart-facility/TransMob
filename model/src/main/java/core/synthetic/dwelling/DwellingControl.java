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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.ApplicationContextHolder;
import jdbc.dao.DwellingCapacityDAO;
import org.apache.log4j.Logger;

import jdbc.dao.DwellingCapacity;
import au.com.bytecode.opencsv.CSVReader;
import core.HardcodedData;
import core.synthetic.HouseholdPool;
import core.synthetic.household.Household;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A class controls the number of available dwellings each year.
 * 
 * @author qun
 * 
 * 
 */

public class DwellingControl {

	private Map<Integer, int[]> currentDwellingsCapacity;
	private Map<Integer, int[]> currentDwellingsLived;
	private final DwellingCapacityDAO dwellingCapacityDao;
    private static final Logger logger = Logger.getLogger(DwellingControl.class);


	public DwellingControl() {
		this.currentDwellingsCapacity = new HashMap<>();
		this.currentDwellingsLived = new HashMap<>();
        dwellingCapacityDao = ApplicationContextHolder.getBean(DwellingCapacityDAO.class);
	}

	public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContextHolder holder = ctx.getBean(ApplicationContextHolder.class);
        HardcodedData.prepend();

        DwellingControl dwellingControl = new DwellingControl();

		dwellingControl.getLimit(HardcodedData.START_YEAR, true);
	}

	/**
	 * Gets limits of different number of dwellings with different bedrooms.
	 * 
	 * @param year
	 *            a selected year
	 * @param isFirstTime
	 *            check whether it is the first year
	 */
	@SuppressWarnings("boxing")
	public void getLimit(int year, boolean isFirstTime) {
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new BufferedReader(new FileReader(HardcodedData.dwelling
					+ year + ".csv")));

			String[] nextLine = csvReader.readNext();

			while ((nextLine = csvReader.readNext()) != null) {
				// nextLine[] is an array of values from the line
				int[] numbers = { Integer.parseInt(nextLine[1]),
						Integer.parseInt(nextLine[2]),
						Integer.parseInt(nextLine[3]),
						Integer.parseInt(nextLine[4]) };

				this.currentDwellingsCapacity.put(
						Integer.parseInt(nextLine[0]), numbers);
				if (isFirstTime) {
					this.currentDwellingsLived.put(
							Integer.parseInt(nextLine[0]), new int[HardcodedData.MAX_BEDROOMS]);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} finally {
            try {
                if (csvReader != null) {
                    csvReader.close();
                }
            } catch (IOException e) {
                logger.error("Failed to close CSV file: " + HardcodedData.dwelling + year + ".csv");
            }
        }

	}

	public void getLimit(int year, String schema) {
		List<DwellingCapacity> dwellingCapacities = dwellingCapacityDao.findAll(year, schema);

		for (DwellingCapacity dwellingCapacity : dwellingCapacities) {

			int[] numbers = { dwellingCapacity.getOneBedroom(),
					dwellingCapacity.getTwoBedroom(), dwellingCapacity.getThreeBedroom(),
					dwellingCapacity.getFourBedroom() };

			this.currentDwellingsCapacity.put(dwellingCapacity.getTz06(),
					numbers);
		}
	}

	public Map<Integer, int[]> getCurrentDwellingsCapacity() {
		return this.currentDwellingsCapacity;
	}

	/**
	 * updates dwelling availability for the whole householdPool.
	 * 
	 * @param householdPool The household pool to update.
	 */
	public void update(HouseholdPool householdPool) {
		currentDwellingsLived.clear();

		for (int i = 0; i < HardcodedData.travelZonesLiveable.length; i++) {
			this.currentDwellingsLived.put(
					HardcodedData.travelZonesLiveable[i], new int[HardcodedData.MAX_BEDROOMS]);
		}

		for (Household household : householdPool.getHouseholds().values()) {

			int rooms = household.calculateNumberOfRoomNeed();
			if (rooms > 0) {
				int travalZone = household.getResidents().get(0)
						.getLivedTravelZoneid();

				int[] result = currentDwellingsLived.get(travalZone);
				result[rooms - 1]++;
				currentDwellingsLived.put(travalZone, result);
			} else {
				if (household.getNumberResidents() == 0) {
					householdPool.remove(household);
				}

			}

		}
	}
}
