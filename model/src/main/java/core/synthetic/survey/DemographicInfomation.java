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
package core.synthetic.survey;

import java.math.BigDecimal;

import core.synthetic.attribute.Gender;
import core.synthetic.individual.Individual;
import org.apache.log4j.Logger;

/**
 * A class represents demographic information (age, sex and income). By
 * different crossed categories, a certain weight will be fitted for liveability
 * evaluation mode.
 * 
 * @author qun
 * 
 */
public class DemographicInfomation {

	private Age age;
	private Gender gender;
	private Salary salary;
	private static final int weeksInAYear = 52;

    private static final Logger logger = Logger.getLogger(DemographicInfomation.class);

	public DemographicInfomation(Age age, Gender gender, Salary salary) {
		super();
		this.age = age;
		this.gender = gender;
		this.salary = salary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.age == null) ? 0 : this.age.hashCode());
		result = prime * result
				+ ((this.gender == null) ? 0 : this.gender.hashCode());
		result = prime * result
				+ ((this.salary == null) ? 0 : this.salary.hashCode());
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
		DemographicInfomation other = (DemographicInfomation) obj;
		if (this.age != other.age) {
			return false;
		}
		if (this.gender != other.gender) {
			return false;
		}
		if (this.salary != other.salary) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DemographicInfomation [age=" + this.age + ", gender="
				+ this.gender + ", salary=" + this.salary + "]";
	}

	/**
	 * Identifies an individual belongs to which demographic.
	 * 
	 * @param individual
	 *            the individual will be identified
	 * @return a certain demographic category the individual belongs to
	 */
	public static DemographicInfomation classifyIndividual(Individual individual) {
		logger.trace(individual.getAge());
		Age age = Age.classify(individual.getAge());
		Gender gender = individual.getGender();
		Salary salary = Salary.classify(individual.getIncome().multiply(new BigDecimal(weeksInAYear)));

		return new DemographicInfomation(age, gender, salary);
	}

	public Age getAge() {
		return this.age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Salary getSalary() {
		return this.salary;
	}

	public void setSalary(Salary salary) {
		this.salary = salary;
	}

	public static int getWeeksinayear() {
		return weeksInAYear;
	}
}
