package hibernate.postgis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for FacilitiesMappingDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class FacilitiesMappingDAOTest {

    @Autowired
    private FacilitiesMappingDAO dao;

    @Test
    public void testFindOne() throws Exception {
        FacilitiesMappingEntity entity = dao.findOne(5);

        assertEquals("Incorrect ID", 5, entity.getId());
        assertEquals("Incorrect Facility", "AUSTRALILA POST", entity.getFacility());
        assertEquals("Incorrect Category", "My services", entity.getCategory());
        assertEquals("Incorrect starred value", false, entity.getStarred());


    }

    @Test
    public void testFindAll() throws Exception {

        List<FacilitiesMappingEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 102, entities.size());

        FacilitiesMappingEntity entity = entities.get(4);

        assertEquals("Incorrect ID", 5, entity.getId());
        assertEquals("Incorrect Facility", "AUSTRALILA POST", entity.getFacility());
        assertEquals("Incorrect Category", "My services", entity.getCategory());
        assertEquals("Incorrect starred value", false, entity.getStarred());

    }
}
