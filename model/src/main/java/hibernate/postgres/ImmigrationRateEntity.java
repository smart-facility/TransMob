package hibernate.postgres;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity for immigration_rate table.
 */
@javax.persistence.Table(name = "immigration_rate", schema = "public")
@Entity
public class ImmigrationRateEntity {
    private int year;

    @javax.persistence.Column(name = "year", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private Integer hf1;

    @javax.persistence.Column(name = "hf1", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf1() {
        return hf1;
    }

    public void setHf1(Integer hf1) {
        this.hf1 = hf1;
    }

    private Integer hf2;

    @javax.persistence.Column(name = "hf2", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf2() {
        return hf2;
    }

    public void setHf2(Integer hf2) {
        this.hf2 = hf2;
    }

    private Integer hf3;

    @javax.persistence.Column(name = "hf3", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf3() {
        return hf3;
    }

    public void setHf3(Integer hf3) {
        this.hf3 = hf3;
    }

    private Integer hf4;

    @javax.persistence.Column(name = "hf4", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf4() {
        return hf4;
    }

    public void setHf4(Integer hf4) {
        this.hf4 = hf4;
    }

    private Integer hf5;

    @javax.persistence.Column(name = "hf5", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf5() {
        return hf5;
    }

    public void setHf5(Integer hf5) {
        this.hf5 = hf5;
    }

    private Integer hf6;

    @javax.persistence.Column(name = "hf6", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf6() {
        return hf6;
    }

    public void setHf6(Integer hf6) {
        this.hf6 = hf6;
    }

    private Integer hf7;

    @javax.persistence.Column(name = "hf7", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf7() {
        return hf7;
    }

    public void setHf7(Integer hf7) {
        this.hf7 = hf7;
    }

    private Integer hf8;

    @javax.persistence.Column(name = "hf8", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf8() {
        return hf8;
    }

    public void setHf8(Integer hf8) {
        this.hf8 = hf8;
    }

    private Integer hf9;

    @javax.persistence.Column(name = "hf9", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf9() {
        return hf9;
    }

    public void setHf9(Integer hf9) {
        this.hf9 = hf9;
    }

    private Integer hf10;

    @javax.persistence.Column(name = "hf10", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf10() {
        return hf10;
    }

    public void setHf10(Integer hf10) {
        this.hf10 = hf10;
    }

    private Integer hf11;

    @javax.persistence.Column(name = "hf11", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf11() {
        return hf11;
    }

    public void setHf11(Integer hf11) {
        this.hf11 = hf11;
    }

    private Integer hf12;

    @javax.persistence.Column(name = "hf12", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf12() {
        return hf12;
    }

    public void setHf12(Integer hf12) {
        this.hf12 = hf12;
    }

    private Integer hf13;

    @javax.persistence.Column(name = "hf13", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf13() {
        return hf13;
    }

    public void setHf13(Integer hf13) {
        this.hf13 = hf13;
    }

    private Integer hf14;

    @javax.persistence.Column(name = "hf14", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf14() {
        return hf14;
    }

    public void setHf14(Integer hf14) {
        this.hf14 = hf14;
    }

    private Integer hf15;

    @javax.persistence.Column(name = "hf15", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf15() {
        return hf15;
    }

    public void setHf15(Integer hf15) {
        this.hf15 = hf15;
    }

    private Integer hf16;

    @javax.persistence.Column(name = "hf16", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getHf16() {
        return hf16;
    }

    public void setHf16(Integer hf16) {
        this.hf16 = hf16;
    }

    private Integer nf;

    @javax.persistence.Column(name = "nf", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getNf() {
        return nf;
    }

    public void setNf(Integer nf) {
        this.nf = nf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmigrationRateEntity that = (ImmigrationRateEntity) o;

        if (year != that.year) return false;
        if (hf1 != null ? !hf1.equals(that.hf1) : that.hf1 != null) return false;
        if (hf10 != null ? !hf10.equals(that.hf10) : that.hf10 != null) return false;
        if (hf11 != null ? !hf11.equals(that.hf11) : that.hf11 != null) return false;
        if (hf12 != null ? !hf12.equals(that.hf12) : that.hf12 != null) return false;
        if (hf13 != null ? !hf13.equals(that.hf13) : that.hf13 != null) return false;
        if (hf14 != null ? !hf14.equals(that.hf14) : that.hf14 != null) return false;
        if (hf15 != null ? !hf15.equals(that.hf15) : that.hf15 != null) return false;
        if (hf16 != null ? !hf16.equals(that.hf16) : that.hf16 != null) return false;
        if (hf2 != null ? !hf2.equals(that.hf2) : that.hf2 != null) return false;
        if (hf3 != null ? !hf3.equals(that.hf3) : that.hf3 != null) return false;
        if (hf4 != null ? !hf4.equals(that.hf4) : that.hf4 != null) return false;
        if (hf5 != null ? !hf5.equals(that.hf5) : that.hf5 != null) return false;
        if (hf6 != null ? !hf6.equals(that.hf6) : that.hf6 != null) return false;
        if (hf7 != null ? !hf7.equals(that.hf7) : that.hf7 != null) return false;
        if (hf8 != null ? !hf8.equals(that.hf8) : that.hf8 != null) return false;
        if (hf9 != null ? !hf9.equals(that.hf9) : that.hf9 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + (hf1 != null ? hf1.hashCode() : 0);
        result = 31 * result + (hf2 != null ? hf2.hashCode() : 0);
        result = 31 * result + (hf3 != null ? hf3.hashCode() : 0);
        result = 31 * result + (hf4 != null ? hf4.hashCode() : 0);
        result = 31 * result + (hf5 != null ? hf5.hashCode() : 0);
        result = 31 * result + (hf6 != null ? hf6.hashCode() : 0);
        result = 31 * result + (hf7 != null ? hf7.hashCode() : 0);
        result = 31 * result + (hf8 != null ? hf8.hashCode() : 0);
        result = 31 * result + (hf9 != null ? hf9.hashCode() : 0);
        result = 31 * result + (hf10 != null ? hf10.hashCode() : 0);
        result = 31 * result + (hf11 != null ? hf11.hashCode() : 0);
        result = 31 * result + (hf12 != null ? hf12.hashCode() : 0);
        result = 31 * result + (hf13 != null ? hf13.hashCode() : 0);
        result = 31 * result + (hf14 != null ? hf14.hashCode() : 0);
        result = 31 * result + (hf15 != null ? hf15.hashCode() : 0);
        result = 31 * result + (hf16 != null ? hf16.hashCode() : 0);
        result = 31 * result + (nf != null ? nf.hashCode() : 0);
        return result;
    }
}
