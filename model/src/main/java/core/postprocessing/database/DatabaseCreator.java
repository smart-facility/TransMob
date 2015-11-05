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
package core.postprocessing.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import core.postprocessing.file.TimePlanFileProcessor;
import core.synthetic.travel.mode.TravelModes;
import core.synthetic.traveldiary.TravelDiaryColumns;

import org.apache.log4j.Logger;
import org.postgresql.jdbc2.optional.PoolingDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import core.ApplicationContextHolder;
import core.HardcodedData;
import core.ModelMain;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;


/**
 * A class for creating a new database and related tables by input a database
 * name
 * 
 * @author qun
 * 
 */ 
public class DatabaseCreator {

    private final DataSourceTransactionManager transactionManager;

    private final PoolingDataSource datasource;

    private static final Logger logger = Logger.getLogger(DatabaseCreator.class);
	private final ModelMain main = ModelMain.getInstance();

    private JdbcTemplate jdbcTemplate;
    private int travelId;

    public DatabaseCreator() {
        this.datasource = (PoolingDataSource) ApplicationContextHolder.getBean("sourceOutput");
        this.transactionManager =  (DataSourceTransactionManager) ApplicationContextHolder.getBean("transactionManagerOutput");
    }

    /**
	 * Creates a new database by input a scenario name
	 * 
	 * @return a flag indicate whether the new database created
	 */
	public JdbcTemplate createDatabase() {

		Statement stm = null;
		Connection con = null;
		String url = "jdbc:postgresql://" + this.datasource.getServerName() + "/?user=" + this.datasource.getUser() + "&password=" + this.datasource.getPassword(); 
		String databaseName = this.datasource.getDatabaseName() + "_";
		try {
			con = DriverManager.getConnection(url);
			
			
			Date date = new Date();

			stm = con.createStatement();
			databaseName += this.main.getSelectedDatabaseName().toLowerCase()
					+ "_"
					+ this.main.getAgentNumber()
					+ "_"
					+ this.main.getSeed()
					+ "_"
					+ this.main.getScenarioName()
					+ "_"
					+ new Timestamp(date.getTime()).toString().replaceAll(
							"[^0-9]", "");
			this.main.setOutputDatabaseName(databaseName);
			

			logger.info("Creating database with name: " + databaseName);

			String sql = "CREATE DATABASE " + databaseName;
			stm.executeUpdate(sql);

            this.datasource.setDatabaseName(databaseName);

            // Create our JdbcTemplate.
            this.jdbcTemplate = new JdbcTemplate(this.datasource);

		} catch (SQLException e) {
            logger.error("Exception caught", e);
            System.exit(1);
		} finally {
			if (stm != null) {
				try {
					stm.close();
				} catch (SQLException e) {
                    logger.error("Failed to close DB resource.", e);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
                    logger.error("Failed to close DB resource.", e);
				}
			}
		}

		return this.jdbcTemplate;

	}


	public boolean createTravelModeChoiseOutputTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE travelmode_choice_output_" 
						+ year
						+ "(travel_id serial NOT NULL," +
						"individual_id text, " +
						"hhold_id text," +
						"travel_mode text," +
						"purpose text," +
						"origin text," +
						"destination text," +
						"old_travel_mode text," +
						"CONSTRAINT mode_pk" + year
						+ " PRIMARY KEY (travel_id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create travelmode choice tables.", exception);
			System.exit(1);
		}

		return isCreated;

	}
	
//	public boolean createTravelModeChoiseOutputTable(int startYear, int numberOfYear) {
//		boolean isCreated = false;
//
//		try {
//			int year = startYear;
//
//			/* simulation years */
//			for (int i = 0; i <= numberOfYear; i++) {
//
//			    String	sql =" CREATE TABLE travelmode_choice_output_" 
//						+ year
//						+ "(travel_id integer NOT NULL,individual_id integer NOT NULL, hhold_id integer,travel_mode character varying(20),purpose text,origin integer,destination integer,old_travel_mode character varying(20),CONSTRAINT mode_pk" 
//						+ year
//						+ " PRIMARY KEY (travel_id))WITH (OIDS=FALSE);"; 
//
//                this.jdbcTemplate.execute(sql);
//				year++;
//			}
//
//			isCreated = true;
//
//		} catch (DataAccessException exception) {
//			logger.error("Unable to create travelmode choice tables.", exception);
//		}
//
//		return isCreated;
//
//	}

	/**
	 * Creates some synthetic population output table in the postgresql.
	 * 
	 * @param startYear
	 *            the table suffix, like: synthetic_population_output_2006
	 * @param numberOfYear
	 *            number of tables in the database
	 * @return a flag indicate whether the new database created
	 */
	public boolean createSyntheticPopulationOutputTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql = " CREATE TABLE synthetic_population_output_" 
						+ year
						+ "(id integer NOT NULL,age integer,income integer,household_relationship character varying(20),household_id integer,household_type character varying(10),gender integer,satisfaction double precision,tz_2006 integer,tenure integer,CONSTRAINT pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE); ALTER TABLE synthetic_population_output_" 
						+ year + " OWNER TO postgres;"; 
				year++;

				this.jdbcTemplate.execute(sql);
			}

			isCreated = true;

		} catch (DataAccessException exception) {
            logger.error("Unable to create synthetic population output tables.", exception);
            System.exit(1);
		}

		return isCreated;

	}

	public boolean createDwellingOccupancyCountsTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

				String sql = "CREATE TABLE dwelling_occupancy_counts_" 
						+ year
						+ "(tz integer,_1bd integer,_2bd integer,_3bd integer,_4bd integer,CONSTRAINT _1bd_count_check CHECK (_1bd >= 0::integer),CONSTRAINT _2bd_rate_check CHECK (_2bd >= 0::integer),CONSTRAINT _3bd_rate_check CHECK (_3bd >= 0::integer),CONSTRAINT _4bd_rate_check CHECK (_4bd >= 0::integer))WITH (OIDS=FALSE);ALTER TABLE dwelling_occupancy_counts_" + year + " OWNER TO postgres;"; 

				year++;

				this.jdbcTemplate.execute(sql);
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create dwelling occupancy rate tables", exception);
			System.exit(1);
		}

		return isCreated;

	}

	
	/**
	 * 
	 * @param startYear
	 * @param numberOfYear
	 * @return
	 */
	public boolean createDeadPeopleOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;
		
		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE test_deadpeople_" 
						+ year
						+ "(id SERIAL, " +
						"indiv_id text, " +
						"age text, " +
						"hhold_id text, " +
						"hhold_relationship text, " +
						"gender text, " +
						" CONSTRAINT deadpeople_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_deadPeople tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	
	/**
	 * 
	 * @param startYear
	 * @param numberOfYear
	 * @return
	 */
	//indiv_id, gender, hhold_id
	public boolean createNewbornsOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;
		
		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE test_newborns_" 
						+ year
						+ "(id SERIAL, " +
						"indiv_id text, " +
						"gender text, " +
						"hhold_id text, " +
						" CONSTRAINT newborns_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_newborns tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	
	/**
	 * 
	 * @param startYear
	 * @param numberOfYear
	 * @return
	 */
	public boolean createImmigrantOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;
		
		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE test_immigrants_" 
						+ year
						+ "(id SERIAL, " +
						"indiv_id text, " +
						"age text, " +
						"gender text, " +
						"hhold_relationship text, " +
						"hhold_id text, " +
						"hhold_type text, " +
						" CONSTRAINT immigrants_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_immigrants tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	/**
	 * 
	 * @param startYear
	 * @param numberOfYear
	 * @return
	 */
	public boolean createEmigrantOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;
		
		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE test_emigrants_" 
						+ year
						+ "(id SERIAL, " +
						"indiv_id text, " +
						"age text, " +
						"gender text, " +
						"hhold_id text, " +
						"hhold_type text, " +
						"nbr_needed text, " +
						"hhold_income text, " +
						"ownership text, " +
						"year_dwelling_bought text, " +
						"CONSTRAINT emigrants_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_emigrants tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	
	/**
	 * 
	 * @param startYear
	 * @param numberOfYear
	 * @return
	 */
	public boolean createEquityOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;
		
		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE test_equity_" 
						+ year
						+ "(id SERIAL, " +
						"hhold_id text, " +
						"hhold_type text, " +
						"hhold_income text, " +
						"ownership text, " +
						"savings_if_rent text, " + 
						"year_dwelling_bought text, " +
						"equity text, " +
						"yearly_mortgage text, " +
						"dwelling_price text, " +
						"duty_paid text, " +
						"CONSTRAINT equity_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_equity tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	
	/**
	 * 
	 * @param startYear
	 * @param numberOfYear
	 * @return
	 */
	public boolean createNotRelocHHOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql = " CREATE TABLE test_notreloc_hhold_" 
						+ year
						+ "(id SERIAL, " +
						"hhold_id text, " +
						"nbr_needed text, " +
						"nbr_having text, " +
						"CONSTRAINT notreloc_hhold_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_notreloc_hhold tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	/**
	 * 
	 * @param startYear
	 * @param numberOfYear
	 * @return
	 */
	public boolean createRelocHHOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;
		
		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql = " CREATE TABLE test_reloc_hhold_" 
						+ year
						+ "(id SERIAL, " +
						"order_reloc text, " +
						"hhold_id text, " +
						"nbr_needed text, " +
						"tz_relocated text, " +
						"CONSTRAINT reloc_hhold_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_reloc_hhold tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	public boolean createSPAfterRelocOutputTable(int startYear, int numberOfYear){
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql = " CREATE TABLE test_sp_after_reloc_" 
						+ year
						+ "(id SERIAL, " +
						"indiv_id text, " +
						"age text, " +
						"gender text, " +
						"indiv_income text, " +
						"hhold_relationship text, " + 
						"hhold_id text, " + 
						"hhold_type text, " + 
						"tenure text, " +
						"nbr_needed text, " +
						"ownership text, " +
						"tz text, " +
						"nbr_having text, " +
						"CONSTRAINT sp_after_reloc_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create test_sp_after_reloc tables.", exception);
			System.exit(1);
		}
		
		return isCreated;
	}
	
	
	public boolean createRemovedSyntheticPopulationOutputTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

				String sql = " CREATE TABLE removed_synthetic_population_output_" 
						+ year
						+ "(id integer NOT NULL,age integer,income integer,household_relationship character varying(20),household_id integer,household_type character varying(10),gender integer,satisfaction double precision,tz_2006 integer,tenure integer,CONSTRAINT removed_synthetic_population_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE); ALTER TABLE removed_synthetic_population_output_" 
						+ year + " OWNER TO postgres;"; 
				// stm.addBatch(sql.toString());
				year++;

				this.jdbcTemplate.execute(sql);
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create removed synthetic population output tables", exception);
			System.exit(1);
		}

		return isCreated;

	}

	public boolean createDeadSyntheticPopulationOutputTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

				String sql = " CREATE TABLE dead_synthetic_population_output_" 
						+ year
						+ "(id integer NOT NULL,age integer,income integer,household_relationship character varying(20),household_id integer,household_type character varying(10),gender integer,satisfaction double precision,tz_2006 integer,tenure integer,CONSTRAINT dead_synthetic_population_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE); ALTER TABLE dead_synthetic_population_output_" 
						+ year + " OWNER TO postgres;"; 
				year++;

				this.jdbcTemplate.execute(sql);
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create dead synthetic poplulation output tables.", exception);
			System.exit(1);
		}

		return isCreated;

	}

	/**
	 * Creates some travel zone output table in the postgresql.
	 * 
	 * @param startYear
	 *            the table suffix, like: travel_zone_output_2006
	 * @param numberOfYear
	 *            number of tables in the database
	 * @return a flag indicate whether the new database created
	 */
	public boolean createTravelZoneOutputTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {

			for (int i = 0; i <= numberOfYear; i++) {
				String tableName = "travel_zone_output_" + (startYear + i); 
				String sql = " CREATE TABLE " 
						+ tableName
						+ " (tz_2006 integer NOT NULL,work_trips_time double precision,satisfaction double precision,CONSTRAINT " 
						+ tableName
						+ "_pkey PRIMARY KEY (tz_2006))WITH (OIDS=FALSE);ALTER TABLE " 
						+ tableName + " OWNER TO postgres;"; 

				this.jdbcTemplate.execute(sql);

				String insertSqlString = "INSERT INTO " 
						+ tableName
						+ " (tz_2006, work_trips_time,satisfaction) VALUES (?,?,?);"; 

                this.jdbcTemplate.batchUpdate(insertSqlString, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        int travelZone = HardcodedData.travelZones[i];
                        ps.setInt(1, travelZone);
                        ps.setDouble(2, 0);
                        ps.setDouble(3, 0);
                    }

                    @Override
                    public int getBatchSize() {
                        return HardcodedData.travelZones.length;
                    }
                });
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create travel zone tables", exception);
			System.exit(1);
		}

		return isCreated;
	}

	/**
	 * Updates trips mode in Travel diaries to a travelmode_choice_output table
	 * 
	 * travelDiaries columns are:
	 * [travel_id][individual_id][household_id][age][gender][income]
	 * [origin][destination][start_time][end_time][duration]
	 * [travel_mode][purpose][vehicle_id][trip_id]
	 * 
	 * @param year
	 *            : the current year
	 */
	public void insertTripsMode(int year) {

        String tableName = "travelmode_choice_output_" + year;
       
        final String sqlInsert = "INSERT INTO "
                + tableName
                + " (travel_id, individual_id, hhold_id, travel_mode, purpose, origin, destination, old_travel_mode) VALUES (?,?,?,?,?,?,?,?);";


        try {
            // Perform this in a transaction so that we can roll it back if necessary.
            TransactionTemplate transTemplate = new TransactionTemplate(transactionManager);
            transTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // Reset our travel Id index. This is needed as the inner classes need access to the ID value.
                    resetTravelId();

                    for (Household household : main.getHouseholdPool().getHouseholds().values()) {

                        // Gather all travel diaries for a household and send them to the DB as a batch.
                        final List<int[]> houseHoldDiaries = new ArrayList<>();

                        for (Individual individual : household.getResidents()) {
                            final int[][] diaries = individual.getTravelDiariesWeekdays();
                            if (diaries == null) {
                                continue;
                            }

                            for (int itd = 0; itd <= diaries.length - 1; itd++) {
                                //Only save the diaries where the person hasn't stayed at home
                                if (diaries[itd][6]!=-1 //origin
                                        && diaries[itd][7]!=-1 //destination
                                        && diaries[itd][8]!=-1 //start_time
                                        && diaries[itd][9]!=-1 //end_time
                                        && diaries[itd][10]!=-1 //duration
                                        && diaries[itd][11]!=-1 ) {//travel_mode
                                	
                                    houseHoldDiaries.add(diaries[itd]);
                                }
                                
                            }
                        }

                        try {
                            // batch update operation.
                            jdbcTemplate.batchUpdate(sqlInsert, new BatchPreparedStatementSetter() {
                                @Override
                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                    int [] diary = houseHoldDiaries.get(i);
                                    final String hHoldPersonTripTD = String.valueOf(diary[2]) + "_" + String.valueOf(diary[1]) + "_" + String.valueOf(diary[14]);
                                    ps.setInt(1, getNextTravelId());
                                    ps.setInt(2, diary[1]);
                                    ps.setInt(3, diary[2]);
                                    ps.setString(4, TravelModes.classify(diary[11]).getTranssimsName());
                                    ps.setString(5, TimePlanFileProcessor.getPurposeString(diary[TravelDiaryColumns.Purpose_Col.getIntValue()]));
                                    ps.setInt(6, diary[6]);
                                    ps.setInt(7, diary[7]);
                                    if (ModelMain.getHhppTripModeBeforeTMC().containsKey(hHoldPersonTripTD)) {
                                    	ps.setString(8,TravelModes.classify(ModelMain.getHhppTripModeBeforeTMC().get(hHoldPersonTripTD)).getTranssimsName());
                                    } else {
                                    	ps.setString(8, TravelModes.classify(diary[11]).getTranssimsName());
                                    }
                                    
                                }

                                @Override
                                public int getBatchSize() {
                                    return houseHoldDiaries.size();
                                }
                            });
                        } catch (Exception e) {
                            logger.error("Failed to update diaries.", e);
                            status.setRollbackOnly();
                            System.exit(1);
                        }
                    }
                }
            });
		} catch (TransactionException exception) {
            logger.error("Failed to update travel mode choice.", exception);
            System.exit(1);
        }

	}

    private void resetTravelId() {
        this.travelId = 0;
    }

    private int getNextTravelId() {
        return ++this.travelId;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public boolean createTransimsOutputTimePlansTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE transims_output_timeplans_" 
						+ year
						+ "(id SERIAL, household text, person text, trip text, mode text, depart text, " +
											"purpose text,trip_length text, CONSTRAINT timeplans_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create timeplans tables.", exception);
			System.exit(1);
		}

		return isCreated;
	}
    
    public boolean createTransimsOutputLinkDelayTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE transims_output_linkdelay_" 
						+ year
						+ "(id SERIAL, link text, dir text, depart text, arrive text, flow text, time text, CONSTRAINT linkdelay_pk"
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create link_delay tables.", exception);
			System.exit(1);
		}

		return isCreated;
	}

	public boolean createTripsMadeTMC(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE trips_made_tmc_" 
						+ year
						+ "(id serial, household text, person text, trip text, old_mode text, new_mode text, CONSTRAINT trips_made_tmc_pk" 
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; 

                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create trips_made_tmc tables.", exception);
			System.exit(1);
		}

		return isCreated;
	}

	public boolean createTravelDiaryTable(int startYear, int numberOfYear) {
		boolean isCreated = false;

		try {
			int year = startYear;

			/* simulation years */
			for (int i = 0; i <= numberOfYear; i++) {

			    String	sql =" CREATE TABLE travel_diary_" //$NON-NLS-1$
						+ year
						+ "(id serial NOT NULL, travel_id text, agen_id text, hhold_id text, age text, gender text, income text, "
						+ "origin text, destination text, start_time text, end_time text, duration text, mode text, purpose text, " 
						+ "vehicle_id text, trip_id text, CONSTRAINT travel_diary_pk" //$NON-NLS-1$
						+ year
						+ " PRIMARY KEY (id))WITH (OIDS=FALSE);"; //$NON-NLS-1$
                this.jdbcTemplate.execute(sql);
				year++;
			}

			isCreated = true;

		} catch (DataAccessException exception) {
			logger.error("Unable to create travel_diary_ tables.", exception);
			System.exit(1);
		}

		return isCreated;		
	}
    
    
    
}
