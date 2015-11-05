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
package core.synthetic.household;

import hibernate.postgis.TravelZonesFacilitiesEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;


import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import core.HardcodedData;
import core.HardcodedData.OwnershipStatus;
import core.ModelMain;
import core.model.TextFileHandler;
import core.synthetic.HouseholdPool;
import core.synthetic.IndividualPool;
import core.synthetic.attribute.Category;
import core.synthetic.attribute.Gender;
import core.synthetic.attribute.HighestEduFinished;
import core.synthetic.attribute.HouseholdRelationship;
import core.synthetic.attribute.Occupation;
import core.synthetic.attribute.TransportModeToWork;
import core.synthetic.dwelling.Duty;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.individual.Individual;
import core.synthetic.individual.lifeEvent.LifeEventProbability;


/**
 * This class groups some individuals as a household and affords household level
 * operation
 * 
 * @author qun
 * 
 */
public class Household implements Serializable {

	/**
	 * name="id"
	 */
	private int id;
	/**
	 * name="category"
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Category category;
	/**
	 * name="numberResidents"
	 */
	private int numberResidents;
	/**
	 * name="residents"
	 */
	private List<Individual> residents = Lists.newArrayList();

	private double tenure;
	/**
	 * name="grossIncome"
	 */
	private BigDecimal grossIncome = BigDecimal.ZERO;

	private transient OwnershipStatus ownershipStatus;
	private transient BigDecimal principleValue;
	private transient BigDecimal equity;
	private transient BigDecimal yearlyMortgagePayment;
	private transient int yearBoughtThisProperties;
	private transient BigDecimal savingsForHouseBuying;
	private transient BigDecimal priceCrnDwelling;
	private transient BigDecimal dutyPaid;

	private int dwellingId;

	private boolean travelDiariesChanged;

    private static final Logger logger = Logger.getLogger(Household.class);

	public Household() {
        // NO-OP
	}

	@SuppressWarnings("hiding")
	public Household(int id, Category category, int numberResidents,
			BigDecimal grossIncome) {
		this.id = id;
		this.category = category;
		this.numberResidents = numberResidents;
		this.grossIncome = grossIncome;
	}

	public Household(Household baseHousehold) {
		this.id = baseHousehold.getId();
		this.category = baseHousehold.getCategory();
		this.numberResidents = baseHousehold.getNumberResidents();
		this.residents = baseHousehold.getResidents();
		this.grossIncome = baseHousehold.getGrossIncome();
	}

	/**
	 * This constructor directly derives numberResidents from the size of
	 * residents.
	 * 
	 * @param id
	 * @param category
	 * @param grossIncome
	 * 
	 * @author Johan Barthelemy
	 */
	@SuppressWarnings("hiding")
	public Household(int id, Category category, BigDecimal grossIncome) {
		super();
		this.id = id;
		this.category = category;
		this.grossIncome = grossIncome;
	}

	
	
	/**
	 * Household constructor only requiring a id and a household category.
	 * Resident list is set to null, number of resident is 0 and gross income is
	 * 0.
	 * 
	 * @param id
	 * @param aCategory
	 * 
	 * @author Johan Barthelemy
	 */
	public Household(@SuppressWarnings("hiding") int id, Category aCategory) {
		super();
		this.id = id;
		this.category = aCategory;
		this.numberResidents = 0;
	}

	/**
	 * @return name="grossIncome"
	 */
	public BigDecimal getGrossIncome() {
		return this.grossIncome;
	}

	/**
	 * @param grossIncome
	 *            name="grossIncome"
	 */
	public void setGrossIncome(
			@SuppressWarnings("hiding") BigDecimal grossIncome) {
		this.grossIncome = grossIncome;
	}

	/**
	 * Compute the GrossIncome of the household directly from the residents'
	 * GrossIncomes.
	 * 
	 * @author Johan Barthelemy
	 */
	public void setGrossIncome() {

		BigDecimal aGrossIncome = BigDecimal.ZERO;

        for (Individual indiv : getResidents()) {
            aGrossIncome = aGrossIncome.add(indiv.getIncome());
        }

		setGrossIncome(aGrossIncome);

	}

	/**
	 * @return name="id"
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            name="id"
	 */
	public void setId(@SuppressWarnings("hiding") int id) {
		this.id = id;
	}

	/**
	 * @return name="category"
	 */
	public Category getCategory() {
		return this.category;
	}

	/**
	 * @param category
	 *            name="category"
	 */
	public void setCategory(@SuppressWarnings("hiding") Category category) {
		this.category = category;
		for (Individual individual : getResidents()) {
			individual.setHholdCategory(category);
		}
	}

	public List<Individual> getResidents() {
		return this.residents;
	}

	public void setResidents(
			@SuppressWarnings("hiding") List<Individual> residents) {
		this.residents = residents;
	}

	/**
	 * Check if the current household can afford to buy the property in travel
	 * zones, having nBedrooms in year buyingYear. The yearly mortgage payment
	 * is calculated from the formula you gave, with P being the sale price of a
	 * property having nBedrooms in travel zones at buyingYear. R is the
	 * interest rate at buyingYear. If this payment is smaller than 1/3 of this
	 * household�s yearly income, return true. Also, the household attributes
	 * yearBoughtThisProperty is set to buyingYear, principleValue is set to the
	 * sale price of the property at buyingYear plus stamp duty, and
	 * ownershipStatus is set to 'paying'. This method is executed only if
	 * ownershipStatus of the household is not �owned� or �paying� and if the
	 * household is not a non-family household.
	 * 
	 * @param nBedrooms
	 *            number of bedrooms
	 * @param year
	 *            current year
	 * @param travelZonesFacilities
	 *            contains dwelling prices
	 * @param interest
	 *            mortgage interest
	 * @param duty
	 *            duty cost
	 * @return the selected travel zone id
	 * 
	 * @author qun
	 */
	@SuppressWarnings("boxing")
	public int isBuyingFirstProperty(int nBedrooms, int year,
			TravelZonesFacilitiesEntity travelZonesFacilities, double interest,
			Duty duty) {

		int selectedID = 0;

		double salePrice = travelZonesFacilities.getPrice(true, nBedrooms);

		double dutyPayable = duty.calculateDuty(salePrice);

		// calculate yearly mortgage payment
//		double deposit = salePrice*0.2;
		double c = (salePrice - this.getEquity().doubleValue() + dutyPayable) * interest
				* Math.pow(1 + interest, HardcodedData.maxYearMortgage)
				/ (Math.pow(1 + interest, HardcodedData.maxYearMortgage) - 1);
		// this is not the first year and the household is renting
		if (this.getOwnershipStatus()!=null &&
				this.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.rent)) { 
			c = (salePrice + dutyPayable - this.getSavingsForHouseBuying().doubleValue()) * interest
					* Math.pow(1 + interest, HardcodedData.maxYearMortgage)
					/ (Math.pow(1 + interest, HardcodedData.maxYearMortgage) - 1);
		}
		
		// calculate yearly income of this household
		double yearlyIncome = this.getGrossIncome().doubleValue() * 52;
		
		if (logger.isTraceEnabled()) {
			String tmpBuy1stHouseStr = String.valueOf(this.getId()) + "," +
					String.valueOf(travelZonesFacilities.getTz2006()) + "," +
					String.valueOf(nBedrooms) + "," +
					String.valueOf(salePrice) + "," + 
					String.valueOf(dutyPayable) + "," + String.valueOf(interest) + "," + String.valueOf(c) + "," +
					String.valueOf(yearlyIncome) + "," +
					((this.getEquity()==null) ? "" : String.valueOf(this.getEquity().doubleValue())) + "," + 
					((this.getSavingsForHouseBuying()==null) ? "" : String.valueOf(this.getSavingsForHouseBuying().doubleValue())) + "," +
					this.getCategory().toString();
			TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear()+"_buy1stHouseStr.csv"), tmpBuy1stHouseStr, true);
		}
		
		if (c<0) {
			
			boolean moveInOK = ModelMain.getInstance().getDwellingAllocator().allocateAvailableDwellingForRelocation(travelZonesFacilities.getTz2006(), nBedrooms, year, this);
			selectedID = travelZonesFacilities.getTz2006();
			if (moveInOK) {
				if (this.getOwnershipStatus()!=null &&
						this.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.rent)) {
					this.setEquity(this.getSavingsForHouseBuying());
					this.setSavingsForHouseBuying(new BigDecimal(-1));
				}
				
				this.setOwnershipStatus(HardcodedData.OwnershipStatus.own);
				this.setYearBoughtThisProperties(year);
				this.setPrincipleValue(new BigDecimal(-1));
				this.setYearlyMortgagePayment(new BigDecimal(-1));
				this.setSavingsForHouseBuying(new BigDecimal(-1));
				this.setPriceCrnDwelling(new BigDecimal(salePrice));
				this.setDutyPaid(new BigDecimal(dutyPayable));
				
				return selectedID;
			}
		}
		
		if (yearlyIncome / 3 > c) {
			boolean moveInOK = ModelMain.getInstance().getDwellingAllocator().allocateAvailableDwellingForRelocation(travelZonesFacilities.getTz2006(), nBedrooms, year, this);
			selectedID = travelZonesFacilities.getTz2006();
			if (moveInOK) {
				if (this.getOwnershipStatus()!=null &&
						this.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.rent)) {
					this.setEquity(this.getSavingsForHouseBuying());
	            }

				this.setOwnershipStatus(HardcodedData.OwnershipStatus.paying);
				this.setYearBoughtThisProperties(year);
				this.setPrincipleValue(new BigDecimal(salePrice + dutyPayable));
				this.setYearlyMortgagePayment(new BigDecimal(c));
				
				this.setSavingsForHouseBuying(new BigDecimal(-1));
				this.setPriceCrnDwelling(new BigDecimal(salePrice));
				this.setDutyPaid(new BigDecimal(dutyPayable));
				
				// this.setnBedrooms(nBedrooms);
				// this.setResidentialCD(cdName);
				logger.debug("buy first dwelling in TZ: " + selectedID
						+ " , Price: " + c + " , year: " + year + " , bedrooms: "
						+ nBedrooms);
				return selectedID;
			}
			
		}

		return selectedID;
	
	}

	/**
	 * checks if this household can afford the mortgage payment for dwelling that have salePrice, with interest and duty included.
	 * 
	 * @param salePrice
	 * @param interest
	 * @param duty
	 * @param year
	 * @return true if the household can afford it, false if it cannot.
	 */
	@SuppressWarnings("boxing")
	public boolean isBuyingFirstProperty(double salePrice, double interest, Duty duty, int year) {
		double dutyPayable = duty.calculateDuty(salePrice);

		// calculate yearly mortgage payment
//		double deposit = salePrice*0.2;
		double c = (salePrice + dutyPayable - this.getEquity().doubleValue()) * interest
				* Math.pow(1 + interest, HardcodedData.maxYearMortgage)
				/ (Math.pow(1 + interest, HardcodedData.maxYearMortgage) - 1);
		// this is not the first year and the household is renting
		if (this.getOwnershipStatus()!=null &&
				this.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.rent)) { 
			c = (salePrice + dutyPayable - this.getSavingsForHouseBuying().doubleValue()) * interest
					* Math.pow(1 + interest, HardcodedData.maxYearMortgage)
					/ (Math.pow(1 + interest, HardcodedData.maxYearMortgage) - 1);
		}
		
		if (c<0) {
			this.setOwnershipStatus(HardcodedData.OwnershipStatus.own);
			this.setYearBoughtThisProperties(year);
			this.setPrincipleValue(new BigDecimal(-1));
			this.setYearlyMortgagePayment(new BigDecimal(-1));
			this.setSavingsForHouseBuying(new BigDecimal(-1));
			this.setPriceCrnDwelling(new BigDecimal(salePrice));
			this.setDutyPaid(new BigDecimal(dutyPayable));
			
			return true;
		}
		
		// calculate yearly income of this household
		double yearlyIncome = this.getGrossIncome().doubleValue() * 52;

		if (yearlyIncome / 3 > c) {
			this.setOwnershipStatus(HardcodedData.OwnershipStatus.paying);
			this.setYearBoughtThisProperties(year);
			this.setPrincipleValue(new BigDecimal(salePrice + dutyPayable));
			this.setYearlyMortgagePayment(new BigDecimal(c));
			
			if (this.getOwnershipStatus()!=null &&
					this.getOwnershipStatus().equals(HardcodedData.OwnershipStatus.rent)) {
				this.setEquity(this.getSavingsForHouseBuying());
            }
//			else 
//				this.setEquity(new BigDecimal(deposit));
			
			this.setSavingsForHouseBuying(BigDecimal.ZERO);
			this.setPriceCrnDwelling(new BigDecimal(salePrice));
			this.setDutyPaid(new BigDecimal(dutyPayable));
			
			return true;

		} else {
			// if the grossHholdIncome is 0 (less than $1), set it to $1 so that it can increase (by 2% or 4%) next years.
			if (yearlyIncome < 1) {
				this.setGrossIncome(BigDecimal.ONE);
            }
			
			this.setOwnershipStatus(HardcodedData.OwnershipStatus.rent);
			this.setYearBoughtThisProperties(-1);
			this.setPrincipleValue(new BigDecimal(-1));
			this.setYearlyMortgagePayment(new BigDecimal(-1));
			this.setSavingsForHouseBuying(this.getEquity());
			this.setEquity(new BigDecimal(-1));
			this.setPriceCrnDwelling(new BigDecimal(-1));
			this.setDutyPaid(new BigDecimal(-1));
			
			return false;
		}
		
	}
	
	
	/**
	 * Checks if the household can afford to rent the property in a travel zone,
	 * having nBedrooms in rentingYear. The yearly rent is obtained from table
	 * of yearly rent versus number of bedroom for the travel zone. If this
	 * value is smaller than 1/3 of this household�s yearly income, return true.
	 * Also, the household attributes yearBoughtThisProperty is set to -1,
	 * principleValue is set to -1, and ownershipStatus is set to 'rent'.
	 * 
	 * @param nBedrooms
	 *            number of bedrooms
	 * @param year
	 *            current year
	 * @param travelZonesFacilities
	 *            contains dwelling prices
	 * @return the selected travel zone id
	 * @author qun
	 */
	@SuppressWarnings("boxing")
	public int isRentingProperty(int nBedrooms, int year,
			TravelZonesFacilitiesEntity travelZonesFacilities) {
		// boolean renting = true;
		int selectedID = 0;

		double rentPrice = travelZonesFacilities.getPrice(false, nBedrooms);
		double yearlyRent = rentPrice;
		double yearlyIncome = this.getGrossIncome().doubleValue() * 52;

		if (logger.isTraceEnabled()) {
			String tmpRentStr = String.valueOf(this.id) + "," +
					String.valueOf(travelZonesFacilities.getTz2006()) + "," +
					String.valueOf(nBedrooms) + "," +
					String.valueOf(yearlyRent) + "," + 
					String.valueOf(yearlyIncome) + "," + 
					this.getCategory().toString();
			TextFileHandler.writeToText(String.valueOf(ModelMain.getInstance().getCurrentYear()) + "_rentStr.csv", tmpRentStr, true);
		}

		if (yearlyIncome / 3 > yearlyRent) {
			boolean moveInOK = ModelMain.getInstance().getDwellingAllocator().allocateAvailableDwellingForRelocation(travelZonesFacilities.getTz2006(), nBedrooms, year, this);
			selectedID = travelZonesFacilities.getTz2006();
			if (moveInOK) {
				this.setOwnershipStatus(HardcodedData.OwnershipStatus.rent);
				this.setYearBoughtThisProperties(-1);
				this.setPrincipleValue(new BigDecimal(-1));
				this.setYearlyMortgagePayment(new BigDecimal(-1));
				this.setEquity(new BigDecimal(-1));
				this.setSavingsForHouseBuying(new BigDecimal(yearlyIncome/3));
				this.setPriceCrnDwelling(new BigDecimal(-1));
				this.setDutyPaid(new BigDecimal(-1));
				logger.debug("rent dwelling in TZ: " + selectedID
						+ " , rent: " + rentPrice + " , year: " + year
						+ " , bedrooms: " + nBedrooms);
				return selectedID;
			}
		}

		return selectedID;
	}

	
	/**
	 * Check if the current household can afford to buy the property in CD
	 * cdName, having nBedrooms in year buyingYear. The payment for the new
	 * house is calculated from P1 and P2, which are the sale prices of the
	 * current property and the new property respectively (having nBedrooms in
	 * CD cdName) at buyingYear. If this payment is smaller than 1/3 of this
	 * household�s yearly income, return true. The household attributes
	 * yearBoughtThisProperty is set to buyingYear, principleValue is set to the
	 * new payment calculated above, and ownershipStatus is set to 'paying'.
	 * Also set this.nBedrooms to nBedrooms and this.residentialCD to cdName.
	 * This method is executed only if ownershipStatus of the household is
	 * �paying� and if the household is not a non-family household.
	 * 
	 * @param nBedrooms
	 *            number of bedrooms
	 * @param year
	 *            current year
	 * @param travelZonesFacilities
	 *            contains the prices
	 * @param interest
	 *            mortgage interest
	 * @param duty
	 *            duty cost
	 * @return selected travel zone ID
	 */
	@SuppressWarnings("boxing")
	public int isBuyingAnotherProperty(int nBedrooms, int year,
			TravelZonesFacilitiesEntity travelZonesFacilities, double interest,
			Duty duty) {

		int selectedID = 0;

		double p2 = travelZonesFacilities.getPrice(true, nBedrooms);

		// CALCULATE dutyPayable
		double dutyPayable = duty.calculateDuty(p2);

		double newPrincipal = p2 - this.getEquity().doubleValue() + dutyPayable;
		double newMortgagePayment = newPrincipal
				* interest
				* Math.pow(1 + interest, HardcodedData.maxYearMortgage)
				/ (Math.pow(1 + interest, HardcodedData.maxYearMortgage) - 1);
		double yearlyIncome = this.getGrossIncome().doubleValue() * 52;

		if (logger.isTraceEnabled()) {
			String tmpBuyAnotherProperty = String.valueOf(this.getId()) + "," +
					String.valueOf(travelZonesFacilities.getTz2006()) + "," + String.valueOf(nBedrooms) + "," +
					String.valueOf(p2) + "," + 
					String.valueOf(dutyPayable) + "," + String.valueOf(interest) + "," + String.valueOf(newMortgagePayment) + "," +
					String.valueOf(yearlyIncome) + "," +
					((this.getEquity()==null) ? "" : String.valueOf(this.getEquity().doubleValue())) + "," + 
					((this.getSavingsForHouseBuying()==null) ? "" : String.valueOf(this.getSavingsForHouseBuying().doubleValue())) + "," + 
					this.getCategory().toString();
			TextFileHandler.writeToText(
					String.valueOf(ModelMain.getInstance().getCurrentYear())+"_buyAnotherHouseStr.csv", tmpBuyAnotherProperty, true);
		}
		
		if (newPrincipal < 0) {
			boolean moveInOK = ModelMain.getInstance().getDwellingAllocator().allocateAvailableDwellingForRelocation(travelZonesFacilities.getTz2006(), nBedrooms, year, this);
			selectedID = travelZonesFacilities.getTz2006();
			if (moveInOK) {
				this.setOwnershipStatus(HardcodedData.OwnershipStatus.own);
				this.setYearBoughtThisProperties(year);
				this.setPrincipleValue(new BigDecimal(-1));
				this.setYearlyMortgagePayment(new BigDecimal(-1));

				logger.debug("buy new dwelling in TZ: " + selectedID
						+ " , new Price: " + newPrincipal + " , year: " + year
						+ " , bedrooms: " + nBedrooms);

				this.setPriceCrnDwelling(new BigDecimal(p2));
				this.setDutyPaid(new BigDecimal(dutyPayable));
				return selectedID;
			}
		} else {
			if (yearlyIncome / 3 > newMortgagePayment) {
				boolean moveInOK = ModelMain.getInstance().getDwellingAllocator().allocateAvailableDwellingForRelocation(travelZonesFacilities.getTz2006(), nBedrooms, year, this);
				selectedID = travelZonesFacilities.getTz2006();
				if (moveInOK) {
					this.setOwnershipStatus(HardcodedData.OwnershipStatus.paying);
					this.setYearBoughtThisProperties(year);
					this.setPrincipleValue(new BigDecimal(newPrincipal));
					this.setYearlyMortgagePayment(new BigDecimal(newMortgagePayment));
					logger.debug("buy new dwelling in TZ: " + selectedID
							+ " , new Price: " + newPrincipal + " , year: " + year
							+ " , bedrooms: " + nBedrooms);

					this.setPriceCrnDwelling(new BigDecimal(p2));
					this.setDutyPaid(new BigDecimal(dutyPayable));
					
					return selectedID;
				}
			}
		}

		return selectedID;
	}


	public void initialiseEquity() {
//		this.setEquity(new BigDecimal(500000));
		
		double shapeNF = 1.409439749;
		double scaleNF = 181961.4382;
		double shapeHF1 = 3.695003085;
		double scaleHF1 = 90023.16208;
		double shapeHF2 = 5.97954387;
		double scaleHF2 = 95123.32616;
		double shapeHF3 = 4.938192387;
		double scaleHF3 = 95652.81688;
		double shapeHF4 = 5.178526204;
		double scaleHF4 = 97659.83651;
		double shapeHF5 = 3.828756334;
		double scaleHF5 = 93210.5125;
		double shapeHF6 = 5.476073204;
		double scaleHF6 = 101822.7393;
		double shapeHF7 = 5.230964028;
		double scaleHF7 = 88232.82047;
		double shapeHF8 = 5.381103414;
		double scaleHF8 = 99364.45083;
		double shapeHF9 = 3.655309979;
		double scaleHF9 = 117618.4634;
		double shapeHF10 = 3.205149725;
		double scaleHF10 = 114141.3979;
		double shapeHF11 = 3.358917759;
		double scaleHF11 = 117369.3485;
		double shapeHF12 = 1.999603213;
		double scaleHF12 = 164980.7846;
		double shapeHF13 = 3.657966021;
		double scaleHF13 = 131008.7916;
		double shapeHF14 = 2.730605949;
		double scaleHF14 = 133197.4482;
		double shapeHF15 = 3.408987134;
		double scaleHF15 = 120216.1238;
		double shapeHF16 = 2.810296525;
		double scaleHF16 = 150334.2161;

		GammaDistribution nfDis = new GammaDistribution(shapeNF, scaleNF);
		GammaDistribution hf1Dis = new GammaDistribution(shapeHF1, scaleHF1);
		GammaDistribution hf2Dis = new GammaDistribution(shapeHF2, scaleHF2);
		GammaDistribution hf3Dis = new GammaDistribution(shapeHF3, scaleHF3);
		GammaDistribution hf4Dis = new GammaDistribution(shapeHF4, scaleHF4);
		GammaDistribution hf5Dis = new GammaDistribution(shapeHF5, scaleHF5);
		GammaDistribution hf6Dis = new GammaDistribution(shapeHF6, scaleHF6);
		GammaDistribution hf7Dis = new GammaDistribution(shapeHF7, scaleHF7);
		GammaDistribution hf8Dis = new GammaDistribution(shapeHF8, scaleHF8);
		GammaDistribution hf9Dis = new GammaDistribution(shapeHF9, scaleHF9);
		GammaDistribution hf10Dis = new GammaDistribution(shapeHF10, scaleHF10);
		GammaDistribution hf11Dis = new GammaDistribution(shapeHF11, scaleHF11);
		GammaDistribution hf12Dis = new GammaDistribution(shapeHF12, scaleHF12);
		GammaDistribution hf13Dis = new GammaDistribution(shapeHF13, scaleHF13);
		GammaDistribution hf14Dis = new GammaDistribution(shapeHF14, scaleHF14);
		GammaDistribution hf15Dis = new GammaDistribution(shapeHF15, scaleHF15);
		GammaDistribution hf16Dis = new GammaDistribution(shapeHF16, scaleHF16);
		
		// scaling factor to adjust the final equity, applied to all household types.
		double globalScale = HardcodedData.globalEquityScale;
		
		if (this.getCategory().equals(Category.NF)) {
			this.setEquity(new BigDecimal(globalScale*nfDis.sample()));
		} else if (this.getCategory().equals(Category.HF1)) {
			this.setEquity(new BigDecimal(globalScale*hf1Dis.sample()));
		} else if (this.getCategory().equals(Category.HF2)) {
			this.setEquity(new BigDecimal(globalScale*hf2Dis.sample()));
		} else if (this.getCategory().equals(Category.HF3)) {
			this.setEquity(new BigDecimal(globalScale*hf3Dis.sample()));
		} else if (this.getCategory().equals(Category.HF4)) {
			this.setEquity(new BigDecimal(globalScale*hf4Dis.sample()));
		} else if (this.getCategory().equals(Category.HF5)) {
			this.setEquity(new BigDecimal(globalScale*hf5Dis.sample()));
		} else if (this.getCategory().equals(Category.HF6)) {
			this.setEquity(new BigDecimal(globalScale*hf6Dis.sample()));
		} else if (this.getCategory().equals(Category.HF7)) {
			this.setEquity(new BigDecimal(globalScale*hf7Dis.sample()));
		} else if (this.getCategory().equals(Category.HF8)) {
			this.setEquity(new BigDecimal(globalScale*hf8Dis.sample()));
		} else if (this.getCategory().equals(Category.HF9)) {
			this.setEquity(new BigDecimal(globalScale*hf9Dis.sample()));
		} else if (this.getCategory().equals(Category.HF10)) {
			this.setEquity(new BigDecimal(globalScale*hf10Dis.sample()));
		} else if (this.getCategory().equals(Category.HF11)) {
			this.setEquity(new BigDecimal(globalScale*hf11Dis.sample()));
		} else if (this.getCategory().equals(Category.HF12)) {
			this.setEquity(new BigDecimal(globalScale*hf12Dis.sample()));
		} else if (this.getCategory().equals(Category.HF13)) {
			this.setEquity(new BigDecimal(globalScale*hf13Dis.sample()));
		} else if (this.getCategory().equals(Category.HF14)) {
			this.setEquity(new BigDecimal(globalScale*hf14Dis.sample()));
		} else if (this.getCategory().equals(Category.HF15)) {
			this.setEquity(new BigDecimal(globalScale*hf15Dis.sample()));
		} else if (this.getCategory().equals(Category.HF16)) {
			this.setEquity(new BigDecimal(globalScale*hf16Dis.sample()));
		}
		
	}
	
	
	/**
	 * @return name="numberResidents"
	 */
	public int getNumberResidents() {
		return residents.size();
	}

	/**
	 * @param numberResidents
	 *            name="numberResidents"
	 */
	public void setNumberResidents(
			@SuppressWarnings("hiding") int numberResidents) {
		this.numberResidents = numberResidents;
	}

	/**
	 * Compute the size of the household from the size of Resident array.
	 * 
	 * @return the number of residents in the households
	 * 
	 * @author Johan Barthelemy
	 */
	public void setNumberResidents() {
		this.numberResidents = this.getResidents().size();
	}

	/**
	 * This method checks if the household head and/or mate want to divorce.
	 * 
	 * @param lifeEventProbability
	 * @param random
	 * @return true if household head and mate are divorcing, false otherwise
	 * 
	 * @author Johan Barthelemy
	 */
	public boolean isGettingDivorced(LifeEventProbability lifeEventProbability,
			Random random) {
		boolean hhDivorcing = false;
		Iterator<Individual> itr = getResidents().iterator();
		while (itr.hasNext() && !hhDivorcing) {

			Individual curIndiv = itr.next();

			if (curIndiv.getHouseholdRelationship() == HouseholdRelationship.Married
					|| curIndiv.getHouseholdRelationship() == HouseholdRelationship.DeFacto) {
				hhDivorcing = curIndiv.isGettingDivorced(lifeEventProbability,
						random);
			}

		}
		return hhDivorcing;
	}

	/**
	 * This methods implements the divorce behaviour. Only individuals with
	 * household relationship set to Married or DeFacto can divorce which are
	 * the household head and his/her mate. One of these individual, randomly
	 * chosen, is leaving the household for a new one of category NF.
	 * 
	 * The new household is added to the pool of household and the original
	 * household and its member are modified to take into account the departure
	 * of an individual.
	 * 
	 * @param aHouseholdPool
	 *            the pool of household
	 * 
	 * @author Johan Barthelemy
	 */
	public Household getDivorced(HouseholdPool aHouseholdPool, Random random) {

		/*
		 * Finding the individual leaving the household. If selectIndiv = 0: the
		 * 1st married individual of the household leaves = 1: the 2nd married
		 * individual of the household leaves
		 */

		// Random random = GlobalRandom.getInstance();
		int selectIndiv = random.nextInt(2);

		logger.info(ModelMain.randNumFile);

		Individual curIndiv = new Individual();
		int curMarried = 0;
		boolean find = false;

		Iterator<Individual> itr = this.getResidents().iterator();
		while (itr.hasNext() && !find) {

			curIndiv = itr.next();

			if ((curIndiv.getHouseholdRelationship() == HouseholdRelationship.Married || curIndiv
					.getHouseholdRelationship() == HouseholdRelationship.DeFacto)
					&& selectIndiv == curMarried) {
				find = true;
			} else if (curIndiv.getHouseholdRelationship() == HouseholdRelationship.Married
					|| curIndiv.getHouseholdRelationship() == HouseholdRelationship.DeFacto) {
				curMarried++;
			}

		}

		/*
		 * Copying the selected individual attributes in selIndiv and removing
		 * it from the original household (ie the residents array)
		 */

		// curIndiv = individualPool.getByID(curIndiv.getId());

		// curIndiv = new Individual(curIndiv.getId(), curIndiv.getAge(),
		// curIndiv.getGender(), curIndiv.getIncome(),
		// HouseholdRelationship.LonePerson, curIndiv.getOccupation(),
		// curIndiv.getTransportModeToWork(),
		// curIndiv.getHighestEduFinished(), Category.NF, -1,
		// curIndiv.getYearsLivedIn(), curIndiv.getLivedTravelZoneid());

		curIndiv.setHouseholdRelationship(HouseholdRelationship.LonePerson);
		curIndiv.setHholdCategory(Category.NF);

		logger.debug("Remove from household because of divorce: "+curIndiv.toString());
		
		itr.remove();

		/* Generating an household for the individual leaving */

		// ArrayList<Individual> arrList = new ArrayList<Individual>();
		// arrList.add(curIndiv);

		Household newHh = new Household(
				aHouseholdPool.getMaxId()+1, Category.NF,
				curIndiv.getIncome());
		

		aHouseholdPool.add(newHh);
		newHh.addIndividual(curIndiv);
		newHh.setDwellingId(0);
		
		// LiveabilityUtility liveabilityUtility = new LiveabilityUtility();

		// int selectedTravelZoneid =
		// liveabilityUtility.chooseLocation(curIndiv,
		// this, 2, dwellingPool, year, interest, travelZoneCongestion);
		// if (selectedTravelZoneid == 0) {
		// logger.info((newHh + "can not afford to any travel zone"));
		// } else {
		// logger.info(newHh + "move to " + selectedTravelZoneid);
		//
		// allocateToDwelling(dwellingPool, selectedTravelZoneid);
		//
		// curIndiv.setLivedTravelZoneid(selectedTravelZoneid);
		// // curIndiv.setYearsLivedIn(0);
		// }

		/* Updating original household attributes */

		this.setNumberResidents(this.getNumberResidents() - 1); // number of
		// residents

		switch (this.getCategory()) { // household category
		case HF1:
			if (this.getNumberResidents() == 1) {
				this.setCategory(Category.NF);
            } else {
				this.setCategory(Category.HF16);
            }
			break;
		case HF2:
			this.setCategory(Category.HF9);
			break;
		case HF3:
			this.setCategory(Category.HF10);
			break;
		case HF4:
			this.setCategory(Category.HF11);
			break;
		case HF5:
			this.setCategory(Category.HF12);
			break;
		case HF6:
			this.setCategory(Category.HF13);
			break;
		case HF7:
			this.setCategory(Category.HF14);
			break;
		case HF8:
			this.setCategory(Category.HF15);
			break;
		// default:
		// this.setCategory(Category.HF16);
		// break;
		}

		// this.setGrossIncome(this.getGrossIncome()
		// .subtract(curIndiv.getIncome())); // gross income
		setGrossIncome();

		/* Household's members' attributes update */
		itr = this.getResidents().iterator();
		while (itr.hasNext()) {

			curIndiv = itr.next();
			curIndiv.setHholdCategory(this.getCategory()); // household category

			if (curIndiv.getHouseholdRelationship().equals(
					HouseholdRelationship.Married) // household relationship
					|| curIndiv.getHouseholdRelationship().equals(
							HouseholdRelationship.DeFacto)) {
				if (this.getCategory().equals(Category.NF)) {
					curIndiv.setHouseholdRelationship(HouseholdRelationship.LonePerson); // mate
																							// become
																							// lone
																							// person
				} else if (this.getCategory().equals(Category.HF16)) {
					curIndiv.setHouseholdRelationship(HouseholdRelationship.Relative);
				} else {
					curIndiv.setHouseholdRelationship(HouseholdRelationship.LoneParent); // or
																							// lone
																							// parent
				}
			}
		}

		return newHh;
	}

	/**
	 * @return the number of dependent children < 15
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNChildrenU15() {

		int n = 0;
		for (Individual individual : this.residents) {
			if (individual.getHouseholdRelationship() == HouseholdRelationship.U15Child) {
				n++;
			}
		}
		return n;

	}

	/**
	 * @return the number of independent children >= 15
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNChildrenO15() {

		int n = 0;
		for (Individual individual : this.residents) {
			if (individual.getHouseholdRelationship() == HouseholdRelationship.O15Child) {
				n++;
			}
		}
		return n;

	}

	/**
	 * @return the number of students
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNStudents() {

		int n = 0;
		for (Individual individual : this.residents) {
			if (individual.getHouseholdRelationship() == HouseholdRelationship.Student) {
				n++;
			}
		}
		return n;

	}

	/**
	 * @return the total number of children of the household
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNTotChildren() {

		return this.getNChildrenO15() + this.getNChildrenU15()
				+ this.getNStudents();

	}

	/**
	 * @return the number of de facto married in the households
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNDefacto() {

		int n = 0;
        for (Individual individual : this.getResidents()) {
            if (individual.getHouseholdRelationship() == HouseholdRelationship.DeFacto) {
                n++;
            }
        }
		return n;

	}

	/**
	 * @return the number of married individuals
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNMarried() {

		int n = 0;
        for (Individual individual : this.getResidents()) {
            if (individual.getHouseholdRelationship() == HouseholdRelationship.Married) {
                n++;
            }
        }
		return n;

	}

	/**
	 * @return the number of relatives living in the households
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNRelatives() {

		int n = 0;
        for (Individual individual : this.getResidents()) {
            if (individual.getHouseholdRelationship() == HouseholdRelationship.Relative) {
                n++;
            }
        }
		return n;

	}

	/**
	 * @return the number of non-relatives living in the households
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNNonRelatives() {

		int n = 0;
        for (Individual individual : this.getResidents()) {
            if (individual.getHouseholdRelationship() == HouseholdRelationship.NonRelative) {
                n++;
            }
        }
		return n;

	}

	/**
	 * @return the number of Lone parent in the household
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNLoneParent() {

		int n = 0;
        for (Individual individual : this.getResidents()) {
            if (individual.getHouseholdRelationship() == HouseholdRelationship.LoneParent) {
                n++;
            }
        }
		return n;

	}

	/**
	 * Returns the number of parents inside the household.
	 * 
	 * @return the number of parents of the household
	 * 
	 * @author Johan Barthelemy
	 */
	public int getNParents() {

		if ((this.getNDefacto() + this.getNMarried()) == 2) {
			return 2;
        } else if (this.getNLoneParent() == 1) {
			return 1;
        }
		return 0;
	}

	/**
	 * Returns the number of lone persons inside the household.
	 * 
	 * @return the number of parents of the household
	 * 
	 * @author Nam Huynh
	 */
	public int getNLonePersons() {
		int n = 0;
        for (Individual individual : this.getResidents()) {
            if (individual.getHouseholdRelationship() == HouseholdRelationship.LonePerson) {
                n++;
            }
        }
		return n;
	}

	/**
	 * Returns the number of group household people inside the household.
	 * 
	 * @return the number of parents of the household
	 * 
	 * @author Nam Huynh
	 */
	public int getNGroupHholders() {
		int n = 0;
        for (Individual individual : this.getResidents()) {
            if (individual.getHouseholdRelationship() == HouseholdRelationship.GroupHhold) {
                n++;
            }
        }
		return n;
	}

	/**
	 * This method determines the household category of the current household.
	 * 
	 * @return Category of the household.
	 * 
	 * @author Johan Barthelemy, edited/rewritten by Nam Huynh
	 */
	public Category computeHhCategory() {

		/* Initial household category */
		Category result = getCategory();

		int nParents = this.getNParents();
		int nChildU15 = this.getNChildrenU15();
		int nStudents = this.getNStudents();
		int nChildO15 = this.getNChildrenO15();
		int nRelative = this.getNRelatives() + this.getNNonRelatives();
		int nTotChildren = nChildU15 + nStudents + nChildO15;// getNTotChildren();
		int nNonFamilyMem = this.getNLonePersons() + this.getNGroupHholders();
		int nResidents = nParents + nTotChildren + nRelative + nNonFamilyMem;

		if (nResidents == 0) {
			return result;
        }

		// if (nParents>2) { // this should not happen but if it does, keep 2
		// parents only and change other parents into Relatives.
		// int tmpnParents = 0;
		// for (Individual indiv : this.getResidents())
		// if (indiv.getHouseholdRelationship()==HouseholdRelationship.Married
		// ||
		// indiv.getHouseholdRelationship()==HouseholdRelationship.DeFacto) {
		// if (tmpnParents<2) tmpnParents += 1;
		// else indiv.setHouseholdRelationship(HouseholdRelationship.Relative);
		// }
		// tfHdlr.writeToText("testCalHholdCat.csv",
		// "Hhold has more than 2 parents!!!", true);
		// // recalculates the number of parents in this household
		// nParents = getNMarried() + getNDefacto();
		// }

		if (nResidents == 1) {
			result = Category.NF;
			this.getResidents().get(0)
					.setHouseholdRelationship(HouseholdRelationship.LonePerson);
		} else {
			switch (nParents) {
			case 2:
				if (nTotChildren == 0) {
					// sets category to HF1
					result = Category.HF1;
				} else {
					if (nChildU15 > 0 && nStudents == 0 && nChildO15 == 0) {
						// sets category to HF5
						result = Category.HF5;
					}
					if (nChildU15 > 0 && nStudents > 0 && nChildO15 == 0) {
						// sets category to HF3
						result = Category.HF3;
					}
					if (nChildU15 > 0 && nStudents == 0 && nChildO15 > 0) {
						// sets category to HF4
						result = Category.HF4;
					}
					if (nChildU15 > 0 && nStudents > 0 && nChildO15 > 0) {
						// sets category to HF2
						result = Category.HF2;
					}
					if (nChildU15 == 0 && nStudents > 0 && nChildO15 == 0) {
						// sets category to HF7
						result = Category.HF7;
					}
					if (nChildU15 == 0 && nStudents == 0 && nChildO15 > 0) {
						// sets category to HF8
						result = Category.HF8;
					}
					if (nChildU15 == 0 && nStudents > 0 && nChildO15 > 0) {
						// sets category to HF6
						result = Category.HF6;
					}
				}
				break;
			case 1:
				if (nTotChildren == 0) {
					// assigns HF16 as category of this household
					result = Category.HF16;
					// // assigns Relatives as household relationship to all
					// residents in this household
					for (Individual indiv : this.getResidents()) {
						indiv.setHouseholdRelationship(HouseholdRelationship.Relative);
                    }
				} else {
					if (nChildU15 > 0 && nStudents == 0 && nChildO15 == 0) {
						// sets category to HF12
						result = Category.HF12;
					}
					if (nChildU15 > 0 && nStudents > 0 && nChildO15 == 0) {
						// sets category to HF10
						result = Category.HF10;
					}
					if (nChildU15 > 0 && nStudents == 0 && nChildO15 > 0) {
						// sets category to HF11
						result = Category.HF11;
					}
					if (nChildU15 > 0 && nStudents > 0 && nChildO15 > 0) {
						// sets category to HF9
						result = Category.HF9;
					}
					if (nChildU15 == 0 && nStudents > 0 && nChildO15 == 0) {
						// sets category to HF14
						result = Category.HF14;
					}
					if (nChildU15 == 0 && nStudents == 0 && nChildO15 > 0) {
						// sets category to HF15
						result = Category.HF15;
					}
					if (nChildU15 == 0 && nStudents > 0 && nChildO15 > 0) {
						// sets category to HF13
						result = Category.HF13;
					}
				}
				break;
			case 0:
				if (nRelative == 0 && nTotChildren == 0) {
					// assigns cateogry to NF
					result = Category.NF;
					// // assigns groupHhold as household relationship to all
					// residents in this household
					for (Individual indiv : this.getResidents()) {
						indiv.setHouseholdRelationship(HouseholdRelationship.GroupHhold);
                    }
				} else {
					// if (nRelative>0 || nTotChildren>0) {
					// assigns HF16 as category of this household
					result = Category.HF16;
					// // assigns Relatives as household relationship to all
					// residents in this household
					for (Individual indiv : this.getResidents()) {
						indiv.setHouseholdRelationship(HouseholdRelationship.Relative);
                    }
				}
				break;
			}
			// if (nParents==2 && nTotChildren>0) {
			// if (nChildU15>0 && nStudents==0 && nChildO15==0) {
			// // sets category to HF5
			// result = Category.HF5;
			// } else if (nChildU15>0 && nStudents>0 && nChildO15==0) {
			// // sets category to HF3
			// result = Category.HF3;
			// } else if (nChildU15>0 && nStudents==0 && nChildO15>0) {
			// // sets category to HF4
			// result = Category.HF4;
			// } else if (nChildU15>0 && nStudents>0 && nChildO15>0) {
			// // sets category to HF2
			// result = Category.HF2;
			// } else if (nChildU15==0 && nStudents>0 && nChildO15==0) {
			// // sets category to HF7
			// result = Category.HF7;
			// } else if (nChildU15==0 && nStudents==0 && nChildO15>0) {
			// // sets category to HF8
			// result = Category.HF8;
			// } else if (nChildU15==0 && nStudents>0 && nChildO15>0) {
			// // sets category to HF6
			// result = Category.HF6;
			// }
			// } else if (nParents==2 && nTotChildren==0) {
			// // sets category to HF1
			// result = Category.HF1;
			// } else if (nParents==1 && nTotChildren>0) {
			// if (nChildU15>0 && nStudents==0 && nChildO15==0) {
			// // sets category to HF12
			// result = Category.HF12;
			// } else if (nChildU15>0 && nStudents>0 && nChildO15==0) {
			// // sets category to HF10
			// result = Category.HF10;
			// } else if (nChildU15>0 && nStudents==0 && nChildO15>0) {
			// // sets category to HF11
			// result = Category.HF11;
			// } else if (nChildU15>0 && nStudents>0 && nChildO15>0) {
			// // sets category to HF9
			// result = Category.HF9;
			// } else if (nChildU15==0 && nStudents>0 && nChildO15==0) {
			// // sets category to HF14
			// result = Category.HF14;
			// } else if (nChildU15==0 && nStudents==0 && nChildO15>0) {
			// // sets category to HF15
			// result = Category.HF15;
			// } else if (nChildU15==0 && nStudents>0 && nChildO15>0) {
			// // sets category to HF13
			// result = Category.HF13;
			// }
			// } else if ((nParents==1 && nTotChildren==0) ||
			// (nParents==0 && (nRelative>0 || nTotChildren>0))) {
			// // assigns HF16 as category of this household
			// result = Category.HF16;
			// // assigns Relatives as household relationship to all residents
			// in this household
			// for (Individual indiv : this.getResidents())
			// indiv.setHouseholdRelationship(HouseholdRelationship.Relative);
			// } else if (nParents==0 && nRelative==0 && nTotChildren==0) {
			// // assigns cateogry to NF
			// result = Category.NF;
			// // assigns groupHhold as household relationship to all residents
			// in this household
			// for (Individual indiv : this.getResidents())
			// indiv.setHouseholdRelationship(HouseholdRelationship.GroupHhold);
			// }
		}

		this.setCategory(result);

		return result;
	}

	/**
	 * Check if the household contains one or more children.
	 * 
	 * @return true if household contains one or more children, false otherwise.
	 * 
	 * @author Johan Barthelemy
	 */
	public boolean hasChildren() {
        for (Individual indiv : getResidents()) {
            if (indiv.isChild()) {
                return true;
            }
        }

		return false;

	}

	/**
	 * This method handle the deaths inside a households. It first check if the
	 * household members stay alive or if there is a death, and update the
	 * household and individual attributes if necessary.
	 * 
	 * If there is a death, the method remove the individual from the pool of
	 * individual aIndPool, update numberResidents, numberResidents, grossIncome
	 * and category household attributes.
	 * 
	 * If the dead individual is married or de facto married, then the
	 * individual household relationship is modified.
	 * 
	 * When a single parent dies, the rest of the household is assumed to go to
	 * an orphanage, i.e. a non private-dwelling and is thus removed from the
	 * households and its members are also removed from the pool of individual.
	 * 
	 * on top of that, if the household is empty, the dwelling occupied should
	 * be empty
	 * 
	 * @param aLifeEventProbability
	 *            the life event probability including death probability
	 * @param aRandom
	 *            a random number
	 * @param aIndPool
	 *            the individual pool
	 * @param aHhPool
	 *            the household pool
	 * @param dwellingAllocator
	 *            the dwelling allocator for reset vacant dwelling
	 * 
	 * @author Johan Barthelemy edit by Qun and Nam Huynh
	 */
	public void isIndividualDying(LifeEventProbability aLifeEventProbability,
			Random aRandom, IndividualPool aIndPool, HouseholdPool aHhPool,
			DwellingAllocator dwellingAllocator, List<Individual> orphans,
			List<String[]> deadPeople) {

		int travelZoneId = 0;

		boolean aIndDied = false;
		Individual curInd = null;

		/* Loop over the members of the households */

		Iterator<Individual> itr = getResidents().iterator();

        List<Individual> deadIndividuals = ModelMain.getInstance().getDeadIndividuals();

		while (itr.hasNext()) {
			curInd = itr.next();
			if (!curInd.isAlive(aLifeEventProbability, aRandom)) { // a
				// death
				// occurs
				aIndDied = true;
				Individual individual = new Individual(curInd);

                if (logger.isDebugEnabled()) {
                    logger.debug(curInd.toString());
                    logger.debug(individual);
                    logger.debug(deadIndividuals.toString());
                }
                deadIndividuals.add(individual);

                if (logger.isDebugEnabled()) {
                    logger.debug(deadIndividuals.toString());
                }

				travelZoneId = curInd.getLivedTravelZoneid();
				this.killIndividual(aIndPool, curInd, itr, deadPeople); // ...
				// updating
				// the household
				// attribute

			}
		}

		/*
		 * If a death occurred in the household, update the residents'
		 * attributes
		 */

		if (aIndDied && this.getResidents().size() > 0) {
			this.updateDeathOccurred(orphans);
		}

		/* If no more resident then removing the household from the pool */

		else if (this.getResidents().size() == 0) {

			// logger.debug("travelZoneId:" + travelZoneId);
			// if (numberOfRooms != 0) {
			// dwellingControl.moveOut(travelZoneId, numberOfRooms);
			// dwellingPool.getTravelZoneVacantDwellings().get(travelZoneId)
			// .remove(dwellingPool.getDwellings().get(dwellingId));
			// }

			if (getDwellingId() != 0) {
				dwellingAllocator.moveOutDwelling(this);

			}
			aHhPool.remove(this);

		}
		this.computeHhCategory();
	}

	/**
	 * This method update the household in case an individual dies.
	 * 
	 * @param aIndPool
	 *            a pool of individual
	 * @param curInd
	 *            the individual dying
	 * 
	 * @author Johan Barthelemy
	 */

	public void killIndividual(IndividualPool aIndPool, Individual curInd, Iterator<Individual> itr,
			List<String[]> deadPeople) {

		/* Updating the household attributes */

		// this.getResidents().remove(curInd); // remove the individual from the
		// // household
		itr.remove();
		this.setNumberResidents(getNumberResidents() - 1); // update total
		// number of
		// residents
		// this.setGrossIncome(this.getGrossIncome().subtract(curInd.getIncome()));
		// // update
		setGrossIncome();
		// income
		// of
		// the
		// household
		// this.computeHhCategory(); // update household category

		/* Removing the individual from the pool */
		String[] deadPersonDetails = new String[] {
				String.valueOf(curInd.getId()),
				String.valueOf(curInd.getAge()),
				String.valueOf(curInd.getHouseholdId()),
				curInd.getHouseholdRelationship().toString(),
				curInd.getGender().toString() };
		deadPeople.add(deadPersonDetails);
		curInd.passAway(aIndPool);

	}

	/**
	 * Updating individual attributes after a death occurred in a household
	 * 
	 * @author Johan Barthelemy, edited by Nam Huynh
	 */
	public void updateDeathOccurred(List<Individual> orphans) {

		Individual curInd = null;

		int nParents = 0;
		Iterator<Individual> itr;

		nParents = this.getNParents(); // number of parents

		/*
		 * No more parents. Either - dependent under 15 years old children :
		 * removes the orphans - independent over 15 years old children :
		 * becoming relatives or lone persons - student: if alone, goes to a
		 * HF16 family if there is other people in the household or to a
		 * university accommodation (i.e. removing it from the pool)
		 */

		if (this.hasChildren() && nParents == 0) { // orphans

			itr = getResidents().iterator(); // loop over the remaining
			ArrayList<Individual> orphansThisHhold = new ArrayList<>();

			// individuals
			while (itr.hasNext()) {
				curInd = itr.next();
				if (curInd.getHouseholdRelationship() == HouseholdRelationship.U15Child) {
					// //removing dependent under 15 children
					// aIndPool.remove(curInd);
					// itr.remove();
					// this.setNumberResidents();

					// add this individual to the list of orphans
					orphans.add(curInd);
					// add this individual to a list so he/she can be removed
					// from this household
					orphansThisHhold.add(curInd);
				}
				if (curInd.getHouseholdRelationship() == HouseholdRelationship.O15Child
						|| curInd.getHouseholdRelationship() == HouseholdRelationship.Student) {
					// over 15 independent and Student become relative
					curInd.setHouseholdRelationship(HouseholdRelationship.Relative);
					// if (this.getNRelatives() > 0) {
					// curInd.setHouseholdRelationship(HouseholdRelationship.Relative);
					// } else {
					// curInd.setHouseholdRelationship(HouseholdRelationship.LonePerson);
					// }
				}

				// if (curInd.getHouseholdRelationship() ==
				// HouseholdRelationship.Student) { // ...
				// // students
				// if (this.getNRelatives() == 0) {
				// aIndPool.remove(curInd);
				// itr.remove();
				// this.setNumberResidents();
				// }
				// }

			}
			// remove orphans out of this household
			for (int iOrphan = 0; iOrphan <= orphansThisHhold.size() - 1; iOrphan++) {
				this.removeIndividual(orphansThisHhold.get(iOrphan));
            }

		}

		else {

			itr = this.getResidents().iterator(); // loop over the remaining
			while (itr.hasNext()) { // ... individuals

				curInd = itr.next();
				curInd.setHholdCategory(this.getCategory()); // new household
																// category

				if (this.getNumberResidents() == 1) {
					curInd.setHouseholdRelationship(HouseholdRelationship.LonePerson); // individual
					// becomes
					// alone
				} else {

					if ((curInd.getHouseholdRelationship() == HouseholdRelationship.Married || curInd
							.getHouseholdRelationship() == HouseholdRelationship.DeFacto)
							&& nParents == 1 && this.hasChildren()) { // married
						// people
						// lost
						// his/her
						// mate
						curInd.setHouseholdRelationship(HouseholdRelationship.LoneParent); // ...
						// and
						// become
						// lone
						// parent
					} else if ((curInd.getHouseholdRelationship() == HouseholdRelationship.Married || curInd
							.getHouseholdRelationship() == HouseholdRelationship.DeFacto)
							&& nParents == 1 && !this.hasChildren()) { // married
						// people
						// lost
						// his/her
						// mate
						// and
						// children
						curInd.setHouseholdRelationship(HouseholdRelationship.Relative); // ...
						// and
						// become
						// lone
						// person
					}

				}

			}

		}

	}

	/**
	 * This method handle the birth inside a households. It first check if the
	 * household members have a baby and in that case update the household and
	 * individual attributes if necessary.
	 * 
	 * The number of babies are computing using the 2010 rates coming from ABS
	 * website for New South Wales.
	 * 
	 * The chance of having childs for women, given by age class, can be found
	 * in the database within the table
	 * pop_birth_2007.chances_for_females_having_childs_by_age
	 * 
	 * Only female between 15 and 49 can give birth.
	 * 
	 * 
	 * @param aLifeEventProbability
	 *            the life event probability including reproduction probability
	 * @param aRandom
	 *            a random number
	 * @param aIndPool
	 *            the individual pool
	 * @return whether there is at least one child
	 * 
	 * @author Johan Barthelemy
	 */
	@SuppressWarnings("null")
	public boolean newChild(LifeEventProbability aLifeEventProbability,
			Random aRandom, IndividualPool aIndPool, List<String[]> newborns) {

		double maleProp = 0.48;
		double singleBabyProp = 0.984;
		double twinsBabyProp = 0.014;
		double tripletBabyProp = 0.002;
		double probaNBaby = 0.0;
		int travelZoneID = -1;
		Gender genBaby;
		int idBaby;
		boolean isNewBaby = false;

		/* Loop over the members of the households */

		int babyNumber = 0;

        for (Individual curInd : this.getResidents()) {

			/* Testing if an woman is having a baby */

            if (curInd.getGender() == Gender.Female
                    && curInd.getAge() >= 15
                    && curInd.getAge() <= 49
                    && curInd.isGettingChild(aLifeEventProbability, aRandom, getNTotChildren())) {

                isNewBaby = true;

                // compute the number of babies
                probaNBaby = aRandom.nextDouble();

                if (probaNBaby <= tripletBabyProp + twinsBabyProp + singleBabyProp) {
                    babyNumber++;
                } else if (probaNBaby <= tripletBabyProp + twinsBabyProp) {
                    babyNumber = babyNumber + 2;
                } else {
                    babyNumber = babyNumber + 3;
                }

                if (curInd.getHouseholdRelationship() != HouseholdRelationship.Married
                        && curInd.getHouseholdRelationship() != HouseholdRelationship.DeFacto) {
                    curInd.setHouseholdRelationship(HouseholdRelationship.LoneParent);
                }

                travelZoneID = curInd.getLivedTravelZoneid();
            }

        }

		/* Building the babies */

		for (int i = 0; i < babyNumber; i++) {

			Double genderProb = aRandom.nextDouble();

			/* computing the gender of the current baby */

			if (genderProb < maleProp) {
				genBaby = Gender.Male;
			} else {
				genBaby = Gender.Female;
			}

			/* Computing its id */

			//idBaby = aIndPool.getMaxId() + 1;
			idBaby = -1;
			
			/*
			 * generating the baby and updating the household and the pool of
			 * individuals
			 */

			Individual baby = new Individual(idBaby, 0, genBaby, BigDecimal.ZERO,
					HouseholdRelationship.U15Child,
					Occupation.InadequatelyDescribedOrNotStated,
					TransportModeToWork.Other,
					HighestEduFinished.LevelOfEducationNotStated,
					this.getCategory(), this.getId(),
					travelZoneID);
			
			aIndPool.add(baby);
			this.residents.add(baby);
			this.setNumberResidents();
			//this.computeHhCategory();
			
			newborns.add(new String[] {String.valueOf(baby.getId()), 
					baby.getGender().toString(),
					String.valueOf(this.getId())});
		}

		return isNewBaby;
	}

	/**
	 * toString method
	 * 
	 * @return a String describing the household.
	 * 
	 * @author Qun
	 */
	@Override
	public String toString() {
        StringBuilder builder = new StringBuilder();

		builder.append("Household [").append(this.id).append(", live in ").append(this.dwellingId).append(',')
				.append(this.category).append(", ").append(this.numberResidents).append(", $")
				.append(this.grossIncome);

        builder.append("\nResidents: ");
        for (Individual indiv : residents) {
            builder.append(indiv.toString());
            builder.append('\n');

        }

        builder.append(']');

        return builder.toString();
	}

	/**
	 * finds individuals willing to get married in the household and remove he,
	 * she or those out of the household
	 * 
	 * @param aLifeEventProbability
	 * @param aRandom
	 * @param menIndividuals
	 *            men marriage candidates
	 * @param womenIndividuals
	 *            women marriage candidates
	 * @author qun
	 */
	public void generateWeddingSet(LifeEventProbability aLifeEventProbability,
			Random aRandom, Set<Individual> menIndividuals,
			Set<Individual> womenIndividuals) {

		boolean indMarry = false;
		Iterator<Individual> iterator = getResidents().iterator();

		while (iterator.hasNext()) {

			Individual individual = iterator.next();

			switch (individual.getHouseholdRelationship()) {
			case Married:
			case DeFacto:
			case U15Child:
				// case Student:
			case Visitor:
				// result = false;
				continue;
			}

			if (individual.getAge() <= 64 && individual.getAge() >= 16) {
				indMarry = individual.isGettingMarried(aLifeEventProbability,
						aRandom);
			}
			if (indMarry) {
				if (individual.getGender() == Gender.Male) {
					menIndividuals.add(individual);
				} else {
					womenIndividuals.add(individual);
				}

				logger.debug("leave household because of get married: "+individual.toString());
				
				iterator.remove();
			}
		}

	}


	/**
	 * This method add the members of a given household to the current
	 * household. A new household relationship status is given to the lone
	 * parent or lone individual belonging to the given household.
	 * 
	 * @param aHousehold
	 * @param aHouseholdRelationship
	 * 
	 * @author Johan Barthelemy
	 */
	public void mergeHousehold(Household aHousehold,
			HouseholdRelationship aHouseholdRelationship) {

		int livedTravelZoneId = getResidents().get(0).getLivedTravelZoneid();

		// Individual curInd = null;

		/* Merging the members' list of the 2 households */

		// ArrayList<Individual> newResidents = this.getResidents();
		// newResidents.addAll(aHousehold.getResidents());
		// this.setResidents(newResidents);

		for (Individual individual : aHousehold.getResidents()) {
			addIndividual(individual);
		}

		/*
		 * Updating the household relationship status of the lone parent or the
		 * lone person from aHousehold
		 */

		// Iterator<Individual> itr = aHousehold.getResidents().iterator();
		// while (itr.hasNext() == true) {
		// curInd = itr.next();
		// curInd = individualPool.getByID(curInd.getId());
		// if (curInd.getHouseholdRelationship() ==
		// HouseholdRelationship.LoneParent
		// || curInd.getHouseholdRelationship() ==
		// HouseholdRelationship.LonePerson) {
		// curInd.setHouseholdRelationship(aHouseholdRelationship);
		// }
		// // curInd.setHouseholdId(getId());
		// }

		/* Updating household attributes */

		for (Individual individual : getResidents()) {

			// if (individual.getHouseholdRelationship() ==
			// HouseholdRelationship.LoneParent
			// || individual.getHouseholdRelationship() ==
			// HouseholdRelationship.LonePerson) {
			individual.setHouseholdRelationship(aHouseholdRelationship);
			// }

			individual.setHholdCategory(getCategory());

			individual.setHouseholdId(getId());

			individual.setLivedTravelZoneid(livedTravelZoneId);

		}

		this.setGrossIncome(); // income
		this.setNumberResidents(); // number of residents
		computeHhCategory(); // category
		// this.setNumberResidents(); // number of residents
	}

	/**
	 * Add an individual to the household
	 * 
	 * @param aIndividual
	 *            the individual added to the household
	 * 
	 * @author Johan Barthelemy
	 */
	public void addIndividual(Individual aIndividual) {

		aIndividual.setHouseholdId(this.id);
		this.getResidents().add(aIndividual);
		this.setNumberResidents();
		this.computeHhCategory();
		this.setGrossIncome();

	}

	/**
	 * Remove an individual from the household.
	 * 
	 * @param aIndividual
	 *            the individual to be removed.
	 * 
	 * @author Johan Barthelemy
	 */
	public void removeIndividual(Individual aIndividual) {

		this.getResidents().remove(aIndividual);
		this.computeHhCategory();
		this.setGrossIncome();
		this.setNumberResidents();

	}



	public OwnershipStatus getOwnershipStatus() {
		return this.ownershipStatus;
	}

	public void setOwnershipStatus(OwnershipStatus ownershipStatus) {
		this.ownershipStatus = ownershipStatus;
	}

	public BigDecimal getPrincipleValue() {
		return this.principleValue;
	}

	public void setPrincipleValue(
			@SuppressWarnings("hiding") BigDecimal principleValue) {
		this.principleValue = principleValue;
	}

	public int getYearBoughtThisProperties() {
		return this.yearBoughtThisProperties;
	}

	public void setYearBoughtThisProperties(
			@SuppressWarnings("hiding") int yearBoughtThisProperties) {
		this.yearBoughtThisProperties = yearBoughtThisProperties;
	}


	/**
	 * Returns the Individual object from the household, given the individual ID
	 * 
	 * @param indivID
	 * @return
	 */
	public Individual getIndividualInHholdByID(int indivID) {
		int numResidents;
		Individual individual = null;
		numResidents = this.getNumberResidents();
		for (int i = 0; i < numResidents; i++) {
			if (this.getResidents().get(i).getId() == indivID) {
				individual = this.getResidents().get(i);
				break;
			}
		}
		return individual;
	}


	public int getDwellingId() {
		return this.dwellingId;
	}

	public void setDwellingId(int dwellingId) {
		this.dwellingId = dwellingId;
		setTenure(0);
	}

	/**
	 * calculates the bedrooms need of a household
	 * 
	 * @return the minimum number of required bedrooms
	 */
	public int calculateNumberOfRoomNeed() {

		int boys = 0, girls = 0, couples = 0;

		int number;

		for (Individual individual : getResidents()) {
			if (individual.getAge() < 10
					&& individual.getGender() == Gender.Male) {
				boys++;
			}

			if (individual.getAge() < 10
					&& individual.getGender() == Gender.Female) {
				girls++;
			}

			if (individual.getHouseholdRelationship() == HouseholdRelationship.DeFacto
					|| individual.getHouseholdRelationship() == HouseholdRelationship.Married) {
				couples++;
			}
		}

		number = getNumberResidents() - couples - girls - boys;

		number += (couples / 2) + 1;

		if (couples % 2 == 0) {
			number--;
		}

		number += (girls / 3) + 1;

		if (girls % 3 == 0) {
			number--;
		}

		number += (boys / 3) + 1;

		if (boys % 3 == 0) {
			number--;
		}

		if (number > HardcodedData.MAX_BEDROOMS) {
			number = HardcodedData.MAX_BEDROOMS;
		}

		return number;
	}

	public void setLivedTravelZone(int travelZoneId) {
		for (Individual individual : getResidents()) {
			individual.setLivedTravelZoneid(travelZoneId);
		}

	}

	public void setTravelDiariesChanged(boolean travelDiariesChanged) {
		this.travelDiariesChanged = travelDiariesChanged;
	}

	public boolean isTravelDiariesChanged() {
		return travelDiariesChanged;
	}

	/**
	 * @return the tenure
	 */
	public double getTenure() {
		return tenure;
	}

	/**
	 * @param tenure
	 *            the tenure to set
	 */
	public void setTenure(double tenure) {
		this.tenure = tenure;
	}
	
	public int getLivedTravelZoneId() {
		return residents.get(0).getLivedTravelZoneid();
	}
	

	public List<Individual> getParents() {
		List<Individual> parents = new ArrayList<>();
		for (Individual individual : residents) {
			if ((individual.getHouseholdRelationship() == HouseholdRelationship.DeFacto)
					|| (individual.getHouseholdRelationship() == HouseholdRelationship.LoneParent)
					|| (individual.getHouseholdRelationship() == HouseholdRelationship.Married)) {
				parents.add(individual);
			}
		}

		return parents;
	}

	public BigDecimal getEquity() {
		return equity;
	}

	public void setEquity(BigDecimal equity) {
		this.equity = equity;
	}

	public BigDecimal getYearlyMortgagePayment() {
		return yearlyMortgagePayment;
	}

	public void setYearlyMortgagePayment(BigDecimal yearlyMortgagePayment) {
		this.yearlyMortgagePayment = yearlyMortgagePayment;
	}

	public BigDecimal getSavingsForHouseBuying() {
		return savingsForHouseBuying;
	}

	public void setSavingsForHouseBuying(BigDecimal savingsForHouseBuying) {
		this.savingsForHouseBuying = savingsForHouseBuying;
	}

	public BigDecimal getPriceCrnDwelling() {
		return priceCrnDwelling;
	}

	public void setPriceCrnDwelling(BigDecimal priceCrnDwelling) {
		this.priceCrnDwelling = priceCrnDwelling;
	}

	public BigDecimal getDutyPaid() {
		return dutyPaid;
	}

	public void setDutyPaid(BigDecimal dutyPaid) {
		this.dutyPaid = dutyPaid;
	}

}