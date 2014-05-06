package core.synthetic;

import static org.junit.Assert.assertEquals;
import hibernate.postgres.SyntheticPopulationDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.ApplicationContextHolder;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.relocationTrigger.RelocationTrigger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class RelocationTriggerTest {

	private RelocationTrigger relocationTrigger;

	
	@Before
	public void setUp() throws Exception {
	
		this.relocationTrigger = new RelocationTrigger();
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

    @Test
	public void testCalcualteBedrooms() {
		Household household = new Household();
		Individual individual1 = new Individual();
		individual1.setHouseholdRelationship(HouseholdRelationship.Married);
		individual1.setAge(29);
		Individual individual2 = new Individual();
		individual2.setHouseholdRelationship(HouseholdRelationship.U15Child);
		individual2.setAge(9);
		Individual individual3 = new Individual();
		individual3.setHouseholdRelationship(HouseholdRelationship.Relative);
		individual2.setAge(19);
		Individual individual4 = new Individual();
		individual4.setHouseholdRelationship(HouseholdRelationship.U15Child);
		individual4.setAge(8);

		List<Individual> residents = new ArrayList<Individual>();
		residents.add(individual1);
		residents.add(individual2);
		residents.add(individual3);
		residents.add(individual4);

		household.setResidents(residents);

		assertEquals(3, this.relocationTrigger.calcualteBedrooms(household), 0);

	}

    @Test @Ignore    // Not testable until output databases have been created and datasource has been reconfigured.
	public void testGetLastYearData() {
		Map<Integer, int[]> resultMap = this.relocationTrigger.getDataForYear(2006);
	}

}
