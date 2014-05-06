package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Intermediate Synthetic Population entity.
 */
@javax.persistence.Table(name = "intermediate_synthetic_population", schema = "public")
@Entity
public class IntermediateSyntheticPopulationEntity {
    private int id;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Integer age;

    @javax.persistence.Column(name = "age", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private String householdRelationship;

    @javax.persistence.Column(name = "household_relationship", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getHouseholdRelationship() {
        return householdRelationship;
    }

    public void setHouseholdRelationship(String householdRelationship) {
        this.householdRelationship = householdRelationship;
    }

    private String householdType;

    @javax.persistence.Column(name = "household_type", nullable = true, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    public String getHouseholdType() {
        return householdType;
    }

    public void setHouseholdType(String householdType) {
        this.householdType = householdType;
    }

    private Integer gender;

    @javax.persistence.Column(name = "gender", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    private Integer travelZone;

    @javax.persistence.Column(name = "travel_zone", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTravelZone() {
        return travelZone;
    }

    public void setTravelZone(Integer travelZone) {
        this.travelZone = travelZone;
    }

    private String cd;

    @javax.persistence.Column(name = "cd", nullable = true, insertable = true, updatable = true, length = 15, precision = 0)
    @Basic
    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    private Integer income;

    @javax.persistence.Column(name = "income", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }    private Integer householdId;

    @javax.persistence.Column(name = "household_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Integer householdId) {
        this.householdId = householdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntermediateSyntheticPopulationEntity that = (IntermediateSyntheticPopulationEntity) o;

        if (id != that.id) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (cd != null ? !cd.equals(that.cd) : that.cd != null) return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (householdId != null ? !householdId.equals(that.householdId) : that.householdId != null) return false;
        if (householdRelationship != null ? !householdRelationship.equals(that.householdRelationship) : that.householdRelationship != null)
            return false;
        if (householdType != null ? !householdType.equals(that.householdType) : that.householdType != null)
            return false;
        if (income != null ? !income.equals(that.income) : that.income != null) return false;
        if (travelZone != null ? !travelZone.equals(that.travelZone) : that.travelZone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (householdRelationship != null ? householdRelationship.hashCode() : 0);
        result = 31 * result + (householdType != null ? householdType.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (travelZone != null ? travelZone.hashCode() : 0);
        result = 31 * result + (cd != null ? cd.hashCode() : 0);
        result = 31 * result + (income != null ? income.hashCode() : 0);
        result = 31 * result + (householdId != null ? householdId.hashCode() : 0);
        return result;
    }
}
