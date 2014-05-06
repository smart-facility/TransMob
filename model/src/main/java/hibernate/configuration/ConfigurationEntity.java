package hibernate.configuration;

import javax.persistence.*;

/**
 * Configuration entity.
 */
@javax.persistence.Table(name = "configuration", schema = "public")
@Entity
public class ConfigurationEntity {
    private int id;
    private Integer agentNumber;
    private String shapefile;
    private Integer startYear;
    private Integer runningYear;
    private String nameofscenario;

    @javax.persistence.Column(name = "configuration_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    @SequenceGenerator(name="seq-gen", sequenceName="configuration_id_seq")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "agent_number", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getAgentNumber() {
        return agentNumber;
    }

    public void setAgentNumber(Integer agentNumber) {
        this.agentNumber = agentNumber;
    }

    @javax.persistence.Column(name = "shapefile", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getShapefile() {
        return shapefile;
    }

    public void setShapefile(String shapefile) {
        this.shapefile = shapefile;
    }
    
    // private String nameofscenario;
    @javax.persistence.Column(name = "nameofscenario", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getNameofscenario() {
        return nameofscenario;
    }

    public void setNameofscenario(String nameofscenario) {
        this.nameofscenario = nameofscenario;
    }
    
    
    @javax.persistence.Column(name = "start_year", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    @javax.persistence.Column(name = "running_year", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getRunningYear() {
        return runningYear;
    }

    public void setRunningYear(Integer runningYear) {
        this.runningYear = runningYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationEntity that = (ConfigurationEntity) o;

        if (id != that.id) return false;
        if (agentNumber != null ? !agentNumber.equals(that.agentNumber) : that.agentNumber != null) return false;
        if (runningYear != null ? !runningYear.equals(that.runningYear) : that.runningYear != null) return false;
        if (shapefile != null ? !shapefile.equals(that.shapefile) : that.shapefile != null) return false;
        if (startYear != null ? !startYear.equals(that.startYear) : that.startYear != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (agentNumber != null ? agentNumber.hashCode() : 0);
        result = 31 * result + (shapefile != null ? shapefile.hashCode() : 0);
        result = 31 * result + (startYear != null ? startYear.hashCode() : 0);
        result = 31 * result + (runningYear != null ? runningYear.hashCode() : 0);
        return result;
    }
}
