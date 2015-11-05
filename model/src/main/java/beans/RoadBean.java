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
 * a bean class contains road related information
 * 
 * @author qun
 * 
 */
public class RoadBean {

	/**
	 *  name="roadId"
	 */
	private int roadId;
	/**
	 *  name="roadName"
	 */
	private String roadName;
	/**
	 *  name="roadType"
	 */
	private String roadType;
	/**
	 *  name="speed"
	 */
	private int speed;
	/**
	 *  name="points"
	 */
	private double[][] points;

	// private String coords;
	// private MultiLineString multiLineString;
	//
	// public void setMultiLineString(MultiLineString multiLineString) {
	// this.multiLineString = multiLineString;
	// }
	//
	// public MultiLineString getMultiLineString() {
	// return multiLineString;
	// }

	public RoadBean() {

	}

	public RoadBean(int roadId, String roadName, String roadType, int speed,
			double[][] points) {
		this.setRoadId(roadId);
		this.setRoadName(roadName);
		this.setRoadType(roadType);
		this.setSpeed(speed);
		this.setPoints(points);
	}

	// public RoadBean(int roadId, String roadName, String roadType, int
	// speed,String coords) {
	// this.setRoadId(roadId);
	// this.setRoadName(roadName);
	// this.setRoadType(roadType);
	// this.setSpeed(speed);
	// this.setCoords(coords);
	// }

	// public RoadBean(int roadId, MultiLineString multiLineString) {
	// this.setRoadId(roadId);
	// this.setMultiLineString(multiLineString);
	// }
	//
	// public String getCoords() {
	// return coords;
	// }
	//
	// public void setCoords(String coords) {
	// this.coords = coords;
	// }

	/**
	 * @param roadId
	 *  name="roadId"
	 */
	public void setRoadId(int roadId) {
		this.roadId = roadId;
	}

	/**
	 * @return
	 *  name="points"
	 */
	public double[][] getPoints() {
		return points;
	}

	/**
	 * @param points
	 *  name="points"
	 */
	public void setPoints(double[][] points) {
		if (points != null) {
			double[][] inputPoints = points.clone();
			this.points = new double[inputPoints.length][inputPoints[0].length];
			System.arraycopy(inputPoints, 0, this.points, 0, inputPoints.length);
		}
	}

	/**
	 * @param speed
	 *  name="speed"
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * @return
	 *  name="speed"
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @return
	 *  name="roadId"
	 */
	public int getRoadId() {
		return roadId;
	}

	/**
	 * @param roadName
	 *  name="roadName"
	 */
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	/**
	 * @return
	 *  name="roadName"
	 */
	public String getRoadName() {
		return roadName;
	}

	/**
	 * @param roadType
	 *  name="roadType"
	 */
	public void setRoadType(String roadType) {
		this.roadType = roadType;
	}

	/**
	 * @return
	 *  name="roadType"
	 */
	public String getRoadType() {
		return roadType;
	}

}
