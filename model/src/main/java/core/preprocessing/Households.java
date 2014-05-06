package core.preprocessing;

/**
 * This class define the structure of output files: .HOUSEHOLDS.
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * @deprecated
 */
@Deprecated
public class Households {

	/**
	 * @return
	 *  name="hhold"
	 */
	public Integer getHhold() {
		return hhold;
	}

	/**
	 * @return
	 *  name="location"
	 */
	public Integer getLocation() {
		return location;
	}

	/**
	 * @return
	 *  name="persons"
	 */
	public Integer getPersons() {
		return persons;
	}

	/**
	 * @return
	 *  name="workers"
	 */
	public Integer getWorkers() {
		return workers;
	}

	/**
	 * @return
	 *  name="vehicles"
	 */
	public Integer getVehicles() {
		return vehicles;
	}

	/**
	 * @param hhold
	 *  name="hhold"
	 */
	public void setHhold(int hhold) {
		this.hhold = hhold;
	}

	/**
	 * @param location
	 *  name="location"
	 */
	public void setLocation(int location) {
		this.location = location;
	}

	/**
	 * @param persons
	 *  name="persons"
	 */
	public void setPersons(int persons) {
		this.persons = persons;
	}

	/**
	 * @param workers
	 *  name="workers"
	 */
	public void setWorkers(int workers) {
		this.workers = workers;
	}

	/**
	 * @param vehicles
	 *  name="vehicles"
	 */
	public void setVehicles(int vehicles) {
		this.vehicles = vehicles;
	}

	/**
	 *  name="hhold"
	 */
	private int hhold;
	/**
	 *  name="location"
	 */
	private int location;
	/**
	 *  name="persons"
	 */
	private int persons;
	/**
	 *  name="workers"
	 */
	private int workers;
	/**
	 *  name="vehicles"
	 */
	private int vehicles;
}
