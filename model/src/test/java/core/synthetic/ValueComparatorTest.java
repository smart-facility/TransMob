package core.synthetic;

import hibernate.postgis.TravelZonesFacilitiesDAO;
import hibernate.postgis.TravelZonesFacilitiesEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.synthetic.liveability.ValueComparator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ValueComparatorTest {

	private ValueComparator valueComparator;
	private Map<Integer, TravelZonesFacilitiesEntity> travelZonesFacilitiesMap;

    @Autowired
    private TravelZonesFacilitiesDAO travelZonesFacilitiesDAO;

	@Before
	public void setUp() throws Exception {
		travelZonesFacilitiesMap = travelZonesFacilitiesDAO.getByMap();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Map<Integer, Double> baseMap = new HashMap<Integer, Double>();

		baseMap.put(537, 0.95);

		baseMap.put(549, 0.95);

		baseMap.put(509, 0.95);

		valueComparator = new ValueComparator(baseMap, travelZonesFacilitiesMap, 1);

		TreeMap<Integer, Double> treeMap = new TreeMap<Integer, Double>(valueComparator);

		treeMap.putAll(baseMap);

		System.out.println(treeMap.toString());
	}

}
