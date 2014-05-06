package core.synthetic.travel.mode;

import java.math.BigDecimal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import core.HardcodedData;
import core.postprocessing.file.TimePlan;

/**
 * A class provides functions to calculate travel cost
 * 
 * @author qun
 * 
 */
public final class TravelModeSelection {

	private final TravelPriceBean travelPriceBean;

	private static TravelModeSelection travelModeSelection;

	/**
	 * Instances an object
	 * 
	 * @return a static object
	 */
	public static synchronized TravelModeSelection getInstance() {
		if (travelModeSelection == null) {
			travelModeSelection = new TravelModeSelection();
		}
		return travelModeSelection;
	}

	private TravelModeSelection() {

		ApplicationContext priceApplicationContext = new ClassPathXmlApplicationContext(
                "priceBeans.xml");
		travelPriceBean = (TravelPriceBean) priceApplicationContext
				.getBean("travelPriceBean");
	}

	// public String selectTravelMode(TravelBy... travelBys) {
	// double minCost = Double.MIN_VALUE;
	//
	// TravelBy resultBy = null;
	// for (TravelBy travelBy : travelBys) {
	// double actualCost = travelBy.getTravelIndex(travelPriceBean)
	// .doubleValue();
	// if (actualCost < minCost) {
	// resultBy = travelBy;
	// minCost = actualCost;
	// }
	// }
	//
	// return showType(resultBy);
	// }

	/**
	 * Calculates the cost according to the travel modes and associated time
	 * plus parking fee.
	 * 
	 * @param travelModes
	 * @param walkTime
	 * @param driveTime
	 * @param transitTime
	 * @param waitTime
	 * @param otherTime
	 * @return the cost
	 */
	public BigDecimal calculateCostOld(TravelModes travelModes,
			double walkTime, double driveTime, double transitTime,
			double waitTime, double otherTime) {

		BigDecimal totalCost = new BigDecimal("0.0");

		final int bikeWalkingFixedCost = 10;
		final int carDriverFixedCost = 2;
		final int bikeJourneyTime = 30 * 60;
		final int walkJourneyTime = 45 * 60;
		final int busLightRailJourneyTime1 = 15 * 60;
		final int busLightRailJourneyTime2 = 30 * 60;
		final int busLightRailJourneyTime3 = 45 * 60;
		final int busFixedCost1 = 2;
		final int busFixedCost2 = 3;
		final int busFixedCost3 = 4;
		final int busFixedCost4 = 5;
		final int lightRailFixedCost1 = 1;
		final int lightRailFixedCost2 = 2;
		final int lightRailFixedCost3 = 3;
		final int lightRailFixedCost4 = 4;
		final int taxiFixedCost = 4;

		switch (travelModes) {
		case Bike:

			if (otherTime >= bikeJourneyTime) {
				totalCost = totalCost.add(new BigDecimal(bikeWalkingFixedCost));
			}

			totalCost = totalCost.add(
					new BigDecimal(walkTime * travelPriceBean.getWalk())).add(
					new BigDecimal(otherTime * travelPriceBean.getBike()));

			break;

		case CarDriver:

			totalCost = totalCost
					.add(new BigDecimal(carDriverFixedCost))
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(driveTime * travelPriceBean.getDriver()));
			break;
		case CarPassenger:

			totalCost = totalCost.add(
					new BigDecimal(walkTime * travelPriceBean.getWalk())).add(
					new BigDecimal(driveTime * travelPriceBean.getPassenger()));
			break;

		case Taxi:

			totalCost = totalCost.add(new BigDecimal(taxiFixedCost))
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(driveTime * travelPriceBean.getTaxi()));
			break;

		case Walk:

			if (walkTime >= walkJourneyTime) {
				totalCost = totalCost.add(new BigDecimal(bikeWalkingFixedCost));
			}

			totalCost = totalCost.add(new BigDecimal(walkTime
					* travelPriceBean.getWalk()));

			break;

		case Bus:

			if (transitTime < busLightRailJourneyTime1) {
				totalCost = totalCost.add(new BigDecimal(busFixedCost1));
			} else if (transitTime < busLightRailJourneyTime2) {
				totalCost = totalCost.add(new BigDecimal(busFixedCost2));
			} else if (transitTime < busLightRailJourneyTime3) {
				totalCost = totalCost.add(new BigDecimal(busFixedCost3));
			} else {
				totalCost = totalCost.add(new BigDecimal(busFixedCost4));
			}

			totalCost = totalCost
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(transitTime
							* travelPriceBean.getTransit()))
					.add(new BigDecimal(waitTime * travelPriceBean.getWait()));

			break;

		case LightRail:

			if (transitTime < busLightRailJourneyTime1) {
				totalCost = totalCost.add(new BigDecimal(lightRailFixedCost1));
			} else if (transitTime < busLightRailJourneyTime2) {
				totalCost = totalCost.add(new BigDecimal(lightRailFixedCost2));
			} else if (transitTime < busLightRailJourneyTime3) {
				totalCost = totalCost.add(new BigDecimal(lightRailFixedCost3));
			} else {
				totalCost = totalCost.add(new BigDecimal(lightRailFixedCost4));
			}

			totalCost = totalCost
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(transitTime
							* travelPriceBean.getTransit()))
					.add(new BigDecimal(waitTime * travelPriceBean.getWait()));

			break;
		case Other:
			break;

		}

		return totalCost;

	}

	/**
	 * Calculates the cost according to the travel modes and associated time
	 * plus parking fee.On top of that, individuals' income will be taken into
	 * account
	 * 
	 * @param travelModes
	 * @param walkTime
	 * @param driveTime
	 * @param transitTime
	 * @param waitTime
	 * @param otherTime
	 * @param income
	 * @return the cost
	 */
	public BigDecimal calculateCost(TravelModes travelModes, double walkTime,
			double driveTime, double transitTime, double waitTime,
			double otherTime, BigDecimal income) {

		BigDecimal totalCost = new BigDecimal("0.0");

		switch (travelModes) {
		case Bike:

			totalCost = totalCost
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(otherTime * travelPriceBean.getBike()))
					.add(new BigDecimal(walkTime).multiply(income))
					.add(new BigDecimal(otherTime).multiply(income));

			break;

		case CarDriver:

			totalCost = totalCost
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(driveTime * travelPriceBean.getDriver()))
					.add(new BigDecimal(walkTime).multiply(income))
					.add(new BigDecimal(driveTime).multiply(income));
			break;
		case CarPassenger:

			totalCost = totalCost
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(driveTime
							* travelPriceBean.getPassenger()))
					.add(new BigDecimal(walkTime).multiply(income))
					.add(new BigDecimal(driveTime).multiply(income));
			break;

		case Taxi:

			totalCost = totalCost
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(driveTime * travelPriceBean.getTaxi()))
					.add(new BigDecimal(walkTime).multiply(income))
					.add(new BigDecimal(driveTime).multiply(income));

			break;

		case Walk:
			if (walkTime > 660.0) {
				totalCost = new BigDecimal(1000000);
			} else {
				totalCost = totalCost.add(
						new BigDecimal(walkTime * travelPriceBean.getWalk()))
						.add(new BigDecimal(walkTime).multiply(income));
			}
			break;

		default:

			totalCost = totalCost
					.add(new BigDecimal(walkTime * travelPriceBean.getWalk()))
					.add(new BigDecimal(transitTime
							* travelPriceBean.getTransit()))
					.add(new BigDecimal(waitTime * travelPriceBean.getWait()))
					.add(new BigDecimal(walkTime).multiply(income))
					.add(new BigDecimal(transitTime).multiply(income));

			break;

		}

		return totalCost;

	}

	/**
	 * 
	 * Calculates the cost according to the travel modes and a time plan
	 * 
	 * @param travelModes
	 * @param timePlan
	 * @return the cost
	 */
	public BigDecimal calculateCost(TravelModes travelModes, TimePlan timePlan) {
		return calculateCostOld(travelModes, timePlan.getWalkTime(),
				timePlan.getDriveTime(), timePlan.getTransitTime(),
				timePlan.getWaitTime(), timePlan.getOtherTime());
	}

	/**
	 * 
	 * Calculates the delta variable cost:
	 * 		"variable cost (current mode) - minimum variable cost (of all modes)" 
	 * according to the travel mode and a trip time plan
	 * 
	 * @param travelModes
	 * @return the delta variable cost
	 */
	public double calculateDeltaVariableCost(TravelModes travelModes,
			double bikeTime, double walkTime, double transitTime, double carTime) {
		
		double deltaVariableCost=0, currModeVariableCost=0, bikeVariableCost=0, walkVariableCost=0,
				transitVariableCost=0, carDriverVariableCost=0, carPassVariableCost=0, taxiVariableCost=0;
		
		bikeVariableCost = bikeTime * travelPriceBean.getBike();
		walkVariableCost = walkTime * travelPriceBean.getWalk();
		transitVariableCost = transitTime * travelPriceBean.getTransit();
		carDriverVariableCost = carTime * travelPriceBean.getDriver();
		carPassVariableCost = carTime * travelPriceBean.getPassenger();
		taxiVariableCost = carTime * travelPriceBean.getTaxi();
		
		double[] costArr = new double[] {bikeVariableCost, walkVariableCost, transitVariableCost, carDriverVariableCost,
				carPassVariableCost, taxiVariableCost};
		
		switch (travelModes) {
		case Walk:
			currModeVariableCost = walkVariableCost;
			break;

		case CarDriver:
			currModeVariableCost = carDriverVariableCost;
			break;
			
		case Bus:
			currModeVariableCost = transitVariableCost;
			break;

		case LightRail:
			currModeVariableCost = transitVariableCost;
			break;
			
		case Bike:
			currModeVariableCost = bikeVariableCost;
			break;

		case CarPassenger:
			currModeVariableCost = carPassVariableCost;
			break;

		case Taxi:
			currModeVariableCost = taxiVariableCost;
			break;

		case Other:
			currModeVariableCost = costArr[HardcodedData.random.nextInt(costArr.length)];
			break;

		}
		
		//calculate delta variable cost
		deltaVariableCost = currModeVariableCost - minCost(costArr);
		
		return deltaVariableCost;
	}
	
//	public double[] calculateDeltaVariableCost(TravelModes travelModes,
//			double bikeTime, double walkTime, double transitTime, double carTime) {
//		
//		double[] dataout = new double[9];
//		
//		double deltaVariableCost=0, currModeVariableCost=0, bikeVariableCost=0, walkVariableCost=0,
//				transitVariableCost=0, carDriverVariableCost=0, carPassVariableCost=0, taxiVariableCost=0;
//		
//		bikeVariableCost = bikeTime * travelPriceBean.getBike();
//		walkVariableCost = walkTime * travelPriceBean.getWalk();
//		transitVariableCost = transitTime * travelPriceBean.getTransit();
//		carDriverVariableCost = carTime * travelPriceBean.getDriver();
//		carPassVariableCost = carTime * travelPriceBean.getPassenger();
//		taxiVariableCost = carTime * travelPriceBean.getTaxi();
//		
//		double[] costArr = new double[] {bikeVariableCost, walkVariableCost, transitVariableCost, carDriverVariableCost,
//				carPassVariableCost, taxiVariableCost};
//		
//		switch (travelModes) {
//		case Walk:
//			currModeVariableCost = walkVariableCost;
//			break;
//
//		case CarDriver:
//			currModeVariableCost = carDriverVariableCost;
//			break;
//			
//		case Bus:
//			currModeVariableCost = transitVariableCost;
//			break;
//
//		case LightRail:
//			currModeVariableCost = transitVariableCost;
//			break;
//			
//		case Bike:
//			currModeVariableCost = bikeVariableCost;
//			break;
//
//		case CarPassenger:
//			currModeVariableCost = carPassVariableCost;
//			break;
//
//		case Taxi:
//			currModeVariableCost = taxiVariableCost;
//			break;
//
//		case Other:
//			currModeVariableCost = costArr[HardcodedData.random.nextInt(costArr.length)];
//			break;
//
//		}
//		
//		//calculate delta variable cost
//		deltaVariableCost = currModeVariableCost - minCost(costArr);
//		
//		/* save to return data*/
//		dataout[0] = deltaVariableCost;
//		dataout[1] = minCost(costArr);
//		dataout[2] = walkVariableCost;
//		dataout[3] = carDriverVariableCost;
//		dataout[4] = transitVariableCost;
//		dataout[5] = transitVariableCost;
//		dataout[6] = bikeVariableCost;
//		dataout[7] = carPassVariableCost;
//		dataout[8] = taxiVariableCost;
//		
////		return deltaVariableCost;
//		return dataout;
//	}
	
	/**
	 * 
	 * Calculates the delta fixed cost:
	 * 		"fixed cost (current mode) - minimum fixed cost (of all modes)" 
	 * according to the travel mode and a trip time plan
	 * 
	 * @param travelModes
	 * @return the delta fixed cost
	 */
	public double calculateDeltaFixedCost(TravelModes travelModes,
			double bikeTime, double walkTime, double transitTime) {
		
		double deltaFixedCost=0, minFixedCost=0;
		double bikeFixedCost=0, walkFixedCost=0, busFixedCost=0, lightRailFixedCost=0;
		
		final double bikeJourneyTime = 30 * 60;
		final double walkJourneyTime = 45 * 60;
		final double busLightRailJourneyTime1 = 15 * 60;
		final double busLightRailJourneyTime2 = 30 * 60;
		final double busLightRailJourneyTime3 = 45 * 60;
		
		final double taxiFixedCost = 4;
		final double carDriverFixedCost = 2;
		final double carPassengerFixedCost = 0;
		final double bikeWalkingFixedCost = 10;
		
		final double busFixedCost1 = 2;
		final double busFixedCost2 = 3;
		final double busFixedCost3 = 4;
		final double busFixedCost4 = 5;
		
		final double lightRailFixedCost1 = 1;
		final double lightRailFixedCost2 = 2;
		final double lightRailFixedCost3 = 3;
		final double lightRailFixedCost4 = 4;
		
		switch (travelModes) {
		case Walk:
			if (walkTime >= walkJourneyTime) {
				walkFixedCost = bikeWalkingFixedCost;
			}

			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
			
			deltaFixedCost = walkFixedCost - minFixedCost;
			
			break;
			
		case CarDriver:
			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
			
			deltaFixedCost = carDriverFixedCost - minFixedCost;
			
			break;

		case Bus:
			if (transitTime < busLightRailJourneyTime1) {
				busFixedCost = busFixedCost1;
			} else if (transitTime < busLightRailJourneyTime2) {
				busFixedCost = busFixedCost2;
			} else if (transitTime < busLightRailJourneyTime3) {
				busFixedCost = busFixedCost3;
			} else {
				busFixedCost = busFixedCost4;
			}

			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});

			deltaFixedCost = busFixedCost - minFixedCost; 
			
			break;

		case LightRail:
			if (transitTime < busLightRailJourneyTime1) {
				lightRailFixedCost = lightRailFixedCost1;
			} else if (transitTime < busLightRailJourneyTime2) {
				lightRailFixedCost = lightRailFixedCost2;
			} else if (transitTime < busLightRailJourneyTime3) {
				lightRailFixedCost = lightRailFixedCost3;
			} else {
				lightRailFixedCost = lightRailFixedCost4;
			}

			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});

			deltaFixedCost = lightRailFixedCost - minFixedCost; 
			
			break;
			
		case Bike:
			if (bikeTime >= bikeJourneyTime) {
				bikeFixedCost = bikeWalkingFixedCost;
			}
			
			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
			
			deltaFixedCost = bikeFixedCost - minFixedCost; 
					
			break;

		case CarPassenger:
			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
			
			deltaFixedCost = carPassengerFixedCost - minFixedCost;
			
			break;
			
		case Taxi:
			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
			
			deltaFixedCost = taxiFixedCost - minFixedCost;
			
			break;
			
		default:
			double[] costArr = new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost};
			minFixedCost = minCost(costArr);
			
			deltaFixedCost = costArr[HardcodedData.random.nextInt(costArr.length)] - minFixedCost; 
			
			costArr=null;
			break;
		}
		
		return deltaFixedCost;
	}

//	public double[] calculateDeltaFixedCost(TravelModes travelModes,
//			double bikeTime, double walkTime, double transitTime) {
//		
//		double[] dataout= new double[9]; 
//		
//		double deltaFixedCost=0, minFixedCost=0;
//		double bikeFixedCost=0, walkFixedCost=0, busFixedCost=0, lightRailFixedCost=0;
//		
//		final double bikeJourneyTime = 30 * 60;
//		final double walkJourneyTime = 45 * 60;
//		final double busLightRailJourneyTime1 = 15 * 60;
//		final double busLightRailJourneyTime2 = 30 * 60;
//		final double busLightRailJourneyTime3 = 45 * 60;
//		
//		final double taxiFixedCost = 4;
//		final double carDriverFixedCost = 2;
//		final double carPassengerFixedCost = 0;
//		final double bikeWalkingFixedCost = 10;
//		
//		final double busFixedCost1 = 2;
//		final double busFixedCost2 = 3;
//		final double busFixedCost3 = 4;
//		final double busFixedCost4 = 5;
//		final double lightRailFixedCost1 = 1;
//		final double lightRailFixedCost2 = 2;
//		final double lightRailFixedCost3 = 3;
//		final double lightRailFixedCost4 = 4;
//		
//		switch (travelModes) {
//		case Walk:
//			if (walkTime >= walkJourneyTime) {
//				walkFixedCost = bikeWalkingFixedCost;
//			}
//
//			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
//			
//			deltaFixedCost = walkFixedCost - minFixedCost;
//			
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			dataout[2] = walkFixedCost;
// 			
//			break;
//			
//		case CarDriver:
//			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
//			
//			deltaFixedCost = carDriverFixedCost - minFixedCost;
//			
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			dataout[3] = carDriverFixedCost;
//			
//			break;
//
//		case Bus:
//			if (transitTime < busLightRailJourneyTime1) {
//				busFixedCost = busFixedCost1;
//			} else if (transitTime < busLightRailJourneyTime2) {
//				busFixedCost = busFixedCost2;
//			} else if (transitTime < busLightRailJourneyTime3) {
//				busFixedCost = busFixedCost3;
//			} else {
//				busFixedCost = busFixedCost4;
//			}
//
//			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
//
//			deltaFixedCost = busFixedCost - minFixedCost; 
//
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			dataout[4] = busFixedCost;
//			
//			break;
//
//		case LightRail:
//			if (transitTime < busLightRailJourneyTime1) {
//				lightRailFixedCost = lightRailFixedCost1;
//			} else if (transitTime < busLightRailJourneyTime2) {
//				lightRailFixedCost = lightRailFixedCost2;
//			} else if (transitTime < busLightRailJourneyTime3) {
//				lightRailFixedCost = lightRailFixedCost3;
//			} else {
//				lightRailFixedCost = lightRailFixedCost4;
//			}
//
//			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
//
//			deltaFixedCost = lightRailFixedCost - minFixedCost; 
//
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			dataout[5] = lightRailFixedCost;
//			
//			break;
//			
//		case Bike:
//			if (bikeTime >= bikeJourneyTime) {
//				bikeFixedCost = bikeWalkingFixedCost;
//			}
//			
//			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
//			
//			deltaFixedCost = bikeFixedCost - minFixedCost; 
//
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			dataout[6] = bikeFixedCost;
//					
//			break;
//
//		case CarPassenger:
//			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
//			
//			deltaFixedCost = carPassengerFixedCost - minFixedCost;
//
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			dataout[7] = carPassengerFixedCost;
//			
//			break;
//			
//		case Taxi:
//			minFixedCost = minCost(new double[] {walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost});
//			
//			deltaFixedCost = taxiFixedCost - minFixedCost;
//
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			dataout[8] = taxiFixedCost;
//			
//			break;
//			
//		default:
//			double[] costArr =  new double[]{walkFixedCost, carDriverFixedCost, busFixedCost, 
//					lightRailFixedCost, bikeFixedCost, carPassengerFixedCost, taxiFixedCost};
//			minFixedCost = minCost(costArr);
//			
//			deltaFixedCost = costArr[HardcodedData.random.nextInt(costArr.length)] - minFixedCost; 
//
//			/* save to return data*/
//			dataout[0] = deltaFixedCost;
//			dataout[1] = minFixedCost;
//			
//			costArr=null;
//			break;
//		}
//		
////		return deltaFixedCost;
//		
//		return dataout;
//	}


	/**
	 * Find the minimum value in an array of cost value
	 * 
	 * @param costArray
	 * @return minimum value in the cost array
	 * 
	 * @author vlcao
	 */
	public static double minCost(double[] costArray) {
		double minCost=costArray[0];
		
		for (int i = 1; i < costArray.length; i++) {
			if (costArray[i]<minCost) {
				minCost = costArray[i];
			}
		}
		
		return minCost;
	}

	@Override
	public Object clone() throws CloneNotSupportedException

	{

		throw new CloneNotSupportedException();

	}

}
