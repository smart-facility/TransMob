package hibernate.postgres.lifeEvents;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Female divorce probabilities.
 */
@javax.persistence.Table(name = "divorce_probability_female", schema = "life_event")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DivorceProbabilityFemaleEntity {
    private int age;

    @javax.persistence.Column(name = "age", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private Double probability;

    @javax.persistence.Column(name = "probability", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DivorceProbabilityFemaleEntity that = (DivorceProbabilityFemaleEntity) o;

        if (age != that.age) return false;
        if (probability != null ? !probability.equals(that.probability) : that.probability != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = age;
        result = 31 * result + (probability != null ? probability.hashCode() : 0);
        return result;
    }
}
