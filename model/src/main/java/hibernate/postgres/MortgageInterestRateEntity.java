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
 * Mortgage Interest Rates entity.
 */
@javax.persistence.Table(name = "mortgage_interest_rate", schema = "public")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MortgageInterestRateEntity {
    private int year;
    private Double rate;

    @javax.persistence.Column(name = "year", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @javax.persistence.Column(name = "rate", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MortgageInterestRateEntity that = (MortgageInterestRateEntity) o;

        if (year != that.year) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }
}
