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
﻿/*
-- FUNCTION: migrate_synthetic_population_output
--      This function joins tables "synthetic_population_output_20*"
-- in the scenario database.
-- 
-- Usage:
--
--    migrate_dwelling_occupancy_output(DB_NAME, CURRENT_RUN_KEY)
--
-- Parameters:
--    DB_NAME		- database which used for the dashboard
--    CURRENT_RUN_KEY   - a unique indicator of a scenario runr
-- 
*/

CREATE OR REPLACE FUNCTION migrate_dwelling_occupancy_output(
   db_name text,
   current_run_key INTEGER) 
RETURNs void AS $$
DECLARE
	i INTEGER := 0;
	year_count INTEGER := 0;
	conn text := '';
	query_statement text := '';
BEGIN
    
	conn = 'dbname=' || db_name;
         
   -- get total year count in a simulation 
	query_statement = '
      SELECT 
         table_count 
      FROM dblink(
         ' || quote_literal(conn) || ', 
         ' || quote_literal('
                  SELECT 
                     count(tablename) 
                  FROM 
                     pg_tables 
                  WHERE 
                     tablename LIKE ''dwelling_occupancy_rate_20%''') || '
      ) AS t1(table_count INTEGER)';

   EXECUTE query_statement INTO year_count;

   -- migrate synthetic population data year by year 
   FOR i IN SELECT * FROM GENERATE_SERIES(2006, 2006 + year_count - 1, 1)
   LOOP
      -- upload to "dwelling_occupancy_rate_backup"
      query_statement = '
         INSERT INTO dwelling_occupancy_rate_backup (
            scenario_run_key, 
            year_key, 
            tz_2006,
            rate_1_bd,
            rate_2_bd,
            rate_3_bd,
            rate_4_bd
         ) ' || ' 
         SELECT   
            scenario_run_key,
            year_key,
            tz_2006,
            rate_1_bd,
            rate_2_bd,
            rate_3_bd,
            rate_4_bd
         FROM dblink(
            ' || quote_literal(conn) || ', 
            ' || quote_literal('
                     SELECT ' || 
                        to_char(current_run_key, 'FM9999') || ', ' || 
                        to_char(i, 'FM9999') || ', ' || '
                        tz,
                        _1bd,
                        _2bd,
                        _3bd,
                        _4bd 
                     FROM 
                        dwelling_occupancy_rate_' || to_char(i, 'FM9999')) || ' 
         ) AS t1(
               scenario_run_key integer, 
               year_key integer, 
               tz_2006 INTEGER,
               rate_1_bd numeric,
               rate_2_bd numeric,
               rate_3_bd numeric,
               rate_4_bd numeric
               )
      ';
      EXECUTE query_statement;
      
   END LOOP;
   
   -- upload to "travel_zone_dwellings"
   INSERT INTO travel_zone_dwellings (
      scenario_run_key,
      year_key,
      tz_2006,
      dw_1_bed,
      dw_2_bed,
      dw_3_bed,
      dw_4_bed)
   SELECT
      b.scenario_run_key,
      b.year_key,
      b.tz_2006,
      round(dw_1_bed * rate_1_bd) dw_1_bed,
      round(dw_2_bed * rate_2_bd) dw_2_bed,
      round(dw_3_bed * rate_3_bd) dw_3_bed,
      round(dw_4_bed * rate_4_bd) dw_4_bed
   FROM 
      ref_travel_zone_dwellings a, dwelling_occupancy_rate_backup b
   WHERE
       b.scenario_run_key = current_run_key 
   AND b.year_key = a.year_key
   AND b.tz_2006 = a.tz_2006;

   UPDATE 
      travel_zone_dwellings a
   SET
      scenario_id = b.scenario_id
   FROM
      scenario_run b
   WHERE
       a.scenario_run_key = current_run_key
   AND a.scenario_run_key = b.scenario_run_key;

   -- upload to "travel_zone_dwellings_by_ratio"
   INSERT INTO travel_zone_dwellings_by_ratio (
      scenario_run_key,
      scenario_key,
      year_key,
      tz_2006, 
      total_dwelling,
      dwelling_type,
      sub_total,
      percent)
   SELECT
      scenario_run_key,
      scenario_key,
      year_key,
      tz_2006, 
      total_dwelling,
      dwelling_type,
      sub_total,
      cast(sub_total as real)/total_dwelling percent
   FROM (
      SELECT    
         scenario_run_key,
         scenario_id scenario_key,
         year_key,
         tz_2006, 
         (dw_1_bed + dw_2_bed + dw_3_bed + dw_4_bed) total_dwelling,
         '1-bedroom' dwelling_type,
         dw_1_bed sub_total
      FROM
         travel_zone_dwellings
      UNION 
         select    
            scenario_run_key,
            scenario_id scenario_key,
            year_key,
            tz_2006, 
            (dw_1_bed + dw_2_bed + dw_3_bed + dw_4_bed) total_dwelling,
            '2-bedroom' dwelling_type,
            dw_2_bed sub_total
         FROM
            travel_zone_dwellings
      UNION 
         select    
            scenario_run_key,
            scenario_id scenario_key,
            year_key,
            tz_2006, 
            (dw_1_bed + dw_2_bed + dw_3_bed + dw_4_bed) total_dwelling,
            '3-bedroom' dwelling_type,
            dw_3_bed sub_total
         FROM
            travel_zone_dwellings
      UNION 
         select    
            scenario_run_key,
            scenario_id scenario_key,
            year_key,
            tz_2006, 
            (dw_1_bed + dw_2_bed + dw_3_bed + dw_4_bed) total_dwelling,
            '4-bedroom' dwelling_type,
            dw_4_bed sub_total
         from
            travel_zone_dwellings
   ) u
   WHERE 
      scenario_run_key = current_run_key
   and 
      total_dwelling > 0;   
   
END;
$$ LANGUAGE PLPGSQL;