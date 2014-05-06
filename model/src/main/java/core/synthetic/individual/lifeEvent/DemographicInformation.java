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
