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
package hibernate.postgis;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the travel_zones_facilities database table.
 * 
 */
@Table(name="travel_zones_facilities", schema = "public")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TravelZonesFacilitiesEntity {
	private Integer tz2006;
	private Double entertainment1km;
	private Double entertainment2km;
	private Double entertainment500m;
	private Double neighbourhood1km;
	private Double neighbourhood2km;
	private Double neighbourhood500m;
	private Double population1km;
	private Double population2km;
	private Double population500m;
	private BigDecimal prices;
	private Double rentalPrices1Bedroom;
	private Double rentalPrices2Bedroom;
	private Double rentalPrices3Bedroom;
	private Double rentalPrices4Bedroom;
	private Double salePrices1Bedroom;
	private Double salePrices2Bedroom;
	private Double salePrices3Bedroom;
	private Double salePrices4Bedroom;
	private Double services1km;
	private Double services2km;
	private Double services500m;
	private Double transport1km;
	private Double transport2km;
	private Double transport500m;
	private Double workspacesEducation1km;
	private Double workspacesEducation2km;
	private Double workspacesEducation500m;

	public TravelZonesFacilitiesEntity() {
	}


	@Id
	@Column(name="tz_2006", unique=true, nullable=false, insertable = true, updatable = true, length = 10, precision = 0)
	public Integer getTz2006() {
		return this.tz2006;
	}

	public void setTz2006(Integer tz2006) {
		this.tz2006 = tz2006;
	}


	@Column(name="entertainment_1km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getEntertainment1km() {
		return this.entertainment1km;
	}

	public void setEntertainment1km(Double entertainment1km) {
		this.entertainment1km = entertainment1km;
	}


	@Column(name="entertainment_2km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getEntertainment2km() {
		return this.entertainment2km;
	}

	public void setEntertainment2km(Double entertainment2km) {
		this.entertainment2km = entertainment2km;
	}


	@Column(name="entertainment_500m", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getEntertainment500m() {
		return this.entertainment500m;
	}

	public void setEntertainment500m(Double entertainment500m) {
		this.entertainment500m = entertainment500m;
	}


	@Column(name="neighbourhood_1km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getNeighbourhood1km() {
		return this.neighbourhood1km;
	}

	public void setNeighbourhood1km(Double neighbourhood1km) {
		this.neighbourhood1km = neighbourhood1km;
	}


	@Column(name="neighbourhood_2km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getNeighbourhood2km() {
		return this.neighbourhood2km;
	}

	public void setNeighbourhood2km(Double neighbourhood2km) {
		this.neighbourhood2km = neighbourhood2km;
	}


	@Column(name="neighbourhood_500m", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getNeighbourhood500m() {
		return this.neighbourhood500m;
	}

	public void setNeighbourhood500m(Double neighbourhood500m) {
		this.neighbourhood500m = neighbourhood500m;
	}


	@Column(name="population_1km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getPopulation1km() {
		return this.population1km;
	}

	public void setPopulation1km(Double population1km) {
		this.population1km = population1km;
	}


	@Column(name="population_2km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getPopulation2km() {
		return this.population2km;
	}

	public void setPopulation2km(Double population2km) {
		this.population2km = population2km;
	}


	@Column(name="population_500m", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getPopulation500m() {
		return this.population500m;
	}

	public void setPopulation500m(Double population500m) {
		this.population500m = population500m;
	}


	@Column(name="prices", nullable = true, insertable = true, updatable = true, length = 131089, precision = 0)
	public BigDecimal getPrices() {
		return this.prices;
	}

	public void setPrices(BigDecimal prices) {
		this.prices = prices;
	}


	@Column(name="rental_prices_1_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getRentalPrices1Bedroom() {
		return this.rentalPrices1Bedroom;
	}

	public void setRentalPrices1Bedroom(Double rentalPrices1Bedroom) {
		this.rentalPrices1Bedroom = rentalPrices1Bedroom;
	}


	@Column(name="rental_prices_2_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getRentalPrices2Bedroom() {
		return this.rentalPrices2Bedroom;
	}

	public void setRentalPrices2Bedroom(Double rentalPrices2Bedroom) {
		this.rentalPrices2Bedroom = rentalPrices2Bedroom;
	}


	@Column(name="rental_prices_3_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getRentalPrices3Bedroom() {
		return this.rentalPrices3Bedroom;
	}

	public void setRentalPrices3Bedroom(Double rentalPrices3Bedroom) {
		this.rentalPrices3Bedroom = rentalPrices3Bedroom;
	}


	@Column(name="rental_prices_4_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getRentalPrices4Bedroom() {
		return this.rentalPrices4Bedroom;
	}

	public void setRentalPrices4Bedroom(Double rentalPrices4Bedroom) {
		this.rentalPrices4Bedroom = rentalPrices4Bedroom;
	}


	@Column(name="sale_prices_1_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getSalePrices1Bedroom() {
		return this.salePrices1Bedroom;
	}

	public void setSalePrices1Bedroom(Double salePrices1Bedroom) {
		this.salePrices1Bedroom = salePrices1Bedroom;
	}


	@Column(name="sale_prices_2_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getSalePrices2Bedroom() {
		return this.salePrices2Bedroom;
	}

	public void setSalePrices2Bedroom(Double salePrices2Bedroom) {
		this.salePrices2Bedroom = salePrices2Bedroom;
	}


	@Column(name="sale_prices_3_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getSalePrices3Bedroom() {
		return this.salePrices3Bedroom;
	}

	public void setSalePrices3Bedroom(Double salePrices3Bedroom) {
		this.salePrices3Bedroom = salePrices3Bedroom;
	}


	@Column(name="sale_prices_4_bedroom", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getSalePrices4Bedroom() {
		return this.salePrices4Bedroom;
	}

	public void setSalePrices4Bedroom(Double salePrices4Bedroom) {
		this.salePrices4Bedroom = salePrices4Bedroom;
	}


	@Column(name="services_1km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getServices1km() {
		return this.services1km;
	}

	public void setServices1km(Double services1km) {
		this.services1km = services1km;
	}


	@Column(name="services_2km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getServices2km() {
		return this.services2km;
	}

	public void setServices2km(Double services2km) {
		this.services2km = services2km;
	}


	@Column(name="services_500m", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getServices500m() {
		return this.services500m;
	}

	public void setServices500m(Double services500m) {
		this.services500m = services500m;
	}


	@Column(name="transport_1km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getTransport1km() {
		return this.transport1km;
	}

	public void setTransport1km(Double transport1km) {
		this.transport1km = transport1km;
	}


	@Column(name="transport_2km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getTransport2km() {
		return this.transport2km;
	}

	public void setTransport2km(Double transport2km) {
		this.transport2km = transport2km;
	}


	@Column(name="transport_500m", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getTransport500m() {
		return this.transport500m;
	}

	public void setTransport500m(Double transport500m) {
		this.transport500m = transport500m;
	}


	@Column(name="workspaces_education_1km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getWorkspacesEducation1km() {
		return this.workspacesEducation1km;
	}

	public void setWorkspacesEducation1km(Double workspacesEducation1km) {
		this.workspacesEducation1km = workspacesEducation1km;
	}


	@Column(name="workspaces_education_2km", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getWorkspacesEducation2km() {
		return this.workspacesEducation2km;
	}

	public void setWorkspacesEducation2km(Double workspacesEducation2km) {
		this.workspacesEducation2km = workspacesEducation2km;
	}

    @Column(name="workspaces_education_500m", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
	public Double getWorkspacesEducation500m() {
		return this.workspacesEducation500m;
	}

	public void setWorkspacesEducation500m(Double workspacesEducation500m) {
		this.workspacesEducation500m = workspacesEducation500m;
	}

    public Double getPrice(boolean isBuy, int numberOfBedrooms) {
        if (isBuy) {
            switch (numberOfBedrooms) {
                case 1:
                    return getSalePrices1Bedroom();

                case 2:
                    return getSalePrices2Bedroom();

                case 3:
                    return getSalePrices3Bedroom();

                case 4:
                    return getSalePrices4Bedroom();

                default:
                    return getSalePrices4Bedroom();

            }
        } else {
            switch (numberOfBedrooms) {
                case 1:
                    return getRentalPrices1Bedroom();

                case 2:
                    return getRentalPrices2Bedroom();

                case 3:
                    return getRentalPrices3Bedroom();

                case 4:
                    return getRentalPrices4Bedroom();

                default:
                    return getRentalPrices4Bedroom();

            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TravelZonesFacilitiesEntity that = (TravelZonesFacilitiesEntity) o;

        if (Double.compare(that.entertainment1km, entertainment1km) != 0) return false;
        if (Double.compare(that.entertainment2km, entertainment2km) != 0) return false;
        if (Double.compare(that.entertainment500m, entertainment500m) != 0) return false;
        if (Double.compare(that.neighbourhood1km, neighbourhood1km) != 0) return false;
        if (Double.compare(that.neighbourhood2km, neighbourhood2km) != 0) return false;
        if (Double.compare(that.neighbourhood500m, neighbourhood500m) != 0) return false;
        if (Double.compare(that.population1km, population1km) != 0) return false;
        if (Double.compare(that.population2km, population2km) != 0) return false;
        if (Double.compare(that.population500m, population500m) != 0) return false;
        if (Double.compare(that.rentalPrices1Bedroom, rentalPrices1Bedroom) != 0) return false;
        if (Double.compare(that.rentalPrices2Bedroom, rentalPrices2Bedroom) != 0) return false;
        if (Double.compare(that.rentalPrices3Bedroom, rentalPrices3Bedroom) != 0) return false;
        if (Double.compare(that.rentalPrices4Bedroom, rentalPrices4Bedroom) != 0) return false;
        if (Double.compare(that.salePrices1Bedroom, salePrices1Bedroom) != 0) return false;
        if (Double.compare(that.salePrices2Bedroom, salePrices2Bedroom) != 0) return false;
        if (Double.compare(that.salePrices3Bedroom, salePrices3Bedroom) != 0) return false;
        if (Double.compare(that.salePrices4Bedroom, salePrices4Bedroom) != 0) return false;
        if (Double.compare(that.services1km, services1km) != 0) return false;
        if (Double.compare(that.services2km, services2km) != 0) return false;
        if (Double.compare(that.services500m, services500m) != 0) return false;
        if (Double.compare(that.transport1km, transport1km) != 0) return false;
        if (Double.compare(that.transport2km, transport2km) != 0) return false;
        if (Double.compare(that.transport500m, transport500m) != 0) return false;
        if (Double.compare(that.workspacesEducation1km, workspacesEducation1km) != 0) return false;
        if (Double.compare(that.workspacesEducation2km, workspacesEducation2km) != 0) return false;
        if (Double.compare(that.workspacesEducation500m, workspacesEducation500m) != 0) return false;
        if (prices != null ? !prices.equals(that.prices) : that.prices != null) return false;
        if (!tz2006.equals(that.tz2006)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tz2006 != null ? tz2006.hashCode() : 0;
        result = 31 * result + (entertainment1km != null ? entertainment1km.hashCode() : 0);
        result = 31 * result + (entertainment2km != null ? entertainment2km.hashCode() : 0);
        result = 31 * result + (entertainment500m != null ? entertainment500m.hashCode() : 0);
        result = 31 * result + (neighbourhood1km != null ? neighbourhood1km.hashCode() : 0);
        result = 31 * result + (neighbourhood2km != null ? neighbourhood2km.hashCode() : 0);
        result = 31 * result + (neighbourhood500m != null ? neighbourhood500m.hashCode() : 0);
        result = 31 * result + (population1km != null ? population1km.hashCode() : 0);
        result = 31 * result + (population2km != null ? population2km.hashCode() : 0);
        result = 31 * result + (population500m != null ? population500m.hashCode() : 0);
        result = 31 * result + (prices != null ? prices.hashCode() : 0);
        result = 31 * result + (rentalPrices1Bedroom != null ? rentalPrices1Bedroom.hashCode() : 0);
        result = 31 * result + (rentalPrices2Bedroom != null ? rentalPrices2Bedroom.hashCode() : 0);
        result = 31 * result + (rentalPrices3Bedroom != null ? rentalPrices3Bedroom.hashCode() : 0);
        result = 31 * result + (rentalPrices4Bedroom != null ? rentalPrices4Bedroom.hashCode() : 0);
        result = 31 * result + (salePrices1Bedroom != null ? salePrices1Bedroom.hashCode() : 0);
        result = 31 * result + (salePrices2Bedroom != null ? salePrices2Bedroom.hashCode() : 0);
        result = 31 * result + (salePrices3Bedroom != null ? salePrices3Bedroom.hashCode() : 0);
        result = 31 * result + (salePrices4Bedroom != null ? salePrices4Bedroom.hashCode() : 0);
        result = 31 * result + (services1km != null ? services1km.hashCode() : 0);
        result = 31 * result + (services2km != null ? services2km.hashCode() : 0);
        result = 31 * result + (services500m != null ? services500m.hashCode() : 0);
        result = 31 * result + (transport1km != null ? transport1km.hashCode() : 0);
        result = 31 * result + (transport2km != null ? transport2km.hashCode() : 0);
        result = 31 * result + (transport500m != null ? transport500m.hashCode() : 0);
        result = 31 * result + (workspacesEducation1km != null ? workspacesEducation1km.hashCode() : 0);
        result = 31 * result + (workspacesEducation2km != null ? workspacesEducation2km.hashCode() : 0);
        result = 31 * result + (workspacesEducation500m != null ? workspacesEducation500m.hashCode() : 0);
        return result;
    }
}