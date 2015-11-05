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
 * Transims Activity Location entity.
 */
@javax.persistence.Table(name = "transims_activity_location", schema = "public")
@Entity
public class TransimsActivityLocationEntity {
    private int facilityId;
    private Integer activityLocation;
    private String name;
    private String suburb;
    private Double xCoord;
    private Double yCoord;
    private String type;
    private Integer hotspotId;
    private Integer noteBus;
    private Integer noteTrain;
    private Integer travelZone;
    private Integer noteEntry;

    @javax.persistence.Column(name = "facility_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    @javax.persistence.Column(name = "activity_location", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(Integer activityLocation) {
        this.activityLocation = activityLocation;
    }

    @javax.persistence.Column(name = "name", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @javax.persistence.Column(name = "suburb", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    @javax.persistence.Column(name = "x_coord", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getxCoord() {
        return xCoord;
    }

    public void setxCoord(Double xCoord) {
        this.xCoord = xCoord;
    }

    @javax.persistence.Column(name = "y_coord", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getyCoord() {
        return yCoord;
    }

    public void setyCoord(Double yCoord) {
        this.yCoord = yCoord;
    }

    @javax.persistence.Column(name = "type", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getType() {
        String locationType = type.toLowerCase().trim();
        locationType = locationType.replace(" ", "_");
        locationType = locationType.replace("-", "_");
        locationType = locationType.replace("/", "_");
        locationType = locationType.replace("___", "_");
        return locationType;
    }

    public void setType(String type) {
        this.type = type;
    }

    @javax.persistence.Column(name = "hotspot_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(Integer hotspotId) {
        this.hotspotId = hotspotId;
    }

    @javax.persistence.Column(name = "note_bus", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getNoteBus() {
        return noteBus;
    }

    public void setNoteBus(Integer noteBus) {
        this.noteBus = noteBus;
    }

    @javax.persistence.Column(name = "note_train", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getNoteTrain() {
        return noteTrain;
    }

    public void setNoteTrain(Integer noteTrain) {
        this.noteTrain = noteTrain;
    }

    @javax.persistence.Column(name = "travel_zone", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTravelZone() {
		return travelZone;
	}

	public void setTravelZone(Integer travelZone) {
		this.travelZone = travelZone;
	}

	@javax.persistence.Column(name = "note_entry", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
	@Basic
	public Integer getNoteEntry() {
		return noteEntry;
	}

	public void setNoteEntry(Integer noteEntry) {
		this.noteEntry = noteEntry;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransimsActivityLocationEntity that = (TransimsActivityLocationEntity) o;

        if (facilityId != that.facilityId) return false;
        if (activityLocation != null ? !activityLocation.equals(that.activityLocation) : that.activityLocation != null)
            return false;
        if (hotspotId != null ? !hotspotId.equals(that.hotspotId) : that.hotspotId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (noteBus != null ? !noteBus.equals(that.noteBus) : that.noteBus != null) return false;
        if (noteTrain != null ? !noteTrain.equals(that.noteTrain) : that.noteTrain != null) return false;
        if (suburb != null ? !suburb.equals(that.suburb) : that.suburb != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (xCoord != null ? !xCoord.equals(that.xCoord) : that.xCoord != null) return false;
        if (yCoord != null ? !yCoord.equals(that.yCoord) : that.yCoord != null) return false;
        if (travelZone != null ? !travelZone.equals(that.travelZone) : that.travelZone != null) return false;
        if (noteEntry != null ? !noteEntry.equals(that.noteEntry) : that.noteEntry != null) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int result = facilityId;
        result = 31 * result + (activityLocation != null ? activityLocation.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (suburb != null ? suburb.hashCode() : 0);
        result = 31 * result + (xCoord != null ? xCoord.hashCode() : 0);
        result = 31 * result + (yCoord != null ? yCoord.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (hotspotId != null ? hotspotId.hashCode() : 0);
        result = 31 * result + (noteBus != null ? noteBus.hashCode() : 0);
        result = 31 * result + (noteTrain != null ? noteTrain.hashCode() : 0);
        result = 31 * result + (travelZone != null ? travelZone.hashCode() : 0);
        result = 31 * result + (noteEntry != null ? noteEntry.hashCode() : 0);
        return result;
    }
}
