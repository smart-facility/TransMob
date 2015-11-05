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
package core.synthetic.attribute;

import core.HardcodedData;

/**
 * The transport mode to work distribution in the individual pool
 * 
 * @author qun
 */
public enum TransportModeToWork {
	/**
	 * name="train"
	 * 
	 * @uml.associationEnd
	 */
	Train, /**
	 * name="bus"
	 * 
	 * @uml.associationEnd
	 */
	Bus, /**
	 * name="ferry"
	 * 
	 * @uml.associationEnd
	 */
	Ferry, /**
	 * name="tramIncludesLightRail"
	 * 
	 * @uml.associationEnd
	 */
	TramIncludesLightRail, /**
	 * name="taxi"
	 * 
	 * @uml.associationEnd
	 */
	Taxi, /**
	 * name="carAsDriver"
	 * 
	 * @uml.associationEnd
	 */
	CarAsDriver, /**
	 * name="carAsPassenger"
	 * 
	 * @uml.associationEnd
	 */
	CarAsPassenger, /**
	 * name="truck"
	 * 
	 * @uml.associationEnd
	 */
	Truck, /**
	 * name="motorbikeOrScooter"
	 * 
	 * @uml.associationEnd
	 */
	MotorbikeOrScooter,
	/**
	 * name="bicycle"
	 * 
	 * @uml.associationEnd
	 */
	Bicycle, /**
	 * name="other"
	 * 
	 * @uml.associationEnd
	 */
	Other, /**
	 * name="walkedOnly"
	 * 
	 * @uml.associationEnd
	 */
	WalkedOnly;

	public static TransportModeToWork random() {
		// SecureRandom random = GlobalRandom.getInstance();
		return TransportModeToWork.values()[HardcodedData.random
				.nextInt(TransportModeToWork.values().length)];
	}
}
