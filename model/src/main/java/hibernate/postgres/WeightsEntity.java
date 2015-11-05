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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Weights entity.
 */
@javax.persistence.Table(name = "weights", schema = "public")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WeightsEntity {
    private int category;
    private Integer gender;
    private Integer age;
    private Integer income;
    private Double home;
    private Double neighborhood;
    private Double services;
    private Double entertainment;
    private Double workAndEducation;
    private Double transport;

    @javax.persistence.Column(name = "category", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @javax.persistence.Column(name = "gender", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

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

    @javax.persistence.Column(name = "home", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getHome() {
        return home;
    }

    public void setHome(Double home) {
        this.home = home;
    }

    @javax.persistence.Column(name = "neighborhood", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Double neighborhood) {
        this.neighborhood = neighborhood;
    }

    @javax.persistence.Column(name = "services", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getServices() {
        return services;
    }

    public void setServices(Double services) {
        this.services = services;
    }

    @javax.persistence.Column(name = "entertainment", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getEntertainment() {
        return entertainment;
    }

    public void setEntertainment(Double entertainment) {
        this.entertainment = entertainment;
    }

    @javax.persistence.Column(name = "work_and_education", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getWorkAndEducation() {
        return workAndEducation;
    }

    public void setWorkAndEducation(Double workAndEducation) {
        this.workAndEducation = workAndEducation;
    }

    @javax.persistence.Column(name = "transport", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getTransport() {
        return transport;
    }

    public void setTransport(Double transport) {
        this.transport = transport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeightsEntity that = (WeightsEntity) o;

        if (category != that.category) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (entertainment != null ? !entertainment.equals(that.entertainment) : that.entertainment != null)
            return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (home != null ? !home.equals(that.home) : that.home != null) return false;
        if (income != null ? !income.equals(that.income) : that.income != null) return false;
        if (neighborhood != null ? !neighborhood.equals(that.neighborhood) : that.neighborhood != null) return false;
        if (services != null ? !services.equals(that.services) : that.services != null) return false;
        if (transport != null ? !transport.equals(that.transport) : that.transport != null) return false;
        if (workAndEducation != null ? !workAndEducation.equals(that.workAndEducation) : that.workAndEducation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category;
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (income != null ? income.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
        result = 31 * result + (neighborhood != null ? neighborhood.hashCode() : 0);
        result = 31 * result + (services != null ? services.hashCode() : 0);
        result = 31 * result + (entertainment != null ? entertainment.hashCode() : 0);
        result = 31 * result + (workAndEducation != null ? workAndEducation.hashCode() : 0);
        result = 31 * result + (transport != null ? transport.hashCode() : 0);
        return result;
    }
}
