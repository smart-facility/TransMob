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
package core.postprocessing.file;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

import au.com.bytecode.opencsv.CSVParser;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Maps;

/**
 * A class contains a map stores each set of link id in the shape file for each
 * travel zone
 * 
 * @author qun
 * 
 */
public final class LinkInTravelZone {

	private String linkListFilePath;
	private Map<Integer, ArrayList<Integer>> linkInTravelZoneMap;

	private static LinkInTravelZone linkInTravelZone;

    private static final Logger logger = Logger.getLogger(LinkInTravelZone.class);

	private LinkInTravelZone(String linkListFilePath) {
		super();
		this.linkListFilePath = linkListFilePath;
		linkInTravelZoneMap = Maps.newHashMap();
		readFile();
	}

	public static synchronized LinkInTravelZone getInstance(
			String linkListFilePath) {
		if (linkInTravelZone == null) {
			linkInTravelZone = new LinkInTravelZone(linkListFilePath);
		}
		return linkInTravelZone;
	}

	/**
	 * reads a link list file contains each set of link id in the shape file for
	 * each travel zone
	 */
	private void readFile() {
		final int linkIdColumn = 0, travelZoneIdColumn = 1;

		String[] nextline;
		CSVReader csvReader = null;
		try {
            // Skip first line as it is the header.
			csvReader = new CSVReader(new BufferedReader(
					new FileReader(new File(linkListFilePath))), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

			int travelZoneId, linkId;

			while ((nextline = csvReader.readNext()) != null) {

				travelZoneId = Integer.parseInt(nextline[travelZoneIdColumn]);

				linkId = Integer.parseInt(nextline[linkIdColumn]);

				ArrayList<Integer> storedRecord = linkInTravelZoneMap.get(linkId);

				if (storedRecord == null) {
					ArrayList<Integer> tempRecord = new ArrayList<Integer>();
					tempRecord.add(travelZoneId);
					linkInTravelZoneMap.put(linkId, tempRecord);
				} else {
					storedRecord.add(travelZoneId);
					linkInTravelZoneMap.put(linkId, storedRecord);
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

	public Map<Integer, ArrayList<Integer>> getLinkInTravelZoneMap() {
		return linkInTravelZoneMap;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new CloneNotSupportedException();
	}

}
