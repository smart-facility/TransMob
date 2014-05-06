package core.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @author qun
 * 
 */
@Deprecated
public class BusRoute {
	private String filePath;
	private final HashMap<String, Integer> linkHashMap;
	@SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(BusRoute.class);

	public BusRoute() {
		linkHashMap = new HashMap<>();
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void readLinkFile(String linkFilePath) {
        String [] line;

        CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new BufferedReader(new FileReader(linkFilePath)), '\t');
            // Skip header

            csvReader.readNext();
			while ((line = csvReader.readNext()) != null) {

				linkHashMap.put(line[2] + line[3], Integer.parseInt(line[0]));
				// logger.debug(splitStrings[0]+": "+splitStrings[2]+" - "+splitStrings[3]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
            logger.error("Failed to read link file: " + linkFilePath, e);
		} finally {
            try {
                if (csvReader != null) {
                    csvReader.close();
                }
            } catch (IOException e) {
                logger.error("Failed to close file: " + linkFilePath, e);
            }
        }
	}

	public ArrayList<Integer> readBusRoute(String busRouteName) {
		File file = new File(filePath + "bus_routes" + busRouteName
				+ "_shp_nodes.txt");
		ArrayList<Integer> nodesArrayList = new ArrayList<Integer>();

		int previousID = 0, currentID = 0;
        BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (currentID != 0) {
					previousID = currentID;
				}
				currentID = Integer.parseInt(line);
				if (previousID != 0) {
					nodesArrayList.add(getLinkID(previousID, currentID));
					logger.info(previousID + "-->" + currentID);
				}
			}

		} catch (IOException e) {
            logger.error("Failed to read file: " + file.getAbsolutePath(), e);
		} finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after reading: " + file.getAbsolutePath(), e);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            for (Integer integer : nodesArrayList) {
                builder.append(integer).append(',');
            }
            logger.debug(builder.toString());
        }

		return nodesArrayList;
	}

	public int getLinkID(int nodeID1, int nodeID2) {
		String nodes = String.valueOf(nodeID1) + String.valueOf(nodeID2);
		if (linkHashMap.get(nodes) == null) {
			nodes = String.valueOf(nodeID2) + String.valueOf(nodeID1);
		}
		return linkHashMap.get(nodes);
	}
}
