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
 * Test harness for ActivityHotspotsStreetBlockDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ActivityHotspotsStreetBlockDAOTest {

    @Autowired
    private ActivityHotspotsStreetBlockDAO dao;

    @Test
    public void testFindOne() throws Exception {
        ActivityHotspotsStreetBlockEntity entity = dao.findOne(5);

        assertEquals("Unexpected value for activity id", 5, entity.getActivityId());
        assertEquals("Unexpected value for hotspot street block", 4043, entity.getHotspotStreetBlockId().intValue());
    }

    @Test
    public void testFindAll() throws Exception {
        List<ActivityHotspotsStreetBlockEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 103403, entities.size());

        ActivityHotspotsStreetBlockEntity entity = entities.get(4);

        assertEquals("Unexpected value for activity id", 5, entity.getActivityId());
        assertEquals("Unexpected value for hotspot street block", 4043, entity.getHotspotStreetBlockId().intValue());
    }
}
