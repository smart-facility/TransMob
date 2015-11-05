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
package jdbc.dao;

public class DwellingCapacity {

	private int tz06;
	private int oneBedroom;
	private int twoBedroom;
	private int threeBedroom;
	private int fourBedroom;

	public DwellingCapacity(int tz06, int oneBedroom, int twoBedroom, int threeBedroom, int fourBedroom) {
		super();
		this.tz06 = tz06;
		this.oneBedroom = oneBedroom;
		this.twoBedroom = twoBedroom;
		this.threeBedroom = threeBedroom;
		this.fourBedroom = fourBedroom;
	}

	/**
	 * @return the tz06
	 */
	public int getTz06() {
		return tz06;
	}

	/**
	 * @param tz06
	 *            the tz06 to set
	 */
	public void setTz06(int tz06) {
		this.tz06 = tz06;
	}

	/**
	 * @return the oneBedroom
	 */
	public int getOneBedroom() {
		return oneBedroom;
	}

	/**
	 * @param oneBedroom
	 *            the oneBedroom to set
	 */
	public void setOneBedroom(int oneBedroom) {
		this.oneBedroom = oneBedroom;
	}

	/**
	 * @return the twoBedroom
	 */
	public int getTwoBedroom() {
		return twoBedroom;
	}

	/**
	 * @param twoBedroom
	 *            the twoBedroom to set
	 */
	public void setTwoBedroom(int twoBedroom) {
		this.twoBedroom = twoBedroom;
	}

	/**
	 * @return the threeBedroom
	 */
	public int getThreeBedroom() {
		return threeBedroom;
	}

	/**
	 * @param threeBedroom
	 *            the threeBedroom to set
	 */
	public void setThreeBedoom(int threeBedroom) {
		this.threeBedroom = threeBedroom;
	}

	/**
	 * @return the fourBedroom
	 */
	public int getFourBedroom() {
		return fourBedroom;
	}

	/**
	 * @param fourBedroom
	 *            the fourBedroom to set
	 */
	public void setFourBedroom(int fourBedroom) {
		this.fourBedroom = fourBedroom;
	}

}
