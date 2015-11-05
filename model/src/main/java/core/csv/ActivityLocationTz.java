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


import java.io.Reader;
import java.util.List;

/**
 * Bean for Activity Location ID to TZ06 mapping.
 */
public class ActivityLocationTz {

    private int activityId;

    private int tz06;


    public static List<ActivityLocationTz> readFromCsv(Reader file) {
        String[] columns = new String[]{"activityId", "tz06"}; // the fields to bind to in your JavaBean

        // Set reader to start at line 1 so to skip the header.
        return CSVHelper.readFromCsv(file, ActivityLocationTz.class, columns, 1);
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getTz06() {
        return tz06;
    }

    public void setTz06(int tz06) {
        this.tz06 = tz06;
    }
}
