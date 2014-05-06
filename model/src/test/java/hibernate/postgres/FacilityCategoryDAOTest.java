package hibernate.postgres;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for FacilityCategoryDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class FacilityCategoryDAOTest {
    @Autowired
    private FacilityCategoryDAO dao;

    @Test
    public void testFindOne() throws Exception {
        FacilityCategoryEntity entity = dao.findOne(10);

        assertEquals("Unexpected value for facility ID", 10, entity.getId());
        assertEquals("Unexpected value category", "go_to_work", entity.getCategory());
        assertEquals("Unexpected value facility", "boat_ramp", entity.getFacility());


    }

    @Test
    public void testFindAll() throws Exception {
        List<FacilityCategoryEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 204, entities.size());

        FacilityCategoryEntity entity = entities.get(9);

        assertEquals("Unexpected value for facility ID", 10, entity.getId());
    }
}
