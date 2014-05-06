package hibernate.postgres;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Parking Access.
 */
@javax.persistence.Table(name = "parking_access", schema = "public")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ParkingAccessEntity {
    private int accessId;
    private Integer fromId;
    private Integer toId;
    private String fromType;
    private String toType;

    @javax.persistence.Column(name = "access_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getAccessId() {
        return accessId;
    }

    public void setAccessId(int accessId) {
        this.accessId = accessId;
    }

    @javax.persistence.Column(name = "from_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    @javax.persistence.Column(name = "to_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    @javax.persistence.Column(name = "from_type", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    @javax.persistence.Column(name = "to_type", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParkingAccessEntity that = (ParkingAccessEntity) o;

        if (accessId != that.accessId) return false;
        if (fromId != null ? !fromId.equals(that.fromId) : that.fromId != null) return false;
        if (fromType != null ? !fromType.equals(that.fromType) : that.fromType != null) return false;
        if (toId != null ? !toId.equals(that.toId) : that.toId != null) return false;
        if (toType != null ? !toType.equals(that.toType) : that.toType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accessId;
        result = 31 * result + (fromId != null ? fromId.hashCode() : 0);
        result = 31 * result + (toId != null ? toId.hashCode() : 0);
        result = 31 * result + (fromType != null ? fromType.hashCode() : 0);
        result = 31 * result + (toType != null ? toType.hashCode() : 0);
        return result;
    }
}
