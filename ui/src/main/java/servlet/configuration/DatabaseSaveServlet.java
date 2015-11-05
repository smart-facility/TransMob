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
package servlet.configuration;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlet.GeoDBReader;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class DatabaseSaveServlet
 */
public class DatabaseSaveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DatabaseSaveServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DatabaseSaveServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String databaseName = request.getParameter("databaseName"); //$NON-NLS-1$

		String responseString = "Save the scenario sucessfully"; //$NON-NLS-1$

		GeoDBReader geoDBReader = GeoDBReader.getPostgisInstance();

		List<String> existingDatabase = geoDBReader.listDatabase();
		
		try {
			if (existingDatabase.contains(databaseName)) {
				geoDBReader.overwriteDatabase(
						"postgres_TransportNSW_ui", databaseName); //$NON-NLS-1$
			} else {
				geoDBReader.copyDatabase("\"postgres_TransportNSW_ui\"",
						"\""+databaseName+"\"");
			}			
			geoDBReader.overwriteDatabase("postgres_TransportNSW_backup",
					"postgres_TransportNSW_ui");
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseString = e.getMessage();
		}
		
		GeoDBReader geoDBReaderTest = GeoDBReader
				.getPostgresTransportNSWInstance();
		logger.info("reset connection with Database like postgres_TransportNSW in DatabaseSaveServlet;");
				while (true) {
					try {
						if (geoDBReaderTest.isValidated()) {
							break;
						}

					} catch (Exception e) {
						// TODO: handle exception
					}
				}

		response.getWriter().print(responseString);
	}
}
