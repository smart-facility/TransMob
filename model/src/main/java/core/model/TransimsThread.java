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
package core.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import core.HardcodedData;

public class TransimsThread extends Thread {

    private static final Logger logger = Logger.getLogger(TransimsThread.class);

	/**
	 *  name="shell"
	 */
	private String shell;
	/**
	 *  name="batchPath"
	 */
	private String batchPath;
	/**
	 *  name="execFilename"
	 */
	private String execFilename;
	/**
	 *  name="extension"
	 */
	private String extension;
	/**
	 *  name="demandPath"
	 */
	private String demandPath;
	/**
	 *  name="resultsPath"
	 */
	private String resultsPath;
	/**
	 *  name="fFinished"
	 */
	private final String fFinished;

	public TransimsThread(String dirLocation, String scenarioName) {

		final String m = "m";
		final String n = "n";
		final String o = "o";
		final String base = "base";

		String osName = System.getProperty("os.name");
		this.shell = "cmd /c start ";
		HardcodedData.setWinTransimsPath(dirLocation);
		this.batchPath = HardcodedData.getWinTransimsPath() + "batch/";
		this.setDemandPath(HardcodedData.getWinTransimsPath() + "demand/");
		this.setResultsPath(HardcodedData.getWinTransimsPath() + "results/");
		
		if (scenarioName.equalsIgnoreCase(base)) {
			this.execFilename = "Rand.RouteMsim";
		} else if (scenarioName.equalsIgnoreCase(m)) {
			this.execFilename = "Rand.RouteMsim.Option.M";
		} else if (scenarioName.equalsIgnoreCase(n)) {
			this.execFilename = "Rand.RouteMsim.Option.N";
		} else if (scenarioName.equalsIgnoreCase(o)) {
			this.execFilename = "Rand.RouteMsim.Option.O";
		} else {
			try {
				throw new Exception("wrong scenario name");
			} catch (Exception e) {
				logger.error("Exception caught", e);
			}
		}

		this.extension = ".bat";
		
		
		if (!osName.startsWith("Windows")) {
			this.shell = "/bin/bash ";
			HardcodedData.setUnixTransimsPath(dirLocation);
			this.batchPath = HardcodedData.getUnixTransimsPath() + "batch/";
			this.setDemandPath(HardcodedData.getUnixTransimsPath() + "demand/");
			this.setResultsPath(HardcodedData.getUnixTransimsPath()
					+ "results/");


			if (scenarioName.equalsIgnoreCase(base)) {
				this.execFilename = "Rand.RouteMsim";
			} else if (scenarioName.equalsIgnoreCase(m)) {
				this.execFilename = "Rand.RouteMsim.Option.M";
			} else if (scenarioName.equalsIgnoreCase(n)) {
				this.execFilename = "Rand.RouteMsim.Option.N";
			} else if (scenarioName.equalsIgnoreCase(o)) {
				this.execFilename = "Rand.RouteMsim.Option.O";
			} else {
				try {
					throw new Exception("wrong scenario name");
				} catch (Exception e) {
					logger.error("Exception caught", e);
				}
			}

			this.extension = ".sh";
		}
		this.fFinished = this.batchPath + "finished.txt";

	}

	@Override
	public void run() {

		File fFin = new File(this.fFinished);
		if (fFin.exists())
			fFin.delete();

        BufferedReader stdOut = null;

		// call transims
		try {
			Runtime rt = Runtime.getRuntime();
			String command = this.shell + this.batchPath + this.execFilename
					+ this.extension;

			logger.debug(command);

			Process pr = rt.exec(command, null, new File(this.batchPath));
			// pr.waitFor();
            stdOut = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));
			String line;

            if (logger.isDebugEnabled()) {
                while ((line = stdOut.readLine()) != null) {
                    logger.debug(line);
                }
            }
			// logger.debug(pr.exitValue());
		} catch (Exception error) {
            logger.error("Exception caught", error);
		} finally {
            if (stdOut != null) {
                try {
                    stdOut.close();
                } catch (IOException e) {
                    logger.error("Failed to close output stream from process.", e);
                }
            }
        }
	}

	public boolean finished() {
		File fFin = new File(this.fFinished);
		if (fFin.exists()) {

			// logger.info("End Transims at " + (new
			// Date(System.currentTimeMillis())).toString());

			return true;
		} else
			return false;
	}

	public void deleteFile() {
		File fFin = new File(this.fFinished);
		fFin.delete();

	}

	public String getFFinished() {
		return this.fFinished;
	}

	/**
	 * @param demandPath
	 *  name="demandPath"
	 */
	public void setDemandPath(String demandPath) {
		this.demandPath = demandPath;
	}

	/**
	 * @return
	 *  name="demandPath"
	 */
	public String getDemandPath() {
		return demandPath;
	}

	/**
	 * @param resultsPath
	 *  name="resultsPath"
	 */
	public void setResultsPath(String resultsPath) {
		this.resultsPath = resultsPath;
	}

	/**
	 * @return
	 *  name="resultsPath"
	 */
	public String getResultsPath() {
		return resultsPath;
	}

	public void setStep(int stepNumber, String scenarioName){
		final String m = "m";
		final String n = "n";
		final String o = "o";
		final String base = "base";
		
		switch (stepNumber) {
		case 1:
			if (scenarioName.equalsIgnoreCase(base)) {
				this.execFilename = "Rand.RouteMsim";
			} else if (scenarioName.equalsIgnoreCase(m)) {
				this.execFilename = "Rand.RouteMsim.Option.M";
			} else if (scenarioName.equalsIgnoreCase(n)) {
				this.execFilename = "Rand.RouteMsim.Option.N";
			} else if (scenarioName.equalsIgnoreCase(o)) {
				this.execFilename = "Rand.RouteMsim.Option.O";
			} else {
				try {
					throw new Exception("wrong scenario name");
				} catch (Exception e) {
                    logger.error("Exception caught", e);
				}
			}
			break;

		case 2:
			if (scenarioName.equalsIgnoreCase(base)) {
				this.execFilename = "Rand.RouteMsim_Msim";
			} else if (scenarioName.equalsIgnoreCase(m)) {
				this.execFilename = "Rand.RouteMsim.Option.M_Msim";
			} else if (scenarioName.equalsIgnoreCase(n)) {
				this.execFilename = "Rand.RouteMsim.Option.N_Msim";
			} else if (scenarioName.equalsIgnoreCase(o)) {
				this.execFilename = "Rand.RouteMsim.Option.O_Msim";
			} else {
				try {
					throw new Exception("wrong scenario name");
				} catch (Exception e) {
                    logger.error("Exception caught", e);
				}
			}
			break;
		}
	}
}
