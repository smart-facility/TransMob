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
