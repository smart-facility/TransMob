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
 * Test harness for ParkingCapacityDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ParkingCapacityDAOTest {

    @Autowired
    private ParkingCapacityDAO dao;

    @Test
    public void testFindOne() throws Exception {
        ParkingCapacityEntity entity = dao.findOne(1);

        assertEquals("Unexpected value for X coordinates", 334562.3922, entity.getxCoordinate(), 0);
        assertEquals("Unexpected value for Y coordinates", 6248536.848, entity.getyCoordinate(), 0);
        assertEquals("Unexpected value for space", 9, entity.getSpace().intValue());
        assertEquals("Unexpected value for note", "HouseHold_Parking", entity.getNote());



    }

    @Test
    public void testFindAll() throws Exception {
        List<ParkingCapacityEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 102335, entities.size());

        ParkingCapacityEntity entity = entities.get(0);

        assertEquals("Unexpected value for Parking ID", 1, entity.getParkingId());
        assertEquals("Unexpected value for X coordinates", 334562.3922, entity.getxCoordinate(), 0);

    }
}
