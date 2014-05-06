package beans;

/**
 * a bean class contains user scenario related information
 * 
 * @author qun
 * 
 */
public class UserScenarioBean {

	/**
	 *  name="scenarioName"
	 */
	private String scenarioName;
	/**
	 *  name="runId"
	 */
	private int runId;

	public UserScenarioBean() {

	}

	public UserScenarioBean(String scenarioName) {

		this.setScenarioName(scenarioName);
	}

	public UserScenarioBean(String scenarioName, int runId) {
		this.setRunId(runId);
		this.setScenarioName(scenarioName);
	}

	/**
	 * @param scenarioName
	 *  name="scenarioName"
	 */
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	/**
	 * @return
	 *  name="scenarioName"
	 */
	public String getScenarioName() {
		return scenarioName;
	}

	/**
	 * @param runId
	 *  name="runId"
	 */
	public void setRunId(int runId) {
		this.runId = runId;
	}

	/**
	 * @return
	 *  name="runId"
	 */
	public int getRunId() {
		return runId;
	}
}
