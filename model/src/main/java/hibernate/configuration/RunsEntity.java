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
package hibernate.configuration;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Flags configuration table.
 */
@javax.persistence.Table(name = "runs", schema = "public")
@Entity
public class RunsEntity {
    private Integer seed;
    private Integer status;
    private Timestamp timeStart;
    private Timestamp timeFinished;
    private Integer configurationId;
    private int id;
    private int hostname;
    
    
    private String nameforoutputdb;
    @Column(name = "nameforoutputdb", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getNameforoutputdb() {
        // Clean up the category value.       
        return this.nameforoutputdb;
    }

    public void setNameforoutputdb(String nameforoutputdb) {
        this.nameforoutputdb = nameforoutputdb;
    }
    
    @javax.persistence.Column(name = "seed", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getSeed() {
        return this.seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }
    
    @javax.persistence.Column(name = "hostname", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHostname() {
        return this.hostname;
    }

    public void setHostname(Integer hostname) {
        this.hostname = hostname;
    }

    @javax.persistence.Column(name = "status", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @javax.persistence.Column(name = "time_start", nullable = true, insertable = true, updatable = true, length = 29, precision = 6)
    @Basic
    public Timestamp getTimeStart() {
        return this.timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    @javax.persistence.Column(name = "time_finished", nullable = true, insertable = true, updatable = true, length = 29, precision = 6)
    @Basic
    public Timestamp getTimeFinished() {
        return this.timeFinished;
    }

    public void setTimeFinished(Timestamp timeFinished) {
        this.timeFinished = timeFinished;
    }

    @javax.persistence.Column(name = "configuration_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getConfigurationId() {
        return this.configurationId;
    }

    public void setConfigurationId(Integer configurationId) {
        this.configurationId = configurationId;
    }

    @javax.persistence.Column(name = "runs_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    @SequenceGenerator(name="seq-gen", sequenceName="flags_id_seq")
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RunsEntity other = (RunsEntity) obj;
		if (this.configurationId == null) {
			if (other.configurationId != null)
				return false;
		} else if (!this.configurationId.equals(other.configurationId))
			return false;
		if (this.id != other.id)
			return false;
		if (this.seed == null) {
			if (other.seed != null)
				return false;
		} else if (!this.seed.equals(other.seed))
			return false;
		if (this.status == null) {
			if (other.status != null)
				return false;
		} else if (!this.status.equals(other.status))
			return false;
		if (this.timeFinished == null) {
			if (other.timeFinished != null)
				return false;
		} else if (!this.timeFinished.equals(other.timeFinished))
			return false;
		if (this.timeStart == null) {
			if (other.timeStart != null)
				return false;
		} else if (!this.timeStart.equals(other.timeStart))
			return false;
		return true;
	}

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.configurationId == null) ? 0 : this.configurationId.hashCode());
		result = prime * result + this.id;
		result = prime * result + ((this.seed == null) ? 0 : this.seed.hashCode());
		result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
		result = prime * result
				+ ((this.timeFinished == null) ? 0 : this.timeFinished.hashCode());
		result = prime * result
				+ ((this.timeStart == null) ? 0 : this.timeStart.hashCode());
		return result;
	}
    
    
}
