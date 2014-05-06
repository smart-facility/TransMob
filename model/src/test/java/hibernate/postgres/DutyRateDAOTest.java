package hibernate.postgres;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for DutyRateDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class DutyRateDAOTest {

    @Autowired
    private DutyRateDAO dao;

    @Test
    public void testFindOne() throws Exception {

        DutyRateEntity entity = dao.findOne(30001);

        assertEquals("Unexpected value for dutiable value 1", 30001, entity.getDutiableValue1());
        assertEquals("Unexpected value for dutiable value 2", 80000, entity.getDutiableValue2().intValue());
        assertEquals("Unexpected value for duty rate", 1.75, entity.getDutyRate(), 0);
        assertEquals("Unexpected value for base value", 415, entity.getBaseValue(), 0);

    }

    @Test
    public void testFindAll() throws Exception {
        List<DutyRateEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 7, entities.size());

        DutyRateEntity entity = entities.get(2);
        assertEquals("Unexpected value for dutiable value 1", 30001, entity.getDutiableValue1());

    }
}
