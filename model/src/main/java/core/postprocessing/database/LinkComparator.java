package core.postprocessing.database;

import java.util.Comparator;

/**
 * A comparator class to compare links by link id, hour and direction
 * 
 * @author qun
 * 
 */
public class LinkComparator implements Comparator<Object> {

	/**
	 * compares 2 links according to id, hour and then direction
	 * 
	 * @param arg0
	 *            first link
	 * @param arg1
	 *            second link
	 * @return the results of comparing
	 * @author qun
	 */
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		int id0 = Integer.parseInt(((String) arg0).split("@")[0]);
		int hour0 = Integer.parseInt(((String) arg0).split("@")[1]);
		int direction0 = Integer.parseInt(((String) arg0).split("@")[2]);

		int id1 = Integer.parseInt(((String) arg1).split("@")[0]);
		int hour1 = Integer.parseInt(((String) arg1).split("@")[1]);
		int direction1 = Integer.parseInt(((String) arg1).split("@")[2]);

		if (id0 < id1) {
			return -1;
		} else if (id0 > id1) {
			return 1;
		} else {
			if (hour0 < hour1) {
				return -1;
			} else if (hour0 > hour1) {
				return 1;
			} else {
				if (direction0 < direction1) {
					return -1;
				} else if (direction0 > direction1) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
}
