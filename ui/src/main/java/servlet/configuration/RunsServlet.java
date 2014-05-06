package servlet.configuration;

import hibernate.configuration.RunsDAO;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import core.ApplicationContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class RunsServlet
 */
public class RunsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RunsDAO runsDAO;
	private static final Logger logger = Logger.getLogger(RunsServlet.class);

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RunsServlet() {
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
		this.runsDAO=ApplicationContextHolder.getBean(RunsDAO.class);

	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();

//		String results = gson.toJson(this.runsDAO.findAll() );
//		[{"seed":0,"status":1,"timeStart":"15/02/2013 9:49:48 AM","configurationId":3,"id":2},{"seed":0,"status":1,"timeStart":"15/02/2013 9:52:23 AM","configurationId":4,"id":3},{"seed":0,"status":1,"timeStart":"15/02/2013 10:11:29 AM","configurationId":5,"id":4},{"seed":0,"status":1,"timeStart":"15/02/2013 10:32:35 AM","configurationId":7,"id":6},{"seed":0,"status":1,"timeStart":"11/07/2013 10:57:49 AM","timeFinished":"11/07/2013 11:21:15 AM","configurationId":6,"id":5},{"seed":0,"status":1,"timeStart":"12/07/2013 11:49:15 AM","timeFinished":"15/02/2013 9:06:12 AM","configurationId":2,"id":1,"nameofscenario":"postgres_TransportNSW_backup"},{"seed":1,"status":1,"configurationId":200,"id":150,"nameofscenario":"\"postgres_TransportNSW_jack\""},{"seed":144,"status":1,"configurationId":250,"id":200,"nameofscenario":"\"postgres_TransportNSW_jack\""},{"seed":2,"status":1,"configurationId":251,"id":201,"nameofscenario":"postgres_TransportNSWert"},{"seed":14,"status":1,"configurationId":300,"id":250,"nameofscenario":"postgres_TransportNSWf"}]
		
		List<Object[]> rows =this.runsDAO.getAllfromRunsAndConfigurationEntity();
		String resdults = gson.toJson( this.runsDAO.getAllfromRunsAndConfigurationEntity() );
		logger.info(resdults);

		logger.info("get All from Runs And Configuration Entity");

		
		List<Map<String,String>> listmap=new ArrayList<Map<String,String>>();
		for (Object[] row : rows) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("id", String.valueOf(row[0]));
			map.put("nameofscenario", (String) row[1]);
			map.put("seed",String.valueOf(row[2]));			
			
			int model_status = Integer.parseInt(String.valueOf(row[3]));
			String result4status;
		        switch (model_status) {
		            case 0:  result4status = "Model ready to start.";
		                     break;
		            case 1:  result4status = "Model running.";
		                     break;
		            case 2:  result4status = "Model completed, processing of output data commenced.";
		                     break;
		            case 3:  result4status = "Model died with an error.";
		                     break;
		            case 4:  result4status = "Model finished, output data processed.";
		                     break;
		            case 5:  result4status = "Model finished, error in processing output data.";
		                     break;		            
		            default: result4status = "Unknown.";
		                     break;
		        }
		        
			map.put("status",result4status);

			if( row[4] == null)
				map.put("timeStart", "Unknown.");
			else
				map.put("timeStart",  row[4].toString());
			if( row[5] == null)
				map.put("timeFinished",  "Unknown.");
			else
				map.put("timeFinished",  row[5].toString());
			map.put("vmid",String.valueOf(row[6]));	
			if( row[7] == null)
				map.put("nameforoutputdb",  "Unknown.");
			else
				map.put("nameforoutputdb",  row[7].toString());
			listmap.add(map);		
			
		}
		
			
		String formattedresults = gson.toJson(listmap);		
		response.getWriter().write(formattedresults);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		
//		int previousActiveId = Integer.parseInt(request.getParameter("PreviousActiveId"));
//		int activeID = Integer.parseInt(request.getParameter("ActiveID"));
//		RunsEntity runsEntity = this.runsDAO.findOne(previousActiveId);
//		runsEntity.setActive(false);
//		this.runsDAO.update(runsEntity);
//		runsEntity = this.runsDAO.findOne(activeID);
//		runsEntity.setActive(true);
//		this.runsDAO.update(runsEntity);
//		
//        response.getWriter().write(request.getParameter("ActiveID"));
	}

}
