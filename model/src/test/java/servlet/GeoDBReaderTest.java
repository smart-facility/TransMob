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
package servlet;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.HardcodedData;
import core.synthetic.attribute.HouseholdRelationship;
/**
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * 
 */
public class GeoDBReaderTest {

	/**
	 * name="logger"
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * name="connection"
	 */
	@SuppressWarnings("unused")
	private Connection connection;
	/**
	 * name="mockConnectionControl"
	 * 
	 * @uml.associationEnd readOnly="true"
	 */
	@SuppressWarnings("unused")
	private IMocksControl mockConnectionControl;
	/**
	 * name="preparedStatement"
	 */
	@SuppressWarnings("unused")
	private PreparedStatement preparedStatement;
	/**
	 * name="resultSet"
	 */
	@SuppressWarnings("unused")
	private ResultSet resultSet;



	@Before
	public void setUp() throws Exception {
		// mockConnectionControl =createControl();
		// connection = mockConnectionControl.createMock(Connection.class);
		// preparedStatement =
		// mockConnectionControl.createMock(PreparedStatement.class);
		// resultSet=mockConnectionControl.createMock(ResultSet.class);
	}

	@After
	public void tearDown() throws Exception {
	}


    @Test @Ignore
	public void testGetBusRoutes() {
	}







	@Test @Ignore
	public void testPopulateLifecycleEventsProbTable() {
		// geoDBReaderPostgres.populateLEPTable();

		/*
		 * SimpleIndividual aSimpleIndividual = new SimpleIndividual(2, 24,
		 * Gender.Female, new BigDecimal(10000), HouseholdRelationship.Married,
		 * Occupation.ClericalAndAdministrativeWorkers,
		 * TransportModeToWork.Bicycle,
		 * HighestEduFinished.AdvancedDiplomaAndDiploma, Category.HF1);
		 * 
		 * 
		 * geoDBReaderPostgres.getProbabilityFromTable(aSimpleIndividual);
		 */
		// Random random = GlobalRandom.getInstance();
		System.out.println(HouseholdRelationship.values()[HardcodedData.random
				.nextInt(HouseholdRelationship.values().length)].toString());
	}

}
