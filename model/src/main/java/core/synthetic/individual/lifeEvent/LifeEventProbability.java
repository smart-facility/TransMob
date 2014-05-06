package core.synthetic.individual.lifeEvent;

import java.util.HashMap;
import java.util.Map;

import core.ApplicationContextHolder;
import hibernate.postgres.lifeEvents.BirthProbabilityEntity;
import hibernate.postgres.lifeEvents.LifeEventsDAO;

import core.synthetic.attribute.Gender;

/**
 * a class fetches and stores each probability for different life events occurs
 * by different demographic categories.
 * 
 * @author qun
 * 
 */
public class LifeEventProbability {


	private Map<DemographicInformation, Double> getHigherEducationProbability = new HashMap<>();

    /** DAO for life event probabilities. */
    private final LifeEventsDAO lifeEventsDao;

	public LifeEventProbability() {
        lifeEventsDao = ApplicationContextHolder.getBean(LifeEventsDAO.class);
	}

	@Override
	public String toString() {
		return "LifeEventProbability [femaleDeathProbability=" + lifeEventsDao.findAllFemaleDeathProbabilities()
                + ", maleDeathProbability=" + lifeEventsDao.findAllMaleDeathProbabilities()
                + ", femaleMarriageProbability=" + lifeEventsDao.findAllFemaleMarriageProbabilities()
                + ", maleMarriageProbability=" + lifeEventsDao.findAllMaleMarriageProbabilities()
                + ", femaleDivorceProbability=" + lifeEventsDao.findAllFemaleDivorceProbabilities()
                + ", maleDivorceProbability=" + lifeEventsDao.findAllMaleDivorceProbabilities()
                + ", femaleJobGainProbability=" + lifeEventsDao.findAllFemaleJobGainProbabilities()
                + ", maleJobGainProbability=" + lifeEventsDao.findAllMaleJobGainProbabilities()
                + ", femaleJobLossProbability=" + lifeEventsDao.findAllFemaleJobLossProbabilities()
                + ", maleJobLossProbability=" + lifeEventsDao.findAllFemaleJobLossProbabilities()
                + ", reproductionProbability=" + lifeEventsDao.findAllBirthProbabilities()
				+ ", getHigherEducationProbability=" + getHigherEducationProbability.toString() + "]";
	}

	public Double getMarriageProbabilityByAgeSex(int age, Gender gender) {

        switch(gender) {
            case Male:
                return lifeEventsDao.findMaleMarriageProbability(age).getProbability();
            case Female:
                return lifeEventsDao.findFemaleMarriageProbability(age).getProbability();
        }

        return (double) 0;
	}

	public Double getDeathProbabilityByAgeSex(int age, Gender gender) {
        //If over 100 set to 100.
        age = age > 100 ? 100 : age;

        switch(gender) {
            case Male:
                return lifeEventsDao.findMaleDeathProbability(age).getProbability();
            case Female:
                return lifeEventsDao.findFemaleDeathProbability(age).getProbability();
        }

        return (double) 0;
	}

	public Double getDivorceProbabilityByAgeSex(int age, Gender gender) {
        // If over 69 set to 69
        age = age >= 69 ? 69 : age;

        switch(gender) {
            case Male:
                return lifeEventsDao.findMaleDivorceProbability(age).getProbability();
            case Female:
                return lifeEventsDao.findFemaleDivorceProbability(age).getProbability();
        }

        return (double) 0;
	}

	public Double getJobGainProbabilityByAgeSex(int age, Gender gender) {

        switch(gender) {
            case Male:
                return lifeEventsDao.findMaleJobGainProbability(age).getProbability();
            case Female:
                return lifeEventsDao.findFemaleJobGainProbability(age).getProbability();
        }

        return (double) 0;
	}

	public Double getJobLossProbabilityByAgeSex(int age, Gender gender) {

        switch(gender) {
            case Male:
                return lifeEventsDao.findMaleJobLossProbability(age).getProbability();
            case Female:
                return lifeEventsDao.findFemaleJobLossProbability(age).getProbability();
        }

        return (double) 0;
	}

	public double getReproductionProbabilityByAgeSex(int age, int numberOfChild) {

        BirthProbabilityEntity birthProbability = lifeEventsDao.findBirthProbability(age);
        switch (numberOfChild) {
            case 0:
                return birthProbability.getFirstChild();
            case 1:
                return birthProbability.getSecondChild();
            case 2:
                return birthProbability.getThirdChild();
            case 3:
                return birthProbability.getFourthChild();
            case 4:
                return birthProbability.getFifthChild();
            case 5:
            default:
                return birthProbability.getSixOrMore();
        }
	}

	public Double getGetHigherEducationProbabilityByAgeSex(int age,
			Gender gender) {

		DemographicInformation demographicInfomation = new DemographicInformation(
				age, gender);

		return getHigherEducationProbability.get(demographicInfomation);
	}
}
