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

import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.HardcodedData;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TripsTimeProcessorTest {

	private TripsTimeProcessor tripsTimeProcessor;
    private static final Logger logger = Logger
			.getLogger(TripsTimeProcessorTest.class);

    @Before
	public void setUp() throws Exception {
		// HardcodedData.setPerformanceFileLocation("weekdays/");
		HardcodedData.prepend();
		String csvFilename = HardcodedData.linkList + "/Link_list.csv";

		tripsTimeProcessor = new TripsTimeProcessor(csvFilename);

	}

	@After
	public void tearDown() throws Exception {
		tripsTimeProcessor = null;
	}

	@Test @Ignore
	public void testCalculateNormalizedTravelZoneAverageTripsTime() {

		// double[][] results = GeoDBReader.getWorkTripTravelTime();
		//
		// for (double[] ds : results) {
		// for (double d : ds) {
		// System.out.print(d + ",");
		// }
		// System.out.println();
		// }

		// logger.debug(GeoDBReader.getWorkTripTravelTime());

		// Map<Integer, List<Double>> results = new HashMap<Integer,
		// List<Double>>();
		//
		// for (int i = 1; i < 9000; i++) {
		//
		// List<Double> time = new ArrayList<Double>();
		// for (int j = 1; j < 4; j++) {
		// time.add((double) (i + j));
		// }
		//
		// results.put(i, time);
		//
		// }

		// HardcodedData.setTimePlanFileLocation("weekdays/");
		// TimePlanFileProcessor timePlanFileProcessor = TimePlanFileProcessor
		// .getInstance(HardcodedData.getTimePlanFileLocation());

		TimePlanFileProcessor timePlanFileProcessor = TimePlanFileProcessor
				.getInstance("C:/Transims/2006/demand50_20121004155240/3.Syd.TIMEPLANS");

		timePlanFileProcessor.readFile(HardcodedData.travelCost + "2006.csv",
				null);

		Map<Integer, Double> normalizedTravelZoneAverageTripsTime = tripsTimeProcessor
				.calculateNormalizedTravelZoneAverageTripsTime(timePlanFileProcessor
						.getLinksTripsTime());
		logger.debug(normalizedTravelZoneAverageTripsTime);
	}
}
