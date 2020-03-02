package com.sen4ik.vfb.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "sent_messages_log", schema = "verse_from_bible")
public class SentMessagesLogEntity {
    private int id;
    private Integer userId;
    private Integer verseId;
    private String messageBody;
    private String phoneNumberSentTo;
    private String phoneNumberSentFrom;
    private Timestamp timestamp;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "verse_id")
    public Integer getVerseId() {
        return verseId;
    }

    public void setVerseId(Integer verseId) {
        this.verseId = verseId;
    }

    @Basic
    @Column(name = "message_body")
    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    @Basic
    @Column(name = "phone_number_sent_to")
    public String getPhoneNumberSentTo() {
        return phoneNumberSentTo;
    }

    public void setPhoneNumberSentTo(String phoneNumberSentTo) {
        this.phoneNumberSentTo = phoneNumberSentTo;
    }

    @Basic
    @Column(name = "phone_number_sent_from")
    public String getPhoneNumberSentFrom() {
        return phoneNumberSentFrom;
    }

    public void setPhoneNumberSentFrom(String phoneNumberSentFrom) {
        this.phoneNumberSentFrom = phoneNumberSentFrom;
    }

    @Basic
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SentMessagesLogEntity that = (SentMessagesLogEntity) o;

        if (id != that.id) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (verseId != null ? !verseId.equals(that.verseId) : that.verseId != null) return false;
        if (messageBody != null ? !messageBody.equals(that.messageBody) : that.messageBody != null) return false;
        if (phoneNumberSentTo != null ? !phoneNumberSentTo.equals(that.phoneNumberSentTo) : that.phoneNumberSentTo != null)
            return false;
        if (phoneNumberSentFrom != null ? !phoneNumberSentFrom.equals(that.phoneNumberSentFrom) : that.phoneNumberSentFrom != null)
            return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (verseId != null ? verseId.hashCode() : 0);
        result = 31 * result + (messageBody != null ? messageBody.hashCode() : 0);
        result = 31 * result + (phoneNumberSentTo != null ? phoneNumberSentTo.hashCode() : 0);
        result = 31 * result + (phoneNumberSentFrom != null ? phoneNumberSentFrom.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
