package core.synthetic.dwelling;


/**
 * a class represents dwelling
 * 
 * @author qun
 * 
 */

public class Dwelling {

	private int livedHouseholdId;
	private double xCoord;
	private double yCoord;
	private int numberOfRooms;
	private int yearAvailable;
	private int travelZoneId;
	private int id;
	private int noteBus;
	private int noteTrain;
	private int activityLocation;
	private double salePrices;

	public Dwelling() {

	}

	public Dwelling(int livedHouseholdId, double xCoord, double yCoord,
			int numberOfRooms, int yearAvailable, int travelZoneId, int id,
			int noteBus, int noteTrain, int activityLocation,
			double salePrices) {
		super();
		this.livedHouseholdId = livedHouseholdId;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.numberOfRooms = numberOfRooms;
		this.yearAvailable = yearAvailable;
		this.travelZoneId = travelZoneId;
		this.id = id;
		this.noteBus = noteBus;
		this.noteTrain = noteTrain;
		this.activityLocation = activityLocation;
		this.salePrices = salePrices;
	}

	/**
	 * @return the activityLocation
	 */
	public int getActivityLocation() {
		return activityLocation;
	}

	/**
	 * @return the livedHouseholdId
	 */
	public int getLivedHouseholdId() {
		return livedHouseholdId;
	}

	/**
	 * @param livedHouseholdId
	 *            the livedHouseholdId to set
	 */
	public void setLivedHouseholdId(int livedHouseholdId) {
		this.livedHouseholdId = livedHouseholdId;
	}

	/**
	 * @return the xCoord
	 */
	public double getxCoord() {
		return xCoord;
	}

	/**
	 * @return the yCoord
	 */
	public double getyCoord() {
		return yCoord;
	}

	/**
	 * @return the numberOfRooms
	 */
	public int getNumberOfRooms() {
		return numberOfRooms;
	}

	/**
	 * @return the yearAvailable
	 */
	public int getYearAvailable() {
		return yearAvailable;
	}

	/**
	 * @return the travelZoneId
	 */
	public int getTravelZoneId() {
		return travelZoneId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */

	/**
	 * @return the noteBus
	 */
	public int getNoteBus() {
		return noteBus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dwelling [livedHouseholdId=" + livedHouseholdId + ", xCoord="
				+ xCoord + ", yCoord=" + yCoord + ", numberOfRooms="
				+ numberOfRooms + ", yearAvailable=" + yearAvailable
				+ ", travelZoneId=" + travelZoneId + ", id=" + id
				+ ", noteBus=" + noteBus + ", noteTrain=" + noteTrain
				+ ", activityLocation=" + activityLocation + ", salePrices="
				+ salePrices + "]";
	}

	/**
	 * @return the noteTrain
	 */
	public int getNoteTrain() {
		return noteTrain;
	}

	/**
	 * @return the salePrices
	 */
	public double getSalePrices() {
		return salePrices;
	}
}
