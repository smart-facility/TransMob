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

/**
 * Test harness for ProcessLinkDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ProcessLinkDAOTest {

    @Autowired
    private ProcessLinkDAO dao;

    @Test
    public void testFindOne() throws Exception {

        ProcessLinkEntity entity = dao.findOne(1);

        assertEquals("Unexpected value for access", "1", entity.getAccess());
        assertEquals("Unexpected value for FROM ID", "1", entity.getFromId());
        assertEquals("Unexpected value for TO ID", "1", entity.getToId());
        assertEquals("Unexpected value for TO TYPE", "PARKING", entity.getToType());
        assertEquals("Unexpected value for TIME", "86400", entity.getTime());
        assertEquals("Unexpected value for COST", "0", entity.getCost());
        assertEquals("Unexpected value for Notes", "Parking Access", entity.getNotes());
        assertEquals("Unexpected value for FROM DETAILS", "HOUSEHOLD LOCATION", entity.getFromDetails());
        assertEquals("Unexpected value for TO DETAILS", "HouseHold_Parking", entity.getToDetails());
        assertEquals("Unexpected value for FROM TYPE", "ACTIVITY", entity.getFromType());

    }

    @Test
    public void testFindAll() throws Exception {
        List<ProcessLinkEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 100403, entities.size());

        ProcessLinkEntity entity = entities.get(0);
        assertEquals("Unexpected value for ID", 1, entity.getId());

    }

    @Test
    public void testFindAllToIds() throws Exception {
        List<Integer> toIds = dao.findAllToIds();

        // List is sorted by ID, so lets look at the last one
        assertEquals("Could not find expected TO_ID", 103403, toIds.get(toIds.size() - 1).intValue());

    }
}
