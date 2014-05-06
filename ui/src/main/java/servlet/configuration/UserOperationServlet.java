package servlet.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import core.ApplicationContextHolder;
import core.EnvironmentConfig;
import hibernate.configuration.RunsDAO;
import hibernate.configuration.RunsEntity;

/**
 * a class fetch and update configuration table
 * 
 * @author jie
 * 
 */
@WebServlet("/UserOperationServlet")
public class UserOperationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger
			.getLogger(UserOperationServlet.class);
	private static JSch jsch;
	private static Session session;
	private RunsDAO runsDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		this.runsDAO = ApplicationContextHolder.getBean(RunsDAO.class);

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// whether start or terminate
		String useroperation = request.getParameter("useroperation"); //$NON-NLS-1$		
		int runsid = Integer.valueOf(request.getParameter("runsid")); //$NON-NLS-1$

		if (useroperation.equals("start")) {
			int vmid = Integer.valueOf(request.getParameter("vmid")); //$NON-NLS-1$
			// set up the running server

			try {
				EnvironmentConfig environmentConfig = ApplicationContextHolder
						.getBean(EnvironmentConfig.class);
				PropertiesConfiguration propertiesConfiguration = null;
				propertiesConfiguration = new PropertiesConfiguration(new File(
						environmentConfig.getConfigPath()
								+ "/environment.properties"));

				// choose the hostname
				if (!decidehostname(vmid)) {
					response.getWriter()
							.write("The VM server is busy. Please wait until it finishes."); //$NON-NLS-1$
				} else {
					String hostname = null;
					hostname = (String) propertiesConfiguration
							.getProperty("runningserver" + String.valueOf(vmid)
									+ ".hostname");

					int port = Integer.valueOf((String) propertiesConfiguration
							.getProperty("runningserver.port"));
					String user = (String) propertiesConfiguration
							.getProperty("runningserver.user");
					String password = (String) propertiesConfiguration
							.getProperty("runningserver.password");

					RunsEntity runsEntity = this.runsDAO.findOne(runsid);
					runsEntity
							.setTimeStart(new Timestamp(new Date().getTime()));
					runsEntity.setTimeFinished(null);
					runsEntity.setHostname(vmid);
					runsEntity.setStatus(1);
					this.runsDAO.update(runsEntity);

					execCmd("./run.sh " + runsid, user, password, hostname,
							port);
					response.getWriter().write("Execute the start command!"); //$NON-NLS-1$
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
				response.getWriter().write(
						"Something wrong when executing the start command!"); //$NON-NLS-1$
			}

		}

		if (useroperation.equals("terminate")) {

			try {
				// set up the running server
				EnvironmentConfig environmentConfig = ApplicationContextHolder
						.getBean(EnvironmentConfig.class);
				PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
						new File(environmentConfig.getConfigPath()
								+ "/environment.properties"));

				// choose the hostname
				RunsEntity runsEntity = this.runsDAO.findOne(runsid);
				int availablehost = runsEntity.getHostname();
				String hostname = null;
				if (availablehost == 1)
					hostname = (String) propertiesConfiguration
							.getProperty("runningserver1.hostname");
				else if (availablehost == 2)
					hostname = (String) propertiesConfiguration
							.getProperty("runningserver2.hostname");
				int port = Integer.valueOf((String) propertiesConfiguration
						.getProperty("runningserver.port"));
				String user = (String) propertiesConfiguration
						.getProperty("runningserver.user");
				String password = (String) propertiesConfiguration
						.getProperty("runningserver.password");

				runsEntity.setHostname(0);
				runsEntity.setStatus(0);
				runsEntity.setTimeFinished(new Timestamp(new Date().getTime()));
				this.runsDAO.update(runsEntity);

				execCmd("./run.sh " + runsid, user, password, hostname, port);
				response.getWriter().write("Execute the terminate command!"); //$NON-NLS-1$
			} catch (Exception e) {
				logger.info(e.getMessage());
				response.getWriter().write(
						"Something wrong when executing the start command!"); //$NON-NLS-1$
			}
		}
	} // end of get

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.info("UserOperationServlet from Post;");
	}// end of post

	private boolean decidehostname(int requestHost) {
		List<Integer> results = this.runsDAO.getListHostnamefromRun();
		if (results.contains(Integer.valueOf(requestHost))) {
			return false;
		}
		return true;

	}

	// additional method
	/**
	 * Connect to an IP address
	 * 
	 * @throws JSchException
	 */
	public static void connect(String user, String passwd, String hostname,
			int port) throws JSchException {
		jsch = new JSch();
		session = jsch.getSession(user, hostname, port);
		session.setPassword(passwd);

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
	}

	/**
	 * execute the command
	 * 
	 * @throws JSchException
	 */
	public static void execCmd(String command, String user, String passwd,
			String hostname, int port) throws JSchException {

		connect(user, passwd, hostname, port);

		if (session == null) {
			throw new RuntimeException("Session is null!");
		}

		if (command != null) {
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect();

			try (InputStream in = channel.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in))) {

				String b = "";
				StringBuffer buffer = new StringBuffer();

				while ((b = reader.readLine()) != null) {
					buffer.append(b);
				}
				logger.info(buffer.toString());

				channel.disconnect();
				session.disconnect();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
