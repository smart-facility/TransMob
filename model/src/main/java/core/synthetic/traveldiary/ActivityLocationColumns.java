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

public enum ActivityLocationColumns {
	FacilityId_Col(0),
	ActivityID_Col(1),
	Type_Col(2),
	HospotId_Col(3),
	NoteBus_Col(4),
	NoteTrain_Col(5),
	TravelZone_Col(6),
	NoteEntry_Col(7),
	;
    
    private final int value;
    
    /**
     * Private constructor.
     * @param value
     */
    private ActivityLocationColumns(int value) {
        this.value = value;
    }
    
    public int getIntValue() {
        return value;
    }

}
