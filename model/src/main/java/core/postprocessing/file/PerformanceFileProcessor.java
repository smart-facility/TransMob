package core.postprocessing.file;

import core.ApplicationContextHolder;
import core.EnvironmentConfig;
import hibernate.postgis.StreetsGMARandwickAndSurroundingDAO;
import hibernate.postgis.StreetsGMARandwickAndSurroundingEntity;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Maps;

import core.postprocessing.database.LinkComparator;

/**
 * A class reads in the performance file and calculate travel zone congestion in
 * 24 hours.
 * 
 * @author qun
 * 
 */
public final class PerformanceFileProcessor {

	private String performanceFilePath;
	private Map<String, ArrayList<Double>> linkSpeedMap;
	private final Map<String, ArrayList<Double>> linkDensityMap;
	private LinkInTravelZone linkInTravelZone;
	private Map<String, ArrayList<Double>> travelZoneCongestionMap;
    private static final Logger logger = Logger
			.getLogger(PerformanceFileProcessor.class);
	private static PerformanceFileProcessor performanceFileProcessor;

    private final StreetsGMARandwickAndSurroundingDAO streetsGMARandwickAndSurroundingDAO;

	/**
	 * Contract function
	 * 
	 * @param performanceFilePath
	 *            the path for performance file
	 * @param linkListFilePath
	 *            the path for link list file
	 */
	private PerformanceFileProcessor(String performanceFilePath,
			String linkListFilePath) {
		super();
		this.performanceFilePath = performanceFilePath;
		linkSpeedMap = Maps.newHashMap();
		linkDensityMap = Maps.newHashMap();
		linkInTravelZone = LinkInTravelZone.getInstance(linkListFilePath);
		travelZoneCongestionMap = Maps.newHashMap();
        streetsGMARandwickAndSurroundingDAO = ApplicationContextHolder.getBean(StreetsGMARandwickAndSurroundingDAO.class);

		// readFile();
	}

	public static synchronized PerformanceFileProcessor getInstance(
			String performanceFilePath, String linkListFilePath) {

		if (performanceFileProcessor == null) {
			performanceFileProcessor = new PerformanceFileProcessor(
					performanceFilePath, linkListFilePath);
		}

		return performanceFileProcessor;
	}

	/**
	 * reads in performance file from output of Transims. It chiefly store speed
	 * and density information.
	 */
	public void readFile() {
		final int idColumn = 0, totalPerformanceColumn = 15, // totalEventsColumn
		// = 10, //
		// common
		startTimeColumn = 2, speedColumn = 5, densityColumn = 9, directionColumn = 1;

		String[] nextline;
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new BufferedReader(new FileReader(new File(
					performanceFilePath))), '\t');

			nextline = csvReader.readNext();
			nextline = csvReader.readNext();
			int id, hour, direction;
			double speed, density;

			while ((nextline = csvReader.readNext()) != null) {
				if ((nextline.length == totalPerformanceColumn)
						&& nextline[startTimeColumn].contains(":")) {

					hour = Integer.parseInt(nextline[startTimeColumn]
							.split(":")[0]);

					hour += 4;// postponded 4 hours

					if (hour >= 24) {
						hour -= 24;
					}// if over 24, set it at the early morning

					if (hour >= 0 && hour < 24) {
						id = Integer.parseInt(nextline[idColumn]);

						direction = Integer.parseInt(nextline[directionColumn]);

						speed = Double.parseDouble(nextline[speedColumn]);
						// * milesToKm;
						density = Double.parseDouble(nextline[densityColumn]);
						String key = String.valueOf(id) + "@" + hour + "@" + direction;
						initialLinkSpeed(speed, key);
						initialLinkDensity(density, key);
					}
				}
			}

		} catch (FileNotFoundException e) {
            logger.error("Exception caught", e);
        } catch (IOException e) {
            logger.error("Exception caught", e);
		} finally {
			try {
				if (csvReader != null) {
                    csvReader.close();
                }
			} catch (IOException e) {
                logger.error("Exception caught", e);
			}
		}
	}

	private void initialLinkSpeed(double speed, String key) {
		ArrayList<Double> storedRecord = linkSpeedMap.get(key);
		if (storedRecord == null) {
			ArrayList<Double> tempRecord = new ArrayList<Double>();
			tempRecord.add(speed);
			linkSpeedMap.put(key, tempRecord);
		} else {
			storedRecord.add(speed);
			linkSpeedMap.put(key, storedRecord);
		}
	}

	private void initialLinkDensity(double congestion, String key) {
		ArrayList<Double> storedRecord = linkDensityMap.get(key);
		if (storedRecord == null) {
			ArrayList<Double> tempRecord = new ArrayList<Double>();
			tempRecord.add(congestion);
			linkDensityMap.put(key, tempRecord);
		} else {
			storedRecord.add(congestion);
			linkDensityMap.put(key, storedRecord);
		}
	}

	/**
	 * calculates average link speed
	 * 
	 * @return a Map contains average link speed each hour <br>
	 *         key: String, link id + "@" +hour+"@" +direction(0 or 1), like
	 *         "1@6@1" <br>
	 *         value: Double, speed
	 */
	public Map<String, Double> getLinkAverageSpeed() {
		// int size = linkSpeedMap.size();
		Map<String, Double> averageLinkSpeed = new HashMap<String, Double>();
		// int i = 0;
		String key;

		for (Map.Entry<String, ArrayList<Double>> entry : linkSpeedMap
				.entrySet()) {

			key = entry.getKey();

			double averageSpeed = 0;
			for (Double speed : entry.getValue()) {
				averageSpeed += speed;
			}

			averageSpeed /= entry.getValue().size();

			averageLinkSpeed.put(key, averageSpeed);

			// i++;
		}

		return averageLinkSpeed;
	}

	/**
	 * calculates average link density
	 * 
	 * @return a Map contains average link density each hour <br>
	 *         key: String, link id + "@" +hour+"@" +direction(0 or 1), like
	 *         "1@6@1" <br>
	 *         value: Double,density
	 */
	public Map<String, Double> getAverageLinkDensity() {
		// int size = linkSpeedMap.size();
		Map<String, Double> averageLinkDensity = new HashMap<String, Double>();
		// int i = 0;
		String key;

		for (Map.Entry<String, ArrayList<Double>> entry : linkDensityMap
				.entrySet()) {

			key = entry.getKey();

			double averageDensity = 0;
			for (Double density : entry.getValue()) {
				averageDensity += density;
			}

			averageDensity /= entry.getValue().size();

			averageLinkDensity.put(key, averageDensity);

			// i++;
		}

		return averageLinkDensity;
	}

	/**
	 * Transforms average speed to average congestion by the formula: 1-average
	 * speed of a link/speed limit of this link. the higher value indicate high
	 * congestion
	 * 
	 * @param averageLinkSpeed
	 *            Map contains average link speed each hour <br>
	 *            key: String, link id + "@" +hour+"@" +direction(0 or 1),
	 *            like"1@6@1" <br>
	 *            value: Double, speed
	 * @return a Map contains average link congestion each hour <br>
	 *         key: String, link id + "@" +hour+"@" +direction(0 or 1),
	 *         like"1@6@1" <br>
	 *         value: Double, congestion
	 */
	public Map<String, Double> getLinkCongestion(
			Map<String, Double> averageLinkSpeed) {

		Map<String, Double> averageLinkCongestion = new HashMap<String, Double>();
		int id;
		double congestion;

		for (Entry<String, Double> entry : averageLinkSpeed.entrySet()) {
			id = Integer.parseInt(entry.getKey().split("@")[0]);

			// logger.debug(id);
			double speedLimit = 60; // default
            StreetsGMARandwickAndSurroundingEntity entity = streetsGMARandwickAndSurroundingDAO.findById(id);
			if (entity != null) {
				speedLimit = entity.getSpeedKmh();
			}

			congestion = 1 - entry.getValue() / speedLimit;

			averageLinkCongestion.put(entry.getKey(), congestion);
		}

		return averageLinkCongestion;

	}

	/**
	 * Stores the congestion and density of links in files
	 * 
	 * @param averageLinkCongestion
	 *            the average link congestion
	 * @param averageLinkDensity
	 *            the average link density
	 * @param year
	 *            the selected year
	 */
	public void storeLink(Map<String, Double> averageLinkCongestion,
			Map<String, Double> averageLinkDensity, int year) {

        EnvironmentConfig config = ApplicationContextHolder.getBean(EnvironmentConfig.class);
		String linkFileName = config.getDataPath() + "/links" + year + ".csv";
		File file = new File(linkFileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
                logger.error("Exception caught", e);
			}
		}

		TreeMap<String, Double> congestionTreeMap = new TreeMap<String, Double>(
				new LinkComparator());
		congestionTreeMap.putAll(averageLinkCongestion);

		TreeMap<String, Double> densityTreeMap = new TreeMap<String, Double>(
				new LinkComparator());
		densityTreeMap.putAll(averageLinkDensity);

		CSVWriter csvWriter = null;
		String id, hour, direction;
		try {
			csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(file)));

			String[] titles = {"link id", "hour", "direction", "congestion", "density"};
			csvWriter.writeNext(titles);

			Set<Entry<String, Double>> congestionEntrySet = congestionTreeMap
					.entrySet();
			Set<Entry<String, Double>> densityEntrySet = densityTreeMap
					.entrySet();

			Iterator<Entry<String, Double>> congestionIterator = congestionEntrySet
					.iterator();
			Iterator<Entry<String, Double>> densityIterator = densityEntrySet
					.iterator();

			for (int i = 0; i < congestionEntrySet.size(); i++) {
				Entry<String, Double> congestionEntry = congestionIterator
						.next();
				Entry<String, Double> densityEntry = densityIterator.next();

                String [] congestionSplit = congestionEntry.getKey().split("@");

				id = congestionSplit[0];
				hour = congestionSplit[1];
				direction = congestionSplit[2];

                String [] entries = Arrays.asList(id, hour, direction, congestionEntry.getValue().toString(),
                        Long.toString(Math.round(densityEntry.getValue()))).toArray(new String[5]);
				csvWriter.writeNext(entries);
			}

		} catch (FileNotFoundException e) {
            logger.error("Exception caught", e);
		} catch (IOException e) {
            logger.error("Exception caught", e);
		} finally {
			try {
				if (csvWriter != null) {
                    csvWriter.close();
                }
			} catch (IOException e) {
                logger.error("Exception caught", e);
			}
		}

	}

	/**
	 * calculates the congestion by each travel zone by average links' average
	 * speed in a travel zone
	 * 
	 * @param averageLinkCongestion
	 *            Map contains average link congestion each hour <br>
	 *            key: String, link id + "@" +hour+"@" +direction(0 or 1),
	 *            like"1@6@1" <br>
	 *            value: Double, congestion
	 * @return a Map contains travel zone congestion each hour <br>
	 *         key: String, travel zone id + "@" +hour, like "270@6" <br>
	 *         value: Double, congestion
	 */
	public Map<String, Double> getTravelZoneCongestion(
			Map<String, Double> averageLinkCongestion) {

		Map<String, Double> travelZoneCogestion = new HashMap<String, Double>();

		int linkId;
		int hour;
		int direction;
		double congestion;
		// int i = 0;

		for (Entry<String, Double> entry : averageLinkCongestion.entrySet()) {
			linkId = Integer.parseInt(entry.getKey().split("@")[0]);
			hour = Integer.parseInt(entry.getKey().split("@")[1]);
			direction = Integer.parseInt(entry.getKey().split("@")[2]);
			congestion = entry.getValue();
			ArrayList<Integer> travelZoneId = linkInTravelZone
					.getLinkInTravelZoneMap().get(linkId);
			if (travelZoneId != null) {
				for (Integer travelZone : travelZoneId) {
                    ArrayList<Double> storedCongestion = travelZoneCongestionMap.get(String.valueOf(travelZone)
							+ "@" + hour + "@" + direction);
					if (storedCongestion != null) {
						storedCongestion.add(congestion);
						travelZoneCongestionMap.put(String.valueOf(travelZone) + "@" + hour
								+ "@" + direction, storedCongestion);
						logger.trace(travelZoneCongestionMap.get(travelZone.toString() + "@" + hour));
					} else {
						ArrayList<Double> temp = new ArrayList<>();
						temp.add(congestion);
						travelZoneCongestionMap.put(String.valueOf(travelZone) + "@" + hour
								+ "@" + direction, temp);
					}
				}
			}

		}

		for (Entry<String, ArrayList<Double>> entry : travelZoneCongestionMap
				.entrySet()) {
			double averageCongestion = 0;

			// travelZoneId = entry.getKey();
			// result[i][0] = travelZoneId;

			for (Double d : entry.getValue()) {
				averageCongestion += d;
			}

			averageCongestion /= entry.getValue().size();

			// result[i][1] = averageCongestion;

			travelZoneCogestion.put(entry.getKey(), averageCongestion);

			// i++;
		}

		return travelZoneCogestion;
	}

	/**
	 * calculate travel zone congestion for each travel zone
	 * 
	 * @param averageLinkCongestion
	 *            the average link congestion by hour
	 * @return travel zone congestion
	 * @author qun
	 */
	public Map<Integer, Double> calculateTravelZoneCongestion(
			Map<String, Double> averageLinkCongestion) {
		Map<Integer, Double> travelZoneCogestion = new HashMap<Integer, Double>();
		Map<Integer, ArrayList<Double>> travelZoneCogestionList = new HashMap<Integer, ArrayList<Double>>();
		int linkId;
		double congestion;
		for (Entry<String, Double> entry : averageLinkCongestion.entrySet()) {
			linkId = Integer.parseInt(entry.getKey().split("@")[0]);
			congestion = entry.getValue();
			ArrayList<Integer> travelZoneId = linkInTravelZone
					.getLinkInTravelZoneMap().get(linkId);
			if (travelZoneId != null) {
				for (Integer integer : travelZoneId) {
					ArrayList<Double> storedCongestion = travelZoneCogestionList.get(integer);
					if (storedCongestion != null) {
						storedCongestion.add(congestion);
						travelZoneCogestionList.put(integer, storedCongestion);
					} else {
						ArrayList<Double> temp = new ArrayList<Double>();
						temp.add(congestion);
						travelZoneCogestionList.put(integer, temp);
					}
				}
			}
		}

		for (Entry<Integer, ArrayList<Double>> entry : travelZoneCogestionList
				.entrySet()) {
			double averageCongestion = 0;

			// travelZoneId = entry.getKey();
			// result[i][0] = travelZoneId;

			for (Double d : entry.getValue()) {
				averageCongestion += d;
			}

			averageCongestion /= entry.getValue().size();

			// result[i][1] = averageCongestion;

			travelZoneCogestion.put(entry.getKey(), averageCongestion);

			// i++;
		}

		return travelZoneCogestion;
	}

	/**
	 * Calculate travel zone congestion each hour by calling other functions
	 * 
	 * @return a Map contains travel zone congestion each hour <br>
	 *         key: String, travel zone id + "@" +hour, like "270@6" <br>
	 *         value: Double, congestion
	 */
	public Map<String, Double> calculateTravelZoneCongestionByHour() {
		Map<String, Double> congestionResult = getLinkAverageSpeed();
		congestionResult = getLinkCongestion(congestionResult);

		// Map<String, Double> densityResult = getAverageLinkDensity();

		// storeLink(congestionResult, densityResult, year);
		congestionResult = getTravelZoneCongestion(congestionResult);
		return congestionResult;
	}


	@Override
	public Object clone() throws CloneNotSupportedException

	{

		throw new CloneNotSupportedException();

	}
}
