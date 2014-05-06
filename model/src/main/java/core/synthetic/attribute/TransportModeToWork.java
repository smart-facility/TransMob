package core.synthetic.attribute;

import core.HardcodedData;

/**
 * The transport mode to work distribution in the individual pool
 * 
 * @author qun
 */
public enum TransportModeToWork {
	/**
	 * name="train"
	 * 
	 * @uml.associationEnd
	 */
	Train, /**
	 * name="bus"
	 * 
	 * @uml.associationEnd
	 */
	Bus, /**
	 * name="ferry"
	 * 
	 * @uml.associationEnd
	 */
	Ferry, /**
	 * name="tramIncludesLightRail"
	 * 
	 * @uml.associationEnd
	 */
	TramIncludesLightRail, /**
	 * name="taxi"
	 * 
	 * @uml.associationEnd
	 */
	Taxi, /**
	 * name="carAsDriver"
	 * 
	 * @uml.associationEnd
	 */
	CarAsDriver, /**
	 * name="carAsPassenger"
	 * 
	 * @uml.associationEnd
	 */
	CarAsPassenger, /**
	 * name="truck"
	 * 
	 * @uml.associationEnd
	 */
	Truck, /**
	 * name="motorbikeOrScooter"
	 * 
	 * @uml.associationEnd
	 */
	MotorbikeOrScooter,
	/**
	 * name="bicycle"
	 * 
	 * @uml.associationEnd
	 */
	Bicycle, /**
	 * name="other"
	 * 
	 * @uml.associationEnd
	 */
	Other, /**
	 * name="walkedOnly"
	 * 
	 * @uml.associationEnd
	 */
	WalkedOnly;

	public static TransportModeToWork random() {
		// SecureRandom random = GlobalRandom.getInstance();
		return TransportModeToWork.values()[HardcodedData.random
				.nextInt(TransportModeToWork.values().length)];
	}
}
