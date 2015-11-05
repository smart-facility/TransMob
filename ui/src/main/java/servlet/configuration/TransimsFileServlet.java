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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import core.HardcodedData;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class TransimsFileServlet
 */
@WebServlet("/TransimsFileServlet")
public class TransimsFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TransimsFileServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TransimsFileServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String fileName = request.getParameter("fileName"); //$NON-NLS-1$
		String orig = "";

        switch (fileName) {
            case "Vehicle_Type.txt":  //$NON-NLS-1$
                HardcodedData.setVehicleTypeFileLocation("weekdays/"); //$NON-NLS-1$
                orig = HardcodedData.getVehicleTypeFileLocation();
                break;
            case "Transit_Schedule":  //$NON-NLS-1$
                HardcodedData.setTransitScheduleFileLocation("weekdays/"); //$NON-NLS-1$
                orig = HardcodedData.getTransitScheduleFileLocation();
                break;
            case "Transit_Route":  //$NON-NLS-1$
                HardcodedData.setTransitRouteFileLocation("weekdays/"); //$NON-NLS-1$
                orig = HardcodedData.getTransitRouteFileLocation();
                break;
            case "Transit_Stop":  //$NON-NLS-1$
                HardcodedData.setTransitStopFileLocation("weekdays/"); //$NON-NLS-1$
                orig = HardcodedData.getTransitStopFileLocation();
                break;
            case "Link":  //$NON-NLS-1$
                HardcodedData.setLinkFileLocation("weekdays/"); //$NON-NLS-1$
                orig = HardcodedData.getLinkFileLocation();
                break;
            case "Signalized_Node_2":  //$NON-NLS-1$
                HardcodedData.setSignalizedNode2FileLocation("weekdays/"); //$NON-NLS-1$
                orig = HardcodedData.getSignalizedNode2FileLocation();
                break;
        }
        
        logger.info("Get the file ready in TransimsFileServlet ");
        
		String strOut = "";
		if (orig!=null) {
			File fOrig = new File(orig); // get the original content			
			
			try (BufferedReader br =  new BufferedReader(new FileReader(fOrig));) {
				String line = "";
				StringBuffer sb = new StringBuffer((int)orig.length());
				
				while( (line = br.readLine() ) != null ) {
					sb.append(line);
				}
				
				strOut = sb.toString();
			} 
		} // finishing reading the file
		
        
		
        //***** Output strOut to Response ******
		response.reset(); // Reset the response
		response.setContentType("application/octet-stream;charset=GB2312"); // the encoding of this example is GB2312
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		logger.info("export the data from" + fileName + " in the TransimsFileServlet");
		
		try (PrintWriter out = response.getWriter()) {
			out.write(strOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// end of get

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String fileName = "";
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Configure a repository (to ensure a secure temp location is used)
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir"); //$NON-NLS-1$
		((DiskFileItemFactory) factory).setRepository(repository);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		String respondmessage = "";
		
		// Parse the request
		try {
			List<FileItem> items = upload.parseRequest(request);
			InputStream inStream = null;
			
			// Process the uploaded items
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField() && item.getFieldName().equals("fileName") ){
					fileName = item.getString();                  			
				}
				else
				{
					inStream = item.getInputStream();						        	
				}
			}// end of while
		
			try (BufferedReader br = new BufferedReader(new InputStreamReader(inStream))) {
				
				//prepare the destination file
				String dest = "";
				if ("Vehicle_Type.txt".equals(fileName)) { //$NON-NLS-1$
					HardcodedData.setVehicleTypeFileLocation("weekdays/"); //$NON-NLS-1$
					dest = HardcodedData.getVehicleTypeFileLocation();
				} else if ("Transit_Schedule".equals(fileName)) { //$NON-NLS-1$
					HardcodedData.setTransitScheduleFileLocation("weekdays/"); //$NON-NLS-1$
					dest = HardcodedData.getTransitScheduleFileLocation();
				} else if ("Transit_Route".equals(fileName)) { //$NON-NLS-1$
					HardcodedData.setTransitRouteFileLocation("weekdays/"); //$NON-NLS-1$
					dest = HardcodedData.getTransitRouteFileLocation();
				} else if ("Transit_Stop".equals(fileName)) { //$NON-NLS-1$
					HardcodedData.setTransitStopFileLocation("weekdays/"); //$NON-NLS-1$
					dest = HardcodedData.getTransitStopFileLocation();
				} else if ("Link".equals(fileName)) { //$NON-NLS-1$
					HardcodedData.setLinkFileLocation("weekdays/"); //$NON-NLS-1$
					dest = HardcodedData.getLinkFileLocation();
				} else if ("Signalized_Node_2".equals(fileName)) { //$NON-NLS-1$
					HardcodedData.setSignalizedNode2FileLocation("weekdays/"); //$NON-NLS-1$
					dest = HardcodedData.getSignalizedNode2FileLocation();
				}
				
				logger.info("File imported from TransimsFileServlet");
				
				//overwrite
				if (dest!=null) {			
					File fDest = new File(dest);
					
					// copy in the data
					try (BufferedWriter bw = new BufferedWriter(new FileWriter(fDest))) {
						  
						String b = "";            
						while ((b = br.readLine()) != null) {
			                bw.write(b);
			                bw.newLine();
			                bw.flush();  
			            }
						// finish writing the file
						
						response.getWriter().write("File imported from : " + fileName); 
					} catch (Exception e) {
						respondmessage = e.getMessage();
					}
				}else {
					response.getWriter().write("Wrong file"); 
				}
			} catch (Exception e) {
				respondmessage = e.getMessage();
			}
		} catch (Exception e) {
			respondmessage = e.getMessage();
		} 
	} 
	// end of post
}
