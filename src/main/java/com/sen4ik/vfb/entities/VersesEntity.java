package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.base.Constants;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "verses", schema = Constants.SCHEMA)
public class VersesEntity {
    private int id;
    private String ruSynodal;
    private String enEsv;
    private String enKjv;
    private String enNiv;
    private Date date;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ru_synodal")
    public String getRuSynodal() {
        return ruSynodal;
    }

    public void setRuSynodal(String ruSynodal) {
        this.ruSynodal = ruSynodal;
    }

    @Basic
    @Column(name = "en_esv")
    public String getEnEsv() {
        return enEsv;
    }

    public void setEnEsv(String enEsv) {
        this.enEsv = enEsv;
    }

    @Basic
    @Column(name = "en_kjv")
    public String getEnKjv() {
        return enKjv;
    }

    public void setEnKjv(String enKjv) {
        this.enKjv = enKjv;
    }

    @Basic
    @Column(name = "en_niv")
    public String getEnNiv() {
        return enNiv;
    }

    public void setEnNiv(String enNiv) {
        this.enNiv = enNiv;
    }

    @Basic
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersesEntity that = (VersesEntity) o;

        if (id != that.id) return false;
        if (ruSynodal != null ? !ruSynodal.equals(that.ruSynodal) : that.ruSynodal != null) return false;
        if (enEsv != null ? !enEsv.equals(that.enEsv) : that.enEsv != null) return false;
        if (enKjv != null ? !enKjv.equals(that.enKjv) : that.enKjv != null) return false;
        if (enNiv != null ? !enNiv.equals(that.enNiv) : that.enNiv != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (ruSynodal != null ? ruSynodal.hashCode() : 0);
        result = 31 * result + (enEsv != null ? enEsv.hashCode() : 0);
        result = 31 * result + (enKjv != null ? enKjv.hashCode() : 0);
        result = 31 * result + (enNiv != null ? enNiv.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
