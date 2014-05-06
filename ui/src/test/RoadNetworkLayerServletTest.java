/**
 * 
 */
package servlet.test;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servlet.RoadNetworkLayerServlet;

/**
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * 
 */
public class RoadNetworkLayerServletTest {

	/**
	 *  name="roadNetWorkLayerServlet"
	 * @uml.associationEnd
	 */
	private RoadNetworkLayerServlet roadNetWorkLayerServlet;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		roadNetWorkLayerServlet = new RoadNetworkLayerServlet();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		roadNetWorkLayerServlet = null;
	}

	/**
	 * Test method for
	 * {@link servlet.RoadNetworkLayerServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * .
	 */
	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link servlet.RoadNetworkLayerServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * .
	 */
	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link servlet.RoadNetworkLayerServlet#postPocess()}.
	 */
	@Test
	public void testPostPocess() {
		String shapeFilePath = "C:/Transims/my DOC/TRANSIMS5/RandwickSim/inputs/shp/";
		String dumpingBatchPath = "C:/Transims/my DOC/TRANSIMS5/RandwickSim/batch/pgsql2shp.bat";
		String tableName = "streetspro_gma_randwick_and_surrounding";
		String controlFilePath = "C:/Transims/my DOC/TRANSIMS5/RandwickSim/control/Net.Alex.NetPrep.ctl";
		String netBatchPath = "C:/Transims/my DOC/TRANSIMS5/RandwickSim/batch/NetworkConverter.bat";
		roadNetWorkLayerServlet.postPocess(shapeFilePath, dumpingBatchPath,
				tableName, controlFilePath, netBatchPath);
	}

}
