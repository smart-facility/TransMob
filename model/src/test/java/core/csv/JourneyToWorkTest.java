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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Test;

/**
 * Test harness to ensure that we can read in the JourneyToWork CSV files.
 */
public class JourneyToWorkTest {
    @Test
    public void testReadFile() {
    	
    	File dir = new File("data/2006jtw.csv");    	
    	if (dir.exists())
    	{
    		InputStream testFileStream = getClass().getClassLoader().getResourceAsStream("data/2006jtw.csv");

            Reader testFileReader = new BufferedReader(new InputStreamReader(testFileStream));

            List<JourneyToWork> results = JourneyToWork.readFromCsv(testFileReader);

            assertEquals("Failed to rad in expected number of entries", 59, results.size());

            JourneyToWork firstEntry = results.get(0);

            assertEquals("First entry's Destination TZ06 not as expected", 520, firstEntry.getDestinationTz06());
            assertEquals("First entry's Mode not as expected", 7, firstEntry.getMode9());
    	}    		
    	else
    		System.out.println("The file does not exist");
    	
    	
        
    }
}
