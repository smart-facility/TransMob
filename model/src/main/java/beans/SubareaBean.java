package beans;

/**
 * a bean class contains sub-area related information
 * 
 * @author qun
 * 
 */
public class SubareaBean {

	/**
	 *  name="subareaName"
	 */
	private String subareaName;
	/**
	 *  name="area"
	 */
	private double area;
	/**
	 *  name="population"
	 */
	private int population;
	/**
	 *  name="household"
	 */
	private int household;
	/**
	 *  name="popDensity"
	 */
	private int popDensity;
	/**
	 *  name="houDensity"
	 */
	private int houDensity;
	/**
	 *  name="satisfaction"
	 */
	private double satisfaction;

	public SubareaBean() {

	}

	public SubareaBean(String subareaName, double area, int population,
			double satisfaction) {
		this.setSubareaName(subareaName);
		this.setArea(area);
		this.setPopulation(population);
		this.setSatisfaction(satisfaction);
	}

	/**
	 * @param subareaName
	 *  name="subareaName"
	 */
	public void setSubareaName(String subareaName) {
		this.subareaName = subareaName;
	}

	/**
	 * @return
	 *  name="subareaName"
	 */
	public String getSubareaName() {
		return subareaName;
	}

	/**
	 * @param area
	 *  name="area"
	 */
	public void setArea(double area) {
		this.area = area;
	}

	/**
	 * @return
	 *  name="area"
	 */
	public double getArea() {
		return area;
	}

	/**
	 * @param population
	 *  name="population"
	 */
	public void setPopulation(int population) {
		this.population = population;
	}

	/**
	 * @return
	 *  name="population"
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * @param household
	 *  name="household"
	 */
	public void setHousehold(int household) {
		this.household = household;
	}

	/**
	 * @return
	 *  name="household"
	 */
	public int getHousehold() {
		return household;
	}

	/**
	 * @param popDensity
	 *  name="popDensity"
	 */
	public void setPopDensity(int popDensity) {
		this.popDensity = popDensity;
	}

	/**
	 * @return
	 *  name="popDensity"
	 */
	public int getPopDensity() {
		return popDensity;
	}

	/**
	 * @param houDensity
	 *  name="houDensity"
	 */
	public void setHouDensity(int houDensity) {
		this.houDensity = houDensity;
	}

	/**
	 * @return
	 *  name="houDensity"
	 */
	public int getHouDensity() {
		return houDensity;
	}

	/**
	 * @param satisfaction
	 *  name="satisfaction"
	 */
	public void setSatisfaction(double satisfaction) {
		this.satisfaction = satisfaction;
	}

	/**
	 * @return
	 *  name="satisfaction"
	 */
	public double getSatisfaction() {
		return satisfaction;
	}

}
