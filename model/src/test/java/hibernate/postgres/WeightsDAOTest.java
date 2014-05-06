package hibernate.postgres;

import core.synthetic.attribute.Gender;
import core.synthetic.individual.Individual;
import core.synthetic.survey.DemographicInfomation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for TransimsActivityLocationDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class WeightsDAOTest {

    @Autowired
    private WeightsDAO dao;

    @Test
    public void testFindOne() throws Exception {

        WeightsEntity entity = dao.findOne(2);

        assertEquals("Unexpected value for category", 2, entity.getCategory());
        assertEquals("Unexpected value for gender", 1, entity.getGender().intValue());
        assertEquals("Unexpected value for age", 1, entity.getAge().intValue());
        assertEquals("Unexpected value for income", 2, entity.getIncome().intValue());
        assertEquals("Unexpected value for home weight", 0.21429, entity.getHome(), 0);
        assertEquals("Unexpected value for neighbourhood weight", 0.095238, entity.getNeighborhood(), 0);
        assertEquals("Unexpected value for services weight", 0.071429, entity.getServices(), 0);
        assertEquals("Unexpected value for entertainment weight", 0.14286, entity.getEntertainment(), 0);
        assertEquals("Unexpected value for work and education weight", 0.2619, entity.getWorkAndEducation(), 0);
        assertEquals("Unexpected value for transport weight", 0.21429, entity.getTransport(), 0);
    }



    @Test
    public void testFindByDemographic() throws Exception {

        Individual indiv = new Individual();
        indiv.setAge(45);
        indiv.setGender(Gender.Female);
        indiv.setIncome(new BigDecimal(500));
        DemographicInfomation info = DemographicInfomation.classifyIndividual(indiv);

        WeightsEntity entity = dao.findByDemographic(info);

        assertEquals("Unexpected value for category", 27, entity.getCategory());
        assertEquals("Unexpected value for gender", 2, entity.getGender().intValue());
        assertEquals("Unexpected value for age", 2, entity.getAge().intValue());
        assertEquals("Unexpected value for income", 2, entity.getIncome().intValue());
    }

    @Test
    public void testFindAll() throws Exception {

        List<WeightsEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 40, entities.size());

        WeightsEntity entity = entities.get(0);
        assertEquals("Unexpected value for category", 1, entity.getCategory());
        assertEquals("Unexpected value for gender", 1, entity.getGender().intValue());
        assertEquals("Unexpected value for age", 1, entity.getAge().intValue());
        assertEquals("Unexpected value for income", 1, entity.getIncome().intValue());


    }
}
