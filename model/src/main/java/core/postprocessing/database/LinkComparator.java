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
