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
    @Column(name = "deleted")
    private Byte deleted = 0;

    @Basic
    @Column(name = "subscription_confirmed")
    private Byte subscriptionConfirmed = 0;

    @Basic
    @Column(name = "subscribed")
    private Byte subscribed = 1;

    @Basic
    @Column(name = "selected_send_time")
    private Double selectedSendTime;

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

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    public Byte getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Byte subscribed) {
        this.subscribed = subscribed;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactsEntity that = (ContactsEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) return false;
        if (bibleTranslation != null ? !bibleTranslation.equals(that.bibleTranslation) : that.bibleTranslation != null)
            return false;
        if (addedOn != null ? !addedOn.equals(that.addedOn) : that.addedOn != null) return false;
        if (deleted != null ? !deleted.equals(that.deleted) : that.deleted != null) return false;
        if (subscribed != null ? !subscribed.equals(that.subscribed) : that.subscribed != null) return false;
        if (selectedSendTime != null ? !selectedSendTime.equals(that.selectedSendTime) : that.selectedSendTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (bibleTranslation != null ? bibleTranslation.hashCode() : 0);
        result = 31 * result + (addedOn != null ? addedOn.hashCode() : 0);
        result = 31 * result + (deleted != null ? deleted.hashCode() : 0);
        result = 31 * result + (subscribed != null ? subscribed.hashCode() : 0);
        result = 31 * result + (selectedSendTime != null ? selectedSendTime.hashCode() : 0);
        return result;
    }
}
