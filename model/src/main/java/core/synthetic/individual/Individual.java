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
package core.synthetic.individual;

import core.synthetic.household.Household;
import hibernate.postgres.IntermediateSyntheticPopulationEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Random;

import hibernate.postgres.SyntheticPopulationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.HardcodedData;
import core.ModelMain;
import core.synthetic.IndividualPool;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.attribute.HighestEduFinished;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.attribute.Occupation;
import core.synthetic.attribute.TransportModeToWork;
import core.synthetic.individual.lifeEvent.LifeEventProbability;
import core.synthetic.travel.mode.TravelModeSelection;
import core.synthetic.travel.mode.TravelModes;


/**
 * A class represents agent behaviours including evolution in the simulation.
 * 
 * @author qun
 * 
 */
public class Individual implements Serializable {

	private static final int AVERAGE_INCOME = 1125;
    private static final Logger logger = LoggerFactory.getLogger(Individual.class);
	private static final ModelMain main = ModelMain.getInstance();

	private static final long serialVersionUID = 5428263034048380346L;

	/**
	 * name="id".
	 */
	private int id;
	/**
	 * name="age"
	 */
	private int age;
	/**
	 * name="gender".
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Gender gender;
	/**
	 * name="income".
	 */
	private BigDecimal weeklyIncome;

	private transient BigDecimal preserveIncome;
	/**
	 * name="householdRelationship".
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private HouseholdRelationship householdRelationship;
	/**
	 * name="occupation".
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Occupation occupation;
	/**
	 * name="transportModeToWork".
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private TransportModeToWork transportModeToWork;
	/**
	 * name="highestEduFinished".
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private HighestEduFinished highestEduFinished;
	/**
	 * name="hholdCategory".
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Category hholdCategory;

	private transient int householdId;

	private int livedTravelZoneid;

	private transient double satisfaction;

	private transient int[][] travelDiariesWeekdays;
	private transient int[][] travelDiariesWeekend;

	// ==========================================================================================================
	public int getLivedTravelZoneid() {
		return this.livedTravelZoneid;
	}

	public void setLivedTravelZoneid(int livedTravelZoneid) {
		this.livedTravelZoneid = livedTravelZoneid;
	}

	public int[][] getTravelDiariesWeekdays() {
		return this.travelDiariesWeekdays;
	}

	public void setTravelDiariesWeekdays(int[][] travelDiariesWeekdays) {
		if (travelDiariesWeekdays != null) {
			int[][] inputTravelDiariesWeekdays = travelDiariesWeekdays.clone();
			this.travelDiariesWeekdays = new int[inputTravelDiariesWeekdays.length][inputTravelDiariesWeekdays[0].length];
			System.arraycopy(inputTravelDiariesWeekdays, 0, this.travelDiariesWeekdays, 0, inputTravelDiariesWeekdays.length);
		}
	}

	public int[][] getTravelDiariesWeekend() {
		return this.travelDiariesWeekend;
	}

	public void setTravelDiariesWeekend(int[][] travelDiariesWeekend) {
		if (travelDiariesWeekend != null) {
			int[][] inputTravelDiariesWeekdays = travelDiariesWeekend.clone();
			this.travelDiariesWeekend = new int[inputTravelDiariesWeekdays.length][inputTravelDiariesWeekdays[0].length];
			System.arraycopy(inputTravelDiariesWeekdays, 0, this.travelDiariesWeekend, 0, inputTravelDiariesWeekdays.length);
		}
	}

	public Individual() {
	}

	public Individual(Individual aIndividual) {

		this.id = aIndividual.getId();
		this.age = aIndividual.getAge();
		this.weeklyIncome = new BigDecimal(aIndividual.getIncome().intValue());
		this.householdRelationship = aIndividual.getHouseholdRelationship();
		this.hholdCategory = aIndividual.getHholdCategory();
		this.gender = aIndividual.getGender();
		this.householdId = aIndividual.getHouseholdId();
		this.satisfaction = aIndividual.getSatisfaction();
		this.livedTravelZoneid = aIndividual.getLivedTravelZoneid();
	}

	/**
	 * @return name="id".
	 */
	public int getId() {
		return this.id;
	}

	public BigDecimal getPreserveIncome() {
		return this.preserveIncome;
	}

	public void setPreserveIncome(BigDecimal preserveIncome) {
		this.preserveIncome = preserveIncome;
	}

	/**
	 * @param id
	 *            name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return name="age"
	 */
	public int getAge() {
		return this.age;
	}

	/**
	 * @param age
	 *            name="age"
	 */
	public void setAge(int age) {
		this.age = age;

	}

	/**
	 * @return name="gender"
	 */
	public Gender getGender() {

		return this.gender;
	}

	/**
	 * @param gender
	 *            name="gender"
	 */
	public void setGender(Gender gender) {
		// notifyListeners("Gender", this.gender, gender);
		this.gender = gender;
	}

	/**
	 * @return name="income"
	 */
	public BigDecimal getIncome() {
		return this.weeklyIncome;
	}

	/**
	 * @param income
	 *            name="income"
	 */
	public void setIncome(BigDecimal income) {
		this.weeklyIncome = income;
	}

	/**
	 * @return name="householdRelationship"
	 */
	public HouseholdRelationship getHouseholdRelationship() {
		return this.householdRelationship;
	}

	/**
	 * @param householdRelationship
	 *            name="householdRelationship"
	 */
	public void setHouseholdRelationship(
			HouseholdRelationship householdRelationship) {
		this.householdRelationship = householdRelationship;
	}

	/**
	 * @return name="occupation"
	 */
	public Occupation getOccupation() {
		return this.occupation;
	}

	/**
	 * @param occupation
	 *            name="occupation"
	 */
	public void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}

	/**
	 * @return name="transportModeToWork"
	 */
	public TransportModeToWork getTransportModeToWork() {
		return this.transportModeToWork;
	}

	/**
	 * @param transportModeToWork
	 *            name="transportModeToWork"
	 */
	public void setTransportModeToWork(TransportModeToWork transportModeToWork) {
		this.transportModeToWork = transportModeToWork;
	}

	/**
	 * @return name="highestEduFinished"
	 */
	public HighestEduFinished getHighestEduFinished() {
		return this.highestEduFinished;
	}

	/**
	 * @param highestEduFinished
	 *            name="highestEduFinished"
	 */
	public void setHighestEduFinished(HighestEduFinished highestEduFinished) {
		this.highestEduFinished = highestEduFinished;
	}

	/**
	 * @return name="hholdCategory"
	 */
	public Category getHholdCategory() {
		return this.hholdCategory;
	}

	/**
	 * @param hholdCategory
	 *            name="hholdCategory"
	 */
	public void setHholdCategory(Category hholdCategory) {
		this.hholdCategory = hholdCategory;
	}

	public int getHouseholdId() {
		return this.householdId;
	}

	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}

	/*
	 * Code by Appu on 11/01/2012 (Below)
	 */

	public void setSatisfaction(double satisfaction) {
		this.satisfaction = satisfaction;
	}

	public double getSatisfaction() {
		return this.satisfaction;
	}

	// ==========================================================================================================
	/**
	 * constructs from a syntheticPopualtion which is a table mapped object.
	 * 
	 * @param syntheticPopulation
	 *            a table mapped object from synthetic_population table
	 */
	public Individual(SyntheticPopulationEntity syntheticPopulation) {
		setId(syntheticPopulation.getId());
		setAge(syntheticPopulation.getAge());
		setGender(syntheticPopulation.getGender() == 1 ? Gender.Male
				: Gender.Female);
		setIncome(new BigDecimal(syntheticPopulation.getIncome()));
		setPreserveIncome(new BigDecimal(syntheticPopulation.getIncome()));
		setHouseholdRelationship(HouseholdRelationship
				.identify(syntheticPopulation.getHouseholdRelationship().trim()));
		if (syntheticPopulation.getIncome().doubleValue() > 0) {
			setOccupation(Occupation.random());
		} else {
			setOccupation(Occupation.InadequatelyDescribedOrNotStated);
		}

		setTransportModeToWork(TransportModeToWork.random());
		setHighestEduFinished(HighestEduFinished.random());
		setHholdCategory(Category.identify(syntheticPopulation
				.getHouseholdType().trim()));
		setHouseholdId(syntheticPopulation.getHouseholdId());

		// no dwellings in the following travel zones

		setLivedTravelZoneid(syntheticPopulation.getTravelZone());
	}

	public Individual(IntermediateSyntheticPopulationEntity syntheticPopulation) {
		setId(syntheticPopulation.getId());

		setHouseholdId(syntheticPopulation.getHouseholdId());

		setLivedTravelZoneid(syntheticPopulation.getTravelZone());
		setIncome(BigDecimal.ZERO);

	}

	// ============== SCHEDULE METHODS ==============
	/**
	 * Build up the action of a agent in one tick: making travel mode choice.
	 * 
	 * @author Vu Lam Cao
	 */
	public void step(TravelModeSelection travelModeSelection) {

		getTravelDiariesWeekdays();

		/*
		 * only make the Travel mode decision when this individual has travel
		 * diary, and NOT the under 15 child
		 */
		if (this.travelDiariesWeekdays != null
				&& this.getHouseholdRelationship() != HouseholdRelationship.U15Child) {

			travelmodeChoice(travelModeSelection);
		}
	}

	// ==========================================================================================================


	public Individual(int id, int age, Gender gender, BigDecimal income,
			HouseholdRelationship householdRelationship, Occupation occupation,
			TransportModeToWork transportModeToWork,
			HighestEduFinished highestEduFinished, Category hholdCategory,
			int householdId, int livedTravelZoneid) {
		super();
		this.id = id;
		this.age = age;
		this.gender = gender;
		this.weeklyIncome = income;
		this.householdRelationship = householdRelationship;
		this.occupation = occupation;
		this.transportModeToWork = transportModeToWork;
		this.highestEduFinished = highestEduFinished;
		this.hholdCategory = hholdCategory;
		this.householdId = householdId;
		this.livedTravelZoneid = livedTravelZoneid;
	}

	/**
	 * Checks if the individual remains alive or dies.
	 * 
	 * @param lifeEventProbability
	 * @param probability
	 * 
	 * @return true if the individual remains alive, false otherwise
	 * 
	 * @author Appu - modified by Johan Barthelemy
	 */
	public boolean isAlive(LifeEventProbability lifeEventProbability,
			Random probability) {

		double deathProbability = lifeEventProbability
				.getDeathProbabilityByAgeSex(getAge(), getGender());
		double randomProbability = probability.nextDouble();

		if (deathProbability < randomProbability) {
			return true;
        }

        return false;
	}

	/**
	 * if the agent do not has an income, it assumes that he or she does not
	 * have a job.
	 * 
	 * @return
	 * @author qun
	 */
	public boolean haveJob() {
		if (this.weeklyIncome.doubleValue() == 0) {
			return true;
        }
		return false;
	}

	/**
	 * determines whether this individual get a job.
	 * 
	 * @param lifeEventProbability
	 * @param probability
	 * @return
	 */
	public boolean isJobGained(LifeEventProbability lifeEventProbability,
			Random probability) {

		if (getAge() >= 15 && getAge() <= 59) {

			double jobGainProbability = lifeEventProbability
					.getJobGainProbabilityByAgeSex(getAge(), getGender());

			double randomProbability = probability.nextDouble();

			if (jobGainProbability > randomProbability) {
				return true;
			}
		}
		return false;

	}

	/**
	 * determines whether this individual lost the job.
	 * 
	 * @param lifeEventProbability
	 * @param probability
	 * @return
	 */
	public boolean isJobLost(LifeEventProbability lifeEventProbability,
			Random probability) {

		if (getAge() < 15 || getAge() > 59) {
			return false;
		}

		double jobLossProbability = lifeEventProbability
				.getJobLossProbabilityByAgeSex(getAge(), getGender());

		double randomProbability = probability.nextDouble();

		if (jobLossProbability > randomProbability) {
			return true;
        }

        return false;
	}

	/**
	 * Assigns this agent a new job. if he got a job before, his salary is same
	 * to previous one, other wise is average income.
	 * 
	 * @param random
	 * @param previousIncome
	 */
	public void getNewJob(Random random, BigDecimal previousIncome) {

		int randInt = random.nextInt(Occupation.values().length - 1);

		setOccupation(Occupation.values()[randInt]); // A bit indirect:
		// the -1 is added
		// so that the last
		// logger.debug("Agent got new job"); // Occupation option
		// "Inadequately desc..."
		// won't be considered.
		if (previousIncome == null || previousIncome.doubleValue() == 0) {
			setIncome(new BigDecimal(AVERAGE_INCOME));// set a average income
		} else {
			setIncome(previousIncome);
		}

	}

	/**
	 * This method update the job status of an individual given a
	 * LifeEventProbability.
	 * 
	 * @param aLifeEventProbability
	 * @param aRandom
	 * 
	 * @author Johan Barthelemy
	 * @return
	 */
	public boolean updateJob(LifeEventProbability aLifeEventProbability,
			Random aRandom) {

		boolean isMove = false;

		/* Individual has a job */

		if (this.haveJob()) {

			// maybe loose it
			if (this.isJobLost(aLifeEventProbability, aRandom)) {
				this.loseCurrentJob();
			} else {
				// or maybe change it
				if (getAge() >= 15 && getAge() <= 59
						&& isJobGained(aLifeEventProbability, aRandom)) {
					this.getNewJob(aRandom, getPreserveIncome());
					logger.info(getId() + " change to a new job");

					if (getHouseholdRelationship() == HouseholdRelationship.Married // only
							// a
							// change
							// to
							// parents
							// job
							// status
							// is
							// to
							// affect
							// household
							// relocation
							|| getHouseholdRelationship() == HouseholdRelationship.DeFacto) {
						isMove = true;
					}
				}

			}

		}

		/* Individual has not a job */

		else if (getAge() >= 15 || getAge() <= 59) {

			// maybe find one
			if (this.isJobGained(aLifeEventProbability, aRandom)) {
				this.getNewJob(aRandom, getPreserveIncome());
				logger.info(getId() + " got a new job");

				if (getHouseholdRelationship() == HouseholdRelationship.Married // only
						// a
						// change
						// to
						// parents
						// job
						// status
						// is
						// to
						// affect
						// household
						// relocation
						|| getHouseholdRelationship() == HouseholdRelationship.DeFacto) {
					isMove = true;
				}
			}
		}

		return isMove;

	}

	/**
	 * an agent lose his or her job.
	 */
	public void loseCurrentJob() {
		setOccupation(Occupation.InadequatelyDescribedOrNotStated);
		setIncome(BigDecimal.ZERO);
		logger.info(getId() + " lost current job");
	}

	/**
	 * Update the education level of an individual.
	 * 
	 * @param aLifeEventProbability
	 * @param aRandom
	 * 
	 * @author Johan Bartelemy
	 */
	public void updateEducation(LifeEventProbability aLifeEventProbability,
			Random aRandom) {

		double proba = aRandom.nextDouble();

		if ((this.getHighestEduFinished() != HighestEduFinished.PostgraduateDegree)
				&& (proba <= aLifeEventProbability
						.getGetHigherEducationProbabilityByAgeSex(getAge(),
								getGender()))) {

			switch (this.getHighestEduFinished()) {
			case LevelOfEducationNotStated:
				this.setHighestEduFinished(HighestEduFinished.CertificateIAndIId);
				break;
			case CertificateIAndIId:
				this.setHighestEduFinished(HighestEduFinished.CertificateIIIAndIVc);
				break;
			case CertificateIIIAndIVc:
				this.setHighestEduFinished(HighestEduFinished.CertificateNfd);
				break;
			case CertificateNfd:
				this.setHighestEduFinished(HighestEduFinished.AdvancedDiplomaAndDiploma);
				break;
			case AdvancedDiplomaAndDiploma:
				this.setHighestEduFinished(HighestEduFinished.BachelorDegree);
				break;
			case BachelorDegree:
				this.setHighestEduFinished(HighestEduFinished.GraduateDiplomaAndGraduateCertificate);
				break;
			case GraduateDiplomaAndGraduateCertificate:
				this.setHighestEduFinished(HighestEduFinished.PostgraduateDegree);
				break;
			default:
				this.setHighestEduFinished(HighestEduFinished.LevelOfEducationInadequatelyDescribed);
				break;
			}

		}

	}

	/**
	 * Update the income of an individual.
	 * 
	 * @author Johan Barthelemy
	 * @deprecated
	 */
	@Deprecated
	public void updateIncome(LifeEventProbability aLifeEventProbability,
			Random aRandom) {
		// Maybe we don't even need a LifeEventProbability here...
	}

	/**
	 * Checks if an individual is willing to get married. Only individual with
	 * HouseholdRelationship equals to
	 * 
	 * LoneParent O15Child Relative NonRelative GroupHhold LonePerson
	 * 
	 * can become married, with a probability depending on the age and gender of
	 * the individual.
	 * 
	 * @param lifeEventProbability
	 * @param probability
	 * @return true if the individual wants to become married, false otherwise
	 * 
	 * @author Gun - modified by Johan Barthelemy
	 */
	public boolean isGettingMarried(LifeEventProbability lifeEventProbability,
			Random probability) {

		logger.trace(getAge() + ":" + getGender().toString());

		double marriageProbability = lifeEventProbability
				.getMarriageProbabilityByAgeSex(getAge(), getGender());

		if (getAge() >= 18) {
			marriageProbability *= 2;
		}

		double randomProbability = probability.nextDouble();

		if (marriageProbability > randomProbability) {
			return true;
        }

		return false;

	}

	/**
	 * determines whether this agent get divorced.
	 * 
	 * @param lifeEventProbability
	 * @param probability
	 * @return
	 */
	public boolean isGettingDivorced(LifeEventProbability lifeEventProbability,
			Random probability) {

		// logger.debug(getAge());
		if (getAge() < 16 || getAge() > 69) {
			return false;
		}

		double divorceProbability = lifeEventProbability
				.getDivorceProbabilityByAgeSex(getAge(), getGender());

		double randomProbability = probability.nextDouble();

		if (divorceProbability > randomProbability) {
			return true;
        }
		return false;

	}

	/**
	 * Check if the individual is willing to leaves its current household. The
	 * probability of leaving is derived from
	 * http://www.abs.gov.au/AUSSTATS/abs@.nsf
	 * /Lookup/4102.0Main+Features50June+2009
	 * 
	 * Only non relatives and independent children over 15 between 18 and 34 can
	 * leave the household.
	 * 
	 * @return true if the individual leaves his/her household, false otherwise
	 * 
	 * @author Johan Barthelemy
	 */
	public boolean isLeavingHousehold(Random sRandom) {

		boolean result = false;

		/* Probability of leaving house */

		double probability18To19 = 28.0;
		double probability20To24 = 52.8;
		double probability25To29 = 83.2;
		double probability30To34 = 91.8;

		double aRandomProba = sRandom.nextDouble();

		switch (this.getHouseholdRelationship()) {
		case O15Child:
		case NonRelative:
			if (this.getAge() <= 17) {
				result = false;
			} else if (this.getAge() <= 20 && aRandomProba <= probability18To19) {
				result = true;
			} else if (this.getAge() <= 24 && aRandomProba <= probability20To24) {
				result = true;
			} else if (this.getAge() <= 29 && aRandomProba <= probability25To29) {
				result = true;
			} else if (this.getAge() > 29 && aRandomProba <= probability30To34) {
				result = true;
			}
			break;
		default:
			result = false;
		}

		return result;

	}


	/**
	 * determines whether this individual get new babies.
	 * 
	 * @param lifeEventProbability
	 * @param probability
	 * @param numberOfChild
	 * @return
	 */
	public boolean isGettingChild(LifeEventProbability lifeEventProbability,
			Random probability, int numberOfChild) {

		if (this.getHouseholdRelationship() == HouseholdRelationship.Visitor) {
			return false;
		}

		double reproductionProbability = lifeEventProbability
				.getReproductionProbabilityByAgeSex(getAge(), numberOfChild);// for
		// test
		double randomProbability = probability.nextDouble();

		if (reproductionProbability > randomProbability) {
			return true;
        }

		return false;
	}

	/**
	 * This method handle the dying process of an individual by returning the id
	 * of the dead individual and removing it from the pool of individual.
	 * 
	 * @param aIndPool
	 *            pool of individual.
	 * @return id of the dead individual.
	 * 
	 * @author Johan Barthelemy
	 */
	public int passAway(IndividualPool aIndPool) {
		aIndPool.remove(this);
		logger.info(getId() + " died");
		return this.getId();
	}

	/**
	 * makes agent grow up
	 */
	public void growUp() {
		this.setAge(this.getAge() + 1);
	}


	/**
	 * Checks if an individual is a child.
	 * 
	 * @return true if the individual is a child, false otherwise.
	 * 
	 * @author Johan Barthelemy
	 */
	public boolean isChild() {

		boolean result;
		switch (this.getHouseholdRelationship()) {
		case O15Child:
		case U15Child:
		case Student:
			result = true;
			break;
		default:
			result = false;
			break;
		}
		return result;

	}

	/**
	 * 
	 * @param individual
	 */
	public void copy(Individual individual) {
		this.householdRelationship = individual.householdRelationship;
		this.hholdCategory = individual.hholdCategory;
		this.householdId = individual.householdId;
		this.livedTravelZoneid = individual.livedTravelZoneid;
		this.satisfaction = individual.satisfaction;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Individual [" + this.id + " in " + this.householdId + ":"
				+ this.age + "," + this.gender + ",$" + this.weeklyIncome
				+ ", " + this.householdRelationship + ", " + this.hholdCategory
				+ ", " + this.livedTravelZoneid + "]";
	}

	/**
	 * Making a travel mode choice based on the algorithm of fixed cost and
	 * variable cost.
	 * 
	 * @author vlcao
	 */
	public void travelmodeChoice(TravelModeSelection travelModeSelection) {
		
		try {

			for (int i = 0; i < this.travelDiariesWeekdays.length; i++) {

				/*
				 * TravelDiaries columns are:
				 * 0.[travel_id]1.[individual_id]2.[household_id]
				 * 3.[age]4.[gender]5.[income]6.[origin]7.[destination]
				 * 8.[start_time]9.[end_time]10.[duration]
				 * 11.[travel_mode]12.[purpose]13.[vehicle_id] 14.[trip_id]
				 */

				TravelModes oldMode = TravelModes.classify(this.travelDiariesWeekdays[i][11]);
				String hhPersonTrip = this.travelDiariesWeekdays[i][2] + "_"
						+ this.travelDiariesWeekdays[i][1] + "_"
						+ this.travelDiariesWeekdays[i][14];
				
                Household household = main.getHouseholdPool().getByID(householdId);
                if (household == null) {
                    logger.error("This individual's household no longer exists!! Individual: " + toString());
                    return;
                }

				double grossIncome = household.getGrossIncome()
						.doubleValue();

				/* travel mode choice process */
				if (main.getTripTimePlanMap().containsKey(hhPersonTrip)) {
					double[] deltaCosts = main.getTripTimePlanMap().get(
							hhPersonTrip);

					// calculate values for the array ultTravelModes
					// with order of columns as
					// "carDriver, carPassenger, bus, light rail, taxi, walk, bicycle"
					double[] ultTravelModes = new double[7];
					for (int j = 0; j < HardcodedData.betaValues[0].length; j++) {
						/* for columns as "carDriver, carPassenger" */
						if (j < 2) {
							ultTravelModes[j] = HardcodedData.betaValues[0][j]
									+ HardcodedData.betaValues[1][j]
									* deltaCosts[0]
									+ HardcodedData.betaValues[2][j]
									* deltaCosts[1]
									+ HardcodedData.betaValues[3][j]
									* grossIncome;
						}
						/* for columns as "bus, light rail" */
						if (j == 2) {
							ultTravelModes[j] = ultTravelModes[j + 1] = HardcodedData.betaValues[0][j]
									+ HardcodedData.betaValues[1][j]
									* deltaCosts[0]
									+ HardcodedData.betaValues[2][j]
									* deltaCosts[1]
									+ HardcodedData.betaValues[3][j]
									* grossIncome;
						}
						/* for columns as "taxi, walk, bicycle" */
						if (j > 2) {
							ultTravelModes[j + 1] = HardcodedData.betaValues[0][j]
									+ HardcodedData.betaValues[1][j]
									* deltaCosts[0]
									+ HardcodedData.betaValues[2][j]
									* deltaCosts[1]
									+ HardcodedData.betaValues[3][j]
									* grossIncome;
						}

					}

                    if (logger.isTraceEnabled()) {
					    main.getUltTravelModeMap().put(hhPersonTrip, ultTravelModes);
                    }

					double sumAllExpOfUlts = 0;
					// calculate the sum of all utilities of all modes
					// with order of columns as
					// "carDriver, carPassenger, bus, light rail, taxi, walk, bicycle"
                    for (double ultTravelMode : ultTravelModes) {
                        sumAllExpOfUlts = sumAllExpOfUlts
                                + Math.exp(ultTravelMode);
                    }

					// skip all the calculation when the
					// "sumAllExpOfUlts = Infinity"
					if (Double.isInfinite(sumAllExpOfUlts)|| Double.isNaN(sumAllExpOfUlts)) {
						main.getProTravelModeMap().put(hhPersonTrip,
								new double[] { 0, 0, 0, 0, 0, 0, 0 });
						main.getCumProTravelModeMap().put(hhPersonTrip,
								new double[] { 0, 0, 0, 0, 0, 0, 0 });
						continue;
					}

					double[] proTravelModes = new double[7];
					// calculate the probability for all modes
					// with order of columns as
					// "carDriver, carPassenger, bus, light rail, taxi, walk, bicycle"
					for (int j = 0; j < proTravelModes.length; j++) {
						proTravelModes[j] = Math.exp(ultTravelModes[j])
								/ sumAllExpOfUlts;
					}
					main.getProTravelModeMap()
							.put(hhPersonTrip, proTravelModes);

					double[] culProTravelModes = new double[8];
					// calculate values for the array culProTravelModes
					// with order of columns as
					// "carDriver, carPassenger, bus, light rail, taxi, walk, bicycle"
					culProTravelModes[1] = proTravelModes[0];
					for (int j = 1; j < culProTravelModes.length-1; j++) {
						culProTravelModes[j+1] = proTravelModes[j]
								+ culProTravelModes[j];
					}
					main.getCumProTravelModeMap().put(hhPersonTrip, culProTravelModes);

					// assign the percentage for choosing the new travel mode
					// as the "nextDouble" message will give back a random real
					// number between 0 and 1
					TravelModes newMode = oldMode;
					double chosenPercentage = HardcodedData.random.nextDouble();

					// pick up the new travel mode
					for (int j = 1; j < culProTravelModes.length; j++) {
						if (chosenPercentage <= culProTravelModes[j] 
								&& chosenPercentage > culProTravelModes[j-1]) {
							switch (j) {
							case 1:
                                newMode = TravelModes.CarDriver;
								break;

							case 2:
								newMode = TravelModes.CarPassenger;
								break;

							case 3:
								newMode = TravelModes.Bus;
								break;

							case 4:
								newMode = TravelModes.LightRail;
								break;

							case 5:
								newMode = TravelModes.Taxi;
								break;

							case 6:
								newMode = TravelModes.Walk;
								break;

							case 7:
								newMode = TravelModes.Bike;
								break;

							default:
								newMode = TravelModes.Other;
								break;
							}
							
							// end the loop "for"
							break;
							
						}
					}

					/*
					 * switch on and off for the light rail option to choose the
					 * travel mode
					 */
					// when the scenario is "base", individual will chose bus
					// instead of light rail
					if (TravelModes.LightRail.equals(newMode)
							&& main.getScenarioName().equalsIgnoreCase("base")) {
						newMode = TravelModes.Bus;
					}

					// update the new travel mode
					if (! oldMode.equals(newMode)) {
						this.travelDiariesWeekdays[i][11] = newMode.getIntValue();
					}
				} 
			}// end loop "for" of travelDiariesWeekdays

		} catch (Exception e) {
            logger.error("Exception caught", e);
		}
	}
}
