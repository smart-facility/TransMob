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

/**
 * Test harness for StreetsGMARandwickAndSurroundingDAO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class StreetsGMARandwickAndSurroundingDAOTest {

    @Autowired
    private StreetsGMARandwickAndSurroundingDAO dao;

    @Test
    public void testFindById() throws Exception {

        StreetsGMARandwickAndSurroundingEntity entity = dao.findById(5);

        assertEquals("Unexpected value for SWF_UFI", "100578199", entity.getSwUfi());
        assertEquals("Unexpected value for ID", "356959", entity.getId());
        assertEquals("Unexpected value for Street", "ROAD", entity.getStreet());
        assertEquals("Unexpected value for Length in meters", 385.5, entity.getLengthM(), 0);


    }

    @Test
    public void testFindAll() throws Exception {

        List<StreetsGMARandwickAndSurroundingEntity> results = dao.findAll();

        assertEquals("Unexpected number of results", 9768, results.size());

        StreetsGMARandwickAndSurroundingEntity entity = results.get(4);

        assertEquals("Unexpected value for SWF_UFI", "100578199", entity.getSwUfi());
        assertEquals("Unexpected value for ID", "356959", entity.getId());
        assertEquals("Unexpected value for Street", "ROAD", entity.getStreet());
        assertEquals("Unexpected value for Length in meters", 385.5, entity.getLengthM(), 0);

    }
}
