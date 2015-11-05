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



import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;









import core.ApplicationContextHolder;
import core.EnvironmentConfig;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class RunsServlet
 */
public class JudgeServersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JudgeServersServlet.class);

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JudgeServersServlet() {
        super();
        // TODO Auto-generated constructor stub
    }    
    

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

	}


	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		try {
			EnvironmentConfig environmentConfig = ApplicationContextHolder
					.getBean(EnvironmentConfig.class);
			PropertiesConfiguration propertiesConfiguration = null;
			propertiesConfiguration = new PropertiesConfiguration(new File(
					environmentConfig.getConfigPath()
							+ "/environment.properties"));
			
			String NumberServers = (String) propertiesConfiguration
					.getProperty("No.runningservers");
			
			response.getWriter().write(NumberServers); //$NON-NLS-1$
		}catch (Exception e) {
		logger.info(e.getMessage());
		response.getWriter().write(
				"Something wrong with JudgeServersServlet"); //$NON-NLS-1$
		}
	}
		

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		// TODO Auto-generated method stub
		
		

	}

}
