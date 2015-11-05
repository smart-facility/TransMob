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

import static org.junit.Assert.assertEquals;

import java.util.Map;

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
public class PerformanceFileProcessorTest {

	private PerformanceFileProcessor performanceFileProcessor;

    @Before
	public void setUp() throws Exception {
		HardcodedData.setPerformanceFileLocation("weekdays/");
		HardcodedData.prepend();
		String csvFilename = HardcodedData.linkList + "/Link_list.csv";
		performanceFileProcessor = PerformanceFileProcessor.getInstance(
				HardcodedData.getPerformanceFileLocation(), csvFilename);
		performanceFileProcessor.readFile();
	}

	@After
	public void tearDown() throws Exception {
		performanceFileProcessor = null;
	}


    // Test cases can't run until test copy of performance file is in SVN
	@Test @Ignore
	public void testGetAverageLinkSpeed() {
		Map<String, Double> result = performanceFileProcessor
				.getLinkAverageSpeed();

		System.out.println(result.toString());

		String string1 = "986@18";
		String string2 = "424@6";

		assertEquals(32.8, result.get(string1), 0.01);
		assertEquals(35.9375, result.get(string2), 0.01);
		// double expect = 0;
		// for (double[] ds : result) {
		// if (ds[0] == 54) {
		// expect = ds[1];
		// }
		// }
		// assertEquals(61.65, expect, 0);
	}

    @Test @Ignore
	public void testGetLinkCongestion() {
		Map<String, Double> result = performanceFileProcessor
				.getLinkAverageSpeed();
		//
		// double expect = 0;
		// for (double[] ds : result) {
		// if (ds[0] == 54) {
		// expect = ds[1];
		// }
		// }
		// assertEquals(61.65, expect, 0);

		result = performanceFileProcessor.getLinkCongestion(result);

		String string1 = "986@18";
		String string2 = "424@6";

		assertEquals(1 - 33.1 / 50, result.get(string1), 0.01);
		assertEquals(1 - 35.5125 / 60, result.get(string2), 0.01);
	}


    @Test @Ignore
	public void testGetTravelZoneCongestion() {
		Map<String, Double> result = performanceFileProcessor
				.getLinkAverageSpeed();
		result = performanceFileProcessor.getLinkCongestion(result);
		result = performanceFileProcessor.getTravelZoneCongestion(result);

		String string1 = "270@6";

		double _61 = 1 - 51.3 / 60;
		double _67 = 1 - 54.733 / 60;
		double _1000 = 1 - 32.15 / 60;
		double _1871 = 1 - 23.067 / 50;
		double _1873 = 1 - 45.0 / 60;
		double _6784 = 1 - 70.25 / 80;
		double _8429 = 1 - 42.0 / 60;

		// System.out.println(_61 + " " + _67 + " " + _1000 + " " + _1871 + " "
		// + _1873 + " " + _6784 + " " + _8429);

		assertEquals((_61 + _67 + _1000 + _1871 + _1873 + _6784 + _8429) / 7,
				result.get(string1), 0.01);

		String string2 = "270@18";

		double _56 = 1 - 63.0 / 50;
		double _54 = 1 - 63.0 / 50;
		_61 = 1 - 51.0 / 60;
		double _64 = 1 - 59.4 / 50;
		_67 = 1 - 55.1625 / 60;
		double _995 = 1 - 90.8 / 50;
		_1000 = 1 - 33.0 / 60;
		double _1001 = 1 - 54.0 / 60;
		_1871 = 1 - 23.15 / 50;
		_1873 = 1 - 45.1125 / 60;
		_6784 = 1 - 92.383 / 80;
		_8429 = 1 - 36.15 / 60;

		// System.out.println(_61 + " " + _67 + " " + _1000 + " " + _1871 + " "
		// + _1873 + " " + _6784 + " " + _8429);

		assertEquals((_54 + _56 + _61 + _64 + _67 + _995 + _1000 + _1001
				+ _1871 + _1873 + _6784 + _8429) / 12, result.get(string2),
				0.01);
	}

    @Test @Ignore
	public void testStoreLinkCongestion() {

		Map<String, Double> congestionResult = performanceFileProcessor
				.getLinkAverageSpeed();
		congestionResult = performanceFileProcessor
				.getLinkCongestion(congestionResult);

		Map<String, Double> densityResult = performanceFileProcessor
				.getAverageLinkDensity();

		performanceFileProcessor.storeLink(congestionResult, densityResult,
				2006);
	}

    @Test @Ignore
	public void testCalculateTravelZoneCongestionByHour() {
		performanceFileProcessor.calculateTravelZoneCongestionByHour();
	}

    @Test @Ignore
	public void testCalculateTravelZoneCongestion() {

		performanceFileProcessor.readFile();
		Map<String, Double> averageLinkSpeed = performanceFileProcessor
				.getLinkAverageSpeed();
		Map<String, Double> linkCongestion = performanceFileProcessor
				.getLinkCongestion(averageLinkSpeed);
		Map<Integer, Double> travelZoneCongestion = performanceFileProcessor
				.calculateTravelZoneCongestion(linkCongestion);
		Map<String, Double> travelZoneCongestionByHour = performanceFileProcessor
				.getTravelZoneCongestion(linkCongestion);
	}
}
