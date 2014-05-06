package servlet;


import hibernate.postgres.ProcessLinkDAO;
import hibernate.postgres.ProcessLinkEntity;
import hibernate.postgres.TransimsActivityLocationDAO;
import hibernate.postgres.TransimsActivityLocationEntity;
import hibernate.postgres.TransimsHouseholdLocationDAO;
import hibernate.postgres.TransimsHouseholdLocationEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.postgresql.jdbc2.optional.PoolingDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Joiner;

import core.ApplicationContextHolder;
import core.HardcodedData;
import core.locations.ParkingManager;
import core.model.TextFileHandler;


 


/**
 * A database access class is obligated to select, update, delete and insert
 * information
 * 
 * @author qun
 * 
 */
public class GeoDBReader {

    private static final Logger logger = Logger.getLogger(GeoDBReader.class);


	private static GeoDBReader postgresTransportNSWGeoDBReader;
	private static GeoDBReader postgisGeoDBReader;

	private final JdbcTemplate jdbcTemplate;
	
	private String version_db = "";

	/**
	 * Constructs new instance with GeoDBReader.sourcePostgres or
	 * GeoDBReader.sourcePostgis
	 */
	private GeoDBReader(PoolingDataSource source) {
		this.jdbcTemplate = new JdbcTemplate(source);
	}

	public static synchronized GeoDBReader getPostgisInstance() {
		if (postgisGeoDBReader == null) {

			postgisGeoDBReader = new GeoDBReader(
					(PoolingDataSource) ApplicationContextHolder
							.getBean("sourcePostgis"));
		}

		return postgisGeoDBReader;
	}

	public static synchronized GeoDBReader getPostgresTransportNSWInstance() {
		if (postgresTransportNSWGeoDBReader == null) {

			postgresTransportNSWGeoDBReader = new GeoDBReader(
					(PoolingDataSource) ApplicationContextHolder
							.getBean("sourcePostgres"));

			
			logger.info(((PoolingDataSource) ApplicationContextHolder
							.getBean("sourcePostgres")).getDatabaseName());



		}

		return postgresTransportNSWGeoDBReader;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new CloneNotSupportedException();
	}

	/**
	 * Static factory method return a GeoDBReader.
	 * 
	 * @param source
	 *            the pooling data source will be use
	 * @return a new GeoDBReader instance
	 */
	public static GeoDBReader getInstance(PoolingDataSource source) {
		return new GeoDBReader(source);
	}



	public void csvInsertToDatabase(String tableName, String csvFilePath,
			final String[] columns, final int[] locations, int startRowNumber) {
		String[] nextLine;

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(tableName).append(" ( ")
				.append(Joiner.on(",").join(columns)).append(" ) VALUES ( ");

		String[] values = new String[columns.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = "?";
		}

		sql.append(Joiner.on(",").join(values)).append(" );");

		logger.debug(sql.toString());

		try (CSVReader csvReader= new CSVReader(new BufferedReader(new FileReader(
				new File(csvFilePath))))){

			for (int i = 1; i < startRowNumber; i++) {
				csvReader.readNext();
			}

			while ((nextLine = csvReader.readNext()) != null) {

				final String[] line = nextLine;

				this.jdbcTemplate.update(sql.toString(),
						new PreparedStatementSetter() {
							@Override
							public void setValues(PreparedStatement ps)
									throws SQLException {
								for (int i = 1; i <= columns.length; i++) {
									String string = String
											.valueOf(line[locations[i - 1]]);

									if (isInteger(string)) {
										ps.setObject(i, Integer.valueOf(string));
										continue;
									}

									if (isDouble(string)) {
										ps.setObject(i, Double.valueOf(string));
										continue;
									}

									ps.setObject(i, string);
								}
							}
						});
			}

		} catch (DataAccessException|IOException e) {
			logger.error("Exception caught", e);
		} 
	}

	// ==========================================================================
	/**
	 * Copy one table from another table in a same database.
	 * 
	 * @param oldTableName
	 * @param newTableName
	 * 
	 * @author Vu Lam Cao
	 */
	public void copyTable(String oldTableName, String newTableName) {

		String sqlDelete = "DELETE FROM " + newTableName + ";";
		String sqlCopy = "INSERT INTO " + newTableName + " SELECT * FROM "
				+ oldTableName + ";";

		try {

			this.jdbcTemplate.update(sqlDelete);
			this.jdbcTemplate.update(sqlCopy);
		} catch (DataAccessException e) {
			logger.error("Exception caught", e);
		}
	}

	// ===========================================================================================
	/**
	 * Get the data of household locations and put into database.
	 * 
	 * @author Vu Lam Cao
	 */
	public void setHouseHoldLocation() {

		TransimsHouseholdLocationDAO transimsHouseholdLocationDAO = ApplicationContextHolder
				.getBean(TransimsHouseholdLocationDAO.class);
		/* 1- GET HOUSEHOLD LOCATION */
		List<List<String>> sheetDataHHLocation = TextFileHandler.readCSV(HardcodedData.dwellingFileLocation);

		double[][] hholdLocation = new double[sheetDataHHLocation.size()][10];

		for (int i = 1; i < sheetDataHHLocation.size(); i++) {
			List<String> list = sheetDataHHLocation.get(i);

			for (int j = 0; j < 10; j++) {
				switch (j) {
				case 6:
					if (list.get(j).toLowerCase().contains("near_bus")) {
						hholdLocation[i][j] = 1;
						// flagBus = true;
					} else
						hholdLocation[i][j] = 0;
					break;

				case 7:
					if (list.get(j).toLowerCase().contains("near_train")) {
						hholdLocation[i][j] = 1;
					} else
						hholdLocation[i][j] = 0;
					break;

				case 0:
					hholdLocation[i][j] = Double.parseDouble(list.get(j)) + 1;
					break;

				default:
					hholdLocation[i][j] = Double.parseDouble(list.get(j));
					break;
				}
			}
		}

		/* 4 - CREATE DATABASE */
		try {
			transimsHouseholdLocationDAO.deleteAll();

			List<TransimsHouseholdLocationEntity> entities = new ArrayList<>();

			// Iterates the data and put it into the database.
			for (int h = 1; h < hholdLocation.length; h++) {
				TransimsHouseholdLocationEntity entity = new TransimsHouseholdLocationEntity();
				entity.setDwellingIndex((int) hholdLocation[h][0]);
				entity.setActivityLocation((int) hholdLocation[h][1]);
				entity.setCdCode((int) hholdLocation[h][2]);
				entity.setxCoord(hholdLocation[h][3]);
				entity.setyCoord(hholdLocation[h][4]);
				entity.setBlockId((int) hholdLocation[h][5]);
				entity.setNoteBus((int) hholdLocation[h][6]);
				entity.setNoteTrain((int) hholdLocation[h][7]);
				entity.setHholdIndex((int) hholdLocation[h][8]);
				entity.setTravelZoneId((int) hholdLocation[h][9]);

				entities.add(entity);
			}

			transimsHouseholdLocationDAO.save(entities);

		} catch (Exception e) {
			logger.error("Exception caught", e);
		}

		logger.debug("Finish putting household locations into database");
	}

	// ===========================================================================================
	/**
	 * Get the data of activity locations from csv file and put into database.
	 * 
	 * @author Vu Lam Cao
	 */
	public static void setActivityLocation() {

		List<List<String>> sheetData = TextFileHandler.readCSV(HardcodedData.activityFileLocation);
		TransimsActivityLocationDAO activityDao = ApplicationContextHolder
				.getBean(TransimsActivityLocationDAO.class);

		List<TransimsActivityLocationEntity> activityLocations = new ArrayList<>();
		try {
			// Clear the table
			activityDao.deleteAll();

			// Iterates the data and put it into database.
			for (List<String> list : sheetData) {

				String locationType = list.get(6);
				locationType = locationType.toLowerCase().trim();
				locationType = locationType.replace(" ", "_");
				locationType = locationType.replace("-", "_");
				locationType = locationType.replace("/", "_");
				locationType = locationType.replace("___", "_");

				String activityLocID = list.get(1).trim();

				TransimsActivityLocationEntity entity = new TransimsActivityLocationEntity();
				entity.setFacilityId(Integer.parseInt(list.get(0).trim()));
				entity.setActivityLocation(Integer.parseInt(activityLocID));
				entity.setName(list.get(2).trim());
				entity.setSuburb(list.get(3).trim());
				entity.setxCoord(Double.parseDouble(list.get(4).trim()));
				entity.setyCoord(Double.parseDouble(list.get(5).trim()));
				entity.setType(locationType);
				entity.setHotspotId(Integer.parseInt(list.get(7).trim()));
				entity.setNoteBus(Integer.parseInt(list.get(8).trim()));
				entity.setNoteTrain(Integer.parseInt(list.get(9).trim()));

				activityLocations.add(entity);
			}

			activityDao.save(activityLocations);
			logger.debug("Finish putting activity locations into database");

		} catch (Exception e) {
			logger.error("Exception caught", e);
		}
	}

	
	// ===========================================================================================
	/**
	 * Get the data of process link (.csv file) and put into database.
	 * 
	 * 
	 * @author vlcao
	 */
	@SuppressWarnings("rawtypes")
	public void setProcessLinkToDB() {

		try {

			List sheetData = TextFileHandler.readCSV(HardcodedData.processLinkFileLocation);
			ProcessLinkDAO processLinkDAO = ApplicationContextHolder
					.getBean(ProcessLinkDAO.class);

			// clear the table.
			processLinkDAO.deleteAll();

			// input activity_schedule
			List<ProcessLinkEntity> processLinks = new ArrayList<>();

			/* Iterates the data and put it into the database */
			for (int i = 1; i < sheetData.size(); i++) {
				logger.trace(i);
				List list = (List) sheetData.get(i);

				ProcessLinkEntity processLink = new ProcessLinkEntity();
				processLink.setId(i);
				processLink.setAccess(String.valueOf(list.get(0))); /* access */
				processLink.setFromId(String.valueOf(list.get(1))); /* from_id */
				processLink.setFromType(String.valueOf(list.get(2))); /* from_type */
				processLink.setToId(String.valueOf(list.get(3))); /* to_id */
				processLink.setToType(String.valueOf(list.get(4))); /* to_type */
				processLink.setTime(String.valueOf(list.get(5))); /* time */
				processLink.setCost(String.valueOf(list.get(6))); /* cost */
				processLink.setNotes(String.valueOf(list.get(7))); /* note */
				processLink.setFromDetails(String.valueOf(list.get(8))); /* from_details */
				processLink.setToDetails(String.valueOf(list.get(9))); /* to_details */

				processLinks.add(processLink);

			}

			processLinkDAO.saveAll(processLinks);

			logger.debug("Finish putting process link information into database");

		} catch (Exception e) {
			logger.error("Exception caught", e);
		}

	}

	private boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	
	// ======================================================================
	/**
	 * Create all initial and hard coded tables in the postgres_TransportNSW.
	 * database
	 * 
	 * @author vlcao
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void createDatabaseTables() {
        ParkingManager parkingManager = ParkingManager.getInstance();
		setActivityLocation();
		setProcessLinkToDB();
        parkingManager.setParkingCapacityToDB();
        parkingManager.setParkingAccessToDB();
		setHouseHoldLocation();

		String[] columns = { "activity_id", "hotspot_street_block_id" };
		int[] locations = { 0, 1 };
		int startRowNumber = 2;
		csvInsertToDatabase("activity_hotspots_street_block",
				HardcodedData.activity_hotspot_blockID_FileLocation, columns,
				locations, startRowNumber);
	}

	public void copyDatabase(String oldDatabaseName, String newDatabaseName) {
		
		oldDatabaseName=oldDatabaseName.replace("\"", "");
		newDatabaseName=newDatabaseName.replace("\"", "");
		
		removeAccess(oldDatabaseName);
		removeAccess(newDatabaseName);

		String sql = "CREATE DATABASE \"" + newDatabaseName + "\" WITH TEMPLATE \""
				+ oldDatabaseName + "\" OWNER postgres;";

		try {
			this.jdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			logger.error("Exception caught", e);
			throw e;
		}finally {
			allowConnection(oldDatabaseName);
			allowConnection(newDatabaseName);
		}
		logger.debug(sql);
	}

	private void dropDatabase(String databaseName) {
		
		databaseName=databaseName.replace("\"", "");
		String sql = "Drop DATABASE if exists \"" + databaseName + "\"";

		try {
			this.jdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			logger.error("Exception caught", e);
			throw e;
		}
		logger.debug(sql);
	}

	private void prohibitConnection(String databaseName) {
		String sql = "update pg_database set datallowconn = 'FALSE' where datname = "
				+ databaseName + ";";
		try {

			this.jdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			logger.error("Exception caught", e);
			throw e;
		}
		logger.debug(sql);
	}
	
	private void allowConnection(String databaseName) {
		String sql = "update pg_database set datallowconn = 'True' where datname = \'"
				+ databaseName+ "\';";
		try {

			this.jdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			logger.error("Exception caught", e);
			throw e;
		}
		logger.debug(sql);
	}

	private void forceDisconnection(String databaseName) {
		
// check the db version
		String sqltogetDataBaseVersion = "select version();";
		int majorVerNum=0, minorVerNum=0;
		try {
			String result=jdbcTemplate.queryForObject(sqltogetDataBaseVersion,  String.class);
			String regEx="[\\.0-9]+";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(result.substring(0,20));
			if (m.find()) {
				this.version_db = m.group(0);
			}
			logger.info("db version is " + this.version_db);
			
			majorVerNum = Integer.parseInt(version_db.split("\\.")[0]);
			
			minorVerNum = Integer.parseInt(version_db.split("\\.")[1]);
			}
		catch (DataAccessException e) {
			logger.info("can not get the db version");
			throw e;
			}
				
		String sql = null;
		if ((majorVerNum < 9) || (majorVerNum==9 && minorVerNum<2)) {
			sql = "	SELECT pg_terminate_backend(procpid) FROM pg_stat_activity WHERE datname = "
					+ databaseName + ";";
		} else 
		{
			sql = "	SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE pid <> pg_backend_pid() and datname = "
					+ databaseName + ";";
		}
		
		try {	
			this.jdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			logger.error("Exception caught", e);
			throw e;
		}
		logger.debug(sql);
	}

	public void overwriteDatabase(String origin, String destination) {

		prohibitConnection("\'"+destination+"\'");

		forceDisconnection("\'"+destination+"\'");
		
		dropDatabase(destination);

		prohibitConnection("\'"+origin+"\'");

		forceDisconnection("\'"+origin+"\'");

		
		try {
			copyDatabase("\""+origin+"\"", "\""+destination+"\"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void removeAccess(String destination) {		
		
		prohibitConnection("\'"+destination+"\'");

		forceDisconnection("\'"+destination+"\'");
	}
	
	public List<String> listDatabase() {
		//String sql = "SELECT datname FROM pg_database WHERE datistemplate = false and datname like 'postgres_%'  order by datname";
		String sql = "SELECT datname FROM pg_database WHERE datistemplate = false order by datname";
		List<String> databaseNames = null;

		try {
			databaseNames = this.jdbcTemplate.queryForList(sql, String.class);
		} catch (DataAccessException e) {
			logger.error("Failed to retrieve database names", e);
		}

		List<String> result = new ArrayList<String>();

		//Pattern databaseNamePatternRegexes = Pattern.compile(".*[A-Z].*");
		for (String string : databaseNames) {
			//if (databaseNamePatternRegexes.matcher(string).matches()) {
			//	string = "\"" + string + "\"";
			//}
			result.add(string);
		}

		logger.debug(result.toString());

		return result;
	}
	
	public boolean isValidated() {
		String sql="SELECT 1";
		
		int result=jdbcTemplate.queryForObject(sql, Integer.class);
		
		if (result==1) {
			
			logger.info("connection is valid NOW");
			
			return true;
		}
		
		logger.info("connection is NOT valid NOW");
		
		return false;
	}

	
}