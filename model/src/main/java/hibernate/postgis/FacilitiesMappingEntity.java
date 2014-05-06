package hibernate.postgis;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Facilities Mapping Entity.
 */
@javax.persistence.Table(name = "facilities_mapping", schema = "public")
@Entity
public class FacilitiesMappingEntity {
    private int id;
    private String facility;
    private Boolean starred;
    private String category;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "facility", nullable = false, insertable = true, updatable = true, length = 50, precision = 0)
    @Basic
    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    @javax.persistence.Column(name = "starred", nullable = true, insertable = true, updatable = true, length = 1, precision = 0)
    @Basic
    public Boolean getStarred() {
        return starred;
    }

    public void setStarred(Boolean starred) {
        this.starred = starred;
    }

    @javax.persistence.Column(name = "category", nullable = true, insertable = true, updatable = true, length = 30, precision = 0)
    @Basic
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacilitiesMappingEntity that = (FacilitiesMappingEntity) o;

        if (id != that.id) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (facility != null ? !facility.equals(that.facility) : that.facility != null) return false;
        if (starred != null ? !starred.equals(that.starred) : that.starred != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        result = 31 * result + (starred != null ? starred.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
