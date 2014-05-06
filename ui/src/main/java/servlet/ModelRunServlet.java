package servlet;

import hibernate.configuration.RunsDAO;
import hibernate.configuration.RunsEntity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.ApplicationContextHolder;

import org.apache.log4j.Logger;


//import core.testprototype.PrototypeTest;

/**
 * a class could update Runs table 
 * @author qun
 *
 */

public class ModelRunServlet extends HttpServlet {

	private final RunsDAO runsDAO;


	private static final long serialVersionUID = 5904478952876082756L;
	private static final Logger logger = Logger.getLogger(ModelRunServlet.class);

	// Rcall rc;
	// Rengine re;
	/**
	 * name="rOperationInJava"
	 * 
	 * @uml.associationEnd
	 */
	// private ROperationInJava rOperationInJava;

	/**
	 * Constructor of the object.
	 */
	public ModelRunServlet() {
		super();
		this.runsDAO = ApplicationContextHolder.getBean(RunsDAO.class);

	}

	/**
	 * Destruction of the servlet. <br>
	 */
	@Override
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// rc.endRengine(re);
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info(" saving the configuration;");
		int ConfigurationId = Integer.parseInt(request.getParameter("ConfigurationId"));
		int seed = Integer.parseInt(request.getParameter("seed"));
		RunsEntity runs = new RunsEntity();
		runs.setSeed(seed);
		runs.setConfigurationId(ConfigurationId);
		runs.setStatus(0);
	    
		this.runsDAO.save(runs);
		response.getWriter().write("Successfully save the configuration");

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	@Override
	public void init() throws ServletException {

	}
}
