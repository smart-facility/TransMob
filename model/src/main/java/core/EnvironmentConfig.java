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
