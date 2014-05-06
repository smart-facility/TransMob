package hibernate.postgres;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Flags configuration table.
 */
@javax.persistence.Table(name = "_2006", schema = "tend_by_berdr")
@Entity
public class NewTendbyBerdrEntity {
	private Integer id;
    private Integer tz06;
    private Double bd1 ;
    private Double bd2 ;
    private Double bd3 ;
    private Double bdp4 ;
    private String ownership  ;
    
    public static enum OwnershipTypes {
    	Rented, Being_purchased, Fully_owned
    };
      
    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    @javax.persistence.Column(name = "tz06", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getTz06() {
        return this.tz06;
    }

    public void setTz06(Integer tz06) {
        this.tz06 = tz06;
    }

    @Column(name = "_1bd ", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getBd1() {
        return bd1;
    }

    public void setBd1(Double bd1) {
        this.bd1 = bd1;
    }
    
    @Column(name = "_2bd ", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getBd2() {
        return bd2;
    }

    public void setBd2(Double bd2) {
        this.bd2 = bd2;
    }
    
    @Column(name = "_3bd ", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getBd3() {
        return bd3;
    }

    public void setBd3(Double bd3) {
        this.bd3 = bd3;
    }
    
    @Column(name = "_4bdp ", nullable = true, insertable = true, updatable = true, length = 17, precision = 17)
    @Basic
    public Double getBd4() {
        return bdp4;
    }

    public void setBd4(Double bdp4) {
        this.bdp4 = bdp4;
    }
    
    @Column(name = "ownership", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getOwnership() {
//         Clean up the ownership value ????  
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }
    
    
}
