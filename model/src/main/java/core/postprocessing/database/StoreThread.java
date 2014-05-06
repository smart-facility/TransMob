package core.postprocessing.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import core.ApplicationContextHolder;
import core.ModelMain;

import org.apache.log4j.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;


public class StoreThread implements Runnable {

    private static final int BATCH_SIZE = 500;
    private DataSourceTransactionManager transactionManager;

    private int year;

	public void setYear(int year) {
		this.year = year;
	}

	private DatabaseCreator databaseCreator;
	private Map<Integer, Double> normalizedTravelZoneAverageTripsTime;
	private Map<Integer, int[]> occupancyRate;
	private List<String[]> timeplanOutputArrayList;
	private List<String[]> linkDelayOutputArrayList;

	private List<String[]> testImmigrants;
	private List<String[]> testEmigrants;
	private List<String[]> testEquity;
	private List<String[]> testNotRelocHhold;
	private List<String[]> testRelocHhold;
	private List<String[]> testSPAfterReloc;
	private List<String[]> deadPeople;
	private List<String[]> newborns;
	private List<String[]> travelModeChoiceOutput;
	
    private final JdbcTemplate jdbcTemplate;


	public StoreThread(JdbcTemplate jdbcTemplate) {
		super();
        this.jdbcTemplate = jdbcTemplate;
        transactionManager = (DataSourceTransactionManager) ApplicationContextHolder.getBean("transactionManagerOutput");
    }

	public StoreThread(int year,
			DatabaseCreator databaseCreator,
			Map<Integer, Double> normalizedTravelZoneAverageTripsTime,
			Map<Integer, int[]> occupancyRate, List<String[]> timeplanOutputArrayList,
			List<String[]> linkDelayOutputArrayList,
			List<String[]> testImmigrants,
			List<String[]> testEmigrants,
			List<String[]> testEquity,
			List<String[]> testNotrelocHhold,
			List<String[]> testRelocHhold,
			List<String[]> testSPAfterReloc,
			List<String[]> deadPeople,
			List<String[]> newborns,
			List<String[]> travModeChoice) {

		super();
        this.databaseCreator = databaseCreator;
        this.jdbcTemplate = databaseCreator.getJdbcTemplate();
        transactionManager = (DataSourceTransactionManager) ApplicationContextHolder.getBean("transactionManagerOutput");
		this.year = year;
		this.normalizedTravelZoneAverageTripsTime = normalizedTravelZoneAverageTripsTime;
		this.occupancyRate = occupancyRate;
		this.timeplanOutputArrayList = timeplanOutputArrayList;
		this.linkDelayOutputArrayList = linkDelayOutputArrayList;
		this.testImmigrants = testImmigrants;
		this.testEmigrants = testEmigrants;
		this.testEquity = testEquity;
		this.testNotRelocHhold = testNotrelocHhold;
		this.testRelocHhold = testRelocHhold;
		this.testSPAfterReloc = testSPAfterReloc;
		this.deadPeople = deadPeople;
		this.newborns = newborns;
		this.travelModeChoiceOutput = travModeChoice;
	}

	private static final Logger logger = Logger.getLogger(StoreThread.class);

	@Override
	public void run() {
		// stores travel mode selection
//		this.databaseCreator.insertTripsMode(this.year);
		storeTravelModeChoiceOutput();
		
		storeTripsTime();
		storeSatisfaction();
		storeOccupancyCounts(this.occupancyRate, this.year);
		storeTimePlanOutput();
		storeLinkDelayOutput();
		
		storeImmigrants();
		storeEmigrants();
		storeEquity();
		storeHholdNotReloc();
		storeHholdReloc();
		storeSPAfterReloc();
		
		storeDeadPeople();
		storeNewborns();
		
		logger.info("Finish storing information to database for year " + year);
	}

	/**
	 * 
	 */
	private void storeTravelModeChoiceOutput() {
		String tableName = "travelmode_choice_output_" + year;
        String sql = "INSERT INTO "
                + tableName
                + " (individual_id, hhold_id, travel_mode, purpose, origin, destination, old_travel_mode) VALUES (?,?,?,?,?,?,?);";
        
        try {
        	List<List<String []>> travModeBatches = Lists.partition(travelModeChoiceOutput, BATCH_SIZE);

            for (final List<String []> travModeBatch : travModeBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < travModeBatch.get(i).length; j++) {
                            ps.setString(j+1, travModeBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return travModeBatch.size();
                    }
                });
            }
        } catch (DataAccessException e) {
			logger.error("Failed to insert travel mode choice output.", e);
		} finally {
            // Clear data structure
            travelModeChoiceOutput.clear();
        }

	}
	
	public void storeTravelModeChoiceOutput(ArrayList<String[]> travModeChoice, int inputYear) {
		String tableName = "travelmode_choice_output_" + inputYear;
        String sql = "INSERT INTO "
                + tableName
                + " (individual_id, hhold_id, travel_mode, purpose, origin, destination, old_travel_mode) VALUES (?,?,?,?,?,?,?);";
        
        try {
        	List<List<String []>> travModeBatches = Lists.partition(travModeChoice, BATCH_SIZE);

            for (final List<String []> travModeBatch : travModeBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < travModeBatch.get(i).length; j++) {
                            ps.setString(j+1, travModeBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return travModeBatch.size();
                    }
                });
            }
        } catch (DataAccessException e) {
			logger.error("Failed to insert travel mode choice output.", e);
		} 

	}
	
	
	/**
	 * 
	 */
	private void storeNewborns() {
		String sql = "INSERT INTO test_newborns_" + year
				+ " (indiv_id, gender, hhold_id) VALUES (?, ?, ?);";
		
		try {

            List<List<String []>> newbornsBatches = Lists.partition(newborns, BATCH_SIZE);

            for (final List<String []> newbornsBatch : newbornsBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < newbornsBatch.get(i).length; j++) {
                            ps.setString(j+1, newbornsBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return newbornsBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert newborns output.", e);
		} finally {
            // Clear data structure
            newborns.clear();
        }
	}
	
	
	/**
	 * 
	 */
	private void storeDeadPeople() {
		
		
		String sql = "INSERT INTO test_deadpeople_" + year
				+ " (indiv_id, age, hhold_id, hhold_relationship, gender) VALUES (?, ?, ?, ?, ?);";
		
		try {

            List<List<String []>> deadPeopleBatches = Lists.partition(deadPeople, BATCH_SIZE);

            for (final List<String []> deadPeopleBatch : deadPeopleBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < deadPeopleBatch.get(i).length; j++) {
                            ps.setString(j+1, deadPeopleBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return deadPeopleBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert deadpeople output.", e);
		} finally {
            // Clear data structure
            deadPeople.clear();
        }

	}


	/**
	 * 
	 */
	private void storeImmigrants() {
		String sql = "INSERT INTO test_immigrants_" + year
				+ " (indiv_id, age, gender, hhold_relationship, hhold_id, hhold_type) VALUES (?, ?, ?, ?, ?, ?);";

		try {

            List<List<String []>> immigrantBatches = Lists.partition(testImmigrants, BATCH_SIZE);

            for (final List<String []> immigrantBatch : immigrantBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < immigrantBatch.get(i).length; j++) {
                            ps.setString(j+1, immigrantBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return immigrantBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert immigrants output.", e);
		} finally {
            // Clear data structure
            testImmigrants.clear();
        }
	}
	
	/**
	 * 
	 */
	private void storeEmigrants() {
		String sql = "INSERT INTO test_emigrants_" + year
				+ " (indiv_id, age, gender, hhold_id, hhold_type, nbr_needed, hhold_income, ownership, year_dwelling_bought) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

		try {

            List<List<String[]>> emigrantBatches = Lists.partition(testEmigrants, BATCH_SIZE);

            for (final List<String[]> emigrantBatch : emigrantBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < emigrantBatch.get(i).length; j++) {
                            ps.setString(j+1, emigrantBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return emigrantBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert emigrants output.", e);
		} finally {
            // clear data structure
            testEmigrants.clear();
        }
	}
	
	/**
	 * 
	 */
	private void storeEquity() {
		String sql = "INSERT INTO test_equity_" + year
				+ " (hhold_id, " +
				"hhold_type, " +
				"hhold_income, " +
				"ownership, " +
				"savings_if_rent, " + 
				"year_dwelling_bought, " +
				"equity, " +
				"yearly_mortgage, " +
				"dwelling_price, " +
				"duty_paid" +
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		try {

            List<List<String [] >> equityBatches = Lists.partition(testEquity, BATCH_SIZE);

            for (final List<String []> equityBatch : equityBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < equityBatch.get(i).length; j++) {
                            ps.setString(j+1, equityBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return equityBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert equity output.", e);
		} finally {
            // clear data structure
            testEquity.clear();
        }
	}	
	
	
	/**
	 * 
	 */
	private void storeHholdNotReloc() {
		String sql = "INSERT INTO test_notreloc_hhold_" + year
				+ " (hhold_id, " +
				"nbr_needed, " +
				"nbr_having" +
				") VALUES (?, ?, ?);";

		try {

            List<List<String []>> notRelocatedHholdBatches = Lists.partition(testNotRelocHhold, BATCH_SIZE);

            for (final List<String []> notRelocatedHholdBatch : notRelocatedHholdBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < notRelocatedHholdBatch.get(i).length; j++) {
                            ps.setString(j+1, notRelocatedHholdBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return notRelocatedHholdBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert notreloc_hhold output.", e);
		} finally {
            // clear data structure
            testNotRelocHhold.clear();
        }
	}
	
	private void storeHholdReloc() {
		String sql = "INSERT INTO test_reloc_hhold_" + year
				+ " (order_reloc, " +
				"hhold_id, " +
				"nbr_needed, " +
				"tz_relocated" +
				") VALUES (?, ?, ?, ?);";

		try {
            List<List<String []>> relocatedHholdBatches = Lists.partition(testRelocHhold, BATCH_SIZE);

            for (final List<String []> relocateHholdBatch : relocatedHholdBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < relocateHholdBatch.get(i).length; j++) {
                            ps.setString(j+1, relocateHholdBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return relocateHholdBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert reloc_hhold output.", e);
		} finally {
            // clear data structure
            testRelocHhold.clear();
        }
	}
	
	
	private void storeSPAfterReloc() {
		String sql = "INSERT INTO test_sp_after_reloc_" + year
				+ " (indiv_id, " +
				"age, " +
				"gender, " +
				"indiv_income, " +
				"hhold_relationship, " + 
				"hhold_id, " + 
				"hhold_type, " + 
				"tenure, " +
				"nbr_needed, " +
				"ownership, " +
				"tz, " +
				"nbr_having" +
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		try {
            List<List<String []>> spAfterRelocatedBatches = Lists.partition(testSPAfterReloc, BATCH_SIZE);

            for (final List<String []> spAfterRelocatedBatch : spAfterRelocatedBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < spAfterRelocatedBatch.get(i).length; j++) {
                            ps.setString(j+1, spAfterRelocatedBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return spAfterRelocatedBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert sp_after_reloc output.", e);
		} finally {
            // clear data structure
            testSPAfterReloc.clear();
        }
	}
	
	/**
	 * 
	 */
	private void storeLinkDelayOutput() {
		String sql = "INSERT INTO transims_output_linkdelay_" + year
				+ " (link, dir, depart, arrive, flow, time) VALUES (?, ?, ?, ?, ?, ?);";

		try {
            List<List<String []>> delayOutputArrayListBatches = Lists.partition(linkDelayOutputArrayList, BATCH_SIZE);

            for (final List<String []> delayOutputArrayListBatch : delayOutputArrayListBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < delayOutputArrayListBatch.get(i).length; j++) {
                            ps.setString(j+1, delayOutputArrayListBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return delayOutputArrayListBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert link delay output.", e);
		} finally {
            // clear data structure
            linkDelayOutputArrayList.clear();
        }
	}

	/**
	 * 
	 */
	private void storeTimePlanOutput() {
		String sql = "INSERT INTO transims_output_timeplans_" + year
				+ " (household, person, trip, mode, depart, purpose,trip_length) VALUES (?, ?, ?, ?, ?, ?, ?);";

		try {
            List<List<String []>> timeplanOutputArrayListBatches = Lists.partition(timeplanOutputArrayList, BATCH_SIZE);

            for (final List<String []> timeplanOutputArrayListBatch : timeplanOutputArrayListBatches) {
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < timeplanOutputArrayListBatch.get(i).length; j++) {
                            ps.setString(j+1, timeplanOutputArrayListBatch.get(i)[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return timeplanOutputArrayListBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to insert time plan output.", e);
		} finally {
            // clear data structure
            timeplanOutputArrayList.clear();
        }
	}

	/**
	 * updates satisfaction for each travel zone. first query the
	 * synthetic_population_ouput table in the same year, then update the data
	 * into travel_zone_output table.
	 * 
	 */
	public void storeSatisfaction() {

		String sqlGetData = "select tz_2006 as travelZone,avg(satisfaction) as averageSatisfaction from synthetic_population_output_"
				+ this.year + " group by tz_2006;";
		String sqlSetData = "update travel_zone_output_" + this.year + " set satisfaction = ? where tz_2006 =? ;";

		try {
            List<List<Map<String, Object>>> satisfactionBatches = Lists.partition(jdbcTemplate.queryForList(sqlGetData), BATCH_SIZE);

            for (final List<Map<String, Object>> satisfactionBatch : satisfactionBatches) {
                jdbcTemplate.batchUpdate(sqlSetData, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Map<String, Object> result = satisfactionBatch.get(i);
                        ps.setDouble(1, (Double) result.get("averageSatisfaction"));
                        ps.setInt(2, (Integer) result.get("travelZone"));
                    }

                    @Override
                    public int getBatchSize() {
                        return satisfactionBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to store satisfaction", e);
		}

	}

	public void storeOccupancyCounts(Map<Integer, int[]> occupancyRate, int year) {

		String sql = "INSERT INTO dwelling_occupancy_counts_" + year
				+ "( tz, _1bd, _2bd, _3bd, _4bd) VALUES (?, ?, ?, ?, ?);";

		try {
            List<Entry<Integer, int[]>> occupancyRateSet = new ArrayList<>();
            occupancyRateSet.addAll(occupancyRate.entrySet());

            List<List<Entry<Integer, int[]>>> occupancyBatches = Lists.partition(occupancyRateSet, BATCH_SIZE);

            for (final List<Entry<Integer, int[]>> occupancyBatch : occupancyBatches) {
               jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Map.Entry<Integer, int[]> occupancy = occupancyBatch.get(i);
                        ps.setInt(1, occupancy.getKey());
                        for (int j = 0; j < occupancy.getValue().length; j++) {
                            ps.setInt(j + 2, occupancy.getValue()[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return occupancyBatch.size();
                    }
                });
            }
		} catch (DataAccessException e) {
			logger.error("Failed to update occupancy counts.", e);
		}
	}

	public void storeTripsTime() {
		try {
			String tableName = "travel_zone_output_" + this.year;

			final String updateSql = "UPDATE " + tableName + " SET work_trips_time=? where tz_2006 = ?";

            // Perform this in a transaction so that we can roll it back if necessary.
            TransactionTemplate transTemplate = new TransactionTemplate(transactionManager);
            transTemplate.execute(new TransactionCallbackWithoutResult() {

                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    List<Entry<Integer, Double>> normAverageTripTimes = new ArrayList<>();
                    normAverageTripTimes.addAll(normalizedTravelZoneAverageTripsTime.entrySet());

                    List<List<Entry<Integer, Double>>> tripTimeBatches = Lists.partition(normAverageTripTimes, BATCH_SIZE);
                    try {
                        for (final List<Entry<Integer, Double>> tripTimeBatch : tripTimeBatches) {
                            // batch update operation.
                            jdbcTemplate.batchUpdate(updateSql, new BatchPreparedStatementSetter() {
                                @Override
                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                    Map.Entry<Integer, Double> tripTime = tripTimeBatch.get(i);
                                    ps.setInt(2, tripTime.getKey());
                                    ps.setDouble(1, tripTime.getValue());
                                }

                                @Override
                                public int getBatchSize() {
                                    return tripTimeBatch.size();
                                }
                            });
                        }
                    } catch (Exception e) {
                        logger.error("Failed to update trip times.", e);
                        status.setRollbackOnly();
                    }
                }
            });

		} catch (TransactionException exception) {
			logger.error("Failed to update trip times.", exception);
		} finally {
            // clear data structure
            normalizedTravelZoneAverageTripsTime.clear();

        }
	}
}
