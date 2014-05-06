package core.postprocessing.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
public class DatabaseCreatorTest {

	DatabaseCreator databaseCreator = new DatabaseCreator();

	@Before
	public void setUp() throws Exception {
		databaseCreator = new DatabaseCreator();
	}

	@After
	public void tearDown() throws Exception {
		databaseCreator = null;
	}

	@Test @Ignore
	public void testCreateDatabase() {

		databaseCreator.createDatabase();
	}

    @Test @Ignore
	public void testCreateTravelZoneOutputTable() {
		int startYear = 2006;
		int numberOfYear = 20;
		databaseCreator.createTravelZoneOutputTable(startYear, numberOfYear);
	}

    @Test @Ignore
	public void testCreateSyntheticPopulationOutputTable() {
		int startYear = 2006;
		int numberOfYear = 100;
		databaseCreator.createSyntheticPopulationOutputTable(startYear, numberOfYear);
	}

    @Test @Ignore
	public void testCreateTravelmodeChoiceOutputTable() {
		int startYear = 2006;
		int numberOfYear = 20;
		databaseCreator.createTravelModeChoiseOutputTable(startYear, numberOfYear);
	}

}
