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
