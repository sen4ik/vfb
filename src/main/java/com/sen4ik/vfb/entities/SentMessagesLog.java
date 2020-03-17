package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.base.Constants;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "sent_messages_log", schema = Constants.SCHEMA)
public class SentMessagesLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "user_id")
    private Integer userId;

    @Basic
    @Column(name = "verse_id")
    private Integer verseId;

    @Basic
    @Column(name = "message_body")
    private String messageBody;

    @Basic
    @Column(name = "phone_number_sent_to")
    private String phoneNumberSentTo;

    @Basic
    @Column(name = "phone_number_sent_from")
    private String phoneNumberSentFrom;

    @Basic
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getVerseId() {
        return verseId;
    }

    public void setVerseId(Integer verseId) {
        this.verseId = verseId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getPhoneNumberSentTo() {
        return phoneNumberSentTo;
    }

    public void setPhoneNumberSentTo(String phoneNumberSentTo) {
        this.phoneNumberSentTo = phoneNumberSentTo;
    }

    public String getPhoneNumberSentFrom() {
        return phoneNumberSentFrom;
    }

    public void setPhoneNumberSentFrom(String phoneNumberSentFrom) {
        this.phoneNumberSentFrom = phoneNumberSentFrom;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
