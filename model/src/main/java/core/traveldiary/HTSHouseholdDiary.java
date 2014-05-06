package core.traveldiary;

import java.util.ArrayList;
import java.util.List;

public class HTSHouseholdDiary {

	private int hholdType;
	private int hholdID;
	private int nDependentChild;
	private int nResidents;
	private int nStudents;
	private int nOtherAdults;
	private List<int[][]> dependentChildTD;
	private List<int[][]> studentTD;
	private List<int[][]> otherAdultsTD;

	// hholdIDCol, personNoCol, tripNoCol,hfCol, attendSchoolCol, adultCatCol,
	// numTripsCol, departCol, tripTimeCol, tmodeCol, purpos11Col
	private final int rawTD_hhIDCol = 0;
	private final int rawTD_ppIDCol = 1;
	private final int rawTD_tripIDCol = 2;
	private final int rawTD_hfCol = 3;
	private final int rawTD_schoolCol = 4;
	private final int rawTD_adultCatCol = 5;
	private final int rawTD_nTripsCol = 6;
	private final int rawTD_tdepCol = 7;
	private final int rawTD_ttripCol = 8;
	private final int rawTD_tmodeCol = 9;
	private final int rawTD_purposCol = 10;

	public HTSHouseholdDiary() {
	}

	public HTSHouseholdDiary(int[][] rawDiaries) {
		List<int[][]> tdEachIndivThisHhold = new ArrayList<>();

		List<int[]> indivTD = new ArrayList<>();
		indivTD.add(rawDiaries[0]);
		for (int row = 1; row <= rawDiaries.length - 1; row++) {
			if (rawDiaries[row][rawTD_ppIDCol] == indivTD.get(indivTD.size() - 1)[rawTD_ppIDCol]) {
				indivTD.add(rawDiaries[row]);
				if (row == rawDiaries.length - 1) {
					int[][] tmpTD = new int[indivTD.size()][indivTD.get(0).length];
					for (int tmpi = 0; tmpi <= tmpTD.length - 1; tmpi++)
						tmpTD[tmpi] = indivTD.get(tmpi);
					tdEachIndivThisHhold.add(tmpTD);
				}
			} else {
				int[][] tmpTD = new int[indivTD.size()][indivTD.get(0).length];
				for (int tmpi = 0; tmpi <= tmpTD.length - 1; tmpi++)
					tmpTD[tmpi] = indivTD.get(tmpi);
				tdEachIndivThisHhold.add(tmpTD);

				indivTD = new ArrayList<int[]>();
				indivTD.add(rawDiaries[row]);
			}
		}

		int nDepChild = 0;
		int nStu = 0;
		List<int[][]> depChildTD = new ArrayList<>();
		List<int[][]> stuTD = new ArrayList<>();
		List<int[][]> adultTD = new ArrayList<>();

		for (int[][] td : tdEachIndivThisHhold)
			// for each individual in this household
			if (td[0][rawTD_schoolCol] == 1) {// if this individual has
												// ATTEND_SCHOOL equal to 1,
												// (s)he is a HTS dependent
												// child
				nDepChild += 1;
				depChildTD.add(td);
			} else // if this individual has ATTEND_SCHOOL equal to 0
			if (td[0][rawTD_adultCatCol] == 3 || td[0][rawTD_adultCatCol] == 6) { // and
																					// ADULT_PRIORITY_CAT
																					// equal
																					// to
																					// 3
																					// or
																					// 6,
																					// (s)he
																					// is
																					// a
																					// student
				nStu += 1;
				stuTD.add(td);
			} else
				// (s)he is an adult (e.g. a parent, an over 15 child, a
				// relative, etc. who doesn't have any trips with purpose
				// EDUCATION)
				adultTD.add(td);

		this.setHholdID(rawDiaries[0][rawTD_hhIDCol]);
		this.setHholdType(rawDiaries[0][rawTD_hhIDCol]);
		this.setnDependentChild(nDepChild);
		this.setnResidents(tdEachIndivThisHhold.size());
		this.setnStudents(nStu);
		this.setnOtherAdults(tdEachIndivThisHhold.size() - nDepChild - nStu);
		this.setDependentChildTD(depChildTD);
		this.setStudentTD(stuTD);
		this.setOtherAdultsTD(adultTD);
	}

	public int getnDependentChild() {
		return nDependentChild;
	}

	public void setnDependentChild(int nDependentChild) {
		this.nDependentChild = nDependentChild;
	}

	public int getnResidents() {
		return nResidents;
	}

	public void setnResidents(int nResidents) {
		this.nResidents = nResidents;
	}

	public int getnStudents() {
		return nStudents;
	}

	public void setnStudents(int nStudents) {
		this.nStudents = nStudents;
	}

	public List<int[][]> getDependentChildTD() {
		return dependentChildTD;
	}

	public void setDependentChildTD(List<int[][]> dependentChildTD) {
		this.dependentChildTD = dependentChildTD;
	}

	public List<int[][]> getStudentTD() {
		return studentTD;
	}

	public void setStudentTD(List<int[][]> studentTD) {
		this.studentTD = studentTD;
	}

	public List<int[][]> getOtherAdultsTD() {
		return otherAdultsTD;
	}

	public void setOtherAdultsTD(List<int[][]> otherAdultsTD) {
		this.otherAdultsTD = otherAdultsTD;
	}

	public int getHholdType() {
		return hholdType;
	}

	public void setHholdType(int hholdType) {
		this.hholdType = hholdType;
	}

	public int getHholdID() {
		return hholdID;
	}

	public void setHholdID(int hholdID) {
		this.hholdID = hholdID;
	}

	public int getnOtherAdults() {
		return nOtherAdults;
	}

	public void setnOtherAdults(int nOtherAdults) {
		this.nOtherAdults = nOtherAdults;
	}

}
