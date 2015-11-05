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

import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;
import core.synthetic.travel.mode.TravelModeSelection;

public class ContextCreator implements ContextBuilder<Object> {
	private static final Logger logger = Logger.getLogger(ContextCreator.class);

	private static Context<Object> mainContext;
	private static List<Individual> population;
	private static boolean runTransims;

	@Override
	public Context<Object> build(Context<Object> context) {

		ModelMain main = ModelMain.getInstance();

		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params = ScheduleParameters.createRepeating(0, 0);

		// *------------ Set up context here --------------*//
		setMainContext(context);
		setRunTransims(true);

		TravelModeSelection travelModeSelection = TravelModeSelection
				.getInstance();

		for (Household household:main.getHouseholdPool().getHouseholds().values()) {
        	for (Individual indiv : household.getResidents()) {
        		// trigger individual to run the method "step" following the
    			// schedule
    			schedule.schedule(params, indiv, "step", travelModeSelection);

    			// trigger household to run the method "evolution" following the
    			// schedule

        	}
        }
		
		logger.debug("Finish schedule for agents");

		return context;
	}

	// --------------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param mainContext
	 */
	public static void setMainContext(Context<Object> mainContext) {
		ContextCreator.mainContext = mainContext;
	}

	/**
	 * 
	 * @return
	 */
	public static Context<Object> getMainContext() {
		return mainContext;
	}

	/**
	 * 
	 * @param population
	 */
	public static void setPopulation(List<Individual> population) {
		ContextCreator.population = population;
	}

	/**
	 * 
	 * @return
	 */
	public static List<Individual> getPopulation() {
		return population;
	}

	/**
	 * 
	 * @param runTransimsCondition
	 */
	public static void setRunTransims(boolean runTransimsCondition) {
		ContextCreator.runTransims = runTransimsCondition;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isRunTransims() {
		return runTransims;
	}
}
