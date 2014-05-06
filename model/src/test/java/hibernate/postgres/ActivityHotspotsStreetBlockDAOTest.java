package hibernate.postgres;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for ActivityHotspotsStreetBlockDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ActivityHotspotsStreetBlockDAOTest {

    @Autowired
    private ActivityHotspotsStreetBlockDAO dao;

    @Test
    public void testFindOne() throws Exception {
        ActivityHotspotsStreetBlockEntity entity = dao.findOne(5);

        assertEquals("Unexpected value for activity id", 5, entity.getActivityId());
        assertEquals("Unexpected value for hotspot street block", 4043, entity.getHotspotStreetBlockId().intValue());
    }

    @Test
    public void testFindAll() throws Exception {
        List<ActivityHotspotsStreetBlockEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 103403, entities.size());

        ActivityHotspotsStreetBlockEntity entity = entities.get(4);

        assertEquals("Unexpected value for activity id", 5, entity.getActivityId());
        assertEquals("Unexpected value for hotspot street block", 4043, entity.getHotspotStreetBlockId().intValue());
    }
}
