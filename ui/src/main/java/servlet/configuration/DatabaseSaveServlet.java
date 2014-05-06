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
