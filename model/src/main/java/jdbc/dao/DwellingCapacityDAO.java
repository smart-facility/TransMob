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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import core.HardcodedData;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * JDBC Template based DAO as we need the ability to pick the correct schema at runtime.
 */
public class DwellingCapacityDAO {

    private static final Logger logger = Logger.getLogger(DwellingCapacityDAO.class);

    private JdbcTemplate jdbcTemplate;

    public void setDatasource(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    /**
     * Find all dwelling capacity entities for a given year in a given schema.
     * @param year Which year to retrieve the dwelling capacities for.
     * @param schema Which schema to use.
     * @return List of all dwelling capacities for the given year and schema.
     */
    public List<DwellingCapacity> findAll(int year, String schema) {
        List<DwellingCapacity> dwellingCapacities = new ArrayList<DwellingCapacity>();

        String tableName = schema + "._" + year;
        String sql = "SELECT * FROM  " + tableName + " order by tz06;";

        try {
            dwellingCapacities = this.jdbcTemplate.query(sql, new RowMapper<DwellingCapacity>() {
                @Override
				public DwellingCapacity mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new DwellingCapacity(rs.getInt("tz06"), rs.getInt("_1bd"),
                            rs.getInt("_2bd"), rs.getInt("_3bd"), rs.getInt("_4bd"));
                }
            });
        } catch (DataAccessException e) {
            logger.error("Failed to load all Dwelling Capacities from table: " + tableName, e);
        }

        return dwellingCapacities;
    }

    /**
     * Find all dwelling capacity entities for a given year in a given schema.
     * @param year Which year to retrieve the dwelling capacities for.
     * @param schema Which schema to use.
     * @return List of all dwelling capacities for the given year and schema.
     */
    public DwellingCapacity findByTz06(int year, int tz06, String schema) {
        DwellingCapacity dwellingCapacity = null;

        String tableName = schema + "._" + year;
        String sql = "SELECT * FROM  " + tableName + " where tz06=? order by tz06;";

        try {
            dwellingCapacity = this.jdbcTemplate.queryForObject(sql, new Object[]{tz06}, new RowMapper<DwellingCapacity>() {
                @Override
                public DwellingCapacity mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new DwellingCapacity(rs.getInt("tz06"), rs.getInt("_1bd"),
                            rs.getInt("_2bd"), rs.getInt("_3bd"), rs.getInt("_4bd"));
                }
            });
        } catch (DataAccessException e) {
            logger.error("Failed to load Dwelling Capacities from table: " + tableName, e);
        }

        return dwellingCapacity;
    }


    /**
     * Reset dwelling capacities for the dwelling schema from the dwelling_backup schema.
     */
    public void resetDwellingCapacity() {

        try {
            for (int i = HardcodedData.START_YEAR; i < 2026; i++) {
                String sql = "select * into dwelling._" + i + " from dwelling_backup._" + i + ";";

                logger.debug("Reset dwelling capacity: " + sql);

                // TODO not sure if this should be an execute/update or query due to this being a "select into".
                this.jdbcTemplate.update(sql);
            }
        } catch (DataAccessException e) {
            logger.error("Failed to reset dwelling capacities.", e);
        }
    }

    /**
     * Return the list of available dwelling schemas.
     * @return  Names of the available dwelling schemas.
     */
    public List<String> getAvailableDwellingSchema() {
        String sql = "SELECT distinct table_schema FROM information_schema.tables ORDER BY table_schema;";

        List<String> dwellingSchemas = new ArrayList<String>();

        try {
            List<String> schemaNames = this.jdbcTemplate.query(sql, new RowMapper<String>() {
                @Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("table_schema");
                }
            });

            for (String schemaName : schemaNames) {
                if (schemaName.matches("dwelling[a-zA-Z_0-9]*")) {
                    dwellingSchemas.add(schemaName);
                }

            }
        } catch (DataAccessException e) {
            logger.error("Failed to find available dwelling schemas", e);
        }
        return dwellingSchemas;
    }


    /**
     * Dwelling capacity update via SQL.
     * @param sqls Collection of update SQL operations.
     */
    public void updateBySQL(List<String> sqls) {
        try {
            for (String aSql : sqls) {
                logger.debug(aSql);
                this.jdbcTemplate.update(aSql);
            }

        } catch (DataAccessException e) {
            logger.error("Exception caught doing bulk update", e);
        }
    }
}
