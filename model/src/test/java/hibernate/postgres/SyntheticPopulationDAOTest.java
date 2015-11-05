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

import core.synthetic.attribute.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for SyntheticPopulationDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class SyntheticPopulationDAOTest {

    @Autowired
    private SyntheticPopulationDAO dao;

    @Test
    public void testFindById() throws Exception {
        SyntheticPopulationEntity entity = dao.findById(531);

        assertEquals("Unexpected value for income", 109, (int) entity.getIncome());
        assertEquals("Unexpected value for household relationship", "Married", entity.getHouseholdRelationship());
        assertEquals("Unexpected value for household type", Category.HF1, Category.identify(entity.getHouseholdType()));
        assertEquals("Unexpected value for household id", 267, (int) entity.getHouseholdId());
        assertEquals("Unexpected value for Travel zone", 276, (int) entity.getTravelZone());
        assertEquals("Unexpected value for age", 23, (int) entity.getAge());
    }

    @Test
    public void testFindByHouseholdId() throws Exception {
        List<SyntheticPopulationEntity> entities = dao.findByHouseholdId(267);

        SyntheticPopulationEntity entity = entities.get(0);

        assertEquals("Unexpected value for income", 109, (int) entity.getIncome());
        assertEquals("Unexpected value for household relationship", "Married", entity.getHouseholdRelationship());
        assertEquals("Unexpected value for household type", Category.HF1, Category.identify(entity.getHouseholdType()));
        assertEquals("Unexpected value for household id", 267, (int) entity.getHouseholdId());
        assertEquals("Unexpected value for Travel zone", 276, (int) entity.getTravelZone());
        assertEquals("Unexpected value for age", 23, (int) entity.getAge());

    }

    @Test
    public void testFindAll() throws Exception {

        List<SyntheticPopulationEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 111545, entities.size());

        SyntheticPopulationEntity entity = entities.get(0);

        assertEquals("Unexpected value for income", 109, (int) entity.getIncome());
        assertEquals("Unexpected value for household relationship", "Married", entity.getHouseholdRelationship());
        assertEquals("Unexpected value for household type", Category.HF1, Category.identify(entity.getHouseholdType()));
        assertEquals("Unexpected value for household id", 267, (int) entity.getHouseholdId());
        assertEquals("Unexpected value for Travel zone", 276, (int) entity.getTravelZone());
        assertEquals("Unexpected value for age", 23, (int) entity.getAge());
    }

    @Test
    public void testFindFromFirst() throws Exception {
        List<SyntheticPopulationEntity> entities = dao.findFromFirst(5);

        assertEquals("Unexpected number of entities", 5, entities.size());

        SyntheticPopulationEntity entity = entities.get(0);

        assertEquals("Unexpected value for income", 109, (int) entity.getIncome());
        assertEquals("Unexpected value for household relationship", "Married", entity.getHouseholdRelationship());
        assertEquals("Unexpected value for household type", Category.HF1, Category.identify(entity.getHouseholdType()));
        assertEquals("Unexpected value for household id", 267, (int) entity.getHouseholdId());
        assertEquals("Unexpected value for Travel zone", 276, (int) entity.getTravelZone());
        assertEquals("Unexpected value for age", 23, (int) entity.getAge());

    }

    @Test
    public void testFindHouseholdData() throws Exception {

        List<int []> results = dao.findHouseholdData();

        assertEquals("Unexpected household id", 267, results.get(0)[0]);
        assertEquals("Unexpected income", 1599, results.get(0)[1]);
        assertEquals("Unexpected # bedrooms", 2, results.get(0)[2]);

    }
}
