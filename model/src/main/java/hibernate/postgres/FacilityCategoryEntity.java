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

import core.FacilityType;

import javax.persistence.*;

/**
 * Entity for facility_category table.
 */
@Table(name = "facility_category", schema = "public")
@Entity
public class FacilityCategoryEntity {
    private int id;

    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String category;

    @Column(name = "category", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getCategory() {
        // Clean up the category value.
        String cleanCategory = category.toLowerCase().trim();
        cleanCategory = cleanCategory.replace(" ", "_");
        cleanCategory = cleanCategory.replace("-", "_");
        cleanCategory = cleanCategory.replace("/", "_");
        cleanCategory = cleanCategory.replace("___", "_");
        return cleanCategory;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String facility;

    @Column(name = "facility", nullable = true, insertable = true, updatable = true, columnDefinition = "TEXT", precision = 0)
    @Basic
    public String getFacility() {
        String cleanfacility = facility.toLowerCase().trim();
        cleanfacility = cleanfacility.replace(" ", "_");
        cleanfacility = cleanfacility.replace("-", "_");
        cleanfacility = cleanfacility.replace("/", "_");
        cleanfacility = cleanfacility.replace("___", "_");
        return cleanfacility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public FacilityType retrieveFacilityAsType() {
        String facility = getFacility();

        return FacilityType.getFacilityType(facility);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacilityCategoryEntity that = (FacilityCategoryEntity) o;

        if (id != that.id) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (facility != null ? !facility.equals(that.facility) : that.facility != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        return result;
    }
}
