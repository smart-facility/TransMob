package servlet.test;

/**
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>qun@uow.edu.au
 */
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servlet.BusNetworkLayerServlet;

public class BusNetworkLayerServletTest{

	/**
	 *   name="busNetworkLayerServlet"
	 * @uml.associationEnd  
	 */
	private BusNetworkLayerServlet busNetworkLayerServlet;
	@Before
	public void setUp() throws Exception {
		busNetworkLayerServlet=new BusNetworkLayerServlet();
	}

	@After
	public void tearDown() throws Exception {
		busNetworkLayerServlet=null;
	}

	@Test
	public void testProcess() {
		
		DefaultConfigurationBuilder defaultConfigurationBuilder=null;
		Configuration config=null;
		try {
			defaultConfigurationBuilder = new DefaultConfigurationBuilder("config.xml");
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			config = defaultConfigurationBuilder.getConfiguration();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Properties routeNode=config.getProperties("RouteNodes");
		
		String roadIDString="6809; 6810";

		Integer[] results = {356854, 2059183};

		List<Integer> resultList=Arrays.asList(results);

		HashSet<Integer> resultHashSet=new HashSet<Integer>(resultList);

		assertEquals(resultHashSet, busNetworkLayerServlet.process(roadIDString));

	}

//	@Test
	public void testWriteNewBusRoute(){
		String fileName="C:/Transims/my DOC/TRANSIMS5/RandwickSim/inputs/Route_Nodes.txt";
		
		Integer[] results = { 470073, 463289, 15545, 15986, 15546, 348933};
		List<Integer> resultList=Arrays.asList(results);
		LinkedHashSet<Integer> resultSet=new LinkedHashSet<Integer>(resultList);		
		busNetworkLayerServlet.writeNewBusRoute(fileName, resultSet);
	
		try {
			Scanner scanner=new Scanner(new File(fileName));
			StringBuffer stringBuffer=new StringBuffer();
			while (scanner.hasNext()) {
				stringBuffer.replace(0, stringBuffer.length(), scanner.nextLine());
			}
			assertEquals(348933, Integer.parseInt(stringBuffer.toString().split("	")[1]));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
