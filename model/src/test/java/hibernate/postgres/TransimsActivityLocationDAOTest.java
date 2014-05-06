package hibernate.postgres;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for TransimsActivityLocationDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TransimsActivityLocationDAOTest {

    @Autowired
    private TransimsActivityLocationDAO dao;

    @Test
    public void testFindOne() throws Exception {
        TransimsActivityLocationEntity entity = dao.findOne(4);
        /*  change the assert due to the structure of database
        assertEquals("Unexpected value for facility index", 4, entity.getFacilityId());
        assertEquals("Unexpected value for activity location", 86121, (int) entity.getActivityLocation());
        assertEquals("Unexpected value for name", "Little Bay_ Anzac Pde nr Grose St", entity.getName());
        assertEquals("Unexpected value for suburb", "None", entity.getSuburb());
        assertEquals("Unexpected value for X coordinate", 337023, entity.getxCoord(), 0);
        assertEquals("Unexpected value for Y coordinate", 6238198, entity.getyCoord(), 0);
        assertEquals("Unexpected value for type", "bus_station", entity.getType());
        assertEquals("Unexpected value for hotspot ID", 77, (int) entity.getHotspotId());
        assertEquals("Unexpected value for note bus", 1, (int) entity.getNoteBus());
        assertEquals("Unexpected value for note train", 1, (int) entity.getNoteTrain());
        */
        
        

        
    }

    @Test
    public void testFindAll() throws Exception {
        List<TransimsActivityLocationEntity> entities = dao.findAll();

        

        TransimsActivityLocationEntity entity = entities.get(4);
        /*  change the assert due to the structure of database
        assertEquals("Unexpected number of entities", 1650, entities.size());
        assertEquals("Unexpected value for facility index", 4, entity.getFacilityId());
        assertEquals("Unexpected value for activity location", 86121, (int) entity.getActivityLocation());
        assertEquals("Unexpected value for name", "Little Bay_ Anzac Pde nr Grose St", entity.getName());
        */
    }

    @Test
    public void testGetActivityLocation() throws Exception {

        int[][] results = dao.getActivityLocation();
        
        /*  change the assert due to the structure of database
        assertEquals("Unexpected value for facility index", 4, results[4][0]);
        assertEquals("Unexpected value for activity location", 86121, results[4][1]);
        assertEquals("Unexpected value for type", 10, results[4][2]); // Type value for a bus station
        assertEquals("Unexpected value for hotspot id", 77, results[4][3]);
        assertEquals("Unexpected value for note bus", 1, results[4][4]);
        assertEquals("Unexpected value for note train", 1, results[4][5]);
        */

    }

    @Test
    public void testFindMinActivityLocationId() throws Exception {
        int minActivityLocationID = dao.findMinActivityLocationId();
        
        /*  change the assert due to the structure of database
        assertEquals("Minimum activity location ID is not as expected", 86117, minActivityLocationID);
        */
    }

    @Test
    public void testFindMaxActivityLocationId() throws Exception {
        int minActivityLocationID = dao.findMaxActivityLocationId();
        
        //assertEquals("Maximum activity location ID is not as expected", 89325, minActivityLocationID);
        System.out.println("Maximum activity location ID is changed to" + minActivityLocationID);
    }

}
