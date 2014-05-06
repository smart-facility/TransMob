package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Synthetic population entity.
 */
@javax.persistence.Table(name = "synthetic_population", schema = "public")
@Entity
public class SyntheticPopulationEntity {
    private Integer age;
    private Integer income;
    private Integer diaryWeekdaysStart;
    private Integer diaryWeekdaysEnd;
    private Integer diaryWeekendStart;
    private Integer diaryWeekendEnd;
    private String householdRelationship;
    private String householdType;
    private Integer gender;
    private Double orgSatisfaction;
    private Integer travelZone;
    private Integer householdId;
    private String cd;
    private int id;
    private Integer tenure;

    @javax.persistence.Column(name = "age", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @javax.persistence.Column(name = "income", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    @javax.persistence.Column(name = "diary_weekdays_start", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getDiaryWeekdaysStart() {
        return diaryWeekdaysStart;
    }

    public void setDiaryWeekdaysStart(Integer diaryWeekdaysStart) {
        this.diaryWeekdaysStart = diaryWeekdaysStart;
    }

    @javax.persistence.Column(name = "diary_weekdays_end", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getDiaryWeekdaysEnd() {
        return diaryWeekdaysEnd;
    }

    public void setDiaryWeekdaysEnd(Integer diaryWeekdaysEnd) {
        this.diaryWeekdaysEnd = diaryWeekdaysEnd;
    }

    @javax.persistence.Column(name = "diary_weekend_start", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getDiaryWeekendStart() {
        return diaryWeekendStart;
    }

    public void setDiaryWeekendStart(Integer diaryWeekendStart) {
        this.diaryWeekendStart = diaryWeekendStart;
    }

    @javax.persistence.Column(name = "diary_weekend_end", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getDiaryWeekendEnd() {
        return diaryWeekendEnd;
    }

    public void setDiaryWeekendEnd(Integer diaryWeekendEnd) {
        this.diaryWeekendEnd = diaryWeekendEnd;
    }

    @javax.persistence.Column(name = "household_relationship", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getHouseholdRelationship() {
        return householdRelationship;
    }

    public void setHouseholdRelationship(String householdRelationship) {
        this.householdRelationship = householdRelationship;
    }

    @javax.persistence.Column(name = "household_type", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getHouseholdType() {
        return householdType;
    }

    public void setHouseholdType(String householdType) {
        this.householdType = householdType;
    }

    @javax.persistence.Column(name = "gender", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @javax.persistence.Column(name = "org_satisfaction", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getOrgSatisfaction() {
        return orgSatisfaction;
    }

    public void setOrgSatisfaction(Double orgSatisfaction) {
        this.orgSatisfaction = orgSatisfaction;
    }

    @javax.persistence.Column(name = "travel_zone", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTravelZone() {
        return travelZone;
    }

    public void setTravelZone(Integer travelZone) {
        this.travelZone = travelZone;
    }

    @javax.persistence.Column(name = "household_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Integer householdId) {
        this.householdId = householdId;
    }

    @javax.persistence.Column(name = "cd", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
 //   @SequenceGenerator(name="seq-gen", sequenceName="synthetic_population_id_seq")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "tenure", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }
    
    

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SyntheticPopulationEntity [age=" + age + ", income=" + income
				+ ", householdRelationship=" + householdRelationship
				+ ", householdType=" + householdType + ", gender=" + gender
				+ ", travelZone=" + travelZone + ", householdId=" + householdId
				+ ", id=" + id + ", tenure=" + tenure + "]";
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyntheticPopulationEntity that = (SyntheticPopulationEntity) o;

        if (id != that.id) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (cd != null ? !cd.equals(that.cd) : that.cd != null) return false;
        if (diaryWeekdaysEnd != null ? !diaryWeekdaysEnd.equals(that.diaryWeekdaysEnd) : that.diaryWeekdaysEnd != null)
            return false;
        if (diaryWeekdaysStart != null ? !diaryWeekdaysStart.equals(that.diaryWeekdaysStart) : that.diaryWeekdaysStart != null)
            return false;
        if (diaryWeekendEnd != null ? !diaryWeekendEnd.equals(that.diaryWeekendEnd) : that.diaryWeekendEnd != null)
            return false;
        if (diaryWeekendStart != null ? !diaryWeekendStart.equals(that.diaryWeekendStart) : that.diaryWeekendStart != null)
            return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (householdId != null ? !householdId.equals(that.householdId) : that.householdId != null) return false;
        if (householdRelationship != null ? !householdRelationship.equals(that.householdRelationship) : that.householdRelationship != null)
            return false;
        if (householdType != null ? !householdType.equals(that.householdType) : that.householdType != null)
            return false;
        if (income != null ? !income.equals(that.income) : that.income != null) return false;
        if (orgSatisfaction != null ? !orgSatisfaction.equals(that.orgSatisfaction) : that.orgSatisfaction != null)
            return false;
        if (tenure != null ? !tenure.equals(that.tenure) : that.tenure != null) return false;
        if (travelZone != null ? !travelZone.equals(that.travelZone) : that.travelZone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = age != null ? age.hashCode() : 0;
        result = 31 * result + (income != null ? income.hashCode() : 0);
        result = 31 * result + (diaryWeekdaysStart != null ? diaryWeekdaysStart.hashCode() : 0);
        result = 31 * result + (diaryWeekdaysEnd != null ? diaryWeekdaysEnd.hashCode() : 0);
        result = 31 * result + (diaryWeekendStart != null ? diaryWeekendStart.hashCode() : 0);
        result = 31 * result + (diaryWeekendEnd != null ? diaryWeekendEnd.hashCode() : 0);
        result = 31 * result + (householdRelationship != null ? householdRelationship.hashCode() : 0);
        result = 31 * result + (householdType != null ? householdType.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (orgSatisfaction != null ? orgSatisfaction.hashCode() : 0);
        result = 31 * result + (travelZone != null ? travelZone.hashCode() : 0);
        result = 31 * result + (householdId != null ? householdId.hashCode() : 0);
        result = 31 * result + (cd != null ? cd.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (tenure != null ? tenure.hashCode() : 0);
        return result;
    }
}
