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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for TravelZonesRandwickGSDAO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TravelZonesRandwickGSDAOTest {

    @Autowired
    TravelZonesRandwickGSDAO dao;

    @Test
    public void testFindById() throws Exception {

        TravelZonesRandwickGSEntity entity = dao.findById(5);

        assertEquals("Unexpected value for tz06", 279, (int) entity.getTz06());
        assertEquals("Unexpected value for area in sqm", new BigDecimal(194189.489060).doubleValue(), entity.getAreaSqm().doubleValue(), 0);
        assertEquals("Unexpected tz006_name", "Hpm Industries", entity.getTz2006Nam());

    }


    @Test
    public void testFindByTz06() throws Exception {
        TravelZonesRandwickGSEntity entity = dao.findByTz06(279).get(0);

        assertEquals("Unexpected value for gid", 5, (int) entity.getGid());
        assertEquals("Unexpected value for area in sqm", new BigDecimal(194189.489060).doubleValue(), entity.getAreaSqm().doubleValue(), 0);
        assertEquals("Unexpected tz006_name", "Hpm Industries", entity.getTz2006Nam());

    }

    @Test
    public void testFindAll() throws Exception {
        List<TravelZonesRandwickGSEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities returned", 64, entities.size());

        // Test one from the list
        TravelZonesRandwickGSEntity entity = entities.get(4);

        assertEquals("Unexpected value for tz06", 279, (int) entity.getTz06());
        assertEquals("Unexpected value for area in sqm", new BigDecimal(194189.489060).doubleValue(), entity.getAreaSqm().doubleValue(), 0);

    }

}
