package core;

import org.apache.log4j.PatternLayout;

/**
 * this class is required as a property of LocChoiceFile in lo4j.properties.
 * @author nhuynh
 *
 */
public class LocationChoicePatternLayout extends PatternLayout {
	/**
	 * returns headers for locationChoice.csv.
	 * 
	 */
	@Override
	public String getHeader() {
		return "hholdId,hholdType,reasonToMove,cdMoveFr,cdMoveTo,houseBudget,cdMoveToHousePrice_Sale,cdMoveToHousePrice_Rent,ownershipStatus,"
				+ "indivId,indivAge,indivGender,indivIncome,indivSatIdx_crnLoc,indivSatIdx_nxtLoc,year"
				+ System.getProperty("line.separator");
	}
}