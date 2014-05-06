package core.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * a link file for storing id, density, actual time and speed.
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 */
public class Link {

	/**
	 * name="link"
	 */
	private int link;
	/**
	 * name="density"
	 */
	private List<Double> density;
	/**
	 * name="speed"
	 */
	private List<Double> speed;
	/**
	 * name="actualTime"
	 */
	private List<Integer> actualTime;

	public Link(int link) {
		this.link = link;
		density = new ArrayList<>();
		speed = new ArrayList<>();
		actualTime = new ArrayList<>();
	}

	public void setDensity(List<Double> density) {
		this.density = density;
	}

	public void setSpeed(List<Double> speed) {
		this.speed = speed;
	}

	public void setActualTime(List<Integer> actualTime) {
		this.actualTime = actualTime;
	}

	public List<Integer> getActualTime() {
		return actualTime;
	}

	/**
	 * @return name="link"
	 */
	public int getLink() {
		return link;
	}

	public List<Double> getDensity() {
		return density;
	}

	public List<Double> getSpeed() {
		return speed;
	}

	/**
	 * @param link
	 *            name="link"
	 */
	public void setLink(int link) {
		this.link = link;
	}

	public double getLinkAverageDensity() {
		if (!density.isEmpty()) {
			double sum = 0;
			for (Double d : density) {
				sum += d;
			}

			return sum / density.size();
		} else {
			return 0;
		}

	}

	public double getLinkAverageSpeed() {
		if (!speed.isEmpty()) {
			double sum = 0;
			for (Double d : speed) {
				sum += d;
			}

			return sum / speed.size();
		} else {
			return 0;
		}
	}

	public double getLinkAverageActualTime() {
		if (!actualTime.isEmpty()) {
			double sum = 0;
			for (int i : actualTime) {
				sum += i;
			}

			return sum / actualTime.size();
		} else {
			return 0;
		}
	}

}
