package core.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.HardcodedData;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ROperationInJavaTest {

	/**
	 *  name="rOperationInJava"
	 * @uml.associationEnd
	 */
	private ROperationInJava rOperationInJava;
	/**
	 *  name="logger"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private static final Logger logger = Logger.getLogger(ROperationInJavaTest.class);
    /**
	 *  name="events558"
	 * @uml.associationEnd
	 */
	private final Integer[] events558 = { 478, 479, 480, 481, 482, 483, 484, 485,
			486, 487, 525, 526, 618, 619, 718, 719, 742, 744, 745, 769, 770,
			771, 785, 786, 787, 788, 789, 790, 791, 792, 793, 794, 795, 803,
			810, 811, 812, 831, 844, 868, 869, 888, 889, 1028, 1029, 1036,
			1037, 1290, 1415, 1418, 1495, 1534, 1538, 1539, 1712, 1713, 2044,
			2064 };
	/**
	 *  name="events530"
	 * @uml.associationEnd
	 */
	private final Integer[] events530 = { 2, 8, 26, 30, 40, 41, 43, 44, 45, 48, 58,
			61, 62, 63, 64, 75, 222, 336, 337, 338, 339, 340, 341, 342, 343,
			344, 345, 346, 347, 348, 349, 350, 365, 366, 369, 370, 372, 400,
			401, 402, 403, 412, 524, 710, 711, 864, 935, 946, 947, 989, 990,
			991, 1327, 1328, 1329, 1483, 1484, 1485, 1624, 1747, 1748, 1779,
			1780, 1781, 1782, 1783, 1800, 1801, 1819, 1820, 1821, 1822, 1823,
			1834, 1888, 1891, 1892, 1893, 1894, 1895, 1896, 1897, 1914, 1946,
			1947, 1948, 1949, 1950, 1951, 1952, 1991 };

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws FileNotFoundException {
		HardcodedData.setPerformanceFileLocation("weekdays/");
		HardcodedData.setEventsFileLocation("weekdays/");
		HardcodedData.prepend();
		rOperationInJava = new ROperationInJava();

		rOperationInJava.generateLink("weekdays/");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() {
		rOperationInJava = null;
	}

	/**
	 * Test method for
	 * {@link core.statistics.ROperationInJava#ROperationInJava()} .
	 */
    @Test @Ignore // Missing input files.
	public void testROperationInJava() {
		for (Link link : rOperationInJava.getLinks()) {
			if (link.getLink() == 2071) {
				assertEquals(176, link.getLinkAverageActualTime(), 0);
			}

			if (link.getLink() == 40) {
				assertEquals(Double.NaN, link.getLinkAverageActualTime(), 0);
			}
		}

		HashMap<Integer, ArrayList<Integer>> result = ROperationInJava.tranvelZoneRoadMap;
		assertEquals(0, result.get(2).get(0), 0);
		assertEquals(9, result.get(1400).get(0), 0);
		assertEquals(10, result.get(1400).get(1), 0);
		assertEquals(26, result.get(2064).get(0), 0);
		assertEquals(27, result.get(2064).get(1), 0);

		// int[]
		// events={2,15,24,61,68,73,90,92,121,140,167,199,211,212,233,237,240,245,259,264,276,305,328,329,334,351,361,374,381,383,397,418,425,431,447,457,481,535,543,545,554,562,579,597,651,668,676,677,679,689,690,692,693,
		// 695,696,737,741,747,759,773,774,789,825,827,831,848,881,888,893,904,913,923,934,988,1001,1017,1018,1019,1029,1034,1063,1089,1116,1126,1207,1214,1241,1256,1295,1299,1305,1316,1332,1333,1336,1341,1395,1402,1442,1463,
		// 1469,1479,1494,1526,1529,1531,1561,1563,1581,1590,1592,1627,1628,1632,1656,1661,1662,1670,1682,1720,1735,1771,1773,1797,1820,1822,1840,1849,1885,1889,1917,1933,1991,1997,2001,2024,2038,2048,2061,2067,2071};
		// for(int i=0;i<events.length;i++)
		// {
		// ArrayList<Integer>
		// travelZone=rOperationInJava.getTranvelZoneRoadMap().get(events[i]);
		// if (travelZone!=null) {
		// for (Integer travelID : travelZone) {
		// if(travelID==1)
		// {
		// System.out.print(events[i]+" ");
		// }
		// }
		// }
		// }
	}

	/**
	 * Test method for
	 * {@link core.statistics.ROperationInJava#getTravelZoneAvgSpeed()} .
	 */
	@Test @Ignore // Missing input files.
	public void testGetTravelZoneAvgSpeed() {
		double[] avgSpd = rOperationInJava.getTravelZoneAvgSpeed();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < avgSpd.length; i++) {
			// logger.debug(avgSpd[i]);
			if (avgSpd[i] > max) {
				max = avgSpd[i];
			}
			if (avgSpd[i] < min) {
				min = avgSpd[i];
			}
		}

		double sum = 0;
		int num = 0;
		List<Integer> eventsList = Arrays.asList(events558);
		for (Link link : rOperationInJava.getLinks()) {
			if (eventsList.contains(link.getLink())
					&& link.getLinkAverageSpeed() != 0) {
				num++;
				// System.out.print(link.getLink()+":"+link.getLinkAverageSpeed()+" ");
				sum += link.getLinkAverageSpeed();
			}
		}

		// for (int i = 0; i < avgSpd.length; i++) {
		// System.out.println(i+":"+(avgSpd[i]-min)/(max-min));
		// }
		assertEquals(sum / num, avgSpd[27], 0.01);

		sum = 0;
		num = 0;
		eventsList = Arrays.asList(events530);
		for (Link link : rOperationInJava.getLinks()) {
			if (eventsList.contains(link.getLink())
					&& link.getLinkAverageSpeed() != 0) {
				num++;
				// System.out.print(link.getLink()+":"+link.getLinkAverageSpeed()+" ");
				sum += link.getLinkAverageSpeed();
			}
		}

		assertEquals(sum / num, avgSpd[0], 0.01);
		// assertEquals(1/0.56141264043746,
		// (avgSpd[3]-min)/(avgSpd[0]-min),0.0001);
		assertEquals(max, avgSpd[3], 0.001);
		assertEquals(min, avgSpd[16], 0.001);
	}

	/**
	 * Test method for
	 * {@link core.statistics.ROperationInJava#getLinkAndDensity()} .
	 */
    @Test @Ignore // Missing input files.
	public void testGetLinkAndDensity() {
		double[][] result = rOperationInJava.getLinkAndDensity();
		for (int i = 0; i < result.length; i++) {
			if (result[i][0] == 1) {
				assertEquals(0.18625, result[i][1], 0.001);
			}

			assertFalse(result[i][0] == 999);
			assertFalse(result[i][0] == 1000);

			if (result[i][0] == 1006) {
				assertEquals(0.608461538, result[i][1], 0.001);
			}

		}

	}

	/**
	 * Test method for {@link core.statistics.ROperationInJava#getAvgSpeed()} .
	 */
    @Test @Ignore // Missing input files.
	public void testGetAvgSpeed() {
		double[][] result = rOperationInJava.getAvgSpeed();
		for (int i = 0; i < result.length; i++) {
			if (result[i][0] == 10) {
				assertEquals(14.291, result[i][1], 0.001);
			}

			assertFalse(result[i][0] == 999);

			if (result[i][0] == 2078) {
				assertEquals(14.9279, result[i][1], 0.001);
			}

			assertEquals(850, result.length);
		}

	}

	/**
	 * Test method for
	 * {@link core.statistics.ROperationInJava#getAveTravelTime()} .
	 */
    @Test @Ignore // Missing input files.
	public void testGetAveTravelTime() {
		double[][] result = rOperationInJava.getAveTravelTime();

		for (int i = 0; i < result.length; i++) {
			if (result[i][0] == 139) {
				assertEquals(86, result[i][1], 0);
			}

			if (result[i][0] == 649) {
				assertEquals(218.5, result[i][1], 0);
			}

			assertEquals(45, result.length);
			assertFalse(result[i][0] == 3);

			// if (result[i][0]==2061||result[i][0]==1305||result[i][0]==1299
			// ||result[i][0]==693||result[i][0]==692||result[i][0]==690
			// ||result[i][0]==689||result[i][0]==374||result[i][0]==121) {
			// sum+=result[i][1];
			// }

		}

		// System.out.println(sum/9);
	}

	/**
	 * Test method for
	 * {@link core.statistics.ROperationInJava#getTravelZoneAveTraveTime()} .
	 */
    @Test @Ignore // Missing input files.
	public void testGetTravelZoneAveTraveTime() {
		double[] avgTime = rOperationInJava.getTravelZoneAveTraveTime();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < avgTime.length; i++) {
			// logger.debug(avgSpd[i]);
			if (avgTime[i] > max) {
				max = avgTime[i];
			}
			if (avgTime[i] < min) {
				min = avgTime[i];
			}

		}

		double sum = 0;
		int num = 0;
		List<Integer> eventsList = Arrays.asList(events558);
		for (Link link : rOperationInJava.getLinks()) {
			if (eventsList.contains(link.getLink())
					&& link.getLinkAverageActualTime() != 0) {
				num++;
				// System.out.print(link.getLink()+":"+link.getLinkAverageActualTime()+" ");
				sum += link.getLinkAverageActualTime();
			}
		}

		assertEquals(sum / num, avgTime[27], 0.0001);

		sum = 0;
		num = 0;
		eventsList = Arrays.asList(events530);
		for (Link link : rOperationInJava.getLinks()) {
			if (eventsList.contains(link.getLink())
					&& link.getLinkAverageActualTime() != 0) {
				num++;
				// System.out.print(link.getLink()+":"+link.getLinkAverageActualTime()+" ");
				sum += link.getLinkAverageActualTime();
			}
		}

		assertEquals(sum / num, avgTime[0], 0.0001);

	}
}
