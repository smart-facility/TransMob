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
