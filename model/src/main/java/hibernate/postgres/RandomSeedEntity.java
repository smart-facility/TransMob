package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Random seed entity.
 */
@javax.persistence.Table(name = "random_seed", schema = "public")
@Entity
public class RandomSeedEntity {
    private int id;
    private Integer seed;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "seed", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RandomSeedEntity that = (RandomSeedEntity) o;

        if (id != that.id) return false;
        if (seed != null ? !seed.equals(that.seed) : that.seed != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (seed != null ? seed.hashCode() : 0);
        return result;
    }
}
