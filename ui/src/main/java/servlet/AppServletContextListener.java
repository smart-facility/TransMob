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
