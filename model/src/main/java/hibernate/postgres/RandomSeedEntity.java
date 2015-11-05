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
