package core.preprocessing;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author qun
 *
 */
public class BusRouteTest {

	private BusRoute busRoute;
	/**
	 * @throws java.lang.Exception
	 */
	// @Before Won't work unless test files are available in SVN.
	public void setUp() throws Exception {
		busRoute=new BusRoute();
		busRoute.setFilePath("C:/Transims/my DOC/TRANSIMS5/RandwickSim/inputs/new_busroute_text_files/");
		busRoute.readLinkFile("C:/Transims/my DOC/TRANSIMS5/RandwickSim/inputs/temp/Input_Link.txt");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		busRoute=null;
	}

	/**
	 * Test method for {@link core.preprocessing.BusRoute#readBusRoute(java.lang.String)}.
	 */
	@Test @Ignore
	public void testReadBusRoute() {
		ArrayList<Integer> result=busRoute.readBusRoute("R2738_1070132");
		
		assertEquals(7905, result.get(9),0);
		assertEquals(8269, result.get(result.size()-1),0);
		
		@SuppressWarnings("unused")
		ArrayList<Integer> result2=busRoute.readBusRoute("R11202_1063395");
		
	}
	

	@Test @Ignore
	public void testReadLinkFile(){
		
		assertEquals(3030, busRoute.getLinkID(352385,	352636	));
	}

}
