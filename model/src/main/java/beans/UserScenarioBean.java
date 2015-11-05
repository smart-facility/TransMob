/* This file is part of TransMob.

   TransMob is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   TransMob is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser Public License for more details.

   You should have received a copy of the GNU Lesser Public License
   along with TransMob.  If not, see <http://www.gnu.org/licenses/>.

*/
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
