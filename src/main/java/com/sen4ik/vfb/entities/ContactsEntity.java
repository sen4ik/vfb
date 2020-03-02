package com.sen4ik.vfb.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "contacts", schema = "verse_from_bible")
public class ContactsEntity {
    private int id;
    private String name;
    private String phoneNumber;
    private String bibleTranslation;
    private Timestamp addedOn;
    private Byte deleted;
    private Byte subscribed;
    private Double selectedSendTime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    @Column(name = "bible_translation")
    public String getBibleTranslation() {
        return bibleTranslation;
    }

    public void setBibleTranslation(String bibleTranslation) {
        this.bibleTranslation = bibleTranslation;
    }

    @Basic
    @Column(name = "added_on")
    public Timestamp getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Timestamp addedOn) {
        this.addedOn = addedOn;
    }

    @Basic
    @Column(name = "deleted")
    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    @Basic
    @Column(name = "subscribed")
    public Byte getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Byte subscribed) {
        this.subscribed = subscribed;
    }

    @Basic
    @Column(name = "selected_send_time")
    public Double getSelectedSendTime() {
        return selectedSendTime;
    }

    public void setSelectedSendTime(Double selectedSendTime) {
        this.selectedSendTime = selectedSendTime;
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
