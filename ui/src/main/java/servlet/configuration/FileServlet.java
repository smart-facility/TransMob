package servlet.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import core.ApplicationContextHolder;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

//copy dwelling._2006 from '/temp/d2006.csv' delimiters ',' csv;
//copy (select * from dwelling_2006) to '/temp/d2006.csv' with csv;

/**
 * Servlet implementation class File
 */
@WebServlet("/FileServlet")
public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FileServlet.class);
    private final JdbcTemplate jdbcTemplate;
	private DataSource datasource;

    /**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileServlet() {
		super();
        this.datasource = (DataSource) ApplicationContextHolder.getBean("sourcePostgres");
		this.jdbcTemplate = new JdbcTemplate(this.datasource);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		

		String tableName = request.getParameter("tableName");
		String FileName = tableName+ ".csv";			
		logger.info("select from " + tableName +"  in FileServlet"); 
		List<?> rows = this.jdbcTemplate.queryForList("select * from " + tableName);
		
		
		//Exporting vector to csv file
		StringBuffer buf = new StringBuffer();
		Iterator<?> it = rows.iterator();	
		 switch (tableName) {
         case "life_event.birth_probability":  //$NON-NLS-1$
        	 buf.append("age, first_child, second_child, third_child, fourth_child,fifth_child, six_or_more \n");
        	 while(it.hasNext()) { 
     			Map<?, ?> userMap = (Map<?,?>) it.next(); 
     			buf.append ( userMap.get("age") + "," + userMap.get("first_child") +"," + userMap.get("second_child") +","+
     			userMap.get("third_child") +","+ userMap.get("fourth_child") +","+ userMap.get("fifth_child") +","+ userMap.get("six_or_more") + "\n");
     			}  	
             break;
         case "public.weights":  //$NON-NLS-1$
        	 buf.append( "category, gender, age, income, home,neighborhood, services, entertainment, work_and_education, transport \n");
        	 while(it.hasNext()) { 
     			Map<?, ?> userMap = (Map<?,?>) it.next(); 
     			buf.append( userMap.get("category") + "," + userMap.get("gender") +"," + userMap.get("age") +","+
     			userMap.get("income") +","+ userMap.get("home") +","+ userMap.get("neighborhood") +","+ userMap.get("services") +"," +
     			userMap.get("entertainment") +","+ userMap.get("work_and_education") +","+ userMap.get("transport") +	"\n");
     			}  	
             break;
         case "public.transims_activity_location":  //$NON-NLS-1$
        	 buf.append( "facility_id, activity_location, name, suburb , x_coord,y_coord, type, hotspot_id, note_bus, note_train, travel_zone, note_entry \n");
        	 while(it.hasNext()) { 
     			Map<?, ?> userMap = (Map<?,?>) it.next(); 
     			buf.append( userMap.get("facility_id") + "," + userMap.get("activity_location") +"," + userMap.get("name") +","+
     			userMap.get("suburb") +","+ userMap.get("x_coord") +","+ userMap.get("y_coord") +","+ userMap.get("type") +"," +
     			userMap.get("hotspot_id") +","+ userMap.get("note_bus") +","+ userMap.get("note_train") +"," +	
     			userMap.get("travel_zone") +","+ userMap.get("note_entry") + "\n");
     			}  	
             break;    
         case "public.immigration_rate":  //$NON-NLS-1$
        	 buf.append( "year, hf1, hf2, hf3, hf4,hf5, hf6, hf7, hf8, hf9, hf10, hf11, hf12, hf13, hf14, hf15,hf16, nf \n");
        	 while(it.hasNext()) { 
     			Map<?, ?> userMap = (Map<?,?>) it.next(); 
     			buf.append( userMap.get("year") + "," + userMap.get("hf1") +"," + userMap.get("hf2") +","+
     			userMap.get("hf3") +","+ userMap.get("hf4") +","+ userMap.get("hf5") +"," +
     			userMap.get("hf6") +","+ userMap.get("hf7") +","+ userMap.get("hf8") +","+ userMap.get("hf9") +"," +
     			userMap.get("hf10") +","+ userMap.get("hf11") +","+ userMap.get("hf12") +","+ userMap.get("hf13") +"," +
     			userMap.get("hf14") +","+ userMap.get("hf15") +","+ userMap.get("hf16") +"," + userMap.get("nf") +	"\n");
     			}  	
             break;
             
         default:  //$NON-NLS-1$
        	 buf.append( "age, probability \n");
        	 while(it.hasNext()) { 
     			Map<?, ?> userMap = (Map<?,?>) it.next(); 
     			buf.append(userMap.get("age") + "," + userMap.get("probability") + "\n");
     			}  	
             break;
     }//end of switch
		String strOut = buf.toString();
				
				
		//***** Output strOut to Response ******
		response.reset(); // Reset the response
		response.setContentType("application/octet-stream;charset=GB2312"); // the encoding of this example is GB2312
		response.setHeader("Content-Disposition","attachment; filename=\"" + FileName + "\"");
		logger.info("export the data from" + tableName + " in the FileServlet"); 

		PrintWriter out;
		try
		{
			out = response.getWriter();
			out.write(strOut);
			}
		catch (IOException e)
		{
			e.printStackTrace();
			}			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// read the content from the local file	
		FileItemFactory factory = new DiskFileItemFactory();
		String temp_tableName = "";
		List<List<String>> tempdata =  new ArrayList<>();
		
		// Configure a repository (to ensure a secure temp location is used)
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		((DiskFileItemFactory) factory).setRepository(repository);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// Parse the request
		try {
			List<FileItem> items = upload.parseRequest(request);			
			//System.out.println(items.toString());
			// Process the uploaded items
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField() && item.getFieldName().equals("tableName") )
					temp_tableName = item.getString();                  			
				else 
					tempdata = readfromFile(item);
				}// end of while
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		final List<List<String>> csvData = tempdata; 
		logger.debug(csvData.size());
		        
		// delete the old table			    
		final String  tableName = temp_tableName;
		String deleteSql = "DELETE FROM " + tableName;	
		logger.info("Delete SQL is: " + deleteSql);
		String responseString = null;
	
		String insertSqlString = null;	
		switch (tableName) {
			case "life_event.birth_probability":  //$NON-NLS-1$
				insertSqlString =  "INSERT INTO "
						+ tableName	+ " (age, first_child, second_child, third_child, fourth_child,fifth_child, six_or_more) VALUES (?,?,?,?,?,?,?);";
				break;
				
			 case "public.weights":  //$NON-NLS-1$
				 insertSqlString =  "INSERT INTO "
							+ tableName	+ " (category, gender, age, income, home,neighborhood, services, entertainment, work_and_education, transport) VALUES (?,?,?,?,?,?,?,?,?,?);";
	             break;

			 case "public.immigration_rate":  //$NON-NLS-1$
				 insertSqlString = "INSERT INTO "
						 + tableName	+ " (year, hf1, hf2, hf3, hf4,hf5, hf6, hf7, hf8, hf9, hf10, hf11, hf12, hf13, hf14, hf15,hf16, nf ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
				 break;
	        	 
			default:  //$NON-NLS-1$
				insertSqlString =  "INSERT INTO "
						+ tableName  + " (age, probability) VALUES (?,?);";  	
				break;
				}//end of switch
		
		try {
			jdbcTemplate.update(deleteSql);
			this.jdbcTemplate.batchUpdate(insertSqlString, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {					
					List<String> lineofContent = ( List<String>) csvData.get(i);
					switch (tableName) {
						case "life_event.birth_probability":  //$NON-NLS-1$		
							ps.setInt(1, Integer.parseInt(lineofContent.get(0)));                	
		                	ps.setDouble(2, Double.parseDouble(lineofContent.get(1)));
							ps.setDouble(3, Double.parseDouble(lineofContent.get(2)));
							ps.setDouble(4, Double.parseDouble(lineofContent.get(3)));
							ps.setDouble(5, Double.parseDouble(lineofContent.get(4)));
							ps.setDouble(6, Double.parseDouble(lineofContent.get(5)));
							ps.setDouble(7, Double.parseDouble(lineofContent.get(6)));							
							break;
						case "public.weights":  //$NON-NLS-1$		
							ps.setInt(1, Integer.parseInt(lineofContent.get(0)));                	
		                	ps.setInt(2, Integer.parseInt(lineofContent.get(1)));
							ps.setInt(3, Integer.parseInt(lineofContent.get(2)));
							ps.setInt(4, Integer.parseInt(lineofContent.get(3)));
							ps.setDouble(5, Double.parseDouble(lineofContent.get(4)));
							ps.setDouble(6, Double.parseDouble(lineofContent.get(5)));
							ps.setDouble(7, Double.parseDouble(lineofContent.get(6)));
							ps.setDouble(8, Double.parseDouble(lineofContent.get(7)));		
							ps.setDouble(9, Double.parseDouble(lineofContent.get(8)));		
							ps.setDouble(10, Double.parseDouble(lineofContent.get(9)));		
							break;
						case "public.immigration_rate":  //$NON-NLS-1$
							ps.setInt(1, Integer.parseInt(lineofContent.get(0)));                	
		                	ps.setInt(2, Integer.parseInt(lineofContent.get(1)));
		                	ps.setInt(3, Integer.parseInt(lineofContent.get(2)));                	
		                	ps.setInt(4, Integer.parseInt(lineofContent.get(3)));
		                	ps.setInt(5, Integer.parseInt(lineofContent.get(4)));
		                	ps.setInt(6, Integer.parseInt(lineofContent.get(5)));
							ps.setInt(7, Integer.parseInt(lineofContent.get(6)));
							ps.setInt(8, Integer.parseInt(lineofContent.get(7)));		
							ps.setInt(9, Integer.parseInt(lineofContent.get(8)));
							ps.setInt(10, Integer.parseInt(lineofContent.get(9)));
							ps.setInt(11, Integer.parseInt(lineofContent.get(10)));
							ps.setInt(12, Integer.parseInt(lineofContent.get(11)));
							ps.setInt(13, Integer.parseInt(lineofContent.get(12)));
							ps.setInt(14, Integer.parseInt(lineofContent.get(13)));
							ps.setInt(15, Integer.parseInt(lineofContent.get(14)));
							ps.setInt(16, Integer.parseInt(lineofContent.get(15)));
							ps.setInt(17, Integer.parseInt(lineofContent.get(16)));
							ps.setInt(18, Integer.parseInt(lineofContent.get(17)));
						break;
						default:  //$NON-NLS-1$		
							ps.setInt(1, Integer.parseInt(lineofContent.get(0)));                	
		                	ps.setDouble(2, Double.parseDouble(lineofContent.get(1)));
							break;			    
							}//end of switch				
								
					}// end of inserting the data				
				
				@Override
				public int getBatchSize() {
					return csvData.size();
					}
				});
			
				
			responseString = "File successfully imported to : " + tableName;
			logger.info(responseString);
			} catch(DataAccessException e) {
				logger.error("Failed to reload table: " + tableName, e);
				responseString = e.getMessage();
				}		        
	        
		response.getWriter().write(responseString); // Write response body.


	}// end of the POST


	private List<List<String>> readfromFile(FileItem item) {
        List<List<String>> sheetData = new ArrayList<>();
        BufferedReader br = null;
        try {
            // create BufferedReader to read csv file containing data
        	InputStream inStream = item.getInputStream();
        	br = new BufferedReader(new InputStreamReader(inStream));
            br.readLine();
            String strLine = "";
            StringTokenizer st = null;
            // read comma separated file line by line
            while ((strLine = br.readLine()) != null) {
                // break comma separated line using ","
                st = new StringTokenizer(strLine, ",");
                ArrayList<String> data = new ArrayList<String>();
                while (st.hasMoreTokens()) {
                    // display csv file
                    String editedValue = String.valueOf(st.nextToken()).replace("\"", "").trim();
                    data.add(editedValue);
                }
                sheetData.add(data);
            }
        } catch (Exception e) {
            logger.error("Exception while reading csv file: ", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                logger.error("Exception while closing csv file: ", e);
            }
        }        
        return sheetData;
    } // end of readfromFile
	
}
