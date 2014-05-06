package beans;

/**
 * a bean class contains statistics
 * 
 * @author qun
 * 
 */
public class StatsResultsBean {

	/**
	 *  name="meanTravelTime"
	 */
	private double meanTravelTime;
	/**
	 *  name="meanWaitTime"
	 */
	private double meanWaitTime;
	/**
	 *  name="varTravelTime"
	 */
	private double varTravelTime;
	/**
	 *  name="varWaitTime"
	 */
	private double varWaitTime;
	/**
	 *  name="plotWaitTime"
	 */
	private String plotWaitTime;
	/**
	 *  name="plotTravelTime"
	 */
	private String plotTravelTime;

	public StatsResultsBean() {

	}

	public StatsResultsBean(double meanTravelTime, double meanWaitTime,
			double varTravelTime, double varWaitTime, String plotTravelTime,
			String plotWaitTime) {

		this.meanTravelTime = meanTravelTime;
		this.meanWaitTime = meanWaitTime;
		this.varTravelTime = varTravelTime;
		this.varWaitTime = varWaitTime;
		this.plotTravelTime = plotTravelTime;
		this.plotWaitTime = plotWaitTime;

	}

	/**
	 * @return
	 *  name="meanTravelTime"
	 */
	public double getMeanTravelTime() {
		return this.meanTravelTime;
	}

	/**
	 * @param meanTravelTime
	 *  name="meanTravelTime"
	 */
	public void setMeanTravelTime(double meanTravelTime) {
		this.meanTravelTime = meanTravelTime;
	}

	/**
	 * @return
	 *  name="meanWaitTime"
	 */
	public double getMeanWaitTime() {
		return this.meanWaitTime;
	}

	/**
	 * @param meanWaitTime
	 *  name="meanWaitTime"
	 */
	public void setMeanWaitTime(double meanWaitTime) {

		this.meanWaitTime = meanWaitTime;
	}

	/**
	 * @return
	 *  name="varTravelTime"
	 */
	public double getVarTravelTime() {
		return this.varTravelTime;
	}

	/**
	 * @param varTravelTime
	 *  name="varTravelTime"
	 */
	public void setVarTravelTime(double varTravelTime) {
		this.varTravelTime = varTravelTime;
	}

	/**
	 * @return
	 *  name="varWaitTime"
	 */
	public double getVarWaitTime() {
		return this.varWaitTime;
	}

	/**
	 * @param varWaitTime
	 *  name="varWaitTime"
	 */
	public void setVarWaitTime(double varWaitTime) {
		this.varWaitTime = varWaitTime;
	}

	/**
	 * @param plotWaitTime
	 *  name="plotWaitTime"
	 */
	public void setPlotWaitTime(String plotWaitTime) {
		this.plotWaitTime = plotWaitTime;
	}

	/**
	 * @return
	 *  name="plotWaitTime"
	 */
	public String getPlotWaitTime() {
		return plotWaitTime;
	}

	/**
	 * @param plotTravelTime
	 *  name="plotTravelTime"
	 */
	public void setPlotTravelTime(String plotTravelTime) {
		this.plotTravelTime = plotTravelTime;
	}

	/**
	 * @return
	 *  name="plotTravelTime"
	 */
	public String getPlotTravelTime() {
		return plotTravelTime;
	}

}
