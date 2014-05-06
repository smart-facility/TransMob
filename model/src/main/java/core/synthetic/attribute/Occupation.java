package core.synthetic.attribute;

import core.HardcodedData;

/**
 * The occupation distribution in the individual pool.
 * 
 * @author qun
 */
public enum Occupation {
	/**
	 * name="managers"
	 * 
	 * @uml.associationEnd
	 */
	Managers, /**
	 * name="professionals"
	 * 
	 * @uml.associationEnd
	 */
	Professionals, /**
	 * name="techniciansAndTradesWorkers"
	 * 
	 * @uml.associationEnd
	 */
	TechniciansAndTradesWorkers, /**
	 * 
	 * name="communityAndPersonalServiceWorkers"
	 * 
	 * @uml.associationEnd
	 */
	CommunityAndPersonalServiceWorkers,
	/**
	 * name="clericalAndAdministrativeWorkers"
	 * 
	 * @uml.associationEnd
	 */
	ClericalAndAdministrativeWorkers, /**
	 * name="salesWorkers"
	 * 
	 * @uml.associationEnd
	 */
	SalesWorkers, /**
	 * name="machineryOperatorsAndDrivers"
	 * 
	 * @uml.associationEnd
	 */
	MachineryOperatorsAndDrivers, /**
	 * name="labourers"
	 * 
	 * @uml.associationEnd
	 */
	Labourers,
	/**
	 * name="inadequatelyDescribedOrNotStated"
	 * 
	 * @uml.associationEnd
	 */
	InadequatelyDescribedOrNotStated;

	public static Occupation random() {
		// SecureRandom random = GlobalRandom.getInstance();
		return Occupation.values()[HardcodedData.random.nextInt(Occupation
				.values().length)];
	}
}
