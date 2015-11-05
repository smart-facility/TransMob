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
