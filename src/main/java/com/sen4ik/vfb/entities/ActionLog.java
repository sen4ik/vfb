package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.constants.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "action_log", schema = Constants.SCHEMA)
@NoArgsConstructor
@Getter
@Setter
public class ActionLog {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "log_user_id")
    private Integer userId;

    @Basic
    @Column(name = "log_verse_id")
    private Integer verseId;

    @Basic
    @Column(name = "log_message_body")
    private String messageBody;

    @Basic
    @Column(name = "log_to")
    private String to;

    @Basic
    @Column(name = "log_from")
    private String from;

    @Basic
    @Column(name = "log_timestamp")
    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Timestamp timestamp;

    @Basic
    @Column(name = "log_action")
    private String action;

    @Basic
    @Column(name = "log_notes")
    private String notes;

}
