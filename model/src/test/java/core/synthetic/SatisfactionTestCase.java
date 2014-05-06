package core.synthetic;

import core.synthetic.attribute.Gender;

public class SatisfactionTestCase {
	private int livedYear;
	private Gender gender;
	private double liveability;
	private double expectation;

	public SatisfactionTestCase(int livedYear, Gender gender,
			double liveability, double expectation) {
		super();
		this.livedYear = livedYear;
		this.gender = gender;
		this.liveability = liveability;
		this.expectation = expectation;
	}

	public int getLivedYear() {
		return livedYear;
	}

	public void setLivedYear(int livedYear) {
		this.livedYear = livedYear;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public double getLiveability() {
		return liveability;
	}

	public void setLiveability(double liveability) {
		this.liveability = liveability;
	}

	public double getExpectation() {
		return expectation;
	}

	public void setExpectation(double expectation) {
		this.expectation = expectation;
	}

}
