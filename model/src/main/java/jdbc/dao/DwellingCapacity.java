package jdbc.dao;

public class DwellingCapacity {

	private int tz06;
	private int oneBedroom;
	private int twoBedroom;
	private int threeBedroom;
	private int fourBedroom;

	public DwellingCapacity(int tz06, int oneBedroom, int twoBedroom, int threeBedroom, int fourBedroom) {
		super();
		this.tz06 = tz06;
		this.oneBedroom = oneBedroom;
		this.twoBedroom = twoBedroom;
		this.threeBedroom = threeBedroom;
		this.fourBedroom = fourBedroom;
	}

	/**
	 * @return the tz06
	 */
	public int getTz06() {
		return tz06;
	}

	/**
	 * @param tz06
	 *            the tz06 to set
	 */
	public void setTz06(int tz06) {
		this.tz06 = tz06;
	}

	/**
	 * @return the oneBedroom
	 */
	public int getOneBedroom() {
		return oneBedroom;
	}

	/**
	 * @param oneBedroom
	 *            the oneBedroom to set
	 */
	public void setOneBedroom(int oneBedroom) {
		this.oneBedroom = oneBedroom;
	}

	/**
	 * @return the twoBedroom
	 */
	public int getTwoBedroom() {
		return twoBedroom;
	}

	/**
	 * @param twoBedroom
	 *            the twoBedroom to set
	 */
	public void setTwoBedroom(int twoBedroom) {
		this.twoBedroom = twoBedroom;
	}

	/**
	 * @return the threeBedroom
	 */
	public int getThreeBedroom() {
		return threeBedroom;
	}

	/**
	 * @param threeBedroom
	 *            the threeBedroom to set
	 */
	public void setThreeBedoom(int threeBedroom) {
		this.threeBedroom = threeBedroom;
	}

	/**
	 * @return the fourBedroom
	 */
	public int getFourBedroom() {
		return fourBedroom;
	}

	/**
	 * @param fourBedroom
	 *            the fourBedroom to set
	 */
	public void setFourBedroom(int fourBedroom) {
		this.fourBedroom = fourBedroom;
	}

}
