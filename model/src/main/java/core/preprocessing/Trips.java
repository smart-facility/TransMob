package core.preprocessing;

/**
 * This class define the structure of output files: .TRIPS.
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * 
 */
public class Trips {


	public Trips() {
	}

	public Trips(int hhold, int person, int trip, int purpose, int mode,
			int vehicle, int start, int origin, int end,
			int destination) {
		this.hhold = hhold;
		this.person = person;
		this.mode = mode;
		this.start = start;
		this.origin = origin;
		this.end = end;
		this.destination = destination;
		this.purpose = purpose;
		this.vehicle = vehicle;
		this.trip = trip;
	}

	/**
	 * @param hhold
	 *  name="hhold"
	 */
	public void setHhold(int hhold) {
		this.hhold = hhold;
	}

	/**
	 * @param person
	 *  name="person"
	 */
	public void setPerson(int person) {
		this.person = person;
	}

	/**
	 * @param trip
	 *  name="trip"
	 */
	public void setTrip(int trip) {
		this.trip = trip;
	}

	/**
	 * @param mode
	 *  name="mode"
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * @param vehicle
	 *  name="vehicle"
	 */
	public void setVehicle(int vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * @param start
	 *  name="start"
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @param origin
	 *  name="origin"
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	/**
	 * @param end
	 *  name="end"
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @param destination
	 *  name="destination"
	 */
	public void setDestination(int destination) {
		this.destination = destination;
	}

	/**
	 * @param constraint
	 *  name="constraint"
	 */
	public void setConstraint(int constraint) {
		this.constraint = constraint;
	}

	/**
	 * @return
	 *  name="hhold"
	 */
	public Integer getHhold() {
		return hhold;
	}

	/**
	 * @return
	 *  name="person"
	 */
	public Integer getPerson() {
		return person;
	}

	/**
	 * @return
	 *  name="trip"
	 */
	public Integer getTrip() {
		return trip;
	}

	/**
	 * @return
	 *  name="mode"
	 */
	public Integer getMode() {
		return mode;
	}

	/**
	 * @return
	 *  name="vehicle"
	 */
	public Integer getVehicle() {
		return vehicle;
	}

	/**
	 * @return
	 *  name="start"
	 */
	public Integer getStart() {
		return start;
	}

	/**
	 * @return
	 *  name="origin"
	 */
	public Integer getOrigin() {
		return origin;
	}

	/**
	 * @return
	 *  name="end"
	 */
	public Integer getEnd() {
		return end;
	}

	/**
	 * @return
	 *  name="destination"
	 */
	public Integer getDestination() {
		return destination;
	}

	/**
	 * @return
	 *  name="constraint"
	 */
	public Integer getConstraint() {
		return constraint;
	}

	/**
	 * @return
	 *  name="purpose"
	 */
	public Integer getPurpose() {
		return purpose;
	}

	/**
	 * @param purpose
	 *  name="purpose"
	 */
	public void setPurpose(int purpose) {
		this.purpose = purpose;
	}

	/**
	 *  name="hhold"
	 */
	private int hhold;
	/**
	 *  name="person"
	 */
	private int person;
	/**
	 *  name="trip"
	 */
	private int trip;
	/**
	 *  name="purpose"
	 */
	private int purpose;
	/**
	 *  name="mode"
	 */
	private int mode;
	/**
	 *  name="vehicle"
	 */
	private int vehicle;
	/**
	 *  name="start"
	 */
	private int start;
	/**
	 *  name="origin"
	 */
	private int origin;
	/**
	 *  name="end"
	 */
	private int end;
	/**
	 *  name="destination"
	 */
	private int destination;
	/**
	 *  name="constraint"
	 */
	private int constraint;
}
