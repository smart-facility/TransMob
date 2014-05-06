package hibernate.postgres;

import javax.persistence.*;

/**
 * Duty Rate entity.
 */
@Table(name = "duty_rate", schema = "public")
@Entity
public class DutyRateEntity {
    private int dutiableValue1;

    @javax.persistence.Column(name = "dutiable_value_1", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getDutiableValue1() {
        return dutiableValue1;
    }

    public void setDutiableValue1(int dutiableValue1) {
        this.dutiableValue1 = dutiableValue1;
    }

    private Integer dutiableValue2;

    @Column(name = "dutiable_value_2", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getDutiableValue2() {
        return dutiableValue2;
    }

    public void setDutiableValue2(Integer dutiableValue2) {
        this.dutiableValue2 = dutiableValue2;
    }

    private Double dutyRate;

    @Column(name = "duty_rate", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getDutyRate() {
        return dutyRate;
    }

    public void setDutyRate(Double dutyRate) {
        this.dutyRate = dutyRate;
    }

    private Integer baseValue;

    @Column(name = "base_value", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Integer baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DutyRateEntity that = (DutyRateEntity) o;

        if (dutiableValue1 != that.dutiableValue1) return false;
        if (baseValue != null ? !baseValue.equals(that.baseValue) : that.baseValue != null) return false;
        if (dutiableValue2 != null ? !dutiableValue2.equals(that.dutiableValue2) : that.dutiableValue2 != null)
            return false;
        if (dutyRate != null ? !dutyRate.equals(that.dutyRate) : that.dutyRate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dutiableValue1;
        result = 31 * result + (dutiableValue2 != null ? dutiableValue2.hashCode() : 0);
        result = 31 * result + (dutyRate != null ? dutyRate.hashCode() : 0);
        result = 31 * result + (baseValue != null ? baseValue.hashCode() : 0);
        return result;
    }
}
