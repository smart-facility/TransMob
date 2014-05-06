package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity for process_link entity.
 */
@javax.persistence.Table(name = "process_link", schema = "public")
@Entity
public class ProcessLinkEntity {
    private int id;
    private String access;
    private String fromId;
    private String toId;
    private String toType;
    private String time;
    private String cost;
    private String notes;
    private String fromDetails;
    private String toDetails;
    private String fromType;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "access", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    @javax.persistence.Column(name = "from_id", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    @javax.persistence.Column(name = "to_id", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    @javax.persistence.Column(name = "to_type", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    @javax.persistence.Column(name = "time", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @javax.persistence.Column(name = "cost", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @javax.persistence.Column(name = "notes", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @javax.persistence.Column(name = "from_details", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getFromDetails() {
        return fromDetails;
    }

    public void setFromDetails(String fromDetails) {
        this.fromDetails = fromDetails;
    }

    @javax.persistence.Column(name = "to_details", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getToDetails() {
        return toDetails;
    }

    public void setToDetails(String toDetails) {
        this.toDetails = toDetails;
    }

    @javax.persistence.Column(name = "from_type", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessLinkEntity that = (ProcessLinkEntity) o;

        if (id != that.id) return false;
        if (access != null ? !access.equals(that.access) : that.access != null) return false;
        if (cost != null ? !cost.equals(that.cost) : that.cost != null) return false;
        if (fromDetails != null ? !fromDetails.equals(that.fromDetails) : that.fromDetails != null) return false;
        if (fromId != null ? !fromId.equals(that.fromId) : that.fromId != null) return false;
        if (fromType != null ? !fromType.equals(that.fromType) : that.fromType != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (toDetails != null ? !toDetails.equals(that.toDetails) : that.toDetails != null) return false;
        if (toId != null ? !toId.equals(that.toId) : that.toId != null) return false;
        if (toType != null ? !toType.equals(that.toType) : that.toType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (access != null ? access.hashCode() : 0);
        result = 31 * result + (fromId != null ? fromId.hashCode() : 0);
        result = 31 * result + (toId != null ? toId.hashCode() : 0);
        result = 31 * result + (toType != null ? toType.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (fromDetails != null ? fromDetails.hashCode() : 0);
        result = 31 * result + (toDetails != null ? toDetails.hashCode() : 0);
        result = 31 * result + (fromType != null ? fromType.hashCode() : 0);
        return result;
    }
}
