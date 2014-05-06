package core.synthetic;

import java.math.BigDecimal;

import core.synthetic.attribute.Category;
import hibernate.postgis.TravelZonesFacilitiesEntity;
import hibernate.postgres.WeightsEntity;

public class LiveabilityTestCase {
	private int id;
	private WeightsEntity weights;
	private TravelZonesFacilitiesEntity travelZonesFacilities;
	private int age;
	private BigDecimal incomeBigDecimal;
	private Category category;
	private double travelZoneCongestion;
	private double expectation;
	private int population;

	public LiveabilityTestCase(int id, WeightsEntity weights,
                               TravelZonesFacilitiesEntity travelZonesFacilities, int age,
			BigDecimal incomeBigDecimal, Category category,
			double travelZoneCongestion, double expectation, int population) {
		super();
		this.id = id;
		this.weights = weights;
		this.travelZonesFacilities = travelZonesFacilities;
		this.age = age;
		this.incomeBigDecimal = incomeBigDecimal;
		this.category = category;
		this.travelZoneCongestion = travelZoneCongestion;
		this.expectation = expectation;
		this.population = population;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TravelZonesFacilitiesEntity getTravelZonesFacilities() {
		return travelZonesFacilities;
	}

	public void setTravelZonesFacilities(
            TravelZonesFacilitiesEntity travelZonesFacilities) {
		this.travelZonesFacilities = travelZonesFacilities;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public BigDecimal getIncomeBigDecimal() {
		return incomeBigDecimal;
	}

	public void setIncomeBigDecimal(BigDecimal incomeBigDecimal) {
		this.incomeBigDecimal = incomeBigDecimal;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public double getTravelZoneCongestion() {
		return travelZoneCongestion;
	}

	public void setTravelZoneCongestion(double travelZoneCongestion) {
		this.travelZoneCongestion = travelZoneCongestion;
	}

	public double getExpectation() {
		return expectation;
	}

	public void setExpectation(double expectation) {
		this.expectation = expectation;
	}

	@Override
	public String toString() {
		return "LiveabilityTestCase [id=" + id + ", weights=" + weights
				+ ", travelZonesFacilities=" + travelZonesFacilities + ", age="
				+ age + ", incomeBigDecimal=" + incomeBigDecimal
				+ ", category=" + category + ", travelZoneCongestion="
				+ travelZoneCongestion + ", expectation=" + expectation + "]";
	}

}
