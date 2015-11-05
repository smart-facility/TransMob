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
package core.csv;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Helper class for reading a CSV file into a list of domain objects. By default always
 */
public final class CSVHelper {

    private static final Logger logger = Logger.getLogger(CSVHelper.class);

    // Private constructor.
    private CSVHelper() {
    }

    public static <T> List<T> readFromCsv(Reader file, Class<T> type, String [] columns, int skipLines) {
        ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
        mappingStrategy.setType(type);
        mappingStrategy.setColumnMapping(columns);

        CsvToBean csv = new CsvToBean();

        // Set reader to start at line 1 so to skip the header.
        CSVReader reader = new CSVReader(file, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER,
                CSVParser.DEFAULT_ESCAPE_CHARACTER, skipLines, false, false);
        List<T> entries = csv.parse(mappingStrategy, reader);

        try {
            reader.close();
        } catch (IOException e) {
            logger.error("Failed to close CSV file after reading: " + file, e);
        }

        return entries;
    }

}
