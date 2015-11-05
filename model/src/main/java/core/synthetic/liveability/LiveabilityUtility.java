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
package core.synthetic.liveability;

import core.ApplicationContextHolder;
import core.ArrayHandler;
import core.ModelMain;
import hibernate.postgis.TravelZonesFacilitiesDAO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import hibernate.postgis.TravelZonesFacilitiesEntity;
import hibernate.postgres.WeightsDAO;
import hibernate.postgres.WeightsEntity;
import org.apache.log4j.Logger;

import core.HardcodedData;
import core.synthetic.IndividualPool;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.dwelling.Duty;
import core.synthetic.dwelling.Dwelling;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.survey.DemographicInfomation;

/**
 * an utility class for choosing location.
 * 
 * @author qun
 * 
 */
public final class LiveabilityUtility {

	private WeightsDAO weightsDAO;
	private final double[] betaNeighbour = { -0.279847, 0.0507896, 0.4968639 };
	private final double[] betaServices = { 0.094229, 0.222138, 0.3747711 };
	private final double[] betaEntertainment = { 0.6415578, -0.947025, 0.6817574 };
	private final double[] betaWorkAndEducation = { -0.286069, 0.1283478, 0.238725 };
	private final double[] betaTransport = { -0.2687555, 0.4362865, 0.05464965, };
	private final double[] beta500m = { 0, 0, 0 };

	private static final double beta0 = 0.0410837;
	private static final double beta1 = 0.4525288;
	private static final double beta2 = 0.001326;
	private static final double beta3 = 0.008107;

	private static final double intercept = 1.0231368;

	private final double[] income = { -0.3619588, -0.010223, -0.0681394, 0 };

	private final double[] ageCat = { -0.309736, -0.0160398, 0 };

	private final double[] hHtype = { -0.028844, 0.0044516, -0.036896,
			0.0411812, -0.2610279, -0.112708, 0 };

	private final double[] pop = { -5.735 * 0.000001, -6.153 * 0.000001,
			-7.422 * 0.0000001 };

	private static final Logger logger = Logger.getLogger(LiveabilityUtility.class);

	private final Map<Integer, TravelZonesFacilitiesEntity> travelZonesFacilitiesMap;
	private final TravelZonePopulation travelZonePopulation;

	private static LiveabilityUtility liveabilityUtility;

	private final Duty duty;

	private LiveabilityUtility() {
		super();

		travelZonePopulation = new TravelZonePopulation();

		TravelZonesFacilitiesDAO travelZonesFacilitiesDAO = ApplicationContextHolder.getBean(TravelZonesFacilitiesDAO.class);
		travelZonesFacilitiesMap = travelZonesFacilitiesDAO.getByMap();
        weightsDAO = ApplicationContextHolder.getBean(WeightsDAO.class);

		duty = new Duty();

		initial();
	}

	/**
	 * Gets a singleton instance.
	 * 
	 * @return a static instance
	 */
	public static synchronized LiveabilityUtility getInstance() {
		if (liveabilityUtility == null) {
			liveabilityUtility = new LiveabilityUtility();
		}

		return liveabilityUtility;
	}

	/**
	 * initials the parameters.
	 */
	public void initial() {

		double[][] totalBeta = new double[1][5];
		totalBeta[0] = beta500m;
	}

	public void setPopulation(IndividualPool individualPool) {
		travelZonePopulation.get500MPopulation(individualPool
				.calculateTravelZonePopulation());
	}

    /**
     * Choose a new location for agent.
     *
     * @param individual
     * @param household
     * @param year
     * @param interest
     * @param travelZoneCongestion
     * @param dwellingAllocator
     * @return selected travel zone id
     */
	public int[] chooseLocation(Individual individual, Household household,
			int year, double interest,
			Map<Integer, Double> travelZoneCongestion,
			DwellingAllocator dwellingAllocator,
			Map<Integer, int[]> dwellingsOccupiedByTZ, HashMap<Integer,int[]> totalDwellingStocks) {

		WeightsEntity weights = weightsDAO.findByDemographic(DemographicInfomation.classifyIndividual(individual));
		logger.trace(weights);

		Map<Integer, Double> map = new HashMap<>();
		ValueComparator valueComparator = new ValueComparator(map,
				travelZonesFacilitiesMap, household.calculateNumberOfRoomNeed());
		TreeMap<Integer, Double> treeMap = new TreeMap<>(valueComparator);
		map = rankTravelZone(individual, household, travelZoneCongestion, dwellingAllocator, weights, map, year, dwellingsOccupiedByTZ, totalDwellingStocks);

		treeMap.putAll(map);

        if (logger.isTraceEnabled()) {
		    logger.trace(treeMap.toString());
        }

		return buyOrRent(household, year, treeMap, interest, dwellingAllocator, dwellingsOccupiedByTZ, totalDwellingStocks);
	}

	private Map<Integer, Double> rankTravelZone(Individual individual, Household household,
			Map<Integer, Double> travelZoneCongestion,
			DwellingAllocator dwellingAllocator, WeightsEntity weights,
			Map<Integer, Double> map, int year,
			Map<Integer, int[]> dwellingsOccupiedByTZ, HashMap<Integer,int[]> totalDwellingStocks) {
		
		Map<Integer, Double> returnMap = map;
		int livedYear;
		
		for (int i = 0; i < HardcodedData.travelZonesLiveable.length; i++) {
			int travelZoneId = HardcodedData.travelZonesLiveable[i];
			if (dwellingAllocator.isTravelZoneAvailable(travelZoneId, household.calculateNumberOfRoomNeed(), dwellingsOccupiedByTZ, totalDwellingStocks) ||
					dwellingAllocator.isTravelZoneAvailable(travelZoneId, household.calculateNumberOfRoomNeed()+1, dwellingsOccupiedByTZ, totalDwellingStocks)) {
				
				// exclude non-residential travel zone
				TravelZonesFacilitiesEntity travelZonesFacilities = travelZonesFacilitiesMap.get(travelZoneId);

				double liveability = calculateLiveability(weights,
						travelZonesFacilities, individual.getAge(),
						individual.getIncome(), individual.getHholdCategory(),
						travelZoneCongestion.get(travelZoneId) == null ? 0
								: travelZoneCongestion.get(travelZoneId));
				logger.trace(travelZoneId + ":" + liveability + ",");
				if (travelZonesFacilities.getTz2006() == individual.getLivedTravelZoneid()) {
					livedYear = (int) household.getTenure();
				} else {
					livedYear = 0;
				}
				double satisfaction = calculateSatisfactory(livedYear, individual.getGender(), liveability);
				returnMap.put(travelZoneId, satisfaction);
			}
		}
		
		// sorts the Map<Integer, Double> by values so that travel zones with higher liveability come first
		returnMap = ArrayHandler.sortByValue(returnMap);
		
		return returnMap;
	}

		
	/**
	 * Determines a household will but or rent in a travel zone. if agent can
	 * not afford any he will stay in current travel zone
	 * 
	 * first they will try to buy a property. failing that they will choose to
	 * rent in a travel zone
	 * 
	 * @param household
	 *            the candidate household
	 * @param year
	 * @param treeMap
	 *            a treeMap contains a set of travelZones, first order by
	 *            satisfaction, second order by prices on particular types of
	 *            dwellings
	 * @param interest
	 *            current interest rate
	 * @return a travel zone id agent will move to
	 * 
	 * @author qun
	 */
	private int buyOrRent_old(Household household, int year, Map<Integer, Double> treeMap, double interest) {

		int selectedTravelZoneId = 0;
		
//		String prevOwnership = (household.getOwnershipStatus()==null) ? "" : household.getOwnershipStatus().toString();

		if (household.getEquity()==null) 
			household.initialiseEquity();
		
		if (household.getOwnershipStatus()==null) {
			for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
				selectedTravelZoneId = household.isBuyingFirstProperty(
						household.calculateNumberOfRoomNeed(), year,
						travelZonesFacilitiesMap.get(entry.getKey()),
						interest, duty);
				if (selectedTravelZoneId!=0) break;
			}
		} else {
			if (household.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.own) ||
					household.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.paying)) {
				Dwelling dwelling = ModelMain.getInstance().getDwellingAllocator().getDwellingPool().getDwellings().get(household.getDwellingId());
				if (dwelling==null) {// if for some reason this household doesn't have a dwelling attached to it
					for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
						selectedTravelZoneId = household.isBuyingFirstProperty(
								household.calculateNumberOfRoomNeed(), year,
								travelZonesFacilitiesMap.get(entry.getKey()),
								interest, duty);
						if (selectedTravelZoneId!=0) break;
					}
				} else {
					for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
						selectedTravelZoneId = household.isBuyingAnotherProperty(
								household.calculateNumberOfRoomNeed(), year,
								travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
						if (selectedTravelZoneId!=0) break;
					}
				}
			} else {
				for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
					selectedTravelZoneId = household.isBuyingFirstProperty(
							household.calculateNumberOfRoomNeed(), year,
							travelZonesFacilitiesMap.get(entry.getKey()),
							interest, duty);
					if (selectedTravelZoneId!=0) break;
				}
			}
		}
		if (selectedTravelZoneId == 0)
			selectedTravelZoneId = rent(household, year, treeMap);

		return selectedTravelZoneId;
	}

	/**
	 * Determines a household will but or rent in a travel zone. if agent can
	 * not afford any he will stay in current travel zone
	 * 
	 * first they will try to buy a property. failing that they will choose to
	 * rent in a travel zone
	 * 
	 * @param household
	 *            the candidate household
	 * @param year
	 * @param treeMap
	 *            a treeMap contains a set of travelZones, first order by
	 *            satisfaction, second order by prices on particular types of
	 *            dwellings
	 * @param interest
	 *            current interest rate
	 * @return a travel zone id agent will move to
	 * 
	 * @author qun
	 */
//	private int buyOrRent(Household household, int year, Map<Integer, Double> treeMap, double interest, DwellingAllocator dwellingAllocator) {
//
//		int selectedTravelZoneId = 0;
//
//		if (household.getEquity()==null) 
//			household.initialiseEquity();
//		
//		if (household.getOwnershipStatus()==null) {
//			for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
//				selectedTravelZoneId = household.isBuyingFirstProperty(
//						household.calculateNumberOfRoomNeed(), year,
//						travelZonesFacilitiesMap.get(entry.getKey()),
//						interest, duty);
//				if (selectedTravelZoneId!=0) 
//					break;
//				else
//					selectedTravelZoneId = household.isRentingProperty(
//							household.calculateNumberOfRoomNeed(), year,
//							travelZonesFacilitiesMap.get(entry.getKey()));
//			}
//		} else {
//			if (household.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.own) ||
//					household.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.paying)) {
//				Dwelling dwelling = ModelMain.getInstance().getDwellingAllocator().getDwellingPool().getDwellings().get(household.getDwellingId());
//				if (dwelling==null) {// if for some reason this household doesn't have a dwelling attached to it
//					for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
//						selectedTravelZoneId = household.isBuyingFirstProperty(
//								household.calculateNumberOfRoomNeed(), year,
//								travelZonesFacilitiesMap.get(entry.getKey()),
//								interest, duty);
//						if (selectedTravelZoneId!=0) 
//							break;
//						else
//							selectedTravelZoneId = household.isRentingProperty(
//									household.calculateNumberOfRoomNeed(), year,
//									travelZonesFacilitiesMap.get(entry.getKey()));
//					}
//				} else {
//					for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
//						selectedTravelZoneId = household.isBuyingAnotherProperty(
//								household.calculateNumberOfRoomNeed(), year,
//								travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
//						if (selectedTravelZoneId!=0) 
//							break;
//						else
//							selectedTravelZoneId = household.isRentingProperty(
//									household.calculateNumberOfRoomNeed(), year,
//									travelZonesFacilitiesMap.get(entry.getKey()));
//					}
//				}
//			} else {
//				for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
//					selectedTravelZoneId = household.isBuyingFirstProperty(
//							household.calculateNumberOfRoomNeed(), year,
//							travelZonesFacilitiesMap.get(entry.getKey()),
//							interest, duty);
//					if (selectedTravelZoneId!=0) 
//						break;
//					else
//						selectedTravelZoneId = household.isRentingProperty(
//								household.calculateNumberOfRoomNeed(), year,
//								travelZonesFacilitiesMap.get(entry.getKey()));
//				}
//			}
//		}
//
//		return selectedTravelZoneId;
//	}
	
	private int[] buyOrRent(Household household, int year, Map<Integer, Double> treeMap, double interest, DwellingAllocator dwellingAllocator,
			Map<Integer, int[]> dwellingsOccupiedByTZ, HashMap<Integer,int[]> totalDwellingStocks) {
		
		int selectedTravelZoneId = 0;
		int selectednbr = 0;
		
		if (household.getEquity()==null) 
			household.initialiseEquity();
		
		if (household.getOwnershipStatus()==null) {
			for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
				int nbrHigh = household.calculateNumberOfRoomNeed()+1;
				int nbrLow = household.calculateNumberOfRoomNeed();
				boolean largerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrHigh, dwellingsOccupiedByTZ, totalDwellingStocks);
				boolean smallerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrLow, dwellingsOccupiedByTZ, totalDwellingStocks);
				if (largerDwellingsAvailable) {
					selectedTravelZoneId = household.isBuyingFirstProperty(nbrHigh, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
					if (selectedTravelZoneId!=0) { // successfully bought a dwelling larger than needed
						// returns selectedTravelzoneId and nbrHigh
						return new int[] {selectedTravelZoneId, nbrHigh};
					} else {
						if (smallerDwellingsAvailable) {
							selectedTravelZoneId = household.isBuyingFirstProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
							if (selectedTravelZoneId!=0) { // successfully bought a dwelling as needed
								// returns selectedTravelzoneId and nbrLow
								return new int[] {selectedTravelZoneId, nbrLow};
							} else { // fails to buy any dwellings, so check if this household can afford renting a smaller dwelling in this travel zone
								selectedTravelZoneId = household.isRentingProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()));
								if (selectedTravelZoneId!=0) {// successfully rent smaller dwelling in this travel zone
									// returns selectedTravelzoneId and nbrLow
									return new int[] {selectedTravelZoneId, nbrLow};
								}
							}
						}
					}
				}
			}
		} else {
			if (household.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.own) ||
					household.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.paying)) {
				Dwelling dwelling = ModelMain.getInstance().getDwellingAllocator().getDwellingPool().getDwellings().get(household.getDwellingId());
				if (dwelling==null) {// if for some reason this household doesn't have a dwelling attached to it
					for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
						int nbrHigh = household.calculateNumberOfRoomNeed()+1;
						int nbrLow = household.calculateNumberOfRoomNeed();
						boolean largerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrHigh, dwellingsOccupiedByTZ, totalDwellingStocks);
						boolean smallerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrLow, dwellingsOccupiedByTZ, totalDwellingStocks);
						if (largerDwellingsAvailable) {
							selectedTravelZoneId = household.isBuyingFirstProperty(nbrHigh, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
							if (selectedTravelZoneId!=0) { // successfully bought a dwelling larger than needed
								// returns selectedTravelzoneId and nbrHigh
								return new int[] {selectedTravelZoneId, nbrHigh};
							} else {
								if (smallerDwellingsAvailable) {
									selectedTravelZoneId = household.isBuyingFirstProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
									if (selectedTravelZoneId!=0) { // successfully bought a dwelling as needed
										// returns selectedTravelzoneId and nbrLow
										return new int[] {selectedTravelZoneId, nbrLow};
									} else { // fails to buy any dwellings, so check if this household can afford renting a smaller dwelling in this travel zone
										selectedTravelZoneId = household.isRentingProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()));
										if (selectedTravelZoneId!=0) {// successfully rent smaller dwelling in this travel zone
											// returns selectedTravelzoneId and nbrLow
											return new int[] {selectedTravelZoneId, nbrLow};
										}
									}
								}
							}
						}
					}
				} else {
					for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
						int nbrHigh = household.calculateNumberOfRoomNeed()+1;
						int nbrLow = household.calculateNumberOfRoomNeed();
						boolean largerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrHigh, dwellingsOccupiedByTZ, totalDwellingStocks);
						boolean smallerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrLow, dwellingsOccupiedByTZ, totalDwellingStocks);
						if (largerDwellingsAvailable) {
							selectedTravelZoneId = household.isBuyingAnotherProperty(nbrHigh, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
							if (selectedTravelZoneId!=0) { // successfully bought a dwelling larger than needed
								// returns selectedTravelzoneId and nbrHigh
								return new int[] {selectedTravelZoneId, nbrHigh};
							} else {
								if (smallerDwellingsAvailable) {
									selectedTravelZoneId = household.isBuyingAnotherProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
									if (selectedTravelZoneId!=0) { // successfully bought a dwelling as needed
										// returns selectedTravelzoneId and nbrLow
										return new int[] {selectedTravelZoneId, nbrLow};
									} else { // fails to buy any dwellings, so check if this household can afford renting a smaller dwelling in this travel zone
										selectedTravelZoneId = household.isRentingProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()));
										if (selectedTravelZoneId!=0) {// successfully rent smaller dwelling in this travel zone
											// returns selectedTravelzoneId and nbrLow
											return new int[] {selectedTravelZoneId, nbrLow};
										}
									}
								}
							}
						}
					}
				}
			} else {
				for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
					int nbrHigh = household.calculateNumberOfRoomNeed()+1;
					int nbrLow = household.calculateNumberOfRoomNeed();
					boolean largerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrHigh, dwellingsOccupiedByTZ, totalDwellingStocks);
					boolean smallerDwellingsAvailable = dwellingAllocator.isTravelZoneAvailable(entry.getKey(), nbrLow, dwellingsOccupiedByTZ, totalDwellingStocks);
					if (largerDwellingsAvailable) {
						selectedTravelZoneId = household.isBuyingFirstProperty(nbrHigh, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
						if (selectedTravelZoneId!=0) { // successfully bought a dwelling larger than needed
							// returns selectedTravelzoneId and nbrHigh
							return new int[] {selectedTravelZoneId, nbrHigh};
						} else {
							if (smallerDwellingsAvailable) {
								selectedTravelZoneId = household.isBuyingFirstProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()), interest, duty);
								if (selectedTravelZoneId!=0) { // successfully bought a dwelling as needed
									// returns selectedTravelzoneId and nbrLow
									return new int[] {selectedTravelZoneId, nbrLow};
								} else { // fails to buy any dwellings, so check if this household can afford renting a smaller dwelling in this travel zone
									selectedTravelZoneId = household.isRentingProperty(nbrLow, year, travelZonesFacilitiesMap.get(entry.getKey()));
									if (selectedTravelZoneId!=0) {// successfully rent smaller dwelling in this travel zone
										// returns selectedTravelzoneId and nbrLow
										return new int[] {selectedTravelZoneId, nbrLow};
									}
								}
							}
						}
					}
				}
			}
		}

		return new int[] {selectedTravelZoneId, selectednbr};
	}
	
	
	private int rent(Household household, int year, Map<Integer, Double> treeMap) {
		int selectedTravelZoneId = 0;

		for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
			selectedTravelZoneId = household.isRentingProperty(
					household.calculateNumberOfRoomNeed(), year,
					travelZonesFacilitiesMap.get(entry.getKey()));
			if (selectedTravelZoneId != 0) 
				break;
		}

		return selectedTravelZoneId;
	}


	/**
	 * Calculates liveability according algorithms and coefficients.
	 * 
	 * @param weights
	 *            the weights for an individual
	 * @param travelZonesFacilities
	 *            the facilities of the travel zone the individual currently
	 *            live in
	 * @param age
	 *            age of the individual
	 * @param income
	 *            income of the individual
	 * @param hHtype
	 *            household type of the individual
	 * @param workTripsTime
	 *            travel zone congestion of the individual live in
	 * @return a double value of liveability between -2 and 2
	 * @author qun
	 */
	public double calculateLiveability(WeightsEntity weights,
			TravelZonesFacilitiesEntity travelZonesFacilities, int age,
			BigDecimal income, Category hHtype, double workTripsTime) {

		final double travelToAccessProportion = 0.3;
		final double workTripsTimeProportion = 0.7;
		int population = 0;
		try {
			population = getPopulation(travelZonesFacilities.getTz2006());
		} catch (Exception e) {
			// TODO: handle exception

			logger.error(travelZonesFacilities.toString());
		}

		if (population == 0) {
			population = 1;
		}

		double neighbourhood500m = travelZonesFacilities.getNeighbourhood500m();
		double workspacesEducation500m = travelZonesFacilities.getWorkspacesEducation500m();
		double services500m = travelZonesFacilities.getServices500m();
		double entertainment500m = travelZonesFacilities.getEntertainment500m();
		double transport500m = travelZonesFacilities.getTransport500m();

		double cNeighbourhood = weights.getNeighborhood()
				* neighbourhood500m / population * 100;

		cNeighbourhood = cNeighbourhood
				* (betaNeighbour[0] + betaNeighbour[1] + betaNeighbour[2]);

		double cWorkEducation = weights.getWorkAndEducation()
				* workspacesEducation500m / population * 100;

		cWorkEducation = cWorkEducation
				* (betaWorkAndEducation[0] + betaWorkAndEducation[1] + betaWorkAndEducation[2]);

		double cServices = weights.getServices() * services500m	/ population * 100;

		cServices = cServices
				* (betaServices[0] + betaServices[1] + betaServices[2]);
		double cEntertainment = weights.getEntertainment()
				* entertainment500m / population * 100;

		cEntertainment = cEntertainment
				* (betaEntertainment[0] + betaEntertainment[1] + betaEntertainment[2]);
		double cTransport = weights.getTransport()
				* (travelToAccessProportion*transport500m/population * 100
						* (betaTransport[0] + betaTransport[1] + betaTransport[2]) + workTripsTimeProportion
						* workTripsTime);

		double incomeValue = getIncomeValue(income);

		double ageValue = getAgeValue(age);

		double hHtypeValue = getHHtypeValue(hHtype);

		return getResult(cNeighbourhood, cWorkEducation, cServices,
				cEntertainment, cTransport, population, incomeValue, ageValue,
				hHtypeValue);
	}



	public double calculateLiveability(WeightsEntity weights, int neighbourhood500m,
			int workspacesEducation500m, int services500m,
			int entertainment500m, int transport500m, int age,
			BigDecimal income, Category hHtype, double workTripsTime,
			int population) {

		final double travelToAccessProportion = 0.3;
		final double workTripsTimeProportion = 0.7;

		double cNeighbourhood = weights.getNeighborhood()
				* neighbourhood500m / population * 100;

		cNeighbourhood = cNeighbourhood
				* (betaNeighbour[0] + betaNeighbour[1] + betaNeighbour[2]);

		double cWorkEducation = weights.getWorkAndEducation()
				* workspacesEducation500m / population * 100;

		cWorkEducation = cWorkEducation
				* (betaWorkAndEducation[0] + betaWorkAndEducation[1] + betaWorkAndEducation[2]);

		double cServices = weights.getServices() * services500m
				/ population * 100;

		cServices = cServices
				* (betaServices[0] + betaServices[1] + betaServices[2]);
		double cEntertainment = weights.getEntertainment()
				* entertainment500m / population * 100;

		cEntertainment = cEntertainment
				* (betaEntertainment[0] + betaEntertainment[1] + betaEntertainment[2]);
		double cTransport = weights.getTransport()
				* (travelToAccessProportion
						* transport500m
						/ population
						* 100
						* (betaTransport[0] + betaTransport[1] + betaTransport[2]) + workTripsTimeProportion
						* workTripsTime);

		logger.debug(cServices + "," + cTransport + "," + cWorkEducation + ","
				+ cEntertainment + "," + cNeighbourhood);

		logger.debug(weights.toString());

		double incomeValue = getIncomeValue(income);

		double ageValue = getAgeValue(age);

		double hHtypeValue = getHHtypeValue(hHtype);

		return getResult(cNeighbourhood, cWorkEducation, cServices,
				cEntertainment, cTransport, population, incomeValue, ageValue,
				hHtypeValue);
	}

	private double getResult(double cNeighbourhood, double cWorkEducation,
			double cServices, double cEntertainment, double cTransport,
			double population500, double incomeValue, double ageValue,
			double hHtypeValue) {

		double result = intercept + incomeValue + ageValue + hHtypeValue
				+ cNeighbourhood + cEntertainment + cServices + cWorkEducation
				+ cTransport + pop[0] * population500 + pop[1] * population500
				* 4 + pop[2] * population500 * 16;
		logger.trace("liveability result: " + result);

		if (result > 2) {
			result = 2;
		} else if (result < -2) {
			result = -2;
		}
		return result;
	}

	private double getHHtypeValue(Category hHtype) {
		double hHtypeValue = 0;
		if (hHtype == Category.HF16) {
			hHtypeValue = this.hHtype[0];
		} else if (hHtype == Category.HF1) {
			hHtypeValue = this.hHtype[1];
		} else if (hHtype == Category.HF5) {
			hHtypeValue = this.hHtype[2];
		} else if (hHtype == Category.HF2 || hHtype == Category.HF4
				|| hHtype == Category.HF6 || hHtype == Category.HF7
				|| hHtype == Category.HF8 || hHtype == Category.HF3) {
			hHtypeValue = this.hHtype[3];
		} else if (hHtype == Category.HF12) {
			hHtypeValue = this.hHtype[4];
		} else if (hHtype == Category.HF9 || hHtype == Category.HF10
				|| hHtype == Category.HF11 || hHtype == Category.HF13
				|| hHtype == Category.HF14 || hHtype == Category.HF15) {
			hHtypeValue = this.hHtype[5];
		}
		return hHtypeValue;
	}

	public int getPopulation(int travelZoneId) {
		int population;

		if (travelZonePopulation.getNewPopulation().containsKey(travelZoneId)) {
			population = travelZonePopulation.getNewPopulation().get(
					travelZoneId);
		} else {
			population = 1;
		}
		return population;
	}

	private double getAgeValue(int age) {
		double ageValue = 0;
		if (age < 30) {
			ageValue = ageCat[0];
		} else if (age < 65) {
			ageValue = ageCat[1];
		}
		return ageValue;
	}

	private double getIncomeValue(BigDecimal incomes) {
		double incomeValue = 0;
		if (incomes.doubleValue() * 52 <= 20000) {
			incomeValue = income[0];
		} else if (incomes.doubleValue() * 52 <= 40000) {
			incomeValue = income[1];
		} else if (incomes.doubleValue() * 52 <= 120000) {
			incomeValue = income[2];
		}
		return incomeValue;
	}

	/**
	 * Calculates satisfactory by algorithms.
	 * 
	 * @param livedYear
	 *            years of the individual live in the same travel zone
	 * @param gender
	 *            the gender of the individual
	 * @param liveability
	 *            the liveability of the individual
	 * @return a double value of satisfactory between -1 and 1
	 */
	public double calculateSatisfactory(int livedYear, Gender gender,
			double liveability) {

		double result = 0;
		result = beta0 + beta1 * liveability + beta2 * livedYear + beta3
				* (gender == Gender.Male ? 1 : 0);

		if (result < -1) {
			result = -1;
		} else if (result > 1) {
			result = 1;
		}

		// result=0;
		// TODO CHANGEME

		return result;
	}

	/* method to prevent cloning the SingletonObject */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
