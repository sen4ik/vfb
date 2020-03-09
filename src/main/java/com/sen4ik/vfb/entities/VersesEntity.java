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
    private String ruVerseLocation;
    private String enVerseLocation;
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
    @Column(name = "ru_verse_location")
    public String getRuVerseLocation() {
        return ruVerseLocation;
    }

    public void setRuVerseLocation(String ruVerseLocation) {
        this.ruVerseLocation = ruVerseLocation;
    }

    @Basic
    @Column(name = "en_verse_location")
    public String getEnVerseLocation() {
        return enVerseLocation;
    }

    public void setEnVerseLocation(String enVerseLocation) {
        this.enVerseLocation = enVerseLocation;
    }

    @Basic
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
