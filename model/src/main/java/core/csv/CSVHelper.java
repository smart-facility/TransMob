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
