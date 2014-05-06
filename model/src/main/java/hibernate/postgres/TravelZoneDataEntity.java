package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Travel Zone Data Entity.
 */
@javax.persistence.Table(name = "travel_zone_data", schema = "public")
@Entity
public class TravelZoneDataEntity {
    private int gid;

    @javax.persistence.Column(name = "gid", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    private int areaCode;

    @javax.persistence.Column(name = "area_code", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public int getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    private BigDecimal tzSkm;

    @javax.persistence.Column(name = "tz_skm", nullable = true, insertable = true, updatable = true, length = 131089, precision = 0)
    @Basic
    public BigDecimal getTzSkm() {
        return tzSkm;
    }

    public void setTzSkm(BigDecimal tzSkm) {
        this.tzSkm = tzSkm;
    }

    private BigDecimal satisfaction;

    @javax.persistence.Column(name = "satisfaction", nullable = true, insertable = true, updatable = true, length = 131089, precision = 0)
    @Basic
    public BigDecimal getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(BigDecimal satisfaction) {
        this.satisfaction = satisfaction;
    }

    private BigDecimal orgSatisfaction;

    @javax.persistence.Column(name = "org_satisfaction", nullable = true, insertable = true, updatable = true, length = 131089, precision = 0)
    @Basic
    public BigDecimal getOrgSatisfaction() {
        return orgSatisfaction;
    }

    public void setOrgSatisfaction(BigDecimal orgSatisfaction) {
        this.orgSatisfaction = orgSatisfaction;
    }

    private Double housingAvailability;

    @javax.persistence.Column(name = "housing_availability", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getHousingAvailability() {
        return housingAvailability;
    }

    public void setHousingAvailability(Double housingAvailability) {
        this.housingAvailability = housingAvailability;
    }

    private Double travelTimeToWork;

    @javax.persistence.Column(name = "travel_time_to_work", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getTravelTimeToWork() {
        return travelTimeToWork;
    }

    public void setTravelTimeToWork(Double travelTimeToWork) {
        this.travelTimeToWork = travelTimeToWork;
    }

    private Double beachProximity;

    @javax.persistence.Column(name = "beach_proximity", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getBeachProximity() {
        return beachProximity;
    }

    public void setBeachProximity(Double beachProximity) {
        this.beachProximity = beachProximity;
    }

    private Double weekdaysCongestion;

    @javax.persistence.Column(name = "weekdays_congestion", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getWeekdaysCongestion() {
        return weekdaysCongestion;
    }

    public void setWeekdaysCongestion(Double weekdaysCongestion) {
        this.weekdaysCongestion = weekdaysCongestion;
    }

    private Double weekendCongestion;

    @javax.persistence.Column(name = "weekend_congestion", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getWeekendCongestion() {
        return weekendCongestion;
    }

    public void setWeekendCongestion(Double weekendCongestion) {
        this.weekendCongestion = weekendCongestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TravelZoneDataEntity that = (TravelZoneDataEntity) o;

        if (areaCode != that.areaCode) return false;
        if (gid != that.gid) return false;
        if (beachProximity != null ? !beachProximity.equals(that.beachProximity) : that.beachProximity != null)
            return false;
        if (housingAvailability != null ? !housingAvailability.equals(that.housingAvailability) : that.housingAvailability != null)
            return false;
        if (orgSatisfaction != null ? !orgSatisfaction.equals(that.orgSatisfaction) : that.orgSatisfaction != null)
            return false;
        if (satisfaction != null ? !satisfaction.equals(that.satisfaction) : that.satisfaction != null) return false;
        if (travelTimeToWork != null ? !travelTimeToWork.equals(that.travelTimeToWork) : that.travelTimeToWork != null)
            return false;
        if (tzSkm != null ? !tzSkm.equals(that.tzSkm) : that.tzSkm != null) return false;
        if (weekdaysCongestion != null ? !weekdaysCongestion.equals(that.weekdaysCongestion) : that.weekdaysCongestion != null)
            return false;
        if (weekendCongestion != null ? !weekendCongestion.equals(that.weekendCongestion) : that.weekendCongestion != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gid;
        result = 31 * result + areaCode;
        result = 31 * result + (tzSkm != null ? tzSkm.hashCode() : 0);
        result = 31 * result + (satisfaction != null ? satisfaction.hashCode() : 0);
        result = 31 * result + (orgSatisfaction != null ? orgSatisfaction.hashCode() : 0);
        result = 31 * result + (housingAvailability != null ? housingAvailability.hashCode() : 0);
        result = 31 * result + (travelTimeToWork != null ? travelTimeToWork.hashCode() : 0);
        result = 31 * result + (beachProximity != null ? beachProximity.hashCode() : 0);
        result = 31 * result + (weekdaysCongestion != null ? weekdaysCongestion.hashCode() : 0);
        result = 31 * result + (weekendCongestion != null ? weekendCongestion.hashCode() : 0);
        return result;
    }
}
