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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Geometry;


/**
 * The persistent class for the travel_zones_randwick_gs database table.
 * 
 */
@Entity
@Table(name="travel_zones_randwick_gs", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TravelZonesRandwickGSEntity {
	private Integer gid;
	private BigDecimal area;
	private BigDecimal areaSqm;
	private Integer external;
	private String externalS;
	private BigDecimal hhDensity;
	private Integer internal;
	private Integer island;
	private Integer lga2006;
	private String lga2006Na;
	private Integer pop06;
	private double postcodeA;
	private BigDecimal prices;
	private String ring;
	private Integer sd2006;
	private double sd2006Abs;
	private String sd2006Nam;
	private Integer sla2006;
	private String sla2006Na;
	private Integer ssd2006;
	private double ssd2006Ab;
	private String ssd2006Na;
	private double subregion;
	private String subregion_;
	private String suburbAbs;
	private Geometry theGeom;
	private Integer tz06;
	private Integer tz2006;
	private String tz2006Nam;
	private Integer xCentroid;
	private Integer xMga56;
	private Integer yCentroid;
	private Integer yMga56;

	public TravelZonesRandwickGSEntity() {
	}


	@Id
	@Column(unique=true, nullable=false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    @SequenceGenerator(name="seq-gen", sequenceName="travel_zones_randwick_gs_gid_seq")
	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}


	@Column(precision=10)
	public BigDecimal getArea() {
		return this.area;
	}

	public void setArea(BigDecimal area) {
		this.area = area;
	}


	@Column(name="area_sqm")
	public BigDecimal getAreaSqm() {
		return this.areaSqm;
	}

	public void setAreaSqm(BigDecimal areaSqm) {
		this.areaSqm = areaSqm;
	}

    @Column
	public Integer getExternal() {
		return this.external;
	}

	public void setExternal(Integer external) {
		this.external = external;
	}


	@Column(name="external_s", length=9)
	public String getExternalS() {
		return this.externalS;
	}

	public void setExternalS(String externalS) {
		this.externalS = externalS;
	}


	@Column(name="hh_density", precision=10)
	public BigDecimal getHhDensity() {
		return this.hhDensity;
	}

	public void setHhDensity(BigDecimal hhDensity) {
		this.hhDensity = hhDensity;
	}

    @Column
    public Integer getInternal() {
		return this.internal;
	}

	public void setInternal(Integer internal) {
		this.internal = internal;
	}

    @Column
	public Integer getIsland() {
		return this.island;
	}

	public void setIsland(Integer island) {
		this.island = island;
	}


    @Column
	public Integer getLga2006() {
		return this.lga2006;
	}

	public void setLga2006(Integer lga2006) {
		this.lga2006 = lga2006;
	}


	@Column(name="lga2006_na", length=26)
	public String getLga2006Na() {
		return this.lga2006Na;
	}

	public void setLga2006Na(String lga2006Na) {
		this.lga2006Na = lga2006Na;
	}


	@Column(name="pop_06_")
	public Integer getPop06() {
		return this.pop06;
	}

	public void setPop06(Integer pop06) {
		this.pop06 = pop06;
	}


	@Column(name="postcode_a")
	public double getPostcodeA() {
		return this.postcodeA;
	}

	public void setPostcodeA(double postcodeA) {
		this.postcodeA = postcodeA;
	}


	@Column
	public BigDecimal getPrices() {
		return this.prices;
	}

	public void setPrices(BigDecimal prices) {
		this.prices = prices;
	}


	@Column(length=10)
	public String getRing() {
		return this.ring;
	}

	public void setRing(String ring) {
		this.ring = ring;
	}


    @Column
	public Integer getSd2006() {
		return this.sd2006;
	}

	public void setSd2006(Integer sd2006) {
		this.sd2006 = sd2006;
	}


	@Column(name="sd2006_abs")
	public double getSd2006Abs() {
		return this.sd2006Abs;
	}

	public void setSd2006Abs(double sd2006Abs) {
		this.sd2006Abs = sd2006Abs;
	}


	@Column(name="sd2006_nam", length=12)
	public String getSd2006Nam() {
		return this.sd2006Nam;
	}

	public void setSd2006Nam(String sd2006Nam) {
		this.sd2006Nam = sd2006Nam;
	}


    @Column
	public Integer getSla2006() {
		return this.sla2006;
	}

	public void setSla2006(Integer sla2006) {
		this.sla2006 = sla2006;
	}


	@Column(name="sla2006_na", length=26)
	public String getSla2006Na() {
		return this.sla2006Na;
	}

	public void setSla2006Na(String sla2006Na) {
		this.sla2006Na = sla2006Na;
	}


    @Column
	public Integer getSsd2006() {
		return this.ssd2006;
	}

	public void setSsd2006(Integer ssd2006) {
		this.ssd2006 = ssd2006;
	}


	@Column(name="ssd2006_ab")
	public double getSsd2006Ab() {
		return this.ssd2006Ab;
	}

	public void setSsd2006Ab(double ssd2006Ab) {
		this.ssd2006Ab = ssd2006Ab;
	}


	@Column(name="ssd2006_na", length=26)
	public String getSsd2006Na() {
		return this.ssd2006Na;
	}

	public void setSsd2006Na(String ssd2006Na) {
		this.ssd2006Na = ssd2006Na;
	}


    @Column
	public double getSubregion() {
		return this.subregion;
	}

	public void setSubregion(double subregion) {
		this.subregion = subregion;
	}


	@Column(name="subregion_", length=19)
	public String getSubregion_() {
		return this.subregion_;
	}

	public void setSubregion_(String subregion) {
		this.subregion_ = subregion;
	}


	@Column(name="suburb_abs", length=35)
	public String getSuburbAbs() {
		return this.suburbAbs;
	}

	public void setSuburbAbs(String suburbAbs) {
		this.suburbAbs = suburbAbs;
	}


	@Column(name="the_geom")
    @Type(type = "org.hibernatespatial.GeometryUserType")
	public Geometry getTheGeom() {
		return this.theGeom;
	}

	public void setTheGeom(Geometry theGeom) {
		this.theGeom = theGeom;
	}


    @Column
	public Integer getTz06() {
		return this.tz06;
	}

	public void setTz06(Integer tz06) {
		this.tz06 = tz06;
	}


    @Column
	public Integer getTz2006() {
		return this.tz2006;
	}

	public void setTz2006(Integer tz2006) {
		this.tz2006 = tz2006;
	}


	@Column(name="tz2006_nam", length=47)
	public String getTz2006Nam() {
		return this.tz2006Nam;
	}

	public void setTz2006Nam(String tz2006Nam) {
		this.tz2006Nam = tz2006Nam;
	}


	@Column(name="x_centroid")
	public Integer getXCentroid() {
		return this.xCentroid;
	}

	public void setXCentroid(Integer xCentroid) {
		this.xCentroid = xCentroid;
	}


	@Column(name="x_mga56")
	public Integer getXMga56() {
		return this.xMga56;
	}

	public void setXMga56(Integer xMga56) {
		this.xMga56 = xMga56;
	}


	@Column(name="y_centroid")
	public Integer getYCentroid() {
		return this.yCentroid;
	}

	public void setYCentroid(Integer yCentroid) {
		this.yCentroid = yCentroid;
	}


	@Column(name="y_mga56")
	public Integer getYMga56() {
		return this.yMga56;
	}

	public void setYMga56(Integer yMga56) {
		this.yMga56 = yMga56;
	}

}