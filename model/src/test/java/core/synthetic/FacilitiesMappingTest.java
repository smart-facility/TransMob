package core.synthetic;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import core.statistics.FacilitiesMapping;

public class FacilitiesMappingTest {

	private FacilitiesMapping facilitiesMapping;

	// @Before Missing needed CSV file
	public void setUp() throws Exception {
		facilitiesMapping = new FacilitiesMapping();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test @Ignore
	public void testInitialMapping() {
		assertEquals(101, facilitiesMapping.getMapping().size(), 0);
	}

}
