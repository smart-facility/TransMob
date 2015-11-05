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
package hibernate.postgres.lifeEvents;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test Harness for LifeEventsDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class LifeEventsDAOTest {

    @Autowired
    private LifeEventsDAO dao;

    @Test
    public void testFindBirthProbability() throws Exception {

        BirthProbabilityEntity entity = dao.findBirthProbability(43);

        assertEquals("Unexpected value for age", 43, entity.getAge());
        assertEquals("Unexpected value for 1st Child", 0.006, entity.getFirstChild(), 0);
        assertEquals("Unexpected value for 2nd Child", 0.007659087, entity.getSecondChild(), 0);
        assertEquals("Unexpected value for 3rd Child", 0.00422167, entity.getThirdChild(), 0);
        assertEquals("Unexpected value for 4th Child", 0.001869948, entity.getFourthChild(), 0);
        assertEquals("Unexpected value for 5th Child", 0.001007328, entity.getFifthChild(), 0);
        assertEquals("Unexpected value for 6th Child", 0.000866541, entity.getSixOrMore(), 0);

    }

    @Test
    public void testFindAllBirthProbabilities() throws Exception {
        List<BirthProbabilityEntity> entities = dao.findAllBirthProbabilities();

        assertEquals("Unexpected number of entities", 35, entities.size());

        BirthProbabilityEntity entity = entities.get(28);
        assertEquals("Unexpected value for 1st Child", 0.006, entity.getFirstChild(), 0);

    }

    @Test
    public void testFindMaleDivorceProbability() throws Exception {
        DivorceProbabilityMaleEntity entity = dao.findMaleDivorceProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.019, entity.getProbability(), 0);

    }

    @Test
    public void testFindAllMaleDivorceProbabilities() throws Exception {
        List<DivorceProbabilityMaleEntity> entities = dao.findAllMaleDivorceProbabilities();

        assertEquals("Unexpected number of entities", 54, entities.size());

        DivorceProbabilityMaleEntity entity = entities.get(19);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.019, entity.getProbability(), 0);
    }

    @Test
    public void testFindFemaleDivorceProbability() throws Exception {
        DivorceProbabilityFemaleEntity entity = dao.findFemaleDivorceProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.0181, entity.getProbability(), 0);

    }

    @Test
    public void testFindAllFemaleDivorceProbabilities() throws Exception {
        List<DivorceProbabilityFemaleEntity> entities = dao.findAllFemaleDivorceProbabilities();

        assertEquals("Unexpected number of entities", 54, entities.size());

        DivorceProbabilityFemaleEntity entity = entities.get(19);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.0181, entity.getProbability(), 0);

    }

    @Test
    public void testFindMaleDeathProbability() throws Exception {
        DeathProbabilityMaleEntity entity = dao.findMaleDeathProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.00101, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllMaleDeathProbabilities() throws Exception {
        List<DeathProbabilityMaleEntity> entities = dao.findAllMaleDeathProbabilities();

        assertEquals("Unexpected number of entities", 101, entities.size());

        DeathProbabilityMaleEntity entity = entities.get(35);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.00101, entity.getProbability(), 0);

    }

    @Test
    public void testFindFemaleDeathProbability() throws Exception {
        DeathProbabilityFemaleEntity entity = dao.findFemaleDeathProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.00051, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllFemaleDeathProbabilities() throws Exception {
        List<DeathProbabilityFemaleEntity> entities = dao.findAllFemaleDeathProbabilities();

        assertEquals("Unexpected number of entities", 101, entities.size());

        DeathProbabilityFemaleEntity entity = entities.get(35);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.00051, entity.getProbability(), 0);
    }

    @Test
    public void testFindMaleJobGainProbability() throws Exception {
        JobGainProbabilityMaleEntity entity = dao.findMaleJobGainProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.837, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllMaleJobGainProbabilities() throws Exception {
        List<JobGainProbabilityMaleEntity> entities = dao.findAllMaleJobGainProbabilities();

        assertEquals("Unexpected number of entities", 45, entities.size());

        JobGainProbabilityMaleEntity entity = entities.get(20);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.837, entity.getProbability(), 0);
    }

    @Test
    public void testFindFemaleJobGainProbability() throws Exception {
        JobGainProbabilityFemaleEntity entity = dao.findFemaleJobGainProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.837, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllFemaleJobGainProbabilities() throws Exception {
        List<JobGainProbabilityFemaleEntity> entities = dao.findAllFemaleJobGainProbabilities();

        assertEquals("Unexpected number of entities", 45, entities.size());

        JobGainProbabilityFemaleEntity entity = entities.get(20);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.837, entity.getProbability(), 0);
    }

    @Test
    public void testFindMaleJobLossProbability() throws Exception {
        JobLossProbabilityMaleEntity entity = dao.findMaleJobLossProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.163, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllMaleJobLossProbabilities() throws Exception {
        List<JobLossProbabilityMaleEntity> entities = dao.findAllMaleJobLossProbabilities();

        assertEquals("Unexpected number of entities", 45, entities.size());

        JobLossProbabilityMaleEntity entity = entities.get(20);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.163, entity.getProbability(), 0);
    }

    @Test
    public void testFindFemaleJobLossProbability() throws Exception {
        JobLossProbabilityFemaleEntity entity = dao.findFemaleJobLossProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.163, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllFemaleJobLossProbabilities() throws Exception {
        List<JobLossProbabilityFemaleEntity> entities = dao.findAllFemaleJobLossProbabilities();

        assertEquals("Unexpected number of entities", 45, entities.size());

        JobLossProbabilityFemaleEntity entity = entities.get(20);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.163, entity.getProbability(), 0);
    }

    @Test
    public void testFindMaleMarriageProbability() throws Exception {
        MarriageProbabilityMaleEntity entity = dao.findMaleMarriageProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.0197, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllMaleMarriageProbabilities() throws Exception {
        List<MarriageProbabilityMaleEntity> entities = dao.findAllMaleMarriageProbabilities();

        assertEquals("Unexpected number of entities", 49, entities.size());

        MarriageProbabilityMaleEntity entity = entities.get(19);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.0197, entity.getProbability(), 0);
    }

    @Test
    public void testFindFemaleMarriageProbability() throws Exception {
        MarriageProbabilityFemaleEntity entity = dao.findFemaleMarriageProbability(35);

        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.0154, entity.getProbability(), 0);
    }

    @Test
    public void testFindAllFemaleMarriageProbabilities() throws Exception {
        List<MarriageProbabilityFemaleEntity> entities = dao.findAllFemaleMarriageProbabilities();

        assertEquals("Unexpected number of entities", 49, entities.size());

        MarriageProbabilityFemaleEntity entity = entities.get(19);
        assertEquals("Unexpected value for age", 35, entity.getAge());
        assertEquals("Unexpected value for probability", 0.0154, entity.getProbability(), 0);

    }
}
