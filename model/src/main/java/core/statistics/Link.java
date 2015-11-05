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
