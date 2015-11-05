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
package core.synthetic.household;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a vehicle.
 */
public class Vehicle {

    private Integer vehicleId;

    private Integer hHold;

    private Integer location;

    private Integer type;

    private Integer subType;

    public Integer getVehicleId() {
        return vehicleId;
    }

    public Integer gethHold() {
        return hHold;
    }

    public Integer getLocation() {
        return location;
    }

    public Integer getType() {
        return type;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void sethHold(Integer hHold) {
        this.hHold = hHold;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    /**
     * Return a string array representation of the object ready for writing to a TRANSIMS VEHICLES file.
     * @return
     */
    public String[] toTransimsFormat() {
        List<String> vehicleAsList = Arrays.asList(vehicleId.toString(), hHold.toString(), location.toString(),
                type.toString(), subType.toString());
        return vehicleAsList.toArray(new String[5]);
    }
}
