package core;

import java.io.File;
import java.util.Iterator;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.context.Context;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;
import core.synthetic.individual.Individual;

public class ModelRunner extends AbstractRunner {

	private static MessageCenter msgCenter = MessageCenter
			.getMessageCenter(ModelRunner.class);

	/**
	 *  name="runEnvironmentBuilder"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private final RunEnvironmentBuilder runEnvironmentBuilder;
	/**
	 *  name="controller"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	//protected Controller controller;
	/**
	 *  name="pause"
	 */
	//protected boolean pause = false;
	/**
	 *  name="monitor"
	 */
	protected Object monitor = new Object();
	/**
	 *  name="producer"
	 * @uml.associationEnd readOnly="true"
	 */
	protected SweeperProducer producer;
	/**
	 *  name="schedule"
	 * @uml.associationEnd
	 */
	private ISchedule schedule;
	/**
	 *  name="context"
	 * @uml.associationEnd
	 */
	private Context<Object> context;

	public ModelRunner() {
		this.runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this,
				true);
		this.controller = new DefaultController(this.runEnvironmentBuilder);
		this.controller.setScheduleRunner(this);
	}

	public void load(File scenarioDir) throws Exception {
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader
					.load(this.runEnvironmentBuilder);
			this.controller.setControllerRegistry(registry);
		} else {
			msgCenter
					.error("Scenario not found", new IllegalArgumentException("Invalid scenario " + scenarioDir.getAbsolutePath()));
			return;
		}

		this.controller.batchInitialize();
		this.controller.runParameterSetters(null);
	}

	@SuppressWarnings("unchecked")
	public void runInitialize() {
		this.controller.runInitialize(null);
		RunState rs = RunState.getInstance();
		this.schedule = rs.getScheduleRegistry().getModelSchedule(); // get the
		// schedule
		this.context = rs.getMasterContext(); // get the main context of the
												// model
	}

	public void cleanUpRun() {
		this.controller.runCleanup();
	}

	public void cleanUpBatch() {
		this.controller.batchCleanup();
	}

	// returns the tick count of the next scheduled item
	public double getNextScheduledTime() {
		return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule())
				.peekNextAction().getNextTime();
	}

	// returns the number of model actions on the schedule
	public int getModelActionCount() {
		return this.schedule.getModelActionCount();
	}

	// returns the number of non-model actions on the schedule
	public int getActionCount() {
		return this.schedule.getActionCount();
	}

	// Step the schedule
	@Override
	public void step() {
		this.schedule.execute();
	}

	// stop the schedule
	@Override
	public void stop() {
		if (this.schedule != null)
			this.schedule.executeEndActions();
	}

	public void setFinishing(boolean fin) {
		this.schedule.setFinishing(fin);
	}

	@Override
	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub. We will control the
		// schedule directly.
	}

	public Iterator<Object> getAgentIterator() { // get the collection of the
													// agents
		return context.getObjects(Individual.class).iterator();
	}

	public Context<Object> getContext() {
		return this.context;
	}

	public void setContext(Context<Object> context) {
		this.context = context;
	}
}