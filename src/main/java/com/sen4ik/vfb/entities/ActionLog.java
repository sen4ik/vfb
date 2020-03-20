package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.base.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "action_log", schema = Constants.SCHEMA)
@NoArgsConstructor
@Getter
@Setter
public class ActionLog {

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

    @Basic
    @Column(name = "action")
    private String action;

}
