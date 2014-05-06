package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Transims Household Locations entity.
 */
@Table(name = "transims_household_location", schema = "public")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TransimsHouseholdLocationEntity {
    private int dwellingIndex;

    @javax.persistence.Column(name = "dwelling_index", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getDwellingIndex() {
        return dwellingIndex;
    }

    public void setDwellingIndex(int dwellingIndex) {
        this.dwellingIndex = dwellingIndex;
    }

    private Integer activityLocation;

    @javax.persistence.Column(name = "activity_location", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(Integer activityLocation) {
        this.activityLocation = activityLocation;
    }

    private Integer cdCode;

    @javax.persistence.Column(name = "cd_code", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getCdCode() {
        return cdCode;
    }

    public void setCdCode(Integer cdCode) {
        this.cdCode = cdCode;
    }

    private Double xCoord;

    @javax.persistence.Column(name = "x_coord", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getxCoord() {
        return xCoord;
    }

    public void setxCoord(Double xCoord) {
        this.xCoord = xCoord;
    }

    private Double yCoord;

    @javax.persistence.Column(name = "y_coord", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getyCoord() {
        return yCoord;
    }

    public void setyCoord(Double yCoord) {
        this.yCoord = yCoord;
    }

    private Integer blockId;

    @javax.persistence.Column(name = "block_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    private Integer noteBus;

    @javax.persistence.Column(name = "note_bus", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getNoteBus() {
        return noteBus;
    }

    public void setNoteBus(Integer noteBus) {
        this.noteBus = noteBus;
    }

    private Integer noteTrain;

    @javax.persistence.Column(name = "note_train", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getNoteTrain() {
        return noteTrain;
    }

    public void setNoteTrain(Integer noteTrain) {
        this.noteTrain = noteTrain;
    }

    private Integer hholdIndex;

    @javax.persistence.Column(name = "hhold_index", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHholdIndex() {
        return hholdIndex;
    }

    public void setHholdIndex(Integer hholdIndex) {
        this.hholdIndex = hholdIndex;
    }

    private Integer travelZoneId;

    @javax.persistence.Column(name = "travel_zone_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTravelZoneId() {
        return travelZoneId;
    }

    public void setTravelZoneId(Integer travelZoneId) {
        this.travelZoneId = travelZoneId;
    }

    private Integer yearAvailable;

    @javax.persistence.Column(name = "year_available", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getYearAvailable() {
        return yearAvailable;
    }

    public void setYearAvailable(Integer yearAvailable) {
        this.yearAvailable = yearAvailable;
    }

    private Integer bedroomsNumber;

    @javax.persistence.Column(name = "bedrooms_number", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getBedroomsNumber() {
        return bedroomsNumber;
    }

    public void setBedroomsNumber(Integer bedroomsNumber) {
        this.bedroomsNumber = bedroomsNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransimsHouseholdLocationEntity that = (TransimsHouseholdLocationEntity) o;

        if (dwellingIndex != that.dwellingIndex) return false;
        if (activityLocation != null ? !activityLocation.equals(that.activityLocation) : that.activityLocation != null)
            return false;
        if (bedroomsNumber != null ? !bedroomsNumber.equals(that.bedroomsNumber) : that.bedroomsNumber != null)
            return false;
        if (blockId != null ? !blockId.equals(that.blockId) : that.blockId != null) return false;
        if (cdCode != null ? !cdCode.equals(that.cdCode) : that.cdCode != null) return false;
        if (hholdIndex != null ? !hholdIndex.equals(that.hholdIndex) : that.hholdIndex != null) return false;
        if (noteBus != null ? !noteBus.equals(that.noteBus) : that.noteBus != null) return false;
        if (noteTrain != null ? !noteTrain.equals(that.noteTrain) : that.noteTrain != null) return false;
        if (travelZoneId != null ? !travelZoneId.equals(that.travelZoneId) : that.travelZoneId != null) return false;
        if (xCoord != null ? !xCoord.equals(that.xCoord) : that.xCoord != null) return false;
        if (yCoord != null ? !yCoord.equals(that.yCoord) : that.yCoord != null) return false;
        if (yearAvailable != null ? !yearAvailable.equals(that.yearAvailable) : that.yearAvailable != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dwellingIndex;
        result = 31 * result + (activityLocation != null ? activityLocation.hashCode() : 0);
        result = 31 * result + (cdCode != null ? cdCode.hashCode() : 0);
        result = 31 * result + (xCoord != null ? xCoord.hashCode() : 0);
        result = 31 * result + (yCoord != null ? yCoord.hashCode() : 0);
        result = 31 * result + (blockId != null ? blockId.hashCode() : 0);
        result = 31 * result + (noteBus != null ? noteBus.hashCode() : 0);
        result = 31 * result + (noteTrain != null ? noteTrain.hashCode() : 0);
        result = 31 * result + (hholdIndex != null ? hholdIndex.hashCode() : 0);
        result = 31 * result + (travelZoneId != null ? travelZoneId.hashCode() : 0);
        result = 31 * result + (yearAvailable != null ? yearAvailable.hashCode() : 0);
        result = 31 * result + (bedroomsNumber != null ? bedroomsNumber.hashCode() : 0);
        return result;
    }
}
