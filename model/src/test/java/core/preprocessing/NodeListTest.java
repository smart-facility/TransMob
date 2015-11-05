/* This file is part of TransMob.

   TransMob is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   TransMob is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser Public License for more details.

   You should have received a copy of the GNU Lesser Public License
   along with TransMob.  If not, see <http://www.gnu.org/licenses/>.

*/
/**
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>qun@uow.edu.au
 */
package core.preprocessing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author qun
 *
 */
public class NodeListTest {

	/**
	 *   name="nodeListHashMap"
	 * @uml.associationEnd  
	 */
	private NodeList nodeListHashMap;
	/**
	 * @throws java.lang.Exception
	 */
    // @Before Don't use HardcodedData to setup file locations. Set the file explicitly for the test case.
	public void setUp() throws Exception {
		String file="C:/Transims/my DOC/TRANSIMS5/RandwickSim/inputs/Input_Node.txt";
		
	  nodeListHashMap=NodeList.getInstance(file);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		nodeListHashMap=null;
	}

	/**
	 * Test method for {@link NodeList#getInstance(String)}.
	 */
	@Test @Ignore
	public void testGetNodeList() {
		assertEquals(5922, nodeListHashMap.getNodeListSize());
		assertEquals(2059254, nodeListHashMap.getNodeID(new Coordinate(336315, 6245732.9)));
		assertEquals(15545, nodeListHashMap.getNodeID(new Coordinate(334250.2, 6248730.6)));
		
		WKTReader wktReader=new WKTReader();
		Geometry geometry=null;
		try {
			geometry = wktReader.read("MULTILINESTRING((334250.19678941 6248730.56004295,334235.758657186 6248614.27482286))");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] ids={15545,15986};
		assertArrayEquals(ids, nodeListHashMap.getNodeID(geometry));
	}

}
