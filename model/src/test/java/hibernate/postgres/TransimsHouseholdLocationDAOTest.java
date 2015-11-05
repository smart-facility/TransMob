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
 * Test harness for TransimsHouseholdLocationDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TransimsHouseholdLocationDAOTest {

    @Autowired
    private TransimsHouseholdLocationDAO dao;

    @Test
    public void testFindById() throws Exception {
        TransimsHouseholdLocationEntity entity = dao.findById(5);

        assertEquals("Unexpected value for dwelling index", 5, entity.getDwellingIndex());
        assertEquals("Unexpected value for activity location", 5, (int) entity.getActivityLocation());
        assertEquals("Unexpected value for cd code", 1401214, (int) entity.getCdCode());
        assertEquals("Unexpected value for X coordinate", 334510.6696, entity.getxCoord(), 0);
        assertEquals("Unexpected value for Y coordinate", 6248711.187, entity.getyCoord(), 0);
        assertEquals("Unexpected value for block id", 2457, (int) entity.getBlockId());
        assertEquals("Unexpected value for note bus", 1, (int) entity.getNoteBus());
        assertEquals("Unexpected value for note train", 0, (int) entity.getNoteTrain());
        assertEquals("Unexpected value for household id", -1, (int) entity.getHholdIndex());
        assertEquals("Unexpected value for travel zone", 205, (int) entity.getTravelZoneId());
        assertEquals("Unexpected value for year available", 0, (int) entity.getYearAvailable());
        assertEquals("Unexpected value for bedrooms number ", 0, (int) entity.getBedroomsNumber());

    }

    @Test
    public void testFindByTravelZoneId() throws Exception {
        List<TransimsHouseholdLocationEntity> entities = dao.findByTravelZoneId(205);

        assertEquals("Unexpected number of entities", 1425, entities.size());

        boolean found = false;

        // Find a dwelling we know should exist in the travel zone
        for (TransimsHouseholdLocationEntity entity : entities) {
            if (entity.getDwellingIndex() == 5) {
                found = true;
                assertEquals("Unexpected value for X coordinate", 334510.6696, entity.getxCoord(), 0);
                assertEquals("Unexpected value for Y coordinate", 6248711.187, entity.getyCoord(), 0);
                assertEquals("Unexpected value for block id", 2457, (int) entity.getBlockId());
                break;
            }
        }

        assertTrue("Did not find the expected entity within the travel zone", found);
    }

    @Test
    public void testFindAll() throws Exception {
        List<TransimsHouseholdLocationEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 99820, entities.size());

        TransimsHouseholdLocationEntity entity = entities.get(4);

        assertEquals("Unexpected value for dwelling index", 5, entity.getDwellingIndex());
        assertEquals("Unexpected value for activity location", 5, (int) entity.getActivityLocation());
        assertEquals("Unexpected value for cd code", 1401214, (int) entity.getCdCode());

    }
}
