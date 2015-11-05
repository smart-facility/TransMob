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
