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
package core.synthetic.traveldiary;

public enum TravelDiaryColumns {
	    TravelID_Col(0),
		IndividualID_Col(1),
		HouseholdID_Col(2),
		Age_Col(3),
		Gender_Col(4),
		Income_Col(5),
		Origin_Col(6),
		Destination_Col(7),
		StartTime_Col(8),
		EndTime_Col(9),
		Duration_Col(10),
		TravelMode_Col(11),
		Purpose_Col(12),
		VehicleID_Col(13),
		TripID_Col(14),
		;
	    
	    private final int value;
	    
	    /**
	     * Private constructor.
	     * @param value
	     */
	    private TravelDiaryColumns(int value) {
	        this.value = value;
	    }
	    
	    public int getIntValue() {
	        return value;
	    }

}
