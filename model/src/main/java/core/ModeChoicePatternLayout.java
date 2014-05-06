package core;
import org.apache.log4j.PatternLayout;

/**
 * this class is required as a property of ModeChoiceFile in lo4j.properties.
 * @author nhuynh
 *
 */
public class ModeChoicePatternLayout extends PatternLayout {
	/**
	 * returns headers for modeChoice.csv.
	 * 
	 */
	@Override
	public String getHeader() {
		return "indivId,indivAge,indivGender,indivIncome,indivHholdRel,hholdType,hholdId,origin,destination," +
							"currentMode,newMode,year,percentage, reason" + 
							System.getProperty("line.separator");
	}
}
