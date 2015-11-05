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
 * Test harness for ImmigrationRateDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ImmigrationRateDAOTest {

    @Autowired
    private ImmigrationRateDAO dao;

    @Test
    public void testFindOne() throws Exception {
        ImmigrationRateEntity entity = dao.findOne(2006);

        assertEquals("Unexpected value for year", 2006, entity.getYear());
        assertEquals("Unexpected value for HF1", 18, entity.getHf1().intValue());
        assertEquals("Unexpected value for HF2", 18, entity.getHf2().intValue());
        assertEquals("Unexpected value for HF3", 18, entity.getHf3().intValue());
        assertEquals("Unexpected value for HF4", 18, entity.getHf4().intValue());
        assertEquals("Unexpected value for HF5", 18, entity.getHf5().intValue());
        assertEquals("Unexpected value for HF6", 18, entity.getHf6().intValue());
        assertEquals("Unexpected value for HF7", 18, entity.getHf7().intValue());
        assertEquals("Unexpected value for HF8", 18, entity.getHf8().intValue());
        assertEquals("Unexpected value for HF9", 18, entity.getHf9().intValue());
        assertEquals("Unexpected value for HF10", 18, entity.getHf10().intValue());
        assertEquals("Unexpected value for HF11", 18, entity.getHf11().intValue());
        assertEquals("Unexpected value for HF12", 18, entity.getHf12().intValue());
        assertEquals("Unexpected value for HF13", 18, entity.getHf13().intValue());
        assertEquals("Unexpected value for HF14", 18, entity.getHf14().intValue());
        assertEquals("Unexpected value for HF15", 18, entity.getHf15().intValue());
        assertEquals("Unexpected value for HF16", 18, entity.getHf16().intValue());
        assertEquals("Unexpected value for NF", 18, entity.getNf().intValue());

    }

    @Test
    public void testFindAll() throws Exception {
        List<ImmigrationRateEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 20, entities.size());

        ImmigrationRateEntity entity = entities.get(0);
        assertEquals("Unexpected value for year", 2006, entity.getYear());
    }
}
