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
package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * JTW2006 entity.
 */
@javax.persistence.Table(name = "jtw2006_originoutside_destinationinsidestudyarea", schema = "public")
@Entity
public class JTW2006Entity {
	private int ID;
	private Integer O_Tz06;
	private Integer O_Sla06;
	private Integer O_Lga06;
	private Integer O_Ssd06;
	private Integer O_Sd06;
	private Integer O_State;
	private Integer D_Tz06;
	private Integer D_Sla06;
	private Integer D_Lga06;
	private Integer D_Ssd06;
	private Integer D_Sd06;
	private Integer D_Area;
	private String O_Zone06_abs;
	private String Dzn06_abs;
	private Integer Mode9;
	private Integer Freq;
	private String Source_not_in_tz;
	private String Destination_in_tz;
	private String Note;
	
	@javax.persistence.Column(name = "ID", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
	@javax.persistence.Column(name = "O_Tz06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getO_Tz06() {
		return O_Tz06;
	}
	public void setO_Tz06(Integer o_Tz06) {
		O_Tz06 = o_Tz06;
	}
	
	@javax.persistence.Column(name = "O_Sla06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getO_Sla06() {
		return O_Sla06;
	}
	public void setO_Sla06(Integer o_Sla06) {
		O_Sla06 = o_Sla06;
	}
	
	@javax.persistence.Column(name = "O_Lga06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getO_Lga06() {
		return O_Lga06;
	}
	public void setO_Lga06(Integer o_Lga06) {
		O_Lga06 = o_Lga06;
	}
	
	@javax.persistence.Column(name = "O_Ssd06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getO_Ssd06() {
		return O_Ssd06;
	}
	public void setO_Ssd06(Integer o_Ssd06) {
		O_Ssd06 = o_Ssd06;
	}
	
	@javax.persistence.Column(name = "O_Sd06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getO_Sd06() {
		return O_Sd06;
	}
	public void setO_Sd06(Integer o_Sd06) {
		O_Sd06 = o_Sd06;
	}
	
	@javax.persistence.Column(name = "O_State", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getO_State() {
		return O_State;
	}
	public void setO_State(Integer o_State) {
		O_State = o_State;
	}
	
	@javax.persistence.Column(name = "D_Tz06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getD_Tz06() {
		return D_Tz06;
	}
	public void setD_Tz06(Integer d_Tz06) {
		D_Tz06 = d_Tz06;
	}
	
	@javax.persistence.Column(name = "D_Sla06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getD_Sla06() {
		return D_Sla06;
	}
	public void setD_Sla06(Integer d_Sla06) {
		D_Sla06 = d_Sla06;
	}
	
	@javax.persistence.Column(name = "D_Lga06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getD_Lga06() {
		return D_Lga06;
	}
	public void setD_Lga06(Integer d_Lga06) {
		D_Lga06 = d_Lga06;
	}
	
	@javax.persistence.Column(name = "D_Ssd06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getD_Ssd06() {
		return D_Ssd06;
	}
	public void setD_Ssd06(Integer d_Ssd06) {
		D_Ssd06 = d_Ssd06;
	}
	
	@javax.persistence.Column(name = "D_Sd06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getD_Sd06() {
		return D_Sd06;
	}
	public void setD_Sd06(Integer d_Sd06) {
		D_Sd06 = d_Sd06;
	}
	
	@javax.persistence.Column(name = "D_Area", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getD_Area() {
		return D_Area;
	}
	public void setD_Area(Integer d_Area) {
		D_Area = d_Area;
	}
	
	@javax.persistence.Column(name = "O_Zone06_abs", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
	public String getO_Zone06_abs() {
		return O_Zone06_abs;
	}
	public void setO_Zone06_abs(String o_Zone06_abs) {
		O_Zone06_abs = o_Zone06_abs;
	}
	
	@javax.persistence.Column(name = "Dzn06_abs", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
	public String getDzn06_abs() {
		return Dzn06_abs;
	}
	public void setDzn06_abs(String dzn06_abs) {
		Dzn06_abs = dzn06_abs;
	}
	
	@javax.persistence.Column(name = "Mode9", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getMode9() {
		return Mode9;
	}
	public void setMode9(Integer mode9) {
		Mode9 = mode9;
	}
	
	@javax.persistence.Column(name = "Freq", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
	public Integer getFreq() {
		return Freq;
	}
	public void setFreq(Integer freq) {
		Freq = freq;
	}
	
	@javax.persistence.Column(name = "Source_not_in_tz", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
	public String getSource_not_in_tz() {
		return Source_not_in_tz;
	}
	public void setSource_not_in_tz(String source_not_in_tz) {
		Source_not_in_tz = source_not_in_tz;
	}
	
	@javax.persistence.Column(name = "Destination_in_tz", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
	public String getDestination_in_tz() {
		return Destination_in_tz;
	}
	public void setDestination_in_tz(String destination_in_tz) {
		Destination_in_tz = destination_in_tz;
	}
	
	@javax.persistence.Column(name = "Note", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JTW2006Entity that = (JTW2006Entity) o;

        if (ID != that.ID) return false;
        if (O_Tz06 != null ? !O_Tz06.equals(that.O_Tz06) : that.O_Tz06 != null)
            return false;
        if (O_Sla06 != null ? !O_Sla06.equals(that.O_Sla06) : that.O_Sla06 != null) return false;
        if (O_Lga06 != null ? !O_Lga06.equals(that.O_Lga06) : that.O_Lga06 != null) return false;
        if (O_Ssd06 != null ? !O_Ssd06.equals(that.O_Ssd06) : that.O_Ssd06 != null) return false;
        if (O_Sd06 != null ? !O_Sd06.equals(that.O_Sd06) : that.O_Sd06 != null) return false;
        if (O_State != null ? !O_State.equals(that.O_State) : that.O_State != null) return false;
        if (D_Tz06 != null ? !D_Tz06.equals(that.D_Tz06) : that.D_Tz06 != null) return false;
        if (D_Sla06 != null ? !D_Sla06.equals(that.D_Sla06) : that.D_Sla06 != null) return false;
        if (D_Lga06 != null ? !D_Lga06.equals(that.D_Lga06) : that.D_Lga06 != null) return false;
        if (D_Ssd06 != null ? !D_Ssd06.equals(that.D_Ssd06) : that.D_Ssd06 != null) return false;
        if (D_Sd06 != null ? !D_Sd06.equals(that.D_Sd06) : that.D_Sd06 != null) return false;
        if (D_Area != null ? !D_Area.equals(that.D_Area) : that.D_Area != null) return false;
        if (O_Zone06_abs != null ? !O_Zone06_abs.equals(that.O_Zone06_abs) : that.O_Zone06_abs != null) return false;
        if (Dzn06_abs != null ? !Dzn06_abs.equals(that.Dzn06_abs) : that.Dzn06_abs != null) return false;
        if (Mode9 != null ? !Mode9.equals(that.Mode9) : that.Mode9 != null) return false;
        if (Freq != null ? !Freq.equals(that.Freq) : that.Freq != null) return false;
        if (Source_not_in_tz != null ? !Source_not_in_tz.equals(that.Source_not_in_tz) : that.Source_not_in_tz != null) return false;
        if (Destination_in_tz != null ? !Destination_in_tz.equals(that.Destination_in_tz) : that.Destination_in_tz != null) return false;
        if (Note != null ? !Note.equals(that.Note) : that.Note != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ID;
        result = 31 * result + (O_Tz06 != null ? O_Tz06.hashCode() : 0);
        result = 31 * result + (O_Sla06 != null ? O_Sla06.hashCode() : 0);
        result = 31 * result + (O_Lga06 != null ? O_Lga06.hashCode() : 0);
        result = 31 * result + (O_Ssd06 != null ? O_Ssd06.hashCode() : 0);
        result = 31 * result + (O_Sd06 != null ? O_Sd06.hashCode() : 0);
        result = 31 * result + (O_State != null ? O_State.hashCode() : 0);
        result = 31 * result + (D_Tz06 != null ? D_Tz06.hashCode() : 0);
        result = 31 * result + (D_Sla06 != null ? D_Sla06.hashCode() : 0);
        result = 31 * result + (D_Lga06 != null ? D_Lga06.hashCode() : 0);
        result = 31 * result + (D_Ssd06 != null ? D_Ssd06.hashCode() : 0);
        result = 31 * result + (D_Sd06 != null ? D_Sd06.hashCode() : 0);
        result = 31 * result + (D_Area != null ? D_Area.hashCode() : 0);
        result = 31 * result + (O_Zone06_abs != null ? O_Zone06_abs.hashCode() : 0);
        result = 31 * result + (Dzn06_abs != null ? Dzn06_abs.hashCode() : 0);
        result = 31 * result + (Mode9 != null ? Mode9.hashCode() : 0);
        result = 31 * result + (Freq != null ? Freq.hashCode() : 0);
        result = 31 * result + (Source_not_in_tz != null ? Source_not_in_tz.hashCode() : 0);
        result = 31 * result + (Destination_in_tz != null ? Destination_in_tz.hashCode() : 0);
        result = 31 * result + (Note != null ? Note.hashCode() : 0);
        
        return result;
    }
}
