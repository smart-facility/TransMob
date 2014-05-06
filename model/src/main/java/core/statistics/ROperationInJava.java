package core.statistics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import core.HardcodedData;

/**
 * A class transforms R operations in Java
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * @deprecated
 */
@Deprecated
public class ROperationInJava {

    private static final Logger logger = Logger.getLogger(ROperationInJava.class);

	public static HashMap<Integer, ArrayList<Integer>> tranvelZoneRoadMap;

	/**
	 * name="links"
	 * 
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="core.statistics.Link"
	 */
	private ArrayList<Link> links;

	static {
		tranvelZoneRoadMap = new HashMap<Integer, ArrayList<Integer>>();
		populateTravelZoneRoadList();
	}

	// public HashMap<Integer, ArrayList<Integer>> getTranvelZoneRoadMap() {
	// populateTravelZoneRoadList();
	// return tranvelZoneRoadMap;
	// }

	public ROperationInJava() {
		// tranvelZoneRoadMap=new HashMap<Integer, ArrayList<Integer>>();
		// tranvelZoneRoadMap=getTranvelZoneRoadMap();

	}

	public void generateLink(String weekMode) {
		HardcodedData.setPerformanceFileLocation(weekMode);
		HardcodedData.setEventsFileLocation(weekMode);
		links = readLink(HardcodedData.getPerformanceFileLocation());
	}

	public static Logger getLogger() {
		return logger;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	/**
	 * Reads link_list/*.csv files and stores in the arraylist
	 */
	private static void populateTravelZoneRoadList() {
		// HardcodedData.setPerformanceFileLocation("weekdays/");
		// HardcodedData.setEventsFileLocation("weekdays/");
		// HardcodedData.prepend(ModelRunServlet);
		for (int j = 0; j < HardcodedData.travelZones.length; j++) {
			String csvFilename = HardcodedData.linkList + "/"
					+ Integer.toString(HardcodedData.travelZones[j]) + ".csv";

			CSVReader reader = null;
			try {
				reader = new CSVReader(new BufferedReader(new FileReader(csvFilename)));
				String[] nextLine;
				reader.readNext();
				while ((nextLine = reader.readNext()) != null) {
					ArrayList<Integer> result = new ArrayList<Integer>();
					result.add(j);
					if (tranvelZoneRoadMap.containsKey(Integer
							.valueOf(nextLine[3]))) {
						tranvelZoneRoadMap.get(Integer.valueOf(nextLine[3]))
								.add(j);
					} else {
						tranvelZoneRoadMap.put(Integer.valueOf(nextLine[3]),
								result);
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
					if (reader != null) {
                        reader.close();
                    }
				} catch (IOException e) {
					// TODO Auto-generated catch block
                    logger.error("Exception caught", e);
				}
			}

		}
	}

	/**
	 * Reads links from files into the instance
	 * 
	 * @param fPerformance
	 *            the location of performance file
	 * @return returns 2 column array: 1st column is link id,2nd column is
	 *         required column, column start from 0;
	 */
	public ArrayList<Link> readLink(String fPerformance) {

		final int idPosition = 0, totalPerformanceColumn = 15, // totalEventsColumn
																// = 10, //
																// common
		startTimeColumn = 2, densityColumn = 8, speedColumn = 5; // used in
																	// performance
																	// file for
																	// version 5
																	// personColumn
																	// = 1,
																	// eventColumn
																	// = 5,
																	// actualColumn
																	// = 7,
																	// linkColumn
																	// = 8;//
																	// used
		// in
		// event
		// file

		ArrayList<Link> links = new ArrayList<Link>();

		final int[] peakHours = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
				14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
		int hours = 0;
		boolean isDuplicate = false;

		CSVReader reader = null;
		try {
			reader = new CSVReader(new BufferedReader(new FileReader(fPerformance)), '\t');
			String[] nextLine;
			reader.readNext();
			reader.readNext();
			// if
			// (nextLine.length==totalColumn&&Arrays.asList(peakHours).contains(Integer.valueOf(nextLine[startTimeColumn].split(":")[0])))
			// {
			// ArrayList<Double> averageDensity=new ArrayList<Double>();
			// averageDensity.add(Double.valueOf(nextLine[densityColumn]));
			// ArrayList<Double> averageSpeed=new ArrayList<Double>();
			// averageSpeed.add(Double.valueOf(nextLine[speedColumn]));
			// Link newLink=new
			// Link(Integer.valueOf(nextLine[idPosition]),averageDensity,averageSpeed);
			// links.add(newLink);
			// }
			//

			while ((nextLine = reader.readNext()) != null) {

				// nextLine[] is an array of values from the line
				ArrayList<Link> tempLinks = new ArrayList<Link>(links);

				hours = Integer
						.valueOf(nextLine[startTimeColumn].split(":")[0]);
				if (nextLine.length == totalPerformanceColumn
						&& ArrayUtils.contains(peakHours, hours)) {
					for (Link link : tempLinks) {
						if (link.getLink() == Integer
								.valueOf(nextLine[idPosition])) {
							link.getDensity().add(
									Double.valueOf(nextLine[densityColumn]));
							link.getSpeed().add(
									Double.valueOf(nextLine[speedColumn]));
							// link.getActualTime().add(Integer.valueOf(nextLine[actualColumn]));
							isDuplicate = true;
						}
					}
					if (!isDuplicate) {
						ArrayList<Double> densitys = new ArrayList<Double>();
						densitys.add(Double.valueOf(nextLine[densityColumn]));
						ArrayList<Double> speeds = new ArrayList<Double>();
						speeds.add(Double.valueOf(nextLine[speedColumn]));
						Link newLink = new Link(
								Integer.valueOf(nextLine[idPosition]));
						newLink.setDensity(densitys);
						newLink.setSpeed(speeds);
						links.add(newLink);
					}
				}
				isDuplicate = false;
				// break;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} finally {
			try {
				if (reader != null) {
                    reader.close();
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
                logger.error("Exception caught", e);
			}
		}
		return links;
	}

	/**
	 * Reads in the performance file, returns 2 column array: 1st column is link
	 * id,2nd column is average density
	 * 
	 * @return returns 2 column array: 1st column is link id,2nd column is
	 *         average density
	 */
	public double[][] getLinkAndDensity() {

		ArrayList<double[]> valueArrayList = new ArrayList<double[]>();

        for (Link link : links) {
            if (link.getLinkAverageDensity() != 0) {
                double[] tempValue = new double[2];
                tempValue[0] = link.getLink();
                tempValue[1] = link.getLinkAverageDensity();
                valueArrayList.add(tempValue);
            }
        }

		double[][] value = new double[valueArrayList.size()][];
		for (int i = 0; i < value.length; i++) {
			value[i] = valueArrayList.get(i);
		}

		return value;
	}

	/**
	 * Reads in the performance file, returns 2 column array: 1st column is link
	 * id,2nd column is average speed
	 * 
	 * @return returns 2 column array: 1st column is link id,2nd column is
	 *         average speed
	 */
	public double[][] getAvgSpeed() {

		ArrayList<double[]> valueArrayList = new ArrayList<double[]>();

        for (Link link : links) {
            if (link.getLinkAverageSpeed() != 0) {
                double[] tempValue = new double[2];
                tempValue[0] = link.getLink();
                tempValue[1] = link.getLinkAverageSpeed();
                valueArrayList.add(tempValue);
            }
        }

		double[][] value = new double[valueArrayList.size()][];
		for (int i = 0; i < value.length; i++) {
			value[i] = valueArrayList.get(i);
		}

		return value;
	}

	/**
	 * Matchs the average speed with link lists, returns an array: average speed
	 * for each travel zone, sort by travel zone sequence
	 * 
	 * @return an array: average speed for each travel zone, sort by travel zone
	 *         sequence
	 */
	public double[] getTravelZoneAvgSpeed() {

		double[] value = new double[HardcodedData.travelZones.length];
		double[] linksNumberInTravelZone = new double[HardcodedData.travelZones.length];
		double[][] result = getAvgSpeed();
        for (double[] aResult : result) {
            // logger.debug(result[i][0]);
            ArrayList<Integer> travelZone = tranvelZoneRoadMap
                    .get((int) aResult[0]);
            if (travelZone != null) {
                for (Integer travelID : travelZone) {
                    linksNumberInTravelZone[travelID]++;
                    value[travelID] += aResult[1];
                }
            }

        }

		for (int i = 0; i < value.length; i++) {
			if (linksNumberInTravelZone[i] == 0) {
				value[i] = 0;
			} else {
				value[i] /= linksNumberInTravelZone[i];
			}

		}
		return value;
	}

	/**
	 * Reads in the event file, returns 2 column array: 1st column is link
	 * id,2nd column is average travel time
	 * 
	 * @return returns 2 column array: 1st column is link id,2nd column is
	 *         average travel time
	 */
	public double[][] getAveTravelTime() {
		ArrayList<double[]> valueArrayList = new ArrayList<double[]>();

        for (Link link : links) {
            if (link.getLinkAverageActualTime() != 0) {
                double[] tempValue = new double[2];
                tempValue[0] = link.getLink();
                logger.trace(tempValue[0]);
                tempValue[1] = link.getLinkAverageActualTime();
                logger.trace(link.getLink() + " ");
                valueArrayList.add(tempValue);

            }
        }

		double[][] value = new double[valueArrayList.size()][];
		for (int i = 0; i < value.length; i++) {

			value[i] = valueArrayList.get(i);
		}

		return value;
	}

	public double[] getTravelZoneAveTraveTime() {
		double[] value = new double[HardcodedData.travelZones.length];
		double[] linksNumberInTravelZone = new double[HardcodedData.travelZones.length];
		double[][] result = getAveTravelTime();
        for (double[] aResult : result) {

            ArrayList<Integer> travelZone = tranvelZoneRoadMap
                    .get((int) aResult[0]);
            if (travelZone != null) {
                for (Integer travelID : travelZone) {
                    linksNumberInTravelZone[travelID]++;
                    value[travelID] += aResult[1];
                }
            }

        }

		for (int i = 0; i < value.length; i++) {
			if (linksNumberInTravelZone[i] == 0) {
				value[i] = 0;
			} else {
				value[i] /= linksNumberInTravelZone[i];
			}
		}
		return value;
	}
}
