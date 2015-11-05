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
package hibernate.postgres;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test harness for HtsDataDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class HtsDataDAOTest {

    @Autowired
    private HtsDataDAO dao;

    @Test
    public void testFindOne() throws Exception {

        HtsDataEntity.HtsDataEntityPK key = new HtsDataEntity.HtsDataEntityPK();
        key.setHouseholdId(1);
        key.setPersonNo(1);
        key.setTripNo(1);
        HtsDataEntity entity = dao.findOne(key);

        assertEquals("Unexpected value for household ID", 1, entity.getKey().getHouseholdId().intValue());
        assertEquals("Unexpected value for person #", 1, entity.getKey().getPersonNo().intValue());
        assertEquals("Unexpected value for trip #", 1, entity.getKey().getTripNo().intValue());
        assertEquals("Unexpected value for HF", 0, entity.getHf().intValue());
        assertEquals("Unexpected value for attend school", 0, entity.getAttendSchool().intValue());
        assertEquals("Unexpected value for adult priority", 2, entity.getAdultPriorityCat().intValue());
        assertEquals("Unexpected value for person no trip", 7, entity.getPersNumTrips().intValue());
        assertEquals("Unexpected value for depart", 31800, entity.getDepart().intValue());
        assertEquals("Unexpected value for trip time", 600, entity.getTripTime().intValue());
        assertEquals("Unexpected value for trip mode", 1, entity.getTmode().intValue());
        assertEquals("Unexpected value for purpose", 7, entity.getPurpose11().intValue());

    }

    @Test
    public void testFindAll() throws Exception {
        List<HtsDataEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 167088, entities.size());

        boolean found = false;
        for (HtsDataEntity entity : entities) {
            if (entity.getKey().getHouseholdId() == 1 && entity.getKey().getPersonNo() ==1 && entity.getKey().getTripNo() == 1) {
                found = true;
                break;
            }

        }

        assertTrue("Could not find our entity", found);

    }
}
