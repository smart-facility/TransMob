package core.synthetic;

import hibernate.postgis.TravelZonesFacilitiesDAO;
import hibernate.postgis.TravelZonesFacilitiesEntity;
import hibernate.postgres.MortgageInterestRateDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import core.model.TextFileHandler;
import hibernate.postgres.ImmigrationRateDAO;
import hibernate.postgres.ImmigrationRateEntity;
import hibernate.postgres.NewTendbyBerdrEntity;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import core.ApplicationContextHolder;
import core.ArrayHandler;
import core.HardcodedData;
import core.HardcodedData.OwnershipStatus;
import core.ModelMain;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.dwelling.Duty;
import core.synthetic.dwelling.Dwelling;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
import core.synthetic.immgrants.ImmigrantGenerator;
import core.synthetic.individual.Individual;
import core.synthetic.individual.lifeEvent.LifeEventProbability;
import core.synthetic.liveability.LiveabilityUtility;
import core.synthetic.relocationTrigger.RelocationTrigger;
import core.synthetic.traveldiary.TravelDiaryColumns;


/**
 * The pool of household for storing and manipulating all the households.
 * 
 * @author qun
 * 
 */
public class HouseholdPool extends StatisticalPool {

    private static final Logger logger = Logger.getLogger(HouseholdPool.class);

	private Map<Integer, Household> households;

	private LiveabilityUtility liveabilityUtility;

	private List<Integer> randomTravelZoneId;

	private final ModelMain main = ModelMain.getInstance();

    private final MortgageInterestRateDAO mortgageRatesDAO;

	public HouseholdPool() {
		this.households = new ConcurrentHashMap<>();
		this.maxId = 0;
		this.liveabilityUtility = LiveabilityUtility.getInstance();
        this.mortgageRatesDAO = ApplicationContextHolder.getBean(MortgageInterestRateDAO.class);
		randomTravelZoneId();
	}

	public List<Integer> getRandomTravelZoneId() {
		return this.randomTravelZoneId;
	}

	/**
	 * Adds a household into the pool and updates attributes.
	 * 
	 * @param household
	 *            the household will be added into the pool.
	 */

	@SuppressWarnings("boxing")
	private void addHousehold(Household household) {
		/*
		 * In the simplified net migration, some households in the pool are
		 * duplicated. The following if statement is commented out so that a
		 * household exactly similar to one already existent in the pool can be
		 * added to the pool.
		 */

		if (this.households.containsKey(this.maxId + 1)) {
			Set<Integer> keys = this.households.keySet();
			this.maxId = Collections.max(keys);
		}

		household.setId(this.maxId + 1);

		this.households.put(household.getId(), household);

		for (Individual individual : household.getResidents()) {
			individual.setHouseholdId(household.getId());
		}

		this.maxId++;
	}

	/**
	 * Removes a household into the pool and updates attributes.
	 * 
	 * @param household
	 *            the household will be removed out of the pool.
	 */

	@SuppressWarnings("boxing")
	private void removeHousehold(Household household) {
		this.households.remove(household.getId());
	}

	/**
	 * Removes a household from householdPool and individualPool.
	 * 
	 * @param household
	 * @param individualPool
	 */
	@SuppressWarnings("boxing")
	public void removeHousehold(Household household, IndividualPool individualPool, DwellingAllocator dwellingAllocator) {

		// removes records in hmHhPersonTripTDPurpose that belong to the household being removed
		for (Individual res : household.getResidents()) {
			int[][] td = res.getTravelDiariesWeekdays();
			if (td!=null) {
				for (int i=0; i<=td.length-1; i++) {
					String hHoldPersonTripTD = String.valueOf(household.getId()) + "_"  
	                        + String.valueOf(res.getId()) + "_" + String.valueOf(td[i][TravelDiaryColumns.TripID_Col.getIntValue()]); 
					if (ModelMain.getInstance().getHmHhPersonTripTDPurpose().containsKey(hHoldPersonTripTD)) {
						ModelMain.getInstance().getHmHhPersonTripTDPurpose().remove(hHoldPersonTripTD);
					}
				}
			}
		}
		
		Dwelling dwelling = dwellingAllocator.getDwellingPool().getDwellings().get(household.getDwellingId());
		if (dwelling!=null) {
			dwelling.setLivedHouseholdId(-1);
        }
		
		logger.debug("Remove: " + household.toString());

		for (Individual individual : household.getResidents()) {
			if (individual != null) {
				individualPool.remove(individual);
			}

		}
		this.households.remove(household.getId());
		
		
	}

	public void setHouseholds(Map<Integer, Household> households) {
		this.households = households;
	}

	/**
	 * Populates household pool by individual pool.
	 * 
	 * @param individualPool
	 *            the individual pool will be used for populate the household
	 *            pool
	 * 
	 * 
	 */
	public void generatePool(IndividualPool individualPool) {
		for (Individual individual : individualPool.getIndividuals().values()) {
			int householdId = individual.getHouseholdId();
			Household household = this.households.get(householdId);
			if (household == null) {
				Household newHousehold = new Household();
				newHousehold.setId(householdId);
				newHousehold.setDwellingId(0);

				ArrayList<Individual> residents = new ArrayList<Individual>();
				newHousehold.setResidents(residents);
				newHousehold.getResidents().add(individual);
				newHousehold.setCategory(individual.getHholdCategory());
				newHousehold.setGrossIncome();
				this.households.put(householdId, newHousehold);
			} else {
				household.getResidents().add(individual);
				household.setGrossIncome();
			}

		}

		this.maxId = this.households.size();
	}

	/**
	 * Gets dwelling index for each household from Data base.
	 * 
	 * @param dwellingAllocator
	 */
	public void updateDwellingsIndex(DwellingAllocator dwellingAllocator) {

		for (Dwelling dwelling : dwellingAllocator.getDwellingPool()
				.getDwellings().values()) {

			if (this.households.containsKey(dwelling.getLivedHouseholdId())) {
				Household household = getByID(dwelling.getLivedHouseholdId());
				household.setDwellingId(dwelling.getId());
				household.setLivedTravelZone(dwelling.getTravelZoneId());
			}
		}
	}

	/**
	 * allocates dwellings for household and stores household id in database for
	 * each dwelling.
	 * 
	 * @param dwellingAllocator
	 */
	public void allocateDwellingsForInitialisation(DwellingAllocator dwellingAllocator, int year) {

		ArrayList<Household> remindHouseholds = new ArrayList<>();

		SortedSet<Integer> sortHouseholds = sortHouseholds();

		List<Integer> list = new ArrayList<>();

		list.addAll(sortHouseholds);

		for (Integer id : list) {
			Household household = getByID(id);

			int livedTravelZoneid = household.getResidents().get(0).getLivedTravelZoneid();

			boolean successful = dwellingAllocator.allocateAvailableDwelling(livedTravelZoneid, year, household);

			if (!successful || household.getDwellingId() == 0) {

				remindHouseholds.add(household);
			}
		}

		int size = remindHouseholds.size();
		logger.debug(size);

		findAPlace(dwellingAllocator, remindHouseholds);

		logger.debug(remindHouseholds.size());
	}

	private void findAPlace(DwellingAllocator dwellingAllocator,
			List<Household> remindHouseholds) {

		logger.debug("Find a place");
		for (Household household : remindHouseholds) {
			household.setLivedTravelZone(0);
			
			List<Integer> tempRandomTravelZoneId = getRandomTravelZoneId();
            for (Integer travelZoneId : tempRandomTravelZoneId) {
                dwellingAllocator.allocateAvailableDwelling(travelZoneId,
                        this.main.getCurrentYear(), household);

                if (household.getDwellingId() != 0) {
                    break;
                }

            }
            
		}
	}

	/**
	 * Generate a list of travel zone id randomly.
	 * 
	 * @return an integer arraylist stores travel zone id.
	 */
	public final void randomTravelZoneId() {
		ArrayList<Integer> candidateId = new ArrayList<>();

		for (int id : HardcodedData.travelZonesLiveable) {
			candidateId.add(id);
		}
		this.randomTravelZoneId = candidateId;
	}

	@Override
	/**
	 * returns element number.
	 * @return 
	 */
	public int getPoolNumber() {
		return households.size();
	}

	public Map<Integer, Household> getHouseholds() {
		return this.households;
	}

	/**
	 * Clears the pool.
	 */
	@Override
	public void clearPool() {
		this.households.clear();
	}

	@Override
	/**
	 * adds a household into the pool and mediator will notice the change of the attributes distribution. 
	 * @param o the household will be added into the pool
	 */
	public void add(Object o) {
		addHousehold((Household) o);
	}

	@Override
	/**
	 * Removes a household into the pool and mediator will notice the change of the attributes distribution. 
	 * @param o the household will be removed out of the pool
	 */
	public void remove(Object o) {
		removeHousehold((Household) o);
	}

	/**
	 * 
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HouseholdPool [households=" + this.households.toString() + ", maxId=" + this.maxId + ']';
	}

	/**
	 * Gets a household by it's id.
	 * 
	 * @param id
	 *            the id of a household you want to get
	 * @return the household
	 */
	public Household getByID(int id) {
		Household household = this.households.get(id);
		if (household == null) {
			return null;
		} else {
			return household;
		}

	}

	/**
	 * This method handle the household and individuals evolution for a time
	 * step, in the following order.
	 * 
	 * 1. Individuals are getting older 2. Births 3. Deaths 4. Education 5. Jobs
	 * 6. Income 7. People leaving household 8. Divorce 9. Marriages
	 * 
	 * @param aIndPool
	 *            the pool of individuals.
	 * @param aLifeEventProbability
	 *            the life event probabilities.
	 * @param aRandom
	 *            a random number generator.
	 * 
	 * @author Johan Barthelemy edited: Qun Chen
	 */
	@SuppressWarnings("null")
	public void evolution(IndividualPool aIndPool,
			LifeEventProbability aLifeEventProbability, Random aRandom,
			int year, Map<Integer, Double> travelZoneTripsTime,
			DwellingAllocator dwellingAllocator,
			Set<Household> relocatedHouseholds,
			List<String[]> equityData,
			ArrayList<String[]> deadPeople,
			ArrayList<String[]> newborns) {

		this.liveabilityUtility.setPopulation(aIndPool);

		Map<Household, Integer> moveReason = Maps.newHashMap();

		Set<Individual> menIndividuals = Sets.newCopyOnWriteArraySet();
		Set<Individual> womenIndividuals = Sets.newCopyOnWriteArraySet();

		/*
		 * evolveStatus
		 */
		if (logger.isTraceEnabled()) {
			// population in householdPool before evolutionOnHouseholdLevel
			ArrayList<String[]> hhPoolBeforeEvolHholdLvl = new ArrayList<>();
			for (Household hhold : ModelMain.getInstance().getHouseholdPool().getHouseholds().values()) {
				Dwelling dwelling = dwellingAllocator.getDwellingPool().getDwellings().get(hhold.getDwellingId());
				for (Individual indiv : hhold.getResidents()) {
					hhPoolBeforeEvolHholdLvl.add(new String[]{String.valueOf(indiv.getId()),
                            String.valueOf(indiv.getAge()),
                            indiv.getGender().toString(),
                            String.valueOf(indiv.getIncome().doubleValue()),
                            (indiv.getHouseholdRelationship() == null) ? "" : indiv.getHouseholdRelationship().toString(),
                            String.valueOf(indiv.getHouseholdId()),
                            (indiv.getHholdCategory() == null) ? "" : indiv.getHholdCategory().toString(),
                            String.valueOf(hhold.getGrossIncome().doubleValue()),
                            (hhold.getEquity() == null) ? "" : String.valueOf(hhold.getEquity()),
                            (hhold.getSavingsForHouseBuying() == null) ? "" : String.valueOf(hhold.getSavingsForHouseBuying()),
                            String.valueOf(hhold.getTenure()),
                            String.valueOf(hhold.calculateNumberOfRoomNeed()),
                            hhold.getOwnershipStatus() == null ? "null" : hhold.getOwnershipStatus().toString(),
                            (dwelling == null) ? "" : String.valueOf(dwelling.getLivedHouseholdId()),
                            (dwelling == null) ? "" : String.valueOf(dwelling.getTravelZoneId()),
                            (dwelling == null) ? "" : String.valueOf(dwelling.getNumberOfRooms())});
				}
			}
			TextFileHandler.writeToCSV(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_hhPool_b4_evolHholdLvl.csv", hhPoolBeforeEvolHholdLvl, false);
		}
		
		evolveStatus(aRandom);

		ArrayList<Individual> orphans = new ArrayList<>();
//		ArrayList<String[]> deadPeople = new ArrayList<>();
//		ArrayList<String[]> newborns = new ArrayList<>();
		
		evolutionOnHouseholdLevel(aIndPool, aLifeEventProbability, aRandom,
				dwellingAllocator, moveReason, menIndividuals,
				womenIndividuals, orphans, deadPeople, relocatedHouseholds, newborns);
		
		
//		if (!deadPeople.isEmpty()) {
//			TextFileHandler.writeToCSV(
//					String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_deadPeople.csv",
//					deadPeople, true);
//		}
//		if (!newborns.isEmpty()) {
//			TextFileHandler.writeToCSV(
//					String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_newborns.csv",
//					newborns, true);
//		}
			
		if (logger.isTraceEnabled()) {
			if (!orphans.isEmpty()) {
				ArrayList<String[]> orphanStr = new ArrayList<>();
				for (Individual kid : orphans) {
					orphanStr.add(new String[] {String.valueOf(kid.getId()),
							String.valueOf(kid.getAge()),
							kid.getGender().toString(),
							kid.getHouseholdRelationship().toString()});
				}
				TextFileHandler.writeToCSV(
						String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_orphans.csv",
						orphanStr, false);
			}
		}


		marry(menIndividuals, womenIndividuals, aRandom);
		
		/*
		 * assign orphans to households
		 */
		if (!orphans.isEmpty()) {
			assignOrphansToHholds(orphans);
        }
		
		updateHouseholdPool(aIndPool);
		logger.debug("individual Pool number" + aIndPool.getIndividuals().size());
		logger.debug("household Pool number" + totalIndividual());
		
		updateIncome();
		calculateEquity(year, equityData);
		
		/*
		 * picks up households that need to be relocate as a results of the MNL model
		 */
		aIndPool.calculateIndividualSatisfaction(this.liveabilityUtility, travelZoneTripsTime, this);

		RelocationTrigger relocationTrigger = new RelocationTrigger();

		Map<Integer, int[]> previousResults = null;

		if (year == HardcodedData.START_YEAR) {
			previousResults = relocationTrigger.getBaseData();
		} else {
			previousResults = relocationTrigger.getDataForYear(year - 1);
		}

		for (Household household : this.households.values()) {
			int[] previous = previousResults.get(household.getId());
			if (previous == null) {
				relocatedHouseholds.add(household);
			} else {
				
				double probability = relocationTrigger.calculateProbability(household, previous);
				
				double randomValue = HardcodedData.random.nextDouble();

				if (probability > randomValue) {
					relocatedHouseholds.add(household);
				}
			}
		}
		
		/*
		 * picks up households that need to be relocate because the dwellings where they're living in are no longer available for this year.
		 */
		getListOfRelocHholdsBecauseDwellingsNotAvailable(relocatedHouseholds, dwellingAllocator, year+1);
		
		/*
		 * population in householdPool after evolution
		 */
		if (logger.isTraceEnabled()) {
			ArrayList<String[]> hhPoolAfterEvolution = new ArrayList<>();
			for (Household hhold : ModelMain.getInstance().getHouseholdPool().getHouseholds().values()) {
				Dwelling dwelling = dwellingAllocator.getDwellingPool().getDwellings().get(hhold.getDwellingId());
				for (Individual indiv : hhold.getResidents()) {
					hhPoolAfterEvolution.add(new String[]{String.valueOf(indiv.getId()),
                            String.valueOf(indiv.getAge()),
                            indiv.getGender().toString(),
                            String.valueOf(indiv.getIncome().doubleValue()),
                            (indiv.getHouseholdRelationship() == null) ? "" : indiv.getHouseholdRelationship().toString(),
                            String.valueOf(indiv.getHouseholdId()),
                            (indiv.getHholdCategory() == null) ? "" : indiv.getHholdCategory().toString(),
                            String.valueOf(hhold.getGrossIncome().doubleValue()),
                            (hhold.getEquity() == null) ? "" : String.valueOf(hhold.getEquity()),
                            (hhold.getSavingsForHouseBuying() == null) ? "" : String.valueOf(hhold.getSavingsForHouseBuying()),
                            String.valueOf(hhold.getTenure()),
                            String.valueOf(hhold.calculateNumberOfRoomNeed()),
                            (dwelling == null) ? "" : String.valueOf(dwelling.getLivedHouseholdId()),
                            (dwelling == null) ? "" : String.valueOf(dwelling.getTravelZoneId()),
                            (dwelling == null) ? "" : String.valueOf(dwelling.getNumberOfRooms())});
				}
			}
			TextFileHandler.writeToCSV(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_hhPool_after_evolution.csv", 
					hhPoolAfterEvolution, false);
		}
	}

	
	/**
	 * checks if the dwelling ID of a household is in map dwellingAllocator.dwellingsIDMap for this year.
	 * If not put this household into list of relocatedHouseholds.
	 * 
	 * @param relocatedHouseholds
	 * @param dwelingAllocator
	 * @param year
	 */
	private void getListOfRelocHholdsBecauseDwellingsNotAvailable(Set<Household> relocatedHouseholds, DwellingAllocator dwellingAllocator, int year) {
		ArrayList<String[]> relocHholdsNoDwellings = new ArrayList<String[]>();
		for (Household hhold : this.getHouseholds().values()) {
//			if (relocatedHouseholds.contains(hhold)) { // if this household is already in the relocatedHouseholds list, then move on to next household
//				continue; 
//			}
			
			Dwelling crnDwelling = dwellingAllocator.getDwellingPool().getDwellings().get(hhold.getDwellingId());
			if (crnDwelling==null) {
				relocatedHouseholds.add(hhold);
				hhold.setDwellingId(0);
			} else {
				String key = String.valueOf(crnDwelling.getTravelZoneId()) + "@" + String.valueOf(year) + "@" + String.valueOf(crnDwelling.getNumberOfRooms());
				if (dwellingAllocator.getDwellingPool().getDwellingsIDMap().containsKey(key)) {
					List<Integer> dwellingsIDList = dwellingAllocator.getDwellingPool().getDwellingsIDMap().get(key);
					if (!dwellingsIDList.contains(crnDwelling.getId())) {
						hhold.setDwellingId(0);
						if (!relocatedHouseholds.contains(hhold)) {
							relocatedHouseholds.add(hhold);
						}
						relocHholdsNoDwellings.add(new String[] {String.valueOf(hhold.getId()),
																String.valueOf(crnDwelling.getTravelZoneId()),
																String.valueOf(crnDwelling.getNumberOfRooms()),
																String.valueOf(year)});
					}
				}
			}
		}
		TextFileHandler.writeToCSV(String.valueOf(year)+"_relocHholdNoDwellings.csv", relocHholdsNoDwellings, false);
	}
	
	
	/**
	 * 
	 * @param year
	 * @param equityData
	 */
	private void calculateEquity(int year, List<String[]> equityData) {
		equityData.add(new String[] {"hholdID", "hholdCat", "hholdWeeklyIncome", "ownershipStatus",
				"SavingsForHouseBuying", "yearBoughtThisProperty", "equity", "yearlymortgagePayment",
				"priceCrnDwelling", "dutyPaid"});
		for (Household hhold : this.getHouseholds().values()) {
			if (hhold==null) {
                continue;
            }
			
			if (hhold.getOwnershipStatus()==null || hhold.getEquity()==null) {
				hhold.initialiseEquity();
				equityData.add(new String[] {String.valueOf(hhold.getId()), 
						hhold.getCategory().toString(), 
						String.valueOf(hhold.getGrossIncome().doubleValue()),
						(hhold.getOwnershipStatus()==null) ? "" : hhold.getOwnershipStatus().toString(),
						(hhold.getSavingsForHouseBuying()==null) ? "" : String.valueOf(hhold.getSavingsForHouseBuying().doubleValue()),
						String.valueOf(hhold.getYearBoughtThisProperties()),
						(hhold.getEquity()==null) ? "" : String.valueOf(hhold.getEquity().doubleValue()),
						(hhold.getYearlyMortgagePayment()==null) ? "" : String.valueOf(hhold.getYearlyMortgagePayment().doubleValue()),
						(hhold.getPriceCrnDwelling()==null) ? "" : String.valueOf(hhold.getPriceCrnDwelling().doubleValue()),
						(hhold.getDutyPaid()==null) ? "" : String.valueOf(hhold.getDutyPaid().doubleValue())
						});
				continue;
			}
			
			if (hhold.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.paying)) {
				double equity = hhold.getEquity().doubleValue() + hhold.getYearlyMortgagePayment().doubleValue();
				hhold.setEquity(new BigDecimal(equity));
				if (year - hhold.getYearBoughtThisProperties() == HardcodedData.maxYearMortgage && 
						year != HardcodedData.START_YEAR) {
						hhold.setOwnershipStatus(HardcodedData.OwnershipStatus.own);
						hhold.setYearBoughtThisProperties(year);
						hhold.setPrincipleValue(new BigDecimal(-1));
				}
				
				equityData.add(new String[] {String.valueOf(hhold.getId()), 
						hhold.getCategory().toString(), 
						String.valueOf(hhold.getGrossIncome().doubleValue()),
						hhold.getOwnershipStatus().toString(),
						String.valueOf(hhold.getSavingsForHouseBuying().doubleValue()),
						String.valueOf(hhold.getYearBoughtThisProperties()),
						String.valueOf(hhold.getEquity().doubleValue()),
						String.valueOf(hhold.getYearlyMortgagePayment().doubleValue()),
						String.valueOf(hhold.getPriceCrnDwelling().doubleValue()),
						String.valueOf(hhold.getDutyPaid().doubleValue())
						});
			} else if (hhold.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.rent)) {
				double addSavings = hhold.getGrossIncome().doubleValue()*52/3;
				double newSavings = hhold.getSavingsForHouseBuying().doubleValue() + addSavings;
				hhold.setSavingsForHouseBuying(new BigDecimal(newSavings));
				
				equityData.add(new String[] {String.valueOf(hhold.getId()), 
						hhold.getCategory().toString(), 
						String.valueOf(hhold.getGrossIncome().doubleValue()),
						hhold.getOwnershipStatus().toString(),
						String.valueOf(hhold.getSavingsForHouseBuying().doubleValue()),
						String.valueOf(hhold.getYearBoughtThisProperties()),
						String.valueOf(hhold.getEquity().doubleValue()),
						String.valueOf(hhold.getYearlyMortgagePayment().doubleValue()),
						String.valueOf(hhold.getPriceCrnDwelling().doubleValue()),
						String.valueOf(hhold.getDutyPaid().doubleValue()),
						});
			} else if (hhold.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.own)) {
				equityData.add(new String[] {String.valueOf(hhold.getId()), 
						hhold.getCategory().toString(), 
						String.valueOf(hhold.getGrossIncome().doubleValue()),
						hhold.getOwnershipStatus().toString(),
						String.valueOf(hhold.getSavingsForHouseBuying().doubleValue()),
						String.valueOf(hhold.getYearBoughtThisProperties()),
						String.valueOf(hhold.getEquity().doubleValue()),
						String.valueOf(hhold.getYearlyMortgagePayment().doubleValue()),
						String.valueOf(hhold.getPriceCrnDwelling().doubleValue()),
						String.valueOf(hhold.getDutyPaid().doubleValue())
						});
			}
			
		}
	}
	
	/**
	 * allocates orphans into households. The method first allocates one orphan
	 * into each household that has couple with no children. Each of the
	 * remaining orphans is allocated into each of the households having at
	 * least 1 parent with higher household income.
	 * 
	 * @param orphans
	 *            list of orphans to be allocated
	 */
	private void assignOrphansToHholds(List<Individual> orphans) {
		if (orphans==null || orphans.isEmpty()) {
            return;
        }
		
		for (Household hhold : this.households.values()) {
			if (hhold==null || hhold.getCategory()==null) {
                continue;
            }
			if (hhold.getCategory().equals(Category.HF1)) {
				hhold.addIndividual(orphans.get(0));
				orphans.remove(0);
				if (orphans.isEmpty()) {
					break;
                }
			} 
		}
		if ((!orphans.isEmpty())) {
			// sorts HF households in this.households by income
			ArrayList<Integer> hholdIDs = new ArrayList<>();
			ArrayList<Double> hholdIncomes = new ArrayList<>();
			for (Household hhold : this.households.values()) {
				if (hhold.getCategory().equals(Category.NF)) {
                    continue;
                }
				hholdIDs.add(hhold.getId());
				hholdIncomes.add(hhold.getGrossIncome().doubleValue());
			}
			int[] arrayHholdIDs = new int[hholdIDs.size()];
			double[] arrayHholdIncomes = new double[hholdIncomes.size()];
			for (int i=0; i<=arrayHholdIDs.length-1; i++) {
				arrayHholdIDs[i] = hholdIDs.get(i);
				arrayHholdIncomes[i] = hholdIncomes.get(i);
			}
			int[] idxSortedIncomes = ArrayHandler.sortedIndices(arrayHholdIncomes);
			while (!orphans.isEmpty()) {
				// assigns one orphan to each of these households until arraylist orphans is empty.
				for (int i=idxSortedIncomes.length-1; i>=idxSortedIncomes.length-1; i++) {
					if (orphans.isEmpty()) {
                        break;
                    }
					int pickedHholdID = arrayHholdIDs[idxSortedIncomes[i]];
					Household pickedHhold = this.households.get(pickedHholdID);
					if (pickedHhold.getCategory().equals(Category.HF16)) { // if the household type is HF16
						// picks an adult whose age satisfies the mindAgeParentChild
						boolean foundParent = false;
						for (Individual indiv : pickedHhold.getResidents()) {
							if (indiv.getAge()-orphans.get(0).getAge()>=18) { 
								// reassigns hhold relationship of this adult to LoneParent.
								indiv.setHouseholdRelationship(HouseholdRelationship.LoneParent);
								foundParent = true;
								break;
							}
                        }
						if (!foundParent) {
                            continue;
                        }
					}
					pickedHhold.addIndividual(orphans.get(0));
					orphans.remove(0);
				}
			}
		}
		
	}

	public void relocate(IndividualPool aIndPool, int year,
			Map<Integer, Double> travelZoneCongestion,
			DwellingAllocator dwellingAllocator,
			Set<Household> relocatedHouseholds, List<Household> removedHouseholds,
			List<String[]> removedPopStr, List<String[]> hhNotReloc, List<String[]> hhReloc,
			List<String[]> hhPoolAfterRelocation) {
		
		// ensures that the household occupying a dwelling is a valid one, 
		// i.e. that household is not null or that household is not occupying another dwelling at the same time.
//		for (Integer tz : travelZoneCongestion.keySet())
//			dwellingAllocator.checkAndCorrectDwellingOccupation(tz,this,year);

		// creates a hashmap of households and the number of bedrooms required by each household
		HashMap<Household,Integer> nRoomsNeededByHhold = new HashMap<Household,Integer>();
		Iterator<Household> it = relocatedHouseholds.iterator();
		while (it.hasNext()) {
			Household hhold = it.next();
			if (hhold.getResidents() == null || hhold.getResidents().isEmpty()) {
				removeHousehold(hhold, aIndPool, dwellingAllocator);
				continue;
			}
			// ensures that households in relocatedHouseholds are occupying dwellings that have less bedrooms than what the households need.
			// if there are households that are occupying dwellings that already satisfy their bedroom needs, remove these households from relocatedHouseholds.
			Dwelling dwelling = dwellingAllocator.getDwellingPool().getDwellings().get(hhold.getDwellingId());
			if (dwelling!=null) {
				if (hhold.calculateNumberOfRoomNeed()<=dwelling.getNumberOfRooms()) {
					hhNotReloc.add(new String[] {String.valueOf(hhold.getId()), 
							String.valueOf(hhold.calculateNumberOfRoomNeed()),
							String.valueOf(dwelling.getNumberOfRooms())});
					it.remove();
					continue;
				}
            }
			
			nRoomsNeededByHhold.put(hhold, hhold.calculateNumberOfRoomNeed());
		}
		
		// sorts the hashmap in descending order (households with more bedrooms needed come first)
		nRoomsNeededByHhold = (HashMap<Household, Integer>) ArrayHandler.sortByValue(nRoomsNeededByHhold);

		// gets the total dwelling stocks available for this year
		HashMap<Integer,int[]> totalDwellingStocks = dwellingAllocator.getDwellingStocksForYear(year);
		
		// iterates through this sorted hashmap
		Iterator<Household> iterator = nRoomsNeededByHhold.keySet().iterator();
		int orderReloc = 0;
		while (iterator.hasNext()) {
			// gets a map of number of dwellings being occupied by travel zone
			Map<Integer, int[]> dwellingsOccupiedByTZ = dwellingAllocator.getDwellingsOccupied(ModelMain.getInstance().getHouseholdPool(), year);
			
			Household household = iterator.next();
			
			// dwelling stocks by TZ by number of bedrooms before this household searches for dwelling.
//			ArrayList<String[]> dwellingStockBefore = getDwellingsOccupiedString(dwellingAllocator, household.getId());
//			TextFileHandler.writeToCSV(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_dwellingStockBefore.csv", dwellingStockBefore, true);
			
			int[] newDwellingsInfo = chooseTravelZone(household, year, travelZoneCongestion, dwellingAllocator, dwellingsOccupiedByTZ, totalDwellingStocks);
			int selectedTravelZoneid = newDwellingsInfo[0];
			int selectedNbr = newDwellingsInfo[1];
			
			orderReloc += 1;
			hhReloc.add(new String[] {String.valueOf(orderReloc), String.valueOf(household.getId()), 
					String.valueOf(household.calculateNumberOfRoomNeed()),
					String.valueOf(selectedTravelZoneid)});
			
			if (selectedTravelZoneid == 0) {
				
				for (Individual resident : household.getResidents()) {
					removedPopStr.add(new String[] { String.valueOf(resident.getId()), 
							String.valueOf(resident.getAge()),
							resident.getGender().toString(),
							String.valueOf(household.getId()),
							household.getCategory().toString(),
							String.valueOf(household.calculateNumberOfRoomNeed()),
							String.valueOf(household.getGrossIncome().doubleValue()),
							(household.getOwnershipStatus()==null) ? "" : household.getOwnershipStatus().toString(),
							String.valueOf(household.getYearBoughtThisProperties())
//							(household.getEquity()==null) ? "" :  String.valueOf(household.getEquity().doubleValue()),
//							(household.getYearlyMortgagePayment()==null) ? "" : String.valueOf(household.getYearlyMortgagePayment().doubleValue()),
//							(household.getPriceCrnDwelling()==null) ? "" : String.valueOf(household.getPriceCrnDwelling().doubleValue())
							});
				}
				removedHouseholds.add(household);
				removeHousehold(household, aIndPool, dwellingAllocator);
			}

//			move(selectedTravelZoneid, selectedNbr, household, year, dwellingAllocator);
//			household.setTenure((double)0);
			
			// dwelling stocks by TZ by number of bedrooms after this household searches for dwelling and moved/removed.
//			ArrayList<String[]> dwellingStockAfter = getDwellingsOccupiedString(dwellingAllocator,household.getId());
//			TextFileHandler.writeToCSV(String.valueOf(ModelMain.getInstance().getCurrentYear())+"_dwellingStockAfter.csv", dwellingStockAfter, true);
		}
		
//		/*
//		 * population in householdPool after relocation
//		 */

		removeNoDwellingHousehold(aIndPool, dwellingAllocator);
		
		for (Household hhold : ModelMain.getInstance().getHouseholdPool().getHouseholds().values()) {
			Dwelling dwelling = dwellingAllocator.getDwellingPool().getDwellings().get(hhold.getDwellingId());
			for (Individual indiv : hhold.getResidents()) {
				hhPoolAfterRelocation.add(new String[] {String.valueOf(indiv.getId()),
						String.valueOf(indiv.getAge()),
						indiv.getGender().toString(),
						String.valueOf(indiv.getIncome().doubleValue()),
						(indiv.getHouseholdRelationship()==null) ? "" : indiv.getHouseholdRelationship().toString(),
						String.valueOf(indiv.getHouseholdId()),
						(indiv.getHholdCategory()==null) ? "" : indiv.getHholdCategory().toString(),
//						String.valueOf(hhold.getGrossIncome().doubleValue()),
//						(hhold.getEquity()==null) ? "" : String.valueOf(hhold.getEquity()),
//						(hhold.getSavingsForHouseBuying()==null) ? "" : String.valueOf(hhold.getSavingsForHouseBuying()),
						String.valueOf(hhold.getTenure()),
						String.valueOf(hhold.calculateNumberOfRoomNeed()),
						hhold.getOwnershipStatus()==null ? "" : hhold.getOwnershipStatus().toString(),
//						(dwelling==null) ? "" : String.valueOf(dwelling.getLivedHouseholdId()),
						(dwelling==null) ? "" : String.valueOf(dwelling.getTravelZoneId()),
						(dwelling==null) ? "" : String.valueOf(dwelling.getNumberOfRooms())});
			}
		}
		
	}

	@SuppressWarnings("unused")
	private List<String[]> getDwellingIDbyHholdID(DwellingAllocator dwellingAllocator) {
		ArrayList<String[]> dwellingByHhold = new ArrayList<>();
		for (Household hhold : households.values()) {
			Dwelling dwelling = dwellingAllocator.getDwellingPool().getDwellings().get(hhold.getDwellingId());
			String dwellingID = "-1";
			if (dwelling!=null) {
				dwellingID = String.valueOf(dwelling.getId());
            }
			dwellingByHhold.add(new String[] {String.valueOf(hhold.getId()),dwellingID});
		}
		return dwellingByHhold;
	}
	
		
	@SuppressWarnings({ "boxing", "unused" })
	private void findNoEnoughBedrooms(DwellingAllocator dwellingAllocator,
			Set<Household> relocatedHouseholds,
			Map<Household, Integer> moveReason) {
		for (Household household : this.households.values()) {

			household.computeHhCategory();

			for (Individual individual : household.getResidents()) {
				individual.setHouseholdId(household.getId());
			}

			if (household.getDwellingId() == 0) {
				relocatedHouseholds.add(household);
				moveReason.put(household, 6);
			} else if (household.calculateNumberOfRoomNeed() < dwellingAllocator
					.getDwellingPool().getDwellings()
					.get(household.getDwellingId()).getNumberOfRooms()) {
				relocatedHouseholds.add(household);
				moveReason.put(household, 1);
			}

			if (household.getNumberResidents() == 0) {
				removeHousehold(household);
			}
		}
	}

	@SuppressWarnings("unused")
	private void findLowSatisfaction(IndividualPool aIndPool,
			Map<Integer, Double> travelZoneCongestion,
			Set<Household> relocatedHouseholds,
			Map<Household, Integer> moveReason) {

		Set<Integer> lowSatisfactionHouseholdsIntegers = aIndPool
				.calculateIndividualSatisfaction(this.liveabilityUtility,
						travelZoneCongestion, this);

        for (Integer next : lowSatisfactionHouseholdsIntegers) {
            relocatedHouseholds.add(getByID(next));
            moveReason.put(getByID(next), 3);
        }
	}

	/**
	 * changes individual household relationship U15Child to Student or O15Child
	 * and Student to O15Child.
	 * 
	 * @param aRandom
	 * @author unknown, edited by Nam Huynh to account for change of household
	 *         type due to change of individuals household relationship.
	 */
	private void evolveStatus(Random aRandom) {
		for (Household hhold : this.households.values()) {
			for (Individual individual : hhold.getResidents()) {
				individual.growUp();

				if (individual.getHouseholdRelationship() == HouseholdRelationship.U15Child
						&& individual.getAge() >= 15) {
					int ranaomInt = aRandom.nextInt() % 2;

					if (ranaomInt == 0) {
						individual.setHouseholdRelationship(HouseholdRelationship.O15Child);
                    } else {
						individual.setHouseholdRelationship(HouseholdRelationship.Student);
                    }
				}

				if (individual.getHouseholdRelationship() == HouseholdRelationship.Student
						&& individual.getAge() > 24) {
					individual.setHouseholdRelationship(HouseholdRelationship.O15Child);
				}
			}
			hhold.computeHhCategory();
		}
	}

	private void evolutionOnHouseholdLevel(IndividualPool aIndPool,
			LifeEventProbability aLifeEventProbability, Random aRandom,
			DwellingAllocator dwellingAllocator,
			Map<Household, Integer> moveReason, Set<Individual> menIndividuals,
			Set<Individual> womenIndividuals, List<Individual> orphans,
			List<String[]> deadPeople, Set<Household> relocatedHouseholds,
			List<String[]> newborns) {
		
		for (Household curHh : getHouseholds().values()) {
			// if any individual in the household has not been assigned to a
			// travel zone id, the whole household will relocate
			for (Individual individual : curHh.getResidents()) {
				if (individual.getLivedTravelZoneid() == 0) {
					curHh.setDwellingId(0);
					relocatedHouseholds.add(curHh);
					moveReason.put(curHh, 6);
					break;
				}
			}
		}
		
		/* dead */
		for (Household curHh : getHouseholds().values()) {
			curHh.isIndividualDying(aLifeEventProbability, aRandom, aIndPool,
					this, dwellingAllocator, orphans, deadPeople);
		}
		
		/* birth */
		for (Household curHh : getHouseholds().values()) {
			curHh.newChild(aLifeEventProbability, aRandom, aIndPool, newborns);
		}

			/* Education and job */

			// itrInd = aIndPool.getIndividuals().values().iterator();
			// while (itrInd.hasNext() == true) {
			// curInd = itrInd.next();
			// curInd.updateEducation(aLifeEventProbability, aRandom);
			// curInd.updateJob(aLifeEventProbability, aRandom);
			// }

			/* Income */

			// itrHh = aHhPool.getHouseholds().values().iterator();
			// while (itrHh.hasNext() == true) {
			// itrHh.next()
			// .updateIncomeIndividuals(aLifeEventProbability, aRandom);
			// }

			/* People leaving household */

			// while (itrHh.hasNext() == true) {
			// itrHh.next().individualLeaving(aHhPool);
			// }

			// curHh.individualLeaving(this);

		/* People getting divorced */
		for (Household curHh : getHouseholds().values()) {
			divorceEvent(aLifeEventProbability, aRandom, relocatedHouseholds, moveReason, curHh);
		}

		/* Marriages */
		for (Household curHh : getHouseholds().values()) {
			// ... building the pools of households willing to marry
			// curHh.indInHhMarrying(aLifeEventProbability, aRandom, this,
			// aMenHhPool, aWomenHhPool);
			
			curHh.generateWeddingSet(aLifeEventProbability, aRandom,
					menIndividuals, womenIndividuals);
		}
		
		/* Education and job */
		for (Household curHh : getHouseholds().values()) {
			jobEvent(aLifeEventProbability, aRandom, moveReason, curHh);
		}
		
		for (Household curHh : getHouseholds().values()) {
			if (!relocatedHouseholds.contains(curHh)) {
				curHh.setTenure(curHh.getTenure() + 1);
			}
		}
	}

	private void jobEvent(LifeEventProbability aLifeEventProbability,
			Random aRandom, Map<Household, Integer> moveReason,
			Household curHh) {

		if (!curHh.getResidents().isEmpty()) {

			boolean isUpdateJob = false;
			int max = 0;
			for (Individual individual : curHh.getResidents()) {
				int income = individual.getIncome().intValue();

				if (income > max) {
					max = income;
				}
				isUpdateJob = individual.updateJob(aLifeEventProbability,
						aRandom);
				if (isUpdateJob) {
					moveReason.put(curHh, 2);

					curHh.setGrossIncome();
				}
			}
		}
	}

	private void divorceEvent(LifeEventProbability aLifeEventProbability,
			Random aRandom, Set<Household> relocatedHouseholds,
			Map<Household, Integer> moveReason, Household curHh) {
		if (curHh.isGettingDivorced(aLifeEventProbability, aRandom)) {
			Household newHousehold = curHh.getDivorced(this, aRandom);
			newHousehold.setDwellingId(0);
			relocatedHouseholds.add(newHousehold);
			moveReason.put(newHousehold, 5);
		}
	}

	/**
	 * relocates a household to a new travel zone and new dwelling.
	 * 
	 * @param selectedTravelZoneid
	 *            the reason of moving
	 * @param curHh
	 * @param year
	 * @param dwellingAllocator
	 */
	public void move(int selectedTravelZoneid, int selectedNbr, Household curHh, int year,
			DwellingAllocator dwellingAllocator) {

		if (selectedTravelZoneid == 0) {
			logger.info((curHh + "can not afford to any travel zone"));
		} else {

			if (!dwellingAllocator.allocateAvailableDwellingForRelocation(selectedTravelZoneid, selectedNbr, year, curHh)) {
				logger.info(curHh.toString() + " stay same place.");
			}

		}

	}

	public int[] chooseTravelZone(Household curHh, int year,
			Map<Integer, Double> travelZoneCongestion,
			DwellingAllocator dwellingAllocator,
			Map<Integer, int[]> dwellingsOccupiedByTZ, HashMap<Integer,int[]> totalDwellingStocks) {

		Individual selectedIndividual = findHouseholdHead(curHh);

		logger.debug("Selected Individual: " + selectedIndividual.toString());

		return this.liveabilityUtility.chooseLocation(
				selectedIndividual, curHh, year,
				mortgageRatesDAO.findByYear(year).getRate(), travelZoneCongestion,
				dwellingAllocator, dwellingsOccupiedByTZ, totalDwellingStocks);
	}

	/**
	 * finds the head of the household who will choose travel zone for the whole
	 * family.
	 * 
	 * @param curHh
	 * @return the selected individual
	 */
	private Individual findHouseholdHead(Household curHh) {
		List<Individual> individuals = curHh.getResidents();
		Individual selectedIndividual;
		List<Individual> parents = curHh.getParents();

		if (parents.isEmpty()) {

			selectedIndividual = individuals.get(0);
			BigDecimal minBigDecimal = new BigDecimal(Double.MIN_VALUE);

			for (Individual individual : individuals) {
				if (individual.getIncome().compareTo(minBigDecimal) >= 0) {
					selectedIndividual = individual;
					minBigDecimal = individual.getIncome();
				}
			}
		} else if (parents.size() == 1) {
			selectedIndividual = parents.get(0);
		} else {
			int random = HardcodedData.random.nextInt(parents.size());
			selectedIndividual = parents.get(random);
		}
		return selectedIndividual;
	}

	/**
	 * Marry 2 individuals, if any of them are LoneParent, other family members
	 * in that household will be also included in new household.
	 * 
	 * @param individuals1
	 *            a set of one sex candidates
	 * @param individuals2
	 *            a set of another sex candidates
	 * @param random
	 *            a random number
	 *
	 * @author qun
	 * 
	 */
	public void marry(Set<Individual> individuals1,
			Set<Individual> individuals2, Random random) {

		logger.debug("men: " + individuals1.size() + "women: "
				+ individuals2.size());
		logger.debug("total: "
				+ (totalIndividual() + individuals1.size() + individuals2
						.size()));

		final double deFactoProbability = 0.11;

		int gap = 7;

		for (Individual men : individuals1) {
			gap = 7;
			for (int i = 0; i < 10; i++) {
				if (findPartner(individuals1, individuals2, random,
						deFactoProbability, gap, men)) {

					break;
				} else {
					gap++;
				}

			}
		}

		logger.debug("men: " + individuals1.size() + "women: "
				+ individuals2.size());

		logger.debug("total: "
				+ (totalIndividual() + individuals1.size() + individuals2
						.size()));

		sameSexMatch(individuals1);
		sameSexMatch(individuals2);

		logger.debug("men: " + individuals1.size() + "women: "
				+ individuals2.size());

		logger.debug("total: "
				+ (totalIndividual() + individuals1.size() + individuals2
						.size()));

		for (Household household : getHouseholds().values()) {

			if (household.getNumberResidents() == 0) {
				removeHousehold(household);
			}
		}
	}

	/**
	 * Matches same sex people.
	 * 
	 * @param marryIndividuals
	 *            a set of same sex individuals want to marry each other
	 * @author qun
	 */
	private void sameSexMatch(Set<Individual> marryIndividuals) {
		List<Individual> menlist = new ArrayList<>(marryIndividuals);
		if (menlist.size() % 2 == 1) {
			Individual returnIndividual = menlist.get(0);

			if (getByID(returnIndividual.getHouseholdId()) != null) {
				getByID(returnIndividual.getHouseholdId()).addIndividual(
						returnIndividual);

			} else {
				Household menHousehold = getMarryHousehold(returnIndividual);
				add(menHousehold);
			}

			menlist.remove(0);
		}

		for (int i = 0; i < menlist.size(); i += 2) {
			Household menHousehold = getMarryHousehold(menlist.get(i));
			Household womenHousehold = getMarryHousehold(menlist.get(i + 1));

			add(menHousehold);
			menHousehold.mergeHousehold(womenHousehold,
					HouseholdRelationship.DeFacto);

		}
	}

	/**
	 * Finds an opposite sex partner.
	 * 
	 * @param selectIndividuals
	 *            set of individual will choose partners
	 * @param candidateIndividuals
	 *            set of individual will be chose
	 * @param random
	 * @param deFactoProbability
	 *            the probability of deFacto
	 * @param gap
	 *            year gap
	 * @param selector
	 *            individual will choose partners
	 * @return whether the partner is found
	 * @author qun
	 */
	private boolean findPartner(Set<Individual> selectIndividuals,
			Set<Individual> candidateIndividuals, Random random,
			final double deFactoProbability, int gap, Individual selector) {
		for (Individual women : candidateIndividuals) {
			if (Math.abs(selector.getAge() - women.getAge()) < gap) {
				selectIndividuals.remove(selector);
				candidateIndividuals.remove(women);

				Household menHousehold = getMarryHousehold(selector);
				Household womenHousehold = getMarryHousehold(women);

				double randDouble = random.nextDouble();

				add(menHousehold);
				menHousehold
						.mergeHousehold(
								womenHousehold,
								randDouble > deFactoProbability ? HouseholdRelationship.Married
										: HouseholdRelationship.DeFacto);

				
				// relocatedHouseholds.add(menHousehold);
				// moveReason.put(menHousehold, 4);

				return true;
			}

		}

		return false;
	}

	/**
	 * fetches a household from a individual, if he or she is lone person,
	 * return a household contains one individual; if he or she is lone parent,
	 * return a household including himself or herself and dependable children.
	 * 
	 * @param individual
	 *            the individual who will get married
	 * @return a household can get married
	 * @author qun
	 */
	private Household getMarryHousehold(Individual individual) {
		Household household = getByID(individual.getHouseholdId());
		if ((individual.getHouseholdRelationship() == HouseholdRelationship.LoneParent) && (household != null)) {
			household.addIndividual(individual);
			removeHousehold(household);
		} else {
			household = new Household();
			household.addIndividual(individual);
		}

		return household;
	}



	@Override
	public Object getPoolComponent() {
		return this.households;
	}


	/**
	 * Store keys (which are attribute set) of all households into a hashmap.
	 * 
	 * 
	 * 
	 * 
	 * @return The household id is the key of the hashmap. the value is
	 *         string[][] contains information of each individuals, including 1.
	 *         individual id 2. age 3. income 4. relationship 5. category
	 */
	public Map<Integer, String[][]> storeKeys() {

		HashMap<Integer, String[][]> hmHouseholdKey = new HashMap<>();
		Household thisHousehold;
		int i, j, numResidents;
		Collection<Household> householdCollection = getHouseholds().values();

		CopyOnWriteArrayList<Household> householdList = new CopyOnWriteArrayList<>(
				householdCollection);

		for (i = 0; i < householdList.size(); i++) {

			thisHousehold = householdList.get(i);
			numResidents = thisHousehold.getNumberResidents();
			String[][] householdInShort = new String[numResidents][5];

			for (j = 0; j < numResidents; j++) {
				householdInShort[j][0] = String.valueOf(thisHousehold
						.getResidents().get(j).getId());
				householdInShort[j][1] = String.valueOf(thisHousehold
						.getResidents().get(j).getAge());
				householdInShort[j][2] = thisHousehold.getResidents().get(j)
						.getIncome().toString();
				householdInShort[j][3] = thisHousehold.getResidents().get(j)
						.getHouseholdRelationship().toString();
				householdInShort[j][4] = thisHousehold.getResidents().get(j)
						.getHholdCategory().toString();
			}
			hmHouseholdKey.put(thisHousehold.getId(), householdInShort);

		}
		return hmHouseholdKey;
	}

	public void initialiseTravelDiariesChange() {
		for (Household household : getHouseholds().values()) {
			household.setTravelDiariesChanged(true);
		}
	}

	/**
	 * This function determines which households should be reassigned with a new
	 * travel diary. The travel diaries are reassigned when there is one of the
	 * given type of changes
	 * 
	 * the following cases will trigger reassigning travel diaries for
	 * households . 1. if old or new household is null 2. number of individuals
	 * in a household is different 3. any individuals has an attribute change
	 * except age 4. any individual's age reaches 5,15,55,65
	 */

	public void checkChange(Map<Integer, String[][]> hmHouseholdOldKey,
			Map<Integer, String[][]> hmHouseholdNewKey) {

		Household thisHousehold;
		String[][] oldHholdComposition = null;
		String[][] newHholdComposition = null;
		int i, j, k, numPersons, numPersonsOld;
		Boolean householdsMatch = false, correspondingIndivExists, indivAttributeChange, indivNewAgeGroup;

		Integer[] ageGroupBreakPoints = { 5, 15, 55, 65 };
		Collection<Household> householdCollection = getHouseholds().values();

		CopyOnWriteArrayList<Household> householdList = new CopyOnWriteArrayList<>(householdCollection);

		// iterate through each household in the Household pool
		for (i = 0; i < householdList.size(); i++) {

			thisHousehold = householdList.get(i);
			if (thisHousehold.isTravelDiariesChanged()) {
				continue;
			}
			numPersons = thisHousehold.getNumberResidents();
			// check if the household was present in the pre-evolution
			// HouseholdPool. if so, get its old attributes
			if (hmHouseholdOldKey.containsKey(thisHousehold.getId())) {
				oldHholdComposition = hmHouseholdOldKey.get(thisHousehold
						.getId());
			}
			// get the household's current attributes
			if (hmHouseholdNewKey.containsKey(thisHousehold.getId())) {
				newHholdComposition = hmHouseholdNewKey.get(thisHousehold
						.getId());
			}

			// if household was present in HouseholdPool both before and after
			// evolution, compare them further.

			if (oldHholdComposition != null && newHholdComposition != null) {

				numPersonsOld = oldHholdComposition.length;
				// check if the number of persons in the household have been
				// changed during evolution
				if (numPersons == numPersonsOld) {
					// check if the individuals present are same individuals
					// before evolution.

					Integer[] correspondingIndiv = new Integer[numPersons];
					Arrays.fill(correspondingIndiv, -1);
					correspondingIndivExists = true;
					for (j = 0; j < numPersons; j++) {
						for (k = 0; k < numPersonsOld; k++) {
							if (newHholdComposition[j][0]
									.equals(oldHholdComposition[k][0])) {
								// Individuals might be in a different sequence,
								// so
								// make a map between individuals
								correspondingIndiv[j] = k;
								break;
							} else {
								if (k == numPersonsOld - 1) {
									correspondingIndivExists = false;
								}
							}
						}
					}
					// check for any change in individual's attributes except
					// for age of the individual.
					// The attributes checked are Income, Hhold Relationship and
					// Hhold Category
					indivAttributeChange = false;
					if (correspondingIndivExists) {
						for (j = 0; j < numPersons; j++) {
							for (k = 2; k < 5; k++) {
								if (!newHholdComposition[j][k]
										.equals(oldHholdComposition[correspondingIndiv[j]][k])) {
									indivAttributeChange = true;
								}
							}
						}
					}
					// The Individual's age will change every year, but see if
					// this
					// age change is a significant one
					// Significant ages are given in the list at the beginning.
					// For eg:
					// age 65 probably means retirement and hence significant
					// one
					indivNewAgeGroup = false;
					if (!indivAttributeChange) {
						for (j = 0; j < numPersons; j++) {
							for (k = 0; k < ageGroupBreakPoints.length; k++) {
								if (Integer.parseInt(newHholdComposition[j][1]) == ageGroupBreakPoints[k]) {
									indivNewAgeGroup = true;
								}
							}
						}
					}
					// If there are no changes to the household after above
					// checks, the household is marked are unchanged
					if (!indivNewAgeGroup) {
						householdsMatch = true;
					} else {
						householdsMatch = false;
					}
				} else {
					householdsMatch = false;
				}

			} else {
				householdsMatch = false;

			}

			// now check if this household has new babies
			for (Individual indiv : thisHousehold.getResidents()) {
				if (indiv.getAge() == 1) {
					thisHousehold.setTravelDiariesChanged(true);
					break;
				}
			}

			if (householdsMatch) {
				thisHousehold.setTravelDiariesChanged(false);
			} else {
				thisHousehold.setTravelDiariesChanged(true);
			}

		}

	}

	/**
	 * updates household pool in case some individuals or households are missing.
	 * 
	 * @param individualPool
	 * 
	 * @author qun, edited by Nam Huynh to corrects household relationship of
	 *         residents in each household to ensure that they make sense.
	 */

	public void updateHouseholdPool(IndividualPool individualPool) {

		logger.debug("Updating household pool");

		for (Individual individual : individualPool.getIndividuals().values()) {

			if (individual.getAge() < 15) {
				individual
						.setHouseholdRelationship(HouseholdRelationship.U15Child);
			}

			int count = 0;
			int householdId = individual.getHouseholdId();
			Household household = this.households.get(householdId);

			if (household == null) {

				Household newHousehold = new Household();

				add(newHousehold);
				newHousehold.addIndividual(individual);
			} else {
				for (Individual resident : household.getResidents()) {
					if (resident.getId() == individual.getId()) {
						count++;
					}
				}

				if (count == 0) {
					household.addIndividual(individual);
				}

				if (count > 1) {
					household.removeIndividual(individual);
				}

			}
		}

		// corrects household relationship of residents in each household to
		// ensure that they make sense
		for (Household hhold : this.households.values()) {
			if (hhold.getNumberResidents() == 0) {
				this.removeHousehold(hhold);
				continue;
			}

			int nParents = hhold.getNParents();
			int nChildU15 = hhold.getNChildrenU15();
			int nStudents = hhold.getNStudents();
			int nChildO15 = hhold.getNChildrenO15();
			int nRelative = hhold.getNRelatives() + hhold.getNNonRelatives();
			int nTotChildren = nChildU15 + nStudents + nChildO15;// getNTotChildren();

			if (nParents > 2) { // this should not happen but if it does, keep 2
								// parents only and change other parents into
								// Relatives.
				int tmpnParents = 0;
				for (Individual indiv : hhold.getResidents()) {
					if (indiv.getHouseholdRelationship() == HouseholdRelationship.Married
							|| indiv.getHouseholdRelationship() == HouseholdRelationship.DeFacto) {
						if (tmpnParents < 2) {
							tmpnParents += 1;
                        } else {
							indiv.setHouseholdRelationship(HouseholdRelationship.Relative);
                        }
					}
                }
			}

			if (hhold.getNumberResidents() == 1) {
				hhold.getResidents()
						.get(0)
						.setHouseholdRelationship(
								HouseholdRelationship.LonePerson);
			} else {
				if (nParents == 1 && nTotChildren == 0) {
					for (Individual indiv : hhold.getResidents()) {
						indiv.setHouseholdRelationship(HouseholdRelationship.Relative);
                    }
                }
				if (nParents == 0) {
					if (nRelative == 0 && nTotChildren == 0) {
						// assigns groupHhold as household relationship to all
						// residents in this household
						for (Individual indiv : hhold.getResidents()) {
							indiv.setHouseholdRelationship(HouseholdRelationship.GroupHhold);
                        }
                    } else {
						// assigns Relatives as household relationship to all
						// residents in this household
						for (Individual indiv : hhold.getResidents()) {
							indiv.setHouseholdRelationship(HouseholdRelationship.Relative);
                        }
                    }
                }
			}

			hhold.computeHhCategory();
		}
	}

	// public void update

	/**
	 * counts total number of individuals in the household pool.
	 * 
	 * @return total number of individuals in the household pool
	 */
	public int totalIndividual() {
		int total = 0;
		for (Household house : this.households.values()) {
			total += house.getResidents().size();
		}
		return total;
	}

	/**
	 * Removes households that do not have a dwelling id.
	 * 
	 * @param individualPool
	 * @return number of remove ones
	 */
	public int removeNoDwellingHousehold(IndividualPool individualPool, DwellingAllocator dwellingAllocator) {
		int count = 0;
		for (Household household : this.households.values()) {
			if (household.getDwellingId() <= 0) {
				removeHousehold(household, individualPool, dwellingAllocator);
				this.main.getRemovedHouseholds().add(household);
				count++;
			}
		}

		return count;
	}

	public SortedSet<Integer> sortHouseholds() {
		return new TreeSet<Integer>(this.households.keySet());
	}

	public void updateIncome() {
		for (Household household : this.households.values()) {
			for (Individual individual : household.getResidents()) {
				individual.setIncome(individual.getIncome().multiply(
						BigDecimal.valueOf(1.04)));
			}

			household.setGrossIncome();
		}
	}

	/**
	 * allocates ownershipStatus to households by checking if their income can afford mortgage payment for the dwelling they're living in.
	 * If yes, set ownershipStatus to "paying". Otherwise set it to "renting".
	 */
	public void initialiseOwnership() {
		
		DwellingAllocator dwellingAllocator = ModelMain.getInstance().getDwellingAllocator();
		ArrayList<String[]> equityData = new ArrayList<String[]>();
		equityData.add(new String[] {"hholdID", "hholdCat", "hholdWeeklyIncome", "ownershipStatus",
				"SavingsForHouseBuying", "yearBoughtThisProperty", "equity", "yearlymortgagePayment",
				"priceCrnDwelling", "dutyPaid"});
		
		for (Household hhold : this.households.values()) {
			
			hhold.initialiseEquity();
			
			Double price= dwellingAllocator.getDwellingPool().getDwellingById(hhold.getDwellingId()).getSalePrices();
			hhold.isBuyingFirstProperty(price,
					mortgageRatesDAO.findByYear((ModelMain.getInstance().getCurrentYear())).getRate(), 
					new Duty(), ModelMain.getInstance().getCurrentYear());

			equityData.add(new String[] {String.valueOf(hhold.getId()), 
					hhold.getCategory().toString(), 
					String.valueOf(hhold.getGrossIncome().doubleValue()),
					hhold.getOwnershipStatus().toString(),
					String.valueOf(hhold.getSavingsForHouseBuying().doubleValue()),
					String.valueOf(hhold.getYearBoughtThisProperties()),
					String.valueOf(hhold.getEquity().doubleValue()),
					String.valueOf(hhold.getYearlyMortgagePayment().doubleValue()),
					String.valueOf(hhold.getPriceCrnDwelling().doubleValue()),
					String.valueOf(hhold.getDutyPaid().doubleValue()),
					});
		}
		TextFileHandler.writeToCSV("initialYear_equityData.csv", equityData, false);
	}
	
	/**
	 * stores values in attributes of NewTendByBerdrEntity objects into a hashmap.
	 * 
	 * @param rawtbdData list of NewTendByBerdrEntity objects
	 * @return The key of the output hashmap is NewTendByBerdrEntity.tz06.
	 * HashMap<String,double[]> are from NewTendByBerdrEntity objects having same NewTendByBerdrEntity.tz06.
	 * The key of HashMap<String,double[]> is from NewTendByBerdrEntity.ownership
	 * The 4 values in double[] in HashMap<String,double[]> are NewTendByBerdrEntity.bd1, NewTendByBerdrEntity.bd2, NewTendByBerdrEntity.bd3, NewTendByBerdrEntity.bdp4.
	 */
	private static Map<Integer, HashMap<String, double[]>> mapRawTendencyByBedroomData(List<NewTendbyBerdrEntity> rawtbdData) {
		HashMap<Integer, HashMap<String, double[]>> tendByBerdr = new HashMap<>();

        for (NewTendbyBerdrEntity tmpRec : rawtbdData) {
            HashMap<String, double[]> propValues = new HashMap<>();
            if (tendByBerdr.containsKey(tmpRec.getTz06())) {
                propValues = tendByBerdr.get(tmpRec.getTz06());
            }

            propValues.put(tmpRec.getOwnership(), new double[]{tmpRec.getBd1(), tmpRec.getBd2(), tmpRec.getBd3(), tmpRec.getBd4()});

            tendByBerdr.put(tmpRec.getTz06(), propValues);
        }

        if (logger.isTraceEnabled()) {
            ArrayList<String[]> tbdStr = new ArrayList<>();
            for (Map.Entry<Integer, HashMap<String, double[]>> anEntry : tendByBerdr.entrySet()) {
                HashMap<String, double[]> propValues = anEntry.getValue();
                for (Map.Entry<String, double[]> propValue : propValues.entrySet()) {
                    tbdStr.add(new String[] {String.valueOf(anEntry.getKey()),
                            propValue.getKey(),
                            String.valueOf(propValue.getValue()[0]),
                            String.valueOf(propValue.getValue()[1]),
                            String.valueOf(propValue.getValue()[2]),
                            String.valueOf(propValue.getValue()[3])});
                }
            }
            TextFileHandler.writeToCSV("tend_by_berdr.csv", tbdStr, false);
        }
		
		return tendByBerdr;
	}
	
	/**
	 * initialises ownership status of a household using 2006 ABS data.
	 * This data set specifies the number of dwellings by number of bedrooms being rented, being mortgaged, or owned for each travel zone.
	 * 
	 * @param rawtbdData data set specifying the percentage of dwellings being rented, being mortgaged, or owned for each travel zone by number of bedrooms.
	 * The key of tendByBerdr is travel zone ID.
	 * The 2 dimension integer array has 3 rows and 4 columns.  
	 * The rows represent (in order) "Rented", "Being purchased", and "Fully owned" dwellings.
	 * The columns represent (in order) "1 bedroom", "2 bedroom", "3 bedroom", "4 bedroom +" dwellings.
	 * The sum of values in a column equals to 1.  
	 * 
	 */
	public void initialiseOwnership(List<NewTendbyBerdrEntity> rawtbdData){
		Map<Integer, HashMap<String,double[]>> tendByBerdr = mapRawTendencyByBedroomData(rawtbdData);
		DwellingAllocator dwellingAllocator = ModelMain.getInstance().getDwellingAllocator();
		
		// generates map HashMap<TZ,HashMap<nBedrooms,ArrayList<Household>>>
		HashMap<Integer,HashMap<Integer,ArrayList<Household>>> hhBrTZ = new HashMap<>();
		for (Household hhold : this.households.values()) {
			hhold.initialiseEquity(); // initialises equity for the household
			
			Integer tzID = dwellingAllocator.getDwellingPool().getDwellingById(hhold.getDwellingId()).getTravelZoneId();
			HashMap<Integer,ArrayList<Household>> hhBr = new HashMap<>();
			if (hhBrTZ.containsKey(tzID)) {
				hhBr = hhBrTZ.get(tzID);
            }
			
			Integer nbr = dwellingAllocator.getDwellingPool().getDwellingById(hhold.getDwellingId()).getNumberOfRooms();
			if (nbr>HardcodedData.MAX_BEDROOMS) {
                nbr=HardcodedData.MAX_BEDROOMS;
            }
			if (nbr==null || nbr<=0) {
                continue;
            }
			
			ArrayList<Household> hhList = new ArrayList<>();
			if (hhBr.containsKey(nbr)) {
				hhList = hhBr.get(nbr);
            }
			hhList.add(hhold);
			
			hhBr.put(nbr,hhList);
			
			hhBrTZ.put(tzID,hhBr);
		}

        if (logger.isTraceEnabled()) {
            ArrayList<String[]> hhBrTZStr = new ArrayList<>();
            for (Map.Entry<Integer, HashMap<Integer, ArrayList<Household>>> anEntry : hhBrTZ.entrySet()) {
                HashMap<Integer,ArrayList<Household>> hhBr = anEntry.getValue();
                int nhh1br = 0;
                if (hhBr.get(1)!=null) {
                    nhh1br = hhBr.get(1).size();
                }
                int nhh2br = 0;
                if (hhBr.get(2)!=null) {
                    nhh2br = hhBr.get(2).size();
                }
                int nhh3br = 0;
                if (hhBr.get(3)!=null) {
                    nhh3br = hhBr.get(3).size();
                }
                int nhh4br = 0;
                if (hhBr.get(4)!=null) {
                    nhh4br = hhBr.get(4).size();
                }
                hhBrTZStr.add(new String[] {String.valueOf(anEntry.getKey()),
                        String.valueOf(nhh1br),
                        String.valueOf(nhh2br),
                        String.valueOf(nhh3br),
                        String.valueOf(nhh4br)});
            }
            TextFileHandler.writeToCSV("hhBrTZStr.csv", hhBrTZStr, false);
        }
		
		for (Map.Entry<Integer, HashMap<Integer, ArrayList<Household>>> anEntry : hhBrTZ.entrySet()) {
			HashMap<Integer,ArrayList<Household>> hhBr = anEntry.getValue();
			HashMap<String,double[]> propThisTZ = tendByBerdr.get(anEntry.getKey());
			
			// if there are no probability data on ownership for this TZ, move on (without initialising ownership status of households in this TZ)
			// this simplification needs to be lifted later by initialising ownership status for households in this TZ using the old algorithm
			// (compare the price of the current dwelling with household equity)
			if (propThisTZ==null) {
                continue;
            }
			
			for (int nbr=1; nbr<=HardcodedData.MAX_BEDROOMS; nbr++) {
				// gets the list of households in this TZ living in nbr-bedroom dwellings.
				ArrayList<Household> hhList = hhBr.get(nbr);
				
				// if there are no households in this TZ living in nbr-bedroom dwellings, move on to the next dwelling type.
				if (hhList==null) {
                    continue;
                }
				
				// gets the percentage of ownership types (rented, being purchased, fully owned) for dwellings with nbr bedrooms in travel zone tz.
				// the first value is the percentage of dwellings being rented.
				// second value is the percentage of dwellings being purchased.
				// third value is the percentage of dwellings being fully owned.
				double[] percThisNbr = new double[3];
				if (propThisTZ.get("Rented")!=null) {
					percThisNbr[0] = propThisTZ.get("Rented")[nbr-1];
                }
				if (propThisTZ.get("Being purchased")!=null) {
					percThisNbr[1] = propThisTZ.get("Being purchased")[nbr-1];
                }
				if (propThisTZ.get("Fully owned")!=null) {
					percThisNbr[2] = propThisTZ.get("Fully owned")[nbr-1];
                }
				
				// calculates the number of dwellings having nbr bedrooms in travel zone tz for each ownership types.
				int[] nDwellingsByOwnership = (new ArrayHandler()).allocateProportionally(percThisNbr, hhList.size());
				
				int nDwellingsRented = nDwellingsByOwnership[0];
				int nDwellingsMortgaged = nDwellingsByOwnership[1];
				int nDwellingsOwned = nDwellingsByOwnership[2];
				
				// randomly picks nDwellingsRented households from hhList and assigned ownership status of these households to rent
				for (int i=0; i<=nDwellingsRented-1; i++) {
					int randInt = HardcodedData.random.nextInt(hhList.size());
					Household hhPicked = hhList.get(randInt);
					
					hhPicked.setOwnershipStatus(OwnershipStatus.rent);
					// if the grossHholdIncome is 0 (less than $1), set it to $1 so that it can increase (by 2% or 4%) next years.
					if (hhPicked.getGrossIncome().doubleValue() * 52 < 1) {
						hhPicked.setGrossIncome(BigDecimal.ONE);
                    }
					hhPicked.setOwnershipStatus(HardcodedData.OwnershipStatus.rent);
					hhPicked.setYearBoughtThisProperties(-1);
					hhPicked.setPrincipleValue(new BigDecimal(-1));
					hhPicked.setYearlyMortgagePayment(new BigDecimal(-1));
					hhPicked.setSavingsForHouseBuying(hhPicked.getEquity());
					hhPicked.setEquity(new BigDecimal(-1));
					hhPicked.setPriceCrnDwelling(new BigDecimal(-1));
					hhPicked.setDutyPaid(new BigDecimal(-1));
					
					hhList.remove(hhPicked);
				}
				// randomly picks nDwellingsMortgaged households from hhList and assigned ownership status of these households to paying
				for (int i=0; i<=nDwellingsMortgaged-1; i++) {
					int randInt = HardcodedData.random.nextInt(hhList.size());
					Household hhPicked = hhList.get(randInt);
					
					double housePrice = dwellingAllocator.getDwellingPool().getDwellingById(hhPicked.getDwellingId()).getSalePrices();
					double duty = (new Duty()).calculateDuty(housePrice);
					double interest = mortgageRatesDAO.findByYear((ModelMain.getInstance().getCurrentYear())).getRate();
					double mortgage = (housePrice + duty - hhPicked.getEquity().doubleValue()) * interest
							* Math.pow(1 + interest, HardcodedData.maxYearMortgage)
							/ (Math.pow(1 + interest, HardcodedData.maxYearMortgage) - 1);
					hhPicked.setOwnershipStatus(OwnershipStatus.paying);
					hhPicked.setYearBoughtThisProperties(ModelMain.getInstance().getCurrentYear());
					hhPicked.setPrincipleValue(new BigDecimal(housePrice + duty));
					hhPicked.setYearlyMortgagePayment(new BigDecimal(mortgage));
					hhPicked.setSavingsForHouseBuying(BigDecimal.ZERO);
					hhPicked.setPriceCrnDwelling(new BigDecimal(housePrice));
					hhPicked.setDutyPaid(new BigDecimal(duty));
					
					hhList.remove(hhPicked);
				}
				
				// randomly picks nDwellingsOwned households from hhList and assigned ownership status of these households to own
				for (int i=0; i<=nDwellingsOwned-1; i++) {
					int randInt = HardcodedData.random.nextInt(hhList.size());
					Household hhPicked = hhList.get(randInt);
					
					double housePrice = dwellingAllocator.getDwellingPool().getDwellingById(hhPicked.getDwellingId()).getSalePrices();
					double duty = (new Duty()).calculateDuty(housePrice);
					hhPicked.setOwnershipStatus(OwnershipStatus.own);
					hhPicked.setYearBoughtThisProperties(ModelMain.getInstance().getCurrentYear());
					hhPicked.setPrincipleValue(new BigDecimal(-1));
					hhPicked.setYearlyMortgagePayment(new BigDecimal(-1));
					hhPicked.setSavingsForHouseBuying(new BigDecimal(-1));
					hhPicked.setPriceCrnDwelling(new BigDecimal(housePrice));
					hhPicked.setDutyPaid(new BigDecimal(duty));
					
					hhList.remove(hhPicked);
				}
			}
		}
		
	}
	
	
	
	/**
	 * generates immigrants by randomly selecting households in the initial synthetic population.
	 * The number of households of each type to be selected is specified in database table public.immigration_rate.
	 * Immigrant individuals are added to a given pool of individuals.
	 * Immigrant households are added to a given pool of households. 
	 * These households are also added to a list of households to be relocated to a dwelling in the next year simulation run.
	 * Immigrant households that cannot be allocated to a dwelling in next year simulation run will be removed, i.e. they are not actually immigrating.
	 * 
	 * @param individualPool pool of individuals to which immigrant individuals will be added.
	 * @param relocatedHouseholds list of households to be relocated to a dwelling in the next simulation run
	 * @param immigrantGenerator an instance of class ImmigrantGenerator where information of the initial population is stored.
	 * @param immigrationRateDAO connection to database table public.immigration_rate
	 * @param year the year for which immigrants are generated.
	 */
	public void generateImmigrants(IndividualPool individualPool, Set<Household> relocatedHouseholds, ImmigrantGenerator immigrantGenerator,
			ImmigrationRateDAO immigrationRateDAO, int year, List<String[]> immigrantRecords) {
		// initialises an ArrayList to store details of immigrants.
		// This arraylist is for testing purpose only, and is not used anywhere else in the model.
//		ArrayList<String[]> immigrantRecords = new ArrayList<String[]>();
		ImmigrationRateEntity immigrationRate = immigrationRateDAO.findOne(year);
		
		/*
		 * START MIGRATION V2.1
		 */
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getNf(),
				Category.NF, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf1(),
				Category.HF1, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf2(),
				Category.HF2, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf3(),
				Category.HF3, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf4(),
				Category.HF4, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf5(),
				Category.HF5, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf6(),
				Category.HF6, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf7(),
				Category.HF7, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf8(),
				Category.HF8, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf9(),
				Category.HF9, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf10(),
				Category.HF10, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf11(),
				Category.HF11, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf12(),
				Category.HF12, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf13(),
				Category.HF13, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf14(),
				Category.HF14, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf15(),
				Category.HF15, individualPool, this,
				relocatedHouseholds, immigrantRecords);
		immigrantGenerator.generateImmigrantHholds(immigrationRate.getHf16(),
				Category.HF16, individualPool, this,
				relocatedHouseholds, immigrantRecords);

//		TextFileHandler.writeToCSV(String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_immigrants.csv", immigrantRecords, false);
		/*
		 * END MIGRATION V2.1
		 */
	}
	
	
	@SuppressWarnings("unused")
	private List<String[]> getDwellingsOccupiedString(DwellingAllocator dwellingAllocator, int hhID) {
		ArrayList<String[]> dwellingStocks = new ArrayList<String[]>();
		HashMap<Integer,int[]> dwellingsByTZ = new HashMap<Integer,int[]>();
		
		for (Household hhold : this.households.values()) {
			if (hhold==null) {
                continue;
            }
			
			Dwelling dwelling = dwellingAllocator.getDwellingPool().getDwellings().get(hhold.getDwellingId());
			if (dwelling==null) {
                continue;
            }
			
			int nBedrooms = dwelling.getNumberOfRooms();
			if (nBedrooms==0) {
                continue;
            }
			
			int tz = dwelling.getTravelZoneId();
			int[] nDwellings = new int[4];
			if (dwellingsByTZ.containsKey(tz)) {
				nDwellings = dwellingsByTZ.get(tz);
            }
			
			switch (nBedrooms) {
			case 1: 
				nDwellings[0] += 1;
				break;
			case 2: 
				nDwellings[1] += 1;
				break;
			case 3: 
				nDwellings[2] += 1;
				break;
			default: 
				nDwellings[3] += 1;
				break;
			}
			
			dwellingsByTZ.put(tz, nDwellings);
		}
		
		for (Map.Entry<Integer, int[]> anEntry : dwellingsByTZ.entrySet()) {
			int[] crnDwellings = anEntry.getValue();
			dwellingStocks.add(new String[] {
					String.valueOf(hhID),
					String.valueOf(anEntry.getKey()),
					String.valueOf(crnDwellings[0]), 
					String.valueOf(crnDwellings[1]), 
					String.valueOf(crnDwellings[2]),
					String.valueOf(crnDwellings[3])});
		}
		
		return dwellingStocks;
	}
	

//	private ArrayList<String[]> getDwellingStocks_MMHholdPool(DwellingAllocator dwellingAllocator, int hhID, int year) {
//		ArrayList<String[]> dwellingStocks = new ArrayList<String[]>();
//		for (int i = 0; i < HardcodedData.travelZonesLiveable.length; i++) {
//			int travelZoneId = HardcodedData.travelZonesLiveable[i];
//			String[] dwellingStr = new String[] {
//					String.valueOf(hhID),
//					String.valueOf(travelZoneId),
//					String.valueOf(dwellingAllocator.getnAvailableDwellings(travelZoneId, 1, year)),
//					String.valueOf(dwellingAllocator.getnAvailableDwellings(travelZoneId, 2, year)),
//					String.valueOf(dwellingAllocator.getnAvailableDwellings(travelZoneId, 3, year)),
//					String.valueOf(dwellingAllocator.getnAvailableDwellings(travelZoneId, 4, year)),
//			};
//			dwellingStocks.add(dwellingStr);
//		}
//		return dwellingStocks;
//	}

	@SuppressWarnings("unused")
	private List<String[]> getDwellingPrices() {
		ArrayList<String[]> dwellingPrices = new ArrayList<>();
		
		TravelZonesFacilitiesDAO travelZonesFacilitiesDAO = ApplicationContextHolder.getBean(TravelZonesFacilitiesDAO.class);
		Map<Integer, TravelZonesFacilitiesEntity> tmpTravelZonesFacilitiesMap = travelZonesFacilitiesDAO.getByMap();
		
		for (Integer crnTZ : HardcodedData.travelZonesLiveable) {
			TravelZonesFacilitiesEntity travelZonesFacilities = tmpTravelZonesFacilitiesMap.get(crnTZ);
			dwellingPrices.add(new String[] {
				String.valueOf(crnTZ),	
				String.valueOf(travelZonesFacilities.getPrice(true, 1).doubleValue()), // salePrice of 1 bedroom dwelling in crnTZ
				String.valueOf(travelZonesFacilities.getPrice(true, 2).doubleValue()), // salePrice of 2 bedroom dwelling in crnTZ
				String.valueOf(travelZonesFacilities.getPrice(true, 3).doubleValue()), // salePrice of 3 bedroom dwelling in crnTZ
				String.valueOf(travelZonesFacilities.getPrice(true, 4).doubleValue()), // salePrice of 4 bedroom dwelling in crnTZ
				String.valueOf(travelZonesFacilities.getPrice(false, 1).doubleValue()), // rentPrice of 1 bedroom dwelling in crnTZ
				String.valueOf(travelZonesFacilities.getPrice(false, 2).doubleValue()), // rentPrice of 2 bedroom dwelling in crnTZ
				String.valueOf(travelZonesFacilities.getPrice(false, 3).doubleValue()), // rentPrice of 3 bedroom dwelling in crnTZ
				String.valueOf(travelZonesFacilities.getPrice(false, 4).doubleValue()), // rentPrice of 4 bedroom dwelling in crnTZ
			});
		}
		
		return dwellingPrices;
	}
}
