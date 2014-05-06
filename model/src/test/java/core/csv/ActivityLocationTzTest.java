package core.csv;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.junit.Assert.assertEquals;


import java.io.*;


/**
 * Test Harness to ensure we can read in the ActivityLocation ID to TZ06 CSV mapping file.
 */
public class ActivityLocationTzTest {

    @Test
    public void testReadFile() {
    	
    	File dir = new File("data/ActivityLocation_Tz.csv");    	
    	if (dir.exists())
    	{
    		System.out.println("The file exists");
    		
    		InputStream testFileStream = getClass().getClassLoader().getResourceAsStream("data/ActivityLocation_Tz.csv");

            Reader testFileReader = new BufferedReader(new InputStreamReader(testFileStream));

            List<ActivityLocationTz> results = ActivityLocationTz.readFromCsv(testFileReader);

            assertEquals("Failed to rad in expected number of entires", 59, results.size());

            ActivityLocationTz firstEntry = results.get(0);

            assertEquals("First entry's activity ID not as expected", 86117, firstEntry.getActivityId());
    	}    		
    	else
    		System.out.println("The file does not exist");
    	
        
    }
}
