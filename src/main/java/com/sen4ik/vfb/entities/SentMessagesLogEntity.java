package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.base.Constants;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "sent_messages_log", schema = Constants.SCHEMA)
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

}
