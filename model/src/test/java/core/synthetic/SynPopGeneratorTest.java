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
 * 
 */
package core.synthetic;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.synthetic.SynPopGenerator;

/**
 * @author nhuynh
 * 
 */
public class SynPopGeneratorTest {

	private SynPopGenerator synPopGenerator;
    private static final Logger logger = Logger.getLogger(SynPopGeneratorTest.class);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		synPopGenerator = new SynPopGenerator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		synPopGenerator = null;
	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#main(java.lang.String[])}.
	 */
	@Test @Ignore
	public void testMain() {
		// logger.debug("Start testing SynPopGenerator");
		// SynPopGenerator.main(null);
	}

	/**
	 * Test method for {@link core.synthetic.SynPopGenerator#synPopGenerator()}.
	 */
    @Test @Ignore
	public void testSynPopGenerator() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#correctTablesUsingB04(core.PostgresHandler, core.PostgresHandler)}
	 * .
	 */
    @Test @Ignore
	public void testCorrectTablesUsingB04() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#createIndivRecords(int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testCreateIndivRecords() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#zTest(int[][], int[][], int[][], int[][], int[][], int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testZTest() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#displayTables(int[][], int[][], int[][], int[][], int[][], int[][], int[][], int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testDisplayTables() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#compareWithHTS(int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testCompareWithHTS() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#chiSquaredTest(int[][], int[][], int[][], int[][], int[][], int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testChiSquaredTest() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#adjustNumberOfHF(int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testAdjustNumberOfHF() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#adjustNumberOfNFHhold(int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testAdjustNumberOfNFHhold() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#allocateNumPersonsToHhold(int[][], int[][], int[][], int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testAllocateNumPersonsToHhold() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#correctNumPersonsInFamilyHhold(int[][], int[][], int[][], int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testCorrectNumPersonsInFamilyHhold() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#testMinNumPersonsInHhold(int[][], int[][], int)}
	 * .
	 */
    @Test @Ignore
	public void testTestMinNumPersonsInHhold() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#testNumberofHhold(int[][], int, int, int, int)}
	 * .
	 */
    @Test @Ignore
	public void testTestNumberofHhold() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#displayNumHF(int[][], int, int, int, int, int, int)}
	 * .
	 */
    @Test @Ignore
	public void testDisplayNumHF() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#correctHouseholdRelationshipTable(java.lang.String, core.PostgresHandler, int[][], int[][], int)}
	 * .
	 */
    @Test @Ignore
	public void testCorrectHouseholdRelationshipTable() {

	}

	/**
	 * Test method for
	 * {@link core.synthetic.SynPopGenerator#displayDataHholdRel(int[][], int[][])}
	 * .
	 */
    @Test @Ignore
	public void testDisplayDataHholdRel() {

	}

}
