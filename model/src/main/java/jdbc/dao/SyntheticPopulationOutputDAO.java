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
package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import core.ApplicationContextHolder;
import core.synthetic.HouseholdPool;
import core.synthetic.attribute.Gender;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;

/**
 * DAO for the Synthetic Population output tables.
 */
public class SyntheticPopulationOutputDAO {
    private static final Logger logger = Logger.getLogger(DwellingCapacityDAO.class);
    private static final int BATCH_SIZE = 500;

    private JdbcTemplate jdbcTemplate;

    public void setDatasource(DataSource datasource) {
        jdbcTemplate = new JdbcTemplate(datasource);
    }

    /**
     * Store individuals into the synthetic population database for the given year.
     * @param householdPool Household pool
     * @param individuals List of individuals to add to the database.
     * @param year The year for which to add the population to.
     */
    public void storeIndividuals(final HouseholdPool householdPool, final List<Individual> individuals,  int year) {
        final String tableName;

        tableName = "synthetic_population_output_" + year;
        
        final String sql = "INSERT INTO "
                + tableName
                + " (id, age, income, household_relationship, household_id, household_type, gender,satisfaction,tz_2006,tenure)VALUES (?, ?, ?,?, ?,?, ?, ?,?,?);";


        // Do the batch insert as part of a transaction
        TransactionTemplate transactionTemplate = new TransactionTemplate((DataSourceTransactionManager) ApplicationContextHolder.getBean("transactionManagerOutput"));
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
			protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {

                try
                {
                    List<List<Individual>> individualBatches = Lists.partition(individuals, BATCH_SIZE);

                    for (final List<Individual> individualBatch : individualBatches) {
                        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Individual individual = individualBatch.get(i);
                                ps.setInt(1, individual.getId());
                                ps.setInt(2, individual.getAge());
                                ps.setInt(3, individual.getIncome().intValue());
                                ps.setString(4, individual.getHouseholdRelationship().toString());
                                ps.setString(6, individual.getHholdCategory().toString());
                                ps.setInt(7, (individual.getGender() == Gender.Male) ? 1 : 0);
                                ps.setInt(5, individual.getHouseholdId());
                                ps.setDouble(8, individual.getSatisfaction());
                                ps.setInt(9, individual.getLivedTravelZoneid());
                                ps.setInt(10, (int) householdPool.getByID(individual.getHouseholdId()).getTenure());
                            }

                            @Override
                            public int getBatchSize() {
                                return individualBatch.size();
                            }
                        });
                    }
                } catch (DataAccessException e) {
                    logger.error("Failed to update synthetic population table: " + tableName, e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }


    /**
     * Store the removed individuals.
     * @param year The year to add the removed individuals to.
     * @param removedHouseholds The households to remove.
     */
    public void storeRemovedIndividualInfomation(int year, List<Household> removedHouseholds) {

        final String tableName = "removed_synthetic_population_output_" + year;

        final String sql = "INSERT INTO "
                + tableName
                + " (id, age, income, household_relationship, household_id, household_type, gender,satisfaction,tz_2006,tenure)VALUES (?, ?, ?,?, ?,?, ?, ?,?,?);";


        for (final Household household : removedHouseholds) {

            final List<Individual> individuals = household.getResidents();

            // Do the batch insert as part of a transaction
            TransactionTemplate transactionTemplate = new TransactionTemplate((DataSourceTransactionManager) ApplicationContextHolder.getBean("transactionManagerOutput"));
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
				protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {

                    try
                    {
                        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Individual individual = individuals.get(i);
                                ps.setInt(1, individual.getId());
                                ps.setInt(2, individual.getAge());
                                ps.setInt(3, individual.getIncome().intValue());
                                ps.setString(4, individual.getHouseholdRelationship()
                                        .toString());
                                ps.setString(6, individual.getHholdCategory().toString());
                                ps.setInt(7, (individual.getGender() == Gender.Male) ? 1 : 0);
                                ps.setInt(5, individual.getHouseholdId());
                                ps.setDouble(8, individual.getSatisfaction());
                                ps.setInt(9, individual.getLivedTravelZoneid());
                                ps.setInt(10, (int) household.getTenure());
                            }

                            @Override
                            public int getBatchSize() {
                                return individuals.size();
                            }
                        });
                    } catch (DataAccessException e) {
                        logger.error("Failed to update synthetic population table: " + tableName, e);
                        paramTransactionStatus.setRollbackOnly();
                    }
                }
            });
        }
    }


    /**
     * Retrieves Synthetic population data for the given year from the output database.
     * @param year The year to retrieve.
     * @return key: household_id, value: income, bedrooms
     */
    public Map<Integer, int[]> getDataForYear(int year) {

        final String tableName = "synthetic_population_output_" + year;

        Map<Integer, int[]> syntheticPopData = new HashMap<Integer, int[]>();

        String sqlGeneral = "SELECT household_id,sum(income) as income, count(*)-sum(case when age < 10 then 1 else 0 end)-sum(case when household_relationship = 'Married' or household_relationship = 'Defacto' then 1 else 0 end)+ceil(cast(sum(case when age < 10 then 1 else 0 end) as real)/3)+ceil(cast(sum(case when household_relationship = 'Married' or household_relationship = 'Defacto' then 1 else 0 end) as real)/2) as bedrooms FROM "
                + tableName + " group by household_id order by household_id;";
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlGeneral);

            for (Map<String, Object> result : results) {

                int householdId = (Integer) result.get("household_id");
                int income = ((Long) result.get("income")).intValue();
                int bedrooms = ((Double) result.get("bedrooms")).intValue();

                int[] temp = { income, bedrooms };

                syntheticPopData.put(householdId, temp);

            }

        } catch (DataAccessException e) {
            logger.error("Failed to retrieve synthetic population data from the output database for year: " + year, e);
        }

        return syntheticPopData;
    }
}
