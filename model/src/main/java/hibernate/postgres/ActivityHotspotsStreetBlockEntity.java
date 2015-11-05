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


import javax.persistence.*;
import java.io.Serializable;

/**
 * Entity object for activity_hotspots_street_block table.
 */
@Table(name = "activity_hotspots_street_block", schema = "public")
@Entity
public class ActivityHotspotsStreetBlockEntity implements Serializable {
    private int activityId;

    @Column(name = "activity_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    private Integer hotspotStreetBlockId;

    @Column(name = "hotspot_street_block_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHotspotStreetBlockId() {
        return hotspotStreetBlockId;
    }

    public void setHotspotStreetBlockId(Integer hotspotStreetBlockId) {
        this.hotspotStreetBlockId = hotspotStreetBlockId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityHotspotsStreetBlockEntity that = (ActivityHotspotsStreetBlockEntity) o;

        if (activityId != that.activityId) return false;
        if (hotspotStreetBlockId != null ? !hotspotStreetBlockId.equals(that.hotspotStreetBlockId) : that.hotspotStreetBlockId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = activityId;
        result = 31 * result + (hotspotStreetBlockId != null ? hotspotStreetBlockId.hashCode() : 0);
        return result;
    }
}
