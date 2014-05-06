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
