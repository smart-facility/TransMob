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
package core.synthetic.dwelling;

import hibernate.postgres.TransimsHouseholdLocationEntity;

import java.util.Comparator;


public class TransimsHouseholdLocationComparator implements Comparator<TransimsHouseholdLocationEntity> {



	@Override
	public int compare(TransimsHouseholdLocationEntity arg0,
                       TransimsHouseholdLocationEntity arg1) {
		// TODO Auto-generated method stub
		final int EQUAL = 0;
		final int BEFORE = -1;
		final int LATER = 1;

		if (arg0.getDwellingIndex() > arg1.getDwellingIndex()) {
			return BEFORE;
		}

		if (arg0.getDwellingIndex() < arg1.getDwellingIndex()) {
			return LATER;
		}

		return EQUAL;
	}

}
