package hibernate;

import hibernate.postgres.TransimsHouseholdLocationEntity;

import java.util.Comparator;

public class IdComparator implements Comparator<TransimsHouseholdLocationEntity> {

	@Override
	public int compare(TransimsHouseholdLocationEntity arg0,
                       TransimsHouseholdLocationEntity arg1) {
		// TODO Auto-generated method stub

		int id0 = arg0.getDwellingIndex();
		int id1 = arg1.getDwellingIndex();

		if (id0 < id1) {
			return -1;
		} else if (id0 > id1) {
			return 1;
		}

		return 0;
	}

}
