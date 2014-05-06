package hibernate.postgres;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for ParkingAccessDao.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class ParkingAccessDaoTest {

    @Autowired
    private ParkingAccessDao dao;

    @Test
    public void testFindOne() throws Exception {
        ParkingAccessEntity entity = dao.findOne(1);

        assertEquals("Unexpected value for from ID", 1, entity.getFromId().intValue());
        assertEquals("Unexpected value for to ID", 1, entity.getToId().intValue());
        assertEquals("Unexpected value for from Type", "HOUSEHOLD LOCATION", entity.getFromType());
        assertEquals("Unexpected value for to Type", "HouseHold_Parking", entity.getToType());


    }

    @Test
    public void testFindAll() throws Exception {
        List<ParkingAccessEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 119826, entities.size());

        ParkingAccessEntity entity = entities.get(0);
        assertEquals("Unexpected value for from ID", 1, entity.getFromId().intValue());

    }
}
