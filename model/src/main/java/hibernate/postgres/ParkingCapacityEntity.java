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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Parking Capacity Entity.
 */
@javax.persistence.Table(name = "parking_capacity", schema = "public")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ParkingCapacityEntity {
    private int parkingId;
    private Double xCoordinate;
    private Double yCoordinate;
    private Integer space;
    private String note;

    @javax.persistence.Column(name = "parking_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getParkingId() {
        return parkingId;
    }

    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
    }

    @javax.persistence.Column(name = "x_coordinate", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    @javax.persistence.Column(name = "y_coordinate", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    @javax.persistence.Column(name = "space", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getSpace() {
        return space;
    }

    public void setSpace(Integer space) {
        this.space = space;
    }

    @javax.persistence.Column(name = "note", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParkingCapacityEntity that = (ParkingCapacityEntity) o;

        if (parkingId != that.parkingId) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;
        if (space != null ? !space.equals(that.space) : that.space != null) return false;
        if (xCoordinate != null ? !xCoordinate.equals(that.xCoordinate) : that.xCoordinate != null) return false;
        if (yCoordinate != null ? !yCoordinate.equals(that.yCoordinate) : that.yCoordinate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parkingId;
        result = 31 * result + (xCoordinate != null ? xCoordinate.hashCode() : 0);
        result = 31 * result + (yCoordinate != null ? yCoordinate.hashCode() : 0);
        result = 31 * result + (space != null ? space.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }
}
