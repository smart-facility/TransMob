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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;

/**
 * a class for counting work population
 * 
 * @author qun
 * 
 */
public class WorkPopulation {

    private static final Logger logger = Logger.getLogger(WorkPopulation.class);

	private File file;
	private Map<Double, Double> results;

	public WorkPopulation(File file) {
		super();
		this.file = file;
		results = new HashMap<Double, Double>();
	}

	public File getFile() {
		return file;
	}

	public Map<Double, Double> getResults() {
		return results;
	}

	public void setResults(Map<Double, Double> results) {
		this.results = results;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * gets total population from a file
	 * 
	 * @param idColumn
	 *            the column for id
	 * @param valueColumn
	 *            the column for value
	 */
	public void getTotalPopulation(int idColumn, int valueColumn) {

		double travelZoneID, frequnce, temp;
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new BufferedReader(new FileReader(file)));

			String[] nextLine;
			nextLine = csvReader.readNext();
			while ((nextLine = csvReader.readNext()) != null) {
				// nextLine[] is an array of values from the line
				travelZoneID = Double.parseDouble(nextLine[idColumn]);
				frequnce = Double.parseDouble(nextLine[valueColumn]);
				if (results.get(travelZoneID) == null) {
					results.put(travelZoneID, frequnce);
				} else {
					temp = results.get(travelZoneID);
					results.put(travelZoneID, temp + frequnce);
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
				// TODO Auto-generated catch block
                logger.error("Exception caught", e);
			}
		}

	}

	private String display(Map<Double, Double> results) {

        StringBuilder builder = new StringBuilder();
		for (Map.Entry<Double, Double> entry : results.entrySet()) {
			builder.append(entry.getKey());
            builder.append(", ");
		    builder.append(Math.round(entry.getValue()));
		}

        return builder.toString();
	}

	private Map<Double, Double> getWorkingPopulation(String workPopulationFile) {
		Map<Double, Double> cdWorkPopulationMap = null;
		CSVReader conversion = null;
		CSVReader workPopulation = null;
		try {
			conversion = new CSVReader(new BufferedReader(new FileReader(file)));

			workPopulation = new CSVReader(new BufferedReader(new FileReader(workPopulationFile)));

			Map<Integer, Integer> workPopulationMap = new HashMap<Integer, Integer>();
			cdWorkPopulationMap = new HashMap<Double, Double>();

			String[] nextLine;

			while ((nextLine = workPopulation.readNext()) != null) {
				workPopulationMap.put(Integer.parseInt(nextLine[0]),
						Integer.parseInt(nextLine[1]));
			}

			nextLine = conversion.readNext();

			while ((nextLine = conversion.readNext()) != null) {

				if (cdWorkPopulationMap.get(Double.parseDouble(nextLine[0])) == null) {
					cdWorkPopulationMap
							.put(Double.parseDouble(nextLine[0]),
									workPopulationMap.get(Integer
											.parseInt(nextLine[1]))
											* Double.parseDouble(nextLine[4]));
				} else {
					double temp = cdWorkPopulationMap.get(Double
							.parseDouble(nextLine[0]));
					cdWorkPopulationMap
							.put(Double.parseDouble(nextLine[0]),
									workPopulationMap.get(Integer
											.parseInt(nextLine[1]))
											* Double.parseDouble(nextLine[4])
											+ temp);
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
				if (conversion != null) {
                    conversion.close();
                }
				if (workPopulation != null) {
                    workPopulation.close();
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
                logger.error("Exception caught", e);
			}
		}

		return cdWorkPopulationMap;
	}
}
