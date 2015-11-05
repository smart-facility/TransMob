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
package core.synthetic.individual.lifeEvent;

import core.synthetic.attribute.Gender;

/**
 * A class represents demographic information (age and sex). By different
 * crossed categories, a certain probability for each life event.
 * 
 * @author qun
 * 
 */
public class DemographicInformation {

	private final int age;
	private final Gender gender;

	public DemographicInformation(int age, Gender gender) {
		super();
		this.age = age;
		this.gender = gender;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DemographicInformation other = (DemographicInformation) obj;
		if (age != other.age) {
			return false;
		}
		if (gender != other.gender) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DemographicInfomation [age=" + age + ", gender=" + gender + "]";
	}

}
