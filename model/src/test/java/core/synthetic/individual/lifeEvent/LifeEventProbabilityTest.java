package core.synthetic.individual.lifeEvent;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import core.synthetic.attribute.Gender;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class LifeEventProbabilityTest {

	private static LifeEventProbability lifeEventProbability;

    /**
     * Setup some entities and persist them to the database.
     * @throws Exception
     */
    @BeforeClass
	public static void setUp() throws Exception {
		lifeEventProbability = new LifeEventProbability();
    }


    /**
     * Verify we can retrieve our entities.
     */
	@Test
	public void testDao() {

		assertEquals(0.00187, lifeEventProbability.getDeathProbabilityByAgeSex(
				50, Gender.Female), 0);
		assertEquals(0.0153, lifeEventProbability.getDivorceProbabilityByAgeSex(
                20, Gender.Female), 0);
		assertEquals(0.837, lifeEventProbability.getJobGainProbabilityByAgeSex(
                30, Gender.Female), 0);
        assertEquals(0.163, lifeEventProbability.getJobLossProbabilityByAgeSex(
                40, Gender.Female), 0);
        assertEquals(0.0065, lifeEventProbability.getMarriageProbabilityByAgeSex(
                45, Gender.Female), 0);
        assertEquals(0.000237594, lifeEventProbability.getReproductionProbabilityByAgeSex(18, 2), 0);

		assertEquals(0.00469, lifeEventProbability.getDeathProbabilityByAgeSex(
				55, Gender.Male), 0);
        assertEquals(0.0184, lifeEventProbability.getDivorceProbabilityByAgeSex(
                25, Gender.Male), 0);
        assertEquals(0.837, lifeEventProbability.getJobGainProbabilityByAgeSex(
                35, Gender.Male), 0);
        assertEquals(0.163, lifeEventProbability.getJobLossProbabilityByAgeSex(
                45, Gender.Male), 0);
        assertEquals(0.0037, lifeEventProbability.getMarriageProbabilityByAgeSex(
                60, Gender.Male), 0);
	}

	@After
	public void tearDown() throws Exception {
		lifeEventProbability = null;
	}

}
