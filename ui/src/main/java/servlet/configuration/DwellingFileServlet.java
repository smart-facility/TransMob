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
package servlet.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.PrintWriter;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import core.ApplicationContextHolder;



//copy dwelling._2006 from '/temp/d2006.csv' delimiters ',' csv;
//copy (select * from dwelling_2006) to '/temp/d2006.csv' with csv;

/**
 * Servlet implementation class File
 */
@WebServlet("/DwellingFileServlet")
public class DwellingFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DwellingFileServlet.class);

	private JdbcTemplate jdbcTemplate;
	private DataSource datasource;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DwellingFileServlet() {
		super();
     
	}
	

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		this.datasource = (DataSource) ApplicationContextHolder.getBean("sourcePostgres");
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException {		
		
		String year = request.getParameter("year");
		String FileName = "dwelling_" + year + ".csv";		
		String tableName ="dwelling._" + year;				
		this.jdbcTemplate = new JdbcTemplate(this.datasource);
		
		logger.info("select from " + tableName +"  in DwellingFileServlet"); 
		List<?> rows = this.jdbcTemplate.queryForList("select * from " + tableName);  
		
		//Exporting vector to csv file		
		StringBuffer buf = new StringBuffer();
        buf.append("tz06, _1bd, _2bd, _3bd, _4bd \n");
        Iterator<?> it = rows.iterator();  
        while(it.hasNext()) {  
            Map userMap = (Map) it.next();  
            buf.append(userMap.get("tz06") + "," + userMap.get("_1bd") +"," + userMap.get("_2bd") +","+
            userMap.get("_3bd") +","+ userMap.get("_4bd") + "\n");
        }                 
        String strOut = buf.toString();

		
		//***** Output strOut to Response ******
		response.reset(); // Reset the response
		response.setContentType("application/octet-stream;charset=GB2312"); // the encoding of this example is GB2312
		response.setHeader("Content-Disposition","attachment; filename=\"" + FileName + "\"");
		logger.info("export the data from" + tableName + " in the DwellingFileServlet"); 

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

		FileItemFactory factory = new DiskFileItemFactory();
		String year = "";
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
				if (item.isFormField() && item.getFieldName().equals("year") )
					year = item.getString();                  			
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
	    this.jdbcTemplate = new JdbcTemplate(this.datasource);
	    String tableName ="dwelling._" + year;		
	    String deleteSql = "DELETE FROM " + tableName;      
        logger.debug("Delete SQL is: " + deleteSql);
	    String responseString = null;
	    
	    String insertSqlString = "INSERT INTO " 
				+ tableName
				+ " (tz06, _1bd,_2bd,_3bd,_4bd) VALUES (?,?,?,?,?);"; 

        try {
			jdbcTemplate.update(deleteSql);
			this.jdbcTemplate.batchUpdate(insertSqlString, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {              
                	List<String> lineofContent = ( List<String>) csvData.get(i);
                	ps.setInt(1, Integer.parseInt(lineofContent.get(0)));
                	ps.setInt(2, Integer.parseInt(lineofContent.get(1)));
                	ps.setInt(3, Integer.parseInt(lineofContent.get(2)));
                	ps.setInt(4, Integer.parseInt(lineofContent.get(3)));
                	ps.setInt(5, Integer.parseInt(lineofContent.get(4)));
                    }

                @Override
                public int getBatchSize() {
                    return csvData.size();
                }
            });
			
			responseString = "File successfully imported to " + tableName + ".csv";
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

