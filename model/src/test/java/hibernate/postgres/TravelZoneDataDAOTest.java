package hibernate.postgres;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test harness for TravelZoneDataDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TravelZoneDataDAOTest {

    @Autowired
    private TravelZoneDataDAO dao;

    @Test
    public void testFindById() throws Exception {
        TravelZoneDataEntity entity = dao.findById(5);

        assertEquals("Unexpected value for gid", 5, entity.getGid());
        assertEquals("Unexpected value for areaCode", 535, entity.getAreaCode());
        assertEquals("Unexpected value for tz_skm", 0.321557792706, entity.getTzSkm().doubleValue(), 0);
        assertEquals("Unexpected value for satisfaction", 0.55882763538270119330064744644914753735065460205078125, entity.getSatisfaction().doubleValue(), 0);
        assertEquals("Unexpected value for org satisfaction", 0.3, entity.getOrgSatisfaction().doubleValue(), 0);
        assertEquals("Unexpected value for housing availability", 0.3, entity.getHousingAvailability(), 0);
        assertEquals("Unexpected value for travel time to work", 0.38653846153846155, entity.getTravelTimeToWork(), 0);
        assertEquals("Unexpected value for beach proximity", 1, entity.getBeachProximity(), 0);
        assertEquals("Unexpected value for weekday congestion", 0.8428869897372094, entity.getWeekdaysCongestion(), 0);
        assertEquals("Unexpected value for weekend congestion", 0.5968520286391089, entity.getWeekendCongestion(), 0);

    }

    @Test
    public void testFindAll() throws Exception {
        List<TravelZoneDataEntity> entities = dao.findAll();

        assertEquals("Unexpected number of entities", 64, entities.size());

        TravelZoneDataEntity entity = entities.get(4);

        assertEquals("Unexpected value for gid", 5, entity.getGid());
        assertEquals("Unexpected value for areaCode", 535, entity.getAreaCode());
        assertEquals("Unexpected value for tz_skm", 0.321557792706, entity.getTzSkm().doubleValue(), 0);

    }
}
