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
package hibernate.postgres.lifeEvents;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Birth Probabilities.
 */
@javax.persistence.Table(name = "birth_probability", schema = "life_event")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BirthProbabilityEntity {
    private int age;

    @javax.persistence.Column(name = "age", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private Double firstChild;

    @javax.persistence.Column(name = "first_child", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(Double firstChild) {
        this.firstChild = firstChild;
    }

    private Double secondChild;

    @javax.persistence.Column(name = "second_child", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getSecondChild() {
        return secondChild;
    }

    public void setSecondChild(Double secondChild) {
        this.secondChild = secondChild;
    }

    private Double thirdChild;

    @javax.persistence.Column(name = "third_child", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getThirdChild() {
        return thirdChild;
    }

    public void setThirdChild(Double thirdChild) {
        this.thirdChild = thirdChild;
    }

    private Double fourthChild;

    @javax.persistence.Column(name = "fourth_child", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getFourthChild() {
        return fourthChild;
    }

    public void setFourthChild(Double fourthChild) {
        this.fourthChild = fourthChild;
    }

    private Double sixOrMore;

    @javax.persistence.Column(name = "six_or_more", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getSixOrMore() {
        return sixOrMore;
    }

    public void setSixOrMore(Double sixOrMore) {
        this.sixOrMore = sixOrMore;
    }

    private Double fifthChild;

    @javax.persistence.Column(name = "fifth_child", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getFifthChild() {
        return fifthChild;
    }

    public void setFifthChild(Double fifthChild) {
        this.fifthChild = fifthChild;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BirthProbabilityEntity that = (BirthProbabilityEntity) o;

        if (age != that.age) return false;
        if (fifthChild != null ? !fifthChild.equals(that.fifthChild) : that.fifthChild != null) return false;
        if (firstChild != null ? !firstChild.equals(that.firstChild) : that.firstChild != null) return false;
        if (fourthChild != null ? !fourthChild.equals(that.fourthChild) : that.fourthChild != null) return false;
        if (secondChild != null ? !secondChild.equals(that.secondChild) : that.secondChild != null) return false;
        if (sixOrMore != null ? !sixOrMore.equals(that.sixOrMore) : that.sixOrMore != null) return false;
        if (thirdChild != null ? !thirdChild.equals(that.thirdChild) : that.thirdChild != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = age;
        result = 31 * result + (firstChild != null ? firstChild.hashCode() : 0);
        result = 31 * result + (secondChild != null ? secondChild.hashCode() : 0);
        result = 31 * result + (thirdChild != null ? thirdChild.hashCode() : 0);
        result = 31 * result + (fourthChild != null ? fourthChild.hashCode() : 0);
        result = 31 * result + (sixOrMore != null ? sixOrMore.hashCode() : 0);
        result = 31 * result + (fifthChild != null ? fifthChild.hashCode() : 0);
        return result;
    }
}
