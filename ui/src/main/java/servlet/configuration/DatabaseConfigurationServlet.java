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

import hibernate.configuration.ConfigurationDAO;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlet.GeoDBReader;

import com.google.gson.Gson;

import core.ApplicationContextHolder;

import java.util.Iterator;

@WebServlet("/DatabaseConfigurationServlet")
public class DatabaseConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(DatabaseConfigurationServlet.class);
	private final ConfigurationDAO configurationDAO;

	public DatabaseConfigurationServlet() {
		super();
		this.configurationDAO = ApplicationContextHolder
				.getBean(ConfigurationDAO.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		Gson gson = new Gson();
		List<String> results = GeoDBReader.getPostgresTransportNSWInstance()
				.listDatabase();
		// List<String> results =this.configurationDAO.getAllscenarioname();

		logger.info("Find all databases");

		// * filter the database name
		Iterator<String> iter = results.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.equals("postgres_TransportNSW_ui")
					|| s.equals("postgres_TransportNSW_backup")
					|| s.equals("postgres_TransportNSW")
					|| s.equals("postgres") || s.equals("yellowfindb")
					|| s.equals("postgis") || s.equals("model_configuration")
					|| s.equals("template_postgis") || s.equals("test_only")
					|| s.equals("SID_stsot")) {
				iter.remove();

			}
			if (Character.toString(s.charAt(0)).equals("_")) {
				iter.remove();
			}

		}
		logger.info(" filter the database name done");

		String result = gson.toJson(results);
		resp.getWriter().print(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String SelecteddatabaseName = req.getParameter("SelecteddatabaseName"); //$NON-NLS-1$
		String isNew = req.getParameter("isNew"); //$NON-NLS-1$		
		String NewdatabaseName = req.getParameter("NewdatabaseName"); //$NON-NLS-1$
		StringBuffer responseString = new StringBuffer();

		GeoDBReader geoDBReader = GeoDBReader.getPostgisInstance();
		responseString.append("Scenario is created");

		Boolean correctedName = true;

		if (Character.toString(NewdatabaseName.charAt(0)).equals("_")) {
			responseString.delete(0, responseString.length());
			responseString
					.append("The first Character should not be _ (an underscore)");
		} else {
			try {
				if (!Boolean.valueOf(isNew)) {// select an existing database
					// check name conflicting
					if (SelecteddatabaseName.equals(NewdatabaseName))
						correctedName = true;
					else {
						List<String> results = GeoDBReader
								.getPostgresTransportNSWInstance()
								.listDatabase();
						Iterator<String> iter = results.iterator();
						while (iter.hasNext()) {
							String s = iter.next();
							if (s.equals(NewdatabaseName)) {
								correctedName = false;
								responseString.delete(0,
										responseString.length());
								responseString
										.append("Name conflicts with system database or existing scenario!\n Try another name please.");
								break;
							}// if
						}// while
					}
					if (correctedName)
						geoDBReader.overwriteDatabase(SelecteddatabaseName,
								"postgres_TransportNSW_ui");
				} else// create a new Scenario
				{
					List<String> results = GeoDBReader
							.getPostgresTransportNSWInstance().listDatabase();
					Iterator<String> iter = results.iterator();
					while (iter.hasNext()) {
						String s = iter.next();
						if (s.equals(NewdatabaseName)) {
							responseString.delete(0, responseString.length());
							responseString
									.append("Name conflicts with system database or existing scenario!\n Try another name please.");
							break;
						}
					}// while
				}// else
			} catch (Exception e) {
				// TODO Auto-generated catch block
				responseString.delete(0, responseString.length());
				responseString.append(e.getMessage());
			}
		}

	
		resp.getWriter().print(responseString);

	}

}
