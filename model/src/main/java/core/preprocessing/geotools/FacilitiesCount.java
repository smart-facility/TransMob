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
package core.preprocessing.geotools;

import core.ApplicationContextHolder;
import hibernate.postgis.TravelZonesFacilitiesDAO;

import java.io.*;
import java.util.*;

import hibernate.postgis.TravelZonesFacilitiesEntity;
import org.apache.log4j.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollections;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import core.DeepCopy;
import core.statistics.FacilitiesMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A utility class to count points in shape files.
 * 
 * @author qun
 * 
 */
public class FacilitiesCount {

	private static final String MY_NEIGHBOURHOOD = "My neighbourhood";
	private static final String MY_EDUCATION = "My education";
	private static final String MY_ENTERTAINMENT = "My entertainment";
	private static final String MY_SERVICES = "My services";
	private static final String MY_TRANSPORT = "My transport";
	private static final String WITHIN_THE_GEOM = "WITHIN(the_geom, ";
	private static final String MY_WORKSPACES = "My workspaces";
	
    private static Logger logger = Logger.getLogger(FacilitiesCount.class);

    private static String exceptionMessage = "Exception caught";
    
	public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContextHolder holder = ctx.getBean(ApplicationContextHolder.class);

		FacilitiesCount testGeoTools = new FacilitiesCount();
		// ============calculate facilities in travel zone.
		String travelZoneShapeFileString = "S:/1000002_dpabmrp_share/GIS_Data/Travel_zones/Travel_zones_Randwick_GS_new.shp";
		String pointShapeFile = "S:/1000002_dpabmrp/Phase 2/BTS/Facilities GIS Data/Facilities06122011.shp";
		String busShapeFile = "S:/1000002_dpabmrp/Phase 1/Bus network and timetable/Stops.shp";
		
		double desiredRadius = 500;
		testGeoTools.filterFeatures(new File(pointShapeFile), new File(
				travelZoneShapeFileString), desiredRadius);
		testGeoTools.countBusStops(new File(travelZoneShapeFileString),
				new File(busShapeFile), desiredRadius);
		// testGeoTools.dividedByPopulation();
		// =================================
		// String dwellingShapeFile =
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/random_HH_locations_with_cdcode_tzcode/random_HH_locations_with_cdcode_tzcode.shp";
		// String pointShapeFile =
		// "S:/1000002_dpabmrp/Phase 2/BTS/Facilities GIS Data/Facilities06122011.shp";
		// testGeoTools.writeCount(
		// "C:/Documents and Settings/qun/Desktop/countresults.csv",
		// testGeoTools.filterFeatures(new File(pointShapeFile), new File(
		// dwellingShapeFile), 500));

		// testGeoTools.dividedByPopulation();

		// String cdShapeFile =
		//
		// "S:/1000002_dpabmrp_share/GIS_Data/CDs/CDs_Randwick_Green_Sqaure_MGA56.shp";
		// testGeoTools.readCdShapefile(new File(cdShapeFile));
		//
		// String busShapeFile =
		// "S:/1000002_dpabmrp/Phase 1/Bus network and timetable/Stops.shp";
		// ArrayList<Integer> results=testGeoTools
		// .countBusStops(
		// testGeoTools
		// .preparePoints(
		// new File(
		// "E:/random_hh_locations_with_cdcode_tzcode.csv"),
		// 5, 6), new File(busShapeFile), 500);
		// testGeoTools.writeToCSV("e:/results.csv", results);
		// testGeoTools.countBusStops(new File(travelZoneShapeFileString),
		// new File(busShapeFile), 500).toString();
		// // testGeoTools.readPointShapefile(new File(pointShapeFile));
		//

		//
		// testGeoTools.filterFeatures(new File(pointShapeFile), 4000);

		// logger.debug(testGeoTools
		// .preparePoints(
		// new File(
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/Liveability_survey_xy_locations_section1.csv"))
		// .toString());
		//
		// testGeoTools
		// .filterFeatures(
		// testGeoTools
		// .preparePoints(
		// new File(
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/Liveability_survey_xy_locations_section1.csv"),
		// 5, 6), 1000, new File(pointShapeFile));
		// //
		// testGeoTools
		// .writeToFile("C:/Documents and Settings/qun/Desktop/500m counts.txt");
		// testGeoTools.clearRecords();
		// testGeoTools
		// .filterFeatures(
		// testGeoTools
		// .preparePoints(new File(
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/Liveability_survey_xy_locations_section1.csv")),
		// 3000, new File(pointShapeFile));
		// testGeoTools
		// .writeToFile("C:/Documents and Settings/qun/Desktop/3km counts.txt");
		// testGeoTools.clearRecords();
		// testGeoTools
		// .filterFeatures(
		// testGeoTools
		// .preparePoints(new File(
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/Liveability_survey_xy_locations_section1.csv")),
		// 4000, new File(pointShapeFile));
		//
		// testGeoTools
		// .writeToFile("C:/Documents and Settings/qun/Desktop/500m counts.txt");
	}

	public FacilitiesCount() {
		pointCollection = FeatureCollections.newCollection();
		cdCollection = FeatureCollections.newCollection();
		categoriesCount = new ArrayList<HashMap<String, Integer>>();
		categoriesCount2 = new ArrayList(HashMultiset.create());
		facilitiesMapping = new FacilitiesMapping();
        travelZonesFacilitiesDAO = ApplicationContextHolder.getBean(TravelZonesFacilitiesDAO.class);

	}

	/**
	 * Reads in a .csv file. then return a list of Point
	 * 
	 * @param pointFile
	 *            the .csv file will be read in
	 * @param xColumn
	 * @param yColumn
	 * @return a list of points
	 */
	public List<Point> preparePoints(File pointFile, int xColumn, int yColumn) {
		ArrayList<Point> points = new ArrayList<Point>();
		CSVReader reader = null;
		GeometryFactory geometryFactory = JTSFactoryFinder
				.getGeometryFactory(null);
		try {
			reader = new CSVReader(new BufferedReader(new FileReader(pointFile)));
			String[] nextLine;
			nextLine = reader.readNext();
			while ((nextLine = reader.readNext()) != null) {
				// nextLine[] is an array of values from the line

				Coordinate coord = new Coordinate(
						Double.parseDouble(nextLine[xColumn]),
						Double.parseDouble(nextLine[yColumn]));
				Point point = geometryFactory.createPoint(coord);
				points.add(point);
			}
		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		} finally {
			try {
				if (reader != null ) {
                    reader.close();
                }
			} catch (IOException e) {
                logger.error(exceptionMessage, e);
			}
		}

		return points;
	}

	/**
	 * counts number of bus stops
	 * 
	 * @param points
	 *            a list of points
	 * @param pointFile
	 *            a point shape file
	 * @param radius
	 *            radius of area
	 * @return a list of number of bus stops
	 */
	public List<Integer> countBusStops(List<Point> points, File pointFile,
			double radius) {
		List<Integer> busStopsCount = new ArrayList<Integer>();
		try {
			FileDataStore pointStore = FileDataStoreFinder
					.getDataStore(pointFile);
			Polygon bufferedPolygon;
			Filter filter;
			Query query;
			String name = "Stops";
			SimpleFeatureSource source;
			source = pointStore.getFeatureSource(name);
			SimpleFeatureCollection simpleFeatureCollection;
			// int count;
			for (Point point : points) {
				// count = 0;
				bufferedPolygon = (Polygon) point.buffer(radius);
				filter = CQL.toFilter(WITHIN_THE_GEOM + bufferedPolygon + ")");
				query = new Query(name, filter);
				simpleFeatureCollection = source.getFeatures(query);
				// simpleFeatureIterator = simpleFeatureCollection.features();
				// logger.debug(simpleFeatureCollection.size());

				// while (simpleFeatureIterator.hasNext()) {
				// count++;
				// }
				busStopsCount.add(simpleFeatureCollection.size());
			}

		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		} catch (CQLException e) {
            logger.error(exceptionMessage, e);
		}

		// for (Integer integer : busStopsCount) {
		// logger.debug(integer);
		// }

		return busStopsCount;
	}

	/**
	 * Counts bus stops in shape file
	 * 
	 * @param shapeFile
	 *            a shape file
	 * @param pointFile
	 *            selected point which will be used as centroids.
	 * @param radius
	 *            radius of a point
	 * @return an arraylist contains number of bus stops
	 */
	public List<Integer> countBusStops(File shapeFile, File pointFile,
			double radius) {
		List<Integer> busStopsCount = new ArrayList<>();

		try {
			FileDataStore pointStore = FileDataStoreFinder
					.getDataStore(pointFile);
			Polygon bufferedPolygon;
			Filter filter;
			Query query;
			String name = "Stops";
			SimpleFeatureSource source;
			source = pointStore.getFeatureSource(name);
			SimpleFeatureCollection simpleFeatureCollection;
			SimpleFeature travelZoneFeature;
			Point testPoint;

			FileDataStore store = FileDataStoreFinder.getDataStore(shapeFile);
			SimpleFeatureSource featureSource = store.getFeatureSource();

			SimpleFeatureCollection travelZonesFeatureCollection = featureSource
					.getFeatures();

			SimpleFeatureIterator travelZonesFeatureIterator = travelZonesFeatureCollection
					.features();
			// int count;

			while (travelZonesFeatureIterator.hasNext()) {
				// count = 0;

				travelZoneFeature = travelZonesFeatureIterator.next();
				testPoint = ((Geometry) travelZoneFeature.getDefaultGeometry())
						.getCentroid();

				bufferedPolygon = (Polygon) testPoint.buffer(radius);
				filter = CQL.toFilter(WITHIN_THE_GEOM + bufferedPolygon + ")");
				query = new Query(name, filter);
				simpleFeatureCollection = source.getFeatures(query);
				// simpleFeatureIterator = simpleFeatureCollection.features();
				// logger.debug(simpleFeatureCollection.size());

				// while (simpleFeatureIterator.hasNext()) {
				// count++;
				// }
				busStopsCount.add(simpleFeatureCollection.size());
			}

		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		} catch (CQLException e) {
            logger.error(exceptionMessage, e);
		}


        List<TravelZonesFacilitiesEntity> entitiesToAdd = new ArrayList<>();

		List<TravelZonesFacilitiesEntity> travelZonesFacilitiess = travelZonesFacilitiesDAO.findAll();
		for (int i = 0; i < busStopsCount.size(); i++) {
			logger.debug(busStopsCount.get(i));

            TravelZonesFacilitiesEntity travelZonesFacilities = travelZonesFacilitiess.get(i);

			// travelZonesFacilities.setTransport2km(travelZonesFacilities
			// .getTransport2km() + busStopsCount.get(i));

			// travelZonesFacilities.setTransport1km(travelZonesFacilities
			// .getTransport1km() + busStopsCount.get(i));

			travelZonesFacilities.setTransport500m(travelZonesFacilities
					.getTransport500m() + busStopsCount.get(i));

            entitiesToAdd.add(travelZonesFacilities);

			travelZonesFacilitiesDAO.attachDirty(travelZonesFacilities);

		}
        travelZonesFacilitiesDAO.updateAll(entitiesToAdd);

		for (int busStops : busStopsCount) {
			logger.debug(busStops);
		}

		return busStopsCount;
	}

	/**
	 * Applys points as centroids to buffer a circle with selected radius, then
	 * counts the points within each circles in the shape file
	 * 
	 * @param points
	 *            a list of points
	 * @param radius
	 *            selected radius
	 * @param pointFile
	 *            a point file contains features
	 */
	public void filterFeatures(List<Point> points, double radius, File pointFile) {
		final int typeColumnNumber = 5;
		try {
			FileDataStore pointStore = FileDataStoreFinder
					.getDataStore(pointFile);
			Polygon bufferedPolygon;
			Filter filter;
			Query query;
			String name = "Facilities06122011";
			SimpleFeatureSource source;
			source = pointStore.getFeatureSource(name);
			SimpleFeatureCollection simpleFeatureCollection;
			SimpleFeatureIterator simpleFeatureIterator;
			ArrayList<String> categoryArrayList = new ArrayList<String>();
			HashMap<String, Integer> tempHashMap = new HashMap<String, Integer>();
			Multiset<String> multiset = HashMultiset.create();
			for (Point point : points) {
				bufferedPolygon = (Polygon) point.buffer(radius);
				filter = CQL.toFilter(WITHIN_THE_GEOM + bufferedPolygon + ")");
				query = new Query(name, filter);
				simpleFeatureCollection = source.getFeatures(query);
				simpleFeatureIterator = simpleFeatureCollection.features();

				while (simpleFeatureIterator.hasNext()) {
					// categoryArrayList = countEachCategories(typeColumnNumber,
					// tempHashMap, simpleFeatureIterator);
					categoryArrayList = (ArrayList<String>) countEachCategories(
							typeColumnNumber, multiset, simpleFeatureIterator);
				}
				categoriesCount.add((HashMap<String, Integer>) DeepCopy
						.copyBySerialize(tempHashMap));

				categoriesCount2.add(multiset);
				categoryArrayList.clear();
				tempHashMap.clear();
			}
		} catch (CQLException e) {
            logger.error(exceptionMessage, e);
		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		}

	}

	/**
	 * Applys points as centroids to buffer a circle with selected radius, then
	 * counts the points within each circles in the shape file
	 * 
	 * @param pointFile
	 *            a point file contains features
	 * @param shapeFile
	 *            a shape file
	 * @param radius
	 *            selected radius
	 * @return an array contains an integer array recording numbers of
	 *         facilities in different categories
	 */
	public List<int[]> filterFeatures(File pointFile, File shapeFile,
			double radius) {
		TravelZonesFacilitiesDAO travelZonesFacilitiesDAO = new TravelZonesFacilitiesDAO();

		final int typeColumnNumber = 5;
		ArrayList<int[]> countResults = new ArrayList<int[]>();

		try {
			FileDataStore pointStore = FileDataStoreFinder
					.getDataStore(pointFile);
			String string = "Facilities06122011";
			SimpleFeatureSource source = pointStore.getFeatureSource(string);

			FileDataStore store = FileDataStoreFinder.getDataStore(shapeFile);
			SimpleFeatureSource featureSource = store.getFeatureSource();

			SimpleFeatureCollection travelZonesFeatureCollection = featureSource
					.getFeatures();
			SimpleFeatureIterator travelZonesFeatureIterator = travelZonesFeatureCollection
					.features();

			SimpleFeature travelZonesFeature;
			Geometry bufferedPolygon;
			Filter filter;
			Query query;
			Geometry testPoint;
			SimpleFeatureCollection simpleFeatureCollection;
			SimpleFeatureIterator simpleFeatureIterator;
			ArrayList<String> categoryArrayList = new ArrayList<String>();
			int id;
			travelZonesFacilitiesDAO.startTransaction();

			while (travelZonesFeatureIterator.hasNext()) {
				travelZonesFeature = travelZonesFeatureIterator.next();
				bufferedPolygon = (Geometry) travelZonesFeature
						.getDefaultGeometry();

				testPoint = ((Geometry) travelZonesFeature.getDefaultGeometry())
						.getCentroid();

				// testPoint = (Geometry)
				// travelZonesFeature.getDefaultGeometry();

				bufferedPolygon = testPoint.buffer(radius);

				filter = CQL.toFilter(WITHIN_THE_GEOM + bufferedPolygon + ")");
				query = new Query(string, filter);

				simpleFeatureCollection = source.getFeatures(query);
				simpleFeatureIterator = simpleFeatureCollection.features();
				HashMap<String, Integer> tempHashMap = new HashMap<String, Integer>();
				while (simpleFeatureIterator.hasNext()) {
					categoryArrayList = (ArrayList<String>) countEachCategories(
							typeColumnNumber, tempHashMap,
							simpleFeatureIterator);

				}

				// logger.debug(id);

				id = (Integer) travelZonesFeature.getAttribute(1);

				int[] temple = {
						id,
						tempHashMap.get(MY_ENTERTAINMENT) != null ? tempHashMap
								.get(MY_ENTERTAINMENT) : 0,
						tempHashMap.get(MY_TRANSPORT) != null ? tempHashMap
								.get(MY_TRANSPORT) : 0,
						(tempHashMap.get(MY_WORKSPACES) != null ? tempHashMap
								.get(MY_WORKSPACES) : 0)
								+ (tempHashMap.get(MY_EDUCATION) != null ? tempHashMap
										.get(MY_EDUCATION) : 0),
						tempHashMap.get(MY_ENTERTAINMENT) != null ? tempHashMap
								.get(MY_ENTERTAINMENT) : 0,
						tempHashMap.get(MY_NEIGHBOURHOOD) != null ? tempHashMap
								.get(MY_NEIGHBOURHOOD) : 0,
						tempHashMap.get(MY_SERVICES) != null ? tempHashMap
								.get(MY_SERVICES) : 0 };
				updateCategoriesCount(travelZonesFacilitiesDAO, tempHashMap, id);
				categoriesCount.add(tempHashMap);
				countResults.add(temple);
				categoryArrayList.clear();
				tempHashMap.clear();
			}
			travelZonesFacilitiesDAO.endTransaction();

		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		} catch (CQLException e) {
            logger.error(exceptionMessage, e);
		}

		return countResults;
	}


	/**
	 * Counts number of facilities in each category
	 * 
	 * @param typeColumnNumber
	 *            number of types
	 * @param tempHashMap
	 * @param simpleFeatureIterator
	 * @return
	 */
	private List<String> countEachCategories(final int typeColumnNumber,
			Map<String, Integer> tempHashMap,
			SimpleFeatureIterator simpleFeatureIterator) {
		SimpleFeature pointFeature;
		ArrayList<String> categoryArrayList;
		pointFeature = simpleFeatureIterator.next();
		// resultCollection.add(pointFeature);
		categoryArrayList = facilitiesMapping.getMapping().get(
				pointFeature.getAttribute(typeColumnNumber));

		for (String category : categoryArrayList) {
			if (tempHashMap.containsKey(category)) {
				tempHashMap.put(category,
						tempHashMap.get(category) + 1);
			} else {
				tempHashMap.put(category, 1);
			}
		}
		return categoryArrayList;
	}

	/**
	 * Counts number of facilities in each category
	 * 
	 * @param typeColumnNumber
	 * @param multiset
	 * @param simpleFeatureIterator
	 * @return
	 */
	private List<String> countEachCategories(final int typeColumnNumber,
			Multiset<String> multiset,
			SimpleFeatureIterator simpleFeatureIterator) {
		SimpleFeature pointFeature;
		ArrayList<String> categoryArrayList;
		pointFeature = simpleFeatureIterator.next();
		// resultCollection.add(pointFeature);
		categoryArrayList = facilitiesMapping.getMapping().get(
				pointFeature.getAttribute(typeColumnNumber));

		multiset.addAll(categoryArrayList);

		return categoryArrayList;
	}

	// private void updateCategoriesCount(
	// CDsRandwickGreenSquareMGA56DAO cDsRandwickGreenSquareMGA56DAO,
	// HashMap<String, Integer> tempHashMap, String id) {
	// CDsRandwickGreenSquareMGA56 cDsRandwickGreenSquareMGA56;
	// cDsRandwickGreenSquareMGA56 = cDsRandwickGreenSquareMGA56DAO
	// .findById(Integer.parseInt(id));
	// cDsRandwickGreenSquareMGA56
	// .setMyEntertainment(Double.parseDouble(tempHashMap
	// .get("My entertainment") != null ? tempHashMap.get(
	// "My entertainment").toString() : "0"));
	// cDsRandwickGreenSquareMGA56.setOther(Double.parseDouble(tempHashMap
	// .get("Other") != null ? tempHashMap.get("Other").toString()
	// : "0"));
	// cDsRandwickGreenSquareMGA56.setMyHouse(Double.parseDouble(tempHashMap
	// .get("My house") != null ? tempHashMap.get("My house")
	// .toString() : "0"));
	// cDsRandwickGreenSquareMGA56
	// .setMyTransport(Double.parseDouble(tempHashMap
	// .get("My transport") != null ? tempHashMap.get(
	// "My transport").toString() : "0"));
	// cDsRandwickGreenSquareMGA56
	// .setMyWorkspaces(Double.parseDouble(tempHashMap
	// .get("My workspaces") != null ? tempHashMap.get(
	// "My workspaces").toString() : "0"));
	// cDsRandwickGreenSquareMGA56
	// .setMyEducation(Double.parseDouble(tempHashMap
	// .get("My education") != null ? tempHashMap.get(
	// "My education").toString() : "0"));
	// cDsRandwickGreenSquareMGA56
	// .setMyNeighbourhood(Double.parseDouble(tempHashMap
	// .get("My neighbourhood") != null ? tempHashMap.get(
	// "My neighbourhood").toString() : "0"));
	// cDsRandwickGreenSquareMGA56
	// .setMyServices(Double.parseDouble(tempHashMap
	// .get("My services") != null ? tempHashMap.get(
	// "My services").toString() : "0"));
	// }
	/**
	 * updates a selected travel zone facilities information
	 * 
	 * @param travelZonesFacilitiesDAO
	 *            the data access object
	 * @param tempHashMap
	 *            a hashmap for counting the number
	 * @param id
	 *            a travel zone facility id
	 */

	private void updateCategoriesCount(
			TravelZonesFacilitiesDAO travelZonesFacilitiesDAO,
			Map<String, Integer> tempHashMap, int id) {
		TravelZonesFacilitiesEntity travelZonesFacilities = travelZonesFacilitiesDAO.findById(id);
		travelZonesFacilities
				.setEntertainment500m(Double.parseDouble(tempHashMap
						.get(MY_ENTERTAINMENT) != null ? String.valueOf(tempHashMap.get(
						MY_ENTERTAINMENT)) : "0"));
		travelZonesFacilities.setTransport500m(Double.parseDouble(tempHashMap
				.get(MY_TRANSPORT) != null ? String.valueOf(tempHashMap.get(MY_TRANSPORT)) : "0"));

		travelZonesFacilities.setServices500m(Double.parseDouble(tempHashMap
				.get(MY_SERVICES) != null ? String.valueOf(tempHashMap.get(MY_SERVICES)) : "0"));

		travelZonesFacilities
				.setWorkspacesEducation500m(Double.parseDouble(tempHashMap
						.get(MY_WORKSPACES) != null ? String.valueOf(tempHashMap.get(
						MY_WORKSPACES)) : "0")
						+ Double.parseDouble(tempHashMap.get(MY_EDUCATION) != null ? String.valueOf(tempHashMap
								.get(MY_EDUCATION)) : "0"));

		travelZonesFacilities
				.setNeighbourhood500m(Double.parseDouble(tempHashMap
						.get(MY_NEIGHBOURHOOD) != null ? String.valueOf(tempHashMap.get(
						MY_NEIGHBOURHOOD)) : "0"));

	}

	/**
	 * reads in a point shape file
	 * 
	 * @param file
	 *            the selected file
	 */
	public void readPointShapefile(File file) {

		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();

			pointCollection = featureSource.getFeatures();
			// simpleFeatureIterator=simpleFeatureCollection.features();

			// while (simpleFeatureIterator.hasNext()) {
			// SimpleFeature simpleFeature= simpleFeatureIterator.next();
			// // Geometry geometry=(Geometry)
			// simpleFeature.getDefaultGeometry();
			// // logger.debug(simpleFeature.getID()+":	"+geometry);
			//
			// }
		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		}

	}

	/**
	 * reads in a cd shape file
	 * 
	 * @param file
	 *            the data source file
	 */
	public void readCdShapefile(File file) {

		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();

			cdCollection = featureSource.getFeatures();
			// simpleFeatureIterator=simpleFeatureCollection.features();

			// while (simpleFeatureIterator.hasNext()) {
			// SimpleFeature simpleFeature= simpleFeatureIterator.next();
			// // Geometry geometry=(Geometry)
			// simpleFeature.getDefaultGeometry();
			// // logger.debug(simpleFeature.getID()+":	"+geometry);
			//
			// }
		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		}

	}

	public SimpleFeatureCollection getPointCollection() {
		return pointCollection;
	}

	public void setPointCollection(SimpleFeatureCollection pointCollection) {
		this.pointCollection = pointCollection;
	}

	public SimpleFeatureCollection getCdCollection() {
		return cdCollection;
	}

	public void setCdCollection(SimpleFeatureCollection cdCollection) {
		this.cdCollection = cdCollection;
	}

	/**
	 * writes counts of category in files
	 * 
	 * @param fileName
	 *            selected output file name
	 */
	public void writeToFile(String fileName) {
        final String[] headers = {"id","My neighbourhood","My transport","My workspaces","My education","My entertainment","My services","My house"};
		CSVWriter csvWriter = null;
		try {
            Writer writer = new BufferedWriter(new FileWriter(new File(fileName)));
            csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			csvWriter.writeNext(headers);

            for (Multiset<String> aCategoriesCount2 : categoriesCount2) {
                List<String> counts = Arrays.asList(Integer.toString(aCategoriesCount2.count(MY_NEIGHBOURHOOD)),
                        Integer.toString(aCategoriesCount2.count(MY_TRANSPORT)),
                        Integer.toString(aCategoriesCount2.count(MY_WORKSPACES)),
                        Integer.toString(aCategoriesCount2.count(MY_ENTERTAINMENT)),
                        Integer.toString(aCategoriesCount2.count(MY_SERVICES)),
                        Integer.toString(aCategoriesCount2.count("My house")));
                
                String[] outputCounts = (String []) counts.toArray (new String [counts.size ()]); 
                csvWriter.writeNext(outputCounts);

            }

		} catch (IOException e) {
            logger.error(exceptionMessage, e);
		} finally {
			try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
			} catch (IOException e) {
                logger.error(exceptionMessage, e);
			}
		}
	}

	public void clearRecords() {
		categoriesCount.clear();
	}

	/**
	 * divides counts by population within 500m circle
	 */
	public void dividedByPopulation() {

		travelZonesFacilitiesDAO.startTransaction();

		List<TravelZonesFacilitiesEntity> travelZonesFacilitiess = travelZonesFacilitiesDAO.findAll();
		for (TravelZonesFacilitiesEntity travelZonesFacilities : travelZonesFacilitiess) {

			// travelZonesFacilities.setEntertainment2km(travelZonesFacilities
			// .getEntertainment2km()
			// / travelZonesFacilities.getPopulation2km());
			// travelZonesFacilities.setEntertainment3km(travelZonesFacilities
			// .getEntertainment3km()
			// / travelZonesFacilities.getPopulation3km());
			travelZonesFacilities.setEntertainment500m(travelZonesFacilities
					.getEntertainment500m()
					/ travelZonesFacilities.getPopulation500m());

			// travelZonesFacilities.setNeighbourhood2km(travelZonesFacilities
			// .getNeighbourhood2km()
			// / travelZonesFacilities.getPopulation2km());
			// travelZonesFacilities.setNeighbourhood3km(travelZonesFacilities
			// .getNeighbourhood3km()
			// / travelZonesFacilities.getPopulation3km());
			travelZonesFacilities.setNeighbourhood500m(travelZonesFacilities
					.getNeighbourhood500m()
					/ travelZonesFacilities.getPopulation500m());

			// travelZonesFacilities.setServices2km(travelZonesFacilities
			// .getServices2km()
			// / travelZonesFacilities.getPopulation2km());
			// travelZonesFacilities.setServices3km(travelZonesFacilities
			// .getServices3km()
			// / travelZonesFacilities.getPopulation3km());
			travelZonesFacilities.setServices500m(travelZonesFacilities
					.getServices500m()
					/ travelZonesFacilities.getPopulation500m());

			// travelZonesFacilities.setTransport2km(travelZonesFacilities
			// .getTransport2km()
			// / travelZonesFacilities.getPopulation2km());
			// travelZonesFacilities.setTransport3km(travelZonesFacilities
			// .getTransport3km()
			// / travelZonesFacilities.getPopulation3km());
			travelZonesFacilities.setTransport500m(travelZonesFacilities
					.getTransport500m()
					/ travelZonesFacilities.getPopulation500m());

			// travelZonesFacilities
			// .setWorkspacesEducation2km(travelZonesFacilities
			// .getWorkspacesEducation2km()
			// / travelZonesFacilities.getPopulation2km());
			// travelZonesFacilities
			// .setWorkspacesEducation3km(travelZonesFacilities
			// .getWorkspacesEducation3km()
			// / travelZonesFacilities.getPopulation3km());
			travelZonesFacilities
					.setWorkspacesEducation500m(travelZonesFacilities
							.getWorkspacesEducation500m()
							/ travelZonesFacilities.getPopulation500m());

			travelZonesFacilitiesDAO.attachDirty(travelZonesFacilities);

		}

		travelZonesFacilitiesDAO.endTransaction();
	}

	private SimpleFeatureCollection pointCollection;
	private SimpleFeatureCollection cdCollection;
	private final List<HashMap<String, Integer>> categoriesCount;
	private final List<Multiset<String>> categoriesCount2;
	private final FacilitiesMapping facilitiesMapping;
    private final TravelZonesFacilitiesDAO travelZonesFacilitiesDAO;
}
