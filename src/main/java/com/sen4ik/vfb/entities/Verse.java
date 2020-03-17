package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.base.Constants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "verses", schema = Constants.SCHEMA)
public class Verse {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "ru_synodal")
    private String ruSynodal;

    @Basic
    @Column(name = "en_esv")
    private String enEsv;

    @Basic
    @Column(name = "en_kjv")
    private String enKjv;

    @Basic
    @Column(name = "en_niv")
    private String enNiv;

    @Basic
    @Column(name = "ru_verse_location")
    private String ruVerseLocation;

    @Basic
    @Column(name = "en_verse_location")
    private String enVerseLocation;

    @Basic
    @Column(name = "date")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Temporal(TemporalType.DATE)
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuSynodal() {
        return ruSynodal;
    }

    public void setRuSynodal(String ruSynodal) {
        this.ruSynodal = ruSynodal;
    }

    public String getEnEsv() {
        return enEsv;
    }

    public void setEnEsv(String enEsv) {
        this.enEsv = enEsv;
    }

    public String getEnKjv() {
        return enKjv;
    }

    public void setEnKjv(String enKjv) {
        this.enKjv = enKjv;
    }

    public String getEnNiv() {
        return enNiv;
    }

    public void setEnNiv(String enNiv) {
        this.enNiv = enNiv;
    }

    public String getRuVerseLocation() {
        return ruVerseLocation;
    }

    public void setRuVerseLocation(String ruVerseLocation) {
        this.ruVerseLocation = ruVerseLocation;
    }

    public String getEnVerseLocation() {
        return enVerseLocation;
    }

    public void setEnVerseLocation(String enVerseLocation) {
        this.enVerseLocation = enVerseLocation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
