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
package hibernate.postgis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test harness for TravelZonesFacilitiesDAO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TravelZonesFacilitiesDAOTest {

    @Autowired
    private TravelZonesFacilitiesDAO dao;

    @Test
    public void testFindById() throws Exception {

        TravelZonesFacilitiesEntity entity = dao.findById(279);

        assertEquals("Unexpected value for neighbourhood 2km", 133, entity.getNeighbourhood2km(), 0);
        assertEquals("Unexpected value for entertainment 2km", 73, entity.getEntertainment2km(), 0);
        assertEquals("Unexpected value for transport 2km", 23, entity.getTransport2km(), 0);
    }

    @Test
    public void testFindByIdFailure() throws Exception {
        TravelZonesFacilitiesEntity entity = dao.findById(999999);
        assertNull("Entity should not exist", entity);
    }

    @Test
    public void testFindAll() throws Exception {
        List<TravelZonesFacilitiesEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities returned", 64, entities.size());

        // Test one from the list
        TravelZonesFacilitiesEntity entity = entities.get(4);

        assertEquals("Unexpected value for tz06", 279, (int) entity.getTz2006());
        assertEquals("Unexpected value for neighbourhood 2km", 133, entity.getNeighbourhood2km(), 0);
        assertEquals("Unexpected value for entertainment 2km", 73, entity.getEntertainment2km(), 0);
        assertEquals("Unexpected value for transport 2km", 23, entity.getTransport2km(), 0);
    }
}
