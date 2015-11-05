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
package core;

/**
 * Holds configuration relating to the environment the application is running in.
 */
public class EnvironmentConfig {

    /** The base path where any required data files can be found from. */
    private String dataPath;

    /** The base path where any required config files can be found from. */
    private String configPath;

    /**
	 * @return the configPath
	 */
	public String getConfigPath() {
		return configPath;
	}

	/**
	 * @param configPath the configPath to set
	 */
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
}
