package core.synthetic;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;

import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.attribute.HighestEduFinished;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.attribute.Occupation;
import core.synthetic.attribute.TransportModeToWork;
import core.synthetic.individual.Individual;


public class IndividualTest {

	/**
	 * name="indiviaual"
	 * 
	 * @uml.associationEnd
	 */

	@Before
	public void setUp() throws Exception {

		Individual individualInstance1 = new Individual(1, 30, Gender.Male,
				new BigDecimal(60000), HouseholdRelationship.LonePerson,
				Occupation.ClericalAndAdministrativeWorkers,
				TransportModeToWork.Bicycle, HighestEduFinished.BachelorDegree,
				Category.NF, 1, 1);
		Individual individualInstance2 = new Individual(1, 30, Gender.Male,
				new BigDecimal(50000), HouseholdRelationship.LonePerson,
				Occupation.ClericalAndAdministrativeWorkers,
				TransportModeToWork.Bicycle, HighestEduFinished.BachelorDegree,
				Category.NF, 1, 1);
	}

	@After
	public void tearDown() throws Exception {
	}

}
