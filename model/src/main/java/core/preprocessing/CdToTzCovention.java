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
package core.preprocessing;

import hibernate.postgres.SyntheticPopulationEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import core.HardcodedData;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

public class CdToTzCovention {

	private static final Logger logger = Logger
			.getLogger(CdToTzCovention.class);

	private final Map<Integer, List<List<Double>>> convention;

	public enum Column {
		Age, Gender, HouseholdRelationship, HouseholdId, HouseholdType, Salary, Cd
	}

	public CdToTzCovention(File file) {
		super();
		this.convention = new HashMap<Integer, List<List<Double>>>();
		read(file);
	}

	private void read(File file) {
		CSVReader csvReader = null;
		try {
			// Skip header line
			csvReader = new CSVReader(new BufferedReader(new FileReader(file)),
					CSVParser.DEFAULT_SEPARATOR,
					CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
			String[] nextLine;

			while ((nextLine = csvReader.readNext()) != null) {
				int cdId = Integer.parseInt(nextLine[0]);
				int tzId = Integer.parseInt(nextLine[1]);
				double propotion = Double.parseDouble(nextLine[3]);

				List<List<Double>> storedList = this.convention.get(cdId);
				if (storedList == null) {
					List<List<Double>> newList = new ArrayList<List<Double>>();
					List<Double> tempList = new ArrayList<Double>();
					tempList.add((double) tzId);
					tempList.add(propotion);
					newList.add(tempList);
					this.convention.put(cdId, newList);
				} else {
					List<Double> tempList = new ArrayList<Double>();
					tempList.add((double) tzId);
					tempList.add(propotion);
					storedList.add(tempList);
					this.convention.put(cdId, storedList);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Exception caught", e);
		} catch (IOException e) {
			logger.error("Exception caught", e);
		} finally {
			csvReader = null;
		}

	}

	@SuppressWarnings("boxing")
	public List<SyntheticPopulationEntity> generateIndivious(
			File sourceFile) {

		List<SyntheticPopulationEntity> result = new ArrayList<>();

		try (CSVReader csvReader = new CSVReader(new BufferedReader(
				new FileReader(sourceFile)))) {
			String[] nextLine = null;
			int id = 1;
			while ((nextLine = csvReader.readNext()) != null) {

				int cd = Integer.parseInt(nextLine[Column.Cd.ordinal()]
						.replaceAll("[^0-9]", ""));

				int tz = getTzId(cd, HardcodedData.random.nextDouble());

				if (tz != -1) {

					SyntheticPopulationEntity syntheticPopulationEntity = new SyntheticPopulationEntity();

					syntheticPopulationEntity.setId(id);
					syntheticPopulationEntity.setAge(Integer
							.parseInt(nextLine[Column.Age.ordinal()]));
					syntheticPopulationEntity.setGender(Integer
							.parseInt(nextLine[Column.Gender.ordinal()]));
					syntheticPopulationEntity.setIncome(Integer
							.parseInt(nextLine[Column.Salary.ordinal()]));
					syntheticPopulationEntity.setHouseholdRelationship(nextLine[Column.HouseholdRelationship.ordinal()]);
					syntheticPopulationEntity.setHouseholdId(Integer
							.parseInt(nextLine[Column.HouseholdId.ordinal()]));
					syntheticPopulationEntity.setHouseholdType(nextLine[Column.HouseholdType.ordinal()]);
					syntheticPopulationEntity.setTravelZone(tz);

					result.add(syntheticPopulationEntity);

					id++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public String toString() {
		return "CdToTzCovention [convention=" + this.convention.toString()
				+ "]";
	}

	public int getTzId(int cdId, double random) {
		List<List<Double>> storedList = this.convention.get(cdId);
		double value = 0;

		if (storedList != null)
			for (List<Double> list : storedList) {
				value += list.get(1);
				if (random <= value) {
					return list.get(0).intValue();
				}
			}

		return -1;
	}
}
