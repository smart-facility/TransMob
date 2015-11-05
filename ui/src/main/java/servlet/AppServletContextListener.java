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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import core.ApplicationContextHolder;
/**
 * A class handles notification events about servlet lifecycle changes 
 * @author qun
 *
 */
public class AppServletContextListener implements ServletContextListener {
	

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Initilizes servlet context 
	 * @param arg0
	 * @throws FileNotFoundException if the specified file is not found 
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		final ApplicationContext ctx = new ClassPathXmlApplicationContext("UIapplicationContext.xml");
        final ApplicationContextHolder holder = ctx.getBean(ApplicationContextHolder.class);
	}

	
}
