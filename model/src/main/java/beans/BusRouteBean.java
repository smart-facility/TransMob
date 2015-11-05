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
 * a bean class contains bus related information
 * 
 * @author qun
 * 
 */
public class BusRouteBean {

	/**
	 *  name="busRouteId"
	 */
	private int busRouteId;
	/**
	 *  name="direction"
	 */
	private int direction;
	/**
	 *  name="capacity1"
	 */
	private int capacity1;
	/**
	 *  name="capacity2"
	 */
	private int capacity2;
	/**
	 *  name="speedLim"
	 */
	private int speedLim;
	/**
	 *  name="firstPoint"
	 */
	private double[] firstPoint;
	/**
	 *  name="lastPoint"
	 */
	private double[] lastPoint;

	public BusRouteBean() {

	}

	/**
	 * @return
	 *  name="firstPoint"
	 */
	public double[] getFirstPoint() {
		return this.firstPoint;
	}

	/**
	 * @param firstPoint
	 *  name="firstPoint"
	 */
	public void setFirstPoint(double[] firstPoint) {
		if (firstPoint != null) {
			double[] inputfirstPoint = firstPoint.clone();
			this.firstPoint = new double[inputfirstPoint.length];
			System.arraycopy(inputfirstPoint, 0, this.firstPoint, 0, inputfirstPoint.length);
		}
	}

	/**
	 * @return
	 *  name="lastPoint"
	 */
	public double[] getLastPoint() {
		return this.lastPoint;
	}

	/**
	 * @param lastPoint
	 *  name="lastPoint"
	 */
	public void setLastPoint(double[] lastPoint) {
		if (lastPoint != null) {
			double[] inputlastPoint = lastPoint.clone();
			this.lastPoint = new double[inputlastPoint.length];
			System.arraycopy(inputlastPoint, 0, this.lastPoint, 0, inputlastPoint.length);
		}
	}

	public BusRouteBean(int busRouteId, int direction, int capacity1,
			int capacity2, int speedLim, double[] firstPoint, double[] lastPoint) {

		this.setBusRouteId(busRouteId);
		this.setDirection(direction);
		this.setCapacity1(capacity1);
		this.setCapacity2(capacity2);
		this.setSpeedLim(speedLim);
		this.setFirstPoint(firstPoint);
		this.setLastPoint(lastPoint);
	}

	/**
	 * @return
	 *  name="busRouteId"
	 */
	public int getBusRouteId() {
		return this.busRouteId;
	}

	/**
	 * @param busRouteName
	 *  name="busRouteId"
	 */
	public void setBusRouteId(int busRouteName) {
		this.busRouteId = busRouteName;
	}

	/**
	 * @param capacity1
	 *  name="capacity1"
	 */
	public void setCapacity1(int capacity1) {
		this.capacity1 = capacity1;
	}

	/**
	 * @return
	 *  name="capacity1"
	 */
	public int getCapacity1() {
		return this.capacity1;
	}

	/**
	 * @return
	 *  name="speedLim"
	 */
	public int getSpeedLim() {
		return this.speedLim;
	}

	/**
	 * @param speedLim
	 *  name="speedLim"
	 */
	public void setSpeedLim(int speedLim) {
		this.speedLim = speedLim;
	}

	/**
	 * @param direction
	 *  name="direction"
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * @return
	 *  name="direction"
	 */
	public int getDirection() {
		return this.direction;
	}

	/**
	 * @param capacity2
	 *  name="capacity2"
	 */
	public void setCapacity2(int capacity2) {
		this.capacity2 = capacity2;
	}

	/**
	 * @return
	 *  name="capacity2"
	 */
	public int getCapacity2() {
		return this.capacity2;
	}

}
