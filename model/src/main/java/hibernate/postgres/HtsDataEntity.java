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
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * HTS Data entity.
 */
@javax.persistence.Table(name = "hts_data", schema = "public")
@Entity
public class HtsDataEntity {
    private HtsDataEntityPK key;
    private Integer hf;
    private Integer attendSchool;
    private Integer adultPriorityCat;
    private Integer persNumTrips;
    private Integer depart;
    private Integer tripTime;
    private Integer tmode;
    private Integer purpose11;

    @EmbeddedId
    public HtsDataEntityPK getKey() {
        return key;
    }

    public void setKey(HtsDataEntityPK key) {
        this.key = key;
    }

    @javax.persistence.Column(name = "hf", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf() {
        return hf;
    }

    public void setHf(Integer hf) {
        this.hf = hf;
    }

    @javax.persistence.Column(name = "attend_school", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getAttendSchool() {
        return attendSchool;
    }

    public void setAttendSchool(Integer attendSchool) {
        this.attendSchool = attendSchool;
    }

    @javax.persistence.Column(name = "adult_priority_cat", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getAdultPriorityCat() {
        return adultPriorityCat;
    }

    public void setAdultPriorityCat(Integer adultPriorityCat) {
        this.adultPriorityCat = adultPriorityCat;
    }

    @javax.persistence.Column(name = "pers_num_trips", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getPersNumTrips() {
        return persNumTrips;
    }

    public void setPersNumTrips(Integer persNumTrips) {
        this.persNumTrips = persNumTrips;
    }

    @javax.persistence.Column(name = "depart", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getDepart() {
        return depart;
    }

    public void setDepart(Integer depart) {
        this.depart = depart;
    }

    @javax.persistence.Column(name = "trip_time", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTripTime() {
        return tripTime;
    }

    public void setTripTime(Integer tripTime) {
        this.tripTime = tripTime;
    }

    @javax.persistence.Column(name = "tmode", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTmode() {
        return tmode;
    }

    public void setTmode(Integer tmode) {
        this.tmode = tmode;
    }

    @javax.persistence.Column(name = "purpose11", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getPurpose11() {
        return purpose11;
    }

    public void setPurpose11(Integer purpose11) {
        this.purpose11 = purpose11;
    }

    @Override
    public boolean equals(Object o) {        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HtsDataEntity that = (HtsDataEntity) o;

        if (adultPriorityCat != null ? !adultPriorityCat.equals(that.adultPriorityCat) : that.adultPriorityCat != null)
            return false;
        if (attendSchool != null ? !attendSchool.equals(that.attendSchool) : that.attendSchool != null) return false;
        if (depart != null ? !depart.equals(that.depart) : that.depart != null) return false;
        if (hf != null ? !hf.equals(that.hf) : that.hf != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (persNumTrips != null ? !persNumTrips.equals(that.persNumTrips) : that.persNumTrips != null) return false;
        if (purpose11 != null ? !purpose11.equals(that.purpose11) : that.purpose11 != null) return false;
        if (tmode != null ? !tmode.equals(that.tmode) : that.tmode != null) return false;
        if (tripTime != null ? !tripTime.equals(that.tripTime) : that.tripTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (hf != null ? hf.hashCode() : 0);
        result = 31 * result + (attendSchool != null ? attendSchool.hashCode() : 0);
        result = 31 * result + (adultPriorityCat != null ? adultPriorityCat.hashCode() : 0);
        result = 31 * result + (persNumTrips != null ? persNumTrips.hashCode() : 0);
        result = 31 * result + (depart != null ? depart.hashCode() : 0);
        result = 31 * result + (tripTime != null ? tripTime.hashCode() : 0);
        result = 31 * result + (tmode != null ? tmode.hashCode() : 0);
        result = 31 * result + (purpose11 != null ? purpose11.hashCode() : 0);
        return result;
    }

    @Embeddable
    public static class HtsDataEntityPK implements Serializable {
        private Integer householdId;
        private Integer personNo;
        private Integer tripNo;

        @javax.persistence.Column(name = "household_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
        @Basic
        public Integer getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Integer householdId) {
            this.householdId = householdId;
        }

        @javax.persistence.Column(name = "person_no", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
        @Basic
        public Integer getPersonNo() {
            return personNo;
        }

        public void setPersonNo(Integer personNo) {
            this.personNo = personNo;
        }

        @javax.persistence.Column(name = "trip_no", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
        @Basic
        public Integer getTripNo() {
            return tripNo;
        }

        public void setTripNo(Integer tripNo) {
            this.tripNo = tripNo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HtsDataEntityPK that = (HtsDataEntityPK) o;

            if (!householdId.equals(that.householdId)) return false;
            if (!personNo.equals(that.personNo)) return false;
            if (!tripNo.equals(that.tripNo)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = householdId.hashCode();
            result = 31 * result + personNo.hashCode();
            result = 31 * result + tripNo.hashCode();
            return result;
        }
    }

}
