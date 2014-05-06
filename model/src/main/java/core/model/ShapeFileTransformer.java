package core.model;

/**
 * A class for running a generated batch file to transfer a postgres table to a shapefile
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>qun@uow.edu.au
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import core.ApplicationContextHolder;
import org.apache.log4j.Logger;

import core.ModelMain;
import org.postgresql.jdbc2.optional.PoolingDataSource;

public class ShapeFileTransformer {

    private static final Logger logger = Logger.getLogger(ShapeFileTransformer.class);
	/**
	 *  name="shapeFilePath"
	 */
	private final String shapeFilePath;
	/**
	 *  name="batchPath"
	 */
	private final String batchPath;
	/**
	 *  name="tableName"
	 */
	private final String tableName;
	private final ModelMain main = ModelMain.getInstance();

	public ShapeFileTransformer(String shapeFilePath, String batchPath,
			String tableName) {
		this.shapeFilePath = shapeFilePath;
		this.batchPath = batchPath;
		this.tableName = tableName;
	}

	/**
	 * creates a batch file for transforming a postgres table to a shape file,
	 * then execute this batch file
	 */
	public void run() {
		// TODO Auto-generated method stub
		File batFile = new File(batchPath);
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(batFile));
			bufferedWriter.write("::Author: Qun CHEN qun@uow.edu.au");
			bufferedWriter.newLine();
			bufferedWriter.write("::Time:" + Calendar.getInstance().getTime());
			bufferedWriter.newLine();
			bufferedWriter.write("cd "
					+ main.getConfiguration().getString(
							"File.Directory.PostgreSQL_bin"));
			bufferedWriter.newLine();
			bufferedWriter.write("pgsql2shp -f \"" + shapeFilePath + tableName
					+ "\" -h "
					+ ((PoolingDataSource) ApplicationContextHolder.getBean("sourcePostgis")).getServerName().split(":")[0]
					+ " -u " + ((PoolingDataSource) ApplicationContextHolder.getBean("sourcePostgis")).getUser() + " -P "
					+ ((PoolingDataSource) ApplicationContextHolder.getBean("sourcePostgis")).getPassword()
					+ " postgis \"select * from " + tableName + "\"");
			bufferedWriter.newLine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} finally {
			try {
				if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
                logger.error("Exception caught", e);
			}
		}
		try {
			String line;
			Process p = Runtime.getRuntime().exec(batchPath);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				logger.debug(line);
			}
			input.close();
		} catch (Exception err) {
            logger.error("Exception caught", err);
		}
	}

}
