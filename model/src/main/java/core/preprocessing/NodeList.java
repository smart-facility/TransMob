package core.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A class stored the node list for mapping
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * 
 */
public class NodeList {

	private static NodeList nodeList = null;
    private static final Logger logger = Logger.getLogger(NodeList.class);

    /**
     * name="nodeList"
     *
     * @uml.associationEnd qualifier=
     *                     "coordinate:com.vividsolutions.jts.geom.Coordinate java.lang.Integer"
     */
    private final HashMap<Coordinate, Integer> nodeHashMapCoordinate;
    private final HashMap<Integer, Coordinate> nodeHashMapID;

	private NodeList(String nodeFileLocation) {
		nodeHashMapCoordinate = new HashMap<>();
		nodeHashMapID = new HashMap<>();
		readFile(nodeFileLocation);
	}

	public static NodeList getInstance(String nodeFileLocation) {
		if (nodeList == null) {
			nodeList = new NodeList(nodeFileLocation);
		}
		return nodeList;
	}

	private void readFile(String nodeFileLocation) {
		try {
			CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(new File(nodeFileLocation))), '\t');

            csvReader.readNext();// skip the first line

			String [] lineString;
			while ((lineString = csvReader.readNext()) != null) {
				Coordinate coordinate = new Coordinate(
						Double.parseDouble(lineString[1]),
						Double.parseDouble(lineString[2]));
				int id = Integer.parseInt(lineString[0]);
				nodeHashMapCoordinate.put(coordinate, id);
				nodeHashMapID.put(id, coordinate);
			}

            csvReader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		}
	}

	/**
	 * Gets the node id for a node by its coordinate
	 * 
	 * @param coordinate
	 *            the coordinate of a node need find it's id
	 * @return the id of searched node or -1 if can't find it in the node list
	 */
	public int getNodeID(Coordinate coordinate) {
		if (nodeHashMapCoordinate.containsKey(coordinate)) {
			return nodeHashMapCoordinate.get(coordinate);
		} else {
			logger.debug("Can't find this coodinate in the node list: "
					+ coordinate.toString());
			return -1;
		}

	}

	/**
	 * get node id from a geometry
	 * 
	 * @param geometry
	 *            the selected geometry
	 * @return a int array contains start and end node id
	 */
	public int[] getNodeID(Geometry geometry) {
		Coordinate[] coordinates = geometry.getCoordinates();

		int[] ids = new int[2];

		Coordinate startCoordinate = coordinates[0];
		startCoordinate.x = roundOneDecimals(startCoordinate.x);
		startCoordinate.y = roundOneDecimals(startCoordinate.y);

		Coordinate endCoordinate = coordinates[coordinates.length - 1];
		endCoordinate.x = roundOneDecimals(endCoordinate.x);
		endCoordinate.y = roundOneDecimals(endCoordinate.y);

		logger.debug("startCoordinate: " + startCoordinate);
		logger.debug("endCoordinate: " + endCoordinate);

		ids[0] = getNodeID(startCoordinate);
		ids[1] = getNodeID(endCoordinate);

		logger.debug("startCoordinate Node ID:" + ids[0]);
		logger.debug("endCoordinate Node ID:" + ids[1]);

		return ids;
	}

	/**
	 * Gets the number of nodes in the node list
	 * 
	 * @return the number of nodes in the node list
	 */
	public int getNodeListSize() {
		return nodeHashMapCoordinate.size();
	}

	private double roundOneDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.#");
		return Double.valueOf(twoDForm.format(d));
	}

}
