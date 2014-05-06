package servlet.configuration;

import hibernate.configuration.ConfigurationDAO;
import hibernate.configuration.ConfigurationEntity;
import org.apache.log4j.Logger;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import core.ApplicationContextHolder;


/**
 * a class fetch and update configuration table
 * @author qun
 *
 */
@WebServlet("/ConfigurationServlet")
public class ConfigurationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ConfigurationServlet.class);
    private ConfigurationDAO configurationDAO;
	
	

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		configurationDAO = ApplicationContextHolder.getBean(ConfigurationDAO.class);
	}



	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
		String results = gson.toJson(configurationDAO.findAll());
		logger.info("Find all entities from Configuration;");

		response.getWriter().write(results);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String agentNumber = request.getParameter("agentNumber");
		String shapefile = request.getParameter("shapefile");
		String startYear = request.getParameter("startYear");
		String runningYear = request.getParameter("runningYear");
		String nameofscenario = request.getParameter("nameofscenario");
		
		
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setAgentNumber(Integer.parseInt(agentNumber));
        configurationEntity.setShapefile(shapefile);
        configurationEntity.setStartYear(Integer.parseInt(startYear));
        configurationEntity.setRunningYear(Integer.parseInt(runningYear));
        configurationEntity.setNameofscenario(nameofscenario);
        configurationDAO.save(configurationEntity);
		logger.info("Save user's data into Configuration in ConfigurationServlet;");

        
        response.getWriter().print(configurationEntity.getId());
	}

}
