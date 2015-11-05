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
 * Test harness for MortgageInterestRateDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class MortgageInterestRateDAOTest {

    @Autowired
    private MortgageInterestRateDAO dao;

    @Test
    public void testFindByYear() throws Exception {

        MortgageInterestRateEntity entity = dao.findByYear(2006);
        assertEquals("Unexpected value for year", 2006, entity.getYear());
        assertEquals("Unexpected value for mortgage rate", 0.07, entity.getRate(), 0);

    }

    @Test
    public void testFindAll() throws Exception {

        List<MortgageInterestRateEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 31, entities.size());

        MortgageInterestRateEntity entity = entities.get(0);
        assertEquals("Unexpected value for year", 2006, entity.getYear());

    }
}
