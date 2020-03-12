package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.base.Constants;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Entity
@Table(name = "contacts", schema = Constants.SCHEMA)
public class ContactsEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Basic
    @Column(name = "name")
    @NotEmpty(message = "Please provide a name")
    private String name;

    @Basic
    @Column(name = "phone_number")
    @NotEmpty(message = "Please provide a phone number")
    private String phoneNumber;

    @Basic
    @Column(name = "bible_translation")
    @NotEmpty(message = "Please provide a bible translation")
    private String bibleTranslation;

    @Basic
    @Column(name = "added_on", updatable = false)
    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Timestamp addedOn;

    @Basic
    @Column(name = "subscription_confirmed")
    private Byte subscriptionConfirmed = 0;

    @Basic
    @Column(name = "selected_send_time")
    private Double selectedSendTime;

    @Basic
    @Column(name = "selected_time_zone")
    private String selectedTimeZone;

    @Basic
    @Column(name = "selected_send_time_pacific")
    private Double selectedSendTimePacific;

    public Double getSelectedSendTimePacific() {
        return selectedSendTimePacific;
    }

    public void setSelectedSendTimePacific(Double selectedSendTimePacific) {
        this.selectedSendTimePacific = selectedSendTimePacific;
    }

    public String getSelectedTimeZone() {
        return selectedTimeZone;
    }

    public void setSelectedTimeZone(String selectedTimeZone) {
        this.selectedTimeZone = selectedTimeZone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBibleTranslation() {
        return bibleTranslation;
    }

    public void setBibleTranslation(String bibleTranslation) {
        this.bibleTranslation = bibleTranslation;
    }

    public Timestamp getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Timestamp addedOn) {
        this.addedOn = addedOn;
    }

    public Double getSelectedSendTime() {
        return selectedSendTime;
    }

    public void setSelectedSendTime(Double selectedSendTime) {
        this.selectedSendTime = selectedSendTime;
    }

    public Byte getSubscriptionConfirmed() {
        return subscriptionConfirmed;
    }

    public void setSubscriptionConfirmed(Byte subscriptionConfirmed) {
        this.subscriptionConfirmed = subscriptionConfirmed;
    }

}
