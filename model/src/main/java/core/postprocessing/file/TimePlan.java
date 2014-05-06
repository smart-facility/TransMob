package core.postprocessing.file;

import core.synthetic.travel.mode.TravelModes;

import java.math.BigDecimal;

/**
 * A class transform a row of time plan record into a object.
 * 
 * @author qun
 * 
 */
public class TimePlan {

	private int hhold;

	private int person;

	private int trip;

	private int start;

	private int end;

	private int depart;

	private int arrive;

	private int origin;

	private int destination;

	private int purpose;

	private double walkTime;

	private double driveTime;

	private double transitTime;

	private double waitTime;

	private double otherTime;

	private TravelModes mode;

	private double cost;
	
	private double deltaVariableCost;
	
	private double deltaFixedCost;
	
	private String label;

	private BigDecimal parkingFee;
	
	public TimePlan() {
		super();
		// activityIDMap = new HashMap<Integer, Integer>();
		// readInMapping();
	}

	public TimePlan(int hhold, int person, int trip, int start, int end,
			int depart, int arrive, int origin, int destination, int purpose,
			double walkTime, double driveTime, double transitTime,
			double waitTime, double otherTime, TravelModes mode, double cost,
			String label, BigDecimal parkingFee) {
		super();
		this.hhold = hhold;
		this.person = person;
		this.trip = trip;
		this.start = start;
		this.end = end;
		this.depart = depart;
		this.arrive = arrive;
		this.origin = origin;
		this.destination = destination;
		this.purpose = purpose;
		this.walkTime = walkTime;
		this.driveTime = driveTime;
		this.transitTime = transitTime;
		this.waitTime = waitTime;
		this.otherTime = otherTime;
		this.mode = mode;
		this.cost = cost;
		this.label = label;
		this.parkingFee = parkingFee;
	}

	public TimePlan(int hhold, int person, int trip, int start, int end,
			int depart, int arrive, int origin, int destination, int purpose,
			double walkTime, double driveTime, double transitTime,
			double waitTime, double otherTime, TravelModes mode, double deltaFixedCost,
			double deltaVariableCost, String label, BigDecimal parkingFee) {
		super();
		this.hhold = hhold;
		this.person = person;
		this.trip = trip;
		this.start = start;
		this.end = end;
		this.depart = depart;
		this.arrive = arrive;
		this.origin = origin;
		this.destination = destination;
		this.purpose = purpose;
		this.walkTime = walkTime;
		this.driveTime = driveTime;
		this.transitTime = transitTime;
		this.waitTime = waitTime;
		this.otherTime = otherTime;
		this.mode = mode;
		this.deltaFixedCost = deltaFixedCost;
		this.deltaVariableCost = deltaVariableCost;
		this.label = label;
		this.parkingFee = parkingFee;
	}
	public int getOrigin() {
		return origin;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public double getWalkTime() {
		return walkTime;
	}

	public void setWalkTime(double walkTime) {
		this.walkTime = walkTime;
	}

	public double getDriveTime() {
		return driveTime;
	}

	public void setDriveTime(double driveTime) {
		this.driveTime = driveTime;
	}

	public double getTransitTime() {
		return transitTime;
	}

	public void setTransitTime(double transitTime) {
		this.transitTime = transitTime;
	}

	public TravelModes getMode() {
		return mode;
	}

	public void setMode(TravelModes mode) {
		this.mode = mode;
	}

	public double getOtherTime() {
		return otherTime;
	}

	public void setOtherTime(double otherTime) {
		this.otherTime = otherTime;
	}

	@Override
	public String toString() {
		return "TimePlan [hhold=" + hhold + ", person=" + person + ", trip="
				+ trip + ", start=" + start + ", end=" + end + ", depart="
				+ depart + ", arrive=" + arrive + ", origin=" + origin
				+ ", destination=" + destination + ", purpose=" + purpose
				+ ", walkTime=" + walkTime + ", driveTime=" + driveTime
				+ ", transitTime=" + transitTime + ", waitTime=" + waitTime
				+ ", otherTime=" + otherTime + ", mode=" + mode + ", cost="
				+ cost + ", label=" + label + ", parkingFee=" + parkingFee
				+ "]";
	}

	public void setHhold(int hhold) {
		this.hhold = hhold;
	}

	public int getHhold() {
		return hhold;
	}

	public void setDepart(int depart) {
		this.depart = depart;
	}

	public int getDepart() {
		return depart;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getEnd() {
		return end;
	}

	public void setArrive(int arrive) {
		this.arrive = arrive;
	}

	public int getArrive() {
		return arrive;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStart() {
		return start;
	}

	public void setTrip(int trip) {
		this.trip = trip;
	}

	public int getTrip() {
		return trip;
	}

	public void setPerson(int person) {
		this.person = person;
	}

	public int getPerson() {
		return person;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public BigDecimal getParkingFee() {
		return parkingFee;
	}

	public void setParkingFee(BigDecimal parkingFee) {
		this.parkingFee = parkingFee;
	}

	public double getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(double waitTime) {
		this.waitTime = waitTime;
	}

	public int getPurpose() {
		return purpose;
	}

	public void setPurpose(int purpose) {
		this.purpose = purpose;
	}

	public double getDeltaVariableCost() {
		return deltaVariableCost;
	}

	public void setDeltaVariableCost(double deltaVariableCost) {
		this.deltaVariableCost = deltaVariableCost;
	}

	public double getDeltaFixedCost() {
		return deltaFixedCost;
	}

	public void setDeltaFixedCost(double deltaFixedCost) {
		this.deltaFixedCost = deltaFixedCost;
	}

}
